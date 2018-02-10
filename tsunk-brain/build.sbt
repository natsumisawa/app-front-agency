name := """tsunk-brain"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  ws,
  filters,
  "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.1" % Test,
  "mysql" % "mysql-connector-java" % "5.1.36",
  "com.typesafe.play" %% "play-slick" % "2.0.2",
  "com.typesafe.slick" % "slick-codegen_2.11" % "3.1.1",
  "net.ruippeixotog" %% "scala-scraper" % "2.1.0"
)

// code generation task
slick <<= slickCodeGenTask
lazy val slick = TaskKey[Seq[File]]("gen-tables")
lazy val slickCodeGenTask = (sourceManaged, dependencyClasspath in Compile, runner in Compile, streams) map { (dir, cp, r, s) =>
    val outputDir = "./app"
    val username = "root"
    val password = ""
    val url = "jdbc:mysql://localhost:3306/tsunk-brain"
    val jdbcDriver = "com.mysql.jdbc.Driver"
    val slickDriver = "slick.driver.MySQLDriver"
    val pkg = "model"
    toError(r.run("slick.codegen.SourceCodeGenerator", cp.files, Array(slickDriver, jdbcDriver, url, outputDir, pkg, username, password), s.log))
    val fname = outputDir + "/models/Tables.scala"
    Seq(file(fname))
}
