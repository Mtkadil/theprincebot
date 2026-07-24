package com.theprince.barbershop.ai

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WhatsAppIntegrationTab() {
    Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Integrazione WhatsApp", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Stato: In attesa di avvio")
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { 
                    // TODO: Collegare l'intent per avviare il NotificationListenerService
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Avvia Ascolto Notifiche WhatsApp")
            }
        }
    }
}
