package com.example.calculatorapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import net.objecthunter.exp4j.ExpressionBuilder

enum class CharType {
    NUMBER,
    OPERAND
}

class MainActivity : AppCompatActivity() {

    private var hasTextBeenEntered = false
    private var lastOperationDone = ""

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
            btn_div, btn_mul, btn_sub, btn_add,
        )

        numberList.forEach { button: TextView ->
             button.setOnClickListener {
                appendString(button.text as String, CharType.NUMBER)
             }
        }

        operatorList.forEach { button: TextView ->
            button.setOnClickListener {
                appendString(button.text as String, CharType.OPERAND)
            }
        }

        btn_pt.setOnClickListener {
            calcResult.append(btn_pt.text.toString())
        }

        btn_clr.setOnClickListener {
            calcExpression.text = ""
            calcResult.text = "0"
            hasTextBeenEntered = false
        }

        btn_ent.setOnClickListener {
            evaluateExpression()
        }
    }

    private fun appendString(string: String, charType: CharType) {
        if (charType == CharType.NUMBER) {
            if (calcResult.text == "0" || !hasTextBeenEntered) {
                calcResult.text = ""
                calcResult.append(string)
            } else {
                calcResult.append(string)
            }

            hasTextBeenEntered = true
        }
        else if (charType == CharType.OPERAND) {
            if (calcResult.text.isEmpty()) { return }

            calcExpression.text = calcResult.text.toString()
            calcExpression.append(" ")
            calcExpression.append(string)
            calcExpression.append(" ")
            hasTextBeenEntered = false
            lastOperationDone= ""
        }
    }

    private fun evaluateExpression() {
        if (lastOperationDone == "") {
            calcExpression.append(calcResult.text.toString())
        } else {
            calcExpression.text = calcResult.text.toString()
            calcExpression.append(" ")
            calcExpression.append(lastOperationDone)
        }

        lastOperationDone = calcExpression.text.toString().substringAfter(' ')

        val text = removeSpaces(calcExpression.text.toString())
        val result =  solveExpression(text)
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

    fun String.endsWithMulti(vararg chars: Char): Boolean {
        return chars.any {
            endsWith(it)
        }
    }
}
