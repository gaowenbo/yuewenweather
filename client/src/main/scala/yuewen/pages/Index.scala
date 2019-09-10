package yuewen.pages

import wxapplib.{MiniPage, console, wx}
import yuewen.GlobalData
import yuewen.model.{DayWeather, WeatherData, WeatherTip}

import scala.collection.immutable.IntMap.Tip
import scala.scalajs.js
import scala.scalajs.js.Dictionary
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("dddddddd")
class Index extends MiniPage{
  var data = new IndexData(null)
  val defaultFailFunction = (res: js.Dynamic) => {console.log(res)}

  override def onLoad(): Unit = {
    val getWeather = (longitude: Double, latitude: Double) => wx.request("https://yueyiwenhua.cn/getWeather", "get",
      js.Dictionary("content-type" -> "application/json"),
      js.Dictionary("longitude" -> 119.5,"latitude" -> 40),
      (res: js.Dynamic) =>
        transferWeather(res.data) match {
          case None =>
          case Some(weather) =>
            GlobalData.weatherData = weather
            data.weatherData = GlobalData.weatherData
            setData(data)
            console.log(data)
        }
    )

    wx.getLocation(success = (res: js.Dynamic) => getWeather(res.latitude.asInstanceOf[Double], res.longitude.asInstanceOf[Double]),
      fail = defaultFailFunction)
  }

  def transferWeather(data: js.Dynamic): Option[WeatherData] = if (data.error.asInstanceOf[Int] == 0) {
      val date = data.date.asInstanceOf[String]
      val result = data.results.asInstanceOf[js.Array[js.Dynamic]].last

      val pm25 = result.pm25.asInstanceOf[String]
      val currentCity = result.currentCity.asInstanceOf[String]
      import js.JSConverters._
      val tipList = result.index.asInstanceOf[js.Array[js.Dynamic]].map {
        i => new WeatherTip(i.des.asInstanceOf[String], i.tipt.asInstanceOf[String], i.title.asInstanceOf[String], i.zs.asInstanceOf[String])
      }.toJSArray
      val dayWeathers = result.weather_data.asInstanceOf[js.Array[js.Dynamic]].map {
        i => new DayWeather(i.date.asInstanceOf[String], "", "", i.weather.asInstanceOf[String], i.wind.asInstanceOf[String], i.temperature.asInstanceOf[String])
      }.toJSArray

      Option(new WeatherData(date, currentCity, pm25, tipList, dayWeathers))
    } else {
      None
    }

  class IndexData(var weatherData: WeatherData) extends js.Object
}

