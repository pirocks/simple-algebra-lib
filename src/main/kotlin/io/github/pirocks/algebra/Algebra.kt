package io.github.pirocks.algebra

import io.github.pirocks.algebra.equivalences.MatchSubstitutions
import java.io.Serializable
import java.util.*


sealed class AlgebraFormula : Serializable {
    abstract val parameters: Array<AlgebraFormula>
    abstract fun toPrefixNotation(): String
    override fun equals(other: Any?): Boolean {
        if (other !is AlgebraFormula) return false
        return equalsImpl(other, EqualsContext())
    }

    open fun equalsImpl(other: AlgebraFormula, equalsContext: EqualsContext): Boolean {
        if (this.javaClass != other.javaClass) return false
        return parameters.zip(other.parameters).all { it.first.equalsImpl(it.second, EqualsContext()) }
    }

    override fun hashCode(): Int = hashCodeImpl(HashCodeContext())
    abstract fun hashCodeImpl(hashCodeContext: HashCodeContext): Int
    open fun matches(other: AlgebraFormula, matchContext: MatchSubstitutions): Boolean {
        if (this.javaClass != other.javaClass) {
            return false
        }
        return parameters.zip(other.parameters).all { it.first.matches(it.second, matchContext) }
    }

    abstract fun eval(): Double
}

class EqualsContext(
    val thisToOtherVariableName: MutableMap<VariableName, VariableName> = mutableMapOf()
)

class HashCodeContext {
    val variableToNum: MutableMap<VariableName, Int> = mutableMapOf()
}

/**
 * constants are functions with no parameters
 */
sealed class Constant : AlgebraFormula() {
    override val parameters: Array<AlgebraFormula> = emptyArray()
}

open class ArbitraryConstant(val `val`: Double) : Constant() {
    override fun eval(): Double = `val`

    override fun toPrefixNotation(): String = `val`.toString()

    override fun hashCodeImpl(hashCodeContext: HashCodeContext): Int {
        return `val`.hashCode()
    }
}

class Zero : ArbitraryConstant(0.0)
class One : ArbitraryConstant(1.0)

sealed class BinaryFormula(val left: AlgebraFormula, val right: AlgebraFormula) : AlgebraFormula() {
    abstract val operatorString: String
    override fun toPrefixNotation(): String = """(${operatorString} ${left.toPrefixNotation()} ${right.toPrefixNotation()})"""
    override val parameters: Array<AlgebraFormula> = arrayOf(left, right)
    override fun hashCodeImpl(hashCodeContext: HashCodeContext): Int = operatorString.hashCode() + 101 * left.hashCodeImpl(hashCodeContext) + 31 * right.hashCodeImpl(hashCodeContext)
}

/**
 * todo add a name index?
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

open class Variable(open val name: VariableName = VariableName()) : AlgebraFormula() {
    override fun eval(): Double {
        TODO()
    }

    override fun equalsImpl(other: AlgebraFormula, equalsContext: EqualsContext): Boolean {
        if (other !is Variable) return false
        if (name in equalsContext.thisToOtherVariableName.keys) {
            return equalsContext.thisToOtherVariableName[name] == other.name
        } else {
            equalsContext.thisToOtherVariableName[name] = other.name
            return true;
        }
    }

    override fun hashCodeImpl(hashCodeContext: HashCodeContext): Int {
        return hashCodeContext.variableToNum.computeIfAbsent(this.name) { hashCodeContext.variableToNum.size }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Variable) return false
        if (other.name != name) return false
        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
    override val parameters: Array<AlgebraFormula> = emptyArray()
    override fun toPrefixNotation(): String = name.name

}

abstract class PatternMember() : Variable() {
    override abstract fun matches(other: AlgebraFormula, matchContext: MatchSubstitutions): Boolean
}

class AllowAllVars() : PatternMember() {
    override fun matches(other: AlgebraFormula, matchContext: MatchSubstitutions): Boolean {
        if (this in matchContext.matchedPatterns) {
            return matchContext.matchedPatterns[this]!!.equalsImpl(other, EqualsContext(matchContext.variableSubstitutions))
        } else {
            matchContext.matchedPatterns[this] = other
            return true
        }
    }

}


class Addition(left: AlgebraFormula, right: AlgebraFormula) : BinaryFormula(left, right) {
    override fun eval(): Double = left.eval() + right.eval()

    override val operatorString: String = "+"
}

class Multiplication(left: AlgebraFormula, right: AlgebraFormula) : BinaryFormula(left, right) {
    override fun eval(): Double = left.eval() * right.eval()

    override val operatorString: String = "*"
}

class Division(val numerator: AlgebraFormula, val denominator: AlgebraFormula) : BinaryFormula(numerator, denominator) {
    override fun eval(): Double = numerator.eval() / denominator.eval()

    override val operatorString: String = "/"

}


class Exponentiation(val base: AlgebraFormula, val exponent: AlgebraFormula) : BinaryFormula(base, exponent) {
    override fun eval(): Double = Math.exp(Math.log(base.eval()) * exponent.eval())

    override val operatorString: String = "^"

}

sealed class UnaryFormula(val parameter: AlgebraFormula) : AlgebraFormula() {
    override val parameters: Array<AlgebraFormula> = arrayOf(parameter)
    abstract val operatorString: String
    override fun toPrefixNotation(): String = """(${operatorString} ${parameter.toPrefixNotation()})"""
    override fun hashCodeImpl(hashCodeContext: HashCodeContext): Int = operatorString.hashCode() + 31 * parameter.hashCodeImpl(hashCodeContext)
}

class NaturalLog(parameter: AlgebraFormula) : UnaryFormula(parameter) {
    override fun eval(): Double = Math.log(parameter.eval())

    override val operatorString: String = "log"
}

class Cos(parameter: AlgebraFormula) : UnaryFormula(parameter) {
    override fun eval(): Double = Math.cos(parameter.eval())

    override val operatorString: String = "cos"
}

class UMinus(parameter: AlgebraFormula) : UnaryFormula(parameter) {
    override fun eval(): Double = -parameter.eval()

    override val operatorString: String = "-"
}