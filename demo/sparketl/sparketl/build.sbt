scalaVersion := "2.12.10"
version := "0.1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.apache.logging.log4j" % "log4j-api" % "2.17.0",
  "org.apache.logging.log4j" % "log4j-core" % "2.17.0",
  "org.apache.spark" % "spark-core_2.12" % "3.3.0",
  "com.google.code.gson" % "gson" % "2.8.9",
  "com.typesafe.akka" %% "akka-actor" % "2.6.18"
)

// Assembly settings
assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*)                   => MergeStrategy.discard
  case PathList("git.properties")                      => MergeStrategy.first
  case x if x.matches(".*FastHashMap.*\\.class")       => MergeStrategy.first
  case x if x.endsWith("package-info.class")           => MergeStrategy.first
  case x if x.endsWith("Inject.class")                 => MergeStrategy.first
  case x if x.endsWith("UnusedStubClass.class")        => MergeStrategy.first
  case x if x.endsWith("Named.class")                  => MergeStrategy.first
  case x if x.endsWith("Provider.class")               => MergeStrategy.first
  case x if x.endsWith("Scope.class")                  => MergeStrategy.first
  case x if x.endsWith("Qualifier.class")              => MergeStrategy.first
  case x if x.endsWith("ArrayStack.class")             => MergeStrategy.first
  case x if x.endsWith("BufferUnderflowException.class") => MergeStrategy.first
  case x if x.endsWith("Invocation.class")             => MergeStrategy.first
  case x if x.endsWith("Interceptor.class")            => MergeStrategy.first
  case x if x.endsWith("Singleton.class")              => MergeStrategy.first
  case x if x.endsWith("Advice.class")                 => MergeStrategy.first
  case x if x.endsWith("AspectException.class")        => MergeStrategy.first
  case x if x.endsWith("Joinpoint.class")              => MergeStrategy.first
  case x if x.endsWith("Buffer.class")                 => MergeStrategy.first
  case x if x.endsWith("NoOpLog.class")                => MergeStrategy.first
  case x if x.endsWith("AuthenticationType.class")     => MergeStrategy.first
  case x if x.endsWith("module-info.class")            => MergeStrategy.first
  case x if x.endsWith("Log.class")                    => MergeStrategy.first
  case x if x.endsWith("LogConfigurationException.class") => MergeStrategy.first
  case x if x.endsWith("LogFactory.class")             => MergeStrategy.first
  case x if x.endsWith("SimpleLog.class")              => MergeStrategy.first
  case x if x.endsWith("SimpleLog$1.class")            => MergeStrategy.first

  case x =>
    val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
    oldStrategy(x)
}

lazy val utils = (project in file("."))
  .settings(
    assembly / assemblyJarName := "sparketl.jar"
  )
