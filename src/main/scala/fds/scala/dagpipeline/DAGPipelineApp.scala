package fds.scala.dagpipeline

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
object DAGPipelineApp extends App {

  val maxWorkers = 1

  implicit val ec =
    ExecutionContext.fromExecutor(
      java.util.concurrent.Executors.newFixedThreadPool(maxWorkers * 2))

  // Start Workers
  def createIntermediateWorker(name: String, searchString: String) = Future {
    new Worker(readChannelName = WorkChannelName,
               writeChannelName = ForwardChannelName).start(name, searchString)
  }
  def createFinalWorker(name: String, searchString: String) = Future {
    new Worker(readChannelName = ForwardChannelName,
               writeChannelName = ReplyChannelName).start(name, searchString)
  }

  createIntermediateWorker("Intermediate Worker", "Windows")
  createFinalWorker("Final Worker", "Media")

  val fileName = "access.log"
  val logLines = scala.io.Source.fromFile(fileName).getLines

  val end = new End(logLines.length)
  Start.publish(logLines)

  val result = end.consumeResults(0, 0)
  println(s"DAG RESULT: $result")

}
