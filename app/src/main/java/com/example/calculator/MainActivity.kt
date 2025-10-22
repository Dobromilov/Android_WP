package com.example.calculator

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.calculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var str_view = ""
    private var resetScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.button0.setOnClickListener{ appendStr("0") }
        binding.button1.setOnClickListener{ appendStr("1") }
        binding.button2.setOnClickListener{ appendStr("2") }
        binding.button3.setOnClickListener{ appendStr("3") }
        binding.button4.setOnClickListener{ appendStr("4") }
        binding.button5.setOnClickListener{ appendStr("5") }
        binding.button6.setOnClickListener{ appendStr("6") }
        binding.button7.setOnClickListener{ appendStr("7") }
        binding.button8.setOnClickListener{ appendStr("8") }
        binding.button9.setOnClickListener{ appendStr("9") }

        binding.buttonMinus.setOnClickListener { appendStr(" - ") }
        binding.buttonPlus.setOnClickListener { appendStr(" + " ) }
        binding.buttonDel.setOnClickListener { appendStr(" / ") }
        binding.buttonMultiplication.setOnClickListener { appendStr(" * ") }
        binding.buttonPoint.setOnClickListener{ appendStr(".")}

        binding.buttomEqually.setOnClickListener { checkResult() }

        binding.buttonDelete.setOnClickListener {
            binding.textView.text = ""
            str_view = ""
            resetScreen = false
        }
    }

    private fun appendStr(string: String){
        if (resetScreen){
            binding.textView.text=""
            str_view=""
            resetScreen=false
        } else {
            str_view += string
            binding.textView.text = str_view
        }
    }


    private fun checkResult(){
        if (str_view.trim().isEmpty()) {
            binding.textView.text = ""
            return
        }

        val strArray = str_view.split(" ").filter { it.isNotBlank() }.toTypedArray()

        if (strArray.isEmpty()) {
            binding.textView.text = ""
            return
        }

        var i = 0
        while (i < strArray.size - 1) {
            val current = strArray[i]
            val next = strArray[i + 1]

            if (current in arrayOf("*", "/", "+", "-") &&
                next in arrayOf("*", "/", "+", "-")) {
                binding.textView.text = "Error"
                return
            }
            i++
        }

        if (strArray.first() in arrayOf("*", "/", "+", "-") ||
            strArray.last() in arrayOf("*", "/", "+", "-")) {
            binding.textView.text = "Error"
            return
        }

        var tokens = strArray.toMutableList()

        i = 0
        while (i < tokens.size) {
            when (tokens[i]) {
                "*", "/" -> {
                    if (i == 0 || i == tokens.size - 1) {
                        binding.textView.text = "Error"
                        return
                    }

                    val left = tokens[i - 1].toDoubleOrNull()
                    val right = tokens[i + 1].toDoubleOrNull()

                    if (left == null || right == null) {
                        binding.textView.text = "Error"
                        return
                    }

                    val result = when (tokens[i]) {
                        "*" -> left * right
                        "/" -> {
                            if (right == 0.0) {
                                binding.textView.text = "Error"
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
                        binding.textView.text = "Error"
                        return
                    }

                    val left = tokens[i - 1].toDoubleOrNull()
                    val right = tokens[i + 1].toDoubleOrNull()

                    if (left == null || right == null) {
                        binding.textView.text = "Error"
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
            binding.textView.text = "Error"
            return
        }

        val finalResult = tokens[0].toDoubleOrNull()
        if (finalResult == null) {
            binding.textView.text = "Error"
            return
        }

        val formattedResult = if (finalResult % 1 == 0.0) {
            finalResult.toInt().toString()
        } else {
            finalResult.toString()
        }
        binding.textView.text = formattedResult
        str_view = formattedResult
        resetScreen = true
    }
}

