package service

import com.const._
import com.model.{ChessState, Step}

import scala.collection.mutable.ArrayBuffer

class LogicService(chessState: ChessState) {



  def checkStep(step: Step): Short = {
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
      var otherFlag = getOtherFlag()
      if (chessState.chessBoard(step.x)(step.y) != otherFlag) {
        return ResultConst.RESULT_PHRASE_PICK_NOTOTHER
      }
    }

    if (chessState.phrase == PhaseConst.END_PHASE) {
      return ResultConst.RESULT_PHRASE_END
    }
    ResultConst.RESULT_NOTHING
  }

  def getOtherFlag() = {
    if (chessState.currentFlag == FlagConst.FENSIVE_FLAG) {
      FlagConst.DEFENSIVE_FLAG
    } else {
      FlagConst.FENSIVE_FLAG
    }
  }

  def containFlag(flag: Short) = {
    chessState.chessBoard.exists(_.contains(flag))
  }

  def judgeWin() = {
    var result = ResultConst.RESULT_NOTHING
    // 如果是提子阶段或走子阶段，有一方没有子了
    if ((chessState.phrase == PhaseConst.PICK_PHASE) || (chessState.phrase == PhaseConst.PICK_AFTER_MOVE_PHASE)) {
      var otherFlag = getOtherFlag()
      if (!containFlag(otherFlag)) { // 赢了
        if (chessState.currentFlag == FlagConst.FENSIVE_FLAG) result = ResultConst.RESULT_FENSIVE_WIN
        else result = ResultConst.RESULT_DEFENSIVE_WIN
      }
    }
    else if (chessState.phrase == PhaseConst.DROP_PHASE) { // 走棋阶段满了
      if (!containFlag(FlagConst.NONE_FLAG)) result = ResultConst.RESULT_NOBODY_WIN
    }
    result
  }

  def moveOnBoard(step: Step) = {
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

  def addGoalToFlag(isFensive: Boolean, value: Int) = {
    if (isFensive) {
//      chessState.fensiveGoal += value
    } else {
//      chessState.defensiveGoal += value
    }
  }

  def sameCurrentFlag(x: Int, y: Int): Boolean = {
    if (x < 0 || x > 4 || y < 0 || y > 4) {
      false
    } else {
      chessState.chessBoard(x)(y) == chessState.currentFlag
    }
  }

  def calculateGoal(step: Step) = {
    if (step.action == StepConst.PICK) {
      addGoalToFlag(chessState.currentFlag == FlagConst.FENSIVE_FLAG, -1)
      chessState.goal(chessState.currentFlag) -= 1
    } else {
      var currentHistoryGoalMap = chessState.historyGoal.getOrElse(chessState.currentFlag, collection.mutable.Set())
      var goalMap = LogicService.scoreMap.getOrElse((step.x, step.y), Array[Int]()).filter(!currentHistoryGoalMap.contains(_)).filter(!LogicService.scoreList(_)._1.exists(e => !chessState.chessBoard(e._1)(e._2).equals(chessState.currentFlag)))

      chessState.goal(chessState.currentFlag) += goalMap.map(LogicService.scoreList(_)).map(_._2).sum
      chessState.historyGoal(chessState.currentFlag) ++= goalMap.toSet

//      var goal = 0
      if (sameCurrentFlag(step.x - 1, step.y)) {
        if (sameCurrentFlag(step.x - 1, step.y - 1) && sameCurrentFlag(step.x, step.y - 1)) {
          addGoalToFlag(chessState.currentFlag == FlagConst.FENSIVE_FLAG, 1)
        }
        if (sameCurrentFlag(step.x - 1, step.y + 1) && sameCurrentFlag(step.x, step.y + 1)){
          addGoalToFlag(chessState.currentFlag == FlagConst.FENSIVE_FLAG, 1)
        }
      }
      if (sameCurrentFlag(step.x + 1, step.y)) {
        if (sameCurrentFlag(step.x + 1, step.y - 1) && sameCurrentFlag(step.x, step.y - 1)) {
          addGoalToFlag(chessState.currentFlag == FlagConst.FENSIVE_FLAG, 1)
        }
        if (sameCurrentFlag(step.x + 1, step.y + 1) && sameCurrentFlag(step.x, step.y + 1)){
          addGoalToFlag(chessState.currentFlag == FlagConst.FENSIVE_FLAG, 1)
        }
      }
      val zeStart = Array(step.x + step.y - 4, 0).max
      val zeLength = step.x + step.y + 1 - (2 * zeStart)
      if (!(zeStart to zeLength).map(i => chessState.chessBoard(i)(zeLength - i)).exists(_ != chessState.currentFlag)) {
        zeLength match {
          case 3 =>
            addGoalToFlag(chessState.currentFlag == FlagConst.FENSIVE_FLAG, 1)
          case 4 =>
            addGoalToFlag(chessState.currentFlag == FlagConst.FENSIVE_FLAG, 2)
          case 5 =>
            addGoalToFlag(chessState.currentFlag == FlagConst.FENSIVE_FLAG, 3)
        }
      }

      val pingStart = Array(step.x - step.y, 0).max
      val pingLength = step.x - step.y + 5 - (2 * pingStart)
      if (!(zeStart to zeLength).map(i => chessState.chessBoard(i)(i - step.x + step.y)).exists(_ != chessState.currentFlag)) {
        zeLength match {
          case 3 =>
            addGoalToFlag(chessState.currentFlag == FlagConst.FENSIVE_FLAG, 1)
          case 4 =>
            addGoalToFlag(chessState.currentFlag == FlagConst.FENSIVE_FLAG, 2)
          case 5 =>
            addGoalToFlag(chessState.currentFlag == FlagConst.FENSIVE_FLAG, 3)
        }
      }

      if (!chessState.chessBoard(step.x).exists(_ != chessState.currentFlag)) {
        addGoalToFlag(chessState.currentFlag == FlagConst.FENSIVE_FLAG, 5)
      }

      if (!chessState.chessBoard.map(l => l(step.y)).exists(_ != chessState.currentFlag)){
        addGoalToFlag(chessState.currentFlag == FlagConst.FENSIVE_FLAG, 5)
      }
    }
  }

  def canMove(waitingFlag: Short): Boolean = {
    chessState.chessBoard.exists(m => m.sliding(2,1).exists(n => n.toSet.equals(Set(waitingFlag, FlagConst.NONE_FLAG)))) ||
      chessState.chessBoard.reverse.exists(m => m.sliding(2,1).exists(n => n.toSet.equals(Set(waitingFlag, FlagConst.NONE_FLAG))))
  }

  def judgePhase() = {
    val currentGoal = chessState.goal.getOrElse(chessState.currentFlag, 0)
    val waitingFlag = getOtherFlag()
    val waitingGoal = chessState.goal.getOrElse(waitingFlag, 0)


    if (chessState.phrase == PhaseConst.DROP_PHASE) {
      // 已走满
      if (!containFlag(FlagConst.NONE_FLAG)) {
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
        if (canMove(waitingFlag)) {
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
        if (canMove(waitingFlag)) {
          chessState.currentFlag = waitingFlag
        }
      } else {
        chessState.phrase = PhaseConst.PICK_AFTER_MOVE_PHASE
      }
    }
  }

  def go(step: Step): Short = {
    val result:Short = checkStep(step)
    if (result == ResultConst.RESULT_NOTHING) {
      moveOnBoard(step)
      calculateGoal(step)
      judgePhase()
      judgeWin()
    } else {
      result
    }
  }
  def getChessState = chessState
}

object LogicService {

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

  val scoreMap = {
    scoreList.zipWithIndex.map(m => {
      m._1._1.map((_, m._2))
    }).flatten.groupBy(_._1).mapValues(_.map(_._2))
  }

  def getChessState: ChessState = {
    var chessState = ChessState()
//    chessState.currentFlag
    chessState
  }
}
