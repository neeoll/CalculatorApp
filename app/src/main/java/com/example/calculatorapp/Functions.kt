package com.example.calculatorapp

import net.objecthunter.exp4j.function.Function
import kotlin.math.*

class Functions {
    var isRadians = false

    var sine: Function = object: Function("sin", 1) {
        override fun apply(vararg args: Double): Double {
            var result = sin(args[0])
            if (!isRadians) { result = sin(Math.toRadians(args[0])) }
            return result
        }
    }

    var arcSine: Function = object: Function("asin", 1) {
        override fun apply(vararg args: Double): Double {
            var result = asin(args[0])
            if (!isRadians) { result = asin(Math.toRadians(args[0])) }
            return result
        }
    }

    var cosine: Function = object: Function("cos", 1) {
        override fun apply(vararg args: Double): Double {
            var result = cos(args[0])
            if (!isRadians) { result = cos(Math.toRadians(args[0])) }
            return result
        }
    }

    var arcCosine: Function = object: Function("acos", 1) {
        override fun apply(vararg args: Double): Double {
            var result = acos(args[0])
            if (!isRadians) { result = acos(Math.toRadians(args[0])) }
            return result
        }
    }

    var tangent: Function = object: Function("tan", 1) {
        override fun apply(vararg args: Double): Double {
            var result = tan(args[0])
            if (!isRadians) { result = tan(Math.toRadians(args[0])) }
            return result
        }
    }

    var arcTangent: Function = object: Function("atan", 1) {
        override fun apply(vararg args: Double): Double {
            var result = atan(args[0])
            if (!isRadians) { result = atan(Math.toRadians(args[0])) }
            return result
        }
    }

    var natLog: Function = object : Function("ln", 1) {
        override fun apply(vararg args: Double): Double {
            val arg = args[0]
            require(arg >= 0) { "The argument of the function can not be less than zero" }
            return ln(arg)
        }
    }
}