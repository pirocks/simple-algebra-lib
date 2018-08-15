package uk.ac.ic.doc.fpn17.algebra


sealed class AlgebraOperator {
    abstract val parameters: Array<AlgebraOperator>
    abstract fun toPrefixNotation(): String
}

/**
 * constants are functions with no parameters
 */
sealed class Constant : AlgebraOperator() {
    override val parameters: Array<AlgebraOperator> = emptyArray()
}

sealed class BinaryOperator(val left: AlgebraOperator, val right: AlgebraOperator) : AlgebraOperator() {
    abstract val operatorString: String
    override fun toPrefixNotation(): String = """(${operatorString} ${left.toPrefixNotation()} ${right.toPrefixNotation()})"""
    override val parameters: Array<AlgebraOperator> = arrayOf(left, right)
}

class Addition(left: AlgebraOperator, right: AlgebraOperator) : BinaryOperator(left, right) {
    override val operatorString: String = "+"
}

class Multiplication(left: AlgebraOperator, right: AlgebraOperator) : BinaryOperator(left, right) {
    override val operatorString: String = "*"
}

class Division(val numerator: AlgebraOperator, val denominator: AlgebraOperator) : BinaryOperator(numerator, denominator) {
    override val operatorString: String = "/"

}


class Exponentiation(val base: AlgebraOperator, val exponent: AlgebraOperator) : BinaryOperator(base, exponent) {
    override val operatorString: String = "^"

}

sealed class UnaryOperator(val parameter: AlgebraOperator) : AlgebraOperator() {
    override val parameters: Array<AlgebraOperator> = arrayOf(parameter)
    abstract val operatorString: String
    override fun toPrefixNotation(): String = """(${operatorString} ${parameter.toPrefixNotation()})"""
}

class NaturalLog(parameter: AlgebraOperator) : UnaryOperator(parameter) {
    override val operatorString: String = "log"

}

class Cos(parameter: AlgebraOperator) : UnaryOperator(parameter) {
    override val operatorString: String = "cos"

}