package com.example.calculatorapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder


class MainActivity : AppCompatActivity() {

    var hasTextBeenEntered = true
    var lastOperationDone = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addButtonListeners()
    }

    private fun addButtonListeners() {
        val numberList = listOf<TextView>(
            btn_0, btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7, btn_8, btn_9
        )
        val operatorList = listOf<TextView>(
            btn_div, btn_mul, btn_sub, btn_add
        )

        numberList.forEach { button: TextView ->
             button.setOnClickListener {
                appendString(button.text as String)
             }
        }

        operatorList.forEach { button: TextView ->
            button.setOnClickListener {
                evaluateExpression(button.text as String)
            }
        }

        btn_pt.setOnClickListener {
            calcResult.append(btn_pt.text.toString())
        }

        btn_clr.setOnClickListener {
            calcExpression.text = ""
            calcResult.text = "0"
        }

        btn_del.setOnClickListener {
            val text = calcResult.text.toString()
            if (text.isNotEmpty()) {
                calcResult.text = text.dropLast(1)
            }
        }

        btn_ent.setOnClickListener {
            solveExpression()
        }
    }

    private fun appendString(string: String) {
        if (calcResult.text == "0" || !hasTextBeenEntered) {
            calcResult.text = ""
            calcResult.append(string)
        } else {
            calcResult.append(string)
        }

        hasTextBeenEntered = true
    }

    private fun evaluateExpression(string: String) {
        if (calcResult.text.isEmpty()) { return }

        calcExpression.text = calcResult.text.toString()
        calcExpression.append(" ")
        calcExpression.append(string)
        calcExpression.append(" ")
        hasTextBeenEntered = false
        lastOperationDone= ""
    }

    private fun solveExpression() {
        var text: String
        var result: Double

        if (lastOperationDone == "") {
            calcExpression.append(calcResult.text.toString())

            lastOperationDone = calcExpression.text.toString().substringAfter(' ')
            text = removeSpaces(calcExpression.text.toString())

            result =  solveExpression(text)
        } else {
            calcExpression.text = calcResult.text.toString()
            calcExpression.append(" ")
            calcExpression.append(lastOperationDone)

            lastOperationDone = calcExpression.text.toString().substringAfter(' ')
            text = removeSpaces(calcExpression.text.toString())

            result =  solveExpression(text)
        }

        val longResult = result.toLong()

        if (result == longResult.toDouble()) {
            calcResult.text = longResult.toString()
        } else {
            calcResult.text = result.toString()
        }
    }

    private fun solveExpression(text: String): Double {
        val expression = ExpressionBuilder(text).build()
        return expression.evaluate()
    }

    private fun removeSpaces(expression: String): String {
        var text = expression
        if (text.contains(" ")) {
            text = text.replace(" ", "")
        }
        return text
    }

    fun String.endsWithMulti(vararg chars: Char): Boolean
    {
        return chars.any {
            endsWith(it)
        }
    }
}
