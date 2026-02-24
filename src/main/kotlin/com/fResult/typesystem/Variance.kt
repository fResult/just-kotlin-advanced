package com.fResult.typesystem

object Variance {
  abstract class Pet
  data class Dog(val name: String) : Pet()
  data class Cat(val name: String) : Pet()

  // COVARIANT generic types
  data class Vet<out A : Pet>(val pet: A) // <out A> -> COVARIANT IN A

  // no - INVARIANT
  interface Combiner<A> { // Semigroup
    fun combine(x: A, y: A): A
  }
  // no - INVARIANT
  val intCombiner = object : Combiner<Int> {
    override fun combine(x: Int, y: Int): Int = x + y
  }

  // java standard library - all Java generics are invariant
  // val javaList: java.util.List<Pet> = java.util.ArrayList<Dog>() // type mismatch

  // HELL NO - CONTRAVARIANT
  // Dog is a Pet, then Owner<Pet> is an Owner<Dog>
  class Owner<in A> {
    fun raisedDog(pet: A) = pet is Dog

    fun raisedCat(pet: A) = pet is Cat
  }

  // covariant types "produce" or "get" elements -> "output" elements
  // contravariant types "consume" or "act on" elements -> "input" elements
  /*
   * Rule of thumb how to decide variance:
   * - If it "outputs" elements -> COVARIANT (out)
   * - If it "consumes" elements -> CONTRAVARIANT (in)
   * - Otherwise, INVARIANT (no modifier)
   */

  /*
   * Exercise: Add variance modifiers
   */
  class RandomGenerator<out A>
  class Option<out A> // holds at most one item
  class JsonSerializer<in A> // turns values of type A into JSON
  interface MyFunction<in A, out B> // takes a value of type A and return B

  /*
   * Exercise:
   * 1. add variance modifiers where appropriate
   * 2. EmptyList should be empty regardless of the type - can we make it an object?
   * 3. add an "add" method to the generic list type
   *    fun add(element: A): LList<A>
   */
  abstract class LList<out A> { // Covariant type "produces" elements, but I want to "consume" an element
    abstract fun head(): A // first item in the list
    abstract fun tail(): LList<A> // rest of the list without the head
  }

  fun <B, A : B> LList<A>.add(elem: B): LList<B> = Cons(elem, this)
  data object EmptyList : LList<Nothing>() {
    override fun head(): Nothing = throw NoSuchElementException()
    override fun tail(): LList<Nothing> = this
  }

  data class Cons<out A>(val h: A, val t: LList<A>) : LList<A>() {
    override fun head(): A = h
    override fun tail(): LList<A> = t
  }

  val myPets: LList<Pet> = EmptyList
  val myStrings: LList<String> = EmptyList

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

    // CONTRAVARIANT generic types
    val ownerOfPet = Owner<Pet>()
    val ownerOfDog: Owner<Dog> = ownerOfPet
    val ownerOfCat: Owner<Cat> = ownerOfPet
    val ownerOfDogs: List<Owner<Dog>> = listOf(ownerOfPet, ownerOfPet)
  }
}
