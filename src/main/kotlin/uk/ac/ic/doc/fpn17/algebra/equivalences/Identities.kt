package uk.ac.ic.doc.fpn17.algebra.equivalences

import uk.ac.ic.doc.fpn17.algebra.*

class AMinusA : Equivalence() {
    val a = AllowAllVars()
    override val patternFrom: AlgebraFormula = Addition(a, UMinus(a))
    override val patternTo: AlgebraFormula = Zero()
}