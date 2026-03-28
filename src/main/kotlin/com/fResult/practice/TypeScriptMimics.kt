@file:Suppress("ktlint:standard:max-line-length")

package com.fResult.practice

import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

object TypeScriptMimics {
  data class Person(
    val name: String,
    val age: Int,
    val pets: List<String>,
  )

  @Suppress("UNCHECKED_CAST")
  fun <T> getData(
    instance: Any,
    prop: KProperty<T>,
  ): T {
    val clazz = instance::class
    val callableProp = clazz.memberProperties.find { it.name == prop.name } as? KProperty1<Any, T>
    return callableProp?.get(instance) as T
  }

  @Suppress("UNCHECKED_CAST")
  fun <R : Any, T> updateInstance(
    instance: R,
    prop: KProperty<T>,
    value: T,
  ): R {
    val clazz = instance::class
    val constructor =
      clazz.primaryConstructor
        ?: throw IllegalArgumentException("${prop.name} has no primary constructor")

    val args =
      constructor.parameters.associateWith { param ->
        return@associateWith if (param.name == prop.name) {
          value
        } else {
          val foundProp =
            clazz.memberProperties.find {
              it.name == param.name
            } as? KProperty1<Any, *>
          foundProp?.get(instance) as? R
        }
      }

    return constructor.callBy(args)
  }

  @JvmStatic
  fun main(args: Array<String>) {
    val p1 = Person("Korn", 99, listOf("Auan"))
    val name = getData(p1, p1::name)
    val age = getData(p1, p1::age)
    val pets = getData(p1, p1::pets)

    println("Result name: $name")
    println("Result age: $age")
    println("Result pets: $pets")

    val updatedPersons = updateInstance(p1, p1::age, 35)
    println("Updated person: $updatedPersons")
  }
}
