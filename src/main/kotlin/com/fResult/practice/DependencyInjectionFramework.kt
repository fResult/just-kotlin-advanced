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

  // controller - HTTP requests
  // service - business logic
  // repository - persistence layer

  @Layer
  class Repository {
    fun getData(): String = "data from repository"
  }

  @Layer
  class Service(@Inject val repository: Repository) {
    fun getData() = repository.getData()
  }

  @Layer
  class Controller(@Inject val service: Service) {
    fun getData() = service.getData()
  }

  @Inject
  lateinit var controller: Controller

  @JvmStatic
  fun main(args: Array<String>) {
    val data = controller.getData()
    println("Should retrieve data: $data")
  }
}
