import sbt._

object Dependencies {
  private val scalaPluginLoaderVersion = "0.18.6-SNAPSHOT"
  private val spigotVersion            = "1.20-R0.1-SNAPSHOT"
  private val circeVersion             = "0.14.0"
  private val circeYamlVersion         = "0.15.0-RC1"
  private val enumeratumVersion        = "1.7.0"
  private val slickVersion             = "3.3.3"
  private val slickCatsVersion         = "0.10.4"
  private val sl4jVersion              = "1.7.32"
  private val catsVersion              = "2.6.1"

  val resolvers = Seq(
    Resolver.mavenCentral,
    "jannyboy11-minecraft-repo" at "https://repo.repsy.io/mvn/jannyboy11/minecraft",
    "spigot-repo" at "https://hub.spigotmc.org/nexus/content/repositories/snapshots/"
  )

  val scalaPluginLoader = "com.janboerman.scalaloader" % "ScalaLoader" % scalaPluginLoaderVersion

  val spigot = "org.spigotmc" % "spigot-api" % spigotVersion

  val enumeratum = Seq(
    "com.beachape" %% "enumeratum",
    "com.beachape" %% "enumeratum-circe",
    "com.beachape" %% "enumeratum-slick"
  ).map(_ % enumeratumVersion)

  val circeYaml = "io.circe" %% "circe-yaml" % circeYamlVersion
  val circe = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser",
    "io.circe" %% "circe-generic-extras"
  ).map(_ % circeVersion) :+ circeYaml

  val slick = Seq(
    "com.typesafe.slick" %% "slick"          % slickVersion,
    "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
    "org.slf4j"           % "slf4j-nop"      % sl4jVersion,
    "com.rms.miu"        %% "slick-cats"     % slickCatsVersion
  )

  val cats = Seq(
    "org.typelevel" %% "cats-core" % catsVersion
  )

  val libraries = enumeratum ++ circe ++ slick ++ cats
}
