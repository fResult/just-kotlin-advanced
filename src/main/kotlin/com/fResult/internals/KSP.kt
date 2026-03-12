package com.fResult.internals

object KSP {
  /*
   * - analyze  source code and generate
   * - new source code
   * - compile
   * -access methods/functionality at COMPILE TIME
   */

  // Use-case: generate builder patterns for data classes
  data class Person(
    val name: String,
    val age: Int,
  )

  /*
   * PersonsBuilder
   *   .name("Korn")
   *   .age(99)
   *   .property(value)
   *   ...
   *   .build()
   */

  // module 1 - symbol definition (annotations)
  // module 2 - KSP logic for generating the source
  // module 3 - source + the place where the generated source will be created
}
