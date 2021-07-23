package com.example.calculatorapp

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.style.SuperscriptSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import kotlinx.android.synthetic.main.activity_main.*
import net.objecthunter.exp4j.ExpressionBuilder


enum class Type {
    NUMBER,
    OPERATOR,
    FUNCTION
}

class MainActivity : AppCompatActivity() {

    private var isAltButtons = false
    private var expressionString = ""
    private var isSuperscript = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        setButtonListeners(isLandscape)
    }

    private fun setButtonListeners(isLandscape: Boolean) {
        val invBtnList: MutableList<TextView> = mutableListOf()

        constraintLayout.children.forEach { it ->
            when (it.getIdPrefix('_')) {
                "mainBtn" -> it.setOnClickListener { it as TextView
                    appendToView(it.text.toString(), Type.NUMBER)
                }
                "operatorBtn" -> it.setOnClickListener { it as TextView
                    appendToView(it.text.toString(), Type.OPERATOR)
                }
                "invBtn" -> invBtnList.add(it as TextView)
            }
        }

        btn_ent.setOnClickListener { evaluateExpression() }
        btn_clr.setOnClickListener {
            calcExpression.text = "0"
            expressionString = ""
            isSuperscript = false
        }

        if (isLandscape) {
            val nonInvArray: Array<String> = resources.getStringArray(R.array.nonInvArray)
            val invArray: Array<String> = resources.getStringArray(R.array.invArray)

            btn_fact.setOnClickListener { appendToView("!", Type.OPERATOR) }
            invBtn_sin.setOnClickListener { appendToView(invBtn_sin.text.toString(), Type.FUNCTION) }
            invBtn_cos.setOnClickListener { appendToView(invBtn_cos.text.toString(), Type.FUNCTION) }
            invBtn_tan.setOnClickListener { appendToView(invBtn_tan.text.toString(), Type.FUNCTION) }
            invBtn_ans.setOnClickListener { appendToView(invBtn_ans.text.toString(), Type.NUMBER) }
            invBtn_ln.setOnClickListener { appendToView(invBtn_ln.text.toString(), Type.FUNCTION) }
            invBtn_log.setOnClickListener { appendToView(invBtn_log.text.toString(), Type.FUNCTION) }
            invBtn_sqrt.setOnClickListener { appendToView(getString(R.string.view_sqrt), Type.OPERATOR) }
            invBtn_expnt.setOnClickListener {
                isSuperscript = true
                writeExpression("^")
            }

            btn_inv.setOnClickListener {
                isAltButtons = if (!isAltButtons) {
                    setAltButtons(invBtnList, invArray)
                    invListeners()
                    true
                } else {
                    setAltButtons(invBtnList, nonInvArray)
                    invListeners()
                    false
                }
            }
        }
    }

    private fun appendToView(string: String, type: Type) {
        when (type) {
            Type.NUMBER -> {
                if (calcExpression.text == "0") {
                    calcExpression.text = string
                } else {
                    calcExpression.customAppend(string, isSuperscript = isSuperscript)
                }
            }
            Type.OPERATOR -> {
                if (calcExpression.text.isEmpty()) { return }
                isSuperscript = false
                calcExpression.customAppend(string, isSuperscript = isSuperscript)
            }
            Type.FUNCTION -> {
                isSuperscript = false
                calcExpression.customAppend(string, "(", isSuperscript = isSuperscript)
            }
        }
        writeExpression(string)
    }

    // TODO: See if it's possible to condense this function any further
    private fun writeExpression(vararg strings: String) {
        for (string in strings) {
            expressionString += when (string) {
                getString(R.string.view_mul) -> { "*" }
                getString(R.string.view_div) -> { "/" }
                getString(R.string.view_nthRoot) -> { "$" }
                getString(R.string.view_sqrt) -> { "sqrt(" }
                getString(R.string.squared) -> { "^2" }
                getString(R.string.view_sin) -> { "sin(" }
                getString(R.string.view_invSin) -> { "asin(" }
                getString(R.string.view_cos) -> { "cos(" }
                getString(R.string.view_invCos) -> { "acos(" }
                getString(R.string.view_tan) -> { "tan(" }
                getString(R.string.view_invTan) -> { "atan(" }
                getString(R.string.view_ln) -> { "ln(" }
                getString(R.string.view_exp) -> { ":" }
                getString(R.string.view_fact) -> { "!" }
                getString(R.string.view_ans) -> { calcResult.text.toString().substringAfter("= ") }
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
            calcExpression.text = "$longResult"
            expressionString = "$longResult"
        } else {
            calcResult.text = "Ans = $result"
            calcExpression.text = "$result"
            expressionString = "$result"
        }
    }

    private fun solveExpression(): Double {
        return ExpressionBuilder(expressionString)
            .operator(
                Operators().factorial,
                Operators().percent,
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

    private fun invListeners() {
        if (isAltButtons) {
            invBtn_ln.setOnClickListener { appendToView(invBtn_ln.text.toString(), Type.FUNCTION) }
            invBtn_log.setOnClickListener { appendToView(invBtn_log.text.toString(), Type.FUNCTION) }
            invBtn_sqrt.setOnClickListener { appendToView(invBtn_sqrt.text.toString(), Type.OPERATOR) }
            invBtn_expnt.setOnClickListener {
                isSuperscript = true
                writeExpression("^") }
        } else {
            invBtn_ln.setOnClickListener {
                appendToView("e", Type.NUMBER)
                isSuperscript = true
                writeExpression("^")
            }
            invBtn_log.setOnClickListener {
                appendToView("10", Type.NUMBER)
                isSuperscript = true
                writeExpression("^")
            }
            invBtn_sqrt.setOnClickListener { appendToView(getString(R.string.squared), Type.OPERATOR) }
            invBtn_expnt.setOnClickListener {
                val toBeConverted = calcExpression.text.toString().substringAfter('+')

                calcExpression.text.toString().dropLast(toBeConverted.length).also { calcExpression.text = it }
                for (element in toBeConverted) {
                    calcExpression.customAppend(element.toString(), isSuperscript = true)
                }
                calcExpression.append(getString(R.string.view_sqrt))
                writeExpression("$")
            }
        }
    }

    private fun insertAt(target: String, position: Int, insert: String): String {
        val targetLen = target.length
        require(!(position < 0 || position > targetLen)) { "position=$position" }
        if (insert.isEmpty()) { return target }
        if (position == 0) {
            return insert + target
        } else if (position == targetLen) {
            return target + insert
        }
        val insertLen = insert.length
        val buffer = CharArray(targetLen + insertLen)
        target.toCharArray(buffer, 0, 0, position)
        insert.toCharArray(buffer, position, 0, insertLen)
        target.toCharArray(buffer, position + insertLen, position, targetLen)
        return String(buffer)
    }

    // Extension functions/properties
    private fun View.getIdPrefix(delimiter: Char): String {
        return resources.getResourceEntryName(id).substringBefore(delimiter)
    }

    private fun TextView.customAppend(vararg strings: String, isSuperscript: Boolean = false) {
        for (string in strings) {
            if (isSuperscript) {
                val spannedString = SpannableStringBuilder(string)
                spannedString.setSpan(SuperscriptSpan(), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannedString.setSpan(RelativeSizeSpan(0.5f), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                this.append(spannedString)
            } else {
                this.append(string)
            }
        }
    }
}
