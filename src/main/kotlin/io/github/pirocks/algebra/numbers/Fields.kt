package io.github.pirocks.algebra.numbers

import java.lang.Double.parseDouble
import java.lang.Float.parseFloat

interface AlgebraValue

//name	addition	multiplication
//associativity	(a+b)+c=a+(b+c)	(ab)c=a(bc)
//commutativity	a+b=b+a	ab=ba
//distributivity	a(b+c)=ab+ac	(a+b)c=ac+bc
//identity	a+0=a=0+a	a·1=a=1·a
//inverses	a+(-a)=0=(-a)+a	aa^(-1)=1=a^(-1)a if a!=0

interface FieldElement<ElementType : FieldElement<ElementType>>: AlgebraValue  {
    val zero: ElementType
    val one: ElementType
    infix fun multiplyBin(b: ElementType): ElementType
    infix fun addBin(b: ElementType): ElementType
    fun inverse(): ElementType
    fun multiply(vararg vals: ElementType) = vals.reduceRight { fieldElement, acc -> fieldElement multiplyBin acc }
    fun add(vararg vals: ElementType) = vals.reduceRight { fieldElement, acc -> fieldElement addBin acc }
    fun parse(string: String): ElementType
}


interface Scalar<T: FieldElement<T>> : FieldElement<T>

class FloatFieldVal(val `val`: Float) : Scalar<FloatFieldVal> {
    override val zero: FloatFieldVal
        get() = FloatFieldVal(0.0f)

    override val one: FloatFieldVal
        get() = FloatFieldVal(1.0f)

    override fun multiplyBin(b: FloatFieldVal): FloatFieldVal = FloatFieldVal(this.`val` * b.`val`)

    override fun addBin(b: FloatFieldVal): FloatFieldVal = FloatFieldVal(this.`val` + b.`val`)

    override fun parse(string: String): FloatFieldVal = FloatFieldVal(parseFloat(string))

    override fun inverse(): FloatFieldVal = FloatFieldVal(-this.`val`)

}

class DoubleFieldVal(val `val`: Double) : Scalar<DoubleFieldVal> {
    override val zero: DoubleFieldVal
        get() = DoubleFieldVal(0.0)

    override val one: DoubleFieldVal
        get() = DoubleFieldVal(1.0)

    override fun multiplyBin(b: DoubleFieldVal): DoubleFieldVal = DoubleFieldVal(this.`val` * b.`val`)

    override fun addBin(b: DoubleFieldVal): DoubleFieldVal = DoubleFieldVal(this.`val` + b.`val`)

    override fun parse(string: String): DoubleFieldVal = DoubleFieldVal(parseDouble(string))

    override fun inverse(): DoubleFieldVal = DoubleFieldVal(-this.`val`)

}



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
interface VectorSpace<VectorType : Vector, OnField : Scalar<*>> {
    val scalarIdentity: OnField
    val vectorAdditiveIdentity: Vector
    fun inverse(v: VectorType): VectorType
    fun addBin(a: VectorType, b: VectorType): VectorType
    fun add(vararg vals: VectorType) = vals.reduceRight { elem, acc -> addBin(elem, acc) }
    fun multiply(a: OnField, v: VectorType): VectorType
}

interface Vector : AlgebraValue

class DoubleVector(val `val`: DoubleArray) : Vector

open class Dimension(val n: Int)

class DoubleVectorSpace<Dim : Dimension>(val dims: Dim) : VectorSpace<DoubleVector, DoubleFieldVal> {
    override val scalarIdentity: DoubleFieldVal
        get() = DoubleFieldVal(1.0)
    override val vectorAdditiveIdentity: Vector
        get() = DoubleVector(Array(dims.n) { 0.0 }.toDoubleArray())

    override fun inverse(v: DoubleVector): DoubleVector =
            DoubleVector(v.`val`.map { -it }.toDoubleArray())

    override fun addBin(a: DoubleVector, b: DoubleVector): DoubleVector {
        return DoubleVector(a.`val`.zip(b.`val`).map { it.first + it.second }.toDoubleArray())
    }

    override fun multiply(a: DoubleFieldVal, v: DoubleVector): DoubleVector {
        return DoubleVector(v.`val`.map { a.`val` * it }.toDoubleArray())
    }

}
