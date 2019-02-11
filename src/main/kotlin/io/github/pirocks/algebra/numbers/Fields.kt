package io.github.pirocks.algebra.numbers

import io.github.pirocks.algebra.RealApproxiamation
import java.lang.Double.parseDouble
import java.lang.Float.parseFloat


interface AlgebraValue

//name	addition	multiplication
//associativity	(a+b)+c=a+(b+c)	(ab)c=a(bc)
//commutativity	a+b=b+a	ab=ba
//distributivity	a(b+c)=ab+ac	(a+b)c=ac+bc
//identity	a+0=a=0+a	a·1=a=1·a
//inverses	a+(-a)=0=(-a)+a	aa^(-1)=1=a^(-1)a if a!=0

interface Field<ElementType : FieldElement>  {
    val zero: ElementType
    val one: ElementType
    fun multiplyBin(a: ElementType, b: ElementType): ElementType
    fun addBin(a: ElementType, b: ElementType): ElementType
    fun inverse(a: ElementType): ElementType
    fun multiply(vararg vals: ElementType) = vals.reduceRight { fieldElement, acc -> multiplyBin(fieldElement, acc) }
    fun add(vararg vals: ElementType) = vals.reduceRight { fieldElement, acc -> addBin(fieldElement, acc) }
    fun parse(string: String): ElementType
}


interface FieldElement : AlgebraValue

interface Scalar : FieldElement

object FloatField : Field<FloatVal> {
    override val zero: FloatVal
        get() = FloatVal(0.0f)

    override val one: FloatVal
        get() = FloatVal(1.0f)

    override fun multiplyBin(a: FloatVal, b: FloatVal): FloatVal = FloatVal(a.`val` * b.`val`)

    override fun addBin(a: FloatVal, b: FloatVal): FloatVal = FloatVal(a.`val` + b.`val`)

    override fun parse(string: String): FloatVal = FloatVal(parseFloat(string))

    override fun inverse(a: FloatVal): FloatVal = FloatVal(-a.`val`)

}

class FloatVal(val `val`: Float) : Scalar


object DoubleField : Field<DoubleVal> {
    override val zero: DoubleVal
        get() = DoubleVal(0.0)

    override val one: DoubleVal
        get() = DoubleVal(1.0)

    override fun multiplyBin(a: DoubleVal, b: DoubleVal): DoubleVal = DoubleVal(a.`val` * b.`val`)

    override fun addBin(a: DoubleVal, b: DoubleVal): DoubleVal = DoubleVal(a.`val` + b.`val`)

    override fun parse(string: String): DoubleVal = DoubleVal(parseDouble(string))

    override fun inverse(a: DoubleVal): DoubleVal = DoubleVal(-a.`val`)

}

class DoubleVal(val `val`: Double) : RealApproxiamation


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
interface VectorSpace<VectorType : Vector, ElementType : Scalar, OnField : Field<ElementType>> {
    val scalarIdentity: Scalar
    val vectorAdditiveIdentity: Vector
    fun inverse(v: VectorType): VectorType
    fun addBin(a: VectorType, b: VectorType): VectorType
    fun add(vararg vals: VectorType) = vals.reduceRight { elem, acc -> addBin(elem, acc) }
    fun multiply(a: ElementType, v: VectorType): VectorType
}

interface Vector : AlgebraValue

class DoubleVector(val `val`: DoubleArray) : Vector

open class Dimension(val n: Int)

class DoubleVectorSpace<Dim : Dimension>(val dims: Dim) : VectorSpace<DoubleVector, DoubleVal, DoubleField> {
    override val scalarIdentity: Scalar
        get() = DoubleVal(1.0)
    override val vectorAdditiveIdentity: Vector
        get() = DoubleVector(Array(dims.n) { 0.0 }.toDoubleArray())

    override fun inverse(v: DoubleVector): DoubleVector =
            DoubleVector(v.`val`.map { -it }.toDoubleArray())

    override fun addBin(a: DoubleVector, b: DoubleVector): DoubleVector {
        return DoubleVector(a.`val`.zip(b.`val`).map { it.first + it.second }.toDoubleArray())
    }

    override fun multiply(a: DoubleVal, v: DoubleVector): DoubleVector {
        return DoubleVector(v.`val`.map { a.`val` * it }.toDoubleArray())
    }

}
