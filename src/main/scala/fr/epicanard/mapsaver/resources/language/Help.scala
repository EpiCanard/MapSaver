package fr.epicanard.mapsaver.resources.language

import fr.epicanard.mapsaver.circe.CapitalizeConfiguration
import io.circe.generic.extras.ConfiguredJsonCodec

@ConfiguredJsonCodec
case class Help(
    usage: String,
    help: String,
    reload: String,
    version: String,
    save: String,
    update: String,
    `import`: String,
    list: String,
    info: String
)

object Help extends CapitalizeConfiguration