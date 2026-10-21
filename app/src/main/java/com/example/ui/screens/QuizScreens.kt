package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.MathQuestionEntity
import com.example.ui.navigation.Screen
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.JadeContainer
import com.example.ui.theme.JadePrimary
import com.example.ui.theme.JadeSecondary
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.MainViewModel
import com.example.util.PersianUtils

// ==========================================
// ۱. صفحه تنظیم و ساخت آزمون سفارشی
// ==========================================
@Composable
fun QuizSetupScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    var selectedGrade by remember { mutableStateOf(10) }
    var selectedTopic by remember { mutableStateOf("همه مباحث") }
    var selectedDifficulty by remember { mutableStateOf("همه سطوح") }
    var selectedCount by remember { mutableStateOf(5) }
    var selectedDuration by remember { mutableStateOf(10) }

    var availableTopics by remember { mutableStateOf<List<String>>(listOf("همه مباحث")) }
    var showNotEnoughQuestionsDialog by remember { mutableStateOf(false) }
    var availableCountForDialog by remember { mutableStateOf(0) }
    var showZeroQuestionsDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(selectedGrade) {
        val topics = viewModel.getTopicsForGrade(selectedGrade)
        availableTopics = listOf("همه مباحث") + topics
        selectedTopic = "همه مباحث"
    }

    val difficulties = listOf("همه سطوح", "آسان", "متوسط", "سخت", "کنکوری")
    val questionCounts = listOf(5, 10, 15, 20)
    val durations = listOf(5, 10, 15, 20)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = JadePrimary)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "آزمون‌ساز سفارشی برسام و ریاضی",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "پایه تحصیلی، مبحث، سطح دشواری و تعداد سوالات را مشخص کنید تا آزمون استاندارد چهارگزینه‌ای شما ساخته شود.",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // ۱. انتخاب پایه
        item {
            Text("۱. پایه تحصیلی:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = JadePrimary)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(10, 11, 12).forEach { grade ->
                    FilterChip(
                        selected = selectedGrade == grade,
                        onClick = { selectedGrade = grade },
                        label = { Text("پایه ${PersianUtils.toPersianDigits(grade)}") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = JadePrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // ۲. انتخاب مبحث درسی
        item {
            Text("۲. مبحث درسی:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = JadePrimary)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(availableTopics) { topic ->
                    FilterChip(
                        selected = selectedTopic == topic,
                        onClick = { selectedTopic = topic },
                        label = { Text(topic) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = JadeSecondary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // ۳. انتخاب سطح دشواری
        item {
            Text("۳. سطح دشواری سوالات:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = JadePrimary)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(difficulties) { diff ->
                    FilterChip(
                        selected = selectedDifficulty == diff,
                        onClick = { selectedDifficulty = diff },
                        label = { Text(diff) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AmberPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // ۴. تعداد سوالات
        item {
            Text("۴. تعداد سوالات آزمون:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = JadePrimary)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                questionCounts.forEach { count ->
                    FilterChip(
                        selected = selectedCount == count,
                        onClick = { selectedCount = count },
                        label = { Text("${PersianUtils.toPersianDigits(count)} سوال") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = JadeSecondary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // ۵. مدت زمان آزمون
        item {
            Text("۵. زمان پاسخ‌گویی:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = JadePrimary)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                durations.forEach { dur ->
                    FilterChip(
                        selected = selectedDuration == dur,
                        onClick = { selectedDuration = dur },
                        label = { Text("${PersianUtils.toPersianDigits(dur)} دقیقه") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF00796B),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // دکمه شروع آزمون
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    coroutineScope.launch {
                        val availableCount = viewModel.getAvailableQuizQuestionCount(
                            grade = selectedGrade,
                            topic = selectedTopic,
                            difficulty = selectedDifficulty
                        )
                        if (availableCount == 0) {
                            showZeroQuestionsDialog = true
                        } else if (availableCount < selectedCount) {
                            availableCountForDialog = availableCount
                            showNotEnoughQuestionsDialog = true
                        } else {
                            viewModel.startCustomQuiz(
                                grade = selectedGrade,
                                topic = selectedTopic,
                                difficulty = selectedDifficulty,
                                count = selectedCount,
                                durationMinutes = selectedDuration
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "شروع آزمون با تایمر هوشمند",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }

    if (showZeroQuestionsDialog) {
        AlertDialog(
            onDismissRequest = { showZeroQuestionsDialog = false },
            title = { Text("عدم وجود سؤال با مشخصات انتخابی", fontWeight = FontWeight.Bold) },
            text = { Text("هیچ سؤالی با فیلترهای انتخابی در بانک سوالات یافت نشد. لطفاً مبحث یا سطح دشواری دیگری را انتخاب کنید.") },
            confirmButton = {
                TextButton(onClick = { showZeroQuestionsDialog = false }) {
                    Text("تغییر فیلترها", color = JadePrimary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showNotEnoughQuestionsDialog) {
        AlertDialog(
            onDismissRequest = { showNotEnoughQuestionsDialog = false },
            title = { Text("محدودیت تعداد سؤالات", fontWeight = FontWeight.Bold) },
            text = {
                Text("فقط ${PersianUtils.toPersianDigits(availableCountForDialog)} سوال با فیلترهای انتخابی در بانک موجود است. آیا مایلید آزمون با همین تعداد آغاز شود یا مایل به تغییر فیلترها هستید؟")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showNotEnoughQuestionsDialog = false
                        viewModel.startCustomQuiz(
                            grade = selectedGrade,
                            topic = selectedTopic,
                            difficulty = selectedDifficulty,
                            count = availableCountForDialog,
                            durationMinutes = selectedDuration
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JadePrimary)
                ) {
                    Text("شروع با ${PersianUtils.toPersianDigits(availableCountForDialog)} سوال", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotEnoughQuestionsDialog = false }) {
                    Text("تغییر فیلترها")
                }
            }
        )
    }
}

// ==========================================
// ۲. صفحه آزمون فعال (Quiz Active Screen)
// ==========================================
@Composable
fun QuizActiveScreen(
    viewModel: MainViewModel,
    onCancel: () -> Unit
) {
    var showCancelDialog by remember { mutableStateOf(false) }
    var showFinishDialog by remember { mutableStateOf(false) }

    BackHandler { showCancelDialog = true }

    val questions by viewModel.activeQuestions.collectAsState()
    val currentIndex by viewModel.currentQuestionIndex.collectAsState()
    val answers by viewModel.userAnswers.collectAsState()
    val remainingSecs by viewModel.remainingSeconds.collectAsState()

    if (questions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("در حال بارگذاری سوالات آزمون...")
        }
        return
    }

    val currentQ = questions.getOrNull(currentIndex) ?: return
    val selectedOption = answers[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // نوار تایمر و وضعیت
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (remainingSecs <= 60) Color(0xFFFFEBEE) else JadeContainer
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = if (remainingSecs <= 60) ErrorRed else JadePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "زمان باقیمانده: ${PersianUtils.formatDuration(remainingSecs)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (remainingSecs <= 60) ErrorRed else JadePrimary
                    )
                }

                Text(
                    text = "سوال ${PersianUtils.toPersianDigits(currentIndex + 1)} از ${PersianUtils.toPersianDigits(questions.size)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = JadePrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { (currentIndex + 1).toFloat() / questions.size },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = AmberPrimary,
            trackColor = Color.LightGray.copy(alpha = 0.4f)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // متن سوال
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "مبحث: ${currentQ.topic}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "سطح: ${currentQ.difficulty}",
                        fontSize = 11.sp,
                        color = AmberPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = currentQ.questionText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = JadePrimary,
                    lineHeight = 22.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // گزینه‌های چهارگانه
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val options = listOf(currentQ.option1, currentQ.option2, currentQ.option3, currentQ.option4)
            itemsIndexed(options) { optIndex, optionText ->
                OptionCard(
                    index = optIndex,
                    text = optionText,
                    isSelected = selectedOption == optIndex,
                    onClick = { viewModel.selectQuizAnswer(currentIndex, optIndex) }
                )
            }
        }

        if (selectedOption != null) {
            TextButton(
                onClick = { viewModel.clearQuizAnswer(currentIndex) },
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 2.dp)
            ) {
                Icon(Icons.Default.Clear, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("پاک کردن پاسخ (بدون پاسخ)", color = ErrorRed, fontSize = 12.sp)
            }
        }

        // دکمه‌های ناوبری قبلی / بعدی / اتمام
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = { viewModel.prevQuestion() },
                enabled = currentIndex > 0,
                modifier = Modifier.weight(1f)
            ) {
                Text("سوال قبلی")
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (currentIndex < questions.size - 1) {
                Button(
                    onClick = { viewModel.nextQuestion() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = JadePrimary)
                ) {
                    Text("سوال بعدی", color = Color.White)
                }
            } else {
                Button(
                    onClick = { showFinishDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary)
                ) {
                    Text("اتمام و مشاهده کارنامه", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("انصراف از آزمون") },
            text = { Text("آیا مطمئن هستید که می‌خواهید آزمون فعلی را بدون ثبت پایان دهید؟") },
            confirmButton = {
                TextButton(onClick = {
                    showCancelDialog = false
                    viewModel.cancelActiveQuiz()
                    onCancel()
                }) {
                    Text("بله، خروج", color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("ادامه آزمون")
                }
            }
        )
    }

    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = { Text("تأیید پایان آزمون") },
            text = { Text("آیا آماده‌اید کارنامه و درصد پاسخ‌های خود را مشاهده کنید؟") },
            confirmButton = {
                Button(
                    onClick = {
                        showFinishDialog = false
                        viewModel.finishActiveQuiz()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JadePrimary)
                ) {
                    Text("مشاهده کارنامه", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) {
                    Text("بازگشت و بازبینی")
                }
            }
        )
    }
}

@Composable
fun OptionCard(
    index: Int,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) JadePrimary else Color.LightGray.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) JadeContainer.copy(alpha = 0.6f) else Color.White
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        color = if (isSelected) JadePrimary else Color.LightGray.copy(alpha = 0.3f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = PersianUtils.toPersianDigits(index + 1),
                    color = if (isSelected) Color.White else Color.DarkGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) JadePrimary else Color.Black
            )
        }
    }
}

// ==========================================
// ۳. صفحه کارنامه و نتیجه آزمون (Quiz Result Screen)
// ==========================================
@Composable
fun QuizResultScreen(
    viewModel: MainViewModel,
    onNavigateToSetup: () -> Unit,
    onNavigateToDashboard: () -> Unit
) {
    BackHandler { onNavigateToDashboard() }

    val result by viewModel.latestQuizResult.collectAsState()
    val questions by viewModel.activeQuestions.collectAsState()
    val answers by viewModel.userAnswers.collectAsState()

    var savingQuestion by remember { mutableStateOf<MathQuestionEntity?>(null) }
    var userChosenOpt by remember { mutableStateOf(-1) }
    var mistakeReason by remember { mutableStateOf("بی‌دقتی محاسباتی") }
    var userNote by remember { mutableStateOf("") }
    var savedSuccessId by remember { mutableStateOf<String?>(null) }

    if (result == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("کارنامه‌ای برای نمایش موجود نیست.")
        }
        return
    }

    val res = result!!

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // بنر درصد موفقیت
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        res.percentage >= 70.0 -> JadePrimary
                        res.percentage >= 40.0 -> AmberPrimary
                        else -> Color(0xFFD32F2F)
                    }
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "کارنامه آزمون ریاضی",
                        fontSize = 15.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = PersianUtils.formatPercentage(res.percentage),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "پایه ${PersianUtils.toPersianDigits(res.grade)} • مبحث: ${res.topic} • سطح: ${res.difficulty} • مدت: ${PersianUtils.formatDuration(res.durationSeconds)}",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // تفکیک تعداد پاسخ‌ها
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResultStatCard(
                    title = "صحیح",
                    count = res.correctCount,
                    color = Color(0xFFE8F5E9),
                    textColor = SuccessGreen,
                    modifier = Modifier.weight(1f)
                )
                ResultStatCard(
                    title = "غلط",
                    count = res.wrongCount,
                    color = Color(0xFFFFEBEE),
                    textColor = ErrorRed,
                    modifier = Modifier.weight(1f)
                )
                ResultStatCard(
                    title = "نزده",
                    count = res.unansweredCount,
                    color = Color(0xFFF5F5F5),
                    textColor = Color.Gray,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // مرور سوالات آزمون و افزودن اشتباهات
        item {
            Text(
                text = "مرور سوالات و پاسخ‌های تشریحی آفلاین:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = JadePrimary,
                modifier = Modifier.padding(top = 6.dp)
            )
        }

        itemsIndexed(questions) { idx, q ->
            val chosen = answers[idx]
            val isCorrect = chosen == q.correctOptionIndex
            val isUnanswered = chosen == null

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "سوال ${PersianUtils.toPersianDigits(idx + 1)} (${q.topic})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = JadePrimary
                        )

                        Text(
                            text = when {
                                isCorrect -> "✓ صحیح"
                                isUnanswered -> "— نزده"
                                else -> "✗ غلط"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isCorrect -> SuccessGreen
                                isUnanswered -> Color.Gray
                                else -> ErrorRed
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = q.questionText, fontSize = 13.sp, lineHeight = 19.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "گزینه درست: گزینه ${PersianUtils.toPersianDigits(q.correctOptionIndex + 1)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )

                    if (!isCorrect && !isUnanswered) {
                        Text(
                            text = "گزینه انتخابی شما: گزینه ${PersianUtils.toPersianDigits(chosen!! + 1)}",
                            fontSize = 11.sp,
                            color = ErrorRed
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "پاسخ تشریحی: ${q.explanation}",
                        fontSize = 11.sp,
                        color = Color.DarkGray,
                        lineHeight = 16.sp
                    )

                    // دکمه ثبت در دفترچه اشتباهات برای تست‌های غلط
                    if (!isCorrect && !isUnanswered) {
                        Spacer(modifier = Modifier.height(8.dp))
                        if (savedSuccessId == q.id) {
                            Text(
                                text = "✓ به دفترچه اشتباهات افزوده شد",
                                fontSize = 11.sp,
                                color = SuccessGreen,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            TextButton(
                                onClick = {
                                    savingQuestion = q
                                    userChosenOpt = chosen!!
                                    mistakeReason = "بی‌دقتی محاسباتی"
                                    userNote = ""
                                }
                            ) {
                                Icon(Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ثبت در دفترچه اشتباهات", fontSize = 11.sp, color = AmberPrimary)
                            }
                        }
                    }
                }
            }
        }

        // دکمه‌های پایین کارنامه
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onNavigateToSetup,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = JadePrimary)
                ) {
                    Text("آزمون مجدد", color = Color.White)
                }
                OutlinedButton(
                    onClick = onNavigateToDashboard,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("داشبورد اصلی")
                }
            }
        }
    }

    if (savingQuestion != null) {
        val q = savingQuestion!!
        AlertDialog(
            onDismissRequest = { savingQuestion = null },
            title = { Text("ثبت در دفترچه اشتباهات", fontSize = 15.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("علت اشتباه در این تست:", fontSize = 12.sp)
                    val reasons = listOf("بی‌دقتی محاسباتی", "عدم تسلط مفهومی", "دام تستی", "کمبود زمان")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(reasons) { r ->
                            FilterChip(
                                selected = mistakeReason == r,
                                onClick = { mistakeReason = r },
                                label = { Text(r, fontSize = 11.sp) }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = userNote,
                        onValueChange = { userNote = it },
                        label = { Text("یادداشت رفع اشکال شما") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveMistake(q, userChosenOpt, mistakeReason, userNote)
                        savedSuccessId = q.id
                        savingQuestion = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JadePrimary)
                ) {
                    Text("ثبت اشتباه", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { savingQuestion = null }) {
                    Text("انصراف")
                }
            }
        )
    }
}

@Composable
fun ResultStatCard(
    title: String,
    count: Int,
    color: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 11.sp, color = textColor)
            Text(
                text = PersianUtils.toPersianDigits(count),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}
