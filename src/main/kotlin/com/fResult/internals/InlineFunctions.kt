@file:Suppress("ktlint:standard:no-consecutive-comments")

package com.fResult.internals

object InlineFunctions {
  // ecommerce platform
  data class Product(
    val name: String,
    var price: Double,
  ) // never use Double for money

  fun List<Product>.applyDiscount(
    discountPercentage: Double,
    operation: (Product) -> Unit,
  ) {
    for (product in this) {
      product.price *= (1 - discountPercentage / 100)
      operation(product)
    }
  }

  inline fun List<Product>.applyDiscountFast(
    discountPercentage: Double,
    operation: (Product) -> Unit,
  ) {
    for (product in this) {
      product.price *= (1 - discountPercentage / 100)
      operation(product)
    }
  }

  fun demoDiscounts() {
    val products =
      listOf(
        Product("Laptop Pro", 1000.00),
        Product("Phone 25 BIG", 500.00),
        Product("Tablet 17 Thin", 300.00),
      )

    println("Applying a 10% discount:")
    products.applyDiscount(10.0, ::displayDiscountIfApplicable)

    println("Fast applying a 10% discount (inline):")
    products.applyDiscountFast(10.0, ::displayDiscountIfApplicable)

    /*
     * The inline call is rewritten (= inlined) to:
     * for (product in products) {
     *   product.price *= (1 - 10.0 / 100)
     *   displayDiscountIfApplicable(product)
     * }
     */
  }

  fun displayDiscountIfApplicable(product: Product) {
    println("\t- ${product.name} is now ${product.price} USD")
  }

  fun demoPerf() {
    val products =
      listOf(
        Product("Laptop Pro", 1000.00),
        Product("Phone 25 BIG", 500.00),
        Product("Tablet 17 Thin", 300.00),
      )

    val startNonInline = System.nanoTime()
    repeat(1000) {
      products.applyDiscount(10.0, ::displayDiscountIfApplicable)
//      products.applyDiscount(10.0) { product ->
//        println("\t- ${product.name} is now ${product.price} USD")
//      }
    }
    val durationNonInline = System.nanoTime() - startNonInline

    val startInline = System.nanoTime()
    repeat(1000) {
      products.applyDiscountFast(10.0, ::displayDiscountIfApplicable)
//      products.applyDiscountFast(10.0) { product ->
//        println("\t- ${product.name} is now ${product.price} USD")
//      }
    }
    val durationInline = System.nanoTime() - startInline

    println("Non-Inline Duration: $durationNonInline")
    println("Inline Duration: $durationInline")
  }

  /*
   * 2 benefits of inline function
   * 1. code is rewritten by the compiler with no overhead (function calls, lambda instantiations
   * 2. potential perf benefits, best if the functions are small and repeat in your code
   */
  // sometimes useful NOT to inline some lambda
  inline fun performOperation(
    noinline storeOperation: () -> Unit,
    executeOperation: () -> Unit,
  ) {
    // store noinline lambdas regular values
    GlobalStore.store(storeOperation)

    // execute the other
    executeOperation()
  }

  object GlobalStore {
    private var storedOperation: (() -> Unit)? = null

    fun store(operation: () -> Unit) {
      storedOperation = operation
    }

    fun executeStored() {
      storedOperation?.invoke()
    }
  }

  fun demoNoInline() {
    performOperation(
      storeOperation = { println("This op should be called later") },
      executeOperation = { println("This op should be called immediately") },
    )

    // later
    GlobalStore.executeStored()
  }

  @JvmStatic
  fun main(args: Array<String>) {
    // demoDiscounts()
    // demoPerf()
    demoNoInline()
  }
}
