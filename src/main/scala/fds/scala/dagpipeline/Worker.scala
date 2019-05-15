package fds.scala.dagpipeline

import com.zink.queue.{Connection, ConnectionFactory, ReadChannel, WriteChannel}
import scala.annotation.tailrec

/**
  *  Consumer Stub
  */
object Worker {

  val con: Connection = ConnectionFactory.connect("127.0.0.1")
  val rc: ReadChannel = con.subscribe(ChannelName)
  val wc: WriteChannel = con.publish(ReplyChannelName)

  def start(workerName: String, searchString: String): Unit = {
    spin(workerName, searchString)
  }

  @tailrec
  def spin(workerName: String, searchString: String): Unit = {
//    Thread.sleep(100)
    val msg = rc.read()
    if (msg.toString().contains(searchString)) {
      wc.write(SearchResultFoundMsg)
      spin(workerName, searchString)
    } else if (msg != EndOfStreamMarker) spin(workerName, searchString)
    else (wc.write(EndOfStreamMarker))
  }

}
