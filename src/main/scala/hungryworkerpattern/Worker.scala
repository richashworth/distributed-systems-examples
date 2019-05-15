package hungryworkerpattern

import com.zink.queue.{Connection, ConnectionFactory, ReadChannel, WriteChannel}
import scala.annotation.tailrec

/**
  *  Consumer Stub
  */
object Worker extends App {

  val con: Connection = ConnectionFactory.connect("127.0.0.1")
  val rc: ReadChannel = con.subscribe(ChannelName)
  val wc: WriteChannel = con.publish(ReplyChannelName)

  @tailrec
  def spin(searchString: String): Unit = {
    val msg = rc.read()
    if (msg.toString().contains(searchString)){
      wc.write(SearchResultFoundMsg)
      spin(searchString)
    }
    else if (msg != EndOfStreamMarker) spin(searchString)
    else(wc.write(EndOfStreamMarker))
  }

  val searchString = "DanielBaudSkiGuide"

  println("Starting worker")
  spin(searchString)
  println("Closing worker")

}
