package com.fResult.internals

object InlineFunctions {
  // ecommerce platform
  data class Product(val name: String, var price: Double) // never use Double for money

  fun List<Product>.applyDiscount(discountPercentage: Double, operation: (Product) -> Unit) {
    for (product in this) {
      product.price *= (1 - discountPercentage / 100)
      operation(product)
    }
  }

  inline fun List<Product>.applyDiscountFast(discountPercentage: Double, operation: (Product) -> Unit) {
    for (product in this) {
      product.price *= (1 - discountPercentage / 100)
      operation(product)
    }
  }

  fun demoDiscounts() {
    val products = listOf(
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

  @JvmStatic
  fun main(args: Array<String>) {
    demoDiscounts()
  }
}
