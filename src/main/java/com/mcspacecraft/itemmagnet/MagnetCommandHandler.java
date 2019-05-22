package com.mcspacecraft.itemmagnet;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MagnetCommandHandler implements CommandExecutor {
    private ItemMagnet plugin;

    public MagnetCommandHandler(ItemMagnet plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getMagnetConfig().getMessage("error-need-player"));
                return true;
            }

            Player player = (Player) sender;
            boolean isEnabled = !plugin.getPlayerSettings().isEnabled(player.getUniqueId());
            plugin.getPlayerSettings().set(player.getUniqueId(), isEnabled);

            if (isEnabled) {
                player.sendMessage(plugin.getMagnetConfig().getMessage("magnet-enabled"));
            } else {
                player.sendMessage(plugin.getMagnetConfig().getMessage("magnet-disabled"));
            }
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                subCmdReload(sender);
                break;
        }

        return true;
    }

    private void subCmdReload(CommandSender sender) {
        if (sender.hasPermission("itemmagnet.admin")) {
            plugin.getMagnetConfig().load();
            sender.sendMessage(plugin.getMagnetConfig().getMessage("configuration-loaded"));
        }
    }
}
