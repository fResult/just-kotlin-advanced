package com.fResult.internals

import java.io.File
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
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

  inline fun <reified T> processList(list: List<T>) {
    processList(list, typeOf<List<T>>())
  }

  // demonstrate a flawed attempt at getting generic type arguments
  fun processListV2(list: List<*>) {
    val typeParams = list::class.typeParameters.map { it.name }
    println("Type arguments: $typeParams")
    if (typeParams.contains("String")) {
      println("Processing list of Strings")
    } else if (typeParams.contains("Int")) {
      println("Processing list of Ints")
    } else {
      println("Not supported")
    }
  }

  // more complex example
  // parsing config files
  /*
   * example conf file
   * host = localhost
   * port = 8800
   * debug = true
   * maxConnections = 100
   * timeout = 3.2
   */
  data class MyConfig(
    val host: String,
    val port: Int,
    val debug: Boolean,
    val maxConnections: Int,
    val timeout: Double,
  )

  // Config.load(...).convertTo<Config>

  @JvmStatic
  fun main(args: Array<String>) {
//    val myList: List<*> = listOf(1, 2, 3, 4, 5)
//    processList(listOf(1, 2, 3), typeOf<List<Int>>())
//    processList(listOf("One", "Two", "Three"))
//    processList(myList, typeOf<List<Int>>())
//    processList(myList)

//    processListV2(listOf("One", "Two", "Three"))

    val config = ConfigLoader.default().loadAs<MyConfig>()
    println("Deserialized config: $config")
    val timeout = config.timeout // now, it's type-safe
    println("Configured Timeout is $timeout seconds")
  }
}

class ConfigLoader private constructor(val path: String = "src/main/resources/application.conf") {
  fun parseFile(): Map<String, String> {
    val file = File(path)
    val configMap = mutableMapOf<String, String>()
    file.forEachLine { line ->
      val trimmedLine = line.trim()
      if (trimmedLine.isNotEmpty() && !trimmedLine.startsWith("#")) {
        val (key, value) = trimmedLine.split("=").map(String::trim)
        configMap[key] = value
      }
    }
    return configMap
  }

  fun deserializeValue(value: String, type: KType): Any = when (type.classifier) { // KType.classifier -> KClassifier (supertype of KClass)
    String::class -> value
    Int::class -> value.toInt()
    Double::class -> value.toDouble()
    Boolean::class -> value.toBoolean()
    else -> throw IllegalArgumentException("Unsupported type: $type")
  }

  inline fun <reified T> deserializeObject(props: Map<String, String>): T {
    // KClass<T> to be able to build an instance of T
    val kClass = T::class
    val constructor = kClass.constructors.firstOrNull()
      ?: throw IllegalArgumentException("Type ${kClass.simpleName} doesn't have an accessible primary constructork")
    val args: Map<KParameter, Any> = constructor.parameters.associateWith { param ->
      val key = param.name
        ?: throw IllegalArgumentException("Unname constructor param for ${kClass.simpleName}")
      val value = props[key]
        ?: throw IllegalArgumentException("Missing value for the constructor param ${param.name} in class ${kClass.simpleName}")
      deserializeValue(value, param.type)
    }

    return constructor.callBy(args)
  }

  companion object {
    fun default() = ConfigLoader()
    fun at(path: String) = ConfigLoader(path)
  }
  inline fun <reified T : Any> loadAs(): T {
    val props = parseFile()
    return deserializeObject<T>(props)
  }
}
