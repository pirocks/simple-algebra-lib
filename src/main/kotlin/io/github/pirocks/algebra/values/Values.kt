package io.github.pirocks.algebra.values

import java.io.Serializable

interface AlgebraValue : Serializable{
    fun toPrefixNotation(): String
}
