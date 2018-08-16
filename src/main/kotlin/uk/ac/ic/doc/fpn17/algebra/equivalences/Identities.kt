package uk.ac.ic.doc.fpn17.algebra.equivalences

import uk.ac.ic.doc.fpn17.algebra.*

private typealias Mul = Multiplication
private typealias Add = Addition
private typealias Div = Division
private typealias `-` = UMinus
private typealias `+` = Addition
private typealias `*` = Multiplication
private typealias `0` = Zero
private typealias `^` = Exponentiation
private typealias  `1` = One

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
    override val patternTo: AlgebraFormula = `0`()
}

class ZeroAddition : Equivalence() {
    val a = AllowAllVars()
    override val patternFrom: AlgebraFormula = `+`(`0`(), a)
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
    override val patternFrom: AlgebraFormula = `*`(`1`(), a)
    override val patternTo: AlgebraFormula = a
}

class ZeroMultiplication : Equivalence() {

    val a = AllowAllVars()
    override val patternFrom: AlgebraFormula = `*`(`0`(), a)
    override val patternTo: AlgebraFormula = a
}

class MultiplicationAssociativity : Equivalence() {
    val a = AllowAllVars()
    val b = AllowAllVars()
    val c = AllowAllVars()
    override val patternFrom: AlgebraFormula = `*`(a, `*`(b, c))
    override val patternTo: AlgebraFormula = `*`(`*`(a, b), c)
}

