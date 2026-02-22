package com.fResult.practice

object KotlinTags {
  /*
   * HTML rendering library (Mimics ScalaTags)
   * KotlinTags
   *
   * 1. Define data types for the HTML tags we want to support
   *    - html, head, title, body, div, and p
   * 2. Define some "builders" that enable the DSL for every tag we want to support
   *    - HtmlBuilder, HeaderBuilder, BodyBuilder, DivBuilder
   * 3. Define methods that take lambdas with receivers as arguments -> build the DSL
   * 4. test that it works
   */

  // val document = html {
  //   head {
  //     title("My Web Page")
  //   }
  //   body {
  //     div(id = "header", className = "main-header") {
  //       p("Welcome to my web site!")
  //     }
  //     div {
  //       p("This is the start of my site!")
  //       p("This was rendered with KotlinTags")
  //     }
  //   }
  // }
  sealed interface HtmlElement
  data class Html(val children: List<HtmlElement>/*...*/) : HtmlElement {
    override fun toString() = "<html>${children.joinToString("")}</html>"
  }
}
