name          := "MapSaver"
organization  := "fr.epicanard"
scalaVersion  := "2.13.6"
useCoursier   := false
scalacOptions ++= Seq(
  "-deprecation",
  "-Xfatal-warnings",
  "-language:higherKinds",
  "-Ywarn-unused",
  "-Ymacro-annotations"
)
artifactName := { (_, _, _) => s"${name.value}-${version.value}.jar" }
updateOptions := updateOptions.value.withLatestSnapshots(false)

resolvers ++= Dependencies.resolvers

libraryDependencies += Dependencies.scalaPluginLoader
libraryDependencies += Dependencies.spigot
libraryDependencies ++= Dependencies.libraries

enablePlugins(BuildInfoPlugin)

// Auto complete plugin.yml with dependencies
unmanagedResources / excludeFilter := "plugin.yml"

Compile / resourceGenerators += Def.task {
  val content = IO.read((Compile / resourceDirectory).value / "plugin.yml")
  val out = (Compile / resourceManaged).value / "plugin.yml"
  IO.write(out, formatDependencies(content, scalaVersion.value))
  Seq(out)
}.taskValue

def formatDependencies(content: String, scalaVersion: String): String = {
  val dependencies = Dependencies.libraries.map { m =>
    val scalaBinary = m.crossVersion match {
      case _: Binary => "_" + CrossVersion.binaryScalaVersion(scalaVersion)
      case _ => ""
    }
    s"  - ${m.organization}:${m.name}$scalaBinary:${m.revision}"
  }.mkString("\n")
  content.replace("${dependencies}", s"\n$dependencies")
}
