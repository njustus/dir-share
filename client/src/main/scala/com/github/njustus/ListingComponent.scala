package com.github.njustus

import com.raquo.laminar.api.L.*
import com.softwaremill.FilesEndpoints.{FileEntry, FileType}
import com.softwaremill.FilesEndpoints.FileType.File
import sttp.tapir.client.sttp4.SttpClientInterpreter

import scala.concurrent.ExecutionContext

class ListingComponent(listingClient: ListEndpointsClient)(using ExecutionContext) {
  def render(paths: Seq[String]) = {
    val path = paths.mkString("/")
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
              li(a(className := "font-semibold",  href := s"/listing${entry.path}", entry.name))
          }
        }
      )
    )
  }
}
