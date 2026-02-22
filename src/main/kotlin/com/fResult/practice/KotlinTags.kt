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
  sealed interface HtmlElement

  // step 1
  data class Div(
    val children: List<HtmlElement>,
    val id: String? = null,
    val className: String? = null,
  ) : HtmlElement {
    override fun toString(): String {
      val idAttr = id?.let { " id=\"$it\"" } ?: ""
      val classAttr = className?.let { " class=\"$it\"" } ?: ""
      val innerHtml = children.joinToString("\n")

      return "<div$idAttr$classAttr>$innerHtml</div>"
    }
  }

  data class Body(val children: List<HtmlElement>) : HtmlElement {
    override fun toString(): String {
      return children.joinToString("\n", "<body>", "</body>")
    }
  }


  data class P(val content: String) : HtmlElement {
    override fun toString(): String {
      return "<p>$content</p>"
    }
  }
  // TODO: add data types for the rest of the HTML tags

  // step 2
  class DivBuilder(val id: String?, val className: String?) {
    private val children = mutableListOf<HtmlElement>()

    fun p(content: String) {
      children.add(P(content))
    }

    // expose a "build" method to give us back the final data structure
    fun build() = Div(children, id, className)
  }

  class BodyBuilder {
    private val children = mutableListOf<HtmlElement>()
    fun div(id: String? = null, className: String? = null, init: DivBuilder.() -> Unit) {
      children.add(KotlinTags.div(id, className, init))
    }

    fun p(content: String) {
      children.add(P(content))
    }

    fun build() = Body(children)
  }
  // TODO: add builders for the rest of the HTML tags: HtmlBuilder, HeadBuilder, and BodyBuilder

  // step 3
  fun div(id: String? = null, className: String? = null, init: DivBuilder.() -> Unit): Div {
    val builder = DivBuilder(id, className)
    builder.init()
    return builder.build()
  }

  fun body(init: BodyBuilder.() -> Unit): Body {
    val builder = BodyBuilder()
    builder.init()
    return builder.build()
  }
  // TODO: add methods for the DSL for the rest of the HTML tags (move some code around, because, `div` will be a part of BodyBuilder)

  // step 4
  @JvmStatic
  fun main(args: Array<String>) {

    val bodyExample = body {
      div("header", "main-header") {
        p("Welcome to my web site!")
      }
      div {
        p("This is the start of my site!")
        p("This was rendered with KotlinTags")
      }
    }
    println(bodyExample)
    // TODO: only expose the top-level DSL - just the html{} method needs to stay top level

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
  }
}
