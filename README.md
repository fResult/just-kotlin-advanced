# Just Kotlin Advanced

This repository is built to master Kotlin's deep internals and advanced type system.\
Following the [Rock the JVM - Advanced Kotlin course](https://rockthejvm.com/courses/advanced-kotlin), I moved beyond essential syntax to understand how to build robust libraries, and internal frameworks.

## Key Learning & Implementation

### Mastering the Type Systems & Generics

- **Declaration-site Variance:** Implemented `in` and `out` variances to create flexible, type-safe APIs that prevent runtime `ClassCastException`
- **Delegated Properties:** Leveraged custom delegates and the standard library to encapsulate common logic like lazy loading and observable state

### Metaprogramming: Reflection & KSP

- **Kotlin Symbol Processing (KSP):** Built a compile-time code generator to automate boilerplate creation, significantly reducing runtime overhead compared to reflection
- **Advanced Reflection:** Developed a mini Dependency Injection (DI) framework and dynamic object inspectors to understand how modern frameworks operate under the hood

### Kotlin Internals & Performance

- **Kotlin Contracts:** Applied compiler contracts to improve smart-casting and null-safety, making the code more predictable for the type checker
- **Reified Type Parameters:** Utilized `inline` functions with `reified` types to overcome JVM type erasure, enabling clean generic operations at runtime

## Architect's Perspective: Beyond the Code

Mastering these advanced features isn't just about writing "clever" code.\
It's about **reducing system entropy** and **enforcing correctness** at scale.

1. **Framework-Level Thinking:** By using KSP and Reflection, we can build internal tools that automate repetitive tasks (like mapping or validation), ensuring team consistency without manual toil
2. **Type-Driven Safety:** Advanced generics allow us to design APIs that make "impossible states unrepresentable."\
    This is crucial for building resilient, mission-critical systems where logic errors lead to data loss
3. **Performance Optimization:** Understanding `inline` and `contracts` enables us to write high-performance, functional code that minimizes Garbage Collection pressure
