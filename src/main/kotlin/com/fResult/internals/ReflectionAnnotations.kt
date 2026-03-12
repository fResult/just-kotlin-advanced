package com.fResult.internals

import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation

object ReflectionAnnotations {
  // annotations = metadata for other declarations

  // meta-annotations
  @Target(AnnotationTarget.CLASS) // can be attached to class/abstract class/interface...
  @Retention(AnnotationRetention.RUNTIME)
  // SOURCE - only inspected by source tools, e.g., compiler + plugins
  // BINARY - copied to the binary
  // RUNTIME - copied to the binary AND can be inspected via reflection
  annotation class TestAnnotation(
    val value: String,
  )

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

  // example: generate table declarations for a data class

  // sits in the library
  @Target(AnnotationTarget.CLASS)
  @Retention(AnnotationRetention.RUNTIME)
  annotation class Table(
    val name: String,
  )

  @Target(AnnotationTarget.PROPERTY)
  @Retention(AnnotationRetention.RUNTIME)
  annotation class Column(
    val name: String,
  )

  fun generateTableStatement(clazz: KClass<*>): String? {
    val tableAnnotation: Table? = clazz.findAnnotation<Table>()
    val tableName = tableAnnotation?.name ?: return null

    val columns =
      clazz.declaredMemberProperties.mapNotNull { prop ->
        val columnAnnotation: Column? = prop.findAnnotation<Column>()
        val columnName = columnAnnotation?.name
        val columnType =
          when (prop.returnType.classifier) {
            Int::class -> "INTEGER"
            String::class -> "TEXT"
            Boolean::class -> "BOOLEAN"
            else -> null
          }

        if (columnName == null || columnType == null) {
          null
        } else {
          "$columnName $columnType"
        }
      }

    return "CREATE TABLE $tableName ${
      columns.joinToString(
        separator = ", ",
        prefix = "(",
        postfix = ")",
      )
    };"
  }

  // user-space
  @Table(name = "users")
  data class User(
    @Column(name = "id")
    val id: Int,
    @Column(name = "name")
    val name: String,
    @Column(name = "age")
    val age: Int,
    @Column(name = "active")
    val active: Boolean,
  )

  @JvmStatic
  fun main(args: Array<String>) {
    val userTableCreationStmt = generateTableStatement(User::class)
    userTableCreationStmt?.also {
      println("User table creation statement: $it")
    }
  }
}
