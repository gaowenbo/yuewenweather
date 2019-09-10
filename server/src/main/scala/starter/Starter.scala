package scalajs.starter

import io.scalajs.RawOptions
import io.scalajs.nodejs.buffer.Buffer
import io.scalajs.nodejs.fs.Fs

import scala.scalajs.js
import io.scalajs.nodejs.http.{ClientRequest, ServerResponse, _}
import io.scalajs.nodejs.https.{Https, ServerOptions}
import io.scalajs.nodejs.{console, process, setTimeout}
import io.scalajs.nodejs.querystring.QueryString
import io.scalajs.nodejs.url.URL

object Starter extends App {
  process.on("uncaughtException", (err: js.Dynamic) => {
    //打印出错误
    console.log(err)
    //打印出错误的调用栈方便调试
    console.log(err.stack)
  });

  var handler = (req: IncomingMessage, res: ServerResponse) => {
    val parse = URL.parse(req.url, true)
    val pathname = parse.pathname.get
    if (pathname.equals("/getWeather")) {
      val params = QueryString.parse(parse.search.getOrElse(""))
      val longitude = params.getOrElse("longitude", "0").toDouble
      val latitude = params.getOrElse("latitude", "0").toDouble
      res.writeHead(200, js.Dictionary(
        "content-type" -> "text/plain"
      ));


      var getWeatherReq = Http.request(s"http://api.map.baidu.com/telematics/v3/weather?location=$longitude,$latitude&output=json&ak=3253dd1e249d58370f8f9dc825099e4d",
        (weatherRes: IncomingMessage) => {
          weatherRes.on("data", (data: Buffer) => {
            res.write(data + "")
          })
          weatherRes.on("end", () => {
            res.end()
          }
          )
        }
      )
      getWeatherReq.end()
    } else if (pathname.startsWith("/assets")) {
      res.writeHead(200, "OK")
      val readStream = Fs.createReadStream("server/dist/" + pathname.substring(7))

      readStream.pipe(res)

    } else {
      setTimeout(() => {
        res.writeHead(200, js.Dictionary(
          "content-type" -> "text/plain"
        ));
        res.write("hello nodejs");
        res.end();
      }, 2000);
    }
  }

  var options = new ServerOptions()

  var https = Https.createServer(
    js.Dictionary("key" -> Fs.readFileSync("server/key/2515456_yueyiwenhua.cn.key"),
      "cert" -> Fs.readFileSync("server/key/2515456_yueyiwenhua.cn.pem"),
      "ca" -> Fs.readFileSync("server/key/2515456_yueyiwenhua.cn.pem")
    ), handler)
  https.listen(443)
  var http = Http.createServer(handler)
  http.listen(80)
}


