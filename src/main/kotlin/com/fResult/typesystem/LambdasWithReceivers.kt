package com.fResult.typesystem

object LambdasWithReceivers {
  // create a behavior
  // option 1 - OO way
  data class Person(
    val name: String,
    val age: Int,
  ) {
    fun greet() = "hi, I'm $name"
  }

  // option 2 (procedure way) - create a function that takes a person
  fun greet(person: Person) = "hi, I'm ${person.name}"

  // option 3 - extension function (Kotlin/Scala
  fun Person.greetExt() =
    // ^^^^ RECEIVER type -> give us access to the `this` reference
    "hi, I'm $name"

  // option 4 - function value (lambda
  val greetFun: (Person) -> String = { person -> "hi, I'm ${person.name}" }

  // option 5 - lambda with receiver (an extension lambda)
  val greetFunWithReceiver: Person.() -> String = { "hi, I'm $name" }
  //                        ^^^^^^ RECEIVER type -> give us access to the `this` reference
  // val simpleLambda: () -> String = { "Kotlin" }

  // APIs that look "baked into Kotlin" aka DSL
  // examples: Ktor, Arrow, Gradle Config, Kotlin coroutines

  // Mini-"library" for JSON serialization
  // support numbers, strings, JSON objects
  sealed interface JsonValue

  data class JsonNumber(
    val value: Int,
  ) : JsonValue {
    override fun toString() = value.toString()
  }

  data class JsonString(
    val value: String,
  ) : JsonValue {
    override fun toString() = "\"$value\""
  }

  data class JsonObject(
    val attributes: Map<String, JsonValue>,
  ) : JsonValue {
    override fun toString() =
      attributes.toList().joinToString(", ", "{", "}") { (key, value) ->
        "\"$key\": $value"
      }
  }

  // "mutable builder" of a JsonObject
  class JsonBuilder {
    private var props: MutableMap<String, JsonValue> = mutableMapOf()

    fun build() = JsonObject(props.toMap())

    // not-so-nice API
    fun addString(
      name: String,
      value: String,
    ) {
      props[name] = JsonString(value)
    }

    fun addNumber(
      name: String,
      value: Int,
    ) {
      props[name] = JsonNumber(value)
    }

    fun addValue(
      name: String,
      value: JsonValue,
    ) {
      props[name] = value
    }

    // nice API
    infix fun String.to(value: String) {
      props[this] = JsonString(value)
    }

    infix fun String.to(value: Int) {
      props[this] = JsonNumber(value)
    }

    infix fun String.to(value: JsonValue) {
      props[this] = value
    }
  }

  fun buildJsonButNotSoNice(init: (JsonBuilder) -> Unit): JsonValue {
    val obj = JsonBuilder()
    init(obj)
    return obj.build()
  }

  fun buildJson(init: JsonBuilder.() -> Unit): JsonValue {
    val obj = JsonBuilder()
    obj.init()
    return obj.build()
  }

  @JvmStatic
  fun main(args: Array<String>) {
    val jsonObjectV1 =
      JsonObject(
        mapOf(
          "user" to
            JsonObject(
              mapOf(
                "name" to JsonString("fResult"),
                "age" to JsonNumber(20),
              ),
            ),
          "credentials" to
            JsonObject(
              mapOf(
                "type" to JsonString("password"),
                "value" to JsonString("secret"),
              ),
            ),
        ),
      )
    println(jsonObjectV1)

    val jsonObjectV2 =
      buildJsonButNotSoNice { json ->
        json.addValue(
          "user",
          buildJsonButNotSoNice { json2 ->
            json2.addString("name", "fResult")
            json2.addNumber("age", 20)
          },
        )
        json.addValue(
          "credentials",
          buildJsonButNotSoNice { json2 ->
            json2.addString("type", "password")
            json2.addString("value", "secret")
          },
        )
      }
    println(jsonObjectV2)

    val jsonObjectV3 =
      buildJson {
        "user" to
          buildJson {
            "name" to "fResult"
            "age" to 20
          }
        "credentials" to
          buildJson {
            "type" to "password"
            "value" to "secret"
          }
      }
    println(jsonObjectV3)
  }

  val smallerJson =
    buildJson {
      "name" to "fResult"
      "age" to 20
    }
}
