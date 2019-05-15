package fds.scala.dagpipeline

import com.zink.queue.{Connection, ConnectionFactory, WriteChannel}

/**
  * Producer stub -- put work on the pipeline
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
object Start {

  val con: Connection = ConnectionFactory.connect("127.0.0.1")
  val wc: WriteChannel = con.publish(WorkChannelName)

  def publish(logLines: Iterator[String]): Unit = {
    for (line <- logLines) {
      wc.write(line)
    }
    wc.write(EndOfStreamMarkerMsg)
  }

}
