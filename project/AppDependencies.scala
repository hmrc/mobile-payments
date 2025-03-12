import sbt._

object AppDependencies {

  private val bootstrapPlayVersion = "9.5.0"
  private val playHmrcApiVersion   = "8.0.0"
  private val refinedVersion       = "0.11.2"
  private val domainVersion        = "10.0.0"
  private val openBankingVersion   = "0.303.0"

  private val scalaMockVersion = "5.2.0"

  val compile = Seq(
    "uk.gov.hmrc" %% "play-hmrc-api-play-29" % playHmrcApiVersion,
    "eu.timepit"  %% "refined"               % refinedVersion,
    "uk.gov.hmrc" %% "domain-play-29"        % domainVersion,
    "uk.gov.hmrc" %% "open-banking-cor"      % openBankingVersion
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

        override lazy val test: Seq[ModuleID] = testCommon(scope)
      }.test
  }

  private def testCommon(scope: String) = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-29" % bootstrapPlayVersion % scope
  )

  def apply(): Seq[ModuleID] = compile ++ Test() ++ IntegrationTest()
}
