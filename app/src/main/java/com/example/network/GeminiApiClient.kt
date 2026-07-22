package com.example.network

import android.util.Log
import com.example.BuildConfig
import com.example.data.BusinessProfile
import com.example.data.FaqItem
import com.example.data.ProductItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AiBotReplyResult(
    val replyText: String,
    val detectedIntent: String,
    val confidence: Float,
    val shouldEscalateToHuman: Boolean,
    val extractedLeadName: String? = null,
    val extractedLeadContact: String? = null
)

object GeminiApiClient {
    private const val TAG = "GeminiApiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generateCustomerReply(
        customerMessage: String,
        profile: BusinessProfile,
        faqs: List<FaqItem>,
        products: List<ProductItem>,
        conversationHistory: List<Pair<String, String>> = emptyList() // sender to text
    ): AiBotReplyResult = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "Gemini API key missing or default, fallback rule matching will be used.")
            return@withContext generateFallbackResponse(customerMessage, profile, faqs, products)
        }

        try {
            val systemInstructionText = buildSystemInstruction(profile, faqs, products)
            val jsonPayload = buildJsonRequestBody(customerMessage, systemInstructionText, conversationHistory)

            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(jsonPayload.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errBody = response.body?.string()
                    Log.e(TAG, "API Error: ${response.code} - $errBody")
                    return@withContext generateFallbackResponse(customerMessage, profile, faqs, products)
                }

                val responseBodyStr = response.body?.string() ?: ""
                val responseJson = JSONObject(responseBodyStr)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val contentObj = firstCandidate.optJSONObject("content")
                    val parts = contentObj?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val rawText = parts.getJSONObject(0).optString("text", "")
                        return@withContext parseAiJsonResponse(rawText, profile)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during Gemini API call", e)
        }

        return@withContext generateFallbackResponse(customerMessage, profile, faqs, products)
    }

    private fun buildSystemInstruction(
        profile: BusinessProfile,
        faqs: List<FaqItem>,
        products: List<ProductItem>
    ): String {
        val sb = StringBuilder()
        sb.append("Sei l'assistente virtuale automatico '${profile.botName}' per l'azienda '${profile.businessName}'.\n")
        sb.append("Descrizione Azienda: ${profile.description}\n")
        sb.append("Indirizzo: ${profile.address}\n")
        sb.append("Orari di apertura: ${profile.openingHours}\n")
        sb.append("Telefono/WhatsApp: ${profile.phone} / ${profile.whatsappNumber}\n")
        sb.append("Sito web: ${profile.website}\n")
        sb.append("Tono di voce: ${profile.tone}\n")
        sb.append("Lingua di risposta principale: ${profile.language}\n\n")

        if (faqs.isNotEmpty()) {
            sb.append("FAQ & Domande Frequenti Aziendali:\n")
            faqs.forEach { faq ->
                sb.append("- D: ${faq.question} | R: ${faq.answer}\n")
            }
            sb.append("\n")
        }

        if (products.isNotEmpty()) {
            sb.append("Catalogo Prodotti / Servizi:\n")
            products.forEach { prod ->
                sb.append("- ${prod.name} (${prod.price}): ${prod.description}\n")
            }
            sb.append("\n")
        }

        sb.append("""
            IL TUO COMPITO:
            Rispondi al messaggio del cliente in modo chiaro, utile, cortese e professionale.
            
            Devi restituire la risposta TASSATIVAMENTE in formato JSON valido con questa struttura:
            {
              "replyText": "testo della risposta per il cliente",
              "detectedIntent": "Inizio Chat | Orari | Prezzi/Prodotti | Prenotazione | Problema/Reclamo | Informazioni Generali",
              "confidence": 0.95,
              "shouldEscalateToHuman": false,
              "extractedLeadName": "Nome cliente se fornito o null",
              "extractedLeadContact": "Numero o email se forniti o null"
            }
            Non includere markdown o tag ```json attorno al JSON se possibile, solo il testo JSON grezzo.
        """.trimIndent())

        return sb.toString()
    }

    private fun buildJsonRequestBody(
        message: String,
        systemInstruction: String,
        history: List<Pair<String, String>>
    ): JSONObject {
        val root = JSONObject()

        // System instruction
        val systemInstructionObj = JSONObject()
        val sysParts = JSONArray()
        sysParts.put(JSONObject().put("text", systemInstruction))
        systemInstructionObj.put("parts", sysParts)
        root.put("systemInstruction", systemInstructionObj)

        // Contents
        val contentsArray = JSONArray()

        // Add history
        history.takeLast(6).forEach { (sender, text) ->
            val role = if (sender == "Customer") "user" else "model"
            val contentObj = JSONObject()
            contentObj.put("role", role)
            val partsArr = JSONArray()
            partsArr.put(JSONObject().put("text", text))
            contentObj.put("parts", partsArr)
            contentsArray.put(contentObj)
        }

        // Current user message
        val currentContent = JSONObject()
        currentContent.put("role", "user")
        val currentParts = JSONArray()
        currentParts.put(JSONObject().put("text", message))
        currentContent.put("parts", currentParts)
        contentsArray.put(currentContent)

        root.put("contents", contentsArray)

        // Generation config
        val genConfig = JSONObject()
        genConfig.put("temperature", 0.3)
        genConfig.put("topP", 0.95)
        root.put("generationConfig", genConfig)

        return root
    }

    private fun parseAiJsonResponse(rawText: String, profile: BusinessProfile): AiBotReplyResult {
        return try {
            val cleanJson = rawText.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val json = JSONObject(cleanJson)
            val replyText = json.optString("replyText", profile.fallbackResponse)
            val intent = json.optString("detectedIntent", "Informazioni")
            val confidence = json.optDouble("confidence", 0.90).toFloat()
            val escalate = json.optBoolean("shouldEscalateToHuman", false)
            val leadName = json.optString("extractedLeadName", "").takeIf { it.isNotBlank() && it != "null" }
            val leadContact = json.optString("extractedLeadContact", "").takeIf { it.isNotBlank() && it != "null" }

            AiBotReplyResult(
                replyText = replyText,
                detectedIntent = intent,
                confidence = confidence,
                shouldEscalateToHuman = escalate,
                extractedLeadName = leadName,
                extractedLeadContact = leadContact
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse JSON AI response, rawText: $rawText", e)
            AiBotReplyResult(
                replyText = rawText.ifBlank { profile.fallbackResponse },
                detectedIntent = "Risposta Automatica",
                confidence = 0.85f,
                shouldEscalateToHuman = false
            )
        }
    }

    private fun generateFallbackResponse(
        customerMessage: String,
        profile: BusinessProfile,
        faqs: List<FaqItem>,
        products: List<ProductItem>
    ): AiBotReplyResult {
        val lower = customerMessage.lowercase()

        // Match FAQs
        val matchedFaq = faqs.firstOrNull { faq ->
            faq.keywords.split(",").any { kw -> kw.isNotBlank() && lower.contains(kw.trim().lowercase()) } ||
                    lower.contains(faq.question.lowercase().take(8))
        }

        if (matchedFaq != null) {
            return AiBotReplyResult(
                replyText = matchedFaq.answer,
                detectedIntent = "FAQ Matching",
                confidence = 0.92f,
                shouldEscalateToHuman = false
            )
        }

        // Match Orari / Indirizzo
        if (lower.contains("orari") || lower.contains("aperto") || lower.contains("apertura")) {
            return AiBotReplyResult(
                replyText = "I nostri orari di apertura sono: ${profile.openingHours}. Ci trovi in ${profile.address}!",
                detectedIntent = "Orari & Posizione",
                confidence = 0.90f,
                shouldEscalateToHuman = false
            )
        }

        // Match Prezzi / Menu / Prodotti
        if (lower.contains("prezz") || lower.contains("quanto costa") || lower.contains("menu") || lower.contains("serviz")) {
            val prodSummary = if (products.isNotEmpty()) {
                "Ecco alcuni dei nostri servizi/prodotti principali:\n" +
                        products.take(3).joinToString("\n") { "• ${it.name}: ${it.price}" }
            } else {
                "Puoi consultare i nostri servizi sul sito ${profile.website} o contattarci al ${profile.phone}."
            }
            return AiBotReplyResult(
                replyText = prodSummary,
                detectedIntent = "Prodotti & Prezzi",
                confidence = 0.88f,
                shouldEscalateToHuman = false
            )
        }

        // Default greeting
        if (lower.contains("ciao") || lower.contains("buongiorno") || lower.contains("salve") || lower.contains("buonasera")) {
            return AiBotReplyResult(
                replyText = profile.defaultGreeting,
                detectedIntent = "Saluto",
                confidence = 0.95f,
                shouldEscalateToHuman = false
            )
        }

        // Default fallback
        return AiBotReplyResult(
            replyText = "${profile.fallbackResponse} (Puoi anche chiamarci al ${profile.phone}).",
            detectedIntent = "Richiesta Generica",
            confidence = 0.70f,
            shouldEscalateToHuman = true
        )
    }
}
