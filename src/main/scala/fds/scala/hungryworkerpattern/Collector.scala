package fds.scala.hungryworkerpattern

import com.zink.queue.{Connection, ConnectionFactory, ReadChannel, WriteChannel}
import scala.annotation.tailrec
import scala.concurrent.{ExecutionContext, Future}

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
object CollectorApp extends App {

  val maxWorkers = 20

  implicit val ec =
    ExecutionContext.fromExecutor(
      java.util.concurrent.Executors.newFixedThreadPool(maxWorkers * 2))

  val con: Connection = ConnectionFactory.connect("127.0.0.1")
  val rc: ReadChannel = con.subscribe(ReplyChannelName)
  val wc: WriteChannel = con.publish(ChannelName)

  val fileName = "access.log"
  val logLines = scala.io.Source.fromFile(fileName).getLines

  // TODO use logLines to write each line onto the channel
  def publish(): Unit = {
    for (line <- logLines) {
      wc.write(line)
    }
    // TODO write an end of stream marker
    wc.write(EndOfStreamMarker)
  }

  @tailrec
  def consumeResults(linesProcessed: Int, count: Int): Int = {
    val msg = rc.read()
    if (msg.toString() == SearchResultFoundMsg) {
      consumeResults(linesProcessed + 1, count + 1)
    } else if (linesProcessed < logLines.length)
      consumeResults(linesProcessed + 1, count)
    else count
  }

  // Start Workers
  def createWorker(name: String) = Future { Worker.start(name, searchString) }

  def runTest(workerCount: Int): Long = {
    publish()
    (1 to workerCount).map(name => createWorker(name.toString))
    val startTime = System.currentTimeMillis()
    // Process results
    val result = consumeResults(0, 0)
    val endTime = System.currentTimeMillis()
    endTime - startTime
  }

  def averageTimings(workerCount: Int): Unit = {
    val avgTiming = (1 to 10)
      .map(_ => runTest(workerCount))
      .sum / workerCount

    println(s"Average time for $workerCount workers: $avgTiming")
  }

  (1 to maxWorkers).map(workerCount => averageTimings(workerCount))

}
