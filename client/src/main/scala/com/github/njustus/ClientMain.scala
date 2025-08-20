package com.github.njustus

import com.raquo.laminar.api.L.*
import org.scalajs.dom
import sttp.client4.*
import sttp.client4.fetch.FetchBackend
import sttp.model.Uri
import sttp.tapir.client.sttp4.SttpClientInterpreter

import scala.concurrent.ExecutionContext.Implicits.global

object ClientMain extends com.softwaremill.FilesEndpoints {

  def main(args: Array[String]): Unit = {
    Var("Loading…")
    val contentVar = Var[List[FileEntry]](List.empty)

    val basePath = uri"${dom.window.location.origin}"
    val backend = FetchBackend()
    SttpClientInterpreter()
      .toRequestThrowErrors(listEndpoint, Some(basePath))
      .apply("Users/nico/Downloads".split("/").toList)
      .send(backend)
      .foreach { response =>
        contentVar.set(response.body)
      }

    val app = div(
      className := "p-6",
      h1("Scala 3 + Tapir + Laminar Full‑Stack"),
      ul(
        className := "list-inside list-disc",
        children <-- contentVar.toObservable.map { list =>
          list.map { entry =>
            li(span(className := "font-semibold", entry.`type`.toString), " - ", entry.path, " == ", entry.contentType)
          }
        }
      )
    )

    render(dom.document.getElementById("appContainer"), app)
  }
}
