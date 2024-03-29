package fr.epicanard.mapsaver

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

sealed abstract class Permission(permission: String) {
  def isSetOn(player: Player): Boolean = player != null && player.hasPermission(permission)

  def isSetOn(sender: CommandSender, defaultSender: Boolean): Boolean =
    sender match {
      case player: Player => isSetOn(player)
      case _              => defaultSender
    }

  def isSetOn(sender: CommandSender): Boolean = isSetOn(sender, defaultSender = true)
}

object Permission {
  case object SaveMap extends Permission("mapsaver.commands.save")

  case object UpdateMap extends Permission("mapsaver.commands.update")

  case object ImportMap extends Permission("mapsaver.commands.import")

  case object ListMap extends Permission("mapsaver.commands.list")

  case object InfoMap extends Permission("mapsaver.commands.info")

  case object VisibilityMap extends Permission("mapsaver.commands.visibility")

  case object LockMap extends Permission("mapsaver.commands.lock")

  case object UnlockMap extends Permission("mapsaver.commands.unlock")

  case object DeleteMap extends Permission("mapsaver.commands.delete")

  case object RenameMap extends Permission("mapsaver.commands.rename")

  case object AdminReload extends Permission("mapsaver.admin.commands.reload")

  case object AdminImportMap extends Permission("mapsaver.admin.commands.import")

  case object AdminListMap extends Permission("mapsaver.admin.commands.list")

  case object AdminInfoMap extends Permission("mapsaver.admin.commands.info")

  case object AdminVisibilityMap extends Permission("mapsaver.admin.commands.visibility")

  case object AdminLockMap extends Permission("mapsaver.admin.commands.lock")

  case object AdminUnlockMap extends Permission("mapsaver.admin.commands.unlock")

  case object AdminDeleteMap extends Permission("mapsaver.admin.commands.delete")

  case object AdminRenameMap extends Permission("mapsaver.admin.commands.rename")
}
