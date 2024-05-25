//
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.ModalBottomSheet
//import androidx.compose.material3.rememberModalBottomSheetState
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import kotlinx.coroutines.launch
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun BottomSheetExampleMaterial3() {
//    val sheetState = rememberModalBottomSheetState(
//        skipPartiallyExpanded = true,
//        confirmValueChange = { newValue ->
//            // 根据实际需要，这里可以加入逻辑判断是否允许状态变化
//            true
//        }
//    )
//    val coroutineScope = rememberCoroutineScope()
//
//    ModalBottomSheet(
//        sheetState = sheetState,
//        onDismissRequest = { coroutineScope.launch { sheetState.hide() } },
//        scrimColor = Color(0x99000000), // 透明遮罩颜色
//        content = {
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp)
//            ) {
////                Text(text = "This is the Material 3 bottom sheet content", style = MaterialTheme.typography.headlineSmall)
////                Spacer(modifier = Modifier.height(20.dp))
////                Text("More content here...", style = MaterialTheme.typography.bodyLarge)
//            }
//        }
//    )
//}