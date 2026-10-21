package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.StudyTaskEntity
import com.example.ui.navigation.Screen
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.JadeContainer
import com.example.ui.theme.JadePrimary
import com.example.ui.theme.OnJadeContainer
import com.example.ui.viewmodel.MainViewModel
import com.example.util.PersianUtils

@Composable
fun StudyPlanScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val tasks by viewModel.allStudyTasks.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val completedCount = tasks.count { it.isCompleted }
    val progress = if (tasks.isNotEmpty()) completedCount.toFloat() / tasks.size else 0f

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // کارت خلاصه پیشرفت مطالعه
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = JadeContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "پیشرفت برنامه‌ریزی مطالعه",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnJadeContainer
                            )
                            Text(
                                text = "${PersianUtils.toPersianDigits(completedCount)} از ${PersianUtils.toPersianDigits(tasks.size)} تسک",
                                fontSize = 12.sp,
                                color = OnJadeContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = JadePrimary,
                            trackColor = Color.White.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            if (tasks.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.height(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "هنوز برنامه‌ای برای مطالعه ثبت نکرده‌اید.",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "با زدن دکمه + مباحث ریاضی، فصول کنکور و دقایق مطالعه روزانه خود را اضافه کنید.",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            } else {
                items(tasks, key = { it.id }) { task ->
                    StudyTaskItem(
                        task = task,
                        onToggle = { isChecked -> viewModel.toggleStudyTask(task.id, isChecked) },
                        onDelete = { viewModel.deleteStudyTask(task.id) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(72.dp)) }
        }

        // دکمه شناور افزودن تسک
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = AmberPrimary,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "افزودن برنامه مطالعه")
        }
    }

    if (showAddDialog) {
        AddStudyTaskDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, topic, grade, minutes, date ->
                viewModel.addStudyTask(title, topic, grade, minutes, date)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun StudyTaskItem(
    task: StudyTaskEntity,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) Color(0xFFF1F8F6) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onToggle(!task.isCompleted) }) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (task.isCompleted) JadePrimary else Color.Gray
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (task.isCompleted) Color.Gray else JadePrimary,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row {
                    Text(
                        text = "پایه ${PersianUtils.toPersianDigits(task.grade)} • ${task.topic}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "هدف: ${PersianUtils.toPersianDigits(task.targetMinutes)} دقیقه",
                        fontSize = 11.sp,
                        color = AmberPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "حذف",
                    tint = Color.LightGray
                )
            }
        }
    }
}

@Composable
fun AddStudyTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, topic: String, grade: Int, targetMinutes: Int, date: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf("تابع") }
    var grade by remember { mutableStateOf(12) }
    var targetMinutes by remember { mutableStateOf("۴۵") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("افزودن برنامه مطالعه ریاضی", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان برنامه (مثلا: تست‌های مشتق)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = topic,
                    onValueChange = { topic = it },
                    label = { Text("مبحث ریاضی (مثلا: مثلثات)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("پایه تحصیلی:", fontSize = 13.sp)
                    Row {
                        listOf(10, 11, 12).forEach { g ->
                            TextButton(
                                onClick = { grade = g },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = if (grade == g) JadePrimary else Color.Gray
                                )
                            ) {
                                Text(
                                    text = "پایه ${PersianUtils.toPersianDigits(g)}",
                                    fontWeight = if (grade == g) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
                OutlinedTextField(
                    value = targetMinutes,
                    onValueChange = { targetMinutes = it },
                    label = { Text("زمان هدف (دقیقه)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val mins = targetMinutes.toIntOrNull() ?: 45
                    if (title.isNotBlank()) {
                        onConfirm(title, topic, grade, mins, "امروز")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = JadePrimary)
            ) {
                Text("ثبت برنامه", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("انصراف", color = Color.Gray)
            }
        }
    )
}
