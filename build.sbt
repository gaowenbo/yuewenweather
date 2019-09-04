import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

lazy val server = (project in file("server")).settings(commonSettings).settings(
  pipelineStages := Seq(digest, gzip),
  // triggers scalaJSPipeline when using compile or continuous compilation
  libraryDependencies ++= Seq(
    ws,
    guice
  )
  // Compile the project before generating Eclipse files, so that generated .scala or .class files for views and routes are present
).enablePlugins(PlayScala).
  dependsOn(sharedJvm)

lazy val copyDev = inputKey[Unit]("developCopy")
lazy val copyProd = inputKey[Unit]("prodCopy")

lazy val client = (project in file("client")).settings(commonSettings).settings(
  scalaJSUseMainModuleInitializer := true
).enablePlugins(ScalaJSPlugin).
  dependsOn(sharedJs).settings(copyDev := {
    IO.copyFile(file("client")/"target/scala-2.12/client-fastopt.js", file("program")/"all.js")
    IO.copyFile(file("client")/"target/scala-2.12/client-fastopt.js.map", file("program")/"client-fastopt.js.map")
  }).settings(copyProd := {
  IO.copyFile(file("client") /"target/scala-2.12/client-opt.js", file("program") /"all.js")
  IO.copyFile(file("client")/"target/scala-2.12/client-opt.js.map", file("program")/"client-opt.js.map")
})


lazy val shared = crossProject(JSPlatform, JVMPlatform)
  //  .crossType(CrossType.Pure)
  //  .in(file("shared"))
  .settings(commonSettings)
  .settings(
    name := "shared"
  )
  .settings(
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % "2.12.8",
      "org.scala-js" %% "scalajs-library" % "1.0.0-M7"
    )
  )
lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

lazy val commonSettings = Seq(
  scalaVersion := "2.12.8",
  organization := "com.wenbo"
)

// loads the server project at sbt startup
onLoad in Global := (onLoad in Global).value andThen {s: State => "project server" :: s}
