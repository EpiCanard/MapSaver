package fr.epicanard.mapsaver.models.map

import enumeratum.EnumEntry.UpperSnakecase
import enumeratum.{Enum, EnumEntry}
import fr.epicanard.mapsaver.Permission
import fr.epicanard.mapsaver.circe.CirceEnumInsensitive
import fr.epicanard.mapsaver.commands.CommandContext
import org.bukkit.OfflinePlayer

sealed trait Visibility extends EnumEntry with UpperSnakecase

object Visibility extends Enum[Visibility] with CirceEnumInsensitive[Visibility] {
  case object Public  extends Visibility
  case object Private extends Visibility

  val values = findValues

  def getRestrictVisibility(
      commandContext: CommandContext,
      owner: OfflinePlayer,
      adminPermission: Permission
  ): Option[Visibility] =
    CommandContext
      .getPlayerOpt(commandContext)
      .filter(player => owner.getUniqueId != player.getUniqueId && !adminPermission.isSetOn(player))
      .map(_ => Visibility.Public)

  def startsWithNameInsensitive(name: String): List[String] =
    values
      .map(_.entryName.toLowerCase())
      .filter(_.startsWith(name.toLowerCase()))
      .toList
}
