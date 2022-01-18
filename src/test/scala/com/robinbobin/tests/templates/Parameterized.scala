package com.robinbobin.tests.templates

import io.gatling.core.Predef.{constantUsersPerSec, rampUsersPerSec,_}
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.{PopulationBuilder, ScenarioBuilder}

import scala.concurrent.duration._

trait Parameterized {

  val url = getParamOrDefault("url","http://localhost:8081")
  val rps = getParamOrDefault("rps","100").toInt
  val time = getParamOrDefault("time", "60").toInt
  val rampTime = getParamOrDefault("rampTime","30").toLong
  val rampUp = getParamOrDefault("ramp","true").toBoolean

  private def getValidRps(perc: Double): Double = if (perc <= 0) 1 else perc

  private def getSetupList(scenarios: List[(ScenarioBuilder, Double)]): List[PopulationBuilder] = {
    scenarios
      .map({ case (scn, rps) => (scn, getValidRps(rps)) })
      .map({ case (scn, rps) =>
        if (rampUp)
          scn.inject(rampUsersPerSec(0) to rps during (time seconds))
        else
          scn.inject(rampUsersPerSec(0) to rps during (rampTime seconds),
            constantUsersPerSec(rps) during (time seconds))
      })
  }

  def parameterizedSetup(simulation: Simulation, scenarios: (ScenarioBuilder, Double)*): simulation.SetUp = {
    simulation.setUp(getSetupList(scenarios.toList))
  }

  implicit class RpsAndScenario2RpsAndScenarioBuilder(scenario: ScenarioBuilder) {
    def injection(percentage: Double): (ScenarioBuilder, Double) = (scenario, percentage)
  }

  implicit class RpsPercentage2Rps(percent: Double) {
    def percent(rps: Int): Double = rps.toDouble * (percent / 100)
  }

  def getParamOrDefault(param: String, default: String): String = {
    if (sys.props.get(param).getOrElse("").isEmpty) default
    else sys.props.get(param).get
  }


}