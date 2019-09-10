

lazy val copyDev = inputKey[Unit]("developCopy")
lazy val copyProd = inputKey[Unit]("prodCopy")

lazy val server = (project in file("server")).settings(commonSettings).settings(
  scalaJSUseMainModuleInitializer := true,
  scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) }

  // Compile the project before generating Eclipse files, so that generated .scala or .class files for views and routes are present
).enablePlugins(ScalaJSPlugin).dependsOn(shared).settings(copyDev := {
  IO.copyFile(file("server")/"target/scala-2.12/server-fastopt.js", file("server")/"server.js")
}).settings(copyProd := {
  IO.copyFile(file("server") /"target/scala-2.12/server-opt.js", file("server") /"server.js")
})


lazy val client = (project in file("client")).settings(commonSettings).settings(
  scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) }
).enablePlugins(ScalaJSPlugin).
  dependsOn(shared).settings(copyDev := {
  IO.copyFile(file("client")/"target/scala-2.12/client-fastopt.js", file("program")/"all.js")
  IO.copyFile(file("client")/"target/scala-2.12/client-fastopt.js.map", file("program")/"client-fastopt.js.map")
}).settings(copyProd := {
  IO.copyFile(file("client") /"target/scala-2.12/client-opt.js", file("program") /"all.js")
  IO.copyFile(file("client")/"target/scala-2.12/client-opt.js.map", file("program")/"client-opt.js.map")
})

lazy val shared = (project in file("shared"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % "2.12.8",
      "org.scala-js" %% "scalajs-library" % "1.0.0-M8"
    )
  )

lazy val commonSettings = Seq(
  scalaVersion := "2.12.8",
  organization := "com.wenbo"
)

// loads the server project at sbt startup
onLoad in Global := (onLoad in Global).value andThen {s: State => "project server" :: s}
