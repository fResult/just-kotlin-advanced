package com.fResult.typesystem

object VariancePositions {
  abstract class Animal
  class Dog : Animal()
  class Cat : Animal()
  class Crocodile : Animal()

  // out = Covariant, in = Contravariant
  // This is illegal
  // class Vet<in A>(val favoriteAnimal: A) {
  //   fun treat(animal: A) = true
  // }
  /*
   * assume that this was legal
   * class Vet<in A>(val favoriteAnimal: A)
   *
   * val garfield = Cat()
   * val lassie = Dog()
   * val vet: Vet<Animal> = Vet<Animal>(garfield) // Vet<SuperTypeOfDog>
   * val dogVet: Vet<Dog> = vet
   * val favAnimal = dogVet.favoriteAnimal // guaranteed to be a Dog, but it is actually a Cat
   *
   * - types of properties (val or var) are in the "out" (aka covariant) position
   * - "in" types cannot be placed in the "out" position
   */

  // class MutableContainer<out A>(var contents: A) // var properties are ALSO in contravariant ("in") position
  /*
   * class Liquid
   * class Water : Liquid()
   * class Gasoline : Liquid()
   *
   * val container: MutableContainer<Liquid> = MutableContainer<Water>(Wallet())
   * container.contents = Gasoline() // guarantee that I can write any Liquid inside, but have to keep it to Water
   *
   * types of vars are in the contravariant ("in") position as well
   * => Therefore it must be INVARIANT
   */

  // class LList<out A> {
  //   // illegal here
  //   fun add(elem: A): LList<A> = TODO()
  // }
  /*
   * val myList: LList<Animal> = LList<Dog>()
   * val newList = myList.add(Crocodile()) // guarantee to be able to add any Animal, BUT have to guarantee just Dog
   *
   * method parameter types are in the contravariant ("in") position
   * => Therefore, we cannot use covariant types in method parameters
   */

  /*
   * assume this compiled:
   * class Vet<in A> {
   *   fun rescueAnimal(): A = TODO()
   * }
   *
   * val vet: Vet<Animal> = object : Vet<Animal> {
   *   override fun rescueAnimal(): Animal = Cat()
   * }
   * val dogVet: Vet<Dog> = vet // legal, because of contravariant
   * val rescuedDog = dogVet.rescueAnimal() // guarantee to return a Dog, BUT return a Cat
   *
   * method return types are in COVARIANT ("out") position
   */

  /*
   * solve variance positions problems
   */
  // 1 - consume elements in a covariant type
  abstract class LList<out A>
  data object EmptyList : LList<Nothing>()
  data class Cons<out A>(val head: A, val tail: LList<A>) : LList<A>()

  // how do we add an element?
  // solution = widening type type
  fun <B, A : B> LList<A>.add(elem: B): LList<B> = Cons(elem, this)

  // 2 - return element in a contravariant type
  // solution = narrow the type
  abstract class Vehicle
  open class Car : Vehicle()
  class SuperCar : Car()

  class RepairShop<in A : Vehicle> {
    fun <B : A> repair(elem: B): B = elem
  }

  @JvmStatic
  fun main(args: Array<String>) {
    // covariant problem
    val myList: LList<Dog> = EmptyList
    val dogs = myList.add(Dog()).add(Dog()) // LList<Dog>
    val animals = dogs.add(Cat()) // LList<Animal> <- Lowest common ancestor
    println(animals)

    // contravariant problem
    val repairShop: RepairShop<Car> = RepairShop<Vehicle>() // contravariance
    val myBeatUpVW = Car()
    val damagedFerrari = SuperCar()

    val freshVW = repairShop.repair(myBeatUpVW) // Car
    val freshFerrari = repairShop.repair(damagedFerrari) // SuperCar
    println(listOf(freshVW, freshFerrari))

    // Mimic Kotlin Native List
    val xs: LLList<Animal> = LLList.of(Cat(), Dog())
    val ys: LLList<Animal> = xs.add(Dog())
    ys.forEach(::println)
    println(ys.map { it::class.simpleName })
  }
}

class LLList<out A>(private var items: MutableList<A>) {
  fun head(): A = TODO()
  fun tail(): LLList<A> = TODO()

  companion object {
    fun <A> of(vararg elements: A): LLList<A> {
      val list = LLList<A>(mutableListOf())
      for (element in elements) {
        list.add(element)
      }
      return list
    }
  }

  // To use a covariant type in a contravariant "in" position (method parameter),
  // we must introduce a new type parameter B that is a supertype of A.
  fun add(element: @UnsafeVariance A): LLList<A> {
    items.add(element)
    return LLList(items)
  }

  fun forEach(f: (A) -> Unit) {
    items.forEach(f)
  }

  fun <B> map(f: (A) -> B): LLList<B> {
    val mappedItems = items.map(f)
    return LLList(mappedItems.toMutableList())
  }

  fun filter(f: (A) -> Boolean): LLList<A> {
    val filteredItems = items.filter(f)
    return LLList(filteredItems.toMutableList())
  }

  fun <B> reduce(initial: B, reducer: (B, A) -> B): B {
    var acc = initial
    for (item in items) {
      acc = reducer(acc, item)
    }
    return acc
  }

  override fun toString(): String = items.toString()
}
