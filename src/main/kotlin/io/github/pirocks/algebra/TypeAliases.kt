package io.github.pirocks.algebra

import io.github.pirocks.algebra.numbers.FloatFieldVal

/**
 * Provides a handful of useful abbreviations/ more concise names.
 */


typealias Mul<T> = Multiplication<T>

typealias Add<T> = Addition<T>
//public typealias Div = Division
typealias `-`<T> = UMinus<T>

typealias `+`<T> = Addition<T>
typealias `*`<T> = Multiplication<T>
//public typealias `^` = Exponentiation

val `0` = FloatFieldVal(0.0f).zero
val `1` = FloatFieldVal(1.0f).one
