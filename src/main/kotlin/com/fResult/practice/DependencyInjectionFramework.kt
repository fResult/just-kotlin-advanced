package com.fResult.practice

object DependencyInjectionFramework {
  // part 1 - create 2 annotations
  // @Layer - runtime annotation, can be attached to classes and interfaces ...
  // @Inject - runtime annotation, can be attached to PROPERTIES only
  @Target(AnnotationTarget.CLASS)
  @Retention(AnnotationRetention.RUNTIME)
  annotation class Layer

  @Target(AnnotationTarget.PROPERTY)
  @Retention(AnnotationRetention.RUNTIME)
  annotation class Inject

  @JvmStatic
  fun main(args: Array<String>) {

  }
}
