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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.local.entity.MistakeNoteEntity
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.JadePrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.MainViewModel
import com.example.util.PersianUtils

@Composable
fun MistakesScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val allMistakes by viewModel.allMistakes.collectAsState()
    var selectedFilter by remember { mutableStateOf("در انتظار مرور") }

    val filteredMistakes = when (selectedFilter) {
        "در انتظار مرور" -> allMistakes.filter { !it.isResolved }
        "حل‌شده" -> allMistakes.filter { it.isResolved }
        else -> allMistakes
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFBE9E7))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "دفترچه اشتباهات هوشمند",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD84315)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "تحلیل تست‌هایی که در آزمون‌ها اشتباه پاسخ داده‌اید، موثرترین ابزار رشد درصد کنکور است.",
                        fontSize = 12.sp,
                        color = Color(0xFFBF360C),
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // فیلترها
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("در انتظار مرور", "حل‌شده", "همه اشتباهات").forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = JadePrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        if (filteredMistakes.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (selectedFilter == "در انتظار مرور") "هیچ اشتباه رفع‌نشده‌ای ندارید! آفرین!" else "موردی یافت نشد.",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = JadePrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "پس از شرکت در هر آزمون، تست‌های نادرست را با یک لمس به این دفترچه منتقل کنید.",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        } else {
            items(filteredMistakes, key = { it.id }) { mistake ->
                MistakeCard(
                    mistake = mistake,
                    onToggleResolved = { viewModel.resolveMistake(mistake.id) },
                    onDelete = { viewModel.deleteMistake(mistake.id) }
                )
            }
        }
    }
}

@Composable
fun MistakeCard(
    mistake: MistakeNoteEntity,
    onToggleResolved: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (mistake.isResolved) Color(0xFFF1F8F6) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AmberContainer),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = mistake.mistakeReason,
                            fontSize = 10.sp,
                            color = AmberPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "پایه ${PersianUtils.toPersianDigits(mistake.grade)} • ${mistake.topic}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.LightGray)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            MathText(
                text = mistake.questionText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row {
                MathText(
                    text = "گزینه غلط شما: ${mistake.userChosenOption}",
                    fontSize = 11.sp,
                    color = ErrorRed
                )
                Spacer(modifier = Modifier.width(12.dp))
                MathText(
                    text = "پاسخ صحیح: ${mistake.correctOption}",
                    fontSize = 11.sp,
                    color = SuccessGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            if (mistake.userNote.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "یادداشت شما: ${mistake.userNote}",
                    fontSize = 11.sp,
                    color = JadePrimary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            MathText(
                text = "پاسخ تشریحی: ${mistake.explanation}",
                fontSize = 10.sp,
                color = Color.DarkGray,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (mistake.isResolved) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("رفع اشکال شده", fontSize = 11.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
                    }
                } else {
                    TextButton(onClick = onToggleResolved) {
                        Icon(Icons.Default.RadioButtonUnchecked, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("علامت‌گذاری به عنوان رفع اشکال شده", fontSize = 11.sp, color = JadePrimary)
                    }
                }
            }
        }
    }
}
