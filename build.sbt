lazy val root = (project in file("."))
  .settings(
    organization := "com.github.aesakamar",
    name := "scala-demos",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "2.13.1",
    scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Yrangepos"),
    libraryDependencies ++= Seq(
      "ai.x"          %% "diff"        % "2.0.1",
      "com.lihaoyi"   %% "pprint"      % "0.5.4",
      "org.typelevel" %% "cats-core"   % "2.0.0",
      "org.typelevel" %% "cats-effect" % "2.0.0",
      "org.scalatest" %% "scalatest"   % "3.0.5" % Test),
    scalafmtOnCompile := false,
    // FYI: https://www.scala-sbt.org/1.0/docs/Using-Sonatype.html
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (version.value.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at s"${nexus}content/repositories/snapshots")
      else Some("releases" at s"${nexus}service/local/staging/deploy/maven2")
    },
    pomExtra := {
      <url>https://github.com/aesakamar/scala-demos</url>
      <licenses>
        <license>
          <name>Apache License 2.0</name>
          <url>https://raw.githubusercontent.com/aesakamar/scala-demos/master/LICENSE.txt</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:aesakamar/scala-demos.git</url>
        <connection>scm:git:git@github.com:aesakamar/scala-demos.git</connection>
      </scm>
      <developers>
        <developer>
          <id>aesakamar</id>
          <name>Aesa Kamar</name>
          <url>https://github.com/aesakamar</url>
        </developer>
      </developers>
    })
  .settings(mimaSettings)

val mimaSettings = MimaPlugin.mimaDefaultSettings ++ Seq(mimaPreviousArtifacts := {
  val previousVersions: Set[String] = Set.empty // e.g. Set("0.1.0", "0.1.1")
  previousVersions.map { v =>
    organization.value % s"${name.value}_${scalaBinaryVersion.value}" % v
  }
}, test in Test := {
  mimaReportBinaryIssues.value
  (test in Test).value
})
