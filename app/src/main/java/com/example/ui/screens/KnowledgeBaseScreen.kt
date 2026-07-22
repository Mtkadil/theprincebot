package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BusinessProfile
import com.example.data.FaqItem
import com.example.data.ProductItem
import com.example.ui.BotViewModel

@Composable
fun KnowledgeBaseScreen(
    viewModel: BotViewModel,
    profile: BusinessProfile?,
    faqs: List<FaqItem>,
    products: List<ProductItem>,
    modifier: Modifier = Modifier
) {
    var selectedSectionTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TabRow(selectedTabIndex = selectedSectionTab) {
            Tab(
                selected = selectedSectionTab == 0,
                onClick = { selectedSectionTab = 0 },
                text = { Text("Profilo Azienda", fontSize = 12.sp) },
                icon = { Icon(Icons.Default.Business, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedSectionTab == 1,
                onClick = { selectedSectionTab = 1 },
                text = { Text("FAQ (${faqs.size})", fontSize = 12.sp) },
                icon = { Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedSectionTab == 2,
                onClick = { selectedSectionTab = 2 },
                text = { Text("Catalogo (${products.size})", fontSize = 12.sp) },
                icon = { Icon(Icons.Default.Inventory2, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedSectionTab) {
                0 -> BusinessProfileTab(profile = profile ?: BusinessProfile(), onSave = { viewModel.saveBusinessProfile(it) })
                1 -> FaqsTab(faqs = faqs, onAdd = { viewModel.addFaq(it) }, onDelete = { viewModel.deleteFaq(it) })
                2 -> ProductsTab(products = products, onAdd = { viewModel.addProduct(it) }, onDelete = { viewModel.deleteProduct(it) })
            }
        }
    }
}

@Composable
fun BusinessProfileTab(
    profile: BusinessProfile,
    onSave: (BusinessProfile) -> Unit
) {
    var businessName by remember(profile) { mutableStateOf(profile.businessName) }
    var botName by remember(profile) { mutableStateOf(profile.botName) }
    var description by remember(profile) { mutableStateOf(profile.description) }
    var address by remember(profile) { mutableStateOf(profile.address) }
    var openingHours by remember(profile) { mutableStateOf(profile.openingHours) }
    var phone by remember(profile) { mutableStateOf(profile.phone) }
    var whatsappNumber by remember(profile) { mutableStateOf(profile.whatsappNumber) }
    var tone by remember(profile) { mutableStateOf(profile.tone) }
    var autoReplyEnabled by remember(profile) { mutableStateOf(profile.autoReplyEnabled) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Informazioni Generali Azienda",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Auto-Risposta IA Globale", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Text("Abilita risposte automatiche per tutti i clienti", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = autoReplyEnabled,
                        onCheckedChange = { autoReplyEnabled = it },
                        modifier = Modifier.testTag("global_autoreply_switch")
                    )
                }

                HorizontalDivider()

                OutlinedTextField(
                    value = businessName,
                    onValueChange = { businessName = it },
                    label = { Text("Nome Azienda / Attività") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = botName,
                    onValueChange = { botName = it },
                    label = { Text("Nome Assistente Virtuale") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrizione Attività e Servizi") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Indirizzo e Sede") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = openingHours,
                    onValueChange = { openingHours = it },
                    label = { Text("Orari di Apertura") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Telefono") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = whatsappNumber,
                        onValueChange = { whatsappNumber = it },
                        label = { Text("WhatsApp") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Text("Tono di Voce del Bot:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Professionale ma cordiale", "Formale", "Informale / Amichevole", "Promozionale").forEach { option ->
                        FilterChip(
                            selected = tone == option,
                            onClick = { tone = option },
                            label = { Text(option, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        onSave(
                            profile.copy(
                                businessName = businessName,
                                botName = botName,
                                description = description,
                                address = address,
                                openingHours = openingHours,
                                phone = phone,
                                whatsappNumber = whatsappNumber,
                                tone = tone,
                                autoReplyEnabled = autoReplyEnabled
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_business_profile_button")
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Salva Profilo Aziendale")
                }
            }
        }
    }
}

@Composable
fun FaqsTab(
    faqs: List<FaqItem>,
    onAdd: (FaqItem) -> Unit,
    onDelete: (FaqItem) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(faqs) { faq ->
                ElevatedCard(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = faq.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            IconButton(onClick = { onDelete(faq) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Elimina", tint = MaterialTheme.colorScheme.error)
                            }
                        }

                        Text(
                            text = "D: ${faq.question}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "R: ${faq.answer}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_faq_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Aggiungi FAQ")
        }
    }

    if (showAddDialog) {
        NewFaqDialog(
            onDismiss = { showAddDialog = false },
            onSave = { q, a, cat, kw ->
                onAdd(FaqItem(question = q, answer = a, category = cat, keywords = kw))
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ProductsTab(
    products: List<ProductItem>,
    onAdd: (ProductItem) -> Unit,
    onDelete: (ProductItem) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(products) { prod ->
                ElevatedCard(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = prod.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = prod.price,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = prod.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = prod.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            IconButton(onClick = { onDelete(prod) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Elimina", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_product_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Aggiungi Prodotto")
        }
    }

    if (showAddDialog) {
        NewProductDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, price, desc, cat ->
                onAdd(ProductItem(name = name, price = price, description = desc, category = cat))
                showAddDialog = false
            }
        )
    }
}

@Composable
fun NewFaqDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var q by remember { mutableStateOf("") }
    var a by remember { mutableStateOf("") }
    var cat by remember { mutableStateOf("Generale") }
    var kw by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Aggiungi Domanda Frequente (FAQ)") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = q, onValueChange = { q = it }, label = { Text("Domanda del Cliente") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = a, onValueChange = { a = it }, label = { Text("Risposta Automatica") }, modifier = Modifier.fillMaxWidth(), maxLines = 3)
                OutlinedTextField(value = cat, onValueChange = { cat = it }, label = { Text("Categoria") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = kw, onValueChange = { kw = it }, label = { Text("Parole Chiave (separate da virgola)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { onSave(q, a, cat, kw) }, enabled = q.isNotBlank() && a.isNotBlank()) {
                Text("Salva FAQ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annulla") }
        }
    )
}

@Composable
fun NewProductDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("€10,00") }
    var desc by remember { mutableStateOf("") }
    var cat by remember { mutableStateOf("Servizi") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Aggiungi Prodotto / Servizio") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome Prodotto/Servizio") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Prezzo") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Descrizione e Dettagli") }, modifier = Modifier.fillMaxWidth(), maxLines = 3)
                OutlinedTextField(value = cat, onValueChange = { cat = it }, label = { Text("Categoria") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { onSave(name, price, desc, cat) }, enabled = name.isNotBlank()) {
                Text("Salva Prodotto")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annulla") }
        }
    )
}
