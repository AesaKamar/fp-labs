lazy val root = (project in file("."))
  .settings(
    organization := "com.github.aesakamar",
    name := "fp-labs",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "2.13.1",
    scalacOptions ++= Seq(
      "-deprecation", // Emit warning and location for usages of deprecated APIs.
      "-explaintypes", // Explain type errors in more detail.
      "-feature", // Emit warning and location for usages of features that should be imported explicitly.
      "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
      "-language:experimental.macros", // Allow macro definition (besides implementation and application)
      "-language:higherKinds", // Allow higher-kinded types
      "-language:implicitConversions", // Allow definition of implicit functions called views
      "-language:postfixOps", // Enable postfix operations
      "-unchecked", // Enable additional warnings where generated code depends on assumptions.
      "-Xcheckinit", // Wrap field accessors to throw an exception on uninitialized access.
      "-Xfatal-warnings", // Fail the compilation if there are any warnings.
      "-Xlint:adapted-args", // Warn if an argument list is modified to match the receiver.
      "-Xlint:constant", // Evaluation of a constant arithmetic expression results in an error.xxx
      "-Xlint:delayedinit-select", // Selecting member of DelayedInit.
      "-Xlint:doc-detached", // A Scaladoc comment appears to be detached from its element.
//      "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.x
      "-Xlint:infer-any", // Warn when a type argument is inferred to be `Any`.
      "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
      "-Xlint:nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
      "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
      "-Xlint:option-implicit", // Option.apply used implicit view.
      "-Xlint:package-object-classes", // Class or object defined in package object.
      "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
      "-Xlint:private-shadow", // A private field (or class parameter) shadows a superclass field.
      "-Xlint:stars-align", // Pattern sequence wildcard must align with sequence component.
      "-Xlint:type-parameter-shadow", // A local type parameter shadows a type already in scope.
      "-Ywarn-extra-implicit", // Warn when more than one implicit parameter section is defined.
      "-Ywarn-numeric-widen", // Warn when numerics are widened.
      // "-Ywarn-unused:implicits", // Warn if an implicit parameter is unused.
      "-Ywarn-value-discard", // Warn when non-Unit expression results are unused.
      "-Ybackend-parallelism",
      "8", // Enable paralellisation — change to desired number!
      "-Ycache-plugin-class-loader:last-modified", // Enables caching of classloaders for compiler plugins
      "-Ycache-macro-class-loader:last-modified" // and macro definitions. This can lead to performance improvements.
    ),
    libraryDependencies ++= Seq(
      "com.lihaoyi"       %% "pprint"                   % "0.5.5",
      "org.typelevel"     %% "cats-core"                % "2.0.0",
      "org.typelevel"     %% "cats-effect"              % "2.0.0",
      "io.higherkindness" %% "droste-core"              % "0.8.0",
      "org.scalatestplus" %% "scalatestplus-scalacheck" % "3.1.0.0-RC2",
      "org.scalatest"     %% "scalatest"                % "3.2.0-M1" % Test,
      "org.scalacheck"    %% "scalacheck"               % "1.14.1" % Test,
      "org.typelevel"     %% "simulacrum"               % "1.0.0"),
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

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full)

val mimaSettings = MimaPlugin.mimaDefaultSettings ++ Seq(mimaPreviousArtifacts := {
  val previousVersions: Set[String] = Set.empty // e.g. Set("0.1.0", "0.1.1")
  previousVersions.map { v =>
    organization.value % s"${name.value}_${scalaBinaryVersion.value}" % v
  }
}, test in Test := {
  mimaReportBinaryIssues.value
  (test in Test).value
})
