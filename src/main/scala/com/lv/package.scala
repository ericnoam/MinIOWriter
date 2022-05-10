package com

import com.lv.models.Transaction
import pureconfig.ConfigReader.Result

import scala.util.Random
import pureconfig._
import pureconfig.generic.auto._

package object lv {

  case class Conf(bucketName: String, accessKey: String, secretKey: String, endpoint: String)
  val confR: Result[Conf] = ConfigSource.default.load[Conf]

  val tTyps: Array[String] = Array("B", "D", "E", "J", "O", "P", "S", "W")

  def timeZ(n: Int): String = {
    val p = Random.nextInt(n)
    if (p < 10) s"0$p" else p.toString
  }

  def plusZ(n: Int): String = {
    val p = Random.nextInt(n) + 1
    if (p < 10) s"0$p"
    else p.toString
  }

  val totalAmnt: () => Double = () => "%.2f".format(1000 * Random.nextDouble()).toDouble

  def createTransaction(): Transaction = {
    val pCode = "0" + (1 + Random.nextInt(3))
    val tTyp = Random.shuffle(tTyps.toList).head
    val gameId = s"gametitl-0000-0000-0000-${Random.alphanumeric.take(12).mkString}"
    val gameSes = s"gamesess-0000-0000-${Random.alphanumeric.take(4).mkString}-${Random.alphanumeric.take(12).mkString}"
    val trxDate = s"2022-04-${plusZ(30)}T${plusZ(23)}:${timeZ(60)}:${timeZ(60)}.00Z"
    val amt = "%.2f".format(100 * Random.nextDouble()).toDouble
    Transaction(pCode, tTyp, gameId, gameSes, trxDate, amt)
  }
}
