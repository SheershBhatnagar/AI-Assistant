package dev.sheershbhatnagar.ai_assistant.features.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToChat: (String) -> Unit
) {
    val userName by viewModel.userName.collectAsState()
    val defaultModelName by viewModel.defaultModelName.collectAsState()
    val homeState by viewModel.homeState.collectAsState()
    var prompt by remember { mutableStateOf("") }

    val suggestionChips = listOf(
        "Write a Python script to sort a list",
        "Explain quantum physics like I'm 5",
        "Suggest healthy dinner options for tonight",
        "Help me draft a polite email requesting leave"
    )

    LaunchedEffect(Unit) {
        viewModel.loadDefaultModelName()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(Modifier.height(16.dp))

            Text(
                text = "Hi, $userName!",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "How can I help you today?",
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(Modifier.height(28.dp))

            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.wrapContentWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFF10B981), RoundedCornerShape(4.dp))
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Active Model: $defaultModelName",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "SUGGESTIONS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(suggestionChips) { chipText ->
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.clickable { prompt = chipText }
                    ) {
                        Text(
                            text = if (chipText.length > 25) chipText.take(22) + "..." else chipText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            if (homeState is HomeState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    placeholder = { Text("Ask anything...", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)) },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (prompt.isNotBlank()) {
                                    val currentPrompt = prompt
                                    prompt = ""
                                    viewModel.startConversationFromPrompt(currentPrompt, onNavigateToChat)
                                }
                            },
                            enabled = prompt.isNotBlank(),
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = if (prompt.isNotBlank()) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        ) {
                            Icon(
                                Icons.Default.Send,
                                contentDescription = "Send"
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            if (homeState is HomeState.Error) {
                Text(
                    text = (homeState as HomeState.Error).message,
                    color = Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
