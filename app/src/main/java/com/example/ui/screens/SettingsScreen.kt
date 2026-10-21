package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.JadePrimary
import com.example.ui.theme.JadeSecondary
import com.example.ui.viewmodel.MainViewModel
import com.example.util.PersianUtils

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val settings by viewModel.appSettings.collectAsState()
    val isSoundEnabled = settings?.isSoundEnabled ?: true
    val isAiEnabled = settings?.isAiEnabled ?: false

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "تنظیمات برنامه",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = JadePrimary
            )
        }

        // ۱. تنظیم صدای برنامه
        item {
            SettingToggleCard(
                title = "صدای برنامه در آزمون‌ها",
                subtitle = "پخش تن صوتی دلپذیر برای پاسخ صحیح و تن ملایم برای پاسخ غلط (در صورت سکوت یا ویبره گوشی، صدایی پخش نخواهد شد).",
                icon = Icons.AutoMirrored.Filled.VolumeUp,
                isChecked = isSoundEnabled,
                onCheckedChange = { viewModel.setSoundEnabled(it) }
            )
        }

        // ۲. تنظیم اتصال هوش مصنوعی Gemini
        item {
            SettingToggleCard(
                title = "اتصال به دستیار هوشمند Gemini",
                subtitle = "در حالت خاموش، کلیه درخواست‌های شبکه هوش مصنوعی قطع بوده و برنامه ۱۰۰٪ آفلاین عمل می‌کند. در حالت روشن، حل تشریحی گام‌به‌گام ارائه می‌شود.",
                icon = Icons.Default.AutoAwesome,
                isChecked = isAiEnabled,
                onCheckedChange = { viewModel.setAiEnabled(it) }
            )
        }

        // ۳. امنیت و حریم خصوصی
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = JadeSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "امنیت و ذخیره‌سازی آفلاین",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = JadePrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "تمام داده‌های آزمون، نمرات، دفترچه اشتباهات و دستاوردها بر روی پایگاه داده محلی Room ذخیره شده و پس از بستن یا به‌روزرسانی برنامه کاملاً حفظ می‌شوند.",
                            fontSize = 11.sp,
                            color = Color.DarkGray,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }

        // ۴. بخش درباره برنامه و شماره نسخه
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = AmberPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "درباره «برسام و ریاضی»",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = JadePrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "برنامه «برسام و ریاضی» یک دستیار تخصصی، آموزشی و ارزیابی برای دانش‌آموزان پایه‌های دهم، یازدهم و دوازدهم رشته‌های تجربی و ریاضی است. این برنامه با ۶۰ تست تاییدشده در ۴ سطح (آسان، متوسط، سخت، کنکوری)، سیستم تحلیل کارنامه، دفترچه اشتباهات هوشمند، تالار مدال‌ها و دستیار هوشمند Gemini طراحی شده است.",
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "نسخه برنامه:",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "1.0.0 (${PersianUtils.toPersianDigits("1.0.0")})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = JadePrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingToggleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = JadePrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = JadePrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    lineHeight = 15.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = JadePrimary
                )
            )
        }
    }
}
