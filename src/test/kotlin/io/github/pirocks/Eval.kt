package io.github.pirocks

import io.github.pirocks.algebra.*
import io.github.pirocks.algebra.values.AlgebraValue
import io.github.pirocks.algebra.values.numbers.DoubleFieldVal
import io.github.pirocks.algebra.values.numbers.DoubleReal
import io.github.pirocks.algebra.values.numbers.FloatFieldVal
import io.github.pirocks.algebra.values.numbers.FloatReal
import org.junit.Assert
import kotlin.test.Test

const val epsilon: Double = 1e-7

const val three = 3.0

const val two = 2.0

class TestAdditionMultiplication {
    val constantFloatReal = Constant(FloatReal(three.toFloat()))
    val constantDoubleReal = Constant(DoubleReal(three))
    val constantFloatField = Constant(FloatFieldVal(three.toFloat()))
    val constantDoubleField = Constant(DoubleFieldVal(three))
    val constants = listOf(constantFloatReal, constantDoubleReal, constantFloatField, constantDoubleField)
    val floatReal = FloatReal(two.toFloat())
    val doubleReal = DoubleReal(two)
    val floatField = FloatFieldVal(two.toFloat())
    val doubleField = DoubleFieldVal(two)
    val values = listOf(floatReal, doubleReal, floatField, doubleField)


    @Test
    fun doTest() {
        constants.zip(values).forEach {
            val variable = Variable()
            val add = Addition(it.first, variable)
            val resAdd = add.eval(mapOf(Pair(variable.name, it.second)))
            checkResult(resAdd, three + two)
            val mul = Multiplication(it.first, variable)
            val resMul = mul.eval(mapOf(Pair(variable.name, it.second)))
            checkResult(resMul, three * two)
        }
    }

}

private fun checkResult(res: AlgebraValue, expected: Double) {
    val num = when (res) {
        is FloatFieldVal -> res.`val`.toDouble()
        is DoubleFieldVal -> res.`val`
        is DoubleReal -> res.`val`
        is FloatReal -> res.`val`.toDouble()
        else -> {
            Assert.fail()
            throw IllegalStateException()
        }
    }
    Assert.assertEquals(num, expected, epsilon)
}

class TestSpecialFunctions {
    @Test
    fun doTest() {
        val ten = 10.0
        val floatConstant = FloatReal(ten.toFloat())
        val doubleConstant = DoubleReal(ten)
        listOf(floatConstant, doubleConstant).forEach {
            it.run {
                val constantLogExpression = NaturalLog(Constant(it))
                val constantRes = constantLogExpression.eval(emptyMap())
                checkResult(constantRes, Math.log(ten))
                val variable = Variable()
                val varLogExpression = NaturalLog(variable)
                val variableRes = varLogExpression.eval(mapOf(Pair(variable.name, it)))
                checkResult(variableRes, Math.log(ten))
            }
            it.run {
                val constantCosExpression = Cos(Constant(it))
                val constantRes = constantCosExpression.eval(emptyMap())
                checkResult(constantRes, Math.cos(ten))
                val variable = Variable()
                val varCosExpression = Cos(variable)
                val variableRes = varCosExpression.eval(mapOf(Pair(variable.name, it)))
                checkResult(variableRes, Math.cos(ten))
            }
        }
    }
}
