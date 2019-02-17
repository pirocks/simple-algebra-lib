package io.github.pirocks.algebra

import io.github.pirocks.algebra.equivalences.MatchSubstitutions
import io.github.pirocks.algebra.values.AlgebraValue
import io.github.pirocks.algebra.values.numbers.DoubleFieldVal
import io.github.pirocks.algebra.values.numbers.FieldElement
import io.github.pirocks.algebra.values.numbers.TypeError
import java.io.Serializable
import java.util.*


/**
 * Base class for all algebraic expressions.
 */
sealed class AlgebraFormula : Serializable {
    /**
     * Evaluates an expression based on a map of variable values. Currently only supports double precision arithmetic
     * @param variableValues variable values to evaluate with.
     */
    abstract fun eval(
            variableValues: Map<VariableName, AlgebraValue>): AlgebraValue

    /**
     * Returns an array of any parameters/subformulas a AlegbraFormula can contain.
     */
    abstract val parameters: List<AlgebraFormula>

    /**
     * Outputs the formula in prefix notation.
     */
    abstract fun toPrefixNotation(): String

    /**
     * Returns true iff two formulas are structurally the same. In other words:
     *  a + b == c + d != a * b
     *  a + a == b + b != 2 * b
     *  a * a != a * b
     *
     *  This is similar to alpha equivalence for lambda calculus.
     */
    override fun equals(other: Any?): Boolean {
        if (other !is AlgebraFormula) return false
        return equalsImpl(other, EqualsContext())
    }

    internal open fun equalsImpl(other: AlgebraFormula, equalsContext: EqualsContext): Boolean {
        if (this.javaClass != other.javaClass) return false
        if (other.parameters.size != parameters.size) return false
        return parameters.zip(other.parameters).all { it.first.equalsImpl(it.second, equalsContext) }
    }

    /**
     * Hashcode behaves consistently with equals.
     */
    override fun hashCode(): Int = hashCodeImpl(HashCodeContext())

    internal abstract fun hashCodeImpl(hashCodeContext: HashCodeContext): Int

    /**
     * Tests if a particular pattern matches a formula.
     * You probably want to use io.github.pirocks.algebra.equivalences.Equivalence.matches instead of this method.
     * @see io.github.pirocks.algebra.equivalences.Equivalence.matches
     * @param other
     * @param matchContext stores pattern matches in a map. Should usually be initialized to a new MatchSubstitutions object.
     * @return True if this pattern matches `other`. False otherwise
     */
    open fun matches(other: AlgebraFormula, matchContext: MatchSubstitutions): Boolean {
        if (this.javaClass != other.javaClass) {
            return false
        }
        if (other.parameters.size != parameters.size) return false
        return parameters.zip(other.parameters).all { it.first.matches(it.second, matchContext) }
    }

    /**
     * Output expression in mathml format. Mathml support varies across browsers. May not be compatible with all browsers.
     * @see io.github.pirocks.algebra.AlgebraFormula.toHtml
     */
    abstract fun toMathML2(): String

    /**
     * Output expression in html format, by wrapping mathml in a <math> tag. Mathml support varies across browsers.
     * May not be compatible with all browsers.
     * @see io.github.pirocks.algebra.AlgebraFormula.toMathML2
     */
    fun toHtml(): String = ("<math> <mrow>" + toMathML2() + "</mrow> </math>").replace("\\s(?!separators)".toRegex(), "").trim().trimIndent()
}

internal class EqualsContext(val thisToOtherVariableName: MutableMap<VariableName, VariableName> = mutableMapOf())

internal class HashCodeContext {
    val variableToNum: MutableMap<VariableName, Int> = mutableMapOf()
}

sealed class Constant : AlgebraFormula() {
    override val parameters: List<AlgebraFormula> = emptyList()
}

/**
 * Represents any constant.
 */
open class DoublePrecisionConstant(val `val`: Double) : Constant() {
    override fun eval(
            variableValues: Map<VariableName, AlgebraValue>): AlgebraValue {
        return DoubleFieldVal(`val`)
    }

    override fun toMathML2(): String = """<mn>$`val`</mn>"""


    override fun toPrefixNotation(): String = `val`.toString()

    override fun hashCodeImpl(hashCodeContext: HashCodeContext): Int {
        return `val`.hashCode()
    }
}

/**
 * Standard object for zero.
 */
class Zero : DoublePrecisionConstant(0.0)

/**
 * Standard object for one
 */
class One : DoublePrecisionConstant(1.0)

/**
 * Represents any operator with two parameters e.g. +,*,^,/, etc.
 */
sealed class BinaryFormula(val left: AlgebraFormula,
                           val right: AlgebraFormula) :
        AlgebraFormula() {//todo maybe add overridable comon input type
    /**
     * String which represents this operator. Should be valid as the <mo> for mathml.
     */
    abstract val operatorString: String

    override fun toPrefixNotation(): String = """(${operatorString} ${left.toPrefixNotation()} ${right.toPrefixNotation()})"""
    override val parameters: List<AlgebraFormula> = listOf(left, right)
    override fun hashCodeImpl(hashCodeContext: HashCodeContext): Int = operatorString.hashCode() + 101 * left.hashCodeImpl(hashCodeContext) + 31 * right.hashCodeImpl(hashCodeContext)
    override fun toMathML2(): String = """
        <mrow>
        <mfenced separators="">
        <mrow>${left.toMathML2()}</mrow>
        <mo>${operatorString}</mo>
        <mrow>${right.toMathML2()}</mrow>
        </mfenced>
        </mrow>
    """
}

/**
 * @param name A name for the variable in question.
 * todo add a name index?
 * @see FunctionName
 */
data class VariableName(val name: String = "" + getAndIncrementCounter(), val uuid: UUID = UUID.randomUUID()) {
    companion object {
        var varCounter = 0
        fun getAndIncrementCounter(): Int {
            varCounter += 1
            return varCounter
        }
    }
}

/**
 * Represents a variable leaf node in a formula.
 * Different from a VariableName, since VariableNames cannot appear in a formula AST.
 * @see VariableName
 */
open class Variable(open val name: VariableName = VariableName()) :
        AlgebraFormula() {
    override fun toMathML2(): String = """<mi>${name.name}</mi>"""

    override fun eval(
            variableValues: Map<VariableName, AlgebraValue>): AlgebraValue {
        return variableValues[name]
                ?: throw IllegalArgumentException("Not all variable values where provided")
    }

    override fun equalsImpl(other: AlgebraFormula, equalsContext: EqualsContext): Boolean {
        if (other !is Variable) return false
        if (name in equalsContext.thisToOtherVariableName.keys) {
            return equalsContext.thisToOtherVariableName[name] == other.name
        } else {
            if (other.name in equalsContext.thisToOtherVariableName.values) {
                return false//prevent multiple variables mapping to the same value. This would allow situations in which a*a+c was equal to a*b+c.
            }
            equalsContext.thisToOtherVariableName[name] = other.name
            return true
        }
    }

    override fun hashCodeImpl(hashCodeContext: HashCodeContext): Int {
        return hashCodeContext.variableToNum.computeIfAbsent(this.name) { hashCodeContext.variableToNum.size }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Variable) return false
        // Two variables are equal if they are both variables on top level.
        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override val parameters: List<AlgebraFormula> = emptyList()
    override fun toPrefixNotation(): String = name.name

}

/**
 * A special type of variable which can be used as part of a pattern. Override matches for custom functionality.
 */
abstract class PatternMember : Variable() {
    abstract override fun matches(other: AlgebraFormula, matchContext: MatchSubstitutions): Boolean
}

class AllowAllVars : PatternMember() {
    override fun matches(other: AlgebraFormula, matchContext: MatchSubstitutions): Boolean {
        return if (this in matchContext.matchedPatterns) {
            matchContext.matchedPatterns[this]!!.equalsImpl(other, EqualsContext(matchContext.variableSubstitutions))
        } else {
            matchContext.matchedPatterns[this] = other
            true
        }
    }
}


class Addition(left: AlgebraFormula, right: AlgebraFormula) :
        BinaryFormula(left, right) {
    override fun eval(
            variableValues: Map<VariableName, AlgebraValue>): FieldElement {
        val leftEval =
                left.eval(variableValues) as? FieldElement ?: throw TypeError()
        val rightEval = right.eval(variableValues) as? FieldElement ?: throw
        TypeError()
        return (leftEval.addBin(rightEval))
    }

    override val operatorString: String = "+"
}

class Multiplication(left: AlgebraFormula, right: AlgebraFormula) : BinaryFormula(left, right) {
    override fun eval(variableValues: Map<VariableName, AlgebraValue>):
            FieldElement/*<*>*/ {
        val leftEval = left.eval(variableValues) as? FieldElement ?: throw
        TypeError()
        val rightEval = right.eval(variableValues) as? FieldElement ?: throw
        TypeError()
        return leftEval multiplyBin rightEval
    }

    override val operatorString: String = "*"
}

/*
todo fields and division not a thing, so not supported atm.
class Division(val numerator: AlgebraFormula<FieldElement>, val denominator: AlgebraFormula<FieldElement>) : BinaryFormula<FieldElement>(numerator, denominator) {
    override fun eval(variableValues: Map<VariableName, AlgebraValue>): FieldElement = numerator.eval(variableValues) / denominator.eval(variableValues)
    override val operatorString: String = "/"
    override fun toMathML2(): String = """
        <mrow>
        <mfrac>
        <mrow>${numerator.toMathML2()}</mrow>
        <mrow>${denominator.toMathML2()}</mrow>
        </mfrac>
        </mrow>
    """
}
*/

/*
todo fields and exponentiation sorta not a thing, so not supported atm.

class Exponentiation(val base: FieldElement, val exponent: FieldElement) : BinaryFormula<FieldElement>(base, exponent) {
    override fun eval(variableValues: Map<VariableName, AlgebraValue>): AlgebraValue = Math.exp(Math.log(base.eval(variableValues)) * exponent.eval(variableValues))

    override val operatorString: String = "^"

}
*/

/**
 * Represents a formaula which has one formula, e.g. log, cos,sin, unary minus.
 */
sealed class UnaryFormula(open val parameter: AlgebraFormula) : AlgebraFormula() {
    override val parameters: List<AlgebraFormula> = listOf(parameter)
    abstract val operatorString: String
    override fun toPrefixNotation(): String = """(${operatorString} ${parameter.toPrefixNotation()})"""
    override fun toMathML2(): String = """
        <mrow>
        <mfenced separators="">
        <mi>${operatorString}</mi>
        <mo> &ApplyFunction; </mo>
        <mrow>${parameter.toMathML2()}</mrow>
        </mfenced>
        </mrow>
    """

    override fun hashCodeImpl(hashCodeContext: HashCodeContext): Int = operatorString.hashCode() + 31 * parameter.hashCodeImpl(hashCodeContext)
}

//class NaturalLog(parameter: AlgebraFormula<*>) : UnaryFormula(parameter) {
//    override fun eval(variableValues: Map<VariableName, AlgebraValue>): AlgebraValue = Math.log(parameter.eval(variableValues))
//
//    override val operatorString: String = "log"
//}
//
//class Cos(parameter: AlgebraFormula<*>) : UnaryFormula(parameter) {
//    override fun eval(variableValues: Map<VariableName, AlgebraValue>): AlgebraValue = Math.cos(parameter.eval(variableValues))
//
//    override val operatorString: String = "cos"
//}

class UMinus(override val parameter: AlgebraFormula) : UnaryFormula(parameter) {
    override fun eval(
            variableValues: Map<VariableName, AlgebraValue>): AlgebraValue {
        val parqameterEval = parameter.eval(variableValues) as? FieldElement
                ?: throw TypeError()
        return parqameterEval.inverse()
    }

    override val operatorString: String = "-"
}

/**
 * For representing arbitrary functions. Not for builtins such as Addition, Trig , etc.
 */
class FunctionApplication(override val parameters: List<AlgebraFormula>, val function: AlgebraFunction) :
        AlgebraFormula() {
    override fun toPrefixNotation(): String = """(${function.name} ${parameters.joinToString(separator = " ", transform = { it.toPrefixNotation() })}"""

    override fun hashCodeImpl(hashCodeContext: HashCodeContext): Int {
        return function.hashCode() + parameters.map { it -> it.hashCodeImpl(hashCodeContext) }.fold(0) { acc, i -> acc + 31 * i }
    }

    override fun equalsImpl(other: AlgebraFormula, equalsContext: EqualsContext): Boolean {
        if (other !is FunctionApplication) return false
        if (other.function.name != function.name) return false
        return super.equalsImpl(other, equalsContext)
    }

    override fun eval(
            variableValues: Map<VariableName, AlgebraValue>): AlgebraValue {
        return function.func(parameters.map { it.eval(variableValues) })
    }


    override fun toMathML2(): String = """
        <mrow>
        <mfenced separators="">
        <mi>${function.name.name}</mi>
        <mo> &ApplyFunction; </mo>
        ${parameters.joinToString(separator = " ", transform = { """<mrow>${it.toMathML2()}</mrow>""" })}
        </mfenced>
        </mrow>
    """
}

/**
 * Uses name AlgebraFunction to avoid clash with builtin function type named function
 * @param func a lambda which evaluates the function in question
 * @param name for the function in question.
 */
data class AlgebraFunction(val func: (List<AlgebraValue>) -> AlgebraValue,
                           val name:
                           FunctionName = FunctionName())

data class FunctionName(val name: String = "" + FunctionName.getAndIncrementCounter(), val uuid: UUID = UUID.randomUUID()) {
    companion object {
        var varCounter = 0
        fun getAndIncrementCounter(): Int {
            varCounter += 1
            return varCounter
        }
    }
}
