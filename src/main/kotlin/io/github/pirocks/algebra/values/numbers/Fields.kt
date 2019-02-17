package io.github.pirocks.algebra.values.numbers

import io.github.pirocks.algebra.values.AlgebraValue
import java.lang.Double.parseDouble
import java.lang.Float.parseFloat

class TypeError : Exception()


//name	addition	multiplication
//associativity	(a+b)+c=a+(b+c)	(ab)c=a(bc)
//commutativity	a+b=b+a	ab=ba
//distributivity	a(b+c)=ab+ac	(a+b)c=ac+bc
//identity	a+0=a=0+a	a·1=a=1·a
//inverses	a+(-a)=0=(-a)+a	aa^(-1)=1=a^(-1)a if a!=0

interface FieldElement/*<ElementType : FieldElement<ElementType>>*/ : AlgebraValue {
    val zero: FieldElement
    val one: FieldElement
    infix fun multiplyBin(b: FieldElement): FieldElement
    infix fun addBin(b: FieldElement): FieldElement
    fun inverse(): FieldElement
    fun multiply(vararg vals: FieldElement) = vals.reduceRight { fieldElement, acc ->
        fieldElement multiplyBin acc
    }

    fun add(vararg vals: FieldElement) = vals.reduceRight { fieldElement, acc ->
        fieldElement addBin acc
    }

    fun parse(string: String): FieldElement {
        TODO("Parsing from string literal not yet implemented. This functionality may be moved to a standalone function in future")
    }
}


interface Scalar/*<T: FieldElement<T>>*/ : FieldElement/*<T>*/

open class FloatFieldVal(open val `val`: Float) : Scalar/*<FloatFieldVal>*/ {
    override val zero: FloatFieldVal
        get() = FloatFieldVal(0.0f)

    override val one: FloatFieldVal
        get() = FloatFieldVal(1.0f)

    override fun multiplyBin(b: FieldElement): FloatFieldVal {
        if (b !is FloatFieldVal) throw TypeError()
        return FloatFieldVal(this.`val` * b.`val`)
    }

    override fun addBin(b: FieldElement): FloatFieldVal {
        if (b !is FloatFieldVal) throw TypeError()
        return FloatFieldVal(this.`val` + b.`val`)
    }

    override fun parse(string: String): FloatFieldVal = FloatFieldVal(parseFloat(string))

    override fun inverse(): FloatFieldVal = FloatFieldVal(-this.`val`)

}

open class DoubleFieldVal(open val `val`: Double) : Scalar/*<DoubleFieldVal>*/ {
    override val zero: DoubleFieldVal
        get() = DoubleFieldVal(0.0)

    override val one: DoubleFieldVal
        get() = DoubleFieldVal(1.0)

    override fun multiplyBin(b: FieldElement): DoubleFieldVal {
        if (b !is DoubleFieldVal) throw TypeError()
        return DoubleFieldVal(this.`val` * b.`val`)
    }

    override fun addBin(b: FieldElement): DoubleFieldVal {
        if (b !is DoubleFieldVal) throw TypeError()
        return DoubleFieldVal(this.`val` + b.`val`)
    }

    override fun parse(string: String): DoubleFieldVal = DoubleFieldVal(parseDouble(string))

    override fun inverse(): DoubleFieldVal = DoubleFieldVal(-this.`val`)

}