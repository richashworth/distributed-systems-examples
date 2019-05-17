package fds.scala.crdt

import io.circe.syntax
import io.circe.generic.auto._

case class PNCounter(upvotes: GCounter, downVotes: GCounter) {

  def inc(nodeName: String): Unit = upvotes.inc(nodeName, 1)

  def inc(nodeName: String, i: Long): Unit = {
    // set up new node
    upvotes.inc(nodeName, i)
  }

  def dec(nodeName: String): Unit = downVotes.inc(nodeName, 1)

  def dec(nodeName: String, i: Long): Unit = {
    // set up new node
    downVotes.inc(nodeName, i)
  }

  def value: Long = upvotes.value - downVotes.value

  def merge(that: PNCounter) =
    PNCounter(upvotes.merge(that.upvotes), downVotes.merge(that.downVotes))

  def toJson:String = upvotes.toJson.spaces2 + downVotes.toJson.spaces2
}

object PNCounterApp extends App {
  val a = new GCounter
  a.inc("a", 3)
  val b = new GCounter
  b.inc("b", 7)
  val c = new GCounter
  c.inc("c", 4)
  val d = new GCounter
  d.inc("c", 5)

  val p1 = PNCounter(a,b)
  val p2 = PNCounter(c,b)
  println((p1.merge(p2)).toJson)
}
