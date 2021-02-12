package com.example.helloworld

import akka.{Done, NotUsed}
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.grpc.GrpcClientSettings
import akka.stream.scaladsl.Source

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object GreeterClientStreaming {

  def main(args: Array[String]): Unit = {
    implicit val sys: ActorSystem[_] = ActorSystem(Behaviors.empty, "GreeterClient")
    implicit val ec: ExecutionContext = sys.executionContext

    val client: GreeterServiceClient = GreeterServiceClient(
      GrpcClientSettings.fromConfig("helloworld.GreeterService")
    )

    List("Alice", "Jim")
      .foreach(streamingBroadcast)

    def streamingBroadcast(name: String): Unit = {
      println(s"Performing streaming requests: $name")

      val requests: Source[HelloRequest, NotUsed] =
        Source
          .tick(1.second, 1.second, "tick")
          .zipWithIndex
          .map { case (_, i) => i }
          .map(i => HelloRequest(s"$name-$i"))
          .mapMaterializedValue(_ => NotUsed)

      val responses: Source[HelloReply, NotUsed] = client.sayHelloToAll(requests)
      val done: Future[Done] = responses.runForeach(reply => println(s"$name got streaming reply: ${reply.message}"))

      done.onComplete {
        case Success(_) => println("streamingBroadcast done")
        case Failure(e) => println(s"Error streamingBroadcast: $e")
      }
    }

  }

}
