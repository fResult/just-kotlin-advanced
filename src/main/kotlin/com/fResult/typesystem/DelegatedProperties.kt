package com.fResult.typesystem

import kotlin.reflect.KProperty

object DelegatedProperties {
  // access (get/set) properties and trigger side effect

  class LoggingClassNaive(val id: Int) {
    var property: Int = 0
      get() {
        // logging of the change of value
        println("[logging $id] getting property")
        return field
      }
      set(value) {
        println("[logging $id] setting property to new value $value")
        field = value
      }
  }

  fun demoNaiveLogger() {
    val logger = LoggingClassNaive(42)
    logger.property = 2
    val x = logger.property // getting
    println(x)
    logger.property = 3
    println(logger.property)
  }

  // delegate properties
  class LoggingProp<A>(val id: String, val default: A) {
    var property: A = default

    operator fun getValue(currentRef: Any, prop: KProperty<*>): A {
      // logging of the change of value
      println("[logging $id] getting property")
      return property
    }

    operator fun setValue(currentRef: Any, prop: KProperty<*>, value: A) {
      println("[logging $id] setting property to new value $value")
      property = value
    }
  }

  class LoggingClass(val id: Int) {
    var intProperty: Int by LoggingProp("$id-intProperty", 0) // <-- delegated property
    var stringProperty: String by LoggingProp(
      "$id-stringProperty",
      "Hello"
    ) // same behavior, reused!
  }

  fun demoLogger() {
    val loggingClass = LoggingClass(42)
    loggingClass.intProperty = 34
    val x = loggingClass.intProperty
    println(x)

    println(loggingClass.stringProperty)
    loggingClass.stringProperty = "Good bye"
    println(loggingClass.stringProperty)
  }

  // how delegates work
  class LoggingClassV2(id: Int) {
    var myProperty: Int by LoggingProp("$id-myProperty", 0)
  }

  class LoggingClassV2Expanded(id: Int) {
    private var propDelegate = LoggingProp("$id-myProperty", 0)
    var myProperty: Int
      get() = propDelegate.getValue(this, ::propDelegate)
      //                                  ^^^^^^^^^^^^ reflective call
      set(value) {
        propDelegate.setValue(this, ::propDelegate, value)
        //                          ^^^^^^^^^^^^ reflective reference
      }
  }

  fun demoLoggerV2() {
    val loggingClass = LoggingClassV2(42)
    loggingClass.myProperty = 34
    val x = loggingClass.myProperty
    println(x)

    val loggingClassV2Expanded = LoggingClassV2Expanded(42)
    loggingClassV2Expanded.myProperty = 34
    val y = loggingClassV2Expanded.myProperty
    println(y)
  }

  // Exercise: implement a class Delayed
  class Delayed<A>(private val func: () -> A) {
    // DONE TODO: add a variable "content" which is a nullable A, starting at null
    private var content: A? = null

    operator fun getValue(currentRef: Any, prop: KProperty<*>): A {
      // DONE TODO: check if the content is null, and if not, invoke the `func` constructor arg
      //       and return the content
      if (content == null) {
        content = func()
      }

      return content!!
    }
  }

  // DONE TODO: use it and find out what it means
  // Lazy Evaluation = variable is not set until first use
  class DelayedClass {
    val intDelayed: Int by Delayed { // usage as delegated property
      println("I'm setting up via Int!")
      42
    }

    val stringDelayed: String by Delayed { // usage as delegated property
      println("I'm setting up via String!")
      "Hello, world!"
    }
  }

  fun demoDelayed() {
    val delayed = DelayedClass()
    println(delayed.intDelayed)
    println(delayed.stringDelayed)
    println(delayed.intDelayed)
    println(delayed.stringDelayed)
  }

  @JvmStatic
  fun main(args: Array<String>) {
    // demoNaiveLogger()
    // demoLogger()
    // demoLoggerV2()
    demoDelayed()
  }
}
