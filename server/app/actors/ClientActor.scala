package actors

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import com.fivetiger.message.SharedMessages._

class ClientActor(out: ActorRef, user: Option[String]) extends Actor with ActorLogging {
  def receive: Receive = {
    case Broadcast(s, c) => {
      if (s == user.get) {
        out ! Broadcast(s, c)
      }
    }
    case StepMessage(m) => {
      out ! StepMessage(m)
    }
  }

  override def preStart(): Unit = {
    user match {
      case Some(u) => {
        out ! Join(u)
      }
      case None =>
        log.info(s"No user name ${out.path}")
        self ! PoisonPill
    }
  }

  override def postStop(): Unit = {
    user match {
      case Some(u) => out ! Leave(u)
      case None => log.info(s"Stop ${out.path}")
    }
    self ! PoisonPill
  }
}

object ClientActor{
  def props(out: ActorRef, user: Option[String]) = Props(classOf[ClientActor], out, user)
}