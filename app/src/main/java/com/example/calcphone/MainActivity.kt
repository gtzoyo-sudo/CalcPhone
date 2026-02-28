package com.example.calcphone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.roundToLong

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}

@Composable
private fun App() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            CalculatorScreen()
        }
    }
}

@Composable
private fun CalculatorScreen() {
    var display by remember { mutableStateOf("0") }
    var acc by remember { mutableStateOf<Double?>(null) }
    var op by remember { mutableStateOf<String?>(null) }
    var reset by remember { mutableStateOf(false) }

    fun fmt(x: Double): String {
        val r = x.roundToLong()
        return if (abs(x - r.toDouble()) < 1e-10) r.toString() else x.toString()
    }

    fun digit(d: String) {
        display = if (reset || display == "0") d else display + d
        reset = false
    }

    fun dot() {
        if (reset) {
            display = "0."
            reset = false
            return
        }
        if (!display.contains(".")) display += "."
    }

    fun ac() {
        display = "0"
        acc = null
        op = null
        reset = false
    }

    fun apply(newOp: String) {
        val cur = display.toDoubleOrNull() ?: 0.0

        if (acc == null) {
            acc = cur
        } else if (op != null && !reset) {
            acc = when (op) {
                "+" -> acc!! + cur
                "−" -> acc!! - cur
                "×" -> acc!! * cur
                "÷" -> if (cur == 0.0) Double.NaN else acc!! / cur
                else -> acc
            }
            display = if (acc!!.isNaN()) "Hiba" else fmt(acc!!)
        }

        op = newOp
        reset = true
    }

    fun equals() {
        val cur = display.toDoubleOrNull() ?: 0.0
        val a = acc
        val o = op
        if (a != null && o != null) {
            val res = when (o) {
                "+" -> a + cur
                "−" -> a - cur
                "×" -> a * cur
                "÷" -> if (cur == 0.0) Double.NaN else a / cur
                else -> cur
            }
            display = if (res.isNaN()) "Hiba" else fmt(res)
            acc = if (res.isNaN()) null else res
            op = null
            reset = true
        }
    }

    fun sign() {
        val v = display.toDoubleOrNull() ?: return
        display = fmt(-v)
    }

    fun percent() {
        val v = display.toDoubleOrNull() ?: return
        display = fmt(v / 100.0)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = display,
            modifier = Modifier.fillMaxWidth().weight(1f),
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.End
        )

        fun RowScope.key(t: String, w: Float = 1f, onClick: () -> Unit) {
            Button(
                onClick = onClick,
                modifier = Modifier.weight(w).height(64.dp)
            ) { Text(t) }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                key("AC") { ac() }
                key("±") { sign() }
                key("%") { percent() }
                key("÷") { apply("÷") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                key("7") { digit("7") }
                key("8") { digit("8") }
                key("9") { digit("9") }
                key("×") { apply("×") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                key("4") { digit("4") }
                key("5") { digit("5") }
                key("6") { digit("6") }
                key("−") { apply("−") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                key("1") { digit("1") }
                key("2") { digit("2") }
                key("3") { digit("3") }
                key("+") { apply("+") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                key("0", w = 2f) { digit("0") }
                key(".") { dot() }
                key("=") { equals() }
            }
        }
    }
}
