package com.github.njustus

import com.raquo.laminar.api.L.*
import com.softwaremill.FilesEndpoints.{FileEntry, FileType}

import scala.concurrent.ExecutionContext
import org.scalajs.dom.HTMLDivElement
import com.raquo.laminar.nodes.ReactiveHtmlElement

class ListingComponent(listingClient: ListEndpointsClient)(using ExecutionContext) {
  def render(paths: Seq[String]): ReactiveHtmlElement[HTMLDivElement] = {
    val path       = paths.mkString("/")
    val contentVar = Var[List[FileEntry]](List.empty)

    listingClient.list(paths.toList).foreach { entries =>
      contentVar.set(entries)
    }

    div(
      className := "p-6",
      h1(s"Contents of: $path"),
      ul(
        className := "list-inside list-disc",
        children <-- contentVar.toObservable.map { list =>
          list.map {
            case entry if entry.`type` == FileType.File =>
              li(span(className := "font-semibold", entry.name, " == ", entry.contentType))
            case entry =>
              li(a(className := "font-semibold", href := s"/listing${entry.path}", entry.name))
          }
        }
      )
    )
  }
}
