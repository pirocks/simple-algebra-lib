package io.github.pirocks.algebra.values

import io.github.pirocks.algebra.values.numbers.Scalar


/**
 *
 */
interface Matrix<Dim1 : SingleDimension, Dim2 : SingleDimension, OnScalar : Scalar/*<OnScalar>*/> :
        Tensor<Dim2, Tensor<Dim1, OnScalar, OnScalar>, OnScalar>