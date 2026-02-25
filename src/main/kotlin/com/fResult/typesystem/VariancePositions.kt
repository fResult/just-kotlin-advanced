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

  @JvmStatic
  fun main(args: Array<String>) {

  }
}
