package fr.epicanard.mapsaver.command;

import fr.epicanard.mapsaver.MapSaverPlugin;
import fr.epicanard.mapsaver.map.ServerMap;
import fr.epicanard.mapsaver.permission.Permissions;
import fr.epicanard.mapsaver.utils.Either;
import fr.epicanard.mapsaver.utils.Messenger;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ImportCommand extends PlayerOnlyCommand {

    public ImportCommand(MapSaverPlugin plugin) {
        super(plugin, Permissions.IMPORT_MAP, plugin.getLanguage().Help.Import);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1 || args[0].isEmpty()) {
            Messenger.sendMessage(sender, this.plugin.getLanguage().ErrorMessages.MissingMapName);
            return true;
        }

        final Player player = (Player) sender;

        final UUID playerUuid = (args.length >= 2) ?
            this.plugin.getServer().getOfflinePlayer(args[1]).getUniqueId() :
            player.getUniqueId();

        final Boolean canGetMap = playerUuid.equals(player.getUniqueId()) || Permissions.ADMIN_IMPORT_MAP.isSetOn(sender);
        this.plugin.getService().getPlayerMap(args[0], playerUuid, canGetMap).match(
            left -> Messenger.sendMessage(sender, "&c" + left),
            right -> player.getInventory().addItem(right)
        );

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
