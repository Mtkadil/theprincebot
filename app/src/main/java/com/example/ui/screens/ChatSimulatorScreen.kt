package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChatMessage
import com.example.data.Conversation
import com.example.ui.BotViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatSimulatorScreen(
    viewModel: BotViewModel,
    conversations: List<Conversation>,
    selectedConvId: Long?,
    messages: List<ChatMessage>,
    isGenerating: Boolean,
    draftMessage: String,
    modifier: Modifier = Modifier
) {
    var showNewChatDialog by remember { mutableStateOf(false) }

    // Ensure first conversation is selected by default
    LaunchedEffect(conversations) {
        if (selectedConvId == null && conversations.isNotEmpty()) {
            viewModel.selectConversation(conversations.first().id)
        }
    }

    val activeConv = conversations.firstOrNull { it.id == selectedConvId }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Conversation Horizontal Selector
        ConversationSelectorHeader(
            conversations = conversations,
            selectedConvId = selectedConvId,
            onSelect = { viewModel.selectConversation(it) },
            onAddClick = { showNewChatDialog = true }
        )

        if (activeConv == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "No Chat Selected",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Nessuna conversazione selezionata",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { showNewChatDialog = true },
                        modifier = Modifier.testTag("create_first_chat_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Simula Nuova Chat Cliente")
                    }
                }
            }
        } else {
            // Chat Header bar with Bot Auto-Reply toggle
            ChatDetailHeader(
                conversation = activeConv,
                onToggleBot = { enabled -> viewModel.toggleConversationAutoReply(activeConv.id, enabled) },
                onDelete = { viewModel.deleteConversation(activeConv.id) }
            )

            // Chat Messages Scrollable Area
            val listState = rememberLazyListState()
            LaunchedEffect(messages.size, isGenerating) {
                if (messages.isNotEmpty()) {
                    listState.animateScrollToItem(messages.size - 1)
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        ChatMessageBubble(message = msg)
                    }

                    if (isGenerating) {
                        item {
                            AiThinkingBubble()
                        }
                    }
                }
            }

            // Quick Test Suggestions
            QuickPromptChipsRow(
                onSelectPrompt = { prompt -> viewModel.updateDraftMessage(prompt) }
            )

            // Input Bar (Simulate Customer or Agent Reply)
            ChatInputBar(
                draftText = draftMessage,
                onTextChange = { viewModel.updateDraftMessage(it) },
                onSendAsCustomer = { viewModel.sendCustomerSimulatedMessage(draftMessage) },
                onSendAsAgent = { viewModel.sendHumanAgentMessage(draftMessage) },
                isGenerating = isGenerating
            )
        }
    }

    if (showNewChatDialog) {
        NewChatDialog(
            onDismiss = { showNewChatDialog = false },
            onCreate = { name, phone, platform ->
                viewModel.createNewSimulatedChat(name, phone, platform)
                showNewChatDialog = false
            }
        )
    }
}

@Composable
fun ConversationSelectorHeader(
    conversations: List<Conversation>,
    selectedConvId: Long?,
    onSelect: (Long) -> Unit,
    onAddClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(conversations) { conv ->
                    val isSelected = conv.id == selectedConvId
                    ElevatedCard(
                        onClick = { onSelect(conv.id) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.testTag("conv_card_${conv.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (conv.platform.contains("WhatsApp")) Color(0xFF25D366) else MaterialTheme.colorScheme.primary
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = conv.customerName.take(1).uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = conv.customerName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = conv.platform,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onAddClick,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .testTag("add_conv_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuova Chat", tint = Color.White)
            }
        }
    }
}

@Composable
fun ChatDetailHeader(
    conversation: Conversation,
    onToggleBot: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = conversation.customerName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF25D366).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = conversation.platform,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF128C7E),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = conversation.customerPhone,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Bot Active Switch
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (conversation.isAutoReplyOn) "Bot Attivo" else "Bot Pausa",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (conversation.isAutoReplyOn) Color(0xFF25D366) else MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.width(6.dp))
                Switch(
                    checked = conversation.isAutoReplyOn,
                    onCheckedChange = onToggleBot,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF25D366)
                    ),
                    modifier = Modifier.testTag("bot_toggle_switch")
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Elimina Chat",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(message: ChatMessage) {
    val isCustomer = message.sender == "Customer"
    val isBot = message.sender == "Bot"

    val align = if (isCustomer) Alignment.Start else Alignment.End
    val bubbleColor = when {
        isCustomer -> MaterialTheme.colorScheme.surfaceVariant
        isBot -> Color(0xFFDCF8C6) // WhatsApp light green style
        else -> MaterialTheme.colorScheme.primaryContainer
    }
    val textColor = when {
        isCustomer -> MaterialTheme.colorScheme.onSurfaceVariant
        isBot -> Color(0xFF075E54)
        else -> MaterialTheme.colorScheme.onPrimaryContainer
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = align
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isCustomer) 4.dp else 16.dp,
                bottomEnd = if (isCustomer) 16.dp else 4.dp
            ),
            color = bubbleColor,
            tonalElevation = 1.dp,
            modifier = Modifier.widthIn(max = 290.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Sender Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when {
                                isCustomer -> Icons.Default.Person
                                isBot -> Icons.Default.SmartToy
                                else -> Icons.Default.SupportAgent
                            },
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = textColor.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when {
                                isCustomer -> "Cliente"
                                isBot -> "Bot IA Gemini"
                                else -> "Operatore Umano"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = textColor.copy(alpha = 0.8f)
                        )
                    }

                    if (isBot && message.detectedIntent.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF128C7E).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = message.detectedIntent,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                color = Color(0xFF128C7E),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Message Text
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Timestamp & Confidence
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isBot && message.isAutoReplied) {
                        Text(
                            text = "Confidenza: ${(message.aiConfidence * 100).toInt()}% • ",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = textColor.copy(alpha = 0.6f)
                        )
                    }
                    Text(
                        text = formatTime(message.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = textColor.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun AiThinkingBubble() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "🤖 Gemini IA sta elaborando la risposta...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}

@Composable
fun QuickPromptChipsRow(onSelectPrompt: (String) -> Unit) {
    val prompts = listOf(
        "Quali sono gli orari di apertura?",
        "Vorrei prenotare un tavolo per stasera",
        "Quanto costa il menu degustazione?",
        "Dove vi trovate esattamente?",
        "Accettate carte di credito?"
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(prompts) { prompt ->
            FilterChip(
                selected = false,
                onClick = { onSelectPrompt(prompt) },
                label = { Text(prompt, fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}

@Composable
fun ChatInputBar(
    draftText: String,
    onTextChange: (String) -> Unit,
    onSendAsCustomer: () -> Unit,
    onSendAsAgent: () -> Unit,
    isGenerating: Boolean
) {
    Surface(
        tonalElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            OutlinedTextField(
                value = draftText,
                onValueChange = onTextChange,
                placeholder = { Text("Scrivi un messaggio di prova per il bot...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("chat_input_textfield"),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                ),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Send as Customer (Triggers Bot Auto-Reply!)
                Button(
                    onClick = onSendAsCustomer,
                    enabled = draftText.isNotBlank() && !isGenerating,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("send_as_customer_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Invia come Cliente", color = Color.White, fontSize = 12.sp)
                }

                // Send as Human Agent
                OutlinedButton(
                    onClick = onSendAsAgent,
                    enabled = draftText.isNotBlank() && !isGenerating,
                    modifier = Modifier.testTag("send_as_agent_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Operatore", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun NewChatDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var platform by remember { mutableStateOf("WhatsApp") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Simula Nuova Chat Cliente") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome Cliente") },
                    placeholder = { Text("Es. Mario Rossi") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Telefono / WhatsApp") },
                    placeholder = { Text("Es. +39 340 1234567") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = platform == "WhatsApp",
                        onClick = { platform = "WhatsApp" },
                        label = { Text("WhatsApp") }
                    )
                    FilterChip(
                        selected = platform == "WPA / Web",
                        onClick = { platform = "WPA / Web" },
                        label = { Text("WPA / Web") }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate(name, phone, platform) },
                modifier = Modifier.testTag("confirm_create_chat_button")
            ) {
                Text("Crea Chat")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla")
            }
        }
    )
}

fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.ITALY)
    return sdf.format(Date(timestamp))
}
