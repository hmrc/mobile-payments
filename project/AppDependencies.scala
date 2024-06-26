import sbt._

object AppDependencies {

  private val bootstrapPlay28Version = "7.19.0"
  private val playHmrcApiVersion     = "7.2.0-play-28"
  private val flexmarkAllVersion     = "0.36.8"
  private val refinedVersion         = "0.9.26"
  private val domainVersion          = "8.1.0-play-28"
  private val openBankingVersion     = "0.247.0"

  private val pegdownVersion       = "1.6.0"
  private val wireMockVersion      = "2.20.0"
  private val scalaMockVersion     = "5.1.0"

  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-28" % bootstrapPlay28Version,
    "uk.gov.hmrc" %% "play-hmrc-api"             % playHmrcApiVersion,
    "eu.timepit"  %% "refined"                   % refinedVersion,
    "uk.gov.hmrc" %% "domain"                    % domainVersion,
    "uk.gov.hmrc" %% "open-banking-cor"          % openBankingVersion
  )

  trait TestDependencies {
    lazy val scope: String        = "test"
    lazy val test:  Seq[ModuleID] = ???
  }

  object Test {

    def apply(): Seq[ModuleID] =
      new TestDependencies {

        override lazy val test: Seq[ModuleID] = testCommon(scope) ++ Seq(
            "org.scalamock" %% "scalamock" % scalaMockVersion % scope
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
    "uk.gov.hmrc"            %% "bootstrap-test-play-28" % bootstrapPlay28Version % scope,
    "org.pegdown"            % "pegdown"                 % pegdownVersion         % scope,
    "com.vladsch.flexmark"   % "flexmark-all"            % flexmarkAllVersion     % scope
  )

  def apply(): Seq[ModuleID] = compile ++ Test() ++ IntegrationTest()
}
