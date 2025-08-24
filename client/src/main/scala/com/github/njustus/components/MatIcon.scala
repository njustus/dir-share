package com.github.njustus.components

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom.HTMLSpanElement

object MatIcon {
  def apply(iconName: String): ReactiveHtmlElement[HTMLSpanElement] = render(iconName)

  private def render(iconName: String): ReactiveHtmlElement[HTMLSpanElement] =
    span(className := "material-symbols-outlined", iconName)
}
