
name := "toranoana-scraping"

version := "1.0"

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.2",
  "nu.validator.htmlparser" % "htmlparser" % "1.4",
  "com.ibm.icu" % "icu4j" % "54.1.1",
  "com.github.nscala-time" %% "nscala-time" % "1.4.0"
)

assemblySettings
