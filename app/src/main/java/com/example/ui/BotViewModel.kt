package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.BusinessProfile
import com.example.data.ChatMessage
import com.example.data.Conversation
import com.example.data.CustomerLead
import com.example.data.FaqItem
import com.example.data.ProductItem
import com.example.repository.BotRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class NavigationTab {
    CHAT_SIMULATOR,
    WPA_INTEGRATION,
    KNOWLEDGE_BASE,
    ANALYTICS_LEADS
}

class BotViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BotRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = BotRepository(database)
        viewModelScope.launch {
            repository.ensureInitialDataLoaded()
        }
    }

    val selectedTab = MutableStateFlow(NavigationTab.CHAT_SIMULATOR)
    val businessProfile: StateFlow<BusinessProfile?> = repository.businessProfileFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val faqs: StateFlow<List<FaqItem>> = repository.faqsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val products: StateFlow<List<ProductItem>> = repository.productsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val conversations: StateFlow<List<Conversation>> = repository.conversationsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val leads: StateFlow<List<CustomerLead>> = repository.leadsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val autoReplyCount: StateFlow<Int> = repository.autoReplyCountFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val selectedConversationId = MutableStateFlow<Long?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeChatMessages: StateFlow<List<ChatMessage>> = selectedConversationId
        .flatMapLatest { convId ->
            if (convId != null) {
                repository.getMessagesForConversation(convId)
            } else {
                flowOf(emptyList())
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isGeneratingReply = MutableStateFlow(false)
    val isGeneratingReply: StateFlow<Boolean> = _isGeneratingReply.asStateFlow()

    private val _userDraftMessage = MutableStateFlow("")
    val userDraftMessage: StateFlow<String> = _userDraftMessage.asStateFlow()

    fun selectTab(tab: NavigationTab) {
        selectedTab.value = tab
    }

    fun selectConversation(convId: Long) {
        selectedConversationId.value = convId
    }

    fun updateDraftMessage(text: String) {
        _userDraftMessage.value = text
    }

    fun sendCustomerSimulatedMessage(text: String) {
        val convId = selectedConversationId.value ?: return
        if (text.isBlank()) return

        _userDraftMessage.value = ""
        viewModelScope.launch {
            _isGeneratingReply.value = true
            try {
                repository.processCustomerMessageAndAutoReply(convId, text)
            } finally {
                _isGeneratingReply.value = false
            }
        }
    }

    fun sendHumanAgentMessage(text: String) {
        val convId = selectedConversationId.value ?: return
        if (text.isBlank()) return

        _userDraftMessage.value = ""
        viewModelScope.launch {
            repository.sendAgentReply(convId, text)
        }
    }

    fun createNewSimulatedChat(customerName: String, phone: String, platform: String) {
        viewModelScope.launch {
            val newId = repository.createNewConversation(
                customerName = customerName.ifBlank { "Cliente WhatsApp" },
                phone = phone.ifBlank { "+39 333 000000" },
                platform = platform
            )
            selectedConversationId.value = newId
        }
    }

    fun toggleConversationAutoReply(convId: Long, enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleConversationAutoReply(convId, enabled)
        }
    }

    fun deleteConversation(convId: Long) {
        viewModelScope.launch {
            if (selectedConversationId.value == convId) {
                selectedConversationId.value = null
            }
            repository.deleteConversation(convId)
        }
    }

    fun saveBusinessProfile(profile: BusinessProfile) {
        viewModelScope.launch {
            repository.saveProfile(profile)
        }
    }

    fun addFaq(faq: FaqItem) {
        viewModelScope.launch {
            repository.addFaq(faq)
        }
    }

    fun updateFaq(faq: FaqItem) {
        viewModelScope.launch {
            repository.updateFaq(faq)
        }
    }

    fun deleteFaq(faq: FaqItem) {
        viewModelScope.launch {
            repository.deleteFaq(faq)
        }
    }

    fun addProduct(product: ProductItem) {
        viewModelScope.launch {
            repository.addProduct(product)
        }
    }

    fun updateProduct(product: ProductItem) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    fun deleteProduct(product: ProductItem) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    fun addCustomerLead(lead: CustomerLead) {
        viewModelScope.launch {
            repository.addLead(lead)
        }
    }

    fun deleteCustomerLead(leadId: Long) {
        viewModelScope.launch {
            repository.deleteLead(leadId)
        }
    }
}
