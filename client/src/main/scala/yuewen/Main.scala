package yuewen

import wxapplib._
import yuewen.pages.Index

import scala.scalajs.js
import scala.scalajs.js.Dynamic.global
import scala.scalajs.js.annotation.{JSExportTopLevel, JSGlobalScope}



object Main extends App {



  global.exports.app = global.app
  global.exports.index = global.index
  global.exports.settings = global.settings

  @JSExportTopLevel("app")
  def app() = new MiniApp().app

  @JSExportTopLevel("index")
  def index() = new Index().page

  @JSExportTopLevel("settings")
  def settings() = new MiniPage().page

}
