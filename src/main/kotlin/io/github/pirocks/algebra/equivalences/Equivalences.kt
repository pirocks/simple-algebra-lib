package io.github.pirocks.algebra.equivalences

import io.github.pirocks.algebra.AlgebraFormula
import io.github.pirocks.algebra.PatternMember
import io.github.pirocks.algebra.VariableName
import io.github.pirocks.algebra.rewriting.RewritingVisitor
import io.github.pirocks.algebra.rewriting.renameAllVars
import io.github.pirocks.algebra.rewriting.renameVar

/**
 * mostly copy paste from nd-thing logic lib, which will likely be renamed
 */
interface PatternBasedRewriter {
    fun matches(formula: AlgebraFormula): Int

    fun apply(formula: AlgebraFormula,
              targetIndex: Int): AlgebraFormula
}

class MatchSubstitutions {
    val matchedPatterns: MutableMap<PatternMember, AlgebraFormula> = mutableMapOf()
    // from formula variable names to pattern variable names
    val variableSubstitutions: MutableMap<VariableName, VariableName> = mutableMapOf()
}

/**
 * Patterns should not include free variables
 */
abstract class Equivalence : PatternBasedRewriter {
    abstract val patternFrom: AlgebraFormula;
    abstract val patternTo: AlgebraFormula;

    override fun matches(formula: AlgebraFormula): Int {
        return matchesImpl(formula, patternFrom);
    }

    private fun matchesImpl(formula: AlgebraFormula, pattern: AlgebraFormula): Int {
        var res: Int = 0;

        val rewritten = object : RewritingVisitor() {

            override fun rewrite(original: AlgebraFormula): AlgebraFormula {
                if (pattern.matches(original, MatchSubstitutions())) {
                    res++;
                }
                return super.rewrite(original)
            }
        }.rewrite(formula)
        assert(rewritten == (formula))
        return res
    }

    override fun apply(formula: AlgebraFormula, targetIndex: Int): AlgebraFormula {

        /**
         * function applies pattern member rewrite rules.
         */
        fun applySubstitutions(patternTo: AlgebraFormula, matchSubstitutions: MatchSubstitutions): AlgebraFormula {
            return object : RewritingVisitor() {
                override fun rewritePatternMember(original: PatternMember): AlgebraFormula {
                    var substitution = matchSubstitutions.matchedPatterns[original]!!
                    //need to change to new variable names for substitution
                    //also need to correctrly handle case when swapping variable names, which requires temporary var names
                    val tempToFinal = mutableMapOf<VariableName, VariableName>()
                    matchSubstitutions.variableSubstitutions.forEach {
                        val originalVarName = it.value//todo this is confusing. change
                        val finalVarName = it.key
                        val tempVarName = VariableName()
                        substitution = renameVar(substitution, originalVarName, tempVarName)
                        tempToFinal[tempVarName] = finalVarName
                    }
                    tempToFinal.forEach {
                        val temp = it.key
                        val final = it.value
                        substitution = renameVar(substitution, temp, final)
                    }
                    return substitution
                }
            }.rewrite(patternTo as AlgebraFormula)
        }

        var index: Int = 0;
        var patternFound = false;
        val rewritten = object : RewritingVisitor() {
            override fun rewrite(original: AlgebraFormula): AlgebraFormula {
                val matchSubstitutions = MatchSubstitutions()
                if (patternFrom.matches(original, matchSubstitutions)) {
                    try {
                        if (index == targetIndex) {
                            patternFound = true
                            return applySubstitutions(patternTo, matchSubstitutions)
                        }
                    } finally {
                        index++;
                    }
                }

                return super.rewrite(original)
            }
        }.rewrite(formula)
        assert(patternFound)
        return renameAllVars(rewritten)

    }
}


class ArbitraryPatternBasedRewriter(override val patternFrom: AlgebraFormula, override val patternTo: AlgebraFormula) : Equivalence()
