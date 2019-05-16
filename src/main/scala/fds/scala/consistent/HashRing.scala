package fds.scala.consistent

import scala.annotation.tailrec
import scala.collection.mutable

/**
  * Stub for a Consistent hash ring
  */
class HashRing(slots: Int) {

  // TODO create data structure for the hash ring
  val ring = mutable.TreeMap[Int, RingNode]()

  def addNode(node: RingNode): RingNode = {
    // TODO add a ring node to this ring
    val index = getHash(node.name)
    ring.put(index, node)
    node
  }

  def removeNode(node: RingNode): RingNode = {
    ring.remove(getHash(node.name))
    node
  }

  def put(key: String, value: Any): Unit = {
    // TODO find the appropriate node and add the value under the key
    val index = getHash(key)
    val node  = walkRingToFindNextNode(index)
//    println(s"adding $key to node ${node.name}")
    node.put(key, value)
  }

  private def getHash(key: String) = {
    (key.hashCode() % slots).abs.intValue()
  }

  def get(key: String): Any = {
    // TODO find the appropriate node and get the value given the key
    val index = getHash(key)
    val node  = walkRingToFindNextNode(index)
    node.get(key)
  }

  def ringSize: Int = {
    // TODO Total of all nodes' sizes
    val nodes = ring.values.toList
    nodes.foldLeft(0)((x, y) => x + y.size)
  }

  @tailrec
  final def walkRingToFindNextNode(startingLocation: Int): RingNode = {
    // TODO walk round the ring until you hit a node
    if (ring.contains(startingLocation)) ring(startingLocation)
    else if (startingLocation > slots) walkRingToFindNextNode(0)
    else walkRingToFindNextNode(startingLocation + 1)
  }
}

object HashRing {
  def apply(slots: Int): HashRing = new HashRing(slots)
}

object HashRingApp extends App {
  val hr = new HashRing(200)
  hr.addNode(RingNode("x Node AA"))
  hr.addNode(RingNode("y Node BB"))
  hr.addNode(RingNode("z Node CC"))
  println(hr.ring.keys.toList)

  for (_ <- 1 to 160000) {
    val key = scala.util.Random.nextInt().toString()
    hr.put(key, "")
  }

  println(s"SIZE: ${hr.ringSize}")
}
