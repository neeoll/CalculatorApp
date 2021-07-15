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
        var invBtnList: MutableList<TextView> = mutableListOf()

        for (element in constraintLayout.children) {
            if (element is TextView && element.idPrefix('_') == "mainBtn") {
                element.setOnClickListener {
                    appendString(element.text.toString(), CharType.NUMBER)
                    writeExpression(element.text.toString())
                }
            }
            if (element is TextView && element.idPrefix('_') == "operatorBtn") {
                element.setOnClickListener {
                    appendString(element.text.toString(), CharType.OPERAND)
                    writeExpression(element.text.toString())
                }
            }
            if (element is TextView && element.idPrefix('_') == "canInvBtn") {
                invBtnList.add(element)
                element.setOnClickListener {
                    if (calcResult.text == "0" || !hasTextBeenEntered) {
                        calcResult.text = element.text.toString()
                        writeExpression(element.text.toString())
                        hasTextBeenEntered = true
                    } else {
                        calcResult.appendMulti(" ", "*", " ", element.text.toString())
                        writeExpression("*", element.text.toString())
                        hasTextBeenEntered = true
                    }
                }
            }
        }

        btn_clr.setOnClickListener {
            calcExpression.text = ""
            calcResult.text = "0"
            expressionString = ""
            hasTextBeenEntered = false
        }

        btn_ent.setOnClickListener {
            evaluateExpression()
        }

        if (isLandscape) {
            val nonInvArray: Array<String> = resources.getStringArray(R.array.nonInvArray)
            val invArray: Array<String> = resources.getStringArray(R.array.invArray)

            btn_inv.setOnClickListener {
                isAltButtons = if (!isAltButtons) {
                    setAltButtons(invBtnList, invArray)
                    true
                } else {
                    setAltButtons(invBtnList, nonInvArray)
                    false
                }
            }

            btn_pi.setOnClickListener {
                if (calcResult.text == "0" || !hasTextBeenEntered) {
                    calcResult.text = btn_pi.text.toString()
                    writeExpression(btn_pi.text.toString())
                    hasTextBeenEntered = true
                } else {
                    calcResult.appendMulti(" ", "*", " ", btn_pi.text.toString())
                    writeExpression("*", btn_pi.text.toString())
                    hasTextBeenEntered = true
                }
            }
        }
    }

    private fun appendString(string: String, charType: CharType) {
        if (charType == CharType.NUMBER) {
            if (calcResult.text == "0" || !hasTextBeenEntered) {
                calcResult.text = string
            } else {
                calcResult.append(string)
            }

            hasTextBeenEntered = true
        }
        else if (charType == CharType.OPERAND) {
            if (calcResult.text.isEmpty()) { return }

            calcExpression.text = calcResult.text.toString()
            calcExpression.appendMulti(" ", string, " ")
            hasTextBeenEntered = false
            lastOperationDone= ""
        }
    }

    private fun writeExpression(vararg strings: String) {
        for (string in strings) {
            expressionString += string
        }

        Log.e("EXPRESSION", expressionString)
    }

    private fun evaluateExpression() {
        if (lastOperationDone == "") {
            calcExpression.append(calcResult.text.toString())
        } else {
            calcExpression.text = calcResult.text.toString()
            calcExpression.appendMulti(" ", lastOperationDone)
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

    private fun setAltButtons(list: MutableList<TextView>, array: Array<String>) {
        for (i in list.indices) {
            list[i].text = array[i]
        }
    }

    // Extension functions/properties
    private fun View.idPrefix(delimiter: Char): String {
        val viewId = resources.getResourceEntryName(id)
        return viewId.substringBefore(delimiter)
    }

    private fun TextView.appendMulti(vararg strings: String) {
        for (string in strings) {
            this.append(string)
        }
    }
}
