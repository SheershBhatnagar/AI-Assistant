/*
 * ॐ नमः शिवाय
 * By: Sheersh Bhatnagar
 * Created On: 01/05/26 23:35
*/

package dev.sheershbhatnagar.ai_assistant.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = {
            Text("AI Assistant")
        },
        navigationIcon = {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        Icons.Default.ArrowBackIosNew,
                        contentDescription = null
                    )
                }
            }
        }
    )
}
