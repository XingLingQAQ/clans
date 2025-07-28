package com.github.sanctum.clans.util;

import com.github.sanctum.clans.model.Clan;
import com.github.sanctum.clans.model.backend.ConfigurationFileBackend;
import com.github.sanctum.panther.container.PantherList;
import com.github.sanctum.panther.container.PantherSet;
import org.jetbrains.annotations.NotNull;

public class ClanLevelTree {

    public static ClanLevelTree INSTANCE;

    public Range getRange(Clan clan) {
        double power = clan.getPower();
        // skim ranges loaded into the config finger print and look for a match
        Range range = ConfigurationFileBackend.CONFIG.values().stream().filter(v -> v instanceof Range).map(o -> (Range)o).filter(r -> power >= r.pos1 && power <= r.pos2).findFirst().orElse(null);
        return range;
    }

    public static ClanLevelTree getInstance() {
        return INSTANCE != null ? INSTANCE : (INSTANCE = new ClanLevelTree());
    }

    public static class Range {

        private final int level, pos1, pos2;

        public Range(int level, int pos1, int pos2) {
            this.level = level;
            this.pos1 = pos1;
            this.pos2 = pos2;
        }

        public int getLevel() {
            return level;
        }

    }

}
