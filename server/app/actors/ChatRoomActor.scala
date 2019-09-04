package actors

import akka.actor._
import com.fivetiger.message.SharedMessages.{StateMessage, StepMessage}
import com.model.ChessState
import service.{GameLogic, LogicService, MidRobot}

class ChatRoomActor(out: ActorRef, chessState: Option[ChessState]) extends Actor with ActorLogging {
  def receive: Receive = {
    case msg: StepMessage => {
      var result = GameLogic.go(chessState.get, msg.step)
//      out ! StateMessage(result, chessState.get)
      MidRobot.getMaxScoreStep(chessState.get) match {
        case None =>
          out ! StateMessage(result, chessState.get)
        case Some(step) =>
          GameLogic.go(chessState.get, step)
          out ! StateMessage(result, chessState.get)
      }
    }
    case s => {
      out ! s
      println(s)
      }
  }
}
object ChatRoomActor{
  def props(out: ActorRef, state: Option[ChessState]) = Props(classOf[ChatRoomActor], out, state)
}

