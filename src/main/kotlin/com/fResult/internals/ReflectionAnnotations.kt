package com.fResult.internals

object ReflectionAnnotations {
  // annotations = metadata for other declarations

  // meta-annotations
  @Target(AnnotationTarget.CLASS) // can be attached to class/abstract class/interface...
  @Retention(AnnotationRetention.RUNTIME)
  // SOURCE - only inspected by source tools, e.g., compiler + plugins
  // BINARY - copied to the binary
  // RUNTIME - copied to the binary AND can be inspected via reflection
  annotation class TestAnnotation(val value: String)

  @TestAnnotation(value = "Example") // TestAnnotation instance per class declaration
  class AnnotatedClass {
    // @TestAnnotation("A Property") // illegal - this annotation can only be used for classes
    val aProperty: Int = 0
  }

  @TestAnnotation("an interface") // legal
  interface MyInterface

  @TestAnnotation("an abstract class") // legal
  abstract class MyAbstractClass

  @TestAnnotation("an object") // legal
  object MyObject

  @JvmStatic
  fun main(args: Array<String>) {
    AnnotatedClass()
  }
}
