package io.github.pirocks.algebra.rewriting

import io.github.pirocks.algebra.*
import io.github.pirocks.algebra.numbers.AlgebraValue
import io.github.pirocks.algebra.numbers.FieldElement

abstract class RewritingVisitor {
    open fun <Tout : AlgebraValue> rewrite(original: AlgebraFormula<*, Tout>): AlgebraFormula<*, Tout> {
        return when (original) {
            is BinaryFormula<*, *, *> -> rewriteBinaryFormula(original)
            is UnaryFormula -> rewriteUnaryFormula(original)
//            is Constant -> rewriteConstant(original)
            is Variable -> rewriteVariable(original)
            is FunctionApplication -> rewriteFunctionApplication(original)
        }
    }

    open fun rewriteFunctionApplication(original: FunctionApplication<*, *>): AlgebraFormula<*, *> {
        return original
    }

    open fun <T : FieldElement<T>> rewriteBinaryFormula(original: BinaryFormula<T, T, T>): AlgebraFormula<*, T> {
        return when (original) {
            is Addition -> rewriteAddition(original)
            is Multiplication -> rewriteMultiplication(original)
//            is Division -> rewriteDivision(original)
//            is Exponentiation -> rewriteExponentiation(original)
        }
    }

    open fun <T : FieldElement<T>> rewriteAddition(original: Addition<T>): AlgebraFormula<*, T> {
        return Addition(rewrite(original.left), rewrite(original.right))
    }

    open fun <T : FieldElement<T>> rewriteMultiplication(original: Multiplication<T>): AlgebraFormula<*, T> {
        return Multiplication(rewrite(original.left), rewrite(original.right))
    }

//    open fun rewriteDivision(original: Division): AlgebraFormula {
//        return Division(rewrite(original.left), rewrite(original.right))
//    }
//
//    open fun rewriteExponentiation(original: Exponentiation): AlgebraFormula {
//        return Exponentiation(rewrite(original.left), rewrite(original.right))
//    }

    open fun <Tin : AlgebraValue, Tout : AlgebraValue> rewriteUnaryFormula(original: UnaryFormula<Tin, Tout>): AlgebraFormula<*, Tout> {
        return when (original) {
//            is NaturalLog -> rewriteNaturalLog(original)
//            is Cos -> rewriteCos(original)
            is UMinus<*> -> {
                val original1: UMinus<*> = original
                rewriteUMinus(original1)
            }
        }
    }

    private fun <T> rewriteUMinus(original: UMinus<*>): AlgebraFormula<*, T> {
        return original.rewriteAccept(this)
    }

//    open fun rewriteNaturalLog(original: NaturalLog): AlgebraFormula {
//        return NaturalLog(rewrite(original.parameter))
//    }
//
//    open fun rewriteCos(original: Cos): AlgebraFormula {
//        return Cos(rewrite(original.parameter))
//    }
//
//    open fun rewriteConstant(original: Constant): AlgebraFormula {
//        return original
//    }

    open fun rewriteVariable(original: Variable<*>): AlgebraFormula<*, *> {
        if (original is PatternMember)
            return rewritePatternMember(original)
        return original
    }

    open fun rewritePatternMember(original: PatternMember<*>): AlgebraFormula<*, *> {
        return original
    }

}