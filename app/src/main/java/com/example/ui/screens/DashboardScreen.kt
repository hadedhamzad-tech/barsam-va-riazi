package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.navigation.Screen
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.JadeContainer
import com.example.ui.theme.JadePrimary
import com.example.ui.theme.JadeSecondary
import com.example.ui.theme.OnAmberContainer
import com.example.ui.theme.OnJadeContainer
import com.example.ui.viewmodel.MainViewModel
import com.example.util.PersianUtils

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onNavigate: (Screen) -> Unit
) {
    val totalQuizzes by viewModel.totalQuizzesCount.collectAsState()
    val averageScore by viewModel.averagePercentage.collectAsState()
    val mistakes by viewModel.unresolvedMistakes.collectAsState()
    val settings by viewModel.appSettings.collectAsState()
    val greeting = PersianUtils.getBarsamGreeting()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ۱. پیام خوشامدگویی متناسب با زمان محلی توسط «برسام»
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = JadePrimary)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(AmberPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ب",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = greeting.first,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = greeting.second,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // ۲. کارت‌های خلاصه آماری
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryStatBox(
                    title = "آزمون‌ها",
                    value = PersianUtils.toPersianDigits(totalQuizzes),
                    subtitle = "انجام‌شده",
                    modifier = Modifier.weight(1f),
                    color = JadeContainer,
                    textColor = OnJadeContainer
                )
                SummaryStatBox(
                    title = "میانگین",
                    value = "%${PersianUtils.toPersianDigits(averageScore ?: 0.0)}",
                    subtitle = "موفقیت",
                    modifier = Modifier.weight(1f),
                    color = AmberContainer,
                    textColor = OnAmberContainer
                )
                SummaryStatBox(
                    title = "اشتباهات",
                    value = PersianUtils.toPersianDigits(mistakes.size),
                    subtitle = "نیاز به مرور",
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFFFEBEE),
                    textColor = Color(0xFFC62828)
                )
                SummaryStatBox(
                    title = "امتیاز کل",
                    value = PersianUtils.toPersianDigits(settings?.totalScore ?: 0),
                    subtitle = "کسب‌شده",
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFE8F5E9),
                    textColor = Color(0xFF2E7D32)
                )
            }
        }

        // ۳. عنوان بخش‌های اصلی
        item {
            Text(
                text = "بخش‌های اصلی برسام و ریاضی",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = JadePrimary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // ۴. ماتریس دسترسی به بخش‌های ۸ گانه
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FeatureCard(
                        title = "آزمون‌ساز سفارشی",
                        description = "آزمون ۴ گزینه‌ای با تایمر و کارنامه",
                        icon = Icons.Default.Quiz,
                        iconTint = AmberPrimary,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.QuizSetup) }
                    )
                    FeatureCard(
                        title = "تمرین و فرمول‌نامه",
                        description = "مرور نکات کلیدی پایه‌های ۱۰، ۱۱ و ۱۲",
                        icon = Icons.Default.Calculate,
                        iconTint = JadeSecondary,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.Practice) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FeatureCard(
                        title = "دفترچه اشتباهات",
                        description = "بررسی دلایل خطا و یادداشت رفع اشکال",
                        icon = Icons.Default.BookmarkBorder,
                        iconTint = Color(0xFFD32F2F),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.Mistakes) }
                    )
                    FeatureCard(
                        title = "برنامه مطالعه",
                        description = "زمان‌بندی و اهداف روزانه مطالعه ریاضی",
                        icon = Icons.Default.CalendarMonth,
                        iconTint = Color(0xFF1976D2),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.StudyPlan) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FeatureCard(
                        title = "تحلیل عملکرد",
                        description = "نمودار رشد، مباحث قوی و نیازمند تمرین",
                        icon = Icons.Default.AutoGraph,
                        iconTint = JadePrimary,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.Analytics) }
                    )
                    FeatureCard(
                        title = "تالار مدال‌ها",
                        description = "دستاوردهای تحصیلی و امتیازات کسب‌شده",
                        icon = Icons.Default.EmojiEvents,
                        iconTint = AmberPrimary,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.Badges) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FeatureCard(
                        title = "دستیار هوشمند برسام",
                        description = "راهنمایی تشریحی مسائل ریاضی کنکور",
                        icon = Icons.Default.AutoAwesome,
                        iconTint = Color(0xFF7B1FA2),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.AiAssistant) }
                    )
                    FeatureCard(
                        title = "تنظیمات برنامه",
                        description = "مدیریت صدا، سوییچ هوش مصنوعی، نسخه",
                        icon = Icons.Default.Settings,
                        iconTint = Color(0xFF455A64),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.Settings) }
                    )
                }
            }
        }

        // ۵. نکته آموزشی روز
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AmberContainer)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "نکته",
                        tint = AmberPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "نکته طلایی مثلثات کنکور",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = OnAmberContainer
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "فرمول cos(2x) = 1 - 2sin²(x) = 2cos²(x) - 1 از پرتکرارترین روابط در انتگرال، مشتق و معادلات است.",
                            fontSize = 11.sp,
                            color = OnAmberContainer.copy(alpha = 0.85f),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryStatBox(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    color: Color,
    textColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 10.sp, color = textColor.copy(alpha = 0.8f))
            Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textColor)
            Text(text = subtitle, fontSize = 9.sp, color = textColor.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun FeatureCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(iconTint.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = JadePrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 10.sp,
                color = Color.Gray,
                lineHeight = 14.sp
            )
        }
    }
}
