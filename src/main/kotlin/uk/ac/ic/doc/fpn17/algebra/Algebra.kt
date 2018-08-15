package uk.ac.ic.doc.fpn17.algebra

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
    open fun matches(other: AlgebraFormula): Boolean {
        if (this.javaClass != other.javaClass) {
            return false
        }
        return parameters.zip(other.parameters).all { it.first.matches(it.second) }
    }
}

class EqualsContext {
    val thisToOtherVariableName: MutableMap<VariableName, VariableName> = mutableMapOf()
}

class HashCodeContext {
    val variableToNum: MutableMap<VariableName, Int> = mutableMapOf()
}

/**
 * constants are functions with no parameters
 */
sealed class Constant : AlgebraFormula() {
    override val parameters: Array<AlgebraFormula> = emptyArray()
}

sealed class BinaryFormula(val left: AlgebraFormula, val right: AlgebraFormula) : AlgebraFormula() {
    abstract val operatorString: String
    override fun toPrefixNotation(): String = """(${operatorString} ${left.toPrefixNotation()} ${right.toPrefixNotation()})"""
    override val parameters: Array<AlgebraFormula> = arrayOf(left, right)
    override fun hashCodeImpl(hashCodeContext: HashCodeContext): Int = operatorString.hashCode() + 101 * left.hashCodeImpl(hashCodeContext) + 31 * right.hashCodeImpl(hashCodeContext)
}

/**
 * todo add a name index?
 */
data class VariableName(val name: String, val uuid: UUID = UUID.randomUUID())

class Variable(val name: VariableName) : AlgebraFormula() {
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

    override val parameters: Array<AlgebraFormula> = emptyArray()
    override fun toPrefixNotation(): String = name.name

}


class Addition(left: AlgebraFormula, right: AlgebraFormula) : BinaryFormula(left, right) {
    override val operatorString: String = "+"
}

class Multiplication(left: AlgebraFormula, right: AlgebraFormula) : BinaryFormula(left, right) {
    override val operatorString: String = "*"
}

class Division(val numerator: AlgebraFormula, val denominator: AlgebraFormula) : BinaryFormula(numerator, denominator) {
    override val operatorString: String = "/"

}


class Exponentiation(val base: AlgebraFormula, val exponent: AlgebraFormula) : BinaryFormula(base, exponent) {
    override val operatorString: String = "^"

}

sealed class UnaryFormula(val parameter: AlgebraFormula) : AlgebraFormula() {
    override val parameters: Array<AlgebraFormula> = arrayOf(parameter)
    abstract val operatorString: String
    override fun toPrefixNotation(): String = """(${operatorString} ${parameter.toPrefixNotation()})"""
    override fun hashCodeImpl(hashCodeContext: HashCodeContext): Int = operatorString.hashCode() + 31 * parameter.hashCodeImpl(hashCodeContext)
}

class NaturalLog(parameter: AlgebraFormula) : UnaryFormula(parameter) {
    override val operatorString: String = "log"
}

class Cos(parameter: AlgebraFormula) : UnaryFormula(parameter) {
    override val operatorString: String = "cos"

}