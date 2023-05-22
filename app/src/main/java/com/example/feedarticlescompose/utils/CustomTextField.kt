import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun CustomTextField(
    placeholder: String?,
    value: String,
    handleValue: (String) -> Unit,

    ){

    var isFocus by remember { mutableStateOf(true) }

    BasicTextField(
        value = value,
       /* textStyle= TextStyle(
            color =
        ),*/
        modifier = Modifier
            .onFocusChanged {
                isFocus = !isFocus
            },
        onValueChange = { handleValue(it) },
        decorationBox = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        drawLine(
                            color = if(isFocus) Color.Green else Color.Black,
                            start = Offset(0f, size.height),
                            end = Offset(size.width,size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
            )  {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder ?: "",
                        color = Color.Gray
                    )
                } else {
                    val scroll = rememberScrollState(0)
                    Text(
                        modifier = Modifier.horizontalScroll(scroll),
                        text =  value,
                        color = Color.Black
                    )
                }

            }
        }

    )

}
