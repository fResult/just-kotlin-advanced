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

  @JvmStatic
  fun main(args: Array<String>) {

  }
}
