package com.wenbo.controllers

import java.nio.charset.StandardCharsets

import javax.inject._
import actors._
import akka.NotUsed
import akka.actor._
import akka.stream.{KillSwitches, Materializer, UniqueKillSwitch}
import akka.stream.scaladsl._
import akka.util.{ByteString, Timeout}
import com.fivetiger.message.SharedMessages.SharedMessages
import com.model.ChessState
import play.api.Logger
import play.api.libs.ws._
import play.api.http.websocket.{BinaryMessage, CloseCodes, CloseMessage, Message}
import play.api.libs.json._
import play.api.libs.streams.{ActorFlow, AkkaStreams}
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._
import service.LogicService
import views.{ChatHtml, ChessHtml}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class Application @Inject()(cc: ControllerComponents, ws: WSClient)(implicit actorSystem: ActorSystem, mat: Materializer, ec: ExecutionContext) extends AbstractController(cc) {

  val logger = play.api.Logger(getClass)

  protected val romMap = new scala.collection.mutable.HashMap[String, Flow[SharedMessages, SharedMessages, _]]

  implicit val webSocketTransformer = new MessageFlowTransformer[SharedMessages, SharedMessages] {
    override def transform(flow: Flow[SharedMessages, SharedMessages, _]): Flow[Message, Message, _] = {
      import boopickle.Default._
      AkkaStreams.bypassWith[ Message, SharedMessages, Message ]( Flow[ Message ] collect {
        case BinaryMessage( data ) =>
          Unpickle.apply[SharedMessages].tryFromBytes( data.asByteBuffer ) match {
            case Success( msg ) =>
              Left( msg )
            case Failure( err ) =>
              Right( CloseMessage( CloseCodes.Unacceptable, s"Error with transfer: $err" ) )
          }
        case _ =>
          Right( CloseMessage( CloseCodes.Unacceptable, "This WebSocket only accepts binary." ) )
      })( flow.map { msg =>
        val bytes = ByteString.fromByteBuffer( Pickle.intoBytes[SharedMessages](msg) )
        BinaryMessage( bytes )
      })
    }
  }

  def index = Action {
    Ok(ChatHtml.getHtml).as(HTML)
  }

  def chess = Action {
    val m = LogicService.scoreList
    Ok(ChessHtml.getHtml).as(HTML)
  }

  def getWeather(longitude: Double, latitude: Double) = Action {
    val httpResult = Await.result(ws.url("http://api.map.baidu.com/telematics/v3/weather").addHttpHeaders("Accept" -> "application/json")
      .addQueryStringParameters("location" -> s"$longitude,$latitude", "output" -> "json","ak" -> "3253dd1e249d58370f8f9dc825099e4d").get()
      , 10.seconds)
    Ok(httpResult.bodyAsBytes.decodeString(StandardCharsets.UTF_8.toString)).as(HTML)
  }

  def getFromMap(str: String) = {
    this.synchronized({
      if (!romMap.contains(str)) {
        var chessState = ChessState()
        val chatRoom = ActorFlow.actorRef[SharedMessages, SharedMessages](out => ChatRoomActor.props(out, Option[ChessState](chessState)))
        val (hubSink: Sink[SharedMessages, NotUsed], hubSource: Source[SharedMessages, NotUsed]) = MergeHub.source[SharedMessages](16).via(chatRoom).toMat(BroadcastHub.sink(256))(Keep.both).run()
        val roomChatFlow: Flow[SharedMessages, SharedMessages, _] = {
          Flow.fromSinkAndSource(hubSink, hubSource).joinMat((KillSwitches.singleBidi[SharedMessages, SharedMessages]))(Keep.right).backpressureTimeout(3 second)
        }
        romMap.put(str,  roomChatFlow)
      }
      romMap.getOrElse(str, null)
    })
  }

  /**
    * Checks that the WebSocket comes from the same origin.  This is necessary to protect
    * against Cross-Site WebSocket Hijacking as WebSocket does not implement Same Origin Policy.
    *
    * See https://tools.ietf.org/html/rfc6455#section-1.3 and
    * http://blog.dewhurstsecurity.com/2013/08/30/security-testing-html5-websockets.html
    */
  def sameOriginCheck(rh: RequestHeader): Boolean = {
    rh.headers.get("Origin") match {
      case Some(originValue) if originMatches(originValue) =>
        logger.debug(s"originCheck: originValue = $originValue")
        true

      case Some(badOrigin) =>
        logger.error(s"originCheck: rejecting request because Origin header value ${badOrigin} is not in the same origin")
        false

      case None =>
        logger.error("originCheck: rejecting request because no Origin header found")
        false
    }
  }

  /**
    * Returns true if the value of the Origin header contains an acceptable value.
    *
    * This is probably better done through configuration same as the allowedhosts filter.
    */
  def originMatches(origin: String): Boolean = {
    //    origin.contains("localhost:9000") || origin.contains("localhost:19001")
    true
  }
}