package io.github.pirocks.algebra.values

import io.github.pirocks.algebra.values.numbers.DoubleFieldVal
import io.github.pirocks.algebra.values.numbers.Scalar

// 1. Commutativity:
//X+Y=Y+X.
//(1)
//
//2. Associativity of vector addition:
//(X+Y)+Z=X+(Y+Z).
//(2)
//
//3. Additive identity: For all X,
//0+X=X+0=X.
//(3)
//
//4. Existence of additive inverse: For any X, there exists a -X such that
//X+(-X)=0.
//(4)
//
//5. Associativity of scalar multiplication:
//r(sX)=(rs)X.
//(5)
//
//6. Distributivity of scalar sums:
//(r+s)X=rX+sX.
//(6)
//
//7. Distributivity of vector sums:
//r(X+Y)=rX+rY.
//(7)
//
//8. Scalar multiplication identity:
//1X=X.
//(8)
interface VectorSpace<VectorType : Vector<VectorType>, OnField : Scalar/*<*>*/> {
    val scalarIdentity: OnField
    val vectorAdditiveIdentity: Vector<VectorType>
    fun inverse(v: VectorType): VectorType
    fun addBin(a: VectorType, b: VectorType): VectorType
    fun add(vararg vals: VectorType) = vals.reduceRight { elem, acc -> addBin(elem, acc) }
    fun multiply(a: OnField, v: VectorType): VectorType
}

interface Vector<T : Vector<T>> : AlgebraValue

class DoubleVector<Dim : SingleDimension>(val `val`: DoubleArray) : Vector<DoubleVector<Dim>>,
        Tensor<Dim, DoubleFieldVal, DoubleFieldVal> {
    override fun toPrefixNotation(): String = """(Vector ${`val`.toList().joinToString(separator = " ") { it.toString() }})""".trimIndent()

    override fun at(i: Int): DoubleFieldVal = DoubleFieldVal(`val`[i])
}


class DoubleVectorSpace<Dim : SingleDimension>(val dim: Dim) : VectorSpace<DoubleVector<Dim>, DoubleFieldVal> {
    override val scalarIdentity: DoubleFieldVal
        get() = DoubleFieldVal(1.0)
    override val vectorAdditiveIdentity: DoubleVector<Dim>
        get() = DoubleVector(Array(dim.m) { 0.0 }.toDoubleArray())

    override fun inverse(v: DoubleVector<Dim>): DoubleVector<Dim> = DoubleVector(v.`val`.map { -it }.toDoubleArray())

    override fun addBin(a: DoubleVector<Dim>, b: DoubleVector<Dim>): DoubleVector<Dim> {
        return DoubleVector(a.`val`.zip(b.`val`).map { it.first + it.second }.toDoubleArray())
    }

    override fun multiply(a: DoubleFieldVal, v: DoubleVector<Dim>): DoubleVector<Dim> {
        return DoubleVector(v.`val`.map { a.`val` * it }.toDoubleArray())
    }

}
