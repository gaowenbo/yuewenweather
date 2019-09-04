package com.model

import com.const.FlagConst
import collection.mutable.Map
import collection.mutable.Set

case class ChessState(chessBoard:Array[Array[Short]] = Array.ofDim(5, 5),
                      var currentFlag: Short = FlagConst.FENSIVE_FLAG,
                      var phrase: Short = 0,
                      goal: Map[Short, Int] = Map[Short, Int](FlagConst.FENSIVE_FLAG -> 0,FlagConst.DEFENSIVE_FLAG -> 0),
                      historyGoal: Map[Short, Set[Int]] = Map[Short, Set[Int]](FlagConst.FENSIVE_FLAG -> Set[Int](), FlagConst.DEFENSIVE_FLAG -> Set[Int]()))
