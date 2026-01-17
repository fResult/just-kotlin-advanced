package com.fResult.recap

object KotlinEssentials {
  // val, var, types
  val meaningOfLife = 42
  var changeableObjective = 10

  // instructions (are executed) vs expressions (are evaluated)
  val anExpression = 2 + 3
  val anIfExpression = if (2 > 3) 43 else 99 // if-expression

  // functions
  fun complexFun(arg: Int): Int {
    println("Just called my complex fun with an argument: $arg")
    return arg + 20
  }

  // repetition (loops or recursions)
  fun concatenateString(
    aString: String,
    count: Int,
  ): String {
    var result = ""
    for (i in 1..count) {
      result += aString
    }
    return result
  }

  // FP - recursion instead of looping
  fun concatenateStringRecursive(
    aString: String,
    count: Int,
  ): String =
    if (count == 0) {
      ""
    } else {
      aString + concatenateStringRecursive(aString, count - 1)
    }

  tailrec fun concatenateStringTailRec(
    aString: String,
    count: Int,
    result: String = "",
  ): String =
    if (count == 0) {
      result
    } else {
      concatenateStringTailRec(aString, count - 1, result + aString)
    }

  // OOP
  open class Animal {
    val length = 100 // property

    open fun eat() { // method
      println("I'm eating what'ya looking at?")
    }
  }

  // inheritance = subtype
  class Dog(
    val name: String,
  ) : Animal() {
    override fun eat() {
      println("Woof, woof, eating my food!")
    }
  }

  // interfaces = "abstract" data types
  interface Carnivore {
    infix fun eat(animal: Animal): String

    companion object { // object Carnivore
      // stores "static" properties & methods
      // properties & methods that depend on Carnivore TYPE
      val eatsAnimals: Boolean = true

      fun build(kind: String): Carnivore =
        when (kind) {
          "croc" -> Crocodile()
          else -> GenericCarnivore
        }
    }
  }

  // inheritance = subtype a single class + zero or more interfaces
  class Crocodile :
    Animal(),
    Carnivore {
    override fun eat(animal: Animal): String = "I'm a croc, I'm eating a poor fella"
  }

  // objects and companions
  object MySingleton {
    // a type + the only single instant of this type
    val aProperty: Int = 57

    fun getSomething(config: String): Int = 45
  }

  object GenericCarnivore : Carnivore {
    override fun eat(animal: Animal): String = "I'm eating all animals"
  }

  // generics = reuse code on many (potentially unrelated) types
  interface LList<A> { // <-- type parameters
    fun add(elem: A): LList<A>
  }

  // data classes = lightweight data structures
  // equals, hashCode, toString, copy, componentN are ready
  data class Person(
    val name: String,
    val age: Int,
  )

  // enums
  enum class Color {
    RED,
    GREEN,
    BLUE, // These are only options
  }

  // FP
  // able to pass functions as values, return them as results
  val aFunction: (Int) -> String = { n -> "Kotlin $n" }

  // higher-order functions (HOFs)
  val aProcessedList = listOf(1, 2, 3, 4).map(aFunction)
  // --> ["Kotlin 1", "Kotlin 2", "Kotlin 3", "Kotlin 4"]
  // map, flatMap, filter, take, takeWhile, drop, let, also, run, ...

  @JvmStatic
  fun main(args: Array<String>) {
    // meaningOfLife = 45 // Error - vals cannot be updated
    changeableObjective = 15 // Okay
    if (changeableObjective > 10) {
      println("Bigger")
    } else {
      println("Smaller")
    }

    val complexfunInvocation = complexFun(16)
    println(concatenateString("Kotlin", 5))
    println(concatenateStringRecursive("Kotlin", 5))
    println(concatenateStringTailRec("Kotlin", 5))

    val myAnimal = Animal()
    println(myAnimal.length)
    println(myAnimal.eat())

    val myDog = Dog("Diggy")
    myDog.eat()

    val croc = Crocodile()
    println(croc.eat(myAnimal))
    println(croc eat myDog)

    val crocV2 = Carnivore.build("croc")
    println(crocV2.eat(myAnimal))
    val genericCarnivore = Carnivore.build("Wolf")
    println(genericCarnivore.eat(myAnimal))

    // FP
    val functionInvocation = aFunction(10) // Kotlin 10
    val mappedFunctionInvocation = aFunction.invoke(5) // Kotlin 5
    println(functionInvocation)
    println(mappedFunctionInvocation)
    println(2.let(aFunction))

    // HOF
    println(aProcessedList)
  }
}
