package com.example.util

import kotlin.math.*

/**
 * Safe recursive-descent math parser supporting:
 * +, -, *, /, ^, parentheses (), variable x, constants pi & e,
 * functions sin, cos, tan, sqrt, abs, ln, log.
 */
object MathEvaluator {
    fun evaluate(expression: String, xValue: Double = 0.0): Double? {
        val clean = expression.replace(" ", "").lowercase()
        if (clean.isEmpty()) return null
        return try {
            val parser = Parser(clean, xValue)
            val result = parser.parseExpression()
            if (parser.pos < clean.length || result.isNaN()) null else result
        } catch (_: Exception) {
            null
        }
    }

    private class Parser(private val str: String, private val xValue: Double) {
        var pos = 0

        private fun peek(): Char? = if (pos < str.length) str[pos] else null

        private fun consume(expected: Char): Boolean {
            if (peek() == expected) {
                pos++
                return true
            }
            return false
        }

        fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                x = when {
                    consume('+') -> x + parseTerm()
                    consume('-') -> x - parseTerm()
                    else -> return x
                }
            }
        }

        private fun parseTerm(): Double {
            var x = parsePower()
            while (true) {
                x = when {
                    consume('*') -> x * parsePower()
                    consume('/') -> {
                        val denom = parsePower()
                        if (abs(denom) < 1e-12) Double.NaN else x / denom
                    }
                    // Implicit multiplication like 2x or 2(x+1) or x(x+1)
                    peek() == '(' || peek() == 'x' || (peek()?.isLetter() == true) -> x * parsePower()
                    else -> return x
                }
            }
        }

        private fun parsePower(): Double {
            val base = parseUnary()
            return if (consume('^')) {
                val exp = parsePower()
                base.pow(exp)
            } else {
                base
            }
        }

        private fun parseUnary(): Double {
            if (consume('+')) return parseUnary()
            if (consume('-')) return -parseUnary()
            return parsePrimary()
        }

        private fun parsePrimary(): Double {
            if (consume('(')) {
                val x = parseExpression()
                if (!consume(')')) throw IllegalArgumentException("Missing ')'")
                return x
            }
            val start = pos
            val ch = peek() ?: throw IllegalArgumentException("Unexpected end")
            if (ch.isDigit() || ch == '.') {
                while (peek()?.let { it.isDigit() || it == '.' } == true) pos++
                return str.substring(start, pos).toDouble()
            }
            if (ch.isLetter()) {
                while (peek()?.isLetter() == true) pos++
                val name = str.substring(start, pos)
                if (consume('(')) {
                    val arg = parseExpression()
                    if (!consume(')')) throw IllegalArgumentException("Missing ')'")
                    return when (name) {
                        "sin" -> sin(arg)
                        "cos" -> cos(arg)
                        "tan" -> tan(arg)
                        "sqrt" -> if (arg < 0) Double.NaN else sqrt(arg)
                        "abs" -> abs(arg)
                        "ln" -> if (arg <= 0) Double.NaN else ln(arg)
                        "log" -> if (arg <= 0) Double.NaN else log10(arg)
                        else -> throw IllegalArgumentException("Unknown func $name")
                    }
                }
                return when (name) {
                    "x" -> xValue
                    "pi" -> PI
                    "e" -> E
                    else -> throw IllegalArgumentException("Unknown symbol $name")
                }
            }
            throw IllegalArgumentException("Unexpected char $ch")
        }
    }
}
