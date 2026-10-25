package com.example.ui.screens

import androidx.activity.compose.BackHandler
import com.example.ui.components.MathText
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.Screen
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.JadeContainer
import com.example.ui.theme.JadePrimary
import com.example.ui.theme.JadeSecondary
import com.example.ui.theme.OnAmberContainer
import com.example.ui.theme.OnJadeContainer
import com.example.ui.viewmodel.MainViewModel

@Composable
fun AiAssistantScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit
) {
    BackHandler { onBack() }

    val messages by viewModel.chatMessages.collectAsState()
    val isLoading by viewModel.isAiLoading.collectAsState()
    val aiLoadingStatus by viewModel.aiLoadingStatus.collectAsState()
    val settings by viewModel.appSettings.collectAsState()
    val isAiEnabled = settings?.isAiEnabled ?: false

    var inputPrompt by remember { mutableStateOf("") }
    var isComplexThinking by remember { mutableStateOf(false) } // پیش‌فرض تفکر عمیق خاموش
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // بنر وضعیت هوش مصنوعی در صورت خاموش بودن
        if (!isAiEnabled) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AmberContainer)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = AmberPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "اتصال هوش مصنوعی خاموش است",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnAmberContainer
                        )
                        Text(
                            text = "برای دریافت حل تشریحی آنلاین می‌توانید آن را فعال کنید.",
                            fontSize = 11.sp,
                            color = OnAmberContainer.copy(alpha = 0.8f)
                        )
                    }
                    TextButton(onClick = onOpenSettings) {
                        Text("تنظیمات", fontSize = 11.sp, color = JadePrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // انتخاب مدل و حالت هوش مصنوعی
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = isComplexThinking,
                onClick = { isComplexThinking = true },
                leadingIcon = { Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(16.dp)) },
                label = { Text("استدلال عمیق", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = JadePrimary,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = !isComplexThinking,
                onClick = { isComplexThinking = false },
                leadingIcon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp)) },
                label = { Text("پاسخ سریع", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AmberPrimary,
                    selectedLabelColor = Color.White
                )
            )
        }

        // تاریخچه پیام‌ها
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(message = msg)
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = JadePrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = aiLoadingStatus,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        // کادر ارسال پیام
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputPrompt,
                    onValueChange = { inputPrompt = it },
                    placeholder = { Text("سوال ریاضی یا صورت مسئله کنکور را بنویسید...", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        val text = inputPrompt.trim()
                        if (text.isNotBlank()) {
                            viewModel.askAiAssistant(text, isComplexThinking)
                            inputPrompt = ""
                        }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .background(if (inputPrompt.isNotBlank() && !isLoading) JadePrimary else Color.LightGray, CircleShape),
                    enabled = inputPrompt.isNotBlank() && !isLoading
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "ارسال",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: MainViewModel.ChatMessage) {
    val isUser = message.isUser
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.Start else Arrangement.End
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 4.dp else 16.dp,
                bottomEnd = if (isUser) 16.dp else 4.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) JadePrimary else Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (!isUser) {
                    Text(
                        text = "دستیار درسی برسام",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = JadeSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                MathText(
                    text = message.text,
                    fontSize = 13.sp,
                    color = if (isUser) Color.White else Color.Black,
                    lineHeight = 20.sp
                )
                if (!isUser && message.modelUsed != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = message.modelUsed,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
                if (!isUser && message.technicalDetail != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = message.technicalDetail,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
