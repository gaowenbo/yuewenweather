package wxapplib


import scala.scalajs.js
import js.Dynamic._
import scala.scalajs.js.Dictionary
import scala.scalajs.js.annotation.JSGlobal

object wx {
  import js.JSConverters._
  def openSetting(success: js.Function = null) = global.wx.openSetting(
    Map(
      "success" -> success
    ).filter(_._2 != null).toJSDictionary
  )

  def getLocation(`type`: String = "wgs84", altitude: Boolean = false, success: js.Function = null, fail: js.Function = null, complete: js.Function = null) = global.wx.getLocation(
    Map(
    "type" -> `type`,
    "altitude" -> altitude,
    "success" -> success,
    "fail" -> fail,
    "complete" -> complete).filter(_._2 != null).toJSDictionary
  )
  def canIUse(name: String): Boolean = global.wx.canIUse(name).asInstanceOf[Boolean]
  def showToast(title: String = null, icon: String = "success", image: String = null, duration: Int = 1500, mask: String = null, success: js.Function = null, fail: js.Function = null, complete: js.Function = null): Unit = {

    val params = Map(
      "title" -> title,
      "icon" -> icon,
      "image" -> image,
      "duration" -> duration,
      "mask" -> mask,
      "success" -> success,
      "fail" -> fail,
      "complete" -> complete
    ).filter(_._2 != null).toJSDictionary
    global.wx.showToast(params)
  }
  def request(url: String = null, method: String = null, header: Dictionary[js.Any] = null, data: Dictionary[js.Any] = null, success: js.Function = null, fail: js.Function = null): Unit = {
    import js.JSConverters._
    var params = Map(
      "url" -> url,
      "method" -> method,
      "header" -> header,
      "data" -> data,
      "success" -> success,
      "fail" -> fail
    ).filter(_._2 != null).toJSDictionary
    global.wx.request(params)
  }
}

object console {
  def log(any: js.Any): Unit = global.console.log(any)
}

class MiniApp {

  val app = global.App(js.Dictionary(
    "onLaunch" -> ({() => onLaunch}:js.Function)
  ))

  def onLaunch(): Unit = {

  }
}


class MiniPage {
  var page: js.Dynamic = {
    global.Page(js.Dictionary(
      "onLoad" -> ({(thisPage: js.Dynamic) =>
        page = thisPage
        onLoad
      }:js.ThisFunction)
    ))
  }

  def onLoad():Unit = {

  }

  def setData(jsObject: js.Object) = page.setData(jsObject)

  def setFunction(key: String, value: js.Function) = page.updateDynamic(key)(value)
}

object Page {
//  type Page = nativelib.Page
//  def apply(data: js.Any = null, onLoad: js.Function = null, otherProperties: Map[String, js.Any] = Map()): Page = {
//    var getThisPage:js.ThisFunction = {(thisPage: Page) =>
//      thisPage
//    }
//    var params = Array(
//      "data" -> data,
//      "onLoad" -> onLoad,
//      "getThisPage" -> getThisPage
//    ).filter(_._2 != null) ++ otherProperties.toArray[(String, js.Any)]
//    nativelib.Page(js.special.objectLiteral(params:_*).asInstanceOf[js.Object with js.Dynamic])
//  }
//    def apply(data: js.Any = null, onLoad: js.Function = null, otherProperties: Map[String, js.Any] = Map()): Page = {
//      var getThisPage:js.ThisFunction = {(thisPage: Page) =>
//        thisPage
//      }
}

object getCurrentPages {
  def apply() = global.getCurrentPages()
}



