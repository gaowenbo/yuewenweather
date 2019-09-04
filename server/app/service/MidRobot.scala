package service

import com.const.{FlagConst, PhaseConst, StepConst}
import com.model.{ChessState, Step}

object MidRobot {
  def getMaxScoreStep(chessState: ChessState): Option[Step] = {
    if (chessState.phrase == PhaseConst.DROP_PHASE) {
      getMaxScoreDrop(chessState, 3)._1
    } else if(chessState.phrase == PhaseConst.MOVE_PHASE) {
      getMaxScoreMove(chessState, 5)._1
    } else {
      getMaxScorePick(chessState)
    }
  }
  def getMaxScoreDrop(chessState: ChessState, deepth: Int): (Option[Step], Int) = {
    var maxScore = 0
    var copy = ChessState(chessState.chessBoard.clone(), chessState.currentFlag, chessState.phrase, chessState.goal, chessState.historyGoal)
    var resultStep: Option[Step] = None
    var resultGoal = 0
    val chessCount = copy.chessBoard.map(_.filter(_ != FlagConst.NONE_FLAG).size).sum
    for (i <- 0 to 4; j <- 0 to 4) {
      if (copy.chessBoard(i)(j) == FlagConst.NONE_FLAG) {
        val step = Step(copy.currentFlag, StepConst.DROP, i.toShort, j.toShort)
        val backStep = Step(copy.currentFlag, StepConst.PICK, i.toShort, j.toShort)
        GameLogic.moveOnBoard(copy, step)
        val goal = GameLogic.calculateGoal(copy, step)
        copy.currentFlag = if (copy.currentFlag == FlagConst.FENSIVE_FLAG) {FlagConst.DEFENSIVE_FLAG} else {FlagConst.FENSIVE_FLAG}
        var tempMaxScore = if (deepth > 0 && chessCount > 1) {
          (goal._2 + 1) * 1000 * (deepth + 10) / 10  + GameLogic.scorePosition(i)(j) * 10 + scala.util.Random.nextInt(10) - getMaxScoreDrop(copy, deepth - 1)._2
        } else {
          (goal._2 + 1) * 1000 * (deepth + 10) / 10  + GameLogic.scorePosition(i)(j) * 10 + scala.util.Random.nextInt(10)
        }
        if (tempMaxScore > maxScore)
        {
          resultGoal = goal._2
          resultStep = Option(step)
          maxScore = tempMaxScore
        }
        GameLogic.moveOnBoard(copy, backStep)
        copy.currentFlag = if (copy.currentFlag == FlagConst.FENSIVE_FLAG) {FlagConst.DEFENSIVE_FLAG} else {FlagConst.FENSIVE_FLAG}
      }
    }

    (resultStep, maxScore)
  }

  def getMaxScoreMove(chessState: ChessState, deepth: Int): (Option[Step], Int) = {
    var maxScore = 0
    var copy = ChessState(chessState.chessBoard.clone(), chessState.currentFlag, chessState.phrase, chessState.goal, chessState.historyGoal)
    var resultStep:Option[Step] = None
    var resultGoal = 0
    val chessCount = copy.chessBoard.map(_.filter(_ != FlagConst.NONE_FLAG).size).sum
    for (i <- 0 to 4; j <- 0 to 4; k <- Array(StepConst.MOVEUP, StepConst.MOVEDOWN, StepConst.MOVELEFT, StepConst.MOVERIGHT)) {
      if (copy.chessBoard(i)(j) == chessState.currentFlag ) {
        val (x, y) = GameLogic.stepVector.getOrElse(k, (0, 0))
        if (copy.chessBoard(i + x)(j + y) == FlagConst.NONE_FLAG) {
          val step = Step(copy.currentFlag, k, i.toShort, j.toShort)
          val backStep = Step(copy.currentFlag, GameLogic.stepBack.getOrElse(k, 0), (i + x).toShort, (j + y).toShort)
          GameLogic.moveOnBoard(copy, step)
          val goal = GameLogic.calculateGoal(copy, step)
          copy.currentFlag = if (copy.currentFlag == FlagConst.FENSIVE_FLAG) {FlagConst.DEFENSIVE_FLAG} else {FlagConst.FENSIVE_FLAG}
          var tempMaxScore = if (deepth > 0) {
            (goal._2 + 1) * 1000 * (deepth + 10) / 10  + GameLogic.scorePosition(i)(j) * 10 + scala.util.Random.nextInt(10) - getMaxScoreDrop(copy, deepth - 1)._2
          } else {
            (goal._2 + 1) * 1000 * (deepth + 10) / 10  + GameLogic.scorePosition(i)(j) * 10 + scala.util.Random.nextInt(10)
          }
          if (tempMaxScore > maxScore)
          {
            resultGoal = goal._2
            resultStep = Option(step)
            maxScore = tempMaxScore
          }
          GameLogic.moveOnBoard(copy, backStep)
          copy.currentFlag = if (copy.currentFlag == FlagConst.FENSIVE_FLAG) {FlagConst.DEFENSIVE_FLAG} else {FlagConst.FENSIVE_FLAG}
        }
      }
    }
    (resultStep, maxScore)
  }


  def getMaxScorePick(state: ChessState) = {
    var maxScore = 0
    var result: Option[Step] = None
    for (i <- 0 to 4; j <- 0 to 4) {
      if (state.chessBoard(i)(j) != FlagConst.NONE_FLAG && state.chessBoard(i)(j) != state.currentFlag) {
        var tempMaxScore = GameLogic.scorePosition(i)(j) * 10 + scala.util.Random.nextInt(10)
        if (tempMaxScore > maxScore) {
          maxScore = tempMaxScore
          result = Option(Step(state.currentFlag, StepConst.PICK, i.toShort, j.toShort))
        }
      }
    }
    result
  }
}
