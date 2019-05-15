package fds.scala.queue

import com.zink.queue.{ReadChannel, ConnectionFactory, Connection}

/**
  *  Consumer Stub
  */
object Consumer extends App {

  val con: Connection = ConnectionFactory.connect("127.0.0.1")
  val rc: ReadChannel = con.subscribe("BBC7")

  def readMessages(count: Int, searchString: String): Int = {
    val msg = rc.read()
    if (msg == EndOfStreamMarker) count
    else if (msg.toString().contains(searchString))
      readMessages(count + 1,searchString)
    else readMessages(count, searchString)
  }

  val searchString = "DanielBaudSkiGuide"

  val result = readMessages(0, searchString)
  println(result)

}
