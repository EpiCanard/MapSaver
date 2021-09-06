package fr.epicanard.mapsaver.database.schema

import fr.epicanard.mapsaver.models.map.PlayerMap
import fr.epicanard.mapsaver.models.map.Visibility
import fr.epicanard.mapsaver.database.schema.VisibilityMappers._
import slick.jdbc.MySQLProfile.api._

import java.util.UUID

class PlayerMaps(tag: Tag) extends Table[PlayerMap](tag, "player_maps") {
  def playerUuid = column[UUID]("player_uuid")
  def dataId     = column[Int]("data_id")
  def owner      = column[Boolean]("owner")
  def visibility = column[Visibility]("visibility", O.Length(20))
  def name       = column[String]("name", O.Length(256))

  def *        = (playerUuid, dataId, owner, visibility, name).mapTo[PlayerMap]
  def idx      = index("idx_playeruuid_name", (playerUuid, name), unique = true)
  def dataIdFk = foreignKey("player_data_id_fk", dataId, DataMaps)(_.id, onDelete = ForeignKeyAction.Cascade)
}

object PlayerMaps extends TableQuery(new PlayerMaps(_)) {}