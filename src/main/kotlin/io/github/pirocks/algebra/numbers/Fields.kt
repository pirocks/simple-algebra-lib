package io.github.pirocks.algebra.numbers

import java.lang.Double.parseDouble
import java.lang.Float.parseFloat

class TypeError : Exception()

interface AlgebraValue

//name	addition	multiplication
//associativity	(a+b)+c=a+(b+c)	(ab)c=a(bc)
//commutativity	a+b=b+a	ab=ba
//distributivity	a(b+c)=ab+ac	(a+b)c=ac+bc
//identity	a+0=a=0+a	a·1=a=1·a
//inverses	a+(-a)=0=(-a)+a	aa^(-1)=1=a^(-1)a if a!=0

interface FieldElement/*<ElementType : FieldElement<ElementType>>*/ :
        AlgebraValue {
    val zero: FieldElement
    val one: FieldElement
    infix fun multiplyBin(b: FieldElement): FieldElement
    infix fun addBin(b: FieldElement): FieldElement
    fun inverse(): FieldElement
    fun multiply(vararg vals: FieldElement) =
            vals.reduceRight { fieldElement, acc ->
                fieldElement multiplyBin acc
            }

    fun add(vararg vals: FieldElement) = vals.reduceRight { fieldElement, acc ->
        fieldElement addBin acc
    }

    fun parse(string: String): FieldElement
}


interface Scalar/*<T: FieldElement<T>>*/ : FieldElement/*<T>*/

class FloatFieldVal(val `val`: Float) : Scalar/*<FloatFieldVal>*/ {
    override val zero: FloatFieldVal
        get() = FloatFieldVal(0.0f)

    override val one: FloatFieldVal
        get() = FloatFieldVal(1.0f)

    override fun multiplyBin(b: FieldElement): FloatFieldVal {
        if (b !is FloatFieldVal) throw TypeError()
        return FloatFieldVal(this.`val`
                * b.`val`)
    }

    override fun addBin(b: FieldElement): FloatFieldVal {
        if (b !is FloatFieldVal) throw TypeError()
        return FloatFieldVal(this.`val` + b
                .`val`)
    }

    override fun parse(string: String): FloatFieldVal = FloatFieldVal(parseFloat(string))

    override fun inverse(): FloatFieldVal = FloatFieldVal(-this.`val`)

}

class DoubleFieldVal(val `val`: Double) : Scalar/*<DoubleFieldVal>*/ {
    override val zero: DoubleFieldVal
        get() = DoubleFieldVal(0.0)

    override val one: DoubleFieldVal
        get() = DoubleFieldVal(1.0)

    override fun multiplyBin(b: FieldElement): DoubleFieldVal {
        if (b !is FloatFieldVal) throw TypeError()
        return DoubleFieldVal(this
                .`val` * b.`val`)
    }

    override fun addBin(b: FieldElement): DoubleFieldVal {
        if (b !is FloatFieldVal) throw TypeError()
        return DoubleFieldVal(this.`val` + b
                .`val`)
    }

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
    override fun at(i: Int): DoubleFieldVal = DoubleFieldVal(`val`[i])
}

open class Dimension(open vararg val n: Int)

open class SingleDimension(val m: Int) : Dimension(m)

class DoubleVectorSpace<Dim : SingleDimension>(val dim: Dim) : VectorSpace<DoubleVector<Dim>, DoubleFieldVal> {
    override val scalarIdentity: DoubleFieldVal
        get() = DoubleFieldVal(1.0)
    override val vectorAdditiveIdentity: DoubleVector<Dim>
        get() = DoubleVector(Array(dim.m) { 0.0 }.toDoubleArray())

    override fun inverse(v: DoubleVector<Dim>): DoubleVector<Dim> =
            DoubleVector(v.`val`.map { -it }.toDoubleArray())

    override fun addBin(a: DoubleVector<Dim>, b: DoubleVector<Dim>): DoubleVector<Dim> {
        return DoubleVector(a.`val`.zip(b.`val`).map { it.first + it.second }.toDoubleArray())
    }

    override fun multiply(a: DoubleFieldVal, v: DoubleVector<Dim>): DoubleVector<Dim> {
        return DoubleVector(v.`val`.map { a.`val` * it }.toDoubleArray())
    }

}


/**
 * For multidimensional tensors use tensors of tensors. All tensors are integer indexed.
 */
interface Tensor<Dim : SingleDimension, Of : AlgebraValue, UnderlyingScalar : Scalar/*<UnderlyingScalar>*/> :
        AlgebraValue {
    infix fun at(i: Int): Of

}


/**
 *
 */
interface Matrix<Dim1 : SingleDimension, Dim2 : SingleDimension, OnScalar : Scalar/*<OnScalar>*/> :
        Tensor<Dim2, Tensor<Dim1, OnScalar, OnScalar>, OnScalar>