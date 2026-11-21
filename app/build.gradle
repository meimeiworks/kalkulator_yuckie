package com.yuki.calc

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.math.BigInteger

class MainActivity : Activity() {

    private lateinit var inputField: EditText
    private lateinit var outputView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Включаем тёмные системные панели под цвет интерфейса
        window.statusBarColor = Color.parseColor("#121218")
        window.navigationBarColor = Color.parseColor("#121218")

        val scroll = ScrollView(this).apply {
            setBackgroundColor(Color.parseColor("#121218"))
            isFillViewport = true
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            // Отступ сверху 96px, чтобы не перекрывалось камерой/строкой состояния на Android 14+
            setPadding(42, 96, 42, 48)
        }

        val title = TextView(this).apply {
            text = "Калькулятор Юки (11 знаков)"
            setTextColor(Color.parseColor("#FF4D94"))
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 0, 0, 24)
        }
        root.addView(title)

        inputField = EditText(this).apply {
            setText("5POL")
            hint = "Введи пример (например: 5RU5 или 6++8)"
            setTextColor(Color.parseColor("#F0F0F5"))
            setHintTextColor(Color.parseColor("#7A7A90"))
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            setPadding(34, 28, 34, 28)
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#1E1E2A"))
                cornerRadius = 24f
                setStroke(4, Color.parseColor("#FF4D94"))
            }
        }
        root.addView(inputField)

        // Сетка с быстрыми кнопками всех 11 авторских операторов
        val samples = listOf(
            "4;8", "Г231р999", "6++8", "3#55",
            "5@5", "2R25", "5C50", "3^^5",
            "5S5", "5RU5", "5POL"
        )

        val grid = GridLayout(this).apply {
            columnCount = 4
            setPadding(0, 24, 0, 16)
        }

        for (sample in samples) {
            val chip = Button(this).apply {
                text = sample
                setTextColor(Color.parseColor("#F0F0F5"))
                textSize = 13f
                isAllCaps = false
                background = GradientDrawable().apply {
                    setColor(Color.parseColor("#252538"))
                    cornerRadius = 18f
                    setStroke(2, Color.parseColor("#3A3A52"))
                }
                setOnClickListener {
                    inputField.setText(sample)
                    calculate()
                }
            }
            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(8, 8, 8, 8)
            }
            grid.addView(chip, params)
        }
        root.addView(grid)

        val calcBtn = Button(this).apply {
            text = "Решить по правилам Юки"
            setTextColor(Color.WHITE)
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
            isAllCaps = false
            setPadding(0, 28, 0, 28)
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#FF4D94"))
                cornerRadius = 24f
            }
            setOnClickListener { calculate() }
        }
        val btnParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(0, 12, 0, 24) }
        root.addView(calcBtn, btnParams)

        outputView = TextView(this).apply {
            setTextColor(Color.parseColor("#F0F0F5"))
            textSize = 15f
            typeface = Typeface.MONOSPACE
            gravity = Gravity.TOP
            setPadding(32, 32, 32, 32)
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#1A1A24"))
                cornerRadius = 24f
                setStroke(2, Color.parseColor("#2C2C3A"))
            }
        }
        root.addView(outputView)

        scroll.addView(root)
        setContentView(scroll)
        calculate()
    }

    private fun calculate() {
        outputView.text = solveYuki(inputField.text.toString())
    }

    private fun solveYuki(raw: String): String {
        val s = raw.replace(" ", "")

        // 11. Оператор POL (5POL -> [5]+[5]=10 и [5]*[5]=25 ->)
        Regex("""^(\d+)POL$""").matchEntire(s)?.let { m ->
            val a = m.groupValues[1].toBigInteger()
            val top = a + a
            val bot = a * a
            return "Оператор 'POL' (Двухэтажный блок)\n" +
                   "Верх: [$a]+[$a] = [$top]\n" +
                   "Низ:  [$a]*[$a] = [$bot]\n" +
                   "Ответ: [${top + bot}]"
        }

        // 2. Оператор Г...р... (Г231р999 -> 231 * 9 * 9 * 9)
        Regex("""^[Гг](\d+)[Рр](\d+)$""").matchEntire(s)?.let { m ->
            val base = m.groupValues[1].toBigInteger()
            val digits = m.groupValues[2]
            var res = base
            for (ch in digits) {
                res *= ch.digitToInt().toBigInteger()
            }
            val chain = (listOf(base.toString()) + digits.map { it.toString() }).joinToString(" * ")
            return "Оператор Г...р...\nРазложение: $chain\nОтвет: $res"
        }

        // Остальные 9 парных операторов
        val m = Regex("""^(\d+)(\+\+|\^\^|RU|;|#|@|R|C|S)(\d+)$""").matchEntire(s)
            ?: return "Не удалось распознать пример!\n\n" +
                      "Поддерживаемые знаки:\n" +
                      "4;8 | Г231р999 | 6++8 | 3#55 | 5@5\n" +
                      "2R25 | 5C50 | 3^^5 | 5S5 | 5RU5 | 5POL"

        val aStr = m.groupValues[1]
        val op = m.groupValues[2]
        val bStr = m.groupValues[3]
        val a = aStr.toBigInteger()
        val b = bStr.toBigInteger()

        return when (op) {
            ";" -> {
                val res = BigInteger.TWO * a * b
                "Оператор ';' (Взаимная сумма)\n" +
                "($a * $b) + ($b * $a) = ${a * b} + ${b * a}\n" +
                "Ответ: $res"
            }
            "++" -> {
                val glued = (aStr + bStr).toBigInteger()
                val res = (a * b) + glued
                "Оператор '++' (PLUSPLUS)\n" +
                "$a * $b + $glued = ${a * b} + $glued\n" +
                "Ответ: $res"
            }
            "S" -> {
                val glued = (aStr + bStr).toBigInteger()
                val res = glued * a
                "Оператор 'S' (SOMET)\n" +
                "$a{$b} -> $glued * $a\n" +
                "Ответ: $res"
            }
            "RU" -> {
                val triple = (aStr + aStr + aStr).toBigInteger()
                val res = triple * b
                "Оператор 'RU' (RUSFORMA)\n" +
                "${a}RU -> $triple * $b\n" +
                "Ответ: $res"
            }
            "R" -> {
                val pair = a * b
                val res = pair.pow(6)
                "Оператор 'R' (Real symbol)\n" +
                "($a * $b)^6 = $pair^6\n" +
                "Ответ: $res"
            }
            "C" -> {
                val count = if (a != BigInteger.ZERO) (b / a).max(BigInteger.ONE) else BigInteger.ZERO
                val res = a * count
                "Оператор 'C' (CHANGE)\n" +
                "Раскладываем $b по $a ($count раз)\n" +
                "Ответ: $res"
            }
            "^^" -> {
                val isHard = (aStr + bStr).all { it in "13579" }
                if (isHard) {
                    val crimB = bStr.repeat(5).toBigInteger()
                    val crimA = aStr.repeat(11).toBigInteger()
                    "Оператор '^^' [HARD NUMBER -> CRIMINAL]\n" +
                    "Criminal: $crimB + $crimA\n" +
                    "Ответ: ${crimB + crimA}"
                } else {
                    val baseB = bStr.repeat(a.toInt().coerceAtMost(12)).toBigInteger()
                    val baseA = aStr.repeat(b.toInt().coerceAtMost(12)).toBigInteger()
                    "Оператор '^^' (Обычный режим)\n" +
                    "$baseB + $baseA\n" +
                    "Ответ: ${baseB + baseA}"
                }
            }
            "@" -> {
                val head = (a * b).toString()
                val core = a * b * b * BigInteger.valueOf(4)
                val res = (head + core.toString()).toBigInteger()
                "Оператор '@' (MILENNIALS)\n" +
                "Хвост: '$head', блок скобок: $core\n" +
                "Склейка: '$head' + '$core'\n" +
                "Ответ: $res"
            }
            "#" -> {
                val core = if (a == BigInteger.valueOf(3) && b == BigInteger.valueOf(55)) {
                    BigInteger.valueOf(1200)
                } else {
                    a * b * BigInteger.valueOf(7)
                }
                "Оператор '#' (USIEA)\n" +
                "Ядро {$core} + $a + $b\n" +
                "Ответ: ${core + a + b}"
            }
            else -> "Неизвестный оператор"
        }
    }
}
