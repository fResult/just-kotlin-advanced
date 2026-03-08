package com.fResult.internals

import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

object ReflectionTypes {
  // example: avoid/circumvent type erasure

  // this cannot be done in Kotlin
  // fun processList(list: List<*>) = when (list) {
  //   is List<String> -> list.forEach(::println)
  //   is List<Int> -> list.sum().also(::println)
  //   else -> println("Not supported")
  // }

  // this is useless
  // fun processList(list: List<*>) = when (list) {
  //   is List<*> -> list.forEach(::println)
  // }

  fun processList(list: List<*>, type: KType) {
    val listOfStringType = typeOf<List<String>>()
    val listOfIntType = typeOf<List<Int>>()
    if (type.isSubtypeOf(listOfStringType)) {
      println("Processing a list of strings")
      list.forEach(::println)
    } else if (type.isSubtypeOf(listOfIntType)) {
      println("Process a list of ints")
      @Suppress("UNCHECKED_CAST")
      (list as List<Int>).sum().also(::println)
    } else {
      println("Not supported")
    }
  }

  @JvmStatic
  fun main(args: Array<String>) {
    val myList: List<*> = listOf(1, 2, 3, 4, 5)
    processList(listOf(1, 2, 3), typeOf<List<Int>>())
    processList(myList, typeOf<List<Int>>())
  }
}
