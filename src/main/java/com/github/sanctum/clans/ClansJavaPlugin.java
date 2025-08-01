package com.github.sanctum.clans;

import com.github.sanctum.clans.model.ClanAddonRegistry;
import com.github.sanctum.clans.model.addon.BountyAddon;
import com.github.sanctum.clans.model.addon.DynmapAddon;
import com.github.sanctum.clans.model.addon.worldedit.DefaultWorldEditAdapter;
import com.github.sanctum.clans.model.addon.worldedit.WorldEditAdapter;
import com.github.sanctum.clans.model.Claim;
import com.github.sanctum.clans.model.Clan;
import com.github.sanctum.clans.model.ClansAPI;
import com.github.sanctum.clans.model.LogoGallery;
import com.github.sanctum.clans.util.*;
import com.github.sanctum.clans.impl.DefaultArena;
import com.github.sanctum.clans.impl.DefaultClaimFlag;
import com.github.sanctum.clans.impl.entity.EntityAssociate;
import com.github.sanctum.clans.listener.PlayerEventListener;
import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.api.Service;
import com.github.sanctum.labyrinth.api.TaskService;
import com.github.sanctum.labyrinth.data.EconomyProvision;
import com.github.sanctum.labyrinth.data.FileList;
import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.labyrinth.data.container.KeyedServiceManager;
import com.github.sanctum.labyrinth.data.container.PersistentContainer;
import com.github.sanctum.labyrinth.event.EnableAfterEvent;
import com.github.sanctum.labyrinth.formatting.FancyMessageChain;
import com.github.sanctum.labyrinth.formatting.Message;
import com.github.sanctum.labyrinth.library.NamespacedKey;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.panther.annotation.AnnotationDiscovery;
import com.github.sanctum.panther.annotation.Ordinal;
import com.github.sanctum.panther.event.Subscribe;
import com.github.sanctum.panther.event.Vent;
import com.github.sanctum.panther.event.VentMap;
import com.github.sanctum.panther.file.Configurable;
import com.github.sanctum.panther.file.Node;
import com.github.sanctum.panther.paste.PasteManager;
import com.github.sanctum.panther.paste.type.Hastebin;
import com.github.sanctum.panther.paste.type.Pastebin;
import com.github.sanctum.panther.placeholder.PlaceholderRegistration;
import com.github.sanctum.panther.recursive.ServiceFactory;
import com.github.sanctum.panther.util.OrdinalProcedure;
import com.github.sanctum.panther.util.Task;
import com.github.sanctum.skulls.CustomHead;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.github.sanctum.skulls.SkullReferenceUtility;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

// FIXME update ascii art
/**
 * <pre>
 *    ████████╗███████╗████████╗██╗  ██╗███████╗██████╗
 *    ╚══██╔══╝██╔════╝╚══██╔══╝██║  ██║██╔════╝██╔══██╗
 *       ██║   █████╗     ██║   ███████║█████╗  ██████╔╝
 *       ██║   ██╔══╝     ██║   ██╔══██║██╔══╝  ██╔══██╗
 *       ██║   ███████╗   ██║   ██║  ██║███████╗██║  ██║
 *       ╚═╝   ╚══════╝   ╚═╝   ╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝
 * <pre>
 * Copyright (c) 2023 Team Sanctum
 */
public class ClansJavaPlugin extends JavaPlugin implements ClansAPI, Vent.Host {

	private NamespacedKey STATE;
	public Configurable.Extension DATATYPE;
	private static ClansJavaPlugin PRO;
	private static FileList origin;
	private MessagePrefix prefix;
	private ArenaManager arenaManager;
	private ClaimManager claimManager;
	private ShieldManager shieldManager;
	private ClanManager clanManager;
	private CommandManager commandManager;
	private LogoGallery gallery;
	public DataManager dataManager;
	private Hastebin hastebin;
	private Pastebin pastebin;
	private KeyedServiceManager<Clan.Addon> serviceManager;

	public String USER_ID = "%%__USER__%%";
	public String NONCE = "%%__NONCE__%%";
	private UUID sessionId;

	public void onLoad() {
		// register api on load, a change from before.
		Bukkit.getServicesManager().register(ClansAPI.class, this, this, ServicePriority.Normal);
	}

	public void onEnable() {
		initialize();
		if (!isValid()) return;

		Configurable.registerClass(Clan.class);
		Configurable.registerClass(Claim.class);
		ConfigurationSerialization.registerClass(Claim.class);
		ConfigurationSerialization.registerClass(Clan.class);
		VentMap.getInstance().subscribe(this, this);

		getClaimManager().getFlagManager().register(DefaultClaimFlag.values());

		OrdinalProcedure.process(new StartProcedure(this));
	}

	@Subscribe
	public void onEnableAfter(EnableAfterEvent e) {
		getLogger().info("- Checking for placeholders.");
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			new PapiPlaceholders(this).register();
			new PantherPlaceholders(this).register();
		} else {
			PlaceholderRegistration.getInstance().registerTranslation(new PantherPlaceholders(this));
			getLogger().info("- NOTE: PlaceholderAPI was not found.");
		}
		getLogger().info("- Loaded clans unified placeholders.");
		ClanAddonRegistry queue = ClanAddonRegistry.getInstance();
		if (Bukkit.getPluginManager().isPluginEnabled("dynmap")) {
			for (String s : queue.register(DynmapAddon.class).read()) {
				getLogger().info(s);
			}
		}
		if (EconomyProvision.getInstance().isValid()) {
			getLogger().info("- Economy found, loading bounty addon.");
			for (String s : queue.register(BountyAddon.class).read()) {
				getLogger().info(s);
			}
		}
	}

	private boolean isValid() {
		String state = LabyrinthProvider.getInstance().getContainer(STATE).get(String.class, "toString");
		if (state != null) {
			boolean recorded = Boolean.parseBoolean(state);
			if (recorded != Bukkit.getOnlineMode()) {
				if (!Clan.ACTION.getAllClanIDs().isEmpty()) {
					FancyMessageChain chain = new FancyMessageChain();
					chain.append(msg -> msg.then("-------------------------------------------------------"));
					chain.append(msg -> msg.then("-------------------------------------------------------"));
					chain.append(msg -> msg.then("-------------------------------------------------------"));
					chain.append(msg -> msg.then("======================================================="));
					chain.append(msg -> msg.then("======================================================="));
					chain.append(msg -> msg.then("            [Online state change detected]             "));
					chain.append(msg -> msg.then("[To use this plugin again your clan data must be reset]"));
					chain.append(msg -> msg.then(" [This is due to a change in unique id's for players.] "));
					chain.append(msg -> msg.then(" "));
					chain.append(msg -> msg.then("   [Online uuid provision is persistent but offline] "));
					chain.append(msg -> msg.then("    [provision is only persistent to the username.] "));
					chain.append(msg -> msg.then("======================================================="));
					chain.append(msg -> msg.then("======================================================="));
					chain.append(msg -> msg.then("-------------------------------------------------------"));
					chain.append(msg -> msg.then("-------------------------------------------------------"));
					chain.append(msg -> msg.then("-------------------------------------------------------"));
					for (Message m : chain) {
						Bukkit.getConsoleSender().spigot().sendMessage(m.build());
					}
					getServer().getPluginManager().disablePlugin(this);
					return false;
				} else {
					LabyrinthProvider.getInstance().getContainer(STATE).delete("toString");
				}
			}
		}
		return true;
	}

	public void onDisable() {
		ClanAddonRegistry addonQueue = ClanAddonRegistry.getInstance();
		Optional.ofNullable(LabyrinthProvider.getService(Service.TASK).getScheduler(TaskService.ASYNCHRONOUS).get(AsynchronousLoanableTask.KEY)).ifPresent(Task::cancel);
		for (PersistentContainer component : LabyrinthProvider.getService(Service.DATA).getContainers(this)) {
			for (String key : component.keySet()) {
				try {
					component.save(key);
				} catch (IOException e) {
					getLogger().severe("- Unable to save meta '" + key + "' from namespace " + component.getKey().getNamespace() + ":" + component.getKey().getKey());
					e.printStackTrace();
				}
			}
		}

		PlayerEventListener.LOANABLE_TASK.stop();

		for (Clan.Addon addon : addonQueue.get()) {
			AnnotationDiscovery<Ordinal, Clan.Addon> discovery = AnnotationDiscovery.of(Ordinal.class, Clan.Addon.class);
			discovery.filter(method -> method.getName().equals("remove"), true);
			discovery.ifPresent((ordinal, method) -> {
				try {
					method.invoke(addon);
				} catch (IllegalAccessException | InvocationTargetException ex) {
					ex.printStackTrace();
				}
			});
		}

		dataManager.ID_MODE.clear();

		PlayerEventListener.ARMOR_STAND_REMOVAL.run(this).deploy();

		getClanManager().getClans().forEach(c -> {
			c.save(); // save the clan
			for (Clan.Associate a : c.getMembers()) {
				if (!(a instanceof EntityAssociate)) {
					a.save(); // save associate data.
				} else a.remove();
			}
			for (Claim claim : c.getClaims()) {
				claim.save(); // save claim data.
			}
			Reservoir r = Reservoir.get(c);
			if (r != null) r.save(); // save reservoir data
			c.remove(); // clean up the clan from cache.
		});

		getLogoGallery().save();

		Optional.ofNullable(System.getProperty("RELOAD")).ifPresent(s -> {
			LabyrinthProvider.getService(Service.DATA).getContainer(STATE).attach("toString", String.valueOf(Bukkit.getOnlineMode()));
			try {
				LabyrinthProvider.getInstance().getContainer(STATE).save("toString");
			} catch (IOException e) {
				getLogger().warning("- Unable to record current online status");
			}
			if (s.equals("FALSE")) {
				System.setProperty("RELOAD", "TRUE");
			}
		});

		FileManager heads = getFileList().get("heads", "Configuration/Data", Configurable.Type.JSON);
		SkullReferenceUtility.getHeads().stream().filter(h -> h.getCategory().equals("Clans")).forEach(h -> {
			heads.write(t -> {
				t.set(h.getName() + ".name", h.getName());
				t.set(h.getName() + ".custom", true);
				t.set(h.getName() + ".category", h.getCategory());
			});
		});
	}

	public void setPrefix(MessagePrefix prefix) {
		this.prefix = prefix;
	}

	@Override
	public @NotNull KeyedServiceManager<Clan.Addon> getServiceManager() {
		return this.serviceManager;
	}

	@Override
	public @NotNull ArenaManager getArenaManager() {
		return this.arenaManager;
	}

	@Override
	public Optional<Clan.Associate> getAssociate(OfflinePlayer player) {
		return player == null ? Optional.empty() : getClanManager().getClans().stream().filter(c -> c.getMember(m -> Objects.equals(m.getName(), player.getName())) != null).map(c -> c.getMember(m -> Objects.equals(m.getName(), player.getName()))).findFirst();
	}

	@Override
	public Optional<Clan.Associate> getAssociate(UUID uuid) {
		return uuid == null ? Optional.empty() : getClanManager().getClans().stream().filter(c -> c.getMember(m -> Objects.equals(m.getId(), uuid)) != null).map(c -> c.getMember(m -> Objects.equals(m.getId(), uuid))).findFirst();
	}

	@Override
	public Optional<Clan.Associate> getAssociate(String playerName) {
		return playerName == null ? Optional.empty() : getClanManager().getClans().stream().filter(c -> c.getMember(m -> Objects.equals(m.getName(), playerName)) != null).map(c -> c.getMember(m -> Objects.equals(m.getName(), playerName))).findFirst();
	}

	@Override
	public @NotNull FileList getFileList() {
		return origin;
	}

	@Override
	public @NotNull ClanManager getClanManager() {
		return clanManager;
	}

	@Override
	public @NotNull ClaimManager getClaimManager() {
		return claimManager;
	}

	@Override
	public @NotNull ShieldManager getShieldManager() {
		return shieldManager;
	}

	@Override
	public @NotNull CommandManager getCommandManager() {
		return commandManager;
	}

	@Override
	public @NotNull LogoGallery getLogoGallery() {
		return gallery;
	}

	@Override
	public @NotNull PasteManager getPasteManager() {
		return PasteManager.getInstance();
	}

	@Override
	public @NotNull Hastebin getHastebin() {
		return hastebin;
	}

	@Override
	public @NotNull Pastebin getPastebin() {
		return pastebin;
	}

	@Override
	public boolean isUpdated() {
		ClansUpdate update = new ClansUpdate(getPlugin());
		return CompletableFuture.supplyAsync(() -> {
			try {
				if (update.hasUpdate()) {
					getPlugin().getLogger().warning("- An update is available! " + update.getLatest() + " download: [" + update.getResource() + "]");
					return false;
				} else {
					getPlugin().getLogger().info("- All up to date! Latest:(" + update.getLatest() + ") Current:(" + getDescription().getVersion() + ")");
					return true;
				}
			} catch (Exception e) {
				getPlugin().getLogger().info("- Couldn't connect to servers, unable to check for updates.");
			}
			return false;
		}).join();
	}

	@Override
	public boolean isTrial() {
		return false;
	}

	@Override
	public boolean isNameBlackListed(String name) {
		for (String s : ClansAPI.getDataInstance().getConfig().read(c -> c.getNode("Clans.name-blacklist").get(ConfigurationSection.class)).getKeys(false)) {
			if (StringUtils.use(name).containsIgnoreCase(s)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public @NotNull UUID getSessionId() {
		return sessionId;
	}

	@Override
	public @NotNull MessagePrefix getPrefix() {
		return this.prefix;
	}

	@Override
	public @NotNull Plugin getPlugin() {
		return PRO;
	}

	void initialize() {
		origin = FileList.search(PRO = this);
		hastebin = getPasteManager().newHaste();
		//noinspection SpellCheckingInspection
		pastebin = getPasteManager().newPaste("a5tsxh3c37_rmPTCN9gy9kjhd5vepz34");
		STATE = new NamespacedKey(this, "online-state");
		sessionId = UUID.randomUUID();
		dataManager = new DataManager();
		gallery = new LogoGallery();
		DATATYPE = new FileTypeCalculator(dataManager).getType();
		clanManager = new ClanManager(this);
		claimManager = new ClaimManager();
		shieldManager = new ShieldManager();
		commandManager = new CommandManager();
		serviceManager = new KeyedServiceManager<>();
		arenaManager = new ArenaManager();
		ServiceFactory.getInstance().newLoader(WorldEditAdapter.class).supply(new DefaultWorldEditAdapter()).load();
		// load configured arenas, each new arena allows for another war to be held.
		FileManager config = dataManager.getConfig();
		Node clans = config.getRoot().getNode("Clans");
		int arenas = clans.getNode("war").getNode("max-arenas").toPrimitive().getInt();
		for (int i = 0; i < arenas; i++) {
			arenaManager.load(new DefaultArena("PRO-" + (i + 1)));
		}
		Node formatting = config.read(c -> c.getNode("Formatting"));
		Node prefix = formatting.getNode("prefix");
		this.prefix = new MessagePrefix(prefix.getNode("prefix").toPrimitive().getString(),
				prefix.getNode("text").toPrimitive().getString(),
				prefix.getNode("suffix").toPrimitive().getString());
	}

}
