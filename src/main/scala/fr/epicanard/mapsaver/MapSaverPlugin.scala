package fr.epicanard.mapsaver

import buildinfo.BuildInfo
import cats.data.EitherT
import fr.epicanard.mapsaver.database.MapRepository
import fr.epicanard.mapsaver.resources.config.Config._
import fr.epicanard.mapsaver.errors.TechnicalError
import fr.epicanard.mapsaver.resources.ResourceLoader.extractAndLoadResource
import fr.epicanard.mapsaver.resources.language.Language
import xyz.janboerman.scalaloader.plugin.ScalaPlugin
import xyz.janboerman.scalaloader.plugin.ScalaPluginDescription
import xyz.janboerman.scalaloader.plugin.description.Scala
import xyz.janboerman.scalaloader.plugin.description.ScalaVersion

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success

@Scala(version = ScalaVersion.v2_13_6)
object MapSaverPlugin
    extends ScalaPlugin(
      new ScalaPluginDescription(BuildInfo.name, BuildInfo.version)
    ) {
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  override def onEnable(): Unit =
    initPlugin(this).onComplete {
      case Success(Left(error)) => TechnicalError.logError(error, this.getLogger)
      case Success(Right(_))    => this.getLogger.info("Loading success")
      case Failure(_)           => this.getLogger.warning("unexpected error")
    }

  def initPlugin(plugin: ScalaPlugin): Future[Either[TechnicalError, Unit]] =
    (for {
      config   <- EitherT.fromEither[Future](extractAndLoadResource(plugin, "config.yml"))
      language <- EitherT.fromEither[Future](extractAndLoadResource[Language](plugin, s"langs/${config.language}.yml"))
      messenger     = Messenger(config.prefix, language)
      logger        = plugin.getLogger
      database      = MapRepository.buildDatabase(config.storage)
      mapRepository = new MapRepository(logger, database)
      _ <- EitherT(mapRepository.initDatabase())
    } yield ()).value

}
