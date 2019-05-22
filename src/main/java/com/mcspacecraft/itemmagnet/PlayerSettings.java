package com.mcspacecraft.itemmagnet;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;

import com.mcspacecraft.itemmagnet.util.SLAPI;

public class PlayerSettings {
    public static final String SETTINGS_FILE = "magnet_settings.dat";
    private Map<UUID, Boolean> settings = new HashMap<>();
    private boolean changed = false;

    public PlayerSettings() {
        Bukkit.getScheduler().runTaskTimer(ItemMagnet.getPlugin(), new SettingsSaver(), 20L * 30, 20L * 30);
        loadSettings();
    }

    public boolean isEnabled(UUID playerId) {
        return settings.getOrDefault(playerId, true);
    }

    public void set(UUID playerId, boolean toggle) {
        settings.put(playerId, toggle);
        changed = true;
    }

    public void saveSettings() {
        try {
            SLAPI.save(settings, ItemMagnet.getPlugin().getDataFolder() + "/" + SETTINGS_FILE);
            changed = false;
            ItemMagnet.getPlugin().logInfoMessage("Saved %d magnet player settings to disk.", settings.size());
        } catch (Exception e) {
            ItemMagnet.getPlugin().logErrorMessage("Error saving magnet player settings to disk: ", e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadSettings() {
        try {
            settings = (Map<UUID, Boolean>) SLAPI.load(ItemMagnet.getPlugin().getDataFolder() + "/" + SETTINGS_FILE);
            changed = false;
            ItemMagnet.getPlugin().logInfoMessage("Loaded %d saved magnet player settings from disk.", settings.size());
        } catch (Exception e) {
            ItemMagnet.getPlugin().logErrorMessage("Error loading magnet player settings from disk: ", e.getMessage());
        }
    }

    class SettingsSaver implements Runnable {
        @Override
        public void run() {
            if (!changed) {
                return;
            }

            saveSettings();
        }
    }
}
