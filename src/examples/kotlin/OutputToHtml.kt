import io.github.pirocks.algebra.*

fun main(args: Array<String>) {
    val expr = NaturalLog(Division(Variable(VariableName("x")), Variable(VariableName("y"))))
    println("""Prints out ${expr.toPrefixNotation()} as ${expr.toHtml()}""")
}