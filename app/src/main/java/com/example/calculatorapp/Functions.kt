package com.example.calculatorapp

import net.objecthunter.exp4j.function.Function
import kotlin.math.ln

class Functions {
    var natLog: Function = object : Function("ln", 1) {
        override fun apply(vararg args: Double): Double {
            val arg = args[0]
            require(arg >= 0) { "The argument of the function can not be less than zero" }
            return ln(arg)
        }
    }
}