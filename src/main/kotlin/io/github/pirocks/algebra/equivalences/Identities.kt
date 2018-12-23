package io.github.pirocks.algebra.equivalences

import io.github.pirocks.algebra.*


val availableIdentities = arrayOf(AMinusA(),
        ZeroAddition(),
        AdditionAssociativity(),
        AdditionCommutativity(),
        MultiplicationCommutativity(),
        OneMultiplication(),
        ZeroMultiplication(),
        MultiplicationAssociativity())

class AMinusA : Equivalence() {
    val a = AllowAllVars()
    override val patternFrom: AlgebraFormula = `+`(a, `-`(a))
    override val patternTo: AlgebraFormula = `0`
}

class ZeroAddition : Equivalence() {
    val a = AllowAllVars()
    override val patternFrom: AlgebraFormula = `+`(`0`, a)
    override val patternTo: AlgebraFormula = a
}

class AdditionAssociativity : Equivalence() {
    val a = AllowAllVars()
    val b = AllowAllVars()
    val c = AllowAllVars()
    override val patternFrom: AlgebraFormula = `+`(a, `+`(b, c))
    override val patternTo: AlgebraFormula = `+`(`+`(a, b), c)

}

class AdditionCommutativity : Equivalence() {

    val a = AllowAllVars()
    val b = AllowAllVars()
    override val patternFrom: AlgebraFormula = `+`(a, b)
    override val patternTo: AlgebraFormula = `+`(b, a)
}

class MultiplicationCommutativity : Equivalence() {

    val a = AllowAllVars()
    val b = AllowAllVars()
    override val patternFrom: AlgebraFormula = `*`(a, b)
    override val patternTo: AlgebraFormula = `*`(b, a)
}

class OneMultiplication : Equivalence() {
    val a = AllowAllVars()
    override val patternFrom: AlgebraFormula = `*`(`1`, a)
    override val patternTo: AlgebraFormula = a
}

class ZeroMultiplication : Equivalence() {

    val a = AllowAllVars()
    override val patternFrom: AlgebraFormula = `*`(`0`, a)
    override val patternTo: AlgebraFormula = `0`
}

class MultiplicationAssociativity : Equivalence() {
    val a = AllowAllVars()
    val b = AllowAllVars()
    val c = AllowAllVars()
    override val patternFrom: AlgebraFormula = `*`(a, `*`(b, c))
    override val patternTo: AlgebraFormula = `*`(`*`(a, b), c)
}

