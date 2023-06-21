import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.feedarticlescompose.ui.theme.BlueApp

@Composable
fun CustomTextField(
    placeholder: String?,
    value: String,
    handleValue: (String) -> Unit,
    maxLines: Int = 1

    ){

    TextField(
        value = value,
        onValueChange = { handleValue(it) },
        placeholder = { Text(text = placeholder ?: "", color = Color.DarkGray) },
        minLines = maxLines,
        maxLines = maxLines,
        textStyle = TextStyle(color = Color.Black),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White
        )

    )

   // var isFocus by remember { mutableStateOf(true) }

   /* BasicTextField(
        value = value,
        modifier = Modifier
            .height(customHeight.dp)
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
                            color = if (isFocus) Color.Green else Color.Black,
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
            )  {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder ?: "",
                    )
                } else {
                    BasicTextField(
                        value =  value,
                        onValueChange = { handleValue(it) },
                        textStyle= TextStyle(
                            color = BlueApp
                        ),
                        maxLines = 15,
                        cursorBrush = SolidColor(Color.Green),
                    )
                }

            }
        }

    )*/

}
