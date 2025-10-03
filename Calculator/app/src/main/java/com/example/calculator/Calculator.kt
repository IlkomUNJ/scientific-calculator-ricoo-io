package com.example.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowRow
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.calculator.ui.theme.CalculatorTheme

object CalculatorScreen {
    @Composable
    fun SetupLayout() {
        var firstValue by remember { mutableStateOf("") }
        var secondValue by remember { mutableStateOf("") }
        var operator by remember { mutableStateOf("") }
        var display by remember { mutableStateOf("") }
        var currentOperation by remember {mutableStateOf("")}

        val scientific = listOf("1/x", "x!", "x^y", "√x", "log", "ln", "sin", "cos", "tan", "arcsin", "arccos", "arctan")
        val expandScientific = remember { mutableStateOf(false) }

        fun EvaluateOperation(first: String, op: String, second: String): Double {
            return when (op) {
                "+" -> first.toDouble() + second.toDouble()
                "-" -> first.toDouble() - second.toDouble()
                "x", "*" -> first.toDouble() * second.toDouble()
                "÷", "/" -> if (second != "0") first.toDouble() / second.toDouble() else Double.NaN
                "^" -> Math.pow(first.toDouble(), second.toDouble())
                else -> 0.0
            }
        }

        fun InputValue(number: String) {
            if (operator.isEmpty()) {
                if (number == "." && firstValue.contains(".")) return
                firstValue += number
                display = firstValue
            } else {
                if (number == "." && secondValue.contains(".")) return
                secondValue += number
                display = secondValue
            }
        }

        fun InputOperator(op: String) {
            if (firstValue.isNotEmpty() && operator.isEmpty()) {
                operator = op
            } else if (firstValue.isNotEmpty() && operator.isNotEmpty() && secondValue.isNotEmpty()) {
                val result = EvaluateOperation(firstValue, operator, secondValue)
                currentOperation = "$firstValue $operator $secondValue"
                firstValue = result.toString()
                secondValue = ""
                operator = op
                display = firstValue
            } else if (firstValue.isEmpty() && op == "-") {
                firstValue = "-"
                display = firstValue
            }

        }

        fun Evaluate() {
            if (firstValue.isNotEmpty() && operator.isNotEmpty() && secondValue.isNotEmpty()) {
                val result = EvaluateOperation(firstValue, operator, secondValue)
                display = result.toString()
                currentOperation = "$firstValue $operator $secondValue"
                firstValue = result.toString()
                secondValue = ""
                operator = ""
            }
        }

        fun ClearOperation() {
            firstValue = ""
            secondValue = ""
            operator = ""
            display = ""
            currentOperation=""
        }

        fun ConvertSign() {
            if (operator.isEmpty()) {

                if (firstValue.isNotEmpty()) {
                    firstValue = if (firstValue.startsWith("-")) {
                        firstValue.drop(1)
                    } else {
                        "-$firstValue"
                    }
                } else {

                    firstValue = "-"
                }
                display = firstValue
            } else {

                if (secondValue.isNotEmpty()) {
                    secondValue = if (secondValue.startsWith("-")) {
                        secondValue.drop(1)
                    } else {
                        "-$secondValue"
                    }
                } else {
                    secondValue = "-"
                }
                display = secondValue
            }
        }

        fun ProcessScientific(op: String) {
            if (op == "x^y" && firstValue.isNotEmpty()) {
                operator = "^"
            }
            if (firstValue.isNotEmpty() && operator.isEmpty()) {
                val x = firstValue.toDouble()
                val result = when (op) {
                    "√x" -> kotlin.math.sqrt(x).also { currentOperation = "√($x)" }
                    "log" -> kotlin.math.log10(x).also { currentOperation = "log($x)" }
                    "ln" -> kotlin.math.ln(x).also { currentOperation = "ln($x)" }
                    "sin" -> kotlin.math.sin(Math.toRadians(x)).also { currentOperation = "sin($x)" }
                    "cos" -> kotlin.math.cos(Math.toRadians(x)).also { currentOperation = "cos($x)" }
                    "tan" -> kotlin.math.tan(Math.toRadians(x)).also { currentOperation = "tan($x)" }
                    "arcsin" -> Math.toDegrees(kotlin.math.asin(x)).also { currentOperation = "arcsin($x)" }
                    "arccos" -> Math.toDegrees(kotlin.math.acos(x)).also { currentOperation = "arccos($x)" }
                    "arctan" -> Math.toDegrees(kotlin.math.atan(x)).also { currentOperation = "arctan($x)" }
                    "1/x" -> if (x != 0.0) (1 / x).also { currentOperation = "1/($x)" } else Double.NaN.also { currentOperation = "1/($x)" }
                    "x!" -> factorial(x.toInt()).also { currentOperation = "$x!" }
                    "%" -> (x / 100).also { currentOperation = "($x)/100" }
                    else -> return
                }
                firstValue = result.toString()
                display = firstValue
            }
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                CalculatorSecondaryScreen(currentOperation)
                CalculatorScreen(display)
            }

            HorizontalDivider(
                color = Color.White,
                thickness = 0.8.dp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Button(
                onClick = { expandScientific.value = !expandScientific.value },
                modifier = Modifier
                    .height(40.dp)
                    .width(((LocalConfiguration.current.screenWidthDp) * 0.9).dp)
                    .padding(bottom = 12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    contentColor = MaterialTheme.colorScheme.background
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp),
                contentPadding = PaddingValues(bottom = 4.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(if (expandScientific.value) "▼" else "▲")
            }

            AnimatedVisibility(
                visible = expandScientific.value,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                FlowRow(
                    modifier = Modifier.padding(2.dp),
                ) {
                    scientific.forEach { value ->
                        CalculatorScientificButton(
                            value = value,
                            buttonAction = { ProcessScientific(value) }
                        )
                    }
                }
            }

            Row {
                CalculatorButton("AC", { ClearOperation() }, MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
                CalculatorButton("+/-", { ConvertSign() }, MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
                CalculatorButton("%", { ProcessScientific("%") }, MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
                CalculatorButton("÷", { InputOperator("÷") }, MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
            }
            Row {
                CalculatorButton("7", { InputValue("7") })
                CalculatorButton("8", { InputValue("8") })
                CalculatorButton("9", { InputValue("9") })
                CalculatorButton("x", { InputOperator("x") }, MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
            }
            Row {
                CalculatorButton("4", { InputValue("4") })
                CalculatorButton("5", { InputValue("5") })
                CalculatorButton("6", { InputValue("6") })
                CalculatorButton("+", { InputOperator("+") }, MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
            }
            Row {
                CalculatorButton("1", { InputValue("1") })
                CalculatorButton("2", { InputValue("2") })
                CalculatorButton("3", { InputValue("3") })
                CalculatorButton("-", { InputOperator("-") }, MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
            }
            Row {
                CalculatorButton("0", { InputValue("0") })
                CalculatorButton(".", { InputValue(".") })
                CalculatorButton("⌫", {
                    if (operator.isEmpty() && firstValue.isNotEmpty()) {
                        firstValue = firstValue.dropLast(1)
                        display = firstValue
                    } else if (secondValue.isNotEmpty()) {
                        secondValue = secondValue.dropLast(1)
                        display = secondValue
                    }
                })
                CalculatorButton("=", { Evaluate() }, MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    }

    private fun factorial(n: Int): Double {
        return if (n <= 1) 1.0 else n * factorial(n - 1)
    }

    @Composable
    fun CalculatorButton(value: String, buttonAction: () -> Unit, containerColor: Color = MaterialTheme.colorScheme.secondary, textColor: Color = MaterialTheme.colorScheme.onSecondary){
        val buttonText= remember {mutableStateOf("$value")}
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val buttonHeight = screenWidth.value / 5
        val buttonWidth = screenWidth.value / 4.5
        val fontSize = (buttonHeight* 0.4).sp

        Button( modifier = Modifier.height(buttonHeight.dp).width(buttonWidth.dp).padding(4.dp),onClick = buttonAction,contentPadding = PaddingValues (0.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                containerColor = containerColor
            )
            ,shape = RoundedCornerShape(30.dp)
        ) {
            Text(
                fontSize = fontSize,
                color = textColor,
                maxLines = 1,
                text=buttonText.value
            )

        }
    }
    @Composable
    fun CalculatorScientificButton(value: String, buttonAction: () -> Unit, containerColor: Color = MaterialTheme.colorScheme.tertiary, textColor: Color = MaterialTheme.colorScheme.onTertiary ){
        val buttonText= remember {mutableStateOf("$value")}
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val buttonWidth = screenWidth.value / 4.5
        val buttonHeight = screenWidth.value / 7.5
        val fontSize = (buttonWidth * 0.2).sp

        Button(
            modifier = Modifier
                .width(buttonWidth.dp)
                .height(buttonHeight.dp)
                .padding(3.dp)
            ,onClick = buttonAction,contentPadding = PaddingValues (0.dp), shape = RoundedCornerShape(20.dp),

            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                containerColor = containerColor
            )
        ) {
            Text(
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                text=buttonText.value,
                color = textColor
            )

        }
    }

    @Composable
    fun CalculatorScreen(display: String){
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val fontSize = (screenWidth.value/8).sp
        Row(
            modifier= Modifier.fillMaxWidth().padding(start=16.dp, end=16.dp),
            horizontalArrangement = Arrangement.End
        ){
            Text(
                color= MaterialTheme.colorScheme.onBackground,
                fontSize = fontSize,
                lineHeight = fontSize * 1f,
                textAlign = TextAlign.End,
                text=display

            )
        }
    }

    @Composable
    fun CalculatorSecondaryScreen(operationDisplayed:String){
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val fontSize = (screenWidth.value/14).sp
        Row(
            modifier= Modifier.fillMaxWidth().padding(start=16.dp, end=16.dp),
            horizontalArrangement = Arrangement.End
        ){
            Text(
                color= MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = fontSize,
                lineHeight = fontSize * 1f,
                textAlign = TextAlign.End,
                text = operationDisplayed

            )
        }
    }
}

@Preview
@Composable
fun CalculatorPreview(){
    CalculatorTheme {
        CalculatorScreen.SetupLayout()
    }
}