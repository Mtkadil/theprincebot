package com.example.repository

import com.example.data.AppDatabase
import com.example.data.BusinessProfile
import com.example.data.ChatMessage
import com.example.data.Conversation
import com.example.data.CustomerLead
import com.example.data.FaqItem
import com.example.data.ProductItem
import com.example.network.GeminiApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class BotRepository(private val db: AppDatabase) {

    val businessProfileFlow: Flow<BusinessProfile?> = db.businessProfileDao().getProfileFlow()
    val faqsFlow: Flow<List<FaqItem>> = db.faqDao().getAllFaqs()
    val productsFlow: Flow<List<ProductItem>> = db.productDao().getAllProducts()
    val conversationsFlow: Flow<List<Conversation>> = db.conversationDao().getAllConversations()
    val leadsFlow: Flow<List<CustomerLead>> = db.customerLeadDao().getAllLeads()
    val autoReplyCountFlow: Flow<Int> = db.chatMessageDao().getAutoReplyCountFlow()

    fun getMessagesForConversation(conversationId: Long): Flow<List<ChatMessage>> {
        return db.chatMessageDao().getMessagesForConversation(conversationId)
    }

    suspend fun ensureInitialDataLoaded() = withContext(Dispatchers.IO) {
        val existingProfile = db.businessProfileDao().getProfile()
        if (existingProfile == null) {
            db.businessProfileDao().insertOrUpdateProfile(BusinessProfile())
        }

        val existingFaqs = db.faqDao().getActiveFaqs()
        if (existingFaqs.isEmpty()) {
            db.faqDao().insertFaq(
                FaqItem(
                    question = "Quali sono i vostri orari di apertura?",
                    answer = "Lunedì: 12:00-19:00, Martedì-Sabato: 10:00-20:00, Domenica: 10:00-16:00.",
                    category = "Orari",
                    keywords = "orario, orari, aperto, chiusura, apertura"
                )
            )
            db.faqDao().insertFaq(
                FaqItem(
                    question = "Come posso prenotare un taglio?",
                    answer = "Puoi prenotare direttamente rispondendo a questo messaggio con il servizio desiderato, data e ora, oppure chiamandoci al +39 375 6866511.",
                    category = "Prenotazioni",
                    keywords = "prenotare, prenotazione, appuntamento, taglio, barba"
                )
            )
            db.faqDao().insertFaq(
                FaqItem(
                    question = "Siete esperti in sfumature?",
                    answer = "Assolutamente sì. Siamo specializzati in sfumature di precisione (fade) e cura dei dettagli in ogni stile.",
                    category = "Servizi",
                    keywords = "sfumatura, fade, precisione, stile"
                )
            )
            db.faqDao().insertFaq(
                FaqItem(
                    question = "Dove si trova il negozio?",
                    answer = "Ci trovi in Via della Sfumatua 42, Roma. Ti aspettiamo!",
                    category = "Posizione",
                    keywords = "dove, indirizzo, mappa, roma"
                )
            )
        }

        val existingProducts = db.productDao().getAvailableProducts()
        if (existingProducts.isEmpty()) {
            db.productDao().insertProduct(
                ProductItem(
                    name = "Taglio Capelli & Sfumatura",
                    price = "€25,00",
                    description = "Taglio personalizzato con sfumatura di precisione a macchinetta e rifinitura a forbice.",
                    category = "Hair Styling"
                )
            )
            db.productDao().insertProduct(
                ProductItem(
                    name = "Modellatura Barba con Panno Caldo",
                    price = "€15,00",
                    description = "Cura e modellatura della barba con trattamento tradizionale a panno caldo e oli essenziali.",
                    category = "Barba"
                )
            )
            db.productDao().insertProduct(
                ProductItem(
                    name = "Servizio Combo Prince",
                    price = "€35,00",
                    description = "Taglio capelli completo e cura della barba con trattamento relax.",
                    category = "Combo"
                )
            )
        }

        val conversations = db.conversationDao().getAllConversations().first()
        if (conversations.isEmpty()) {
            val defaultConvId = db.conversationDao().insertConversation(
                Conversation(
                    customerName = "Alessandro (WhatsApp)",
                    customerPhone = "+39 333 9876543",
                    platform = "WhatsApp",
                    lastMessage = "Vorrei sapere se avete posto per un taglio domani",
                    lastUpdated = System.currentTimeMillis() - 3600000,
                    isAutoReplyOn = true
                )
            )

            db.chatMessageDao().insertMessage(
                ChatMessage(
                    conversationId = defaultConvId,
                    sender = "Customer",
                    text = "Ciao! Vorrei sapere se avete posto per un taglio e barba domani mattina verso le 11:00.",
                    timestamp = System.currentTimeMillis() - 3600000,
                    isAutoReplied = false
                )
            )

            db.chatMessageDao().insertMessage(
                ChatMessage(
                    conversationId = defaultConvId,
                    sender = "Bot",
                    text = "Ciao Alessandro! ✂️ Per domani alle 11:00 abbiamo un posto libero per il Servizio Combo Prince. Vuoi che te lo blocchi a tuo nome?",
                    timestamp = System.currentTimeMillis() - 3550000,
                    isAutoReplied = true,
                    aiConfidence = 0.98f,
                    detectedIntent = "Prenotazione"
                )
            )

            val conv2Id = db.conversationDao().insertConversation(
                Conversation(
                    customerName = "Giulia Bianchi (Web Widget WPA)",
                    customerPhone = "+39 349 1122334",
                    platform = "WPA / Web",
                    lastMessage = "Quali sono i vostri orari di apertura?",
                    lastUpdated = System.currentTimeMillis() - 1800000,
                    isAutoReplyOn = true
                )
            )

            db.chatMessageDao().insertMessage(
                ChatMessage(
                    conversationId = conv2Id,
                    sender = "Customer",
                    text = "Ciao, fino a che ora siete aperti il sabato?",
                    timestamp = System.currentTimeMillis() - 1800000,
                    isAutoReplied = false
                )
            )

            db.chatMessageDao().insertMessage(
                ChatMessage(
                    conversationId = conv2Id,
                    sender = "Bot",
                    text = "Ciao Giulia! 🕒 Il sabato siamo aperti dalle 12:00 alle 15:00 e la sera dalle 19:00 alle 23:30. Ti aspettiamo!",
                    timestamp = System.currentTimeMillis() - 1750000,
                    isAutoReplied = true,
                    aiConfidence = 0.96f,
                    detectedIntent = "Orari"
                )
            )
        }
    }

    suspend fun saveProfile(profile: BusinessProfile) = withContext(Dispatchers.IO) {
        db.businessProfileDao().insertOrUpdateProfile(profile)
    }

    suspend fun addFaq(faq: FaqItem) = withContext(Dispatchers.IO) {
        db.faqDao().insertFaq(faq)
    }

    suspend fun updateFaq(faq: FaqItem) = withContext(Dispatchers.IO) {
        db.faqDao().updateFaq(faq)
    }

    suspend fun deleteFaq(faq: FaqItem) = withContext(Dispatchers.IO) {
        db.faqDao().deleteFaq(faq)
    }

    suspend fun addProduct(product: ProductItem) = withContext(Dispatchers.IO) {
        db.productDao().insertProduct(product)
    }

    suspend fun updateProduct(product: ProductItem) = withContext(Dispatchers.IO) {
        db.productDao().updateProduct(product)
    }

    suspend fun deleteProduct(product: ProductItem) = withContext(Dispatchers.IO) {
        db.productDao().deleteProduct(product)
    }

    suspend fun createNewConversation(customerName: String, phone: String, platform: String): Long = withContext(Dispatchers.IO) {
        val conv = Conversation(
            customerName = customerName,
            customerPhone = phone,
            platform = platform,
            lastMessage = "Nuova conversazione avviata",
            lastUpdated = System.currentTimeMillis()
        )
        db.conversationDao().insertConversation(conv)
    }

    suspend fun deleteConversation(conversationId: Long) = withContext(Dispatchers.IO) {
        db.conversationDao().deleteConversationById(conversationId)
    }

    suspend fun processCustomerMessageAndAutoReply(
        conversationId: Long,
        messageText: String
    ): ChatMessage = withContext(Dispatchers.IO) {
        // 1. Insert customer message
        val customerMsg = ChatMessage(
            conversationId = conversationId,
            sender = "Customer",
            text = messageText,
            timestamp = System.currentTimeMillis()
        )
        db.chatMessageDao().insertMessage(customerMsg)

        // Update conversation summary
        val conv = db.conversationDao().getConversationById(conversationId)
        if (conv != null) {
            db.conversationDao().updateConversation(
                conv.copy(
                    lastMessage = messageText,
                    lastUpdated = System.currentTimeMillis()
                )
            )
        }

        // 2. Fetch business context
        val profile = db.businessProfileDao().getProfile() ?: BusinessProfile()
        val faqs = db.faqDao().getActiveFaqs()
        val products = db.productDao().getAvailableProducts()
        val pastMessages = db.chatMessageDao().getMessagesList(conversationId)
        val historyPairs = pastMessages.map { it.sender to it.text }

        // Check if Bot is enabled globally and for this conversation
        val isBotActive = profile.autoReplyEnabled && (conv?.isAutoReplyOn != false)

        if (!isBotActive) {
            val inactiveReply = ChatMessage(
                conversationId = conversationId,
                sender = "System",
                text = "Bot in pausa per questa conversazione. Risposta manuale richiesta.",
                timestamp = System.currentTimeMillis(),
                isAutoReplied = false
            )
            return@withContext inactiveReply
        }

        // 3. Call Gemini AI or Fallback engine
        val aiResult = GeminiApiClient.generateCustomerReply(
            customerMessage = messageText,
            profile = profile,
            faqs = faqs,
            products = products,
            conversationHistory = historyPairs
        )

        // 4. Record Bot Reply
        val botMsg = ChatMessage(
            conversationId = conversationId,
            sender = "Bot",
            text = aiResult.replyText,
            timestamp = System.currentTimeMillis() + 500,
            isAutoReplied = true,
            aiConfidence = aiResult.confidence,
            detectedIntent = aiResult.detectedIntent
        )
        db.chatMessageDao().insertMessage(botMsg)

        // Update conversation last message
        if (conv != null) {
            db.conversationDao().updateConversation(
                conv.copy(
                    lastMessage = "🤖 ${aiResult.replyText.take(50)}...",
                    lastUpdated = System.currentTimeMillis() + 500
                )
            )
        }

        // 5. Save lead if extracted
        if (!aiResult.extractedLeadName.isNullOrBlank() || !aiResult.extractedLeadContact.isNullOrBlank()) {
            db.customerLeadDao().insertLead(
                CustomerLead(
                    name = aiResult.extractedLeadName ?: (conv?.customerName ?: "Cliente"),
                    contact = aiResult.extractedLeadContact ?: (conv?.customerPhone ?: "WhatsApp"),
                    inquiryType = aiResult.detectedIntent,
                    notes = "Estratto da messaggio: '$messageText'"
                )
            )
        }

        return@withContext botMsg
    }

    suspend fun sendAgentReply(conversationId: Long, text: String) = withContext(Dispatchers.IO) {
        val agentMsg = ChatMessage(
            conversationId = conversationId,
            sender = "Agent",
            text = text,
            timestamp = System.currentTimeMillis(),
            isAutoReplied = false
        )
        db.chatMessageDao().insertMessage(agentMsg)

        val conv = db.conversationDao().getConversationById(conversationId)
        if (conv != null) {
            db.conversationDao().updateConversation(
                conv.copy(
                    lastMessage = "👤 $text",
                    lastUpdated = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun toggleConversationAutoReply(conversationId: Long, enabled: Boolean) = withContext(Dispatchers.IO) {
        val conv = db.conversationDao().getConversationById(conversationId)
        if (conv != null) {
            db.conversationDao().updateConversation(conv.copy(isAutoReplyOn = enabled))
        }
    }

    suspend fun addLead(lead: CustomerLead) = withContext(Dispatchers.IO) {
        db.customerLeadDao().insertLead(lead)
    }

    suspend fun deleteLead(leadId: Long) = withContext(Dispatchers.IO) {
        db.customerLeadDao().deleteLeadById(leadId)
    }
}
