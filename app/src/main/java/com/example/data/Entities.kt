package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "business_profile")
data class BusinessProfile(
    @PrimaryKey val id: Int = 1,
    val businessName: String = "Barbershop The Prince",
    val description: String = "Hair stylist. Il migliore in tutti gli stili. Precisione nelle sfumature e cura dei dettagli. Esperienza in Italia e all'estero. Stile unico.",
    val address: String = "Via della Sfumatua 42, Roma",
    val openingHours: String = "Lunedì: 12:00-19:00, Martedì-Sabato: 10:00-20:00, Domenica: 10:00-16:00",
    val phone: String = "+39 375 6866511",
    val whatsappNumber: String = "+39 375 6866511",
    val website: String = "https://barbershop-theprince.it",
    val botName: String = "Prince Bot",
    val tone: String = "Professionale ma cordiale", // Friendly, Professional, Formal
    val language: String = "Italiano",
    val autoReplyEnabled: Boolean = true,
    val autoReplyDelaySeconds: Int = 2,
    val requireManualApproval: Boolean = false,
    val defaultGreeting: String = "Benvenuto da Barbershop The Prince! ✂️ Sono il tuo assistente virtuale. Come posso aiutarti oggi? Vuoi prenotare un taglio o una sfumatura?",
    val fallbackResponse: String = "Grazie per il messaggio! Un nostro barbiere ti risponderà appena possibile."
)

@Entity(tableName = "faqs")
data class FaqItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val question: String,
    val answer: String,
    val category: String = "Generale",
    val isAutoReplyActive: Boolean = true,
    val keywords: String = "" // Comma separated tags
)

@Entity(tableName = "products")
data class ProductItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val price: String,
    val description: String,
    val category: String = "Menu",
    val isAvailable: Boolean = true
)

@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerName: String,
    val customerPhone: String,
    val platform: String = "WhatsApp", // WhatsApp, Web Chat, WPA
    val lastMessage: String = "",
    val lastUpdated: Long = System.currentTimeMillis(),
    val isAutoReplyOn: Boolean = true,
    val status: String = "Attivo", // Attivo, In Sospeso, Chiuso
    val unreadCount: Int = 0
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: Long,
    val sender: String, // "Customer", "Bot", "Agent"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isAutoReplied: Boolean = false,
    val aiConfidence: Float = 0.95f,
    val detectedIntent: String = "Richiesta Informazioni"
)

@Entity(tableName = "customer_leads")
data class CustomerLead(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val contact: String,
    val inquiryType: String,
    val notes: String = "",
    val dateCaptured: Long = System.currentTimeMillis()
)
