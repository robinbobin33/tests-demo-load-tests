package com.robinbobin.tests.utility

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import scala.util.Random

object RandomData {

  def getRandomString(length: Int): String = Random.alphanumeric.take(length).mkString

  def getRandomPhone: Long = 70000000000L + Random.nextInt(1000000000)

  def getRandomDate: String = LocalDate.now()
    .minusDays(Random.nextInt(365))
    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))

}
