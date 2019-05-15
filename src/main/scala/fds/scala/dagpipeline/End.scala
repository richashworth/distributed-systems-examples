package fds.scala.dagpipeline

import com.zink.queue.{Connection, ConnectionFactory, ReadChannel}
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
class End(expectedMsgCount: Int) {

  val con: Connection = ConnectionFactory.connect("127.0.0.1")
  val rc: ReadChannel = con.subscribe(ReplyChannelName)

  @tailrec
  final def consumeResults(linesProcessed: Int, count: Int): Int = {
    println("LP" + linesProcessed)
    println("expP" + expectedMsgCount)
    rc.read()
    if (linesProcessed == expectedMsgCount) {
      count
    }
    else {
      println("adding to linesprocessed")
      consumeResults(linesProcessed + 1, count + 1)
    }
  }

}
