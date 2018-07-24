import sbt._

name := "japid42"
version := "0.13"

scalaVersion := "2.11.8"

lazy val root = (project in file(".")).enablePlugins(PlayJava)
libraryDependencies ++= Seq(
    javaCore
    ,cache
    ,"org.apache.commons" % "commons-email" % "1.2"
    ,"org.apache.commons" % "commons-lang3" % "3.3.2"
    ,"org.eclipse.tycho" % "org.eclipse.jdt.core" % "3.10.0.v20140604-1726"
    ,"com.google.code.javaparser" % "javaparser" % "1.0.11"
    ,"javax.ws.rs" % "jsr311-api" % "1.1.1"
//		,"org.abstractj.kalium" % "kalium" % "0.8.0"
		,"org.keyczar" % "keyczar" % "0.66"
) 

resolvers += "keyczar" at "https://raw.githubusercontent.com/google/keyczar/master/java/maven/"


fork in run := true