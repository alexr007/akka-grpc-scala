package com.example.helloworld

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.grpc.GrpcClientSettings

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object GreeterClientOneByOne {

  def main(args: Array[String]): Unit = {
    implicit val sys: ActorSystem[_] = ActorSystem(Behaviors.empty, "GreeterClient")
    implicit val ec: ExecutionContext = sys.executionContext

    val client = GreeterServiceClient(
      GrpcClientSettings.fromConfig("helloworld.GreeterService")
    )

    List("Alice", "Bob", "Jim")
      .foreach(makeSingleRequest)

    def makeSingleRequest(name: String): Unit = {
      println(s"Performing Single request: $name")

      val rq: HelloRequest = HelloRequest(name)
      val rs: Future[HelloReply] = client.sayHello(rq)

      rs.onComplete {
        case Success(msg) => println(msg)
        case Failure(e) => println(s"Error: $e")
      }
    }
  }

}
