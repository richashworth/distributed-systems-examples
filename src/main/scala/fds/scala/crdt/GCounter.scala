package fds.scala.crdt

import scala.collection.mutable

import cats.implicits._
import cats.Semigroup

case class GCounter(
    nodeVals: mutable.Map[String, Long] = mutable.Map.empty[String, Long]) {

  implicit val mutableMapSG: Semigroup[mutable.Map[String, Long]] =
    new Semigroup[mutable.Map[String, Long]] {

      override def combine(
          x: mutable.Map[String, Long],
          y: mutable.Map[String, Long]): mutable.Map[String, Long] = {
        y.map((a) => {
          if (x.contains(a._1)) x.put(a._1, math.max(a._2, x.get(a._1).get))
          else x.put(a._1, a._2)
        })
        x
      }
    }

  def inc(nodeName: String): Unit = inc(nodeName, 1)

  def inc(nodeName: String, i: Long): Unit = {
    // set up new node
    nodeVals.put(nodeName, i);
  }

  def value: Long = {
    nodeVals.values.sum
  }

  def merge(that: GCounter) =
    new GCounter(nodeVals.combine(that.nodeVals))

  def toJson = ""

  private def valOrZero(l: Long): Long = {
    if (l != null) return l.longValue
    0
  }
}

object GCounter extends App {
  val a = new GCounter
  a.inc("a", 3)
  val b = new GCounter
  b.inc("b", 7)
  val c = new GCounter
  c.inc("c", 4)

  a.inc("d", 9)

  //TODO replace this with scalacheck property tests
  println(a.nodeVals)
  println(b.nodeVals)
  println((a.merge(b).merge(c)).nodeVals)
  println((b.merge(c).merge(c).merge(a)).nodeVals)
  println((a.merge(a)).nodeVals)
}
