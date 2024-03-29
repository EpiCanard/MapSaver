CREATE TABLE IF NOT EXISTS `{prefix}_data_maps`
(
    `uuid`          CHAR(36)            PRIMARY KEY NOT NULL,
    `bytes`         VARBINARY(16384)                NOT NULL,
    `created_at`    DATETIME                        NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    `updated_at`    DATETIME                        NOT NULL    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `{prefix}_server_maps`
(
    `locked_id`     INT             NOT NULL,
    `original_id`   INT,
    `world`         VARCHAR(50),
    `x`             INT,
    `z`             INT,
    `scale`         VARCHAR(8)      NOT NULL,
    `server`        VARCHAR(256)    NOT NULL,
    `map_uuid`      CHAR(36)        NOT NULL,
    UNIQUE(`locked_id`, `server`),
    FOREIGN KEY (`map_uuid`) REFERENCES `{prefix}_data_maps` (`uuid`) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `{prefix}_player_maps`
(
    `player_uuid`   CHAR(36)        NOT NULL,
    `map_uuid`      CHAR(36)        NOT NULL,
    `owner`         BOOLEAN         NOT NULL,
    `visibility`    VARCHAR(20)     NOT NULL,
    `name`          VARCHAR(256)    NOT NULL,
    UNIQUE(`player_uuid`, `name`),
    FOREIGN KEY (map_uuid) REFERENCES {prefix}_data_maps(uuid) ON DELETE CASCADE
) ENGINE=InnoDB;
