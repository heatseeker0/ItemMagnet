package com.mcspacecraft.itemmagnet;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import com.mcspacecraft.itemmagnet.util.MessageUtils;

public class MagnetConfig {
    private final ItemMagnet plugin;
    private FileConfiguration config;

    private boolean debug = false;
    private int magnetDistance = 10;

    private Map<String, String> messages = new HashMap<>();

    public MagnetConfig(ItemMagnet plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.saveDefaultConfig();
        applyConfig();
    }

    private void applyConfig() {
        plugin.reloadConfig();

        config = plugin.getConfig();
        debug = config.getBoolean("debug", false);
        magnetDistance = config.getInt("magnet-distance", 10);

        messages.clear();
        for (String msgKey : config.getConfigurationSection("messages").getKeys(false)) {
            messages.put(msgKey, MessageUtils.parseColors(config.getString("messages." + msgKey)));
        }
    }

    public boolean getDebug() {
        return debug;
    }

    public int getMagnetDistance() {
        return magnetDistance;
    }

    public FileConfiguration getRawConfig() {
        return config;
    }

    public String getMessage(final String key) {
        if (messages.containsKey(key)) {
            return messages.get(key);
        }
        final String errorMsg = "No message text in config.yml for " + key;
        plugin.logErrorMessage(errorMsg);
        return errorMsg;
    }
}
