package com.mcspacecraft.itemmagnet;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemMagnet extends JavaPlugin {
    private static ItemMagnet plugin;
    public static final Logger logger = Logger.getLogger("Minecraft.ItemMagnet");

    private MagnetConfig config;
    private PlayerSettings playerSettings;

    @Override
    public void onEnable() {
        plugin = this;

        config = new MagnetConfig(this);
        config.load();

        playerSettings = new PlayerSettings();

        getCommand("itemmagnet").setExecutor(new MagnetCommandHandler(this));

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new ItemSearch(), 20L, 20L);
    }

    @Override
    public void onDisable() {
        playerSettings.saveSettings();
    }

    public static ItemMagnet getPlugin() {
        return plugin;
    }

    public MagnetConfig getMagnetConfig() {
        return config;
    }

    public PlayerSettings getPlayerSettings() {
        return playerSettings;
    }

    class ItemSearch implements Runnable {
        @Override
        public void run() {
            double magnetDistanceSq = Math.pow(getMagnetConfig().getMagnetDistance(), 2);

            for (World world : getServer().getWorlds()) {
                List<Player> players = world.getPlayers();
                // No players in this world, nobody to pick items up
                if (players.isEmpty()) {
                    continue;
                }

                for (Item item : world.getEntitiesByClass(Item.class)) {
                    Location itemLocation = item.getLocation();

                    // Make sure we don't pick up items we just threw on the ground
                    if (item.getPickupDelay() > item.getTicksLived()) {
                        continue;
                    }

                    // Find the closest player to the item, within magnet distance
                    Player closestPlayer = null;
                    double closestPlayerDistance = Double.MAX_VALUE;
                    for (Player player : players) {
                        if (!plugin.getPlayerSettings().isEnabled(player.getUniqueId())) {
                            continue;
                        }

                        double playerDistance = player.getLocation().distanceSquared(itemLocation);
                        if (playerDistance <= magnetDistanceSq && playerDistance < closestPlayerDistance) {
                            closestPlayerDistance = playerDistance;
                            closestPlayer = player;
                        }
                    }

                    if (closestPlayer != null) {
                        item.setVelocity(closestPlayer.getLocation().toVector().subtract(itemLocation.toVector()).normalize());
                    }
                }
            }
        }
    }

    public void logInfoMessage(final String msg, final Object... args) {
        if (args == null || args.length == 0) {
            logger.info(String.format("[%s] %s", getDescription().getName(), msg));
        } else {
            logger.info(String.format("[%s] %s", getDescription().getName(), String.format(msg, args)));
        }
    }

    public void logErrorMessage(final String msg, final Object... args) {
        if (args == null || args.length == 0) {
            logger.severe(String.format("[%s] %s", getDescription().getName(), msg));
        } else {
            logger.severe(String.format("[%s] %s", getDescription().getName(), String.format(msg, args)));
        }
    }

}
