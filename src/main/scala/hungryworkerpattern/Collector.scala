package hungryworkerpattern

import com.zink.queue.{Connection, ConnectionFactory, ReadChannel, WriteChannel}
import scala.annotation.tailrec

/**
  * Producer stub
  *
  * From Fly Docker
  *
  * > docker run -d -p 4396:4396 zink/fly
  *
  * To find container <run time name> (not required for running example)
  * > docker ps
  *
  * To find ipAddress (not required to run; point to localhost)
  * > docker inspect <run time name> | grep -i ipaddress
  *
  */
object Collector extends App {

  val con: Connection = ConnectionFactory.connect("127.0.0.1")
  val rc: ReadChannel = con.subscribe(ReplyChannelName)
  val wc: WriteChannel = con.publish(ChannelName)

  val fileName = "access.log"
  val logLines = scala.io.Source.fromFile(fileName).getLines

  // TODO use logLines to write each line onto the channel
  for (line <- logLines) {
    wc.write(line)
  }
  // TODO write an end of stream marker
  wc.write(EndOfStreamMarker)

  @tailrec
  def consumeResults(count: Int): Int = {
    val msg = rc.read()
    if (msg.toString() == SearchResultFoundMsg) {
      consumeResults(count + 1)
    } else if (msg.toString() != EndOfStreamMarker) consumeResults(count)
    else count
  }

  val result = consumeResults(0)
  println("result: " + result)

}
