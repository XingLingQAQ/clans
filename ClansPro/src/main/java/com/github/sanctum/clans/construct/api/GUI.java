package com.github.sanctum.clans.construct.api;

import com.github.sanctum.clans.ClansJavaPlugin;
import com.github.sanctum.clans.bridge.ClanAddon;
import com.github.sanctum.clans.bridge.ClanAddonQuery;
import com.github.sanctum.clans.construct.extra.MessagePrefix;
import com.github.sanctum.clans.construct.extra.SimpleLogoCarrier;
import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.api.Service;
import com.github.sanctum.labyrinth.data.EconomyProvision;
import com.github.sanctum.labyrinth.data.FileList;
import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.labyrinth.data.service.Constant;
import com.github.sanctum.labyrinth.formatting.string.Paragraph;
import com.github.sanctum.labyrinth.gui.unity.construct.Menu;
import com.github.sanctum.labyrinth.gui.unity.construct.PaginatedMenu;
import com.github.sanctum.labyrinth.gui.unity.construct.SingularMenu;
import com.github.sanctum.labyrinth.gui.unity.impl.BorderElement;
import com.github.sanctum.labyrinth.gui.unity.impl.FillerElement;
import com.github.sanctum.labyrinth.gui.unity.impl.InventoryElement;
import com.github.sanctum.labyrinth.gui.unity.impl.ItemElement;
import com.github.sanctum.labyrinth.gui.unity.impl.ListElement;
import com.github.sanctum.labyrinth.gui.unity.impl.MenuType;
import com.github.sanctum.labyrinth.library.Entities;
import com.github.sanctum.labyrinth.library.HUID;
import com.github.sanctum.labyrinth.library.Item;
import com.github.sanctum.labyrinth.library.Items;
import com.github.sanctum.labyrinth.library.Message;
import com.github.sanctum.labyrinth.library.RandomObject;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.labyrinth.task.Schedule;
import com.github.sanctum.skulls.CustomHeadLoader;
import com.github.sanctum.skulls.SkullType;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum GUI {

	/**
	 * Modify arena team spawning locations.
	 */
	ARENA_SPAWN,
	/**
	 * Call a vote to surrender the current war.
	 */
	ARENA_SURRENDER,
	/**
	 * Call a vote to truce the current war.
	 */
	ARENA_TRUCE,
	/**
	 * All activated clans addons
	 */
	ADDONS_ACTIVATED,
	/**
	 * All deactivated clans addons
	 */
	ADDONS_DEACTIVATED,
	/**
	 * All registered clans addons
	 */
	ADDONS_REGISTERED,
	/**
	 * Clan addon registration category selection.
	 */
	ADDONS_SELECTION,
	/**
	 * Entire clan roster.
	 */
	CLAN_ROSTER,
	/**
	 * Most power clans on the server in ordered by rank.
	 */
	CLAN_ROSTER_TOP,
	/**
	 * Roster category selection
	 */
	CLAN_ROSTER_SELECTION,
	/**
	 * A clan's list of claims
	 */
	CLAIM_LIST,
	/**
	 * A clan's titles
	 */
	CLAIM_TITLES,
	/**
	 * A clan's list of logo carriers
	 */
	HOLOGRAM_LIST,
	/**
	 * The public logo market
	 */
	LOGO_LIST,
	/**
	 * View a clan member's info.
	 */
	MEMBER_INFO,
	/**
	 * Edit a clan member
	 */
	MEMBER_EDIT,
	/**
	 * View a clan's member list.
	 */
	MEMBER_LIST,
	/**
	 * Modify game rule settings.
	 */
	SETTINGS_GAME_RULE,
	/**
	 * Modify clan arena settings live
	 */
	SETTINGS_ARENA,
	/**
	 * Edit a clans settings.
	 */
	SETTINGS_CLAN,
	/**
	 * View a list of clans to edit.
	 */
	SETTINGS_CLAN_ROSTER,
	/**
	 * Change the plugin language.
	 */
	SETTINGS_LANGUAGE,
	/**
	 * Edit a clan member's settings.
	 */
	SETTINGS_MEMBER,
	/**
	 * View a clan's member list to edit.
	 */
	SETTINGS_MEMBER_LIST,
	/**
	 * Select files to reload.
	 */
	SETTINGS_RELOAD,
	/**
	 * Select a category to manage.
	 */
	SETTINGS_SELECT,
	/**
	 * Modify the raid shield up/down times live
	 */
	SETTINGS_SHIELD;

	private static final Map<Player, String> tempSpot = new HashMap<>();
	private final ItemStack special = CustomHeadLoader.provide("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWFjMTVmNmZjZjJjZTk2M2VmNGNhNzFmMWE4Njg1YWRiOTdlYjc2OWUxZDExMTk0Y2JiZDJlOTY0YTg4OTc4YyJ9fX0=");

	private static Menu getTemp(Player player) {
		if (!tempSpot.containsKey(player)) {
			tempSpot.put(player, CLAN_ROSTER_TOP.name());
		}
		switch (tempSpot.get(player).toLowerCase()) {
			case "clan_roster_top":
				return CLAN_ROSTER_TOP.get();
			case "clan_roster":
				return CLAN_ROSTER.get();
		}
		return MenuType.SINGULAR.build().join();
	}

	public Menu get() {
		switch (this) {
			case SETTINGS_GAME_RULE:
				return MenuType.PAGINATED.build()
						.setHost(ClansAPI.getInstance().getPlugin())
						.setTitle("&3Session Game Rules &0&l»")
						.setSize(getSize())
						.setStock(i -> {
							BorderElement<?> border = new BorderElement<>(i);
							for (Menu.Panel p : Menu.Panel.values()) {
								if (p == Menu.Panel.MIDDLE) continue;
								if (Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.12")) {
									border.add(p, ed -> ed.setType(ItemElement.ControlType.ITEM_BORDER).setElement(it -> it.setType(Items.findMaterial("STAINED_GLASS_PANE")).setTitle(" ").build()));
								} else {
									border.add(p, ed -> ed.setType(ItemElement.ControlType.ITEM_BORDER).setElement(it -> it.setType(Material.GRAY_STAINED_GLASS_PANE).setTitle(" ").build()));
								}
							}
							FillerElement<?> filler = new FillerElement<>(i);
							filler.add(ed -> ed.setType(ItemElement.ControlType.ITEM_FILLER).setElement(it -> it.setItem(SkullType.COMMAND_BLOCK.get()).setTitle(" ").build()));
							i.addItem(filler);
							i.addItem(border);
							ListElement<String> rules = new ListElement<>(Constant.values(AbstractGameRule.class, String.class));
							rules.setLimit(getLimit());
							rules.setPopulate((flag, element) -> {
								Material mat = new RandomObject<>(Arrays.stream(Material.values()).filter(material -> !material.name().contains("LEGACY")).collect(Collectors.toList())).get(flag);
								if (mat.isAir() || !mat.isItem()) mat = Material.DIAMOND;
								Material finalMat = mat;
								element.setElement(edit -> edit.setType(finalMat).setTitle("&6Edit &r" + flag).setLore(" ", "&eLeft-click to &aadd&e stuff", "&eRight-click to &cremove&e stuff", "&eShift-click to &3&loverwrite&e stuff").build()).setClick(c -> {
									c.setCancelled(true);
									AbstractGameRule rule = AbstractGameRule.of(LabyrinthProvider.getInstance().getLocalPrintManager().getPrint(ClansAPI.getInstance().getLocalPrintKey()));
									if (c.getClickType().isShiftClick()) {
										InventoryElement inventory = rule.edit(AbstractGameRule.Modification.SET, flag);
										if (inventory != null) {
											inventory.open(c.getElement());
										}

									} else if (c.getClickType().isLeftClick()) {
										InventoryElement inventory = rule.edit(AbstractGameRule.Modification.ADD, flag);
										if (inventory != null) {
											inventory.open(c.getElement());
										}

									} else if (c.getClickType().isRightClick()) {
										InventoryElement inventory = rule.edit(AbstractGameRule.Modification.REMOVE, flag);
										if (inventory != null) {
											inventory.open(c.getElement());
										}

									}
								});
							});
							i.addItem(rules);
							i.addItem(b -> b.setElement(getLeftItem()).setSlot(getLeft()).setTypeAndAddAction(ItemElement.ControlType.BUTTON_BACK, click -> {
								click.setCancelled(true);
								click.setHotbarAllowed(false);
								click.setConsumer((target, success) -> {

									if (success) {
										i.open(target);
									} else {
										Clan.ACTION.sendMessage(target, Clan.ACTION.color(Clan.ACTION.alreadyFirstPage()));
									}

								});
							}));

							i.addItem(b -> b.setElement(getRightItem()).setSlot(getRight()).setTypeAndAddAction(ItemElement.ControlType.BUTTON_NEXT, click -> {
								click.setCancelled(true);
								click.setHotbarAllowed(false);
								click.setConsumer((target, success) -> {
									if (success) {
										i.open(target);
									} else {
										Clan.ACTION.sendMessage(target, Clan.ACTION.color(Clan.ACTION.alreadyLastPage()));
									}

								});
							}));

							i.addItem(b -> b.setElement(getBackItem()).setSlot(getBack()).setTypeAndAddAction(ItemElement.ControlType.BUTTON_EXIT, click -> {
								click.setCancelled(true);
								click.setHotbarAllowed(false);
								SETTINGS_SELECT.get().open(click.getElement());
							}));
						})
						.join();

			case LOGO_LIST:
				return MenuType.PAGINATED.build()
						.setHost(ClansAPI.getInstance().getPlugin())
						.setKey("ClansPro:logo-list")
						.setSize(getSize())
						.setTitle("&e&lLogo Gallery &0&l»")
						.setStock(i -> {
							BorderElement<?> border = new BorderElement<>(i);
							for (Menu.Panel p : Menu.Panel.values()) {
								if (p == Menu.Panel.MIDDLE) continue;
								if (Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.12")) {
									border.add(p, ed -> ed.setType(ItemElement.ControlType.ITEM_BORDER).setElement(it -> it.setType(Items.findMaterial("STAINED_GLASS_PANE")).setTitle(" ").build()));
								} else {
									border.add(p, ed -> ed.setType(ItemElement.ControlType.ITEM_BORDER).setElement(it -> it.setType(Material.GRAY_STAINED_GLASS_PANE).setTitle(" ").build()));
								}
							}
							FillerElement<?> filler = new FillerElement<>(i);
							filler.add(ed -> ed.setType(ItemElement.ControlType.ITEM_FILLER).setElement(it -> it.setItem(SkullType.COMMAND_BLOCK.get()).setTitle(" ").build()));
							i.addItem(filler);
							i.addItem(border);
							i.addItem(b -> b.setElement(getLeftItem()).setSlot(getLeft()).setTypeAndAddAction(ItemElement.ControlType.BUTTON_BACK, click -> {
								click.setCancelled(true);
								click.setHotbarAllowed(false);
								click.setConsumer((target, success) -> {

									if (success) {
										i.open(target);
									} else {
										Clan.ACTION.sendMessage(target, Clan.ACTION.color(Clan.ACTION.alreadyFirstPage()));
									}

								});
							}));

							i.addItem(b -> b.setElement(getRightItem()).setSlot(getRight()).setTypeAndAddAction(ItemElement.ControlType.BUTTON_NEXT, click -> {
								click.setCancelled(true);
								click.setHotbarAllowed(false);
								click.setConsumer((target, success) -> {
									if (success) {
										i.open(target);
									} else {
										Clan.ACTION.sendMessage(target, Clan.ACTION.color(Clan.ACTION.alreadyLastPage()));
									}

								});
							}));

							i.addItem(b -> b.setElement(getBackItem()).setSlot(getBack()).setTypeAndAddAction(ItemElement.ControlType.BUTTON_EXIT, click -> {
								click.setCancelled(true);
								click.setHotbarAllowed(false);
								click.getElement().closeInventory();
							}));
							ListElement<SimpleLogoCarrier> list = new ListElement<>(new ArrayList<>(ClansAPI.getInstance().getLogoGallery().getLogos()));
							list.setLimit(getLimit()).setComparator(Comparator.comparingInt(value -> value.getData().get().getLines().size()));
							list.setPopulate((stand, item) -> {
								List<String> set = Arrays.asList(stand.toRaw());
								item.setElement(ed -> ed.setTitle("&e# &f(" + stand.getId() + ")").setLore(set).build());
								item.setClick(click -> {
									click.setCancelled(true);
									if (click.getElement().hasPermission("clanspro.admin")) {
										if (click.getClickType().isShiftClick()) {
											ClansAPI.getInstance().getLogoGallery().remove(set);
											Schedule.sync(() -> GUI.LOGO_LIST.get().open(click.getElement())).waitReal(1);
											return;
										}
									}
									if (click.getClickType().isLeftClick()) {
										ClansAPI.getInstance().getAssociate(click.getElement()).ifPresent(a -> {
											if (Clearance.LOGO_UPLOAD.test(a) && Clearance.LOGO_APPLY.test(a)) {
												a.getClan().setValue("logo", set, false);
												Clan.ACTION.sendMessage(click.getElement(), "&aClan insignia successfully updated.");
											}
										});
									}
								});
							});
							i.addItem(list);
						}).orGet(m -> m instanceof PaginatedMenu && m.getKey().map(("ClansPro:logo-list")::equals).orElse(false));
			case CLAN_ROSTER:
				return MenuType.PAGINATED.build().setHost(ClansAPI.getInstance().getPlugin())
						.setProperty(Menu.Property.CACHEABLE, Menu.Property.RECURSIVE)
						.setTitle(Clan.ACTION.color(ClansAPI.getDataInstance().getMenuTitle("roster-list")))
						.setSize(getSize())
						.setProcessEvent(open -> tempSpot.put(open.getElement(), CLAN_ROSTER.name()))
						.setOpenEvent(open -> tempSpot.put(open.getElement(), CLAN_ROSTER.name()))
						.setKey("ClansPro:Roster")
						.setStock(i -> {
							BorderElement<?> border = new BorderElement<>(i);
							for (Menu.Panel p : Menu.Panel.values()) {
								if (p == Menu.Panel.MIDDLE) continue;
								if (Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.12")) {
									border.add(p, ed -> ed.setType(ItemElement.ControlType.ITEM_BORDER).setElement(it -> it.setType(Items.findMaterial("STAINED_GLASS_PANE")).setTitle(" ").build()));
								} else {
									border.add(p, ed -> ed.setType(ItemElement.ControlType.ITEM_BORDER).setElement(it -> it.setType(Material.GRAY_STAINED_GLASS_PANE).setTitle(" ").build()));
								}
							}
							FillerElement<?> filler = new FillerElement<>(i);
							filler.add(ed -> ed.setType(ItemElement.ControlType.ITEM_FILLER).setElement(it -> it.setItem(SkullType.COMMAND_BLOCK.get()).setTitle(" ").build()));
							i.addItem(filler);
							i.addItem(border);
							i.addItem(b -> b.setElement(getLeftItem()).setSlot(getLeft()).setTypeAndAddAction(ItemElement.ControlType.BUTTON_BACK, click -> {
								click.setCancelled(true);
								click.setHotbarAllowed(false);
								click.setConsumer((target, success) -> {

									if (success) {
										i.open(target);
									} else {
										Clan.ACTION.sendMessage(target, Clan.ACTION.color(Clan.ACTION.alreadyFirstPage()));
									}

								});
							}));

							i.addItem(b -> b.setElement(getRightItem()).setSlot(getRight()).setTypeAndAddAction(ItemElement.ControlType.BUTTON_NEXT, click -> {
								click.setCancelled(true);
								click.setHotbarAllowed(false);
								click.setConsumer((target, success) -> {

									if (success) {
										i.open(target);
									} else {
										Clan.ACTION.sendMessage(target, Clan.ACTION.color(Clan.ACTION.alreadyLastPage()));
									}

								});
							}));

							i.addItem(b -> b.setElement(getBackItem()).setSlot(getBack()).setTypeAndAddAction(ItemElement.ControlType.BUTTON_EXIT, click -> {
								click.setCancelled(true);
								click.setHotbarAllowed(false);
								CLAN_ROSTER_SELECTION.get().open(click.getElement());
							}));

							i.addItem(new ListElement<>(ClansAPI.getInstance().getClanManager().getClans().list()).setLimit(getLimit()).setPopulate((c, element) -> {
								element.setElement(b -> {
									ItemStack it;
									if (c.isConsole()) {
										it = new ItemStack(special);
									} else {
										it = new ItemStack(ClansAPI.getDataInstance().getMenuItem("clan"));
									}
									ItemMeta meta = it.getItemMeta();
									String title = MessageFormat.format(ClansAPI.getDataInstance().getMenuCategory("clan"), c.getPalette().toString(), c.getName(), c.getId().toString().substring(0, 4));

									meta.setDisplayName(StringUtils.use(title).translate());

									it.setItemMeta(meta);
									b.setItem(it);
									return b.build();
								}).setClick(click -> {
									click.setCancelled(true);
									click.setHotbarAllowed(false);
									MEMBER_LIST.get(c).open(click.getElement());
								});
							}).setLimit(getLimit()));
						}).orGet(m -> m instanceof PaginatedMenu && m.getKey().isPresent() && m.getKey().get().equals("ClansPro:Roster"));
			case SETTINGS_CLAN_ROSTER:
				return MenuType.PAGINATED.build().setHost(ClansAPI.getInstance().getPlugin())
						.setProperty(Menu.Property.CACHEABLE, Menu.Property.RECURSIVE)
						.setTitle("&0&l» &3&lSelect a clan")
						.setSize(getSize())
						.setKey("ClansPro:Roster_edit")
						.setOpenEvent(open -> tempSpot.put(open.getElement(), CLAN_ROSTER_TOP.name()))
						.setProcessEvent(open -> tempSpot.put(open.getElement(), CLAN_ROSTER_TOP.name()))
						.setStock(i -> {
							BorderElement<?> border = new BorderElement<>(i);
							for (Menu.Panel p : Menu.Panel.values()) {
								if (p == Menu.Panel.MIDDLE) continue;
								if (Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.12")) {
									border.add(p, ed -> ed.setType(ItemElement.ControlType.ITEM_BORDER).setElement(it -> it.setType(Items.findMaterial("STAINED_GLASS_PANE")).setTitle(" ").build()));
								} else {
									border.add(p, ed -> ed.setType(ItemElement.ControlType.ITEM_BORDER).setElement(it -> it.setType(Material.GRAY_STAINED_GLASS_PANE).setTitle(" ").build()));
								}
							}
							FillerElement<?> filler = new FillerElement<>(i);
							filler.add(ed -> ed.setType(ItemElement.ControlType.ITEM_FILLER).setElement(it -> it.setItem(SkullType.COMMAND_BLOCK.get()).setTitle(" ").build()));
							i.addItem(filler);
							i.addItem(border);
							i.addItem(b -> b.setElement(getLeftItem()).setSlot(getLeft()).setTypeAndAddAction(ItemElement.ControlType.BUTTON_BACK, click -> {
								click.setCancelled(true);
								click.setHotbarAllowed(false);
								click.setConsumer((target, success) -> {

									if (success) {
										i.open(target);
									} else {
										Clan.ACTION.sendMessage(target, Clan.ACTION.color(Clan.ACTION.alreadyFirstPage()));
									}

								});
							}));

							i.addItem(b -> b.setElement(getRightItem()).setSlot(getRight()).setTypeAndAddAction(ItemElement.ControlType.BUTTON_NEXT, click -> {
								click.setCancelled(true);
								click.setHotbarAllowed(false);
								click.setConsumer((target, success) -> {

									if (success) {
										i.open(target);
									} else {
										Clan.ACTION.sendMessage(target, Clan.ACTION.color(Clan.ACTION.alreadyLastPage()));
									}

								});
							}));

							i.addItem(b -> b.setElement(getBackItem()).setSlot(getBack()).setTypeAndAddAction(ItemElement.ControlType.BUTTON_EXIT, click -> {
								click.setCancelled(true);
								click.setHotbarAllowed(false);
								SETTINGS_SELECT.get().open(click.getElement());
							}));

							i.addItem(new ListElement<>(Clan.ACTION.getMostPowerful()).setLimit(getLimit()).setPopulate((c, element) -> {
								element.setElement(b -> {
									ItemStack it;
									if (c.isConsole()) {
										it = new ItemStack(special);
									} else {
										it = new ItemStack(ClansAPI.getDataInstance().getMenuItem("clan"));
									}
									ItemMeta meta = it.getItemMeta();
									String title = MessageFormat.format(ClansAPI.getDataInstance().getMenuCategory("clan"), c.getPalette().toString(), c.getName(), c.getId().toString().substring(0, 4));

									meta.setDisplayName(StringUtils.use(title).translate());

									it.setItemMeta(meta);
									b.setItem(it);
									return b.build();
								}).setClick(click -> {
									click.setCancelled(true);
									click.setHotbarAllowed(false);
									SETTINGS_CLAN.get(c).open(click.getElement());
								});
							}).setLimit(getLimit()));

						}).orGet(m -> m instanceof PaginatedMenu && m.getKey().isPresent() && m.getKey().get().equals("ClansPro:Roster_edit"));
			case CLAN_ROSTER_TOP:
				return MenuType.PAGINATED.build().setHost(ClansAPI.getInstance().getPlugin())
						.setProperty(Menu.Property.CACHEABLE, Menu.Property.RECURSIVE)
						.setTitle(Clan.ACTION.color(ClansAPI.getDataInstance().getMenuTitle("top-list")))
						.setSize(getSize())
						.setKey("ClansPro:Roster_top")
						.setOpenEvent(open -> tempSpot.put(open.getElement(), CLAN_ROSTER_TOP.name()))
						.setProcessEvent(open -> tempSpot.put(open.getElement(), CLAN_ROSTER_TOP.name()))
						.setStock(i -> {
							BorderElement<?> border = new BorderElement<>(i);
							for (Menu.Panel p : Menu.Panel.values()) {
								if (p == Menu.Panel.MIDDLE) continue;
								if (Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.12")) {
									border.add(p, ed -> ed.setType(ItemElement.ControlType.ITEM_BORDER).setElement(it -> it.setType(Items.findMaterial("STAINED_GLASS_PANE")).setTitle(" ").build()));
								} else {
									border.add(p, ed -> ed.setType(ItemElement.ControlType.ITEM_BORDER).setElement(it -> it.setType(Material.GRAY_STAINED_GLASS_PANE).setTitle(" ").build()));
								}
							}
							FillerElement<?> filler = new FillerElement<>(i);
							filler.add(ed -> ed.setType(ItemElement.ControlType.ITEM_FILLER).setElement(it -> it.setItem(SkullType.COMMAND_BLOCK.get()).setTitle(" ").build()));
							i.addItem(filler);
							i.addItem(border);
							i.addItem(b -> b.setElement(getLeftItem()).setSlot(getLeft()).setTypeAndAddAction(ItemElement.ControlType.BUTTON_BACK, click -> {
								click.setCancelled(true);
								click.setHotbarAllowed(false);
								click.setConsumer((target, success) -> {

									if (success) {
										i.open(target);
									} else {
										Clan.ACTION.sendMessage(target, Clan.ACTION.color(Clan.ACTION.alreadyFirstPage()));
									}

								});
							}));

							i.addItem(b -> b.setElement(getRightItem()).setSlot(getRight()).setTypeAndAddAction(ItemElement.ControlType.BUTTON_NEXT, click -> {
								click.setCancelled(true);
								click.setHotbarAllowed(false);
								click.setConsumer((target, success) -> {

									if (success) {
										i.open(target);
									} else {
										Clan.ACTION.sendMessage(target, Clan.ACTION.color(Clan.ACTION.alreadyLastPage()));
									}

								});
							}));

							i.addItem(b -> b.setElement(getBackItem()).setSlot(getBack()).setTypeAndAddAction(ItemElement.ControlType.BUTTON_EXIT, click -> {
								click.setCancelled(true);
								click.setHotbarAllowed(false);
								CLAN_ROSTER_SELECTION.get().open(click.getElement());
							}));

							i.addItem(new ListElement<>(Clan.ACTION.getMostPowerful()).setLimit(getLimit()).setComparator((o1, o2) -> Double.compare(o2.getData().get().getPower(), o1.getData().get().getPower())).setPopulate((c, element) -> {
								element.setElement(b -> {
									ItemStack it;
									if (c.isConsole()) {
										it = new ItemStack(special);
									} else {
										it = new ItemStack(ClansAPI.getDataInstance().getMenuItem("clan"));
									}
									/*
									String memlist = "";
									if (memlist.length() > 44) {
										memlist = memlist.substring(0, 44) + "...";
									}
									String allylist = "";
									if (allylist.length() > 44) {
										allylist = allylist.substring(0, 44) + "...";
									}
									String enemylist = "";
									if (enemylist.length() > 44) {
										enemylist = enemylist.substring(0, 44) + "...";
									}
									String pvp;
									if (c.isPeaceful()) {
										pvp = "&a&lPEACE";
									} else {
										pvp = "&4&lWAR";
									}

									if (c.getRelation().getAlliance().isEmpty()) {
										allylist = "&cEmpty";
									}

									if (c.getRelation().getRivalry().isEmpty()) {
										enemylist = "&cEmpty";
									}



									String[] par = new Paragraph(c.getDescription()).setRegex(Paragraph.COMMA_AND_PERIOD).get();

									List<String> result = new LinkedList<>();
									for (String a : ClansAPI.getDataInstance().getGUIFormat()) {
										result.add(MessageFormat.format(a, c.getPalette().toString().replace("&", "&f»" + c.getPalette().toString()).replace("#", "&f»" + c.getPalette().toString() + " "), (c.getPalette().isGradient() ? c.getPalette().toGradient().context(par[0]).join() : c.getPalette() + par[0]), (c.getPalette().isGradient() ? c.getPalette().toGradient().context(Clan.ACTION.format(c.getPower())).join() : c.getPalette() + Clan.ACTION.format(c.getPower())), c.getBase() != null, c.getPalette().toString() + c.getClaims().length, pvp, memlist, allylist, enemylist, c.getPalette().toString()));
									}
									meta.setLore(color(result.toArray(new String[0])));

									 */
									ItemMeta meta = it.getItemMeta();
									String title = MessageFormat.format(ClansAPI.getDataInstance().getMenuCategory("clan"), c.getPalette().toString(), c.getName(), c.getId().toString().substring(0, 4));

									meta.setDisplayName(StringUtils.use(title).translate());

									it.setItemMeta(meta);
									b.setItem(it);
									return b.build();
								}).setClick(click -> {
									click.setCancelled(true);
									click.setHotbarAllowed(false);
									MEMBER_LIST.get(c).open(click.getElement());
								});
							}).setLimit(getLimit()));

						}).orGet(m -> m instanceof PaginatedMenu && m.getKey().isPresent() && m.getKey().get().equals("ClansPro:Roster_top"));
			case CLAN_ROSTER_SELECTION:
				return MenuType.SINGULAR.build().setHost(ClansAPI.getInstance().getPlugin())
						.setProperty(Menu.Property.RECURSIVE, Menu.Property.CACHEABLE)
						.setTitle(StringUtils.use(ClansAPI.getDataInstance().getMenuTitle("list-types")).translate())
						.setKey("ClansPro:Settings_select")
						.setSize(Menu.Rows.ONE)
						.setStock(i -> {
							FillerElement<?> filler = new FillerElement<>(i);
							filler.add(ed -> ed.setType(ItemElement.ControlType.ITEM_FILLER).setElement(it -> it.setType(Optional.ofNullable(Items.findMaterial("ironbars")).orElse(Items.findMaterial("stainedglasspane"))).setTitle(" ").build()));
							i.addItem(filler);
							i.addItem(b -> b.setElement(it -> it.setType(ClansAPI.getDataInstance().getMenuMaterial("top-list") != null ? ClansAPI.getDataInstance().getMenuMaterial("top-list") : Material.PAPER)
									.setTitle(StringUtils.use(ClansAPI.getDataInstance().getMenuCategory("top-list")).translate()).build()).setSlot(3).setClick(click -> {
								click.setHotbarAllowed(false);
								click.setCancelled(true);
								Player p = click.getElement();
								CLAN_ROSTER_TOP.get().open(p);
							}));
							i.addItem(b -> b.setElement(it -> it.setType(ClansAPI.getDataInstance().getMenuMaterial("roster-list") != null ? ClansAPI.getDataInstance().getMenuMaterial("roster-list") : Material.PAPER)
									.setTitle(ClansAPI.getDataInstance().getMenuCategory("roster-list")).build()).setSlot(5).setClick(click -> {
								click.setHotbarAllowed(false);
								click.setCancelled(true);
								Player p = click.getElement();
								CLAN_ROSTER.get().open(p);
							}));

						}).orGet(m -> m instanceof SingularMenu && m.getKey().isPresent() && m.getKey().get().equals("ClansPro:Settings_Select")).addAction(c -> c.setCancelled(true));
			case SETTINGS_SELECT:
				return MenuType.SINGULAR.build()
						.setHost(ClansAPI.getInstance().getPlugin())
						.setProperty(Menu.Property.CACHEABLE)
						.setKey("ClansPro:Settings")
						.setSize(Menu.Rows.SIX)
						.setTitle(" &0&l» &2&oManagement Area")
						.setStock(i -> {
							FillerElement<?> filler = new FillerElement<>(i);
							filler.add(ed -> ed.setElement(it -> it.setType(Optional.ofNullable(Items.findMaterial("bluestainedglasspane")).orElse(Items.findMaterial("stainedglasspane"))).setTitle(" ").build()));
							i.addItem(filler);
							BorderElement<?> border = new BorderElement<>(i);
							for (Menu.Panel p : Menu.Panel.values()) {
								if (p == Menu.Panel.MIDDLE) continue;
								if (Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.12")) {
									border.add(p, ed -> ed.setType(ItemElement.ControlType.ITEM_BORDER).setElement(it -> it.setType(Items.findMaterial("STAINED_GLASS_PANE")).setTitle(" ").build()));
								} else {
									border.add(p, ed -> ed.setType(ItemElement.ControlType.ITEM_BORDER).setElement(it -> it.setType(Material.GRAY_STAINED_GLASS_PANE).setTitle(" ").build()));
								}
							}
							i.addItem(border);
							i.addItem(b -> b.setElement(new ItemStack(Items.findMaterial("NAUTILUS_SHELL") != null ? Items.findMaterial("NAUTILUS_SHELL") : Items.findMaterial("NETHERSTAR"))).setSlot(33).setElement(ed -> ed.setLore("&bClick to manage all &dclans addons").setTitle("&7[&5Addon Management&7]").build()).setClick(click -> {
								click.setCancelled(true);
								ADDONS_SELECTION.get().open(click.getElement());
							}));
							i.addItem(b -> b.setElement(new ItemStack(Items.findMaterial("CLOCK") != null ? Items.findMaterial("CLOCK") : Items.findMaterial("WATCH"))).setElement(ed -> ed.setTitle("&7[&2Shield Edit&7]").setLore("&bClick to edit the &3raid-shield").build()).setSlot(29).setClick(c -> {
								c.setCancelled(true);
								SETTINGS_SHIELD.get().open(c.getElement());
							}));
							i.addItem(b -> b.setElement(new ItemStack(new ItemStack(Material.ENCHANTED_BOOK))).setElement(ed -> ed.setTitle("&7[&cAll Spy&7]").setLore("&7Click to toggle spy ability on all clan chat channels.").build()).setSlot(12).setClick(c -> {
								Player p = c.getElement();
								c.setCancelled(true);
								Bukkit.dispatchCommand(p, "cla spy");
							}));
							i.addItem(b -> b.setElement(new ItemStack(Items.findMaterial("HEARTOFTHESEA") != null ? Items.findMaterial("HEARTOFTHESEA") : Items.findMaterial("SLIMEBALL"))).setElement(ed -> ed.setTitle("&7[&eGame Rules&7]").setLore("&7Click to manage this sessions game rules.").build()).setSlot(40).setClick(c -> {
								c.setCancelled(true);
								SETTINGS_GAME_RULE.get().open(c.getElement());
							}));
							i.addItem(b -> b.setElement(new ItemStack(Material.ANVIL)).setElement(ed -> ed.setTitle("&7[&eClan Edit&7]").setLore("&7Click to manage clans.").build()).setSlot(10).setClick(c -> {
								c.setCancelled(true);
								SETTINGS_CLAN_ROSTER.get().open(c.getElement());
							}));
							i.addItem(b -> b.setElement(new ItemStack(Material.DIAMOND_SWORD)).setElement(ed -> ed.setTitle("&7[&2War&7]").setLore("&7Click to manage arena spawns.").build()).setSlot(16).setClick(c -> {
								c.setCancelled(true);
								SETTINGS_ARENA.get().open(c.getElement());
							}));
							i.addItem(b -> b.setElement(new ItemStack(ClansAPI.getDataInstance().getMenuMaterial("clan") != null ? ClansAPI.getDataInstance().getMenuMaterial("clan") : Material.PAPER)).setElement(ed -> ed.setTitle("&7[&eClan List&7]").setLore("&bClick to view the entire &6clan roster").build()).setSlot(14).setClick(c -> {
								c.setCancelled(true);
								CLAN_ROSTER_SELECTION.get().open(c.getElement());
							}));
							i.addItem(b -> b.setElement(getBackItem()).setElement(ed -> ed.setTitle("&7[&4Close&7]").setLore("&cClick to close the gui.").build()).setSlot(49).setClick(c -> {
								c.setCancelled(true);
								c.getElement().closeInventory();
							}));
							i.addItem(b -> b.setElement(new ItemStack(Items.findMaterial("HEARTOFTHESEA") != null ? Items.findMaterial("HEARTOFTHESEA") : Items.findMaterial("SLIMEBALL"))).setElement(ed -> ed.setTitle("&7[&cReload&7]").setLore("&eClick to reload data.").build()).setSlot(31).setClick(c -> {
								c.setCancelled(true);
								SETTINGS_RELOAD.get().open(c.getElement());
							}));
						}).orGet(m -> m instanceof SingularMenu && m.getKey().map("ClansPro:Settings"::equals).orElse(false));
			case ADDONS_SELECTION:
				return MenuType.SINGULAR.build().setHost(ClansAPI.getInstance().getPlugin())
						.setSize(Menu.Rows.ONE)
						.setTitle("&2&oManage Addon Cycles &0&l»")
						.setKey("ClansPro:Addons")
						.setProperty(Menu.Property.CACHEABLE)
						.setStock(i -> {
							FillerElement<?> filler = new FillerElement<>(i);
							filler.add(ed -> ed.setElement(it -> it.setType(Optional.ofNullable(Items.findMaterial("bluestainedglasspane")).orElse(Items.findMaterial("stainedglasspane"))).setTitle(" ").build()));
							i.addItem(filler);
							i.addItem(b -> b.setElement(ed -> ed.setType(Material.LAVA_BUCKET).setTitle("&7[&c&lDisabled&7]").setLore("&a&oTurn on disabled addons.").build()).setSlot(4).setClick(c -> {
								c.setCancelled(true);
								ADDONS_DEACTIVATED.get().open(c.getElement());
							}));
							i.addItem(b -> b.setElement(ed -> ed.setType(Material.WATER_BUCKET).setTitle("&7[&3&lRunning&7]").setLore("&2&oTurn off running addons.").build()).setSlot(3).setClick(c -> {
								c.setCancelled(true);
								ADDONS_ACTIVATED.get().open(c.getElement());
							}));
							i.addItem(b -> b.setElement(ed -> ed.setType(Material.BUCKET).setTitle("&7[&e&lLoaded&7]").setLore("&b&oView a list of all currently persistently cached addons.").build()).setSlot(5).setClick(c -> {
								c.setCancelled(true);
								ADDONS_REGISTERED.get().open(c.getElement());
							}));
							i.addItem(b -> b.setElement(ed -> ed.setItem(getBackItem()).build()).setClick(c -> {
								c.setCancelled(true);
								SETTINGS_SELECT.get().open(c.getElement());
							}).setSlot(8));
						})
						.orGet(m -> m instanceof SingularMenu && m.getKey().map("ClansPro:Addons"::equals).orElse(false));
			case SETTINGS_ARENA:
				return MenuType.SINGULAR.build().setHost(ClansAPI.getInstance().getPlugin())
						.setSize(Menu.Rows.ONE)
						.setTitle("&2&oArena Spawns &0&l»")
						.setKey("ClansPro:War")
						.setProperty(Menu.Property.CACHEABLE)
						.setStock(i -> {
							FillerElement<?> filler = new FillerElement<>(i);
							filler.add(ed -> ed.setElement(it -> it.setType(Optional.ofNullable(Items.findMaterial("bluestainedglasspane")).orElse(Items.findMaterial("stainedglasspane"))).setTitle(" ").build()));
							i.addItem(filler);
							i.addItem(b -> b.setElement(ed -> ed.setType(Material.SLIME_BALL).setTitle("&7[&cA&7]").setLore("&bClick to update the a team start location.").build()).setSlot(2).setClick(c -> {
								c.setCancelled(true);
								c.getElement().performCommand("cla setspawn a");
							}));
							i.addItem(b -> b.setElement(ed -> ed.setType(Material.SLIME_BALL).setTitle("&7[&6B&7]").setLore("&bClick to update the b team start location.").build()).setSlot(3).setClick(c -> {
								c.setCancelled(true);
								c.getElement().performCommand("cla setspawn b");
							}));
							i.addItem(b -> b.setElement(ed -> ed.setType(Material.SLIME_BALL).setTitle("&7[&eC&7]").setLore("&bClick to update the c team start location.").build()).setSlot(5).setClick(c -> {
								c.setCancelled(true);
								c.getElement().performCommand("cla setspawn c");
							}));
							i.addItem(b -> b.setElement(ed -> ed.setType(Material.SLIME_BALL).setTitle("&7[&fD&7]").setLore("&bClick to update the d team start location.").build()).setSlot(6).setClick(c -> {
								c.setCancelled(true);
								c.getElement().performCommand("cla setspawn d");
							}));
							i.addItem(b -> b.setElement(ed -> ed.setItem(getBackItem()).build()).setClick(c -> {
								c.setCancelled(true);
								SETTINGS_SELECT.get().open(c.getElement());
							}).setSlot(8));
						})
						.orGet(m -> m instanceof SingularMenu && m.getKey().map("ClansPro:War"::equals).orElse(false));
			case SETTINGS_RELOAD:
				return MenuType.SINGULAR.build().setHost(ClansAPI.getInstance().getPlugin())
						.setSize(Menu.Rows.ONE)
						.setTitle("&0&l» &eReload Files")
						.setKey("ClansPro:Reload")
						.setProperty(Menu.Property.CACHEABLE)
						.setStock(i -> {
							FillerElement<?> filler = new FillerElement<>(i);
							filler.add(ed -> ed.setElement(it -> it.setType(Optional.ofNullable(Items.findMaterial("bluestainedglasspane")).orElse(Items.findMaterial("stainedglasspane"))).setTitle(" ").build()));
							i.addItem(filler);
							i.addItem(b -> b.setElement(ed -> ed.setType(Material.POTION).setTitle("&aConfig.yml").build()).setSlot(0).setClick(c -> {
								c.setCancelled(true);
								SETTINGS_SELECT.get().open(c.getElement());
								ClansAPI.getInstance().getFileList().get("Config", "Configuration").getRoot().reload();
								Message.form(c.getElement()).setPrefix(ClansAPI.getInstance().getPrefix().joined()).send("&aConfig file 'Config' reloaded.");
							}));
							i.addItem(b -> b.setElement(ed -> ed.setType(Material.POTION).setTitle("&5Messages.yml").build()).setSlot(2).setClick(c -> {
								c.setCancelled(true);
								ClansAPI.getInstance().getFileList().get("Messages", "Configuration").getRoot().reload();
								Message.form(c.getElement()).setPrefix(ClansAPI.getInstance().getPrefix().joined()).send("&aConfig file 'Messages' reloaded.");
							}));
							i.addItem(b -> b.setElement(ed -> ed.setType(Material.POTION).setTitle("&2&lAll").build()).setSlot(4).setClick(c -> {
								c.setCancelled(true);
								Player p = c.getElement();
								FileManager config = ClansAPI.getInstance().getFileList().get("Config", "Configuration");
								FileManager message = ClansAPI.getInstance().getFileList().get("Messages", "Configuration");




								FileManager regions = ClansAPI.getInstance().getFileList().get("Regions", "Configuration");
								config.getRoot().reload();
								message.getRoot().reload();
								regions.getRoot().reload();

								if (config.read(fc -> fc.getString("Clans.lang").equalsIgnoreCase("en-US"))) {

									if (config.read(fc -> fc.getString("Clans.lang").equalsIgnoreCase("pt-BR"))) {

										config.getRoot().delete();
										message.getRoot().delete();
										FileList.search(ClansAPI.getInstance().getPlugin()).copyYML("Config_pt_br", config);
										config.getRoot().reload();
										FileList.search(ClansAPI.getInstance().getPlugin()).copyYML("Messages_pt_br", message);
										message.getRoot().reload();
										Message.form(p).setPrefix(ClansAPI.getInstance().getPrefix().joined()).send("&a&oAgora traduzido para o brasil!");

									}

								}

								SETTINGS_SELECT.get().open(p);

								ClansAPI.getInstance().getClanManager().refresh();

								LabyrinthProvider.getInstance().getLocalPrintManager().getPrint(ClansAPI.getInstance().getLocalPrintKey()).reload().deploy();

								Clan.ACTION.sendMessage(p, "&b&oAll configuration files reloaded.");
							}));
							i.addItem(b -> b.setElement(ed -> ed.setType(Material.POTION).setTitle("&eLang Change").build()).setSlot(6).setClick(c -> {
								c.setCancelled(true);
								SETTINGS_LANGUAGE.get().open(c.getElement());
							}));
							i.addItem(b -> b.setElement(ed -> ed.setItem(getBackItem()).build()).setClick(c -> {
								c.setCancelled(true);
								SETTINGS_SELECT.get().open(c.getElement());
							}).setSlot(8));
						})
						.orGet(m -> m instanceof SingularMenu && m.getKey().map("ClansPro:Reload"::equals).orElse(false));
			case SETTINGS_SHIELD:
				return MenuType.SINGULAR.build().setHost(ClansAPI.getInstance().getPlugin())
						.setSize(Menu.Rows.ONE)
						.setTitle("&2&oRaid-Shield Settings &0&l»")
						.setKey("ClansPro:shield-edit")
						.setProperty(Menu.Property.CACHEABLE)
						.setStock(i -> {
							FillerElement<?> filler = new FillerElement<>(i);
							filler.add(ed -> ed.setElement(it -> it.setType(Optional.ofNullable(Items.findMaterial("bluestainedglasspane")).orElse(Items.findMaterial("stainedglasspane"))).setTitle(" ").build()));
							i.addItem(filler);
							i.addItem(b -> b.setElement(ed -> ed.setType(Items.findMaterial("CLOCK") != null ? Items.findMaterial("CLOCK") : Items.findMaterial("WATCH")).setTitle("&a&oUp: Mid-day").setLore("&bClick to change the raid-shield to enable mid-day").build()).setSlot(4).setClick(c -> {
								c.setCancelled(true);
								ClansAPI.getInstance().getShieldManager().getTamper().setUpOverride(6000);
								ClansAPI.getInstance().getShieldManager().getTamper().setDownOverride(18000);
								Clan.ACTION.sendMessage(c.getElement(), "&aRaid-shield engagement changed to mid-day.");
							}));
							i.addItem(b -> b.setElement(ed -> ed.setType(Items.findMaterial("CLOCK") != null ? Items.findMaterial("CLOCK") : Items.findMaterial("WATCH")).setTitle("&a&oUp: Sunrise").setLore("&bClick to change the raid-shield to enable on sunrise").build()).setSlot(3).setClick(c -> {
								c.setCancelled(true);
								ClansAPI.getInstance().getShieldManager().getTamper().setUpOverride(0);
								ClansAPI.getInstance().getShieldManager().getTamper().setDownOverride(13000);
								Clan.ACTION.sendMessage(c.getElement(), "&aRaid-shield engagement changed to sunrise.");
							}));
							i.addItem(b -> b.setElement(ed -> ed.setType(Items.findMaterial("CLOCK") != null ? Items.findMaterial("CLOCK") : Items.findMaterial("WATCH")).setTitle("&a&oPermanent protection.").setLore("&bClick to freeze the raid-shield @ its current status").build()).setSlot(5).setClick(c -> {
								c.setCancelled(true);
								Player p = c.getElement();
								if (ClansAPI.getInstance().getShieldManager().getTamper().isOff()) {
									p.closeInventory();
									Clan.ACTION.sendMessage(p, "&aRaid-shield block has been lifted.");
									ClansAPI.getInstance().getShieldManager().getTamper().setIsOff(false);
								} else {
									Clan.ACTION.sendMessage(p, "&cRaid-shield has been blocked.");
									ClansAPI.getInstance().getShieldManager().getTamper().setIsOff(true);
								}
							}));
							i.addItem(b -> b.setElement(ed -> ed.setItem(getBackItem()).build()).setClick(c -> {
								c.setCancelled(true);
								SETTINGS_SELECT.get().open(c.getElement());
							}).setSlot(8));
						})
						.orGet(m -> m instanceof SingularMenu && m.getKey().map("ClansPro:shield-edit"::equals).orElse(false));
			case SETTINGS_LANGUAGE:
				return MenuType.SINGULAR.build().setHost(ClansAPI.getInstance().getPlugin())
						.setSize(Menu.Rows.ONE)
						.setTitle("&0&l» &ePick a language")
						.setKey("ClansPro:Lang")
						.setProperty(Menu.Property.CACHEABLE)
						.setStock(i -> {
							FillerElement<?> filler = new FillerElement<>(i);
							filler.add(ed -> ed.setElement(it -> it.setType(Optional.ofNullable(Items.findMaterial("bluestainedglasspane")).orElse(Items.findMaterial("stainedglasspane"))).setTitle(" ").build()));
							i.addItem(filler);
							i.addItem(b -> b.setElement(ed -> ed.setType(Material.BOOK).setTitle("&aEnglish").build()).setSlot(0).setClick(c -> {
								c.setCancelled(true);
								c.getElement().closeInventory();
								FileManager config = ClansAPI.getInstance().getFileList().get("Config", "Configuration");
								config.getRoot().delete();
								FileManager messages = ClansAPI.getInstance().getFileList().get("Messages", "Configuration");
								messages.getRoot().delete();
								FileManager nc = FileList.search(ClansAPI.getInstance().getPlugin()).get("Config", "Configuration");
								FileManager nm = FileList.search(ClansAPI.getInstance().getPlugin()).get("Messages", "Configuration");
								String type = config.read(f -> f.getNode("Formatting").getNode("file-type").toPrimitive().getString());
								if (type != null) {
									if (type.equals("JSON")) {
										FileList.search(ClansAPI.getInstance().getPlugin()).copyYML("Config_json", nc);
									} else {
										FileList.search(ClansAPI.getInstance().getPlugin()).copyYML("Config", nc);
									}
								} else {
									FileList.search(ClansAPI.getInstance().getPlugin()).copyYML("Config", nc);
								}
								nc.getRoot().reload();
								FileList.search(ClansAPI.getInstance().getPlugin()).copyYML("Messages", nm);
								nm.getRoot().reload();
								Message.form(c.getElement()).setPrefix(ClansAPI.getInstance().getPrefix().joined()).send("&a&oTranslated back to default english.");
								FileManager main = ClansAPI.getDataInstance().getConfig();

								((ClansJavaPlugin) ClansAPI.getInstance().getPlugin()).setPrefix(new MessagePrefix(main.getRoot().getString("Formatting.prefix.prefix"), main.getRoot().getString("Formatting.prefix.text"), main.getRoot().getString("Formatting.prefix.suffix")));
							}));
							i.addItem(b -> b.setElement(ed -> ed.setType(Material.BOOK).setTitle("&bPortuguese").build()).setSlot(2).setClick(c -> {
								c.setCancelled(true);
								c.getElement().closeInventory();
								FileManager config = ClansAPI.getInstance().getFileList().get("Config", "Configuration");
								config.getRoot().delete();
								FileManager messages = ClansAPI.getInstance().getFileList().get("Messages", "Configuration");
								messages.getRoot().delete();
								FileManager nc = FileList.search(ClansAPI.getInstance().getPlugin()).get("Config", "Configuration");
								FileManager nm = FileList.search(ClansAPI.getInstance().getPlugin()).get("Messages", "Configuration");
								String type = config.read(f -> f.getNode("Formatting").getNode("file-type").toPrimitive().getString());
								if (type != null) {
									if (type.equals("JSON")) {
										FileList.search(ClansAPI.getInstance().getPlugin()).copyYML("Config_pt_br_json", nc);
									} else {
										FileList.search(ClansAPI.getInstance().getPlugin()).copyYML("Config_pt_br", nc);
									}
								} else {
									FileList.search(ClansAPI.getInstance().getPlugin()).copyYML("Config_pt_br", nc);
								}
								nc.getRoot().reload();
								FileList.search(ClansAPI.getInstance().getPlugin()).copyYML("Messages_pt_br", nm);
								nm.getRoot().reload();
								Message.form(c.getElement()).setPrefix(ClansAPI.getInstance().getPrefix().joined()).send("&a&oAgora traduzido para o brasil!");

								FileManager main = ClansAPI.getDataInstance().getConfig();

								((ClansJavaPlugin) ClansAPI.getInstance().getPlugin()).setPrefix(new MessagePrefix(main.getRoot().getString("Formatting.prefix.prefix"), main.getRoot().getString("Formatting.prefix.text"), main.getRoot().getString("Formatting.prefix.suffix")));
							}));
							i.addItem(b -> b.setElement(ed -> ed.setItem(getBackItem()).build()).setClick(c -> {
								c.setCancelled(true);
								SETTINGS_RELOAD.get().open(c.getElement());
							}).setSlot(8));
						})
						.orGet(m -> m instanceof SingularMenu && m.getKey().map("ClansPro:Lang"::equals).orElse(false));
			case ADDONS_ACTIVATED:
				return MenuType.PAGINATED.build()
						.setHost(ClansAPI.getInstance().getPlugin())
						.setProperty(Menu.Property.CACHEABLE, Menu.Property.RECURSIVE)
						.setSize(Menu.Rows.SIX)
						.setTitle("&3&oRegistered Cycles &f(&2RUNNING&f) &8&l»")
						.setStock(i -> {
							ListElement<ClanAddon> list = new ListElement<>(ClanAddonQuery.getUsedNames().stream().map(ClanAddonQuery::getAddon).collect(Collectors.toList()));
							list.setLimit(28);
							list.setPopulate((addon, item) -> {
								ItemStack stack = new ItemStack(Material.CHEST);

								ItemMeta meta = stack.getItemMeta();

								meta.setLore(color("&f&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", "&2&oPersistent: &f" + addon.isPersistent(), "&f&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", "&2&oDescription: &f" + addon.getDescription(), "&f&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", "&2&oVersion: &f" + addon.getVersion(), "&f&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", "&2&oAuthors: &f" + Arrays.toString(addon.getAuthors()), "&f&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", "&2&oActive: &6&o" + ClanAddonQuery.getUsedNames().contains(addon.getName())));

								meta.setDisplayName(StringUtils.use("&3&o " + addon.getName() + " &8&l»").translate());

								stack.setItemMeta(meta);
								item.setElement(stack);
								item.setClick(click -> {
									click.setCancelled(true);
									Player p = click.getElement();
									if (ClanAddonQuery.disable(addon)) {
										for (String d : ClanAddonQuery.getDataLog()) {
											p.sendMessage(Clan.ACTION.color("&b" + d.replace("Clans [Pro]", "&3Clans &7[&6Pro&7]&b")));
										}
									}
									ADDONS_ACTIVATED.get().open(p);
								});

							});
							i.addItem(list);

							i.addItem(ed -> {
								ed.setElement(it -> it.setItem(getLeftItem()).build());
								ed.setType(ItemElement.ControlType.BUTTON_BACK);
								ed.setSlot(45);
							});

							i.addItem(ed -> {
								ed.setElement(it -> it.setItem(getRightItem()).build());
								ed.setType(ItemElement.ControlType.BUTTON_NEXT);
								ed.setSlot(53);
							});

							i.addItem(ed -> {
								ed.setElement(it -> it.setItem(getBackItem()).build());
								ed.setClick(click -> {
									click.setCancelled(true);
									ADDONS_SELECTION.get().open(click.getElement());
								});
								ed.setSlot(49);
							});

							BorderElement<?> border = new BorderElement<>(i);

							for (Menu.Panel p : Menu.Panel.values()) {
								if (p != Menu.Panel.MIDDLE) {
									border.add(p, ed -> {
										Material mat = Arrays.stream(Material.values()).anyMatch(m -> m.name().equals("LIGHT_GRAY_STAINED_GLASS_PANE")) ? Items.findMaterial("LIGHT_GRAY_STAINED_GLASS_PANE") : Items.findMaterial("STAINEDGLASSPANE");
										ed.setElement(it -> it.setType(mat).setTitle(" ").build());
									});
								}
							}

							FillerElement<?> filler = new FillerElement<>(i);
							filler.add(ed -> {
								ed.setElement(it -> it.setItem(SkullType.COMMAND_BLOCK.get()).setTitle(" ").build());
							});
							i.addItem(border);
							i.addItem(filler);

						})
						.join();
			case ADDONS_DEACTIVATED:
				return MenuType.PAGINATED.build()
						.setHost(ClansAPI.getInstance().getPlugin())
						.setProperty(Menu.Property.CACHEABLE, Menu.Property.RECURSIVE)
						.setSize(Menu.Rows.SIX)
						.setTitle("&3&oRegistered Cycles &f(&4DISABLED&f) &8&l»")
						.setStock(i -> {
							ListElement<ClanAddon> list = new ListElement<>(ClanAddonQuery.getUnusedNames().stream().map(ClanAddonQuery::getAddon).collect(Collectors.toList()));
							list.setLimit(28);
							list.setPopulate((addon, item) -> {
								ItemStack stack = new ItemStack(Material.CHEST);

								ItemMeta meta = stack.getItemMeta();

								meta.setLore(color("&f&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", "&2&oPersistent: &f" + addon.isPersistent(), "&f&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", "&2&oDescription: &f" + addon.getDescription(), "&f&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", "&2&oVersion: &f" + addon.getVersion(), "&f&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", "&2&oAuthors: &f" + Arrays.toString(addon.getAuthors()), "&f&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", "&2&oActive: &6&o" + ClanAddonQuery.getUsedNames().contains(addon.getName())));

								meta.setDisplayName(StringUtils.use("&3&o " + addon.getName() + " &8&l»").translate());

								stack.setItemMeta(meta);
								item.setElement(stack);
								item.setClick(click -> {
									click.setCancelled(true);
									Player p = click.getElement();
									if (ClanAddonQuery.enable(addon)) {
										for (String d : ClanAddonQuery.getDataLog()) {
											p.sendMessage(Clan.ACTION.color("&b" + Clan.ACTION.format(d, "Clans [Pro]", "&3Clans &7[&6Pro&7]&b")));
										}
									}
									ADDONS_ACTIVATED.get().open(p);
								});

							});
							i.addItem(list);

							i.addItem(ed -> {
								ed.setElement(it -> it.setItem(getLeftItem()).build());
								ed.setType(ItemElement.ControlType.BUTTON_BACK);
								ed.setSlot(45);
							});

							i.addItem(ed -> {
								ed.setElement(it -> it.setItem(getRightItem()).build());
								ed.setType(ItemElement.ControlType.BUTTON_NEXT);
								ed.setSlot(53);
							});

							i.addItem(ed -> {
								ed.setElement(it -> it.setItem(getBackItem()).build());
								ed.setClick(click -> {
									click.setCancelled(true);
									ADDONS_SELECTION.get().open(click.getElement());
								});
								ed.setSlot(49);
							});

							BorderElement<?> border = new BorderElement<>(i);

							for (Menu.Panel p : Menu.Panel.values()) {
								if (p != Menu.Panel.MIDDLE) {
									border.add(p, ed -> {
										Material mat = Arrays.stream(Material.values()).anyMatch(m -> m.name().equals("LIGHT_GRAY_STAINED_GLASS_PANE")) ? Items.findMaterial("LIGHT_GRAY_STAINED_GLASS_PANE") : Items.findMaterial("STAINEDGLASSPANE");
										ed.setElement(it -> it.setType(mat).setTitle(" ").build());
									});
								}
							}

							FillerElement<?> filler = new FillerElement<>(i);
							filler.add(ed -> {
								ed.setElement(it -> it.setItem(SkullType.COMMAND_BLOCK.get()).setTitle(" ").build());
							});
							i.addItem(border);
							i.addItem(filler);

						})
						.join();
			case ADDONS_REGISTERED:
				return MenuType.PAGINATED.build()
						.setHost(ClansAPI.getInstance().getPlugin())
						.setProperty(Menu.Property.CACHEABLE, Menu.Property.RECURSIVE)
						.setSize(Menu.Rows.SIX)
						.setTitle("&3&oRegistered Cycles &f(&6&lCACHE&f) &8&l»")
						.setStock(i -> {
							ListElement<ClanAddon> list = new ListElement<>(new ArrayList<>(ClanAddonQuery.getRegisteredAddons()));
							list.setLimit(28);
							list.setPopulate((addon, item) -> {
								ItemStack stack = new ItemStack(Material.CHEST);

								ItemMeta meta = stack.getItemMeta();

								meta.setLore(color("&f&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", "&2&oPersistent: &f" + addon.isPersistent(), "&f&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", "&2&oDescription: &f" + addon.getDescription(), "&f&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", "&2&oVersion: &f" + addon.getVersion(), "&f&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", "&2&oAuthors: &f" + Arrays.toString(addon.getAuthors()), "&f&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", "&2&oActive: &6&o" + ClanAddonQuery.getUsedNames().contains(addon.getName()), "&7Clicking these icons won't do anything."));

								meta.setDisplayName(StringUtils.use("&3&o " + addon.getName() + " &8&l»").translate());

								stack.setItemMeta(meta);
								item.setElement(stack);

							});
							i.addItem(list);

							i.addItem(ed -> {
								ed.setElement(it -> it.setItem(getLeftItem()).build());
								ed.setType(ItemElement.ControlType.BUTTON_BACK);
								ed.setSlot(45);
							});

							i.addItem(ed -> {
								ed.setElement(it -> it.setItem(getRightItem()).build());
								ed.setType(ItemElement.ControlType.BUTTON_NEXT);
								ed.setSlot(53);
							});

							i.addItem(ed -> {
								ed.setElement(it -> it.setItem(getBackItem()).build());
								ed.setClick(click -> {
									click.setCancelled(true);
									ADDONS_SELECTION.get().open(click.getElement());
								});
								ed.setSlot(49);
							});

							BorderElement<?> border = new BorderElement<>(i);

							for (Menu.Panel p : Menu.Panel.values()) {
								if (p != Menu.Panel.MIDDLE) {
									border.add(p, ed -> {
										Material mat = Arrays.stream(Material.values()).anyMatch(m -> m.name().equals("LIGHT_GRAY_STAINED_GLASS_PANE")) ? Items.findMaterial("LIGHT_GRAY_STAINED_GLASS_PANE") : Items.findMaterial("STAINEDGLASSPANE");
										ed.setElement(it -> it.setType(mat).setTitle(" ").build());
									});
								}
							}

							FillerElement<?> filler = new FillerElement<>(i);
							filler.add(ed -> {
								ed.setElement(it -> it.setItem(SkullType.COMMAND_BLOCK.get()).setTitle(" ").build());
							});
							i.addItem(border);
							i.addItem(filler);

						})
						.join();
		}
		return MenuType.SINGULAR.build().join();
	}

	public Menu get(Clan.Associate associate) {
		Clan cl = associate.getClan();
		String o = cl.getPalette().toString();
		String balance;
		try {
			if (associate.isEntity()) {
				balance = "3.50";
			} else {
				balance = Clan.ACTION.format(EconomyProvision.getInstance().balance(associate.getUser().toBukkit()).orElse(0.0));
			}
		} catch (NoClassDefFoundError | NullPointerException e) {
			balance = "Un-Known";
		}
		String stats;
		String rank = associate.getRankFull();
		/*
		ZonedDateTime time = associate.getJoinDate().toInstant().atZone(ZoneId.systemDefault());
		Calendar cal = Calendar.getInstance();
		cal.setTime(associate.getJoinDate());
		String temporal = cal.get(Calendar.AM_PM) == Calendar.AM ? "am" : "pm";
		 */
		String date = associate.getJoinDate().toLocaleString();//time.getMonthValue() + "/" + time.getDayOfMonth() + "/" + time.getYear() + " @ " + time.getHour() + ":" + time.getMinute() + temporal;
		String bio = associate.getBiography();
		String kd = "" + associate.getKD();
		if (Bukkit.getVersion().contains("1.15") || LabyrinthProvider.getInstance().isNew()) {
			if (associate.isEntity()) {
				stats = "|&fStatistic's unattainable.";
			} else {
				OfflinePlayer p = associate.getUser().toBukkit();
				stats = o + "Banners washed: &f" + p.getStatistic(Statistic.BANNER_CLEANED) + "|" +
						o + "Bell's rang: &f" + p.getStatistic(Statistic.BELL_RING) + "|" +
						o + "Chest's opened: &f" + p.getStatistic(Statistic.CHEST_OPENED) + "|" +
						o + "Creeper death's: &f" + p.getStatistic(Statistic.ENTITY_KILLED_BY, Entities.getEntity("Creeper")) + "|" +
						o + "Beat's dropped: &f" + p.getStatistic(Statistic.RECORD_PLAYED) + "|" +
						o + "Animal's bred: &f" + p.getStatistic(Statistic.ANIMALS_BRED);
			}
		} else {
			stats = "&c&oVersion under &61.15 |" +
					"&fOffline stat's unattainable.";
		}

		String[] statist = Clan.ACTION.color(stats).split("\\|");

		String test = MessageFormat.format(ClansAPI.getDataInstance().getMenuTitle("member-information"), associate.getName());

		if (test.length() > 32)
			test = "&0&l» " + associate.getClan().getPalette().toString() + associate.getName() + " &7Info";
		switch (this) {
			case MEMBER_INFO:
				String finalBalance = balance;
				return MenuType.SINGULAR.build().setHost(ClansAPI.getInstance().getPlugin())
						.setProperty(Menu.Property.RECURSIVE, Menu.Property.CACHEABLE)
						.setTitle(test)
						.setSize(Menu.Rows.THREE)
						.setStock(i -> {
							FillerElement<?> filler = new FillerElement<>(i);
							filler.add(ed -> ed.setType(ItemElement.ControlType.ITEM_FILLER).setElement(it -> it.setType(Optional.ofNullable(Items.findMaterial("bluestainedglasspane")).orElse(Items.findMaterial("stainedglasspane"))).setTitle(" ").build()));
							i.addItem(filler);
							BorderElement<?> border = new BorderElement<>(i);
							for (Menu.Panel p : Menu.Panel.values()) {
								if (p == Menu.Panel.MIDDLE) continue;
								if (Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.12")) {
									border.add(p, ed -> ed.setType(ItemElement.ControlType.ITEM_BORDER).setElement(it -> it.setType(Items.findMaterial("STAINED_GLASS_PANE")).setTitle(" ").build()));
								} else {
									border.add(p, ed -> ed.setType(ItemElement.ControlType.ITEM_BORDER).setElement(it -> it.setType(Material.GRAY_STAINED_GLASS_PANE).setTitle(" ").build()));
								}
							}
							i.addItem(border);
							i.addItem(b -> b.setElement(it -> it.setItem(Item.ColoredArmor.select(Item.ColoredArmor.Piece.TORSO).setColor(associate.getClan().getPalette().isHex() ? associate.getClan().getPalette().toColor() : Color.RED).build())
									.setTitle("Clan:").setLore(o + cl.getName()).build()).setSlot(13).setClick(click -> {
								click.setHotbarAllowed(false);
								click.setCancelled(true);
								Player p = click.getElement();
								p.performCommand("c info " + cl.getName());
							}));
							i.addItem(b -> b.setElement(it -> it.setItem(associate.getHead() != null ? associate.getHead() : SkullType.PLAYER.get()).setTitle("&6Click to teleport.").setLore(new Paragraph(bio + " &r- " + associate.getNickname()).setRegex(Paragraph.COMMA_AND_PERIOD).get()).build()).setSlot(4).setClick(click -> {
								click.setCancelled(true);
								if ((associate.getTag().isPlayer() && associate.getUser().isOnline()) || associate.getTag().isEntity()) {
									Clan.Associate a = ClansAPI.getInstance().getAssociate(click.getElement()).orElse(null);

									if (a != null) {
										Teleport request = a.getTeleport();
										if (request == null) {
											if (Objects.equals(associate.getName(), a.getName()))
												return;
											if (!associate.getClan().getMembers().contains(a)) {
												Clan.ACTION.sendMessage(click.getElement(), "&cYou're not in our clan.");
												return;
											}
											if (associate.isEntity()) {
												Teleport r = new Teleport.Impl(a, associate.getAsEntity().getLocation());
												r.teleport();
											} else {
												Teleport r = new Teleport.Impl(a, associate.getUser().toBukkit().getPlayer());
												r.teleport();
											}

										} else {
											click.getElement().closeInventory();
											request.cancel();
											Message.form(click.getElement()).setPrefix(ClansAPI.getInstance().getPrefix().joined()).send("&cYou already have a teleport request pending, cancelling...");
										}
									}

								} else {
									Message.form(click.getElement()).setPrefix(ClansAPI.getInstance().getPrefix().joined()).send("&cIm not online at the moment, hit me up later!");
								}
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.BOOK).setTitle(o + "Statistics:").setLore(statist).build()).setSlot(12).setClick(click -> {
								Player p = click.getElement();
								click.setCancelled(true);
								p.closeInventory();
							}));
							i.addItem(b -> b.setElement(getBackItem()).setSlot(19).setClick(click -> {
								Player p = click.getElement();
								click.setCancelled(true);
								MEMBER_LIST.get(cl).open(p);
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Items.findMaterial("GOLDENPICKAXE") != null ? Items.findMaterial("GOLDENPICKAXE") : Items.findMaterial("GOLDPICKAXE")).setTitle(o + "Rank:").setLore(o + rank).build()).setSlot(14).setClick(click -> {
								Player p = click.getElement();
								click.setCancelled(true);
								p.closeInventory();
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Items.findMaterial("ENDPORTALFRAME") != null ? Items.findMaterial("ENDPORTALFRAME") : Items.findMaterial("IRONINGOT")).setTitle(o + "Join Date:").setLore(date).build()).setSlot(22).setClick(click -> {
								Player p = click.getElement();
								click.setCancelled(true);
								p.closeInventory();
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.NAME_TAG).setTitle("&c&lEdit").build()).setSlot(26).setClick(click -> {
								Player p = click.getElement();
								click.setCancelled(true);

								if (p.hasPermission("clanspro.admin")) {
									ClansAPI.getInstance().getMenu(SETTINGS_MEMBER, associate).open(p);
								} else {
									click.getParent().remove(p, true);
									click.getParent().getParent().getParent().open(p);
								}
								p.closeInventory();
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.DIAMOND_SWORD).setTitle(o + "K/D:").setLore(o + kd).build()).setSlot(10).setClick(click -> {
								Player p = click.getElement();
								click.setCancelled(true);
								p.closeInventory();
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.GOLD_INGOT).setTitle(o + "Wallet:").setLore(o + finalBalance).build()).setSlot(16).setClick(click -> {
								Player p = click.getElement();
								click.setCancelled(true);
								p.closeInventory();
							}));

						}).join();
			case MEMBER_EDIT:
				return MenuType.SINGULAR.build().setHost(ClansAPI.getInstance().getPlugin())
						.setProperty(Menu.Property.RECURSIVE, Menu.Property.CACHEABLE)
						.setTitle("&0&l» " + cl.getPalette().toString() + associate.getName() + " settings")
						.setKey("ClansPro:member-" + associate.getName() + "-edit")
						.setSize(Menu.Rows.THREE)
						.setStock(i -> {
							FillerElement<?> filler = new FillerElement<>(i);
							filler.add(ed -> ed.setType(ItemElement.ControlType.ITEM_FILLER).setElement(it -> it.setType(Optional.ofNullable(Items.findMaterial("bluestainedglasspane")).orElse(Items.findMaterial("stainedglasspane"))).setTitle(" ").build()));
							i.addItem(filler);
							BorderElement<?> border = new BorderElement<>(i);
							for (Menu.Panel p : Menu.Panel.values()) {
								if (p == Menu.Panel.MIDDLE) continue;
								if (Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.12")) {
									border.add(p, ed -> ed.setType(ItemElement.ControlType.ITEM_BORDER).setElement(it -> it.setType(Items.findMaterial("STAINED_GLASS_PANE")).setTitle(" ").build()));
								} else {
									border.add(p, ed -> ed.setType(ItemElement.ControlType.ITEM_BORDER).setElement(it -> it.setType(Material.GRAY_STAINED_GLASS_PANE).setTitle(" ").build()));
								}
							}
							i.addItem(border);
							i.addItem(b -> b.setElement(it -> it.setItem(associate.getHead()).setFlags(ItemFlag.HIDE_ENCHANTS).addEnchantment(Enchantment.ARROW_DAMAGE, 100)
									.setTitle(ClansAPI.getDataInstance().getMenuNavigation("back")).build()).setSlot(13).setClick(click -> {
								click.setHotbarAllowed(false);
								click.setCancelled(true);
								Player p = click.getElement();
								MEMBER_LIST.get(associate.getClan()).open(p);
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.ENCHANTED_BOOK).setTitle("&7[&6&lName Change&7]").setLore("&5Click to change my name.").build()).setSlot(22).setClick(click -> {
								Player p = click.getElement();
								ClansAPI.getInstance().getAssociate(p).ifPresent(a -> {
									if (Clearance.MANAGE_NICKNAMES.test(a)) {
										MenuType.PRINTABLE.build()
												.setTitle("&2Type a name")
												.setSize(Menu.Rows.ONE)
												.setHost(ClansAPI.getInstance().getPlugin())
												.setStock(inv -> inv.addItem(be -> be.setElement(it -> it.setItem(SkullType.ARROW_BLUE_RIGHT.get()).setTitle(" ").build()).setSlot(0).setClick(c -> {
													c.setCancelled(true);
													c.setHotbarAllowed(false);
												}))).join()
												.addAction(c -> {
													c.setCancelled(true);
													c.setHotbarAllowed(false);
													if (c.getSlot() == 2) {
														associate.setNickname(c.getParent().getName());
														MEMBER_INFO.get(associate).open(c.getElement());
													}

												}).open(p);
									}
								});
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.ENCHANTED_BOOK).setTitle("&7[&6&lBio Change&7]").setLore("&5Click to change my biography.").build()).setSlot(4).setClick(click -> {
								Player p = click.getElement();
								ClansAPI.getInstance().getAssociate(p).ifPresent(a -> {
									if (Clearance.MANAGE_NICKNAMES.test(a)) {
										MenuType.PRINTABLE.build()
												.setTitle("&2Type a bio")
												.setSize(Menu.Rows.ONE)
												.setHost(ClansAPI.getInstance().getPlugin())
												.setStock(inv -> inv.addItem(be -> be.setElement(it -> it.setItem(SkullType.ARROW_BLUE_RIGHT.get()).setTitle(" ").build()).setSlot(0).setClick(c -> {
													c.setCancelled(true);
													c.setHotbarAllowed(false);
												}))).join()
												.addAction(c -> {
													c.setCancelled(true);
													c.setHotbarAllowed(false);
													if (c.getSlot() == 2) {
														associate.setBio(c.getParent().getName());
														MEMBER_INFO.get(associate).open(c.getElement());
													}

												}).open(p);
									}
								});
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.IRON_BOOTS).setTitle("&7[&4Kick&7]").setLore("&5Click to kick me.").build()).setSlot(11).setClick(click -> {
								Player p = click.getElement();
								ClansAPI.getInstance().getAssociate(p).ifPresent(a -> {
									if (Clearance.KICK_MEMBERS.test(a)) {
										associate.remove();
										Schedule.sync(() -> MEMBER_LIST.get(a.getClan()).open(p)).wait(1);
									}
								});
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.DIAMOND).setTitle("&7[&aPromotion&7]").setLore("&5Click to promote me.").build()).setSlot(14).setClick(click -> {
								Player p = click.getElement();
								ClansAPI.getInstance().getAssociate(p).ifPresent(a -> {
									if (Clearance.MANAGE_POSITIONS.test(a)) {
										Clan.ACTION.promotePlayer(associate.getId());
									}
								});
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.REDSTONE).setTitle("&7[&cDemotion&7]").setLore("&5Click to demote me.").build()).setSlot(15).setClick(click -> {
								Player p = click.getElement();
								ClansAPI.getInstance().getAssociate(p).ifPresent(a -> {
									if (Clearance.MANAGE_POSITIONS.test(a)) {
										Clan.ACTION.demotePlayer(associate.getId());
									}
								});
							}));

						}).orGet(m -> m instanceof SingularMenu && m.getKey().isPresent() && m.getKey().get().equals("ClansPro:member-" + associate.getName() + "-edit")).addAction(c -> c.setCancelled(true));
			case SETTINGS_MEMBER:
				return MenuType.SINGULAR.build().setHost(ClansAPI.getInstance().getPlugin())
						.setProperty(Menu.Property.RECURSIVE, Menu.Property.CACHEABLE)
						.setTitle("&0&l» " + cl.getPalette().toString() + associate.getName() + " settings")
						.setKey("ClansPro:member-" + associate.getName() + "-edit-settings")
						.setSize(Menu.Rows.THREE)
						.setStock(i -> {
							FillerElement<?> filler = new FillerElement<>(i);
							filler.add(ed -> ed.setType(ItemElement.ControlType.ITEM_FILLER).setElement(it -> it.setType(Optional.ofNullable(Items.findMaterial("bluestainedglasspane")).orElse(Items.findMaterial("stainedglasspane"))).setTitle(" ").build()));
							i.addItem(filler);
							BorderElement<?> border = new BorderElement<>(i);
							for (Menu.Panel p : Menu.Panel.values()) {
								if (p == Menu.Panel.MIDDLE) continue;
								if (Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.12")) {
									border.add(p, ed -> ed.setType(ItemElement.ControlType.ITEM_BORDER).setElement(it -> it.setType(Items.findMaterial("STAINED_GLASS_PANE")).setTitle(" ").build()));
								} else {
									border.add(p, ed -> ed.setType(ItemElement.ControlType.ITEM_BORDER).setElement(it -> it.setType(Material.GRAY_STAINED_GLASS_PANE).setTitle(" ").build()));
								}
							}
							i.addItem(border);
							i.addItem(b -> b.setElement(it -> it.setItem(associate.getHead() != null ? associate.getHead() : SkullType.PLAYER.get()).setFlags(ItemFlag.HIDE_ENCHANTS).addEnchantment(Enchantment.ARROW_DAMAGE, 100)
									.setTitle(ClansAPI.getDataInstance().getMenuNavigation("back")).build()).setSlot(13).setClick(click -> {
								click.setHotbarAllowed(false);
								click.setCancelled(true);
								Player p = click.getElement();
								SETTINGS_CLAN.get(cl).open(p);
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.BOOK).setTitle("&7[&bBio change&7]").setLore("&5Click to change my bio.").build()).setSlot(4).setClick(click -> {
								Player p = click.getElement();
								MenuType.PRINTABLE.build()
										.setTitle("&2Type a bio")
										.setSize(Menu.Rows.ONE)
										.setHost(ClansAPI.getInstance().getPlugin())
										.setStock(inv -> inv.addItem(be -> be.setElement(it -> it.setItem(SkullType.ARROW_BLUE_RIGHT.get()).setTitle(" ").build()).setSlot(0).setClick(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
										}))).join()
										.addAction(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
											if (c.getSlot() == 2) {
												associate.setBio(c.getParent().getName());
												MEMBER_INFO.get(associate).open(c.getElement());
											}

										}).open(p);
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.ENCHANTED_BOOK).setTitle("&7[&6&lName Change&7]").setLore("&5Click to change my name.").build()).setSlot(22).setClick(click -> {
								Player p = click.getElement();
								MenuType.PRINTABLE.build()
										.setTitle("&2Type a name")
										.setSize(Menu.Rows.ONE)
										.setHost(ClansAPI.getInstance().getPlugin())
										.setStock(inv -> inv.addItem(be -> be.setElement(it -> it.setItem(SkullType.ARROW_BLUE_RIGHT.get()).setTitle(" ").build()).setSlot(0).setClick(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
										}))).join()
										.addAction(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
											if (c.getSlot() == 2) {
												associate.setNickname(c.getParent().getName());
												MEMBER_INFO.get(associate).open(c.getElement());
											}

										}).open(p);
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.WRITTEN_BOOK).setTitle("&7[&4Switch Clans&7]").setLore("&5Click to put me in another clan.").build()).setSlot(12).setClick(click -> {
								Player p = click.getElement();
								MenuType.PRINTABLE.build()
										.setTitle("&2Type a clan name")
										.setSize(Menu.Rows.ONE)
										.setHost(ClansAPI.getInstance().getPlugin())
										.setStock(inv -> inv.addItem(be -> be.setElement(it -> it.setItem(SkullType.ARROW_BLUE_RIGHT.get()).setTitle(" ").build()).setSlot(0).setClick(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
										}))).join()
										.addAction(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
											if (c.getSlot() == 2) {
												HUID id = ClansAPI.getInstance().getClanManager().getClanID(c.getParent().getName());
												final UUID uid = associate.getUser().getId();
												if (id != null) {
													Clan clan = ClansAPI.getInstance().getClanManager().getClan(id);
													Clan.ACTION.removePlayer(uid);
													Clan.Associate newAssociate = clan.newAssociate(uid);
													if (newAssociate != null) {
														clan.add(newAssociate);
														newAssociate.save();
														MEMBER_INFO.get(newAssociate).open(c.getElement());
													}
												} else {
													Clan.ACTION.sendMessage(c.getElement(), Clan.ACTION.clanUnknown(c.getParent().getName()));
												}
											}

										}).open(p);
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.IRON_BOOTS).setTitle("&7[&4Kick&7]").setLore("&5Click to kick me.").build()).setSlot(11).setClick(click -> {
								Player p = click.getElement();
								associate.remove();
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.DIAMOND).setTitle("&7[&aPromotion&7]").setLore("&5Click to promote me.").build()).setSlot(14).setClick(click -> {
								Player p = click.getElement();
								Clan.ACTION.promotePlayer(associate.getUser().getId());
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.REDSTONE).setTitle("&7[&cDemotion&7]").setLore("&5Click to demote me.").build()).setSlot(15).setClick(click -> {
								Player p = click.getElement();
								Clan.ACTION.demotePlayer(associate.getUser().getId());
							}));

						}).orGet(m -> m instanceof SingularMenu && m.getKey().isPresent() && m.getKey().get().equals("ClansPro:member-" + associate.getName() + "-edit-settings")).addAction(c -> c.setCancelled(true));
		}
		throw new IllegalArgumentException("GUI type " + name() + " not valid, contact developers.");
	}

	public Menu get(Clan clan) {
		switch (this) {
			case CLAIM_TITLES:
				return MenuType.SINGULAR.build().setHost(ClansAPI.getInstance().getPlugin())
						.setProperty(Menu.Property.RECURSIVE, Menu.Property.CACHEABLE)
						.setTitle("&0&l» " + (clan.getPalette().isGradient() ? clan.getPalette().toGradient().context(clan.getName()).translate() : clan.getPalette().toString() + clan.getName()) + " claim titles")
						.setKey("ClansPro:titles-" + clan.getName())
						.setSize(Menu.Rows.THREE)
						.setStock(i -> {
							FillerElement<?> filler = new FillerElement<>(i);
							filler.add(ed -> ed.setType(ItemElement.ControlType.ITEM_FILLER).setElement(it -> it.setType(Optional.ofNullable(Items.findMaterial("bluestainedglasspane")).orElse(Items.findMaterial("stainedglasspane"))).setTitle(" ").build()));
							i.addItem(filler);
							BorderElement<?> border = new BorderElement<>(i);
							for (Menu.Panel p : Menu.Panel.values()) {
								if (p == Menu.Panel.MIDDLE) continue;
								if (Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.12")) {
									border.add(p, ed -> ed.setType(ItemElement.ControlType.ITEM_BORDER).setElement(it -> it.setType(Items.findMaterial("STAINED_GLASS_PANE")).setTitle(" ").build()));
								} else {
									border.add(p, ed -> ed.setType(ItemElement.ControlType.ITEM_BORDER).setElement(it -> it.setType(Material.GRAY_STAINED_GLASS_PANE).setTitle(" ").build()));
								}
							}
							i.addItem(border);
							i.addItem(b -> b.setElement(it -> it.setItem(clan.getOwner().getHead()).setFlags(ItemFlag.HIDE_ENCHANTS).addEnchantment(Enchantment.ARROW_DAMAGE, 100)
									.setLore("&5Click to go back.").setTitle("&7[&6Back to Clan Edit&7]").build()).setSlot(13).setClick(click -> {
								click.setHotbarAllowed(false);
								click.setCancelled(true);
								Player p = click.getElement();
								SETTINGS_CLAN.get(clan).open(p);
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.BOOK).setTitle("&7[&bEnter title change&7]").setLore("&5Click to change our enter title.").build()).setSlot(4).setClick(click -> {
								Player p = click.getElement();
								click.setCancelled(true);
								MenuType.PRINTABLE.build()
										.setTitle("&2Type a title")
										.setSize(Menu.Rows.ONE)
										.setHost(ClansAPI.getInstance().getPlugin())
										.setStock(inv -> inv.addItem(be -> be.setElement(it -> it.setItem(SkullType.ARROW_BLUE_RIGHT.get()).setTitle(" ").build()).setSlot(0).setClick(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
										}))).join()
										.addAction(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
											if (c.getSlot() == 2) {
												clan.setValue("claim_title", c.getParent().getName(), false);
												CLAIM_TITLES.get(clan).open(c.getElement());
											}

										}).open(p);
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.NAME_TAG).setTitle("&7[&bEnter sub-title change&7]").setLore("&5Click to change our enter sub-title.").build()).setSlot(22).setClick(click -> {
								Player p = click.getElement();
								click.setCancelled(true);
								MenuType.PRINTABLE.build()
										.setTitle("&2Type a title")
										.setSize(Menu.Rows.ONE)
										.setHost(ClansAPI.getInstance().getPlugin())
										.setStock(inv -> inv.addItem(be -> be.setElement(it -> it.setItem(SkullType.ARROW_BLUE_RIGHT.get()).setTitle(" ").build()).setSlot(0).setClick(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
										}))).join()
										.addAction(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
											if (c.getSlot() == 2) {
												clan.setValue("claim_sub_title", c.getParent().getName(), false);
												CLAIM_TITLES.get(clan).open(c.getElement());
											}

										}).open(p);
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.WRITTEN_BOOK).setTitle("&7[&bLeave title change&7]").setLore("&5Click to change our leave title.").build()).setSlot(12).setClick(click -> {
								Player p = click.getElement();
								click.setCancelled(true);
								MenuType.PRINTABLE.build()
										.setTitle("&2Type a title")
										.setSize(Menu.Rows.ONE)
										.setHost(ClansAPI.getInstance().getPlugin())
										.setStock(inv -> inv.addItem(be -> be.setElement(it -> it.setItem(SkullType.ARROW_BLUE_RIGHT.get()).setTitle(" ").build()).setSlot(0).setClick(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
										}))).join()
										.addAction(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
											if (c.getSlot() == 2) {
												clan.setValue("leave_claim_title", c.getParent().getName(), false);
												CLAIM_TITLES.get(clan).open(c.getElement());
											}

										}).open(p);
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.WRITTEN_BOOK).setTitle("&7[&bLeave sub-title change&7]").setLore("&5Click to change our leave sub-title.").build()).setSlot(14).setClick(click -> {
								Player p = click.getElement();
								click.setCancelled(true);
								MenuType.PRINTABLE.build()
										.setTitle("&2Type a title")
										.setSize(Menu.Rows.ONE)
										.setHost(ClansAPI.getInstance().getPlugin())
										.setStock(inv -> inv.addItem(be -> be.setElement(it -> it.setItem(SkullType.ARROW_BLUE_RIGHT.get()).setTitle(" ").build()).setSlot(0).setClick(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
										}))).join()
										.addAction(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
											if (c.getSlot() == 2) {
												clan.setValue("leave_claim_sub_title", c.getParent().getName(), false);
												CLAIM_TITLES.get(clan).open(c.getElement());
											}

										}).open(p);
							}));
						}).orGet(m -> m instanceof SingularMenu && m.getKey().isPresent() && m.getKey().get().equals("ClansPro:titles-" + clan.getName())).addAction(c -> c.setCancelled(true));
			case CLAIM_LIST:
				return MenuType.PAGINATED.build()
						.setHost(ClansAPI.getInstance().getPlugin())
						.setKey("ClansPro:" + clan.getName() + "-claims")
						.setSize(getSize())
						.setProperty(Menu.Property.LIVE_META)
						.setTitle("&3&lCLAIMS &0&l»")
						.setStock(i -> {
							BorderElement<?> border = new BorderElement<>(i);
							for (Menu.Panel p : Menu.Panel.values()) {
								if (p == Menu.Panel.MIDDLE) continue;
								if (Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.12")) {
									border.add(p, ed -> ed.setType(ItemElement.ControlType.ITEM_BORDER).setElement(it -> it.setType(Items.findMaterial("STAINED_GLASS_PANE")).setTitle(" ").build()));
								} else {
									border.add(p, ed -> ed.setType(ItemElement.ControlType.ITEM_BORDER).setElement(it -> it.setType(Material.GRAY_STAINED_GLASS_PANE).setTitle(" ").build()));
								}
							}
							FillerElement<?> filler = new FillerElement<>(i);
							filler.add(ed -> ed.setType(ItemElement.ControlType.ITEM_FILLER).setElement(it -> it.setItem(SkullType.COMMAND_BLOCK.get()).setTitle(" ").build()));
							i.addItem(filler);
							i.addItem(border);
							i.addItem(b -> b.setElement(getLeftItem()).setSlot(getLeft()).setTypeAndAddAction(ItemElement.ControlType.BUTTON_BACK, click -> {
								click.setCancelled(true);
								click.setHotbarAllowed(false);
								click.setConsumer((target, success) -> {

									if (success) {
										i.open(target);
									} else {
										Clan.ACTION.sendMessage(target, Clan.ACTION.color(Clan.ACTION.alreadyFirstPage()));
									}

								});
							}));

							i.addItem(b -> b.setElement(getRightItem()).setSlot(getRight()).setTypeAndAddAction(ItemElement.ControlType.BUTTON_NEXT, click -> {
								click.setCancelled(true);
								click.setHotbarAllowed(false);
								click.setConsumer((target, success) -> {
									if (success) {
										i.open(target);
									} else {
										Clan.ACTION.sendMessage(target, Clan.ACTION.color(Clan.ACTION.alreadyLastPage()));
									}

								});
							}));

							i.addItem(b -> b.setElement(getBackItem()).setSlot(getBack()).setTypeAndAddAction(ItemElement.ControlType.BUTTON_EXIT, click -> {
								click.setCancelled(true);
								click.setHotbarAllowed(false);
								click.getElement().closeInventory();
							}));
							ListElement<Claim> list = new ListElement<>(Arrays.asList(clan.getClaims()));
							list.setLimit(getLimit());
							list.setPopulate((claim, item) -> {
								item.setElement(ed -> ed.setTitle("&e# &f(" + claim.getId() + ")").setLore("&bCarriers: &f" + clan.getCarriers(claim.getChunk()).size(), "&bActive Residents: &f" + claim.getResidents().size(), "&bActive Flags: &f" + Arrays.stream(claim.getFlags()).filter(Claim.Flag::isEnabled).count()).build());
								item.setClick(click -> {
									click.setCancelled(true);
									ClansAPI.getInstance().getAssociate(click.getElement()).ifPresent(a -> {
										Location loc = claim.getLocation();
										loc.setY(claim.getLocation().getWorld().getHighestBlockYAt(claim.getLocation()));
										Clan.ACTION.teleport(click.getElement(), loc);
									});
								});
							});
							i.addItem(list);
						}).orGet(m -> m instanceof PaginatedMenu && m.getKey().map(("ClansPro:" + clan.getName() + "-claims")::equals).orElse(false));
			case HOLOGRAM_LIST:
				return MenuType.PAGINATED.build()
						.setHost(ClansAPI.getInstance().getPlugin())
						.setSize(getSize())
						.setTitle("&b&lHOLOGRAMS &0&l»")
						.setStock(i -> {
							BorderElement<?> border = new BorderElement<>(i);
							for (Menu.Panel p : Menu.Panel.values()) {
								if (p == Menu.Panel.MIDDLE) continue;
								if (Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.12")) {
									border.add(p, ed -> ed.setType(ItemElement.ControlType.ITEM_BORDER).setElement(it -> it.setType(Items.findMaterial("STAINED_GLASS_PANE")).setTitle(" ").build()));
								} else {
									border.add(p, ed -> ed.setType(ItemElement.ControlType.ITEM_BORDER).setElement(it -> it.setType(Material.GRAY_STAINED_GLASS_PANE).setTitle(" ").build()));
								}
							}
							FillerElement<?> filler = new FillerElement<>(i);
							filler.add(ed -> ed.setType(ItemElement.ControlType.ITEM_FILLER).setElement(it -> it.setItem(SkullType.COMMAND_BLOCK.get()).setTitle(" ").build()));
							i.addItem(filler);
							i.addItem(border);
							i.addItem(b -> b.setElement(getLeftItem()).setSlot(getLeft()).setTypeAndAddAction(ItemElement.ControlType.BUTTON_BACK, click -> {
								click.setCancelled(true);
								click.setHotbarAllowed(false);
								click.setConsumer((target, success) -> {

									if (success) {
										i.open(target);
									} else {
										Clan.ACTION.sendMessage(target, Clan.ACTION.color(Clan.ACTION.alreadyFirstPage()));
									}

								});
							}));

							i.addItem(b -> b.setElement(getRightItem()).setSlot(getRight()).setTypeAndAddAction(ItemElement.ControlType.BUTTON_NEXT, click -> {
								click.setCancelled(true);
								click.setHotbarAllowed(false);
								click.setConsumer((target, success) -> {
									if (success) {
										i.open(target);
									} else {
										Clan.ACTION.sendMessage(target, Clan.ACTION.color(Clan.ACTION.alreadyLastPage()));
									}

								});
							}));

							i.addItem(b -> b.setElement(getBackItem()).setSlot(getBack()).setTypeAndAddAction(ItemElement.ControlType.BUTTON_EXIT, click -> {
								click.setCancelled(true);
								click.setHotbarAllowed(false);
								click.getElement().closeInventory();
							}));
							ListElement<LogoHolder.Carrier> list = new ListElement<>(clan.getCarriers());
							list.setLimit(getLimit()).setComparator(Comparator.comparingInt(value -> value.getData().get().getLines().size()));
							list.setPopulate((stand, item) -> {
								List<String> set = stand.getLines().stream().map(line -> line.getStand().getCustomName()).collect(Collectors.toCollection(ArrayList::new));
								Collections.reverse(set);
								item.setElement(ed -> ed.setTitle("&e# &f(" + stand.getId() + ")").setLore(set).build());
								item.setClick(click -> {
									click.setCancelled(true);
									ClansAPI.getInstance().getAssociate(click.getElement()).ifPresent(a -> {
										Clan.ACTION.teleport(click.getElement(), stand.getLines().stream().findFirst().get().getStand().getLocation());
									});
								});
							});
							i.addItem(list);
						}).orGet(m -> m instanceof PaginatedMenu && m.getKey().map(("ClansPro:" + clan.getName() + "-holograms")::equals).orElse(false));
			case SETTINGS_CLAN:
				return MenuType.SINGULAR.build().setHost(ClansAPI.getInstance().getPlugin())
						.setTitle("&0&l» " + (clan.getPalette().isGradient() ? clan.getPalette().toGradient().context(clan.getName()).translate() : clan.getPalette().toString() + clan.getName()) + " settings")
						.setSize(Menu.Rows.THREE)
						.setStock(i -> {
							FillerElement<?> filler = new FillerElement<>(i);
							filler.add(ed -> ed.setType(ItemElement.ControlType.ITEM_FILLER).setElement(it -> it.setType(Optional.ofNullable(Items.findMaterial("bluestainedglasspane")).orElse(Items.findMaterial("stainedglasspane"))).setTitle(" ").build()));
							i.addItem(filler);
							BorderElement<?> border = new BorderElement<>(i);
							for (Menu.Panel p : Menu.Panel.values()) {
								if (p == Menu.Panel.MIDDLE) continue;
								if (Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.12")) {
									border.add(p, ed -> ed.setType(ItemElement.ControlType.ITEM_BORDER).setElement(it -> it.setType(Items.findMaterial("STAINED_GLASS_PANE")).setTitle(" ").build()));
								} else {
									border.add(p, ed -> ed.setType(ItemElement.ControlType.ITEM_BORDER).setElement(it -> it.setType(Material.GRAY_STAINED_GLASS_PANE).setTitle(" ").build()));
								}
							}
							i.addItem(border);
							i.addItem(b -> b.setElement(it -> it.setItem(clan.getOwner().getHead()).setFlags(ItemFlag.HIDE_ENCHANTS).addEnchantment(Enchantment.ARROW_DAMAGE, 100)
									.setLore("&5Click to manage clan members.").setTitle("&7[&6Member Edit&7]").build()).setSlot(13).setClick(click -> {
								click.setHotbarAllowed(false);
								click.setCancelled(true);
								Player p = click.getElement();
								SETTINGS_MEMBER_LIST.get(clan).open(p);
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.BOOK).setTitle("&7[&bPassword change&7]").setLore("&5Click to change our password.").build()).setSlot(4).setClick(click -> {
								Player p = click.getElement();
								click.setCancelled(true);
								MenuType.PRINTABLE.build()
										.setTitle("&2Type a bio")
										.setSize(Menu.Rows.ONE)
										.setHost(ClansAPI.getInstance().getPlugin())
										.setStock(inv -> inv.addItem(be -> be.setElement(it -> it.setItem(SkullType.ARROW_BLUE_RIGHT.get()).setTitle(" ").build()).setSlot(0).setClick(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
										}))).join()
										.addAction(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
											if (c.getSlot() == 2) {
												clan.setPassword(c.getParent().getName());
												p.performCommand("c i " + clan.getName());
											}

										}).open(p);
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.ENDER_CHEST).setTitle("&7[&5Stash&7]").setLore("&5Click to live manage our stash.").build()).setSlot(3).setClick(click -> {
								Player p = click.getElement();
								Bukkit.dispatchCommand(p, "cla view " + clan.getName() + " stash");
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.CHEST).setTitle("&7[&6Vault&7]").setLore("&5Click to live manage our vault.").build()).setSlot(5).setClick(click -> {
								Player p = click.getElement();
								Bukkit.dispatchCommand(p, "cla view " + clan.getName() + " vault");
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.SLIME_BALL).setTitle("&7[&a+Claims&7]").setLore("&5Click to give us claims.").build()).setSlot(1).setClick(click -> {
								Player p = click.getElement();
								click.setCancelled(true);
								MenuType.PRINTABLE.build()
										.setTitle("&2Type an amount")
										.setSize(Menu.Rows.ONE)
										.setHost(ClansAPI.getInstance().getPlugin())
										.setStock(inv -> inv.addItem(be -> be.setElement(it -> it.setItem(SkullType.ARROW_BLUE_RIGHT.get()).setTitle(" ").build()).setSlot(0).setClick(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
										}))).join()
										.addAction(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
											if (c.getSlot() == 2) {
												try {
													int amount = Integer.parseInt(c.getParent().getName());
													clan.giveClaims(amount);
													p.performCommand("c i " + clan.getName());
												} catch (NumberFormatException ignored) {
												}
											}

										}).open(p);
							}));
							String co = clan.isPeaceful() ? "&a" : "&4";
							i.addItem(b -> b.setElement(it -> it.setType(Material.HOPPER).setTitle("&7[" + co + "Mode&7]").setLore("&5Click to toggle our mode..").build()).setSlot(9).setClick(click -> {
								Player p = click.getElement();
								click.setCancelled(true);
								if (clan.isPeaceful()) {
									Clan.ACTION.sendMessage(p, "&aClan &r" + clan.getName() + " &atoggle to &cWAR");
									clan.setPeaceful(false);
								} else {
									Clan.ACTION.sendMessage(p, "&aClan &r" + clan.getName() + " &atoggle to &f&lPEACE");
									clan.setPeaceful(true);
								}
								GUI.SETTINGS_CLAN.get(ClansAPI.getInstance().getClanManager().getClan(clan.getId())).open(p);
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.BOOK).setTitle("&7[&dClaim Titles&7]").setLore("&5Click to modify our claim titles..").build()).setSlot(0).setClick(click -> {
								Player p = click.getElement();
								click.setCancelled(true);
								GUI.CLAIM_TITLES.get(clan).open(p);
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.SLIME_BALL).setTitle("&7[&a+Power&7]").setLore("&5Click to give us power..").build()).setSlot(10).setClick(click -> {
								Player p = click.getElement();
								click.setCancelled(true);
								MenuType.PRINTABLE.build()
										.setTitle("&2Type an amount")
										.setSize(Menu.Rows.ONE)
										.setHost(ClansAPI.getInstance().getPlugin())
										.setStock(inv -> inv.addItem(be -> be.setElement(it -> it.setItem(SkullType.ARROW_BLUE_RIGHT.get()).setTitle(" ").build()).setSlot(0).setClick(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
										}))).join()
										.addAction(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
											if (c.getSlot() == 2) {
												try {
													double amount = Double.parseDouble(c.getParent().getName());
													clan.givePower(amount);
													p.performCommand("c i " + clan.getName());
												} catch (NumberFormatException ignored) {
												}
											}

										}).open(p);
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.GOLD_INGOT).setTitle("&7[&a+Money&7]").setLore("&5Click to give us money.").build()).setSlot(19).setClick(click -> {
								Player p = click.getElement();
								click.setCancelled(true);
								MenuType.PRINTABLE.build()
										.setTitle("&2Type an amount")
										.setSize(Menu.Rows.ONE)
										.setHost(ClansAPI.getInstance().getPlugin())
										.setStock(inv -> inv.addItem(be -> be.setElement(it -> it.setItem(SkullType.ARROW_BLUE_RIGHT.get()).setTitle(" ").build()).setSlot(0).setClick(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
										}))).join()
										.addAction(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
											if (c.getSlot() == 2) {
												try {
													double amount = Double.parseDouble(c.getParent().getName());
													p.closeInventory();
													p.performCommand("cla give " + clan.getName() + " money " + amount);
												} catch (NumberFormatException ignored) {
												}
											}

										}).open(p);
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.SLIME_BALL).setTitle("&7[&c-Claims&7]").setLore("&5Click to take claims from us..").build()).setSlot(7).setClick(click -> {
								Player p = click.getElement();
								click.setCancelled(true);
								MenuType.PRINTABLE.build()
										.setTitle("&2Type an amount")
										.setSize(Menu.Rows.ONE)
										.setHost(ClansAPI.getInstance().getPlugin())
										.setStock(inv -> inv.addItem(be -> be.setElement(it -> it.setItem(SkullType.ARROW_BLUE_RIGHT.get()).setTitle(" ").build()).setSlot(0).setClick(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
										}))).join()
										.addAction(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
											if (c.getSlot() == 2) {
												try {
													int amount = Integer.parseInt(c.getParent().getName());
													clan.takeClaims(amount);
													p.performCommand("c i " + clan.getName());
												} catch (NumberFormatException ignored) {
												}
											}

										}).open(p);
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.SLIME_BALL).setTitle("&7[&c-Power&7]").setLore("&5Click to take power from us.").build()).setSlot(16).setClick(click -> {
								Player p = click.getElement();
								click.setCancelled(true);
								MenuType.PRINTABLE.build()
										.setTitle("&2Type an amount")
										.setSize(Menu.Rows.ONE)
										.setHost(ClansAPI.getInstance().getPlugin())
										.setStock(inv -> inv.addItem(be -> be.setElement(it -> it.setItem(SkullType.ARROW_BLUE_RIGHT.get()).setTitle(" ").build()).setSlot(0).setClick(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
										}))).join()
										.addAction(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
											if (c.getSlot() == 2) {
												try {
													double amount = Double.parseDouble(c.getParent().getName());
													clan.takePower(amount);
													p.performCommand("c i " + clan.getName());
												} catch (NumberFormatException ignored) {
												}
											}

										}).open(p);
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.IRON_INGOT).setTitle("&7[&c-Money&7]").setLore("&5Click to take money from us.").build()).setSlot(25).setClick(click -> {
								Player p = click.getElement();
								click.setCancelled(true);
								MenuType.PRINTABLE.build()
										.setTitle("&2Type an amount")
										.setSize(Menu.Rows.ONE)
										.setHost(ClansAPI.getInstance().getPlugin())
										.setStock(inv -> inv.addItem(be -> be.setElement(it -> it.setItem(SkullType.ARROW_BLUE_RIGHT.get()).setTitle(" ").build()).setSlot(0).setClick(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
										}))).join()
										.addAction(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
											if (c.getSlot() == 2) {
												try {
													double amount = Double.parseDouble(c.getParent().getName());
													p.performCommand("cla take " + clan.getName() + " money " + amount);
												} catch (NumberFormatException ignored) {
												}
											}

										}).open(p);
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.NAME_TAG).setTitle("&7[&e&lTag&7]").setLore("&5Click to change our tag.").build()).setSlot(22).setClick(click -> {
								Player p = click.getElement();
								click.setCancelled(true);
								MenuType.PRINTABLE.build()
										.setTitle("&2Type a new tag")
										.setSize(Menu.Rows.ONE)
										.setHost(ClansAPI.getInstance().getPlugin())
										.setStock(inv -> inv.addItem(be -> be.setElement(it -> it.setItem(SkullType.ARROW_BLUE_RIGHT.get()).setTitle(" ").build()).setSlot(0).setClick(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
										}))).join()
										.addAction(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
											if (c.getSlot() == 2) {
												clan.setName(c.getParent().getName());
												p.performCommand("c i " + c.getParent().getName());
											}

										}).open(p);
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.WRITTEN_BOOK).setTitle("&7[&cDescription&7]").setLore("&5Click to change our description.").build()).setSlot(12).setClick(click -> {
								Player p = click.getElement();
								click.setCancelled(true);
								MenuType.PRINTABLE.build()
										.setTitle("&2Type a description")
										.setSize(Menu.Rows.ONE)
										.setHost(ClansAPI.getInstance().getPlugin())
										.setStock(inv -> inv.addItem(be -> be.setElement(it -> it.setItem(SkullType.ARROW_BLUE_RIGHT.get()).setTitle(" ").build()).setSlot(0).setClick(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
										}))).join()
										.addAction(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
											if (c.getSlot() == 2) {
												clan.setDescription(c.getParent().getName());
												p.performCommand("c i " + c.getParent().getName());
											}

										}).open(p);
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.WRITTEN_BOOK).setTitle("&7[&9Color&7]").setLore("&5Click to change our color.").build()).setSlot(14).setClick(click -> {
								Player p = click.getElement();
								click.setCancelled(true);
								MenuType.PRINTABLE.build()
										.setTitle("&2Type a color")
										.setSize(Menu.Rows.ONE)
										.setHost(ClansAPI.getInstance().getPlugin())
										.setStock(inv -> inv.addItem(be -> be.setElement(it -> it.setItem(SkullType.ARROW_BLUE_RIGHT.get()).setTitle(" ").build()).setSlot(0).setClick(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
										}))).join()
										.addAction(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
											if (c.getSlot() == 2) {
												clan.setColor(c.getParent().getName());
												p.performCommand("c i " + clan.getName());
											}

										}).open(p);
							}));
							i.addItem(b -> b.setElement(it -> it.setItem(getBackItem()).build()).setSlot(17).setClick(click -> {
								Player p = click.getElement();
								SETTINGS_CLAN_ROSTER.get().open(p);
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.HOPPER).setTitle("&7[&6Base&7]").setLore("&5Click to update our base location.").build()).setSlot(8).setClick(click -> {
								Player p = click.getElement();
								clan.setBase(p.getLocation());
								Clan.ACTION.sendMessage(p, "&e" + clan.getName() + " base location updated");
							}));
							i.addItem(b -> b.setElement(it -> it.setType(Material.LAVA_BUCKET).setTitle("&7[&4Close&7]").setLore("&5Click to close our clan.").build()).setSlot(26).setClick(click -> {
								Player p = click.getElement();
								click.setCancelled(true);
								MenuType.PRINTABLE.build()
										.setTitle("&01 for &aYES &02 for &cNO")
										.setSize(Menu.Rows.ONE)
										.setHost(ClansAPI.getInstance().getPlugin())
										.setStock(inv -> inv.addItem(be -> be.setElement(it -> it.setItem(SkullType.ARROW_BLUE_RIGHT.get()).setTitle("0").build()).setSlot(0).setClick(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
										}))).join()
										.addAction(c -> {
											c.setCancelled(true);
											c.setHotbarAllowed(false);
											if (c.getSlot() == 2) {
												if (c.getParent().getName().equals("1")) {
													Clan.ACTION.removePlayer(clan.getOwner().getId());
													p.closeInventory();
												} else {
													p.closeInventory();
													Clan.ACTION.sendMessage(p, "&cFailed to confirm deletion.");
												}
											}

										}).open(p);
							}));

						}).join().addAction(c -> c.setCancelled(true));
			case SETTINGS_MEMBER_LIST:
				return MenuType.PAGINATED.build()
						.setHost(ClansAPI.getInstance().getPlugin())
						.setKey("ClansPro:" + clan.getName() + "-members-edit")
						.setSize(getSize())
						.setTitle(Clan.ACTION.color(ClansAPI.getDataInstance().getMenuTitle("member-list")))
						.setStock(i -> {
							BorderElement<?> border = new BorderElement<>(i);
							for (Menu.Panel p : Menu.Panel.values()) {
								if (p == Menu.Panel.MIDDLE) continue;
								if (Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.12")) {
									border.add(p, ed -> ed.setType(ItemElement.ControlType.ITEM_BORDER).setElement(it -> it.setType(Items.findMaterial("STAINED_GLASS_PANE")).setTitle(" ").build()));
								} else {
									border.add(p, ed -> ed.setType(ItemElement.ControlType.ITEM_BORDER).setElement(it -> it.setType(Material.GRAY_STAINED_GLASS_PANE).setTitle(" ").build()));
								}
							}
							FillerElement<?> filler = new FillerElement<>(i);
							filler.add(ed -> ed.setType(ItemElement.ControlType.ITEM_FILLER).setElement(it -> it.setItem(SkullType.COMMAND_BLOCK.get()).setTitle(" ").build()));
							i.addItem(filler);
							i.addItem(border);
							i.addItem(b -> b.setElement(getLeftItem()).setSlot(getLeft()).setTypeAndAddAction(ItemElement.ControlType.BUTTON_BACK, click -> {
								click.setCancelled(true);
								click.setHotbarAllowed(false);
								click.setConsumer((target, success) -> {

									if (success) {
										i.open(target);
									} else {
										Clan.ACTION.sendMessage(target, Clan.ACTION.color(Clan.ACTION.alreadyFirstPage()));
									}

								});
							}));

							i.addItem(b -> b.setElement(getRightItem()).setSlot(getRight()).setTypeAndAddAction(ItemElement.ControlType.BUTTON_NEXT, click -> {
								click.setCancelled(true);
								click.setHotbarAllowed(false);
								click.setConsumer((target, success) -> {
									if (success) {
										i.open(target);
									} else {
										Clan.ACTION.sendMessage(target, Clan.ACTION.color(Clan.ACTION.alreadyLastPage()));
									}

								});
							}));

							i.addItem(b -> b.setElement(getBackItem()).setSlot(getBack()).setTypeAndAddAction(ItemElement.ControlType.BUTTON_EXIT, click -> {
								click.setCancelled(true);
								click.setHotbarAllowed(false);
								SETTINGS_CLAN.get(clan).open(click.getElement());
							}));
							i.addItem(new ListElement<>(new ArrayList<>(clan.getMembers())).setLimit(getLimit()).setPopulate((value, element) -> element.setElement(it -> it.setItem(value.getHead()).setTitle(clan.getPalette().toString() + value.getName()).setLore("&5Click to view my information.").build()).setClick(c -> {
								c.setCancelled(true);
								SETTINGS_MEMBER.get(value).open(c.getElement());
							})));
						}).orGet(m -> m instanceof PaginatedMenu && m.getKey().map(("ClansPro:" + clan.getName() + "-members-edit")::equals).orElse(false));
			case MEMBER_LIST:
				return MenuType.PAGINATED.build()
						.setHost(ClansAPI.getInstance().getPlugin())
						.setKey("ClansPro:" + clan.getName() + "-members")
						.setSize(getSize())
						.setTitle(Clan.ACTION.color(ClansAPI.getDataInstance().getMenuTitle("member-list")))
						.setStock(i -> {
							BorderElement<?> border = new BorderElement<>(i);
							for (Menu.Panel p : Menu.Panel.values()) {
								if (p == Menu.Panel.MIDDLE) continue;
								if (Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.12")) {
									border.add(p, ed -> ed.setType(ItemElement.ControlType.ITEM_BORDER).setElement(it -> it.setType(Items.findMaterial("STAINED_GLASS_PANE")).setTitle(" ").build()));
								} else {
									border.add(p, ed -> ed.setType(ItemElement.ControlType.ITEM_BORDER).setElement(it -> it.setType(Material.GRAY_STAINED_GLASS_PANE).setTitle(" ").build()));
								}
							}
							FillerElement<?> filler = new FillerElement<>(i);
							filler.add(ed -> ed.setType(ItemElement.ControlType.ITEM_FILLER).setElement(it -> it.setItem(SkullType.COMMAND_BLOCK.get()).setTitle(" ").build()));
							i.addItem(filler);
							i.addItem(border);
							i.addItem(b -> b.setElement(getLeftItem()).setSlot(getLeft()).setTypeAndAddAction(ItemElement.ControlType.BUTTON_BACK, click -> {
								click.setCancelled(true);
								click.setHotbarAllowed(false);
								click.setConsumer((target, success) -> {

									if (success) {
										i.open(target);
									} else {
										Clan.ACTION.sendMessage(target, Clan.ACTION.color(Clan.ACTION.alreadyFirstPage()));
									}

								});
							}));

							i.addItem(b -> b.setElement(getRightItem()).setSlot(getRight()).setTypeAndAddAction(ItemElement.ControlType.BUTTON_NEXT, click -> {
								click.setCancelled(true);
								click.setHotbarAllowed(false);
								click.setConsumer((target, success) -> {
									if (success) {
										i.open(target);
									} else {
										Clan.ACTION.sendMessage(target, Clan.ACTION.color(Clan.ACTION.alreadyLastPage()));
									}

								});
							}));

							i.addItem(b -> b.setElement(getBackItem()).setSlot(getBack()).setTypeAndAddAction(ItemElement.ControlType.BUTTON_EXIT, click -> {
								click.setCancelled(true);
								click.setHotbarAllowed(false);
								getTemp(click.getElement()).open(click.getElement());
							}));
							i.addItem(new ListElement<>(new ArrayList<>(clan.getMembers())).setLimit(getLimit()).setPopulate((value, element) -> element.setElement(it -> it.setItem(value.getHead() != null ? value.getHead() : SkullType.PLAYER.get()).setTitle(value.getNickname() != null && !value.getNickname().equals(value.getName()) ? clan.getPalette().toString() + value.getName() + " &r(" + clan.getPalette().toString(value.getNickname()) + "&r)" : clan.getPalette().toString() + value.getName()).setLore("&5Click to view my information.", "", "&c&oShift-Right click to edit (Member).").build()).setClick(c -> {
								c.setCancelled(true);
								if (clan.getMember(a -> a.getName().equals(c.getElement().getName())) != null) {
									if (c.getClickType() == ClickType.SHIFT_RIGHT) {
										MEMBER_EDIT.get(value).open(c.getElement());
									} else {
										MEMBER_INFO.get(value).open(c.getElement());
									}
								} else {
									MEMBER_INFO.get(value).open(c.getElement());
								}
							})));
						}).orGet(m -> m instanceof PaginatedMenu && m.getKey().map(("ClansPro:" + clan.getName() + "-members")::equals).orElse(false));
		}
		throw new IllegalArgumentException("GUI type " + name() + " not valid, contact developers.");
	}

	public Menu get(War war) {
		switch (this) {
			case ARENA_SPAWN:
				return MenuType.SINGULAR.build().setHost(ClansAPI.getInstance().getPlugin())
						.setSize(Menu.Rows.ONE)
						.setTitle("&2&oTeleport to &a" + war.getId() + " &0&l»")
						.setKey("ClansPro:war-" + war.getId())
						.setProperty(Menu.Property.CACHEABLE)
						.setStock(i -> {
							FillerElement<?> filler = new FillerElement<>(i);
							filler.add(ed -> ed.setElement(it -> it.setType(Optional.ofNullable(Items.findMaterial("bluestainedglasspane")).orElse(Items.findMaterial("stainedglasspane"))).setTitle(" ").build()));
							i.addItem(filler);
							i.addItem(b -> b.setElement(ed -> ed.setType(Items.findMaterial("CLOCK") != null ? Items.findMaterial("CLOCK") : Items.findMaterial("WATCH")).setTitle("&7[&6&lClick&7]").setLore("&bClick to teleport to your spawn in arena &e" + war.getId()).build()).setSlot(4).setClick(c -> {
								c.setCancelled(true);
								ClansAPI.getInstance().getAssociate(c.getElement()).ifPresent(a -> {
									War.Team t = war.getTeam(a.getClan());
									Location loc = t.getSpawn();
									if (loc == null) {
										Clan.ACTION.sendMessage(c.getElement(), "&cYour team's spawn location isn't properly setup. Contact staff for support.");
										return;
									}
									c.getElement().teleport(loc);
								});
							}));
						})
						.orGet(m -> m instanceof SingularMenu && m.getKey().map(("ClansPro:war-" + war.getId())::equals).orElse(false));
			case ARENA_TRUCE:
				return MenuType.SINGULAR.build().setHost(ClansAPI.getInstance().getPlugin())
						.setSize(Menu.Rows.ONE)
						.setTitle("&2&oTruce Vote &0&l»")
						.setKey("ClansPro:war-" + war.getId() + "-truce")
						.setProperty(Menu.Property.CACHEABLE)
						.setStock(i -> {
							FillerElement<?> filler = new FillerElement<>(i);
							filler.add(ed -> ed.setElement(it -> it.setType(Optional.ofNullable(Items.findMaterial("bluestainedglasspane")).orElse(Items.findMaterial("stainedglasspane"))).setTitle(" ").build()));
							i.addItem(filler);
							i.addItem(b -> b.setElement(ed -> ed.setType(Items.findMaterial("reddye") != null ? Items.findMaterial("reddye") : Items.findMaterial("lavabucket")).setTitle("&7[&4&lNO&7]").setLore("&cClick to vote no").build()).setSlot(2).setClick(c -> {
								c.setCancelled(true);
								ClansAPI.getInstance().getAssociate(c.getElement()).ifPresent(a -> {
									Vote v = war.getVote();
									v.cast(Vote.NO);
									for (Clan cl : war.getQueue().getTeams()) {
										cl.broadcast("&aWar participant " + a.getNickname() + " voted &cno &aon a truce.");
									}
									int acount = war.getQueue().size();
									if (acount > 1) {
										acount = Math.floorDiv(acount, 2);
									}
									if (v.count(Vote.NO) >= acount) {
										v.clear();
										LabyrinthProvider.getService(Service.MESSENGER).getNewMessage().setPrefix(ClansAPI.getInstance().getPrefix().joined()).broadcast("&c&oTruce amongst the clans failed . Not enough votes yes.");
									}
								});
								c.getElement().closeInventory();
							}));
							i.addItem(b -> b.setElement(ed -> ed.setType(Items.findMaterial("greendye") != null ? Items.findMaterial("greendye") : Items.findMaterial("waterbucket")).setTitle("&7[&2&lYES&7]").setLore("&cClick to vote yes").build()).setSlot(6).setClick(c -> {
								c.setCancelled(true);
								ClansAPI.getInstance().getAssociate(c.getElement()).ifPresent(a -> {
									War.Team t = war.getTeam(a.getClan());
									if (t != null) {
										Vote v = war.getVote(t);
										v.cast(Vote.YES);
										for (Clan cl : war.getQueue().getTeams()) {
											cl.broadcast("&aWar participant " + a.getNickname() + " voted &6yes &aon a truce.");
										}
										int acount = war.getQueue().size();
										if (acount > 1) {
											acount = Math.floorDiv(acount, 2);
										}
										if (v.count(Vote.YES) >= acount) {
											if (v.isUnanimous()) {
												v.clear();
												LabyrinthProvider.getService(Service.MESSENGER).getNewMessage().setPrefix(ClansAPI.getInstance().getPrefix().joined()).broadcast("&c&oTruce amongst the clans failed . Not enough votes yes.");
											} else {
												// good to go cancel
												if (war.stop()) {
													war.reset();
													a.getClan().takePower(8.6);
													LabyrinthProvider.getService(Service.MESSENGER).getNewMessage().setPrefix(ClansAPI.getInstance().getPrefix().joined()).broadcast("&3&oA truce was called and the war is over.");
												}
											}
										}
									}
								});
								c.getElement().closeInventory();
							}));
						})
						.orGet(m -> m instanceof SingularMenu && m.getKey().map(("ClansPro:war-" + war.getId() + "-truce")::equals).orElse(false));
			case ARENA_SURRENDER:
				return MenuType.SINGULAR.build().setHost(ClansAPI.getInstance().getPlugin())
						.setSize(Menu.Rows.ONE)
						.setTitle("&2&oSurrender Vote &0&l»")
						.setKey("ClansPro:war-" + war.getId() + "-surrender")
						.setProperty(Menu.Property.CACHEABLE)
						.setStock(i -> {
							FillerElement<?> filler = new FillerElement<>(i);
							filler.add(ed -> ed.setElement(it -> it.setType(Optional.ofNullable(Items.findMaterial("bluestainedglasspane")).orElse(Items.findMaterial("stainedglasspane"))).setTitle(" ").build()));
							i.addItem(filler);
							i.addItem(b -> b.setElement(ed -> ed.setType(Items.findMaterial("reddye") != null ? Items.findMaterial("reddye") : Items.findMaterial("lavabucket")).setTitle("&7[&4&lNO&7]").setLore("&cClick to vote no").build()).setSlot(2).setClick(c -> {
								c.setCancelled(true);
								ClansAPI.getInstance().getAssociate(c.getElement()).ifPresent(a -> {
									War.Team t = war.getTeam(a.getClan());
									if (t != null) {
										Vote v = war.getVote(t);
										v.cast(Vote.NO);
										for (Clan cl : war.getQueue().getTeams()) {
											cl.broadcast("&aWar participant " + a.getNickname() + " voted &cno &aon surrendering.");
										}
										int acount = war.getQueue().count(a.getClan());
										if (acount > 1) {
											acount = Math.floorDiv(acount, 2);
										}
										if (v.count(Vote.NO) >= acount) {
											v.clear();
											LabyrinthProvider.getService(Service.MESSENGER).getNewMessage().setPrefix(ClansAPI.getInstance().getPrefix().joined()).broadcast("&c&oClan " + a.getClan().getName() + " failed to surrender. Not enough votes yes.");
										}
									}
								});
								c.getElement().closeInventory();
							}));
							i.addItem(b -> b.setElement(ed -> ed.setType(Items.findMaterial("greendye") != null ? Items.findMaterial("greendye") : Items.findMaterial("waterbucket")).setTitle("&7[&2&lYES&7]").setLore("&cClick to vote yes").build()).setSlot(6).setClick(c -> {
								c.setCancelled(true);
								ClansAPI.getInstance().getAssociate(c.getElement()).ifPresent(a -> {
									War.Team t = war.getTeam(a.getClan());
									if (t != null) {
										Vote v = war.getVote(t);
										v.cast(Vote.YES);
										for (Clan cl : war.getQueue().getTeams()) {
											cl.broadcast("&aWar participant " + a.getNickname() + " voted &6yes &aon surrendering.");
										}
										int acount = war.getQueue().count(a.getClan());
										if (acount > 1) {
											acount = Math.floorDiv(acount, 2);
										}
										if (v.count(Vote.YES) >= acount) {
											if (v.isUnanimous()) {
												v.clear();
												// Mutual votes. cancel voting
												LabyrinthProvider.getService(Service.MESSENGER).getNewMessage().setPrefix(ClansAPI.getInstance().getPrefix().joined()).broadcast("&c&oClan " + a.getClan().getName() + " failed to surrender. Voting came to a draw.");
											} else {
												// good to go cancel
												if (war.stop()) {
													war.reset();
													a.getClan().takePower(8.6);
													LabyrinthProvider.getService(Service.MESSENGER).getNewMessage().setPrefix(ClansAPI.getInstance().getPrefix().joined()).broadcast("&5&oClan " + a.getClan().getName() + " has surrendered.");
												}
											}
										}
									}
								});
								c.getElement().closeInventory();
							}));
						})
						.orGet(m -> m instanceof SingularMenu && m.getKey().map(("ClansPro:war-" + war.getId() + "-surrender")::equals).orElse(false));

			default:
				throw new IllegalArgumentException("GUI type " + name() + " not valid, contact developers.");
		}
	}

	Menu.Rows getSize() {
		return Menu.Rows.valueOf(ClansAPI.getDataInstance().getMessageString("pagination-size"));
	}

	List<String> color(String... text) {
		ArrayList<String> convert = new ArrayList<>();
		for (String t : text) {
			convert.add(StringUtils.use(t).translate());
		}
		return convert;
	}

	ItemStack getRightItem() {
		ItemStack right = ClansAPI.getDataInstance().getMenuItem("navigate_right");
		ItemMeta meta = right.getItemMeta();
		meta.setDisplayName(StringUtils.use(ClansAPI.getDataInstance().getMenuNavigation("right")).translate());
		right.setItemMeta(meta);
		return right;
	}

	ItemStack getLeftItem() {
		ItemStack left = ClansAPI.getDataInstance().getMenuItem("navigate_left");
		ItemMeta meta = left.getItemMeta();
		meta.setDisplayName(StringUtils.use(ClansAPI.getDataInstance().getMenuNavigation("left")).translate());
		left.setItemMeta(meta);
		return left;
	}

	ItemStack getBackItem() {
		ItemStack back = ClansAPI.getDataInstance().getMenuItem("back");
		ItemMeta meta = back.getItemMeta();
		meta.setDisplayName(StringUtils.use(ClansAPI.getDataInstance().getMenuNavigation("back")).translate());
		back.setItemMeta(meta);
		return back;
	}

	int getLimit() {
		int amnt = 0;
		switch (getSize().getSize()) {
			case 9:
				amnt = 6;
				break;
			case 18:
				amnt = 15;
				break;
			case 27:
				amnt = 7;
				break;
			case 36:
				amnt = 14;
				break;
			case 45:
				amnt = 21;
				break;
			case 54:
				amnt = 28;
				break;
		}
		return amnt;
	}

	int getBack() {
		int amnt = 0;
		switch (getSize().getSize()) {
			case 9:
				amnt = 7;
				break;
			case 18:
				amnt = 16;
				break;
			case 27:
				amnt = 22;
				break;
			case 36:
				amnt = 31;
				break;
			case 45:
				amnt = 40;
				break;
			case 54:
				amnt = 49;
				break;
		}
		return amnt;
	}

	int getLeft() {
		int amnt = 0;
		switch (getSize().getSize()) {
			case 9:
				amnt = 6;
				break;
			case 18:
				amnt = 15;
				break;
			case 27:
				amnt = 21;
				break;
			case 36:
				amnt = 30;
				break;
			case 45:
				amnt = 39;
				break;
			case 54:
				amnt = 48;
				break;
		}
		return amnt;
	}

	int getRight() {
		int amnt = 0;
		switch (getSize().getSize()) {
			case 9:
				amnt = 8;
				break;
			case 18:
				amnt = 17;
				break;
			case 27:
				amnt = 23;
				break;
			case 36:
				amnt = 32;
				break;
			case 45:
				amnt = 41;
				break;
			case 54:
				amnt = 50;
				break;
		}
		return amnt;
	}

}