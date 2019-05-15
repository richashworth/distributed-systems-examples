package fds.scala.queue

import com.zink.queue.{WriteChannel, ConnectionFactory, Connection}

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
object Producer extends App {

  val con: Connection = ConnectionFactory.connect("127.0.0.1")
  val wc: WriteChannel = con.publish(ChannelName)

  wc.write("Hello Consumer")
  wc.write(EndOfStreamMarker)

  val fileName = "access.log"
  val logLines = scala.io.Source.fromFile(fileName).getLines

  // TODO use logLines to write each line onto the channel
//  for (line <- logLines) {
//    wc.write(line)
//  }
  // TODO write an end of stream marker
  wc.write(EndOfStreamMarker)

}
