import play.core.PlayVersion
import sbt._

object AppDependencies {

  private val bootstrapPlay28Version = "5.12.0"
  private val playHmrcApiVersion     = "6.4.0-play-28"
  private val flexmarkAllVersion     = "0.36.8"
  private val refinedVersion         = "0.9.26"
  private val domainVersion          = "6.2.0-play-28"

  private val pegdownVersion       = "1.6.0"
  private val wireMockVersion      = "2.20.0"
  private val scalaTestVersion     = "3.2.9"
  private val scalaTestPlusVersion = "5.1.0"
  private val scalaMockVersion     = "5.1.0"

  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-28" % bootstrapPlay28Version,
    "uk.gov.hmrc" %% "play-hmrc-api"             % playHmrcApiVersion,
    "eu.timepit"  %% "refined"                   % refinedVersion,
    "uk.gov.hmrc" %% "domain"                    % domainVersion
  )

  val test = Seq(
    Test,
    "com.vladsch.flexmark" % "flexmark-all" % flexmarkAllVersion % "test, it"
  )

  trait TestDependencies {
    lazy val scope: String        = "test"
    lazy val test:  Seq[ModuleID] = ???
  }

  object Test {

    def apply(): Seq[ModuleID] =
      new TestDependencies {

        override lazy val test: Seq[ModuleID] = testCommon(scope) ++ Seq(
            "uk.gov.hmrc"   %% "bootstrap-test-play-28" % bootstrapPlay28Version,
            "org.scalamock" %% "scalamock"              % scalaMockVersion % scope
          )
      }.test
  }

  object IntegrationTest {

    def apply(): Seq[ModuleID] =
      new TestDependencies {

        override lazy val scope: String = "it"

        override lazy val test: Seq[ModuleID] = testCommon(scope) ++ Seq(
            "com.github.tomakehurst" % "wiremock" % wireMockVersion % scope
          )
      }.test
  }

  private def testCommon(scope: String) = Seq(
    "org.pegdown"            % "pegdown"             % pegdownVersion       % scope,
    "com.typesafe.play"      %% "play-test"          % PlayVersion.current  % scope,
    "org.scalatest"          %% "scalatest"          % scalaTestVersion     % scope,
    "org.scalatestplus.play" %% "scalatestplus-play" % scalaTestPlusVersion % scope,
    "com.vladsch.flexmark"   % "flexmark-all"        % flexmarkAllVersion   % scope
  )

  def apply(): Seq[ModuleID] = compile ++ Test() ++ IntegrationTest()
}