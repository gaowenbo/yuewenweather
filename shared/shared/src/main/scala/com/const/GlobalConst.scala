package com.const

object FlagConst extends FlagConst
trait FlagConst {
  val NONE_FLAG:Short = 0
  val FENSIVE_FLAG:Short = 1
  val DEFENSIVE_FLAG:Short = 2
}

object PhaseConst extends PhaseConst
trait PhaseConst {
  val DROP_PHASE:Short = 0
  val PICK_PHASE:Short =1
  val MOVE_PHASE:Short = 2
  val PICK_AFTER_MOVE_PHASE:Short = 3
  val END_PHASE:Short = 4
}

object StepConst extends StepConst
trait StepConst {
  val DROP: Short = 1
  val MOVEUP: Short = 2
  val MOVEDOWN: Short = 3
  val MOVELEFT: Short = 4
  val MOVERIGHT: Short = 5
  val PICK: Short = 6
}

object ResultConst extends ResultConst
trait ResultConst {
  val RESULT_NOTHING: Short = 0
  val RESULT_FENSIVE_WIN: Short = 1
  val RESULT_DEFENSIVE_WIN: Short = 2
  val RESULT_NOBODY_WIN: Short = 3 //和局
  val RESULT_FLAG_ERR: Short = 101 //走棋方错误
  val RESULT_PHRASE_DROP: Short = 201 //方式错误，请落子
  val RESULT_PHRASE_MOVE: Short = 202 //方式错误，请移子
  val RESULT_PHRASE_PICK: Short = 203 //方式错误，请提子
  val RESULT_PHRASE_END: Short = 204 //已结束
  val RESULT_PHRASE_DROP_EXIST: Short = 301 //已有子
  val RESULT_PHRASE_MOVE_NOTSELF: Short = 302 //不是自己的子
  val RESULT_PHRASE_MOVE_NOTBLANK: Short = 303 //不是空地
  val RESULT_PHRASE_PICK_NOTOTHER: Short = 304 //不是对方的子
}
