package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.local.entity.MathQuestionEntity
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.JadePrimary
import com.example.ui.theme.JadeSecondary
import com.example.ui.theme.OnAmberContainer
import com.example.ui.viewmodel.MainViewModel
import com.example.util.PersianUtils

data class FormulaCardItem(
    val title: String,
    val grade: Int,
    val topic: String,
    val formulaSummary: String,
    val testTips: String
)

@Composable
fun PracticeScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    var selectedGrade by remember { mutableStateOf(10) }
    val allQuestions by viewModel.allQuestions.collectAsState()
    val gradeQuestions = allQuestions.filter { it.grade == selectedGrade }

    val formulaSummaries = listOf(
        // دهم
        FormulaCardItem(
            title = "فرمول‌های دنباله حسابی و هندسی",
            grade = 10,
            topic = "مجموعه، الگو و دنباله",
            formulaSummary = "دنباله حسابی: a_n = a_1 + (n - 1)d و S_n = (n/2)[2a_1 + (n - 1)d]\nدنباله هندسی: a_n = a_1 · q^(n - 1) و S_n = a_1(q^n - 1)/(q - 1)",
            testTips = "در دنباله حسابی: a_p + a_q = a_r + a_s به شرطی که p + q = r + s باشد."
        ),
        FormulaCardItem(
            title = "اتحادها و روابط مثلثاتی دهم",
            grade = 10,
            topic = "مثلثات",
            formulaSummary = "sin²(x) + cos²(x) = 1\ntan(x) = sin(x)/cos(x)\n1 + tan²(x) = 1/cos²(x)",
            testTips = "با داشتن sin(x) ± cos(x) طرفین را به توان ۲ برسانید تا sin(2x) به دست آید."
        ),
        FormulaCardItem(
            title = "معادله درجه دوم و فرمول دلتا",
            grade = 10,
            topic = "معادله درجه دوم",
            formulaSummary = "Δ = b² - 4ac\nمجموع ریشه‌ها: S = -b/a\nضرب ریشه‌ها: P = c/a",
            testTips = "رابطه مجموع معکوس ریشه‌ها: 1/α + 1/β = S/P."
        ),
        // یازدهم
        FormulaCardItem(
            title = "فاصله نقطه از خط و دو خط موازی",
            grade = 11,
            topic = "هندسه تحلیلی",
            formulaSummary = "فاصله نقطه (x0, y0) از ax + by + c = 0:\nd = |ax0 + by0 + c| / √(a² + b²)\nفاصله دو خط موازی: d = |c1 - c2| / √(a² + b²)",
            testTips = "قبل از محاسبه فاصله دو خط موازی، مطمئن شوید ضرایب a و b در هر دو معادله یکسان شده باشند."
        ),
        FormulaCardItem(
            title = "قوانین لگاریتم و توابع نمایی",
            grade = 11,
            topic = "توابع نمایی و لگاریتمی",
            formulaSummary = "log_a(x · y) = log_a(x) + log_a(y)\nlog_a(x^k) = k · log_a(x)\nlog_b(a) = log_c(a) / log_c(b)",
            testTips = "شرط دامنه لگاریتم: مبنا مثبت و مخالف ۱، و عبارت جلوی لگاریتم اکیداً مثبت باشد."
        ),
        FormulaCardItem(
            title = "حد و رفع ابهام صفر صفرم",
            grade = 11,
            topic = "حد و پیوستگی",
            formulaSummary = "روش‌های رفع ابهام ۰/۰: تجزیه به عامل‌های جبری (x - a)، ضرب در مزدوج رادیکال و قاعده هم‌ارزی.",
            testTips = "اگر در کسر عامل رادیکالی فرجه ۲ یا ۳ باشد، از اتحاد مزدوج یا چاق و لاغر برای رفع صفر در مخرج استفاده کنید."
        ),
        // دوازدهم
        FormulaCardItem(
            title = "قواعد اساسی مشتق‌گیری",
            grade = 12,
            topic = "مشتق",
            formulaSummary = "(x^n)' = n · x^(n - 1)\n(u · v)' = u'v + v'u\n(u/v)' = (u'v - v'u)/v²\n(f(g(x)))' = g'(x) · f'(g(x))",
            testTips = "مشتق تابع معکوس در نقطه: (f⁻¹)'(y0) = 1 / f'(x0) به طوری که f(x0) = y0."
        ),
        FormulaCardItem(
            title = "نقاط بحرانی، اکسترمم و عطف",
            grade = 12,
            topic = "کاربرد مشتق",
            formulaSummary = "نقاط بحرانی: ریشه‌های f'(x) = 0 یا نقاطی که f' در آن‌ها وجود ندارد.\nنقطه عطف: ریشه مشتق دوم f''(x) = 0 که در آن جهت تقعر تغییر کند.",
            testTips = "در مسائل بهینه‌سازی (بیشترین مساحت یا حجم)، تابع را بر حسب یک متغیر نوشته و مشتق اول را برابر صفر قرار دهید."
        ),
        FormulaCardItem(
            title = "حد در بی‌نهایت و هم‌ارزی‌ها",
            grade = 12,
            topic = "حد در بی‌نهایت",
            formulaSummary = "در کسر جبری به سمت بی‌نهایت: نسبت ضرایب توان‌های مساوی صورت و مخرج جواب حد است.",
            testTips = "وقتی x به سمت راست عدد میل می‌کند، ابتدا اثر [x] جزء صحیح را مشخص و سپس حد را محاسبه کنید."
        )
    )

    val currentFormulas = formulaSummaries.filter { it.grade == selectedGrade }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // انتخاب پایه تحصیلی
        item {
            Text(
                text = "انتخاب پایه برای مرور و تمرین:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = JadePrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf(10, 11, 12)) { grade ->
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

        // بخش فرمول‌های کلیدی
        item {
            Text(
                text = "فرمول‌های طلایی و نکات تستی پایه ${PersianUtils.toPersianDigits(selectedGrade)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = JadeSecondary
            )
        }

        items(currentFormulas) { item ->
            FormulaCard(item = item)
        }

        // بخش نمونه سوالات بازبینی‌شده این پایه
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "نمونه تست‌های آموزشی و پاسخ‌های تشریحی (پایه ${PersianUtils.toPersianDigits(selectedGrade)})",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = JadePrimary
            )
        }

        items(gradeQuestions.take(5)) { q ->
            PracticeQuestionCard(question = q)
        }
    }
}

@Composable
fun FormulaCard(item: FormulaCardItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = item.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = JadePrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.formulaSummary,
                fontSize = 12.sp,
                color = Color.DarkGray,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = AmberContainer),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "💡 نکته کنکوری: ${item.testTips}",
                    fontSize = 11.sp,
                    color = OnAmberContainer,
                    modifier = Modifier.padding(8.dp),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun PracticeQuestionCard(question: MathQuestionEntity) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
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
                    text = "${question.topic} • سطح: ${question.difficulty}",
                    fontSize = 11.sp,
                    color = AmberPrimary,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = question.questionText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = JadePrimary,
                lineHeight = 20.sp
            )

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = "۱) ${question.option1}   ۲) ${question.option2}\n۳) ${question.option3}   ۴) ${question.option4}",
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "گزینه صحیح: گزینه ${PersianUtils.toPersianDigits(question.correctOptionIndex + 1)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "پاسخ تشریحی: ${question.explanation}",
                                fontSize = 11.sp,
                                color = Color(0xFF1B5E20),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
