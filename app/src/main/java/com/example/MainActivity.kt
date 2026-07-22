package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.BotViewModel
import com.example.ui.NavigationTab
import com.example.ui.screens.AnalyticsLeadsScreen
import com.example.ui.screens.ChatSimulatorScreen
import com.example.ui.screens.KnowledgeBaseScreen
import com.example.ui.screens.WpaIntegrationScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: BotViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
                val profile by viewModel.businessProfile.collectAsStateWithLifecycle()
                val faqs by viewModel.faqs.collectAsStateWithLifecycle()
                val products by viewModel.products.collectAsStateWithLifecycle()
                val conversations by viewModel.conversations.collectAsStateWithLifecycle()
                val activeMessages by viewModel.activeChatMessages.collectAsStateWithLifecycle()
                val selectedConvId by viewModel.selectedConversationId.collectAsStateWithLifecycle()
                val isGenerating by viewModel.isGeneratingReply.collectAsStateWithLifecycle()
                val draftMessage by viewModel.userDraftMessage.collectAsStateWithLifecycle()
                val autoReplyCount by viewModel.autoReplyCount.collectAsStateWithLifecycle()
                val leads by viewModel.leads.collectAsStateWithLifecycle()

                val isBotGlobalActive = profile?.autoReplyEnabled != false

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF25D366)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.SmartToy,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Bot Risposta Automatica",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            },
                            actions = {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isBotGlobalActive) Color(0xFF25D366).copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.2f),
                                    modifier = Modifier.padding(end = 12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(if (isBotGlobalActive) Color(0xFF25D366) else Color.Gray)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isBotGlobalActive) "ONLINE" else "PAUSA",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isBotGlobalActive) Color(0xFF128C7E) else Color.DarkGray
                                        )
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp
                        ) {
                            NavigationBarItem(
                                selected = selectedTab == NavigationTab.CHAT_SIMULATOR,
                                onClick = { viewModel.selectTab(NavigationTab.CHAT_SIMULATOR) },
                                icon = { Icon(Icons.Default.SmartToy, contentDescription = "Chat Bot") },
                                label = { Text("Chat Bot", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFF25D366),
                                    indicatorColor = Color(0xFF25D366).copy(alpha = 0.2f)
                                ),
                                modifier = Modifier.testTag("nav_tab_simulator")
                            )
                            NavigationBarItem(
                                selected = selectedTab == NavigationTab.WPA_INTEGRATION,
                                onClick = { viewModel.selectTab(NavigationTab.WPA_INTEGRATION) },
                                icon = { Icon(Icons.Default.Smartphone, contentDescription = "WPA / WhatsApp") },
                                label = { Text("WPA & WhatsApp", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFF25D366),
                                    indicatorColor = Color(0xFF25D366).copy(alpha = 0.2f)
                                ),
                                modifier = Modifier.testTag("nav_tab_wpa")
                            )
                            NavigationBarItem(
                                selected = selectedTab == NavigationTab.KNOWLEDGE_BASE,
                                onClick = { viewModel.selectTab(NavigationTab.KNOWLEDGE_BASE) },
                                icon = { Icon(Icons.Default.MenuBook, contentDescription = "FAQ & Info") },
                                label = { Text("FAQ & Info", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFF25D366),
                                    indicatorColor = Color(0xFF25D366).copy(alpha = 0.2f)
                                ),
                                modifier = Modifier.testTag("nav_tab_kb")
                            )
                            NavigationBarItem(
                                selected = selectedTab == NavigationTab.ANALYTICS_LEADS,
                                onClick = { viewModel.selectTab(NavigationTab.ANALYTICS_LEADS) },
                                icon = { Icon(Icons.Default.Analytics, contentDescription = "Lead & Report") },
                                label = { Text("Lead & Stats", fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFF25D366),
                                    indicatorColor = Color(0xFF25D366).copy(alpha = 0.2f)
                                ),
                                modifier = Modifier.testTag("nav_tab_analytics")
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (selectedTab) {
                            NavigationTab.CHAT_SIMULATOR -> {
                                ChatSimulatorScreen(
                                    viewModel = viewModel,
                                    conversations = conversations,
                                    selectedConvId = selectedConvId,
                                    messages = activeMessages,
                                    isGenerating = isGenerating,
                                    draftMessage = draftMessage
                                )
                            }
                            NavigationTab.WPA_INTEGRATION -> {
                                WpaIntegrationScreen(
                                    viewModel = viewModel,
                                    profile = profile
                                )
                            }
                            NavigationTab.KNOWLEDGE_BASE -> {
                                KnowledgeBaseScreen(
                                    viewModel = viewModel,
                                    profile = profile,
                                    faqs = faqs,
                                    products = products
                                )
                            }
                            NavigationTab.ANALYTICS_LEADS -> {
                                AnalyticsLeadsScreen(
                                    viewModel = viewModel,
                                    autoReplyCount = autoReplyCount,
                                    activeConvCount = conversations.size,
                                    leads = leads
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
