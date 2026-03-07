package com.fResult.internals

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmName

object ReflectionBasics {
  // reflection = inspect and invoke functionality dynamically at runtime
  data class Person(val name: String, val age: Int) {
    var favoriteMovie: String? = "Forrest Gump"
    var phoneNumbers = emptyList<String>()

    fun fillInTaxForm(authority: String) =
      "[$name] Death and taxes... Filing my tax form for $authority"

    constructor(name: String) : this(name, 0) // secondary constructor
  }

  /*
   * UI
   *   select method:
   *     - fillInTaxForm
   *     - downloadPersonalData
   *     ... (parse as a String)
   */
  val personClass: KClass<Person> = Person::class

  @JvmStatic
  fun main(args: Array<String>) {
    // class reference => class name, methods, properties, ...
    println("------------- Class basic info -------------")
    println("Class name: ${personClass.simpleName}")
    println("Qualified name: ${personClass.qualifiedName}")
    println("JVM name: ${personClass.jvmName}")

    // class types
    println("Class is final?: ${personClass.isFinal}")
    println("Class is abstract?: ${personClass.isAbstract}")
    println("Class is sealed?: ${personClass.isSealed}")
    println("Class is open?: ${personClass.isOpen}")
    println("Class is data?: ${personClass.isData}")
    println("Class is inner?: ${personClass.isInner}")
    println("Class is companion?: ${personClass.isCompanion}")
    println("Class is instance of Any ${personClass.isInstance(Any::class)}")

    // visibility
    println("access modifier: ${personClass.visibility}")

    // inspect properties at runtime
    val classProperties = personClass.declaredMemberProperties
    println("------------- Class properties -------------")
    classProperties.forEach { prop ->
      println(
        "Name: ${prop.name}, Type: ${prop.returnType.classifier}, Is Nullable: ${prop.returnType.isMarkedNullable}"
      )
    }

    // refer to a particular properties (at RUNTIME) on an instance
    val korn = Person("Korn", 22)
    println("------------- Korn's properties -------------")
    val kornProperties = classProperties.map { prop ->
      "${prop.name} -> ${prop.call(korn)}" // korn.$prop
    }
    kornProperties.forEach(::println)

    // can mutate properties dynamically
    println("Before mutation: ${korn.favoriteMovie}")
    val favMovieProp = classProperties.find { it.name.lowercase().contains("movie") }
    favMovieProp?.let {
      if (it is KMutableProperty<*>)
        it.setter.call(korn, "Shawshank Redemption")
    }
    println("After mutation: ${korn.favoriteMovie}")

    // inspect functions
    println("------------- Class functions -------------")
    val functions = personClass.declaredFunctions
    functions.forEach { fn ->
      val fnName = fn.name
      val params = fn.parameters
      val returnType = fn.returnType
      println(
        "Function $fnName: (${
          params.joinToString(", ") {
            it.type.toString().split(".").last()
          }
        }) -> $returnType"
      )
    }

    // call a function
    // contains tax
    val taxFunc = functions.find { it.name.lowercase().contains("tax") }
    taxFunc?.call(korn, "THE ROYALTY").also(::println)

    // inspect and invoke constructor
    println("------------- Class constructors -------------")
    val primaryConstructor = personClass.primaryConstructor
    primaryConstructor?.also {
      // dynamically instantiate instances
      val newPerson = it.call("Jane", 99)
      println("New person instantiated: $newPerson")

      // can invoke functions with a map with all the args
      val params = it.parameters
      val newPersonV2 = it.callBy(
        mapOf(params[0] to "John", params[1] to 999)
      )
      println("New person instantiated via parameter map: $newPersonV2")
    }
  }
}
