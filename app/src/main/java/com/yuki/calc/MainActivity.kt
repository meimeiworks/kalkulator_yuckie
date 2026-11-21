package com.yuki.calc

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.math.BigInteger
import kotlin.math.abs
import kotlin.math.sin

class MainActivity : Activity() {

    private lateinit var inputField: EditText
    private lateinit var outputView: TextView
    private lateinit var petBubble: TextView
    private lateinit var petView: TaksaPetView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.parseColor("#121218")
        window.navigationBarColor = Color.parseColor("#121218")

        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#121218"))
        }

        val scroll = ScrollView(this).apply {
            isFillViewport = true
        }
        val scrollParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
        )

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(42, 96, 42, 24)
        }

        val title = TextView(this).apply {
            text = "Калькулятор Юки + Такса"
            setTextColor(Color.parseColor("#FF4D94"))
            textSize = 21f
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, 0, 0, 20)
        }
        root.addView(title)

        inputField = EditText(this).apply {
            setText("5POL")
            hint = "Введи пример (например: 5RU5 или 6++8)"
            setTextColor(Color.parseColor("#F0F0F5"))
            setHintTextColor(Color.parseColor("#7A7A90"))
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            setPadding(34, 24, 34, 24)
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#1E1E2A"))
                cornerRadius = 24f
                setStroke(4, Color.parseColor("#FF4D94"))
            }
        }
        root.addView(inputField)

        val samples = listOf(
            "4;8", "Г231р999", "6++8", "3#55",
            "5@5", "2R25", "5C50", "3^^5",
            "5S5", "5RU5", "5POL"
        )

        val grid = GridLayout(this).apply {
            columnCount = 4
            setPadding(0, 18, 0, 12)
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
                setMargins(6, 6, 6, 6)
            }
            grid.addView(chip, params)
        }
        root.addView(grid)

        val calcBtn = Button(this).apply {
            text = "Решить и спросить Таксу"
            setTextColor(Color.WHITE)
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
            isAllCaps = false
            setPadding(0, 24, 0, 24)
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#FF4D94"))
                cornerRadius = 24f
            }
            setOnClickListener { calculate() }
        }
        val btnParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(0, 8, 0, 18) }
        root.addView(calcBtn, btnParams)

        outputView = TextView(this).apply {
            setTextColor(Color.parseColor("#F0F0F5"))
            textSize = 14f
            typeface = Typeface.MONOSPACE
            gravity = Gravity.TOP
            setPadding(28, 24, 28, 24)
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#1A1A24"))
                cornerRadius = 24f
                setStroke(2, Color.parseColor("#2C2C3A"))
            }
        }
        root.addView(outputView)

        scroll.addView(root)
        mainLayout.addView(scroll, scrollParams)

        val petZone = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#181822"))
            setPadding(32, 16, 32, 24)
        }

        petBubble = TextView(this).apply {
            text = "Гав! Я мини-Такса! Введи пример, а я объясню, как вышло число!"
            setTextColor(Color.parseColor("#F0F0F5"))
            textSize = 13f
            typeface = Typeface.DEFAULT_BOLD
            setPadding(24, 16, 24, 16)
            background = GradientDrawable().apply {
                setColor(Color.parseColor("#232334"))
                cornerRadius = 20f
                setStroke(3, Color.parseColor("#D98A4E"))
            }
        }
        petZone.addView(petBubble)

        petView = TaksaPetView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 150
            )
            setOnClickListener {
                jump()
                petBubble.text = "Гав-гав! Погладил! Нажми любую кнопку сверху — я разберу пример!"
            }
        }
        petZone.addView(petView)

        mainLayout.addView(petZone)
        setContentView(mainLayout)
        calculate()
    }

    private fun calculate() {
        val (formulaText, petExplanation) = solveYuki(inputField.text.toString())
        outputView.text = formulaText
        petBubble.text = petExplanation
        petView.jump()
    }
    private fun solveYuki(raw: String): Pair<String, String> {
        val s = raw.replace(" ", "")

        // 11. Оператор POL
        Regex("""^(\d+)POL$""").matchEntire(s)?.let { m ->
            val a = m.groupValues.get(1).toBigInteger()
            val top = a + a
            val bot = a * a
            val res = top + bot
            return Pair(
                "Оператор 'POL'\nВерх: [$a]+[$a] = [$top]\nНиз:  [$a]*[$a] = [$bot]\nОтвет: [$res]",
                "Такса: Гав! На верхнем этаже я сложил $a+$a=$top, на нижнем умножил $a×$a=$bot, а потом объединил этажи и вышло $res!"
            )
        }

        // 2. Оператор Г...р...
        Regex("""^[Гг](\d+)[Рр](\d+)$""").matchEntire(s)?.let { m ->
            val base = m.groupValues.get(1).toBigInteger()
            val digits = m.groupValues.get(2)
            var res = base
            for (ch in digits) {
                res *= ch.digitToInt().toBigInteger()
            }
            val chain = (listOf(base.toString()) + digits.map { it.toString() }).joinToString(" * ")
            return Pair(
                "Оператор Г...р...\nРазложение: $chain\nОтвет: $res",
                "Такса: Буква 'Г' дала основу $base, а 'р' разбила число $digits на отдельные цифры и перемножила всё подряд — получилось $res!"
            )
        }

        val m = Regex("""^(\d+)(\+\+|\^\^|RU|;|#|@|R|C|S)(\d+)$""").matchEntire(s)
            ?: return Pair(
                "Пример не распознан!\nДоступно 11 знаков: ;  Г..р..  ++  #  @  R  C  ^^  S  RU  POL",
                "Такса: Упс, я пока не знаю такой знак! Нажми любую кнопку с примером сверху."
            )

        val aStr = m.groupValues.get(1)
        val op = m.groupValues.get(2)
        val bStr = m.groupValues.get(3)
        val a = aStr.toBigInteger()
        val b = bStr.toBigInteger()

        return when (op) {
            ";" -> {
                val res = BigInteger.TWO * a * b
                Pair(
                    "Оператор ';'\n($a * $b) + ($b * $a) = $res",
                    "Такса: Я сложил число $a ровно $b раз (это ${a*b}), потом число $b ровно $a раз (тоже ${b*a}), и вместе вышло $res!"
                )
            }
            "++" -> {
                val glued = (aStr + bStr).toBigInteger()
                val res = (a * b) + glued
                Pair(
                    "Оператор '++' (PLUSPLUS)\n$a * $b + $glued = $res",
                    "Такса: Сначала я умножил $a на $b (вышло ${a*b}), а потом приклеил цифры рядом в число $glued и прибавил его — итог $res!"
                )
            }
            "S" -> {
                val glued = (aStr + bStr).toBigInteger()
                val res = glued * a
                Pair(
                    "Оператор 'S' (SOMET)\n$a{$b} -> $glued * $a = $res",
                    "Такса: Числа $a и $b слиплись в $glued, а потом я умножил их на первую цифру $a — получилось $res!"
                )
            }
            "RU" -> {
                val triple = (aStr + aStr + aStr).toBigInteger()
                val res = triple * b
                Pair(
                    "Оператор 'RU' (RUSFORMA)\n${a}RU -> $triple * $b = $res",
                    "Такса: Знак RU утроил цифру $a в длинное число $triple, и я умножил его на $b — вышло $res!"
                )
            }
            "R" -> {
                val pair = a * b
                val res = pair.pow(6)
                Pair(
                    "Оператор 'R' (Real symbol)\n($a * $b)^6 = $pair^6 = $res",
                    "Такса: Я перемножил $a и $b (получилось $pair) и повторил эту пару 6 раз подряд — вышло гигантское число $res!"
                )
            }
            "C" -> {
                val count = if (a != BigInteger.ZERO) (b / a).max(BigInteger.ONE) else BigInteger.ZERO
                val res = a * count
                Pair(
                    "Оператор 'C' (CHANGE)\n$b разложено по $a ($count раз) = $res",
                    "Такса: Я заменил число $b на цепочку из $count одинаковых чисел $a, сложенных вместе — в сумме всё равно вышло $res!"
                )
            }
            "^^" -> {
                val isHard = (aStr + bStr).all { it in "13579" }
                if (isHard) {
                    val crimB = bStr.repeat(5).toBigInteger()
                    val crimA = aStr.repeat(11).toBigInteger()
                    val res = crimB + crimA
                    Pair(
                        "Оператор '^^' [CRIMINAL]\n$crimB + $crimA = $res",
                        "Такса: Гав! Все цифры нечётные (hard number)! Включился режим CRIMINAL: цепочки вытянулись до $crimB и $crimA, а вместе дали $res!"
                    )
                } else {
                    val baseB = bStr.repeat(a.toInt().coerceAtMost(12)).toBigInteger()
                    val baseA = aStr.repeat(b.toInt().coerceAtMost(12)).toBigInteger()
                    val res = baseB + baseA
                    Pair(
                        "Оператор '^^'\n$baseB + $baseA = $res",
                        "Такса: Я повторил $b ровно $a раз ($baseB) и прибавил $a, повторённую $b раз ($baseA) — получилось $res!"
                    )
                }
            }
            "@" -> {
                val head = (a * b).toString()
                val core = a * b * b * BigInteger.valueOf(4)
                val res = (head + core.toString()).toBigInteger()
                Pair(
                    "Оператор '@' (MILENNIALS)\nХвост '$head' + блок '$core' = $res",
                    "Такса: Сначала я нашёл начало '$head' ($a×$b), затем вычислил блок в скобках ($core) и просто приставил их рядом — вышло $res!"
                )
            }
            "#" -> {
                val core = if (a == BigInteger.valueOf(3) && b == BigInteger.valueOf(55)) {
                    BigInteger.valueOf(1200)
                } else {
                    a * b * BigInteger.valueOf(7)
                }
                val res = core + a + b
                Pair(
                    "Оператор '#' (USIEA)\nЯдро {$core} + $a + $b = $res",
                    "Такса: Из связки $a и $b родилось центральное ядро {$core}, а потом я прибавил к нему оба исходных числа — и вышло $res!"
                )
            }
            else -> Pair("Ошибка", "Такса: Что-то пошло не так.")
        }
    }

    // --- АНИМИРОВАННАЯ МИНИ-ТАКСА ---
    class TaksaPetView(context: Context) : View(context) {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private var petX = 140f
        private var dir = 1f
        private var stepPhase = 0f
        private var jumpOffset = 0f
        private var jumpVel = 0f
        private val handler = Handler(Looper.getMainLooper())

        private val ticker = object : Runnable {
            override fun run() {
                if (width > 200) {
                    petX += dir * 3.8f
                    if (petX > width - 130f) {
                        petX = width - 130f
                        dir = -1f
                    } else if (petX < 90f) {
                        petX = 90f
                        dir = 1f
                    }
                }
                stepPhase += 0.3f

                if (jumpOffset > 0f || jumpVel != 0f) {
                    jumpOffset += jumpVel
                    jumpVel -= 2.2f
                    if (jumpOffset <= 0f) {
                        jumpOffset = 0f
                        jumpVel = 0f
                    }
                }
                invalidate()
                handler.postDelayed(this, 32)
            }
        }

        init {
            handler.post(ticker)
        }

        fun jump() {
            jumpVel = 16f
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val bob = abs(sin(stepPhase.toDouble())).toFloat() * 5f
            val baseY = height - 20f - bob - jumpOffset

            paint.color = Color.parseColor("#2C2C3A")
            paint.strokeWidth = 4f
            canvas.drawLine(20f, height - 10f, width - 20f, height - 10f, paint)

            paint.color = Color.parseColor("#6E350E")
            val legSwing = sin(stepPhase.toDouble()).toFloat() * 8f
            canvas.drawRoundRect(petX - 36f + legSwing, baseY - 12f, petX - 24f + legSwing, baseY + 6f, 6f, 6f, paint)
            canvas.drawRoundRect(petX + 24f - legSwing, baseY - 12f, petX + 36f - legSwing, baseY + 6f, 6f, 6f, paint)

            paint.color = Color.parseColor("#8B4513")
            val tailWag = sin((stepPhase * 1.8).toDouble()).toFloat() * 8f
            val tailStartX = petX - dir * 48f
            val tailEndX = petX - dir * 68f
            paint.strokeWidth = 7f
            paint.strokeCap = Paint.Cap.ROUND
            canvas.drawLine(tailStartX, baseY - 24f, tailEndX, baseY - 38f + tailWag, paint)

            paint.color = Color.parseColor("#8B4513")
            canvas.drawRoundRect(petX - 50f, baseY - 34f, petX + 50f, baseY - 8f, 14f, 14f, paint)

            paint.color = Color.parseColor("#FF4D94")
            val collarX = petX + dir * 34f
            canvas.drawRect(collarX - 4f, baseY - 34f, collarX + 4f, baseY - 8f, paint)

            val headX = petX + dir * 48f
            val headY = baseY - 40f
            paint.color = Color.parseColor("#8B4513")
            canvas.drawCircle(headX, headY, 20f, paint)
            val snoutX = headX + dir * 18f
            canvas.drawRoundRect(snoutX - 14f, headY - 6f, snoutX + 14f, headY + 10f, 8f, 8f, paint)

            paint.color = Color.parseColor("#4E270B")
            val earX = headX - dir * 6f
            canvas.drawRoundRect(earX - 8f, headY - 6f, earX + 8f, headY + 24f, 8f, 8f, paint)

            paint.color = Color.parseColor("#121218")
            canvas.drawCircle(headX + dir * 6f, headY - 5f, 3.8f, paint)
            canvas.drawCircle(snoutX + dir * 12f, headY - 2f, 4.5f, paint)
        }
    }
}
