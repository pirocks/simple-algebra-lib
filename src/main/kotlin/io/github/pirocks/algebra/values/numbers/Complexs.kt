package io.github.pirocks.algebra.values.numbers

interface Complex : FieldElement {
    val re: Real
    val im: Real
}

//todo ugly duplication
class FloatComplex(override val re: FloatReal, override val im: FloatReal) : Complex {
    override val zero: FieldElement
        get() = FloatComplex(re.zero, im.zero)
    override val one: FieldElement
        get() = FloatComplex(re.one, im.zero)

    override fun multiplyBin(b: FieldElement): FloatComplex {
        if (b !is FloatComplex) throw TypeError()
        val realRes = (re multiplyBin b.re) addBin (im multiplyBin b.im).inverse()
        val imRes = (re multiplyBin b.im) addBin (im multiplyBin b.re).inverse()
        return FloatComplex(realRes, imRes)
    }

    override fun addBin(b: FieldElement): FieldElement {
        if (b !is FloatComplex) throw TypeError()
        return FloatComplex(re addBin b.re, im addBin b.im)
    }

    override fun inverse(): FieldElement {
        return FloatComplex(re.inverse(), im.inverse())
    }
}

class DoubleComplex(override val re: DoubleReal, override val im: DoubleReal) : Complex {
    override val zero: DoubleComplex
        get() = DoubleComplex(re.zero, im.zero)
    override val one: DoubleComplex
        get() = DoubleComplex(re.one, im.zero)

    override fun multiplyBin(b: FieldElement): DoubleComplex {
        if (b !is DoubleComplex) throw TypeError()
        val realRes = (re multiplyBin b.re) addBin (im multiplyBin b.im).inverse()
        val imRes = (re multiplyBin b.im) addBin (im multiplyBin b.re).inverse()
        return DoubleComplex(realRes, imRes)
    }

    override fun addBin(b: FieldElement): DoubleComplex {
        if (b !is DoubleComplex) throw TypeError()
        return DoubleComplex(re addBin b.re, im addBin b.im)
    }

    override fun inverse(): DoubleComplex {
        return DoubleComplex(re.inverse(), im.inverse())
    }
}