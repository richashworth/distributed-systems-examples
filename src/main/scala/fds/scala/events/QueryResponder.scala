package fds.scala.events

import com.zink.queue.ConnectionFactory
import io.nats.streaming.StreamingConnectionFactory

object QueryResponder {

  @throws[Exception]
  def main(args: Array[String]): Unit = {

    // Connect to queue
    val ipAddr = "127.0.0.1" // or "192.168.1.84" e.g.
    val con    = ConnectionFactory.connect(ipAddr)
    val rc     = con.subscribe("QUERY_CHANNEL")
    val wc     = con.publish("QUERY_RESPONSE_CHANNEL")

    // Connect to EventStore
    val clusterID = "test-cluster"
    val clientID  = "event-writer"
    val cf        = new StreamingConnectionFactory(clusterID, clientID)
    val sc        = cf.createConnection

    while (true) {

      val event = rc.read.asInstanceOf[String]
      // to write an item to the event store
      println("QueryResponder received message " + event.toString())
      //TODO: respond with a pretty-printed view of current state
    }

  }

}
