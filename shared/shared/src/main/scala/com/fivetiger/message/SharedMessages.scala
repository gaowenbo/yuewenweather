package com.fivetiger.message

import com.model.{ChessState, Step}

object SharedMessages {
  def itWorks = "It wworkrrs!"

  def itWorks2 = "It wworkrrs!"

  sealed trait SharedMessages

  case class Join(s: String) extends SharedMessages

  case class Broadcast(s: String, c: String)extends SharedMessages

  case class Leave(s:String) extends SharedMessages
  case class Chat(sender: String, content: String, status: String)

  case class MM(s: Step) extends SharedMessages

  case class StepMessage(step: Step) extends SharedMessages

  case class StateMessage(result: Short, chessState: ChessState) extends SharedMessages
}


