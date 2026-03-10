package com.fResult.practice

import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation

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
    fun performAction() = repository.getData() + " - with some business logic"
  }

  @Layer
  class Controller(@Inject val service: Service, @Inject val users: UserManager) {
    fun processHttpRequest(payload: String, username: String = "invalid@fResult.com") =
      if (users.isLoggedIn(username)) "Processed request: Response ${service.performAction()}"
      else "Not logged in, request denied"
  }

  @Layer
  class UserManager {
    private val loggedUser = mutableSetOf<String>()

    fun login(username: String) {
      loggedUser.add(username)
      println("[log] Logged in as $username")
    }

    fun isLoggedIn(username: String) = username in loggedUser

    fun logout(username: String) {
      loggedUser.remove(username)
      println("[log] $username just logged out")
    }
  }

  @Inject
  lateinit var controller: Controller

  @JvmStatic
  fun main(args: Array<String>) {
    val data = controller.processHttpRequest(p)
    println("Should retrieve data: $data")
  }

  class DependencyInjectionManager {
    private val layers = mutableMapOf<KClass<*>, Any>()

    // part 2.1 - add a function which registers a class into the layers map
    fun <T : Any> register(clazz: KClass<T>): Unit {
      val layerAnnotation = clazz.findAnnotation<Layer>()
      layerAnnotation?.also {
        layers[clazz] = clazz.createInstance()
      }
    }
  }
}
