package com.github.sanctum.clans.model.backend;

import com.github.sanctum.clans.model.ClanError;
import com.github.sanctum.clans.model.ClansAPI;
import com.github.sanctum.clans.util.ClanLevelTree;
import com.github.sanctum.clans.util.StringLibrary;
import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.labyrinth.data.reload.FingerMap;
import com.github.sanctum.labyrinth.data.reload.FingerPrint;
import com.github.sanctum.labyrinth.data.service.Constant;
import com.github.sanctum.labyrinth.library.NamespacedKey;
import com.github.sanctum.panther.util.Deployable;
import com.github.sanctum.panther.util.EasyTypeAdapter;
import com.github.sanctum.panther.util.TypeAdapter;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class ConfigurationFileBackend implements FingerPrint {

    public static final ConfigurationFileBackend CONFIG = new ConfigurationFileBackend(Type.CONFIG, "reload_config");
    public static final ConfigurationFileBackend MESSAGES = new ConfigurationFileBackend(Type.MESSAGES, "reload_messages");

    final FileManager dataManager;
    final NamespacedKey key;
    final Map<String, Object> dataMap = new HashMap<>();
    private FingerMap fingerMap;
    private final Type type;

    ConfigurationFileBackend(@NotNull Type type, @NotNull String key) {
        this.type = type;
        this.key = new NamespacedKey(ClansAPI.getInstance().getPlugin(), key);
        if (type == Type.CONFIG) {
            this.dataManager = ClansAPI.getDataInstance().getConfig();
        } else {
            this.dataManager = ClansAPI.getDataInstance().getMessages();
        }
    }

    void loadConfigMap() {
        this.fingerMap = () -> {
            Map<String, Object> map = new HashMap<>();
            String node = "clans.levels.tree";
            dataManager.getRoot().getNode(node).getKeys(false ).forEach(level -> {
                // load all configured level ranges
                String range = dataManager.getRoot().getNode(node).getNode(level).toPrimitive().getString();
                ClanLevelTree.Range r = new ClanLevelTree.Range(Integer.parseInt(level), Integer.parseInt(range.split("-")[0]), Integer.parseInt(range.split("-")[1]));
                map.put("clans_level_" + level, r);
            });
            return map;
        };
        dataMap.putAll(fingerMap.accept());
    }

    void loadMessagesMap() {
        this.fingerMap = () -> {
            Map<String, Object> map = new HashMap<>();
            // Magically read all constants with their respectively matching keys and load the data
            // Basically any key we list in the string library (THE STRING LIBRARY IS AN ACTUAL LIBRARY NOW BABY LETS GO!!) is an automagically loaded message file response.
            List<Constant<String>> constants = Constant.values(StringLibrary.class, TypeAdapter.STRING);
            for (Constant<String> CON : constants) {
                map.put(CON.getName(), dataManager.read(c -> c.getString(CON.getValue())));
            }
            return map;
        };
        dataMap.putAll(fingerMap.accept());
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return this.key;
    }

    public Object get(String key) {
        return this.dataMap.get(key);
    }

    public boolean getBoolean(String key) {
        return this.dataMap.containsKey(key) ? (new EasyTypeAdapter<Boolean>(){}).cast(this.dataMap.get(key)) : false;
    }

    public String getString(String key) {
        return this.dataMap.containsKey(key) && String.class.isAssignableFrom(this.dataMap.get(key).getClass()) ? (new EasyTypeAdapter<String>(){}).cast(this.dataMap.get(key)) : null;
    }

    public @NotNull Number getNumber(String key) {
        return this.dataMap.containsKey(key) && Number.class.isAssignableFrom(this.dataMap.get(key).getClass()) ? (Number) this.dataMap.get(key) : 0.0;
    }

    public @NotNull List<String> getStringList(String key) {
        return (List<String>) (this.dataMap.containsKey(key) && List.class.isAssignableFrom(this.dataMap.get(key).getClass()) && String.class.isAssignableFrom(((List) this.dataMap.get(key)).get(0).getClass()) ? (new EasyTypeAdapter<List>(){}).cast(this.dataMap.get(key)) : new ArrayList());
    }

    public @NotNull Deployable<Map<String, Object>> clear() {
        return Deployable.of(this.dataMap, Map::clear, 0);
    }

    public Collection<Object> values(){
        return dataMap.values();
    }

    public @NotNull Deployable<FingerPrint> reload(String key) {
        return Deployable.of(() -> {
            Map<String, Object> test = fingerMap.accept();
            if (test.containsKey(key)) {
                this.dataMap.put(key, test.get(key));
                return this;
            } else {
                throw new ClanError("Cannot reload non-existing clan attributes!");
            }
        }, 0);
    }

    public @NotNull Deployable<FingerPrint> reload() {
        return Deployable.of(() -> {
            this.clear().deploy((m) -> {
                m.putAll(fingerMap.accept());
            });
            return this;
        }, 0);
    }

    public Type getType() {
        return this.type;
    }

    public enum Type {

        CONFIG, MESSAGES

    }

}
