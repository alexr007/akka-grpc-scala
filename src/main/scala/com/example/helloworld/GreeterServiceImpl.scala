package com.example.helloworld

import akka.NotUsed
import akka.actor.typed.ActorSystem
import akka.stream.scaladsl.{BroadcastHub, Keep, MergeHub, Sink, Source}

import scala.concurrent.Future

class GreeterServiceImpl(system: ActorSystem[_]) extends GreeterService {
  private implicit val sys: ActorSystem[_] = system

  val (inboundHub: Sink[HelloRequest, NotUsed], outboundHub: Source[HelloReply, NotUsed]) =
    MergeHub.source[HelloRequest]
    .map(request => HelloReply(s"Hello, ${request.name}"))
      .toMat(BroadcastHub.sink[HelloReply])(Keep.both)
      .run()

  override def sayHello(request: HelloRequest): Future[HelloReply] = {
    Future.successful(HelloReply(s"Hello, ${request.name}"))
  }

  override def sayHelloToAll(in: Source[HelloRequest, NotUsed]): Source[HelloReply, NotUsed] = {
    in.runWith(inboundHub)
    outboundHub
  }

}
