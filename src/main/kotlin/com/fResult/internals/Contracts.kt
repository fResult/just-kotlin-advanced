package com.fResult.internals

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.random.Random

object Contracts {
  // contracts = code that helps the compiler make some deductions

  // info to the compiler about the return value of a function
  @OptIn(ExperimentalContracts::class)
  fun containsJustDigits(str: String?): Boolean {
    contract {
      // small DSL for testing things about this function
      returns(true) implies (str != null)
    }
    return if (str == "" || str == null) false
    else str.all { it.isDigit() }
  }

  fun demoNullableString() {
    val maybeString =
      if (Random.nextBoolean()) "123456"
      else null

    println("I has maybe string $maybeString")
    if (containsJustDigits(maybeString)) { // containsJustDigits(...) == true AND maybeString != null
      println("String is just a number, I want the length: ${maybeString.length}")
    }
  }

  // more complex example
  open class User(open val username: String, open val email: String) {
    @OptIn(ExperimentalContracts::class)
    fun isValidAdmin(): Boolean {
      contract {
        returns(true) implies (this@User is Admin)
      }
      return this is Admin && email.endsWith("@fResultAdmin.com")
    }
  }

  class Admin(
    override val username: String,
    override val email: String,
    val permissions: List<String>,
  ) : User(username, email) {
    fun purgeData() = println("ALL DATA REMOVED")
  }

  fun attemptAdminTasks(user: User) {
    if (user.isValidAdmin()) { // if it's true -> the user is implied an Admin
      // user is Admin in this scope
      println("Running admin tasks...")
      user.purgeData()
    } else {
      println("User ${user.username} is not a valid admin")
    }
  }

  fun demoAdmin() {
    val admin = Admin("adminuser", "admin@fResultAdmin.com", listOf("READ", "WRITE"))
    attemptAdminTasks(admin)

    val simpleUser = User("fakeAdmin", "fake@fResultAdmin.com")
    attemptAdminTasks(simpleUser)
  }

  // 2 - callsInPlace - guarantee that a lambda was invoked in a certain way
  // assume this comes from some external library
  open class Resource {
    fun open() {
      println("Resource opened")
    }

    fun close() {
      println("Resource closed")
    }

    fun use(): String {
      println("Resource was accessed")
      return "BELIEVE IN YOURSELF!"
    }
  }

  @OptIn(ExperimentalContracts::class)
  fun <R : Resource, A> R.bracket(block: (R) -> A): A { // "bracket" pattern
    contract {
      callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    this.open()
    try {
      return block(this)
    } finally {
      this.close()
    }
  }

  fun demoResource() {
    val resource = Resource()
    val result: String
    resource.bracket {
      // callsInPlace contract guarantees this code runs just once
      result = it.use() // allowed because this is a ONE-TIME assignment
    }
    println("I got what I wanted: $result")
  }

  @JvmStatic
  fun main(args: Array<String>) {
    // demoNullableString()
    // demoAdmin()
    demoResource()
  }
}
