import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class MainSimulation extends Simulation {

  val baseUrl = sys.env.getOrElse("SERVICE_URL", "http://localhost:8080")

  val httpProtocol = http.baseUrl(baseUrl)
    .header("Content-Type", "application/json")
    .header("Accept-Encoding", "gzip")

  val scn = scenario("BasicScenario")
    .exec(
      http("Get weather for London")
        .post("/weather/getForecast")
        .body(StringBody(
          """{
            |    "city": "London"
            |}""".stripMargin)).asJson
        .check(jsonPath("$.result").is("42"))
    )

  setUp(
    scn.inject(
      rampUsers(10).during(30.seconds),             // Warmup
//      constantUsersPerSec(20).during(20.seconds),    // Main test
//      constantUsersPerSec(30).during(20.seconds),    // Main test
//      constantUsersPerSec(50).during(20.seconds),    // Main test
//      constantUsersPerSec(70).during(20.seconds),    // Main test
//      constantUsersPerSec(100).during(2.minutes),    // Main test
    ).protocols(httpProtocol)
  ).maxDuration(10.minutes)
}
