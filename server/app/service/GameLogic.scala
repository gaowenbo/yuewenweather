package service

import com.const._
import com.model.{ChessState, Step}

import scala.collection.mutable.ArrayBuffer

object GameLogic{

  val scoreList = {
    var result = ArrayBuffer[(Array[(Int, Int)], Int)]()
    for (i <- 0 to 4) {
      result += (((0 to 4).map((_ ,i)).toArray, 5))
    }
    for (i <- 0 to 4) {
      result += (((0 to 4).map((i, _)).toArray, 5))
    }
    result += (((0 to 4).map(i => (i, i)).toArray, 3))
    result += (((0 to 4).map(i => (i, 4 - i)).toArray, 3))
    result += (((0 to 3).map(i => (i, 3 - i)).toArray, 2))
    result += (((0 to 3).map(i => (i + 1, 4 - i)).toArray, 2))
    result += (((0 to 3).map(i => (i, i + 1)).toArray, 2))
    result += (((0 to 3).map(i => (i + 1, i)).toArray, 2))
    result += (((0 to 2).map(i => (i, 2 - i)).toArray, 1))
    result += (((0 to 2).map(i => (i + 2, 4 - i)).toArray, 1))
    result += (((0 to 2).map(i => (i, i + 2)).toArray, 1))
    result += (((0 to 2).map(i => (i + 2, i)).toArray, 1))
    for (i <- 0 to 3; j <- 0 to 3) {
      result += ((Array((i , j), (i + 1 , j), (i , j + 1), (i + 1 , j + 1)), 1))
    }
    result.toArray
  }

  val stepVector = Map(StepConst.MOVEUP -> (0, -1), StepConst.MOVEDOWN -> (0, 1), StepConst.MOVELEFT -> (-1, 0), StepConst.MOVERIGHT -> (1, 0))

  val stepBack = Map(StepConst.MOVEUP -> StepConst.MOVEDOWN, StepConst.MOVEDOWN -> StepConst.MOVEUP, StepConst.MOVELEFT -> StepConst.MOVERIGHT, StepConst.MOVERIGHT -> StepConst.MOVELEFT)

  val scoreMap = {
    scoreList.zipWithIndex.map(m => {
      m._1._1.map((_, m._2))
    }).flatten.groupBy(_._1).mapValues(_.map(_._2))
  }

  val position = scoreMap.keys

  val scorePosition = Array(Array(0, 1, 5, 1, 0),
                            Array(1, 6, 4, 6, 1),
                            Array(4, 4, 5, 3, 4),
                            Array(1, 6, 4, 6, 1),
                            Array(0, 1, 5, 1, 0))

  def checkStep(implicit chessState: ChessState, step: Step): Short = {
    if (step.flag != chessState.currentFlag) {
      return ResultConst.RESULT_FLAG_ERR
    }

    if (chessState.phrase == PhaseConst.DROP_PHASE) {
      if (step.action != StepConst.DROP) {
        return ResultConst.RESULT_PHRASE_DROP
      }

      if (chessState.chessBoard(step.x)(step.y) != FlagConst.NONE_FLAG) {
        return ResultConst.RESULT_PHRASE_DROP_EXIST
      }
    }

    if (chessState.phrase == PhaseConst.MOVE_PHASE) {
      if (!Array(StepConst.MOVEDOWN, StepConst.MOVELEFT, StepConst.MOVERIGHT, StepConst.MOVEUP).contains(step.action)) {
        return ResultConst.RESULT_PHRASE_MOVE
      }
      // 不是自己的子
      if (chessState.chessBoard(step.x)(step.y) != chessState.currentFlag) {
        return ResultConst.RESULT_PHRASE_MOVE_NOTSELF
      }

      // 不是空地
      if (chessState.chessBoard(step.x)(step.y) != FlagConst.NONE_FLAG) {
        return ResultConst.RESULT_PHRASE_MOVE_NOTBLANK
      }
    }

    if (chessState.phrase == PhaseConst.PICK_PHASE || chessState.phrase == PhaseConst.PICK_AFTER_MOVE_PHASE) {
      if (step.action != StepConst.PICK) {
        return ResultConst.RESULT_PHRASE_PICK
      }

      // 不是对方的子
      var otherFlag = getOtherFlag(chessState)
      if (chessState.chessBoard(step.x)(step.y) != otherFlag) {
        return ResultConst.RESULT_PHRASE_PICK_NOTOTHER
      }
    }

    if (chessState.phrase == PhaseConst.END_PHASE) {
      return ResultConst.RESULT_PHRASE_END
    }
    ResultConst.RESULT_NOTHING
  }

  def getOtherFlag(chessState: ChessState) = {
    if (chessState.currentFlag == FlagConst.FENSIVE_FLAG) {
      FlagConst.DEFENSIVE_FLAG
    } else {
      FlagConst.FENSIVE_FLAG
    }
  }

  def containFlag(implicit chessState: ChessState, flag: Short) = {
    chessState.chessBoard.exists(_.contains(flag))
  }

  def judgeWin(chessState: ChessState) = {
    var result = ResultConst.RESULT_NOTHING
    // 如果是提子阶段或走子阶段，有一方没有子了
    if ((chessState.phrase == PhaseConst.PICK_PHASE) || (chessState.phrase == PhaseConst.PICK_AFTER_MOVE_PHASE)) {
      var otherFlag = getOtherFlag(chessState)
      if (!containFlag(chessState, otherFlag)) { // 赢了
        if (chessState.currentFlag == FlagConst.FENSIVE_FLAG) result = ResultConst.RESULT_FENSIVE_WIN
        else result = ResultConst.RESULT_DEFENSIVE_WIN
      }
    }
    else if (chessState.phrase == PhaseConst.DROP_PHASE) { // 走棋阶段满了
      if (!containFlag(chessState, FlagConst.NONE_FLAG)) result = ResultConst.RESULT_NOBODY_WIN
    }
    result
  }

  def moveOnBoard(chessState: ChessState, step: Step) = {
    step.action match {
      case StepConst.DROP =>
        chessState.chessBoard(step.x)(step.y) = step.flag
      case StepConst.MOVEUP =>
        chessState.chessBoard(step.x)(step.y) = FlagConst.NONE_FLAG
        chessState.chessBoard(step.x)(step.y - 1) = step.flag
      case StepConst.MOVEDOWN =>
        chessState.chessBoard(step.x)(step.y) = FlagConst.NONE_FLAG
        chessState.chessBoard(step.x)(step.y + 1) = step.flag
      case StepConst.MOVELEFT =>
        chessState.chessBoard(step.x)(step.y) = FlagConst.NONE_FLAG
        chessState.chessBoard(step.x - 1)(step.y) = step.flag
      case StepConst.MOVERIGHT =>
        chessState.chessBoard(step.x)(step.y) = FlagConst.NONE_FLAG
        chessState.chessBoard(step.x + 1)(step.y) = step.flag
      case StepConst.PICK =>
        chessState.chessBoard(step.x)(step.y) = FlagConst.NONE_FLAG
    }
}

  def sameCurrentFlag(chessState: ChessState, x: Int, y: Int): Boolean = {
    if (x < 0 || x > 4 || y < 0 || y > 4) {
      false
    } else {
      chessState.chessBoard(x)(y) == chessState.currentFlag
    }
  }

  def calculateGoal(chessState: ChessState, step: Step) = {
    if (step.action == StepConst.PICK) {
      chessState.goal(chessState.currentFlag) -= 1
      (Set(), -1)
    } else {
      var currentHistoryGoalMap = chessState.historyGoal.getOrElse(chessState.currentFlag, collection.mutable.Set())
      var goalMap = LogicService.scoreMap.getOrElse((step.x, step.y), Array[Int]()).filter(!currentHistoryGoalMap.contains(_)).filter(!scoreList(_)._1.exists(e => !sameCurrentFlag(chessState, e._1, e._2)))
      (goalMap.toSet, goalMap.map(scoreList(_)).map(_._2).sum)
    }
  }

  def canMove(chessState: ChessState, waitingFlag: Short): Boolean = {
    chessState.chessBoard.exists(m => m.sliding(2,1).exists(n => n.toSet.equals(Set(waitingFlag, FlagConst.NONE_FLAG)))) ||
      chessState.chessBoard.reverse.exists(m => m.sliding(2,1).exists(n => n.toSet.equals(Set(waitingFlag, FlagConst.NONE_FLAG))))
  }

  def judgePhase(chessState: ChessState) = {
    val currentGoal = chessState.goal.getOrElse(chessState.currentFlag, 0)
    val waitingFlag = getOtherFlag(chessState)
    val waitingGoal = chessState.goal.getOrElse(waitingFlag, 0)


    if (chessState.phrase == PhaseConst.DROP_PHASE) {
      // 已走满
      if (!containFlag(chessState, FlagConst.NONE_FLAG)) {
        if (currentGoal == 0) {
          if (waitingGoal != 0) {
            chessState.phrase = PhaseConst.PICK_PHASE
            chessState.currentFlag = waitingFlag
          }
        } else {
          chessState.phrase = PhaseConst.PICK_PHASE
          if (waitingGoal != 0) {
            chessState.currentFlag = waitingFlag
          }
        }
      } else {
        chessState.currentFlag = waitingFlag
      }
    } else if (chessState.phrase == PhaseConst.PICK_PHASE || chessState.phrase == PhaseConst.PICK_AFTER_MOVE_PHASE) {
      if (currentGoal == 0) {
        if (canMove(chessState, waitingFlag)) {
          if (waitingFlag == 0) {
            chessState.currentFlag = waitingFlag
          } else {
            chessState.phrase = PhaseConst.MOVE_PHASE
          }
        } else if (chessState.currentFlag == FlagConst.FENSIVE_FLAG) {
          chessState.phrase = PhaseConst.MOVE_PHASE
          chessState.currentFlag = waitingFlag
        } else {
          if (waitingGoal == 0) {
            chessState.phrase = PhaseConst.MOVE_PHASE
            if (chessState.phrase == PhaseConst.PICK_AFTER_MOVE_PHASE) {
              chessState.currentFlag = waitingFlag
            }
          } else {
            chessState.currentFlag = waitingFlag
          }
        }
      }
    } else {
      if (currentGoal == 0) {
        if (canMove(chessState, waitingFlag)) {
          chessState.currentFlag = waitingFlag
        }
      } else {
        chessState.phrase = PhaseConst.PICK_AFTER_MOVE_PHASE
      }
    }
  }

  def addGoal(chessState: ChessState, goalTuple: (Set[_ <: Int], Int)) = {
    chessState.historyGoal(chessState.currentFlag) ++= goalTuple._1
    chessState.goal(chessState.currentFlag) += goalTuple._2
  }

  def go(chessState: ChessState, step: Step): Short = {
    val result:Short = checkStep(chessState, step)
    if (result == ResultConst.RESULT_NOTHING) {
      moveOnBoard(chessState, step)
      addGoal(chessState, calculateGoal(chessState, step))
      judgePhase(chessState)
      judgeWin(chessState)
    } else {
      result
    }
  }
}


