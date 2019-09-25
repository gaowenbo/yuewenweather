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
    val parse = URL.parse(req.url, false)
    val pathname = parse.pathname.get
    if (pathname.equals("/getWeather")) {
      val params = QueryString.parse(parse.query.getOrElse(""))
      val longitude = params.getOrElse("longitude", "0").toDouble
      val latitude = params.getOrElse("latitude", "0").toDouble
      res.writeHead(200, js.Dictionary(
        "content-type" -> "application/json;charset=utf-8"
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

    } else if (pathname.startsWith("/check")) {
      val params = QueryString.parse(parse.query.getOrElse(""))
      val signature = params.get("signature").getOrElse("")
      val echostr = params.get("echostr").getOrElse("")
      val timestamp = params.get("timestamp").getOrElse("")
      val nonce = params.get("nonce").getOrElse("")

//      const signature = query.signature;
//      const echostr = query.echostr;
//      const timestamp = query.timestamp;
//      const nonce = query.nonce;
//
//      // 拼成数组，字典排序，再拼接
//      const tmpStr = [TOKEN, timestamp, nonce].sort().reduce((prev, cur) => prev + cur);
//
//      // sha1加密
//      const sha1 = crypto.createHash('sha1');
//      const sha1_result = sha1.update(tmpStr).digest('hex');
//
//      // 如果是来自微信的请求就返回echostr
//      if (sha1_result === signature) {
//        res.end(echostr);
//      }

      res.writeHead(200, js.Dictionary(
        "content-type" -> "text/plain"
      ));
      res.write(echostr);
      res.end();
    } else {
      setTimeout(() => {
        res.writeHead(200, js.Dictionary(
          "content-type" -> "text/plain"
        ));
        res.write("hello nodejs");
        res.end();
      }, 500);
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


