package dev.sheershbhatnagar.ai_assistant.features.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversationId: String,
    viewModel: ChatViewModel,
    onNavigateBack: () -> Unit
) {
    val messages by viewModel.messages.collectAsState()
    val chatState by viewModel.chatState.collectAsState()
    var inputMessage by remember { mutableStateOf("") }
    
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(conversationId) {
        viewModel.loadMessages(conversationId)
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AI Chat Session",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(Modifier.height(8.dp)) }
                
                items(messages) { msg ->
                    val isUser = msg.senderType == "user"
                    val alignment = if (isUser) Alignment.End else Alignment.Start
                    val bubbleColor = if (isUser) Color(0xFF6366F1) else MaterialTheme.colorScheme.surface
                    val textColor = if (isUser) Color.White else MaterialTheme.colorScheme.onBackground
                    val shape = if (isUser) {
                        RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp)
                    } else {
                        RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp)
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = alignment
                    ) {
                        Surface(
                            color = bubbleColor,
                            shape = shape,
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Text(
                                text = msg.content,
                                color = textColor,
                                fontSize = 15.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                            )
                        }
                    }
                }

                if (chatState is ChatState.Sending) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = "AI is thinking...",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
                
                item { Spacer(Modifier.height(16.dp)) }
            }

            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .navigationBarsPadding()
                        .imePadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputMessage,
                        onValueChange = { inputMessage = it },
                        placeholder = { Text("Type a message...", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = MaterialTheme.colorScheme.background,
                            unfocusedContainerColor = MaterialTheme.colorScheme.background
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(Modifier.width(12.dp))

                    FloatingActionButton(
                        onClick = {
                            if (inputMessage.isNotBlank()) {
                                viewModel.sendMessage(conversationId, inputMessage)
                                inputMessage = ""
                            }
                        },
                        containerColor = Color(0xFF6366F1),
                        contentColor = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send")
                    }
                }
            }
        }
    }
}
