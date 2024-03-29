package fr.epicanard.mapsaver.commands

import cats.data.EitherT
import fr.epicanard.mapsaver.Permission
import fr.epicanard.mapsaver.commands.SaveCommand.buildMapToSave
import fr.epicanard.mapsaver.database.MapRepository
import fr.epicanard.mapsaver.errors.Error
import fr.epicanard.mapsaver.errors.MapSaverError.{MissingMapName, WrongVisibility}
import fr.epicanard.mapsaver.map.MapExtractor
import fr.epicanard.mapsaver.message.Message._
import fr.epicanard.mapsaver.message.{Message, Messenger}
import fr.epicanard.mapsaver.models.Complete
import fr.epicanard.mapsaver.models.map.status.MapCreationStatus
import fr.epicanard.mapsaver.models.map.{MapToSave, Visibility}
import fr.epicanard.mapsaver.resources.language.Help

import scala.concurrent.{ExecutionContext, Future}

case class SaveCommand(mapRepository: MapRepository)(implicit ec: ExecutionContext)
    extends BaseCommand(Some(Permission.SaveMap)) {
  def helpMessage(help: Help): String = help.save

  def onCommand(messenger: Messenger, commandContext: CommandContext): Future[Either[Error, Message]] =
    (for {
      mapToSave <- EitherT.fromEither[Future](buildMapToSave(commandContext))
      result    <- EitherT(mapRepository.saveMap(mapToSave))
      statusMsg = MapCreationStatus.getMessage(result, messenger.language.infoMessages)
    } yield msg"$statusMsg").value

  def onTabComplete(commandContext: CommandContext): Future[Either[Error, Complete]] = commandContext.tabArgs match {
    case _ :: vis :: Nil => Complete.Visibility(vis).fsuccess
    case _               => Complete.Empty.fsuccess
  }
}

object SaveCommand {
  private def buildMapToSave(commandContext: CommandContext): Either[Error, MapToSave] =
    for {
      player     <- CommandContext.getPlayer(commandContext)
      mapName    <- commandContext.args.headOption.toRight(MissingMapName)
      visibility <- parseVisibility(commandContext.args.tail, commandContext.config.options.defaultVisibility)
      mapItem    <- MapExtractor.extractFromPlayer(player)
      mapToSave = MapToSave(
        item = mapItem,
        name = mapName,
        server = commandContext.server,
        owner = player.getUniqueId,
        visibility = visibility
      )
    } yield mapToSave

  private def parseVisibility(args: List[String], defaultVisibility: Visibility): Either[Error, Visibility] =
    args match {
      case head :: _ => Visibility.withNameInsensitiveOption(head).toRight(WrongVisibility(head))
      case Nil       => Right(defaultVisibility)
    }
}
