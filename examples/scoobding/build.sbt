name := "scoobding"

version := "1.0"

scalaVersion := "2.9.2"

// replace this line with scoobiVersion := "0.7.0-cdh4" or another Scoobi version
scoobiVersion <<= scoobiVersion in GlobalScope 

scalacOptions ++= Seq("-Ydependent-method-types", "-deprecation")


libraryDependencies <++= (scoobiVersion) { version =>
  Seq("com.nicta" %% "scoobi" % version,
      "org.scala-lang" % "scala-swing" % "2.9.2",
      "jfree" % "jfreechart" % "1.0.13",
      "jfree" % "jcommon" % "1.0.16",
      "cc.co.scala-reactive" %% "reactive-core" % "0.3.0") 
}


resolvers ++= Seq("nicta's avro" at "http://nicta.github.com/scoobi/releases",
                  "sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
                  "cloudera" at "https://repository.cloudera.com/content/repositories/releases")
