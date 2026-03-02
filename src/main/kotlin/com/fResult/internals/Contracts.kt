package com.fResult.internals

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.random.Random

object Contracts {
  // contracts = code that helps the compiler make some deductions

  // info to the compiler about the return value of a function
  @OptIn(ExperimentalContracts::class)
  fun containsJustDigits(str: String?): Boolean {
    contract {
      // small DSL for testing things about this function
      returns(true) implies (str != null)
    }
    return if (str == "" || str == null) false
    else str.all { it.isDigit() }
  }

  fun demoNullableString() {
    val maybeString =
      if (Random.nextBoolean()) "123456"
      else null

    println("I has maybe string $maybeString")
    if (containsJustDigits(maybeString)) { // containsJustDigits(...) == true AND maybeString != null
      println("String is just a number, I want the length: ${maybeString.length}")
    }
  }

  @JvmStatic
  fun main(args: Array<String>) {
    demoNullableString()
  }
}
