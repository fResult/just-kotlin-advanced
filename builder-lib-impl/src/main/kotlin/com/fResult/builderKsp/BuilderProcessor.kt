package com.fResult.builderKsp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate

class BuilderProcessor(
  private val generator: CodeGenerator,
) : SymbolProcessor {
  companion object {
    const val BUILDER_ANNOTATION_NAME = "com.fResult.com.fResult.builderKsp.Builder"
  }

  override fun process(resolver: Resolver): List<KSAnnotated> {
    /*
     * 1 - find all classes with the @Builder annotation
     * 2- generate a builder pattern out of every such class
     */

    val classes =
      resolver
        .getSymbolsWithAnnotation(BUILDER_ANNOTATION_NAME)
        .filterIsInstance<KSClassDeclaration>()

    classes.forEach { clazz ->
      if (clazz.validate()) {
        generateBuilderClass(clazz)
      }
    }

    // only those symbols that CANNOT be processed at this time
    return emptyList()
  }

  private fun generateBuilderClass(clazz: KSClassDeclaration) {
    // com.fResult.data.Person - data class
    // com.fResult.data.PersonBuilder - generated class
    val className = clazz.simpleName.asString()
    val packageName = clazz.packageName.asString()
    val properties = clazz.getAllProperties().toList()
    val originalFiles = listOfNotNull(clazz.containingFile)
    val builderClassName = "${className}Builder"
    val file = // new file where we are going to generate the code
      generator.createNewFile(
        Dependencies(false, *originalFiles.toTypedArray()),
        packageName,
        className,
      )

    file.bufferedWriter().use { writer ->
      // put it some string here
      writer.write("package $packageName\n\n")
      writer.write("class $builderClassName {\n")

      properties.forEach { prop ->
        val propName = prop.simpleName.asString()
        val propType = prop.type.resolve()

        // private var $prop: $type? = null
        // fun prop($name: $type): Unit = $prop = $name
        writer.write("  private var $propName: $propType? = null\n")
        writer.write("  fun $propName(value: $propType) = apply { $propName = value }\n")
      }

      /*
       * class PersonBuilder {
       *   private var name: String? = null
       *   private var age: Int? = null
       *
       *   fun name(value: String) = apply { name = value }
       *   fun age(value: Int) = apply { age = value }
       *
       *   fun build() {
       *      return Person(
       *        name ?: throw new IllegalArgumentException("name must be provided"),
       *        age ?: throw new IllegalArgumentException("age must be provided"),
       *      )
       *   }
       * }
       */
      writer.write("  fun build(): $className {\n")
      writer.write("    return $className(\n")
      properties.forEachIndexed { index, prop ->
        prop.simpleName.asString().also { propName ->
          writer.write(
            "      $propName = $propName ?: throw IllegalArgumentException(\"$propName must be provided!\")",
          )
          if (index < properties.size - 1) {
            writer.write(",\n")
          } else {
            writer.write("\n")
          }
        }
      }
      writer.write("    )\n")
      writer.write("  }\n")
      writer.write("}\n")
    }

//    println("className = $className")
//    println("packageName = $packageName")
//    clazz.qualifiedName?.asString().also(::println)
//    clazz.simpleName.getQualifier().also(::println)
//    println("builderName = $builderName")
  }
}
