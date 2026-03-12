package com.fResult.typesystem

// type aliases have to be declared at the top-level
typealias PhoneBook = Map<String, String>

// type aliases can have generic type arguments
typealias Table<A> = Map<String, A>

// example
class Either<out E, out A>

// variant modifiers carry over to the type aliases
typealias ErrorOr<A> = Either<Throwable, A>

object TypeAliases {
  val phoneBook: PhoneBook = mapOf("Superman" to "123-456") // Map<String, String>
  val theMap: Map<String, String> = phoneBook // also okay
  val stringTable: Table<String> = phoneBook // also okay

  @JvmStatic
  fun main(args: Array<String>) {
    println(theMap)
    println(stringTable)
  }
}
