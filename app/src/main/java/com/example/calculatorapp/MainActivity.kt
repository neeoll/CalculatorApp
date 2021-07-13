package com.example.calculatorapp

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import kotlinx.android.synthetic.main.activity_main.*
import net.objecthunter.exp4j.ExpressionBuilder

private var View.idPrefix: String
    get() = resources.getResourceEntryName(id)
    set(value) {
        value.substringBefore("_")
    }

enum class CharType {
    NUMBER,
    OPERAND
}

class MainActivity : AppCompatActivity() {

    private var hasTextBeenEntered = false
    private var expressionString = ""
    private var lastOperationDone = ""
    private var isLandscape: Boolean = false
    private var isAltButtons: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        addButtonListeners()
    }

    private fun addButtonListeners() {

        for (element in constraintLayout.children) {
            if (element is TextView && idPrefix(element) == "m") {
                Log.e("PREFIX", element.idPrefix)
                element.setOnClickListener {
                    appendString(element.text as String, CharType.NUMBER)
                    writeExpression(element.text as String)
                    element.idPrefix
                }
            }
        }

        listOf<TextView>(btn_div, btn_mul, btn_add, btn_add)
            .forEach { button: TextView ->
            button.setOnClickListener {
                appendString(button.text as String, CharType.OPERAND)
            }
        }

        mainBtn_pt.setOnClickListener {
            calcResult.append(mainBtn_pt.text.toString())
        }

        btn_clr.setOnClickListener {
            calcExpression.text = ""
            calcResult.text = "0"
            hasTextBeenEntered = false
        }

        btn_ent.setOnClickListener {
            evaluateExpression()
        }

        if (isLandscape) {
            btn_inv.setOnClickListener {
                setAltButtons()
            }

            btn_sqrt.setOnClickListener {
                calcResult.append(" ")
                calcResult.append(getString(R.string.view_sqrd))
            }

            listOf<TextView>(btn_sin, btn_cos, btn_tan, btn_sqrt)
                .forEach { button: TextView ->
                button.setOnClickListener {
                    if (calcResult.text == "0" || !hasTextBeenEntered) {
                        calcResult.text = ""
                        calcResult.append(button.text.toString())
                        calcResult.append("(")
                        hasTextBeenEntered = true
                    } else {
                        calcResult.append(" ")
                        calcResult.append(getString(R.string.view_mul))
                        calcResult.append(" ")
                        calcResult.append(button.text.toString())
                        calcResult.append("(")
                        hasTextBeenEntered = true
                    }
                }
            }
        }
    }

    private fun idPrefix(element: TextView) =
        resources.getResourceEntryName(element.id)[0].toString()

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

    private fun writeExpression(string: String) {
        expressionString += string
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

        val result =  solveExpression()
        val longResult = result.toLong()

        if (result == longResult.toDouble()) {
            calcResult.text = longResult.toString()
        } else {
            calcResult.text = result.toString()
        }
    }

    private fun solveExpression(): Double {
        val expression = ExpressionBuilder(expressionString).build()
        return expression.evaluate()
    }

    private fun setAltButtons() {
        // TODO: Find a way to condense this function without calling each button individually
        if (!isAltButtons) {
            btn_sin.text = getString(R.string.view_invSin)
            btn_cos.text = getString(R.string.view_invCos)
            btn_tan.text = getString(R.string.view_invTan)
            btn_ln.text = getString(R.string.view_euler_n)
            btn_log.text = getString(R.string.view_ten_n)
            btn_sqrt.text = getString(R.string.view_sqrd)
            btn_expnt.text = getString(R.string.view_nthRoot)
            btn_ans.text = getString(R.string.view_rnd)
            isAltButtons = true
        } else {
            btn_sin.text = getString(R.string.view_sin)
            btn_cos.text = getString(R.string.view_cos)
            btn_tan.text = getString(R.string.view_tan)
            btn_ln.text = getString(R.string.view_ln)
            btn_log.text = getString(R.string.view_log)
            btn_sqrt.text = getString(R.string.view_sqrt)
            btn_expnt.text = getString(R.string.view_expnt)
            btn_ans.text = getString(R.string.view_ans)
            isAltButtons = false
        }
    }

    fun String.endsWithMulti(vararg chars: Char): Boolean {
        return chars.any {
            endsWith(it)
        }
    }
}
