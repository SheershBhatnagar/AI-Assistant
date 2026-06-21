package dev.sheershbhatnagar.ai_assistant.features.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onNavigateToChat: (String) -> Unit
) {
    val conversations by viewModel.conversations.collectAsState()
    val historyState by viewModel.historyState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadConversations()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chat History",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                historyState is HistoryState.Loading && conversations.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                historyState is HistoryState.Error && conversations.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = (historyState as HistoryState.Error).message,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                            Button(onClick = { viewModel.loadConversations() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                conversations.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No past conversations found.",
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 15.sp
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item { Spacer(Modifier.height(8.dp)) }
                        
                        items(conversations) { conversation ->
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(16.dp),
                                tonalElevation = 1.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToChat(conversation.id) }
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = conversation.title ?: "Untitled Chat",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = "Started on: ${conversation.createdAt.take(10)}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }
                        
                        item { Spacer(Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}
