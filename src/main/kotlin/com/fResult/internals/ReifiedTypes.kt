package com.fResult.internals

object ReifiedTypes {
  // doesn't work
  // fun <T> filterByType(list: List<Any>): List<T> =
  //   list.filter { it is T }.map { it as T }

  // JVM has type erasure
  // Generics were added in Java 5 (2004)
  // Java pre-5
  // List things = new ArrayList();
  // Java 5
  // List<String> things = new ArrayList<>();
  // type erasure
  // List things = new ArrayList()

  // solution is inline fun + reified type
  inline fun <reified T> List<Any>.filterByType(): List<T> =
    this.filter(T::class::isInstance).map(T::class.java::cast)

  data class Person(
    val name: String,
    val age: Int,
  )

  data class Car(
    val make: String,
    val model: String,
  )

  fun demoReifiedType() {
    val mixedList =
      listOf(
        Person("John", 30),
        Car("Toyota", "Corolla"),
        Person("Jane", 25),
        Car("Honda", "Accord"),
        "A random string",
        42,
        "Another random string",
      )

    // These will be legal since they can be performed at runtime
    val people = mixedList.filterByType<Person>()
    // -> Rewritten to mixedList.filter(Person::class::isInstance).map(Person::class.java::cast)
    val cars = mixedList.filterByType<Car>()
    // -> Rewritten to mixedList.filter(Car::class::isInstance).map(Car::class.java::cast)
    val strings = mixedList.filterByType<String>()
    // -> Rewritten to mixedList.filter(String::class::isInstance).map(String::class.java::cast)
    val numbers = mixedList.filterByType<Int>()
    // -> Rewritten to mixedList.filter(Int::class::isInstance).map(Int::class.java::cast)

    println("People: $people")
    println("Cars: $cars")
    println("Strings: $strings")
    println("Numbers: $numbers")
  }

  @JvmStatic
  fun main(args: Array<String>) {
    demoReifiedType()
  }
}
