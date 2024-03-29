package fr.epicanard.mapsaver.map

import fr.epicanard.mapsaver.errors.Error
import fr.epicanard.mapsaver.errors.MapSaverError.MapInHandNeeded
import fr.epicanard.mapsaver.errors.TechnicalError.{InvalidMapMeta, InvalidMapView}
import fr.epicanard.mapsaver.map.BukkitMapBuilder.{getColorsMap, MapViewBuilder}
import fr.epicanard.mapsaver.models.map.{MapItem, McMapInfo}
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.{ItemMeta, MapMeta}
import org.bukkit.map.MapView

object MapExtractor {

  def extractMapView(player: Player): Either[Error, MapView] = {
    val stack = player.getInventory.getItemInMainHand
    for {
      _       <- Either.cond(stack.getType == Material.FILLED_MAP, (), MapInHandNeeded)
      mapMeta <- extractMapMeta(stack.getItemMeta).toRight[Error](InvalidMapMeta)
      mapView <- Option(mapMeta.getMapView).toRight[Error](InvalidMapView)
    } yield mapView
  }

  def extractFromPlayer(player: Player): Either[Error, MapItem] =
    for {
      mapView     <- extractMapView(player)
      mapRenderer <- MapViewBuilder.getRenderer(mapView)
      colorsMap   <- getColorsMap(mapRenderer)
      byteMap = new Array[Byte](16384)
      _       = Array.copy(colorsMap.bytes, 0, byteMap, 0, 16384)
    } yield MapItem(
      id = mapView.getId,
      bytes = byteMap,
      mapInfo = McMapInfo(
        scale = mapView.getScale().name(),
        x = mapView.getCenterX(),
        z = mapView.getCenterZ(),
        world = Option(mapView.getWorld())
          .orElse(Option(player.getLocation().getWorld()))
          .map(_.getName())
          .getOrElse("")
      )
    )

  private def extractMapMeta(itemMeta: ItemMeta): Option[MapMeta] = itemMeta match {
    case mapMeta: MapMeta => Some(mapMeta)
    case _                => None
  }
}
