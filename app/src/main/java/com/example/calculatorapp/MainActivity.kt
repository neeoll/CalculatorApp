package com.example.calculatorapp

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import kotlinx.android.synthetic.main.activity_main.*
import net.objecthunter.exp4j.ExpressionBuilder


enum class ButtonType {
    NUMBER,
    OPERAND,
    CAN_INV
}

class MainActivity : AppCompatActivity() {

    private var hasTextBeenEntered = false
    private var isAltButtons = false
    private var expressionString = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        addButtonListeners(isLandscape)
    }

    private fun addButtonListeners(isLandscape: Boolean) {
        val invBtnList: MutableList<TextView> = mutableListOf()

        constraintLayout.children.forEach { it ->
            when (it.idPrefix('_')) {
                "mainBtn" -> it.setOnClickListener { it as TextView
                    appendViewString(it.text.toString(), ButtonType.NUMBER)
                }
                "operatorBtn" -> it.setOnClickListener { it as TextView
                    appendViewString(it.text.toString(), ButtonType.OPERAND)
                }
                "canInvBtn" -> {
                    invBtnList.add(it as TextView)
                    it.setOnClickListener { it as TextView
                        appendViewString(it.text.toString(), ButtonType.CAN_INV)
                    }
                }
            }
        }

        btn_clr.setOnClickListener {
            calcExpression.text = "0"
            expressionString = ""
            hasTextBeenEntered = false
        }

        btn_ent.setOnClickListener {
            evaluateExpression()
        }

        btn_raddeg.setOnClickListener {
            // TODO: Not yet implemented
        }

        btn_percent.setOnClickListener {
            // TODO: Not yet implemented
        }

        if (isLandscape) {
            val nonInvArray: Array<String> = resources.getStringArray(R.array.nonInvArray)
            val invArray: Array<String> = resources.getStringArray(R.array.invArray)

            btn_inv.setOnClickListener {
                isAltButtons = if (!isAltButtons) {
                    setAltButtons(invBtnList, invArray)
                    mainBtn_ans.text = getString(R.string.view_rnd)
                    operatorBtn_expnt.text = getString(R.string.view_nthRoot)
                    true
                } else {
                    setAltButtons(invBtnList, nonInvArray)
                    mainBtn_ans.text = getString(R.string.view_ans)
                    operatorBtn_expnt.text = getString(R.string.view_exponent)
                    false
                }
            }
        }
    }

    private fun appendViewString(string: String, buttonType: ButtonType) {
        if (buttonType == ButtonType.NUMBER) {
            if (calcExpression.text == "0" || !hasTextBeenEntered) {
                calcExpression.text = string
            } else {
                calcExpression.append(string)
            }
        }
        else if (buttonType == ButtonType.OPERAND) {
            if (calcExpression.text.isEmpty()) { return }

            calcExpression.append(string)
        }
        else if (buttonType == ButtonType.CAN_INV) {
            if (calcExpression.text == "0" || !hasTextBeenEntered) {
                calcExpression.text = string
                calcExpression.append("(")
            } else {
                calcExpression.append(string)
                calcExpression.append("(")
            }
        }
        hasTextBeenEntered = true
        writeExpression(string)
    }

    // TODO: See if it's possible to condense this function any further
    private fun writeExpression(vararg strings: String) {
        for (string in strings) {
            expressionString += when (string) {
                getString(R.string.view_mul) -> { "*" }
                getString(R.string.view_div) -> { "/" }
                getString(R.string.view_exponent) -> { "^" }
                getString(R.string.view_nthRoot) -> { "$" }
                getString(R.string.view_sqrt) -> { "sqrt(" }
                getString(R.string.view_squared) -> { "^2" }
                getString(R.string.view_sin) -> { "sin(" }
                getString(R.string.view_invSin) -> { "asin(" }
                getString(R.string.view_cos) -> { "cos(" }
                getString(R.string.view_invCos) -> { "acos(" }
                getString(R.string.view_tan) -> { "tan(" }
                getString(R.string.view_invTan) -> { "atan(" }
                getString(R.string.view_ln) -> { "ln(" }
                getString(R.string.view_exp) -> { ":" }
                getString(R.string.view_fact) -> { "!" }
                getString(R.string.view_ans) -> { calcResult.text.toString().substringAfter('=') }
                getString(R.string.view_rnd) -> { Math.random().toString() }
                else -> { string }
            }
        }
        Log.e("EXPRESSION", expressionString)
    }

    @SuppressLint("SetTextI18n")
    private fun evaluateExpression() {
        val result =  solveExpression()
        val longResult = result.toLong()

        if (result == longResult.toDouble()) {
            calcResult.text = "Ans = $longResult"
        } else {
            calcResult.text = "Ans = $result"
        }

        hasTextBeenEntered = false
    }

    private fun solveExpression(): Double {
        return ExpressionBuilder(expressionString)
            .operator(
                Operators().factorial,
                Operators().exp,
                Operators().nthRoot
            )
            .functions(
                Functions().natLog
            )
            .build()
            .evaluate()
    }

    private fun setAltButtons(list: MutableList<TextView>, array: Array<String>) {
        for (i in list.indices) {
            list[i].text = array[i]
        }
    }

    // Extension functions/properties
    private fun View.idPrefix(delimiter: Char): String {
        return resources.getResourceEntryName(id).substringBefore(delimiter)
    }

    private fun TextView.appendMulti(vararg strings: String) {
        for (string in strings) {
            this.append(string)
        }
    }
}
