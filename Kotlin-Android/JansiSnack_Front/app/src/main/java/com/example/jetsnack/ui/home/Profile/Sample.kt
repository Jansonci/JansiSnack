
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetsnack.R
import com.example.jetsnack.ui.components.AlertDialog
import com.example.jetsnack.ui.theme.JetnewsTheme
import com.example.jetsnack.ui.theme.Typography1

@Composable
fun CustomTextField(

) {
    AlertDialog(
        onDismissRequest = {   },
        title = {
            Text("Delete Collection", style = MaterialTheme.typography.bodyLarge,
                fontFamily = FontFamily(Font(R.font.montserrat_semibold)))
        },
        text = { Text("删除这个收藏?", style = Typography1.h1 ) },
        confirmButton = {
            TextButton(onClick = {  }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = {  }) {
                Text("Cancel")
            }
        },
        modifier = Modifier.width(50.dp)
    )
}


@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen1() {
    // 调用你的 LoginScreen 组件来渲染预览
    // 我们在这里提供一个 Modifier，添加一些 padding 来更好地展示预览
    JetnewsTheme {
        CustomTextField()
    }
}