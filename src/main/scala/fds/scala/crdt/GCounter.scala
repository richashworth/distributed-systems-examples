package fds.scala.crdt

import java.util

class GCounter {
  private val nodeVals = new util.HashMap[String, Long]

  def inc(nodeName: String): Unit = inc(nodeName, 1)

  def inc(nodeName: String, i: Long): Unit = {
    // set up new node
    nodeVals.put(nodeName, 0L);
  }

  def value: Long = { // TODO
    0L
  }

  def merge(that: GCounter) = new GCounter

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
}
