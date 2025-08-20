package com.github.njustus

import com.raquo.laminar.api.L.*
import com.softwaremill.FilesEndpoints.FileEntry
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
          list.map { entry =>
            li(span(className := "font-semibold", entry.`type`.toString), " - ", entry.path, " == ", entry.contentType)
          }
        }
      )
    )
  }
}
