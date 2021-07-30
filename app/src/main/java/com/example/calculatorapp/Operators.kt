package com.example.calculatorapp

import net.objecthunter.exp4j.operator.Operator
import kotlin.math.pow

/* Possible chars to be used as operators:
*  #,§,&,;,~,<,>,|
* */


class Operators {

    var factorial: Operator = object : Operator("!", 1, true, PRECEDENCE_POWER + 1) {
        override fun apply(vararg args: Double): Double {
            val arg = args[0].toInt()
            require(arg.toDouble() == args[0]) { "Operand for factorial has to be an integer" }
            require(arg >= 0) { "The operand of the factorial can not be less than zero" }
            var result = 1.0
            for (i in 1..arg) {
                result *= i.toDouble()
            }
            return result
        }
    }

    var percent: Operator = object: Operator("%", 1, true, PRECEDENCE_DIVISION + 2) {
        override fun apply(vararg args: Double): Double {
            return args[0] * 0.01
        }
    }

    var exp: Operator = object: Operator(":", 2, false, PRECEDENCE_POWER + 2) {
        override fun apply(vararg args: Double): Double {
            val arg2 = args[1].toInt()
            require(arg2.toDouble() == args[1]) { "Exponent of 10 must be an integer" }
            var result = 10.0
            result.pow(arg2).also { result = args[0] * it }
            return result
        }
    }

    var nthRoot: Operator = object: Operator("$", 2, false, PRECEDENCE_DIVISION + 1) {
        override fun apply(vararg args: Double): Double {
            val arg1 = args[0]
            val arg2 = args[1]

            return arg2.pow(1.0 / arg1)
        }
    }
}