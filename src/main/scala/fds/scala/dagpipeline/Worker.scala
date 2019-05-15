package fds.scala.dagpipeline

import com.zink.queue.{Connection, ConnectionFactory, ReadChannel, WriteChannel}
import scala.annotation.tailrec

/**
  *  Consumer Stub - filter for messages and send on for further processing
  */
class Worker(readChannelName: String, writeChannelName: String) {

  val con: Connection = ConnectionFactory.connect("127.0.0.1")
  val rc: ReadChannel = con.subscribe(readChannelName)
  val wc: WriteChannel = con.publish(writeChannelName)

  def start(workerName: String, searchString: String): Unit = {
    spin(workerName, searchString)
  }

  @tailrec
  private def spin(workerName: String, searchString: String): Unit = {
//    Thread.sleep(100)
    val msg = rc.read()
//    println(s"Worker $workerName consumed ${msg.toString()}")
    if (msg.toString().contains(searchString)) {
      wc.write(msg.toString())
      spin(workerName, searchString)
    } else if (msg != EndOfStreamMarkerMsg) spin(workerName, searchString)
    else (wc.write(EndOfStreamMarkerMsg))
  }

}
