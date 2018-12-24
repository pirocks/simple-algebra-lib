package io.github.pirocks

import io.github.pirocks.algebra.Variable
import io.github.pirocks.algebra.`*`
import io.github.pirocks.algebra.`+`
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

val a = Variable()
val b = Variable()
val c = Variable()
val d = Variable()
val expr1 = `+`(`*`(a, a), b)
val expr1a = `+`(`*`(d, d), c)
val expr2 = `+`(`*`(a, b), c)
val expr3 = `+`(`*`(expr1, expr1), c)

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