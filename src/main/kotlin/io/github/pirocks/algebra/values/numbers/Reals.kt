package io.github.pirocks.algebra.values.numbers

import kotlin.reflect.KFunction1

interface Real : FieldElement {
    override val zero: Real
    override val one: Real
    override fun multiplyBin(b: FieldElement): Real
    override fun addBin(b: FieldElement): Real
    override fun inverse(): Real

    val e: Real
    val pi: Real
    fun divide(denominator: Real): Real
    fun exp(base: Real = e): Real
    fun log(base: Real = e): Real
    fun cos(degrees: Boolean = false): Real
    fun sin(degrees: Boolean = false): Real
    fun tan(degrees: Boolean = false): Real = sin(degrees).divide(cos(degrees))
}

//todo ugly duplication with DoubleReal
class FloatReal(override val `val`: Float) : FloatFieldVal(`val`), Real {

    override val zero: FloatReal
        get() = FloatReal(0.0f)
    override val one: FloatReal
        get() = FloatReal(0.0f)

    override fun multiplyBin(b: FieldElement): FloatReal {
        if (b !is FloatReal) throw TypeError()
        return FloatReal(`val` * b.`val`)
    }

    override fun addBin(b: FieldElement): FloatReal {
        if (b !is FloatReal) throw TypeError()
        return FloatReal(`val` + b.`val`)
    }

    override fun inverse(): FloatReal {
        return FloatReal(-`val`)
    }

    override val e: FloatReal
        get() = FloatReal(Math.E.toFloat())
    override val pi: FloatReal
        get() = FloatReal(Math.PI.toFloat())

    override fun divide(denominator: Real): FloatReal {
        if (denominator !is FloatReal) throw TypeError()
        return FloatReal(`val` / denominator.`val`)
    }

    override fun exp(base: Real): Real {
        if (base !is FloatReal) throw TypeError()
        return FloatReal(Math.pow(base.`val`.toDouble(), this.`val`.toDouble()).toFloat())
    }

    override fun log(base: Real): Real {
        if (base !is FloatReal) throw TypeError()
        val baseDouble = base.`val`.toDouble()
        val thisDouble = this.`val`.toDouble()
        val logBase = Math.log(baseDouble)
        val log = Math.log(thisDouble)
        val res = log / logBase
        return FloatReal(res.toFloat())
    }

    private fun trigImpl(degrees: Boolean, trigFunction: KFunction1<Double, Double>): FloatReal {
        if (degrees) {
            return FloatReal(trigFunction(pi.`val` / 180.0 * `val`).toFloat())
        }
        return FloatReal(trigFunction(this.`val`.toDouble()).toFloat())
    }

    override fun cos(degrees: Boolean): Real {
        return trigImpl(degrees, Math::cos)
    }

    override fun sin(degrees: Boolean): Real {
        return trigImpl(degrees, Math::sin)
    }

    override fun tan(degrees: Boolean): Real {
        return trigImpl(degrees, Math::tan)
    }


}

class DoubleReal(override val `val`: Double) : Real, DoubleFieldVal(`val`)/*<DoubleFieldVal>*/ {
    override val zero: DoubleReal
        get() = DoubleReal(0.0)
    override val one: DoubleReal
        get() = DoubleReal(0.0)

    override fun multiplyBin(b: FieldElement): DoubleReal {
        if (b !is DoubleReal) throw TypeError()
        return DoubleReal(`val` * b.`val`)
    }

    override fun addBin(b: FieldElement): DoubleReal {
        if (b !is DoubleReal) throw TypeError()
        return DoubleReal(`val` + b.`val`)
    }

    override fun inverse(): DoubleReal {
        return DoubleReal(-`val`)
    }

    override val e: DoubleReal
        get() = DoubleReal(Math.E)
    override val pi: DoubleReal
        get() = DoubleReal(Math.PI)

    override fun divide(denominator: Real): DoubleReal {
        if (denominator !is DoubleReal) throw TypeError()
        return DoubleReal(`val` / denominator.`val`)
    }

    override fun exp(base: Real): Real {
        if (base !is DoubleReal) throw TypeError()
        return DoubleReal(Math.pow(base.`val`, this.`val`))
    }

    override fun log(base: Real): Real {
        if (base !is DoubleReal) throw TypeError()
        val baseDouble = base.`val`
        val thisDouble = this.`val`
        val logBase = Math.log(baseDouble)
        val log = Math.log(thisDouble)
        val res = log / logBase
        return DoubleReal(res)
    }

    private fun trigImpl(degrees: Boolean, trigFunction: KFunction1<Double, Double>): DoubleReal {
        if (degrees) {
            return DoubleReal(trigFunction(pi.`val` / 180.0 * `val`))
        }
        return DoubleReal(trigFunction(this.`val`))
    }

    override fun cos(degrees: Boolean): Real {
        return trigImpl(degrees, Math::cos)
    }

    override fun sin(degrees: Boolean): Real {
        return trigImpl(degrees, Math::sin)
    }

    override fun tan(degrees: Boolean): Real {
        return trigImpl(degrees, Math::tan)
    }

}