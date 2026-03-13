package com.fResult.builderKsp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration

class BuilderProcessor(
  generator: CodeGenerator,
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
      generateBuilderClass(clazz)
    }

    // only those symbols that CANNOT be processed at this time
    return emptyList()
  }

  private fun generateBuilderClass(clazz: KSClassDeclaration) {
    TODO("Not implemented yet")
  }
}
