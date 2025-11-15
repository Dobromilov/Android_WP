package com.example.android_development

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button

class calc : AppCompatActivity() {
    private lateinit var textView: TextView
    private var str_view = ""
    private var resetScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calc)

        // Инициализация View
        textView = findViewById(R.id.textView)

        // Инициализация кнопок
        findViewById<Button>(R.id.button_0).setOnClickListener{ appendStr("0") }
        findViewById<Button>(R.id.button_1).setOnClickListener{ appendStr("1") }
        findViewById<Button>(R.id.button_2).setOnClickListener{ appendStr("2") }
        findViewById<Button>(R.id.button_3).setOnClickListener{ appendStr("3") }
        findViewById<Button>(R.id.button_4).setOnClickListener{ appendStr("4") }
        findViewById<Button>(R.id.button_5).setOnClickListener{ appendStr("5") }
        findViewById<Button>(R.id.button_6).setOnClickListener{ appendStr("6") }
        findViewById<Button>(R.id.button_7).setOnClickListener{ appendStr("7") }
        findViewById<Button>(R.id.button_8).setOnClickListener{ appendStr("8") }
        findViewById<Button>(R.id.button_9).setOnClickListener{ appendStr("9") }

        findViewById<Button>(R.id.button_minus).setOnClickListener { appendStr(" - ") }
        findViewById<Button>(R.id.button_plus).setOnClickListener { appendStr(" + " ) }
        findViewById<Button>(R.id.button_del).setOnClickListener { appendStr(" / ") }
        findViewById<Button>(R.id.button_multiplication).setOnClickListener { appendStr(" * ") }
        findViewById<Button>(R.id.button_point).setOnClickListener{ appendStr(".")}

        findViewById<Button>(R.id.buttom_equally).setOnClickListener { checkResult() }

        findViewById<Button>(R.id.button_Delete).setOnClickListener {
            textView.text = ""
            str_view = ""
            resetScreen = false
        }
    }

    private fun appendStr(string: String){
        if (resetScreen){
            textView.text=""
            str_view=""
            resetScreen=false
        } else {
            str_view += string
            textView.text = str_view
        }
    }

    private fun checkResult(){
        if (str_view.trim().isEmpty()) {
            textView.text = ""
            return
        }

        val strArray = str_view.split(" ").filter { it.isNotBlank() }.toTypedArray()

        if (strArray.isEmpty()) {
            textView.text = ""
            return
        }

        var i = 0
        while (i < strArray.size - 1) {
            val current = strArray[i]
            val next = strArray[i + 1]

            if (current in arrayOf("*", "/", "+", "-") &&
                next in arrayOf("*", "/", "+", "-")) {
                textView.text = "Error"
                return
            }
            i++
        }

        if (strArray.first() in arrayOf("*", "/", "+", "-") ||
            strArray.last() in arrayOf("*", "/", "+", "-")) {
            textView.text = "Error"
            return
        }

        var tokens = strArray.toMutableList()

        i = 0
        while (i < tokens.size) {
            when (tokens[i]) {
                "*", "/" -> {
                    if (i == 0 || i == tokens.size - 1) {
                        textView.text = "Error"
                        return
                    }

                    val left = tokens[i - 1].toDoubleOrNull()
                    val right = tokens[i + 1].toDoubleOrNull()

                    if (left == null || right == null) {
                        textView.text = "Error"
                        return
                    }

                    val result = when (tokens[i]) {
                        "*" -> left * right
                        "/" -> {
                            if (right == 0.0) {
                                textView.text = "Error"
                                return
                            }
                            left / right
                        }
                        else -> 0.0
                    }

                    tokens[i - 1] = result.toString()
                    tokens.removeAt(i)
                    tokens.removeAt(i)
                    i--
                }
            }
            i++
        }

        i = 0
        while (i < tokens.size) {
            when (tokens[i]) {
                "+", "-" -> {
                    if (i == 0 || i == tokens.size - 1) {
                        textView.text = "Error"
                        return
                    }

                    val left = tokens[i - 1].toDoubleOrNull()
                    val right = tokens[i + 1].toDoubleOrNull()

                    if (left == null || right == null) {
                        textView.text = "Error"
                        return
                    }

                    val result = when (tokens[i]) {
                        "+" -> left + right
                        "-" -> left - right
                        else -> 0.0
                    }

                    tokens[i - 1] = result.toString()
                    tokens.removeAt(i)
                    tokens.removeAt(i)
                    i--
                }
            }
            i++
        }

        if (tokens.size != 1) {
            textView.text = "Error"
            return
        }

        val finalResult = tokens[0].toDoubleOrNull()
        if (finalResult == null) {
            textView.text = "Error"
            return
        }

        val formattedResult = if (finalResult % 1 == 0.0) {
            finalResult.toInt().toString()
        } else {
            finalResult.toString()
        }
        textView.text = formattedResult
        str_view = formattedResult
        resetScreen = true
    }
}