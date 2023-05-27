package com.hevanafa.tippycompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hevanafa.tippycompose.ui.theme.TippyComposeTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MyState(
    val baseAmountText: String = "",
    val perc: Float = 0f
)

class MainActivity : ComponentActivity() {
    private val _uiState = MutableStateFlow(MyState())
    val uiStateFlow = _uiState.asStateFlow()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TippyComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        TopImageRow()

                        BaseAmountRow()
                        TipPercentageRow()

                        TipAmountRow()
                        TotalAmountRow()
                    }
                }
            }
        }
    }

    @Composable
    fun TopImageRow() {
        Row (horizontalArrangement = Arrangement.Center) {
            Image(
                painter = painterResource(id = R.drawable.vivid_by_aziru),
                contentDescription = "Vivid"
            )
        }
    }

    @Composable
    fun getBaseAmount(): Float {
        val uiState by uiStateFlow.collectAsState()
        val text = uiState.baseAmountText

        // Java doesn't like it when I try to parse an empty string
        return if (text.isEmpty()) { 0f } else { text.toFloat() }
    }

    @Composable
    fun getTipStr(): String {
        val uiState by uiStateFlow.collectAsState()
        val perc = uiState.perc

        return "%.2f%%".format(perc * 100f)
    }

    @Composable
    fun LabelComp(text: String) {
        Text(
            text,
            textAlign = TextAlign.Right,
            modifier = Modifier.width(90.dp).absolutePadding(right = 10.dp),
            fontWeight = FontWeight.Bold,
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun BaseAmountRow() {
        val pattern = remember { Regex("^\\d+\$" )}
        val uiState by uiStateFlow.collectAsState()
        val text = uiState.baseAmountText

        Row {
            LabelComp("Base")

            TextField(
                value = text,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.matches(pattern)) {
                        _uiState.update {
                            it.copy(baseAmountText = newValue)
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        }
    }

    @Composable
    fun TipPercentageRow() {
        val uiState by uiStateFlow.collectAsState()
        val perc = uiState.perc

        Row {
            LabelComp(getTipStr())

            Slider(
                value = perc,
                onValueChange = { newValue ->
                    _uiState.update {
                        it.copy(
                            perc = newValue
                        )
                    }
                },
                valueRange = 0f..0.25f
            )
        }
    }

    @Composable
    fun getTipAmount(): Float {
        val uiState by uiStateFlow.collectAsState()
        val perc = uiState.perc

        return getBaseAmount() * perc
    }

    @Composable
    fun TipAmountRow() {
        Row {
            LabelComp("Tip")

            Text("$%.2f".format(getTipAmount()))
        }
    }

    @Composable
    fun TotalAmountRow() {
        Row {
            LabelComp("Total amount")

            Text("$%.2f".format(getBaseAmount() + getTipAmount()))
        }
    }
}




@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TippyComposeTheme {
    }
}