package com.example.calculatorapp

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.SuperscriptSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import kotlinx.android.synthetic.main.activity_main.*
import net.objecthunter.exp4j.ExpressionBuilder


enum class Type {
    NUMBER,
    OPERATOR,
    FUNCTION
}


class MainActivity : AppCompatActivity() {

    private var expressionString = ""
    private var isSuperscript = false
    private var hasTextBeenEntered = false
    private val functions = Functions()
    private val operators = Operators()

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

        val fadeExpression = ObjectAnimator.ofFloat(calcExpression, "alpha", 0.0f, 1.0f)
        val scaleExpression = ObjectAnimator.ofFloat(calcExpression, "scaleY", 0.0f, 1.0f)
        val fadeResult = ObjectAnimator.ofFloat(calcResult, "alpha", 0.0f, 1.0f)
        val scaleResult = ObjectAnimator.ofFloat(calcResult, "scaleY", 0.0f, 1.0f)

        btn_ent.setOnClickListener {
            AnimatorSet().apply {
                duration = 250
                playTogether(fadeExpression, scaleExpression, fadeResult, scaleResult)
                evaluateExpression()
                start()
            }
        }

        btn_clr.setOnClickListener {
            calcExpression.text = "0"
            expressionString = ""
            isSuperscript = false
            hasTextBeenEntered = false
        }

        if (isLandscape) {
            var isAltButtons = false
            val nonInvArray: Array<String> = resources.getStringArray(R.array.nonInvArray)
            val invArray: Array<String> = resources.getStringArray(R.array.invArray)
            val raddegSpan = SpannableStringBuilder(btn_raddeg.text.toString())

            setAngleUnit(raddegSpan)

            btn_raddeg.setOnClickListener { setAngleUnit(raddegSpan) }
            btn_fact.setOnClickListener { appendToView("!", Type.OPERATOR) }
            invBtn_sin.setOnClickListener { appendToView(invBtn_sin.text.toString(), Type.FUNCTION) }
            invBtn_cos.setOnClickListener { appendToView(invBtn_cos.text.toString(), Type.FUNCTION) }
            invBtn_tan.setOnClickListener { appendToView(invBtn_tan.text.toString(), Type.FUNCTION) }
            invBtn_ans.setOnClickListener { appendToView(invBtn_ans.text.toString(), Type.NUMBER) }
            invBtn_ln.setOnClickListener { appendToView(invBtn_ln.text.toString(), Type.FUNCTION) }
            invBtn_log.setOnClickListener { appendToView(invBtn_log.text.toString(), Type.FUNCTION) }
            invBtn_sqrt.setOnClickListener { appendToView(getString(R.string.view_sqrt), Type.NUMBER) }
            invBtn_exponent.setOnClickListener {
                isSuperscript = true
                writeExpression("^")
            }

            btn_inv.setOnClickListener {
                isAltButtons = if (!isAltButtons) {
                    setAltButtons(invBtnList, invArray)
                    invListeners(isAltButtons)
                    true
                } else {
                    setAltButtons(invBtnList, nonInvArray)
                    invListeners(isAltButtons)
                    false
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun appendToView(string: String, type: Type) {
        when (type) {
            Type.NUMBER -> {
                if (!hasTextBeenEntered) {
                    calcExpression.text = string
                } else {
                    calcExpression.customAppend(string, isSuperscript = isSuperscript)
                }
            }
            Type.OPERATOR -> {
                if (calcExpression.text.isEmpty()) { return }
                if (expressionString.isEmpty()) { writeExpression("0") }
                isSuperscript = false
                calcExpression.customAppend(string, isSuperscript = isSuperscript)
            }
            Type.FUNCTION -> {
                isSuperscript = false
                if (!hasTextBeenEntered) {
                    calcExpression.text = "$string("
                } else {
                    calcExpression.customAppend(string, "(", isSuperscript = isSuperscript)
                }
            }
        }
        hasTextBeenEntered = true
        writeExpression(string)
    }

    // TODO: See if it's possible to condense this function any further
    private fun writeExpression(vararg strings: String) {
        strings.forEach { string ->
            expressionString += when (string) {
                getString(R.string.view_mul) -> { "*" }
                getString(R.string.view_div) -> { "/" }
                getString(R.string.view_nthRoot) -> { "$" }
                getString(R.string.view_sqrt) -> { "2$" }
                getString(R.string.squared) -> { "^2" }
                getString(R.string.view_sin) -> { "sin(" }
                getString(R.string.view_arcSin) -> { "asin(" }
                getString(R.string.view_cos) -> { "cos(" }
                getString(R.string.view_log) -> { "log(" }
                getString(R.string.view_arcCos) -> { "acos(" }
                getString(R.string.view_tan) -> { "tan(" }
                getString(R.string.view_arcTan) -> { "atan(" }
                getString(R.string.view_ln) -> { "ln(" }
                getString(R.string.view_exp) -> { ":" }
                getString(R.string.view_fact) -> { "!" }
                getString(R.string.view_ans) -> { calcResult.text.toString().substringAfter("= ") }
                getString(R.string.view_rnd) -> { Math.random().toString() }
                else -> { string }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun evaluateExpression() {
        try {
            val result = solveExpression()
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

            Log.e("EXPRESSION", result.toString())

        } catch (e: ArithmeticException) {
            calcExpression.text = "Undefined"
        } catch (e: IllegalArgumentException) {
            calcExpression.text = "Error"
        }
    }

    private fun solveExpression(): Double {
        return ExpressionBuilder(expressionString)
            .operator(
                operators.factorial,
                operators.percent,
                operators.exp,
                operators.nthRoot
            )
            .functions(
                functions.sine,
                functions.arcSine,
                functions.cosine,
                functions.arcCosine,
                functions.tangent,
                functions.arcTangent,
                functions.natLog
            )
            .build()
            .evaluate()
    }

    private fun setAltButtons(list: MutableList<TextView>, array: Array<String>) {
        for (i in list.indices) {
            list[i].text = array[i]
        }
    }

    private fun invListeners(isAltButtons: Boolean) {
        if (isAltButtons) {
            invBtn_ln.setOnClickListener { appendToView(invBtn_ln.text.toString(), Type.FUNCTION) }
            invBtn_log.setOnClickListener { appendToView(invBtn_log.text.toString(), Type.FUNCTION) }
            invBtn_exponent.setOnClickListener {
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
            invBtn_exponent.setOnClickListener {
                val toBeConverted = calcExpression.text.toString().split('+','-','*','/').last()

                calcExpression.text.toString().dropLast(toBeConverted.length).also { calcExpression.text = it }
                for (element in toBeConverted) {
                    calcExpression.customAppend(element.toString(), isSuperscript = true)
                }
                calcExpression.append(getString(R.string.view_sqrt))
                writeExpression("$")
            }
        }
    }

    private fun setAngleUnit(string: SpannableStringBuilder) {
        string.clearSpans()
        when (functions.isRadians) {
            true -> {
                string.setSpan(ForegroundColorSpan(
                    ContextCompat.getColor(applicationContext, R.color.operationButton)),
                    6,
                    9,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                functions.isRadians = false
            }
            false -> {
                string.setSpan(ForegroundColorSpan(
                    ContextCompat.getColor(applicationContext, R.color.operationButton)),
                    0,
                    3,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                functions.isRadians = true
            }
        }

        btn_raddeg.text = string
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
