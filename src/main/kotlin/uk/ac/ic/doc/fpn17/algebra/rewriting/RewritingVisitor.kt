package uk.ac.ic.doc.fpn17.algebra.rewriting

import uk.ac.ic.doc.fpn17.algebra.*

abstract class RewritingVisitor() {
    open fun rewrite(original: AlgebraFormula): AlgebraFormula {
        return when (original) {
            is BinaryFormula -> rewriteBinaryFormula(original)
            is UnaryFormula -> rewriteUnaryFormula(original)
            is Constant -> rewriteConstant(original)
            is Variable -> rewriteVariable(original)
        }
    }

    open fun rewriteBinaryFormula(original: BinaryFormula): AlgebraFormula {
        return when (original) {
            is Addition -> rewriteAddition(original)
            is Multiplication -> rewriteMultiplication(original)
            is Division -> rewriteDivision(original)
            is Exponentiation -> rewriteExponentiation(original)
        }
    }

    open fun rewriteAddition(original: Addition): AlgebraFormula {
        return Addition(rewrite(original.left), rewrite(original.right))
    }

    open fun rewriteMultiplication(original: Multiplication): AlgebraFormula {
        return Multiplication(rewrite(original.left), rewrite(original.right))
    }

    open fun rewriteDivision(original: Division): AlgebraFormula {
        return Division(rewrite(original.left), rewrite(original.right))
    }

    open fun rewriteExponentiation(original: Exponentiation): AlgebraFormula {
        return Exponentiation(rewrite(original.left), rewrite(original.right))
    }

    open fun rewriteUnaryFormula(original: UnaryFormula): AlgebraFormula {
        return when (original) {
            is NaturalLog -> rewriteNaturalLog(original)
            is Cos -> rewriteCos(original)
        }
    }

    open fun rewriteNaturalLog(original: NaturalLog): AlgebraFormula {
        return NaturalLog(rewrite(original.parameter))
    }

    open fun rewriteCos(original: Cos): AlgebraFormula {
        return Cos(rewrite(original.parameter))
    }

    open fun rewriteConstant(original: Constant): AlgebraFormula {
        return original
    }

    open fun rewriteVariable(original: Variable): AlgebraFormula {
        return original
    }

}