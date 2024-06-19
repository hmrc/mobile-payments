import play.sbt.PlayImport.PlayKeys.playDefaultPort
import uk.gov.hmrc.DefaultBuildSettings.oneForkedJvmPerTest
val appName = "mobile-payments"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(
    Seq(
      play.sbt.PlayScala,
      SbtAutoBuildPlugin,
      SbtDistributablesPlugin,
      ScoverageSbtPlugin
    ): _*
  )
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(
    routesImport ++= Seq(
      "uk.gov.hmrc.mobilepayments.domain.types._",
      "uk.gov.hmrc.mobilepayments.domain.types.ModelTypes._"
    )
  )
  .settings(
    majorVersion := 0,
    scalaVersion := "2.13.8",
    playDefaultPort := 8262,
    libraryDependencies ++= AppDependencies(),
    update / evictionWarningOptions := EvictionWarningOptions.default.withWarnScalaVersionEviction(false),
    Compile / unmanagedResourceDirectories += baseDirectory.value / "resources",
    IntegrationTest / unmanagedSourceDirectories := (IntegrationTest / baseDirectory)(base =>
      Seq(base / "it", base / "test-common")
    ).value,
    Test / unmanagedSourceDirectories := (Test / baseDirectory)(base => Seq(base / "test", base / "test-common")).value,
    IntegrationTest / testGrouping := oneForkedJvmPerTest((IntegrationTest / definedTests).value),
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding",
      "UTF-8",
      "-language:higherKinds",
      "-language:postfixOps",
      "-feature",
      "-Ywarn-dead-code",
      "-Ywarn-value-discard",
      "-Ywarn-numeric-widen",
      "-Xlint"
    ),
    coverageMinimumStmtTotal := 93,
    coverageFailOnMinimum := true,
    coverageHighlighting := true,
    coverageExcludedPackages := "<empty>;com.kenshoo.play.metrics.*;.*definition.*;prod.*;testOnlyDoNotUseInAppConf.*;app.*;.*BuildInfo.*;.*Routes.*;.*javascript.*;.*Reverse.*"
  )
