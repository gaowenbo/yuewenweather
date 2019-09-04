package views

import java.util.Date

import controllers.routes

object ChessHtml {
    def getHtml =
      {
//        var js = scalajs.html.scripts("client", routes.Assets.versioned(_).toString + "?" + new Date().getTime.toString, name => getClass.getResource(s"/public/$name") != null)
        "<!DOCTYPE html>" +
          s"""<html>
      <body>
        <div class="container-fluid">
        <div class="row">
          <div class="col-md-12">
            <div id = "title" class="page-header" align="center">
              <h1>
                五虎棋
              </h1>
            </div>
          </div>
        </div>
        <div id = "board"></div>
        <div class="row">
          <div class="col-md-12" align="center">
            <canvas id = "canvas" width="800" height="800">

            </canvas>
            <div id = "boardd"></div>
          </div>
          <div class="col-md-12">
            <div class="row">
              <div class="col-md-12">
                <h2 id="flag">

                </h2>
              </div>
            </div>
            <div class="row">
              <div class="col-md-12">
              <h2 id="goal">

                </h2>
              </div>
            </div>
            <div class="row">
              <div class="col-md-12">
              <h2 id="phase">
                </h2>
              </div>
            </div>
            <div class="row">
              <div class="col-md-12">
              <h2 id="result">

                </h2>
              </div>
            </div>
          </div>
        </div>
      </div>

      </body>
      </html>"""
      }

}
