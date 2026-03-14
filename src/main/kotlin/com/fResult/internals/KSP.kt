@file:Suppress("ktlint:standard:no-consecutive-comments")

package com.fResult.internals

import com.fResult.com.fResult.builderKsp.Builder

// Use-case: generate builder patterns for data classes
@Builder
data class Person(
  val name: String,
  val age: Int,
)

@Builder
data class Money(
  val amount: Double,
  val currency: String,
)

object KSP {
  /*
   * - analyze  source code and generate
   * - new source code
   * - compile
   * -access methods/functionality at COMPILE TIME
   */

  /*
   * PersonsBuilder
   *   .name("Korn")
   *   .age(99)
   *   .property(value)
   *   ...
   *   .build()
   */

  // module 1 - symbol definition (annotations)
  // module 2 - KSP logic for generating the source
  // module 3 - source + the place where the generated source will be created

  @JvmStatic
  fun main(args: Array<String>) {
    Person("Korn", 99).also(printWithLabel("By Person"))
    PersonBuilder()
      .age(22)
      .name("Korn")
      .build()
      .also(printWithLabel("By PersonBuilder"))

    Money(100.0, "USD").also(printWithLabel("By Money"))
    MoneyBuilder()
      .amount(100.0)
      .currency("USD")
      .build()
      .also(printWithLabel("By MoneyBuilder"))
  }

  fun <T> printWithLabel(label: String): (T) -> Unit = { println("$label: $it") }
}
