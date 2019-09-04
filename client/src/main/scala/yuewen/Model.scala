package yuewen.model

import scala.scalajs.js

class WeatherData(var date: String, var currentCity: String, var pm25: String, var tips: js.Array[WeatherTip], var dayWeathers: js.Array[DayWeather]) extends js.Object

class WeatherTip(var des: String, var tipt: String, var title: String, var zs: String) extends js.Object

class DayWeather(var date: String, var dayPic: String, var nightPic: String, var weather: String, var wind: String, var temperature: String) extends js.Object