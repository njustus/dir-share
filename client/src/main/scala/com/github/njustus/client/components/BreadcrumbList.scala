package com.github.njustus.client.components

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom.*

object BreadcrumbList {
  def render(paths: Seq[String]) = {
    val liItems = paths.scanLeft("/") {
      (parent, currentDir) => s"$parent/$currentDir"
    }.map { (fullPath) =>
      val parts = fullPath.split("/")
      li(a(
        href := s"/listing$fullPath",
        MatIcon("folder"),
        if(parts.isEmpty) "Home" else parts.last
      ))
    }

    div(
      className := "breadcrumbs text-sm",
      ul(
        liItems
      )
    )
  }
}
