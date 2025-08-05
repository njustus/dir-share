package com.github.njustus

import com.raquo.laminar.api.L._
import org.scalajs.dom

object Main {
  def main(args: Array[String]): Unit = {
    val message = Var("Loading…")

    val app = div(h1("Scala 3 + Tapir + Laminar Full‑Stack"), p(child.text <-- message.signal))

    render(dom.document.getElementById("appContainer"), app)
  }
}
