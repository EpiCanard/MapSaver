package fr.epicanard.mapsaver.database.queries

import fr.epicanard.mapsaver.database.SQLActionBuilderExt._
import fr.epicanard.mapsaver.database.mappers.MapByNameMapper._
import fr.epicanard.mapsaver.database.mappers.PlayerMapMapper._
import fr.epicanard.mapsaver.database.mappers.UUIDMapper._
import fr.epicanard.mapsaver.database.mappers.VisibilityMapper._
import fr.epicanard.mapsaver.database.profile.MySQLProfile.api._
import fr.epicanard.mapsaver.database.schema.PlayerMaps
import fr.epicanard.mapsaver.models.map.{MapByName, PlayerMap, Visibility}
import fr.epicanard.mapsaver.models.{Pageable, RestrictVisibility}
import org.bukkit.OfflinePlayer

import java.util.UUID

object PlayerMapQueries {

  def selectByDataId(dataId: Int): DBIO[Option[PlayerMap]] =
    PlayerMaps.filter(m => m.dataId === dataId).take(1).result.headOption

  def insert(playerMap: PlayerMap): DBIO[Int] = PlayerMaps += playerMap

  def delete(playerUUID: UUID, dataId: Int): DBIO[Int] =
    sqlu"""DELETE FROM player_maps WHERE `player_uuid` = $playerUUID AND `data_id` = $dataId"""

  def rename(playerUUID: UUID, dataId: Int, newName: String): DBIO[Int] =
    sqlu"""UPDATE player_maps SET `name` = $newName WHERE `player_uuid` = $playerUUID AND `data_id` = $dataId"""

  def countForPlayer(playerUUID: UUID, restrictVisibility: Option[Visibility]): DBIO[Int] =
    (sql"""
      SELECT count(*) FROM player_maps
      WHERE `player_uuid` = $playerUUID
      """
      +? restrictVisibility.map(vis => sql" AND `visibility` = $vis ")).as[Int].head

  def listForPlayer(
      pageable: Pageable,
      restrictVisibility: Option[Visibility]
  ): DBIO[Seq[PlayerMap]] = {
    val start = (pageable.page - 1) * pageable.pageSize
    (sql"""
      SELECT p.* FROM player_maps AS p
      LEFT JOIN data_maps ON p.data_id = data_maps.id
      WHERE `player_uuid` = ${pageable.player.getUniqueId}
      """
      +? restrictVisibility.map(vis => sql" AND `visibility` = $vis ")
      ++ sql"""
      ORDER BY data_maps.updated_at DESC
      LIMIT $start,${pageable.pageSize}
      """).as[PlayerMap]
  }

  def searchForPlayer(
      search: String,
      owner: OfflinePlayer,
      restrictVisibility: Option[Visibility]
  ): DBIO[Seq[String]] =
    (sql"""
      SELECT p.name FROM player_maps AS p
      LEFT JOIN data_maps ON p.data_id = data_maps.id
      WHERE `player_uuid` = ${owner.getUniqueId} AND `name` LIKE ${s"%$search%"}
      """
      +? restrictVisibility.map(vis => sql" AND `visibility` = $vis ")
      ++ sql"""
      ORDER BY data_maps.updated_at DESC
      """).as[String]

  def selectPlayerMap(
      owner: UUID,
      dataId: Int,
      restrictVisibility: Option[RestrictVisibility]
  ): DBIO[Option[PlayerMap]] =
    (sql"""SELECT * FROM player_maps WHERE data_id = $dataId"""
      +? restrictVisibility.map(QueryHelper.withRestrictVisibility(_, owner)))
      .as[PlayerMap]
      .headOption

  def selectPlayerMapWithName(
      owner: UUID,
      mapName: String,
      restrictVisibility: Option[Visibility]
  ): DBIO[Option[PlayerMap]] =
    (sql"""SELECT * FROM player_maps WHERE `player_uuid` = $owner AND name = $mapName"""
      +? restrictVisibility.map(vis => sql" AND `visibility` = $vis ")).as[PlayerMap].headOption

  def updateVisibility(
      owner: UUID,
      dataId: Int,
      visibility: Visibility
  ): DBIO[Int] =
    sqlu"""UPDATE player_maps SET `visibility` = $visibility WHERE `player_uuid` = $owner AND data_id = $dataId"""

  def selectMapByName(
      owner: UUID,
      mapName: String,
      serverName: String,
      restrictVisibility: Option[Visibility]
  ): DBIO[Option[MapByName]] =
    (sql"""
      SELECT
        player_maps.player_uuid,
        player_maps.data_id,
        player_maps.visibility,
        server_maps.locked_id,
        server_maps.server,
        server_maps.scale,
        server_maps.x,
        server_maps.z,
        server_maps.world
      FROM player_maps
      LEFT JOIN server_maps ON player_maps.data_id = server_maps.data_id AND server_maps.server = $serverName
      WHERE player_maps.name = $mapName AND player_uuid = $owner
    """ +? restrictVisibility.map(vis => sql" AND `visibility` = $vis ")).as[MapByName].headOption

  def updateLocked(
      owner: UUID,
      dataId: Int,
      locked: Boolean
  ): DBIO[Int] =
    sqlu"""UPDATE player_maps SET `locked` = $locked WHERE `player_uuid` = $owner AND data_id = $dataId"""
}
