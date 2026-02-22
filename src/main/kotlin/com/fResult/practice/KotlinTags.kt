package com.fResult.practice

import java.io.File
import java.io.FileWriter
import java.io.PrintWriter

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
  data class Html(val head: Head, val body: Body) : HtmlElement {
    override fun toString() = "<!doctype>\n<html lang=\"en\">\n$head\n$body\n</html>"
  }

  data class Head(val title: Title) : HtmlElement {
    override fun toString() = "<head>\n$title\n</head>"
  }

  data class Title(val content: String) : HtmlElement {
    override fun toString() = "<title>$content</title>"
  }

  data class Body(val children: List<HtmlElement>) : HtmlElement {
    override fun toString(): String {
      return children.joinToString("\n", "<body>\n", "\n</body>")
    }
  }

  data class Div(
    val children: List<HtmlElement>,
    val id: String? = null,
    val className: String? = null,
  ) : HtmlElement {
    override fun toString(): String {
      val idAttr = id?.let { " id=\"$it\"" } ?: ""
      val classAttr = className?.let { " class=\"$it\"" } ?: ""
      val innerHtml = children.joinToString("\n")

      return "<div$idAttr$classAttr>\n$innerHtml\n</div>"
    }
  }

  data class P(val content: String) : HtmlElement {
    override fun toString() = "<p>$content</p>"
  }
  // TODO: add data types for the rest of the HTML tags

  // step 2
  class HtmlBuilder {
    private lateinit var head: Head
    private lateinit var body: Body

    fun head(init: HeadBuilder.() -> Unit) {
      head = KotlinTags.head(init)
    }

    fun body(init: BodyBuilder.() -> Unit) {
      body = KotlinTags.body(init)
    }

    fun build() = Html(head, body)
  }

  class HeadBuilder {
    private lateinit var title: Title

    fun title(content: String) {
      title = Title(content)
    }

    fun build() = Head(title)
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

  class DivBuilder(val id: String?, val className: String?) {
    private val children = mutableListOf<HtmlElement>()

    fun p(content: String) {
      children.add(P(content))
    }

    // expose a "build" method to give us back the final data structure
    fun build() = Div(children, id, className)
  }
  // TODO: add builders for the rest of the HTML tags: HtmlBuilder, HeadBuilder, and BodyBuilder

  // step 3
  fun html(init: HtmlBuilder.() -> Unit): Html {
    val builder = HtmlBuilder()
    builder.init()
    return builder.build()
  }

  fun head(init: HeadBuilder.() -> Unit): Head {
    val builder = HeadBuilder()
    builder.init()
    return builder.build()
  }

  fun body(init: BodyBuilder.() -> Unit): Body {
    val builder = BodyBuilder()
    builder.init()
    return builder.build()
  }

  fun div(id: String? = null, className: String? = null, init: DivBuilder.() -> Unit): Div {
    val builder = DivBuilder(id, className)
    builder.init()
    return builder.build()
  }

  val htmlExample = html {
    head {
      title("My Web Page")
    }
    body {
      p("This is my first paragraph!")
      div(id = "header", className = "main-header") {
        p("Welcome to my web site!")
      }
      div {
        p("This is the start of my site!")
        p("This was rendered with KotlinTags")
      }
    }
  }

  // step 4
  @JvmStatic
  fun main(args: Array<String>) {
    val printWriter = PrintWriter(FileWriter(File("src/main/resources/sample.html")))
    println(htmlExample)
    printWriter.println(htmlExample)
    printWriter.close()
  }
}
