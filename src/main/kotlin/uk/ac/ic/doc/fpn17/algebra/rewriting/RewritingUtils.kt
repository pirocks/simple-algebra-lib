package uk.ac.ic.doc.fpn17.algebra.rewriting

import uk.ac.ic.doc.fpn17.algebra.AlgebraFormula
import uk.ac.ic.doc.fpn17.algebra.Variable
import uk.ac.ic.doc.fpn17.algebra.VariableName

fun renameVar(formula: AlgebraFormula, from: VariableName, to: VariableName): AlgebraFormula {
    return object : RewritingVisitor() {
        override fun rewriteVariable(original: Variable): AlgebraFormula {
            return if (original.name == from)
                Variable(to)
            else
                super.rewriteVariable(original)
        }
    }.rewrite(formula)
}

/**
 * Useful for changing the names of every variable in a formula.
 */
fun renameAllVars(formula: AlgebraFormula): AlgebraFormula {
    val vars = hashSetOf<VariableName>()
    object : RewritingVisitor() {
        override fun rewriteVariable(original: Variable): AlgebraFormula {
            vars.add(original.name)
            return super.rewriteVariable(original);
        }
    }.rewrite(formula)
    var res = formula
    vars.forEach {
        res = renameVar(res, it, VariableName())
    }
    return res
}