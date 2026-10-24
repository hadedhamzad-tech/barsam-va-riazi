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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SimpleLineChart
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.JadeContainer
import com.example.ui.theme.JadePrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.MainViewModel
import com.example.util.PersianUtils

@Composable
fun AnalyticsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val results by viewModel.allQuizResults.collectAsState()
    val totalQuizzes by viewModel.totalQuizzesCount.collectAsState()
    val avgScore by viewModel.averagePercentage.collectAsState()
    val mistakes by viewModel.allMistakes.collectAsState()

    val totalCorrect = results.sumOf { it.correctCount }
    val totalWrong = results.sumOf { it.wrongCount }
    val percentages = results.map { it.percentage }.reversed() // ترتیب از قدیمی به جدید برای نمودار

    // تحلیل مباحث قوی و نیازمند تمرین بر اساس داده‌های واقعی (مستثنی کردن آزمون‌های جامع و همه مباحث)
    val specificTopicResults = results.filter { it.topic != "همه مباحث" && it.topic != "جامع" }
    val topicScores = specificTopicResults.groupBy { it.topic }
        .mapValues { entry -> entry.value.map { it.percentage }.average() }

    val strongTopics = topicScores.filter { it.value >= 60.0 }.keys.take(3)
    val weakTopics = topicScores.filter { it.value < 60.0 }.keys.take(3)
    val onlyGeneralQuizzes = results.isNotEmpty() && specificTopicResults.isEmpty()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = JadePrimary)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = AmberPrimary)
                        Spacer(modifier = Modifier.padding(start = 8.dp))
                        Text(
                            text = "تحلیل عملکرد ریاضی کنکور",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "تمامی آمارها از نتایج واقعی آزمون‌های ثبت‌شده شما در پایگاه داده محلی استخراج شده‌اند.",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // کارت‌های آماری جامع
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AnalyticsStatCard(
                    title = "کل آزمون‌ها",
                    value = PersianUtils.toPersianDigits(totalQuizzes),
                    color = JadeContainer,
                    textColor = JadePrimary,
                    modifier = Modifier.weight(1f)
                )
                AnalyticsStatCard(
                    title = "میانگین تراز",
                    value = PersianUtils.formatPercentage(avgScore),
                    color = AmberContainer,
                    textColor = AmberPrimary,
                    modifier = Modifier.weight(1f)
                )
                AnalyticsStatCard(
                    title = "پاسخ صحیح",
                    value = PersianUtils.toPersianDigits(totalCorrect),
                    color = Color(0xFFE8F5E9),
                    textColor = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
                AnalyticsStatCard(
                    title = "پاسخ غلط",
                    value = PersianUtils.toPersianDigits(totalWrong),
                    color = Color(0xFFFFEBEE),
                    textColor = ErrorRed,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // نمودار پیشرفت در طول زمان
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "روند درصد موفقیت در طول زمان",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = JadePrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SimpleLineChart(percentages = percentages)
                }
            }
        }

        // مباحث قوی و نیازمند تمرین
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // مباحث قوی
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = SuccessGreen)
                            Spacer(modifier = Modifier.padding(start = 4.dp))
                            Text("مباحث قوی", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        if (strongTopics.isNotEmpty()) {
                            strongTopics.forEach { topic ->
                                Text("• $topic", fontSize = 11.sp, color = Color.DarkGray, modifier = Modifier.padding(vertical = 2.dp))
                            }
                        } else if (onlyGeneralQuizzes) {
                            Text("برای تحلیل مباحث، آزمون تک‌مبحثی بدهید", fontSize = 10.sp, color = AmberPrimary, lineHeight = 15.sp)
                        } else {
                            Text("پس از چند آزمون مشخص می‌شود", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }

                // مباحث نیازمند تمرین بیشتر
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = AmberPrimary)
                            Spacer(modifier = Modifier.padding(start = 4.dp))
                            Text("نیازمند تمرین", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AmberPrimary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        if (weakTopics.isNotEmpty()) {
                            weakTopics.forEach { topic ->
                                Text("• $topic", fontSize = 11.sp, color = Color.DarkGray, modifier = Modifier.padding(vertical = 2.dp))
                            }
                        } else if (onlyGeneralQuizzes) {
                            Text("برای تحلیل مباحث، آزمون تک‌مبحثی بدهید", fontSize = 10.sp, color = AmberPrimary, lineHeight = 15.sp)
                        } else if (mistakes.isNotEmpty()) {
                            val mistakeTopics = mistakes.map { it.topic }.distinct().take(3)
                            mistakeTopics.forEach { topic ->
                                Text("• $topic", fontSize = 11.sp, color = Color.DarkGray, modifier = Modifier.padding(vertical = 2.dp))
                            }
                        } else {
                            Text("تمرین مداوم توصیه می‌شود", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsStatCard(
    title: String,
    value: String,
    color: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 10.sp, color = textColor.copy(alpha = 0.8f))
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColor)
        }
    }
}
