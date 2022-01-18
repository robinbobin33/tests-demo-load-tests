package com.robinbobin.tests.testsdemo


import com.robinbobin.tests.templates.Parameterized
import com.robinbobin.tests.utility.RandomData
import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef._

class TestsDemo extends Simulation with Parameterized {

  val httpConf = http
    .baseUrl(url)
    .disableWarmUp

  val scnAddPerson = scenario("Add person")
    .feed(TestData.personsFeeder)
    .exec(
      http("add person")
        .post("/v1/person")
        .header("content-type", "application/json")
        .header("authorization", TestData.token)
        .body(StringBody(TestData.getPersonBodyPayload(_)))
        .check(status.is(201))
    )

  val scnGetPerson = scenario("Get person")
    .feed(TestData.phonesFeeder.random)
    .exec(
      http("get person")
        .get(session => { session("phone").validate[String].map(phone => s"/v1/person/$phone") })
        .header("content-type", "application/json")
        .header("authorization", TestData.token)
        .check(status.is(200))
    )

  parameterizedSetup(this,
    scnGetPerson.injection(75 percent rps),
    scnAddPerson.injection(25 percent rps)
  ).protocols(httpConf)
}

object TestData {

  case class Person(fio: String, birthDate: String, phone: Long)

  val token = "296630060b6ab7837ce25eb83095bc52"
  val personsFeeder = Iterator
    .continually {
      Map("person" -> Person(getFio, RandomData.getRandomDate, RandomData.getRandomPhone))
    }
  val phonesFeeder = ssv("tests-demo/phones.txt")

  def getPersonBodyPayload(session: Session): String = {
    val person = session("person").as[Person]
    s"""{
       |	"phone": ${person.phone},
       |	"fio": "${person.fio}",
       |	"birthDate": "${person.birthDate}"
       |}""".stripMargin
  }

  private def getFio =
    List.range(1, 3)
      .map {
        RandomData.getRandomString(5)
      }
      .mkString(" ")

}