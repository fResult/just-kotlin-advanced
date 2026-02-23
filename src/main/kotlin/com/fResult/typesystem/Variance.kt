package com.fResult.typesystem

object Variance {
  abstract class Pet
  data class Dog(val name: String) : Pet()
  data class Cat(val name: String) : Pet()

  // COVARIANT generic types
  data class Vet<out A : Pet>(val pet: A) // <out A> -> COVARIANT IN A

  @JvmStatic
  fun main(args: Array<String>) {
    // Dog extends Pet -> List<Dog> "extends" List<Pet>?
    // Variance question for the List type: A extends B -> List<A> extends List<B>
    // ==> yes, List is a COVARIANT TYPE
    val buddy = Dog("Buddy")
    val max = Dog("Max")
    val whiskers = Cat("Whiskers")
    val fluffy = Cat("Fluffy")
    val pets: List<Pet> = listOf(buddy, max, whiskers, fluffy)
    println(pets)

    // COVARIANT generic types
    val vetOfDog = Vet(max)
    val vetOfCat = Vet(whiskers)
    val vetOfPets: List<Vet<Pet>> = listOf(vetOfCat, vetOfDog)
    println("Vet of Pets: $vetOfPets")
  }
}
