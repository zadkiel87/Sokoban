name := "Sokoban"

version := "1.0.0"

scalaVersion := "2.10.0"

libraryDependencies <+= scalaVersion { "org.scala-lang" % "scala-swing" % _ }
