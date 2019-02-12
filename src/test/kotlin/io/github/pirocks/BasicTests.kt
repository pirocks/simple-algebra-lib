package io.github.pirocks

import io.github.pirocks.algebra.*
import io.github.pirocks.algebra.numbers.FloatFieldVal
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

val a = Variable<FloatFieldVal>()
val b = Variable<FloatFieldVal>()
val c = Variable<FloatFieldVal>()
val d = Variable<FloatFieldVal>()
val expr1 = Addition(Multiplication(a, a), b)
val expr1a = Addition(Multiplication(d, d), c)
val expr2 = Addition(Multiplication(a, b), c)
val expr3 = Addition(Multiplication(expr1, expr1), c)
val func1 = AlgebraFunction<FloatFieldVal, FloatFieldVal>({ throw IllegalStateException() }, FunctionName())
val func2 = AlgebraFunction<FloatFieldVal, FloatFieldVal>({ throw IllegalStateException() }, FunctionName())
val funcApplication1 = FunctionApplication(emptyList(), func1)
val funcApplication2 = FunctionApplication(emptyList(), func1)
val funcApplication3 = FunctionApplication(emptyList(), func2)
val funcApplication4 = FunctionApplication(listOf(expr1), func2)

class BasicTestsEquals {
    @Test
    fun testSameStructureDifferentVar() {
        assertEquals(expr1, expr1a)
        assertEquals(expr1a, expr1)
        assertNotEquals(expr2, expr1)
        assertNotEquals(expr3, expr1)
        assertNotEquals(expr1, expr2)
        assertNotEquals(expr1, expr3)
    }
}

class BasicTestsHashcode {
    @Test
    fun testSameStructureDifferentVar() {
        assertEquals(expr1.hashCode(), expr1a.hashCode())
        assertEquals(expr1a.hashCode(), expr1.hashCode())
    }
}

class TestFunctions {
    @Test
    fun testEquals() {
        assertEquals(funcApplication1, funcApplication2)
        assertEquals(funcApplication1.hashCode(), funcApplication2.hashCode())
        assertNotEquals(funcApplication1, funcApplication3)
        assertNotEquals(funcApplication4, funcApplication3)
    }
}