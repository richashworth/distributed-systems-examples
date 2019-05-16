package fds.scala.consistent

import scala.collection.mutable

/**
  * Stub for a Consistent hash ring
  */
class HashRing(slots: Int) {

  // TODO create data structure for the hash ring

  private val ring = mutable.Map.empty[String, RingNode]

  def addNode(node: RingNode): RingNode = {
    // TODO add a ring node to this ring
    val ringPosition = Hasher.sha1(node.name).mod(BigInt(slots))
    ring.put(ringPosition.toString(), node)
    node
  }

  def removeNode(node: RingNode): RingNode = {
    // TODO remove a ring node from this ring
    null
  }

  def put(key: String, value: Any): Unit = {
    // TODO find the appropriate node and add the value under the key
  }

  def get(key: String): Any = {
    // TODO find the appropriate node and get the value given the key
    null
  }

  def size: Int = {
    // TODO Total of all nodes' sizes
    0
  }

  def walkRingToFindNextNode(startingLocation: Int): RingNode = {
    // TODO walk round the ring until you hit a node
    null
  }
}

object HashRing {
  def apply(slots: Int): HashRing = new HashRing(slots)
}
