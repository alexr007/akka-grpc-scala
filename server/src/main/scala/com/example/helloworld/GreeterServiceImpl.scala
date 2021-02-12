package com.example.helloworld

import akka.NotUsed
import akka.actor.typed.ActorSystem
import akka.stream.scaladsl.{BroadcastHub, Keep, MergeHub, Sink, Source}

import scala.concurrent.Future

class GreeterServiceImpl(system: ActorSystem[_]) extends GreeterService {
  private implicit val sys: ActorSystem[_] = system

  /** real business */
  def mapper(rq: HelloRequest) = HelloReply(s"Hello, ${rq.name}")

  /** Akka Streams to Serve Source[HelloRequest, _] */
  val (inboundHub: Sink[HelloRequest, NotUsed], outboundHub: Source[HelloReply, NotUsed]) =
    MergeHub
      .source[HelloRequest]
      .map(mapper)
      .toMat(BroadcastHub.sink[HelloReply])(Keep.both)
      .run()

  /** 1 */
  override def sayHello(rq: HelloRequest): Future[HelloReply] =
    Future.successful(mapper(rq))

  /** stream */
  override def sayHelloToAll(in: Source[HelloRequest, NotUsed]): Source[HelloReply, NotUsed] = {
    in.runWith(inboundHub)
    outboundHub
  }

}
