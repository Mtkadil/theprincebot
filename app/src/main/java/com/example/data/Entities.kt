package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "business_profile")
data class BusinessProfile(
    @PrimaryKey val id: Int = 1,
    val businessName: String = "Ristorante Bella Vista",
    val description: String = "Ristorante italiano con cucina tradizionale, pizza a legna e specialità di pesce.",
    val address: String = "Via Roma 123, Milano",
    val openingHours: String = "Lunedì - Sabato: 12:00 - 15:00, 19:00 - 23:30. Domenica chiuso.",
    val phone: String = "+39 02 1234567",
    val whatsappNumber: String = "+39 340 1234567",
    val website: String = "https://bellavista-milano.it",
    val botName: String = "Assistente Virtuale Bella Vista",
    val tone: String = "Professionale ma cordiale", // Friendly, Professional, Formal
    val language: String = "Italiano",
    val autoReplyEnabled: Boolean = true,
    val autoReplyDelaySeconds: Int = 2,
    val requireManualApproval: Boolean = false,
    val defaultGreeting: String = "Ciao! Benvenuto da Bella Vista. Sono l'assistente virtuale. Come posso aiutarti oggi?",
    val fallbackResponse: String = "Grazie per il messaggio! Un nostro operatore ti risponderà al più presto."
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
