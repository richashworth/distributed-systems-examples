package fds.scala.events

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import java.util._
import java.util.concurrent.CountDownLatch
import io.nats.streaming.{
  Message,
  StreamingConnection,
  StreamingConnectionFactory,
  SubscriptionOptions
}
import scala.collection.mutable
import io.circe.parser._
import io.circe.generic.auto._
import io.circe.syntax._
import scala.collection.JavaConverters._

object EventReader {

  private[events] val targets =
    "AdultSki" :: "WinterActivities" :: "DanielBaudSkiGuide" :: Nil

  private[events] def register(logLine: String, document: Map[String, Int]): Unit = {
    targets.foreach((target: String) => {
      if (logLine.contains(target))
        document.compute(target, (k, v) => if (v.equals(null)) 1 else v + 1)
    })
  }

  case class Snapshot(latestEventSeq: Long, state: scala.collection.mutable.Map[String, Int])

  @throws[Exception]
  def main(args: Array[String]): Unit = {

    // Connect to EventStore
    val clusterID: String              = "test-cluster"
    val clientID: String               = "event-reader"
    val cf: StreamingConnectionFactory = new StreamingConnectionFactory(clusterID, clientID)
    val sc: StreamingConnection        = cf.createConnection

    // Subscribe to the store for events
    val subject: String = "BBC7"
    // you may want to remove count down latch for the stream.
    val doneLatch: CountDownLatch = new CountDownLatch(1)

    val seedSnapshot = recoverSnapshot()
    val state        = seedSnapshot.state
    var sequence     = seedSnapshot.latestEventSeq

    //  Many subscribe options @see https://nats.io/documentation/writing_applications/subscribing/
    val opts: SubscriptionOptions =
      new SubscriptionOptions.Builder().deliverAllAvailable
        .startAtSequence(seedSnapshot.latestEventSeq)
        .build

    sc.subscribe(
      subject,
      (evt: Message) => {
        System.out.println("Event reader got " + evt)
        register(new String(evt.getData()), state.asJava)

        sequence += 1
        if (sequence % 1 == 0) persistSnapshot(sequence, state)

//      doneLatch.countDown
        if (new String(evt.getData()) == EndOfStreamMarker) {
          println("received terminate")
          doneLatch.countDown()
        }
      },
      opts
    )

    // wait for a message
    doneLatch.await

    val reportStr = formatDoc(state.asJava)
    writeToFile("report.txt", reportStr)

    // tidy up the connection
    sc.close
  }

  import scala.collection.JavaConverters._

  // Format the map into a report
  private[events] def formatDoc(document: Map[String, Int]): String = {
    val rep = mutable.ArrayBuffer[String]()
    rep += "--------------------------------------------------------------------------------"
    rep += "| Giant Media - Usage Report                                                   |"
    rep += "--------------------------------------------------------------------------------"
    rep ++= document.asScala.map {
      case (k, v) => String.format("| %-49s|%26d |", k, new Integer(v))
    }
    rep += "--------------------------------------------------------------------------------"
    val views = document.asScala.values.sum
    rep += String.format("| %-49s|%26d |", "Total Views", new Integer(views))
    rep += "--------------------------------------------------------------------------------"
    return rep.mkString("\n")
  }

  // Write a String into a named file
  private[events] def writeToFile(filename: String, content: String): Unit = {
    Files.write(Paths.get(filename), content.getBytes(StandardCharsets.UTF_8))
  }

  // Read a String from a named file
  private[events] def readFromFile(filename: String) = {
    new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8)
  }

  private[events] def recoverSnapshot(): Snapshot = {
    val EmptySnapshot = Snapshot(0, scala.collection.mutable.Map[String, Int]())
    val json: String  = readFromFile("SNAPSHOT.txt")
    decode[Snapshot](json).getOrElse(EmptySnapshot)
  }

  private[events] def persistSnapshot(seq: Long, state: mutable.Map[String, Int]): Unit = {
    val updatedSnapshot = Snapshot(seq, state)
    println("PERSISTING SNAPSHOT: " + updatedSnapshot.state.toString())
    writeToFile("SNAPSHOT.txt", updatedSnapshot.asJson.spaces2)
  }

}
