package io.github.pirocks.algebra.values

import io.github.pirocks.algebra.values.numbers.Scalar


open class Dimension(open vararg val n: Int)

open class SingleDimension(val m: Int) : Dimension(m)


/**
 * For multidimensional tensors use tensors of tensors. All tensors are integer indexed.
 */
interface Tensor<Dim : SingleDimension, Of : AlgebraValue, UnderlyingScalar : Scalar/*<UnderlyingScalar>*/> :
        AlgebraValue {
    infix fun at(i: Int): Of

}
