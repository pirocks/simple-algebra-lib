package io.github.pirocks.rewriting

import io.github.pirocks.algebra.*
import io.github.pirocks.algebra.equivalences.*
import io.github.pirocks.algebra.numbers.FloatFieldVal
import org.junit.Assert.assertEquals
import org.junit.Test


open class IdentityTest(val expr: AlgebraFormula, val expected: AlgebraFormula, val identity: Equivalence, val matchIndex: Int = 0) {
    fun doTest() {
        assertEquals(identity.apply(expr, matchIndex), expected)
    }
}

val a = Variable(VariableName())
val b = Variable(VariableName())
val c = `+`(a, b)

class AMinusATest {
    val identityTest = IdentityTest(`+`(c, `-`(c)), `0`, AMinusA(), 0)
    @Test
    fun doTest() {
        identityTest.doTest()
    }
}

class ZeroAdditionTest {
    val identityTest = IdentityTest(`+`(`0`, a), a, ZeroAddition(), 0)
    @Test
    fun doTest() {
        identityTest.doTest()
    }
}

class AdditionAssociativityTest {
    val identityTest = IdentityTest(`+`(a, `+`(a, a)), `+`(`+`(a, a), a), AdditionAssociativity(), 0)
    @Test
    fun doTest() {
        identityTest.doTest()
    }
}

class AdditionCommutativityTest {
    val identityTest = IdentityTest(`+`(a, c), `+`(a, `+`(b, a)), AdditionCommutativity(), 1)
    @Test
    fun doTest() {
        identityTest.doTest()
    }
}

class MultiplicationCommutativityTest {
    val identityTest = IdentityTest(`*`(a, b), `*`(b, a), MultiplicationCommutativity(), 0)
    @Test
    fun doTest() {
        identityTest.doTest()
    }
}

class OneMultiplicationTest {
    val identityTest = IdentityTest(`*`(`1`, c), c, OneMultiplication(), 0)
    @Test
    fun doTest() {
        identityTest.doTest()
    }
}

class ZeroMultiplicationTest {
    val identityTest = IdentityTest(`*`(`0`, a), `0`, ZeroMultiplication(), 0)
    @Test
    fun doTest() {
        identityTest.doTest()
    }
}

class MultiplicationAssociativityTest {
    val identityTest = IdentityTest(`*`(a, `*`(b, c)), `*`(`*`(a, b), c), MultiplicationAssociativity(), 0)
    @Test
    fun doTest() {
        identityTest.doTest()
    }
}
