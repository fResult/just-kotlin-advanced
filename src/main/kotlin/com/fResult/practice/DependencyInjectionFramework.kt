package com.fResult.practice

import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor

object DependencyInjectionFramework {
  const val ADMIN_USER = "admin@fResult.com"

  // part 1 - create 2 annotations
  // @Layer - runtime annotation, can be attached to classes and interfaces ...
  // @Inject - runtime annotation, can be attached to PROPERTIES only
  @Target(AnnotationTarget.CLASS)
  @Retention(AnnotationRetention.RUNTIME)
  annotation class Injectable

  @Target(AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER)
  @Retention(AnnotationRetention.RUNTIME)
  annotation class Inject

  // controller - HTTP requests
  // service - business logic
  // repository - persistence layer

  @Injectable
  class Repository {
    fun getData(): String = "data from repository"
  }

  @Injectable
  class Service(@Inject val repository: Repository) {
    fun performAction() = repository.getData() + " - with some business logic"
  }

  @Injectable
  class Controller(@Inject val service: Service, @Inject val users: UserManager) {
    fun processHttpRequest(payload: String, username: String = "invalid@fResult.com"): String {
      println("Process http request for $payload")
      return if (users.isLoggedIn(username)) "Processed request: Response ${service.performAction()}"
      else "Not logged in, request denied"
    }
  }

  @Injectable
  class UserManager {
    private val loggedUsers = mutableSetOf<String>()

    fun login(username: String) {
      loggedUsers.add(username)
      println("[log] Logged in as $username")
    }

    fun isLoggedIn(username: String): Boolean {
      return username in loggedUsers
    }

    fun logout(username: String) {
      loggedUsers.remove(username)
      println("[log] $username just logged out")
    }
  }

  @JvmStatic
  fun main(args: Array<String>) {
    val diManager = DependencyInjectionManager()
    diManager.initialize()

    // Get the controller instance
    val controller = diManager.getInstance(Controller::class)

    // Use it
    val userManager = diManager.getInstance(UserManager::class)
    userManager.login(ADMIN_USER)

    val data =
      controller.processHttpRequest("{ \"source\": \"sensors/incremental\" }", ADMIN_USER)
    println("Data after login: $data")

    userManager.logout(ADMIN_USER)
    val moreData =
      controller.processHttpRequest("{ \"source\": \"sensors/incremental\" }", ADMIN_USER)
    println("Data after logout: $moreData")
  }

  class DependencyInjectionManager {
    private val layers = mutableMapOf<KClass<*>, Any>()

    fun initialize() =
      DependencyInjectionFramework::class.nestedClasses
        .forEach { register(it) }

    private fun <T : Any> register(clazz: KClass<T>) {
      val layerAnnotation = clazz.findAnnotation<Injectable>()
      layerAnnotation?.also {
        val instance = createInstance(clazz)
        if (!layers.containsKey(clazz)) {
          layers[clazz] = instance
        }
      }
    }


    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getInstance(clazz: KClass<T>): T {
      return layers[clazz] as? T
        ?: throw IllegalArgumentException("Class ${clazz.simpleName} not registered")
    }

    private fun <T : Any> createInstance(clazz: KClass<T>): T {
      val constructor = clazz.primaryConstructor ?: return clazz.createInstance()

      val args = constructor.parameters.map { param ->
        val type = param.type.classifier as KClass<*>
        // Check if we already have an instance of this type
        return@map layers[type] ?: createInstance(type).also {
          // Cache it if created recursively
          layers[type] = it
        }
      }

      return constructor.call(*args.toTypedArray())
    }

//    private fun <T : Any> injectDependencies(instance: T) {
//      val clazz = instance::class
//      clazz.declaredMemberProperties.forEach { prop ->
//        prop.findAnnotation<Inject>()?.also {
//          val type = prop.returnType.classifier as KClass<*>
//          val dependency = layers[type]
//          if (dependency != null && prop is KMutableProperty<*>) {
//            prop.setter.call(instance, dependency)
//          }
//        }
//      }

//      val constructor = clazz.primaryConstructor
////      clazz.members.forEach { member ->
////        val injectAnnotation = member.findAnnotation<Inject>()
////      }
//      val injectedProperties = constructor?.parameters
//        ?.filter { prop -> prop.findAnnotation<Inject>() != null }
//        ?: emptyList()
//      injectedProperties.forEach { prop ->
//        val type = prop.type.classifier as KClass<*>
//        val dependency = layers[type]
//        dependency?.also {
//          if (it is KMutableProperty<*>)
//            it.setter.call(instance, createInstance(type))
//        }
//      }
//    }
  }
}
