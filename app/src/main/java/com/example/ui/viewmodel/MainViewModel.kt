package com.example.ui.viewmodel

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.bank.BarsamQuestionBank
import com.example.data.local.BarsamDatabase
import com.example.data.local.entity.AppSettingsEntity
import com.example.data.local.entity.MathQuestionEntity
import com.example.data.local.entity.MistakeNoteEntity
import com.example.data.local.entity.QuizResultEntity
import com.example.data.local.entity.StudyTaskEntity
import com.example.data.local.entity.UserBadgeEntity
import com.example.data.remote.AiAssistantResult
import com.example.data.repository.GeminiAssistantRepository
import com.example.ui.navigation.Screen
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel @JvmOverloads constructor(
    application: Application,
    private val database: BarsamDatabase = BarsamDatabase.getInstance(application)
) : AndroidViewModel(application) {

    val soundManager = SoundManager(application)
    val aiRepository = GeminiAssistantRepository(application, database.settingsDao())

    // جهت‌یابی صفحات
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Dashboard)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    // جریان‌های داده دیتابیس
    val allQuestions = database.questionDao().getAllQuestions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BarsamQuestionBank.questions)

    val allQuizResults = database.quizDao().getAllResults()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMistakes = database.mistakeDao().getAllMistakes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unresolvedMistakes = database.mistakeDao().getUnresolvedMistakes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStudyTasks = database.studyTaskDao().getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBadges = database.badgeDao().getAllBadges()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appSettings = database.settingsDao().getSettings()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            AppSettingsEntity(id = 1, isSoundEnabled = true, isAiEnabled = false)
        )

    val averagePercentage = database.quizDao().getAveragePercentage()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalQuizzesCount = database.quizDao().getQuizCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalUnlockedPoints = database.badgeDao().getTotalUnlockedPoints()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        // همگام‌سازی اولیه تنظیم صدای برنامه با SoundManager
        viewModelScope.launch {
            appSettings.collect { settings ->
                settings?.let {
                    soundManager.isSoundEnabled = it.isSoundEnabled
                }
            }
        }
    }

    // ==========================================
    // مدیریت آزمون فعال (Quiz State) با تایمر پایدار
    // ==========================================
    private val _activeQuestions = MutableStateFlow<List<MathQuestionEntity>>(emptyList())
    val activeQuestions: StateFlow<List<MathQuestionEntity>> = _activeQuestions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _userAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap()) // اندیس سوال به گزینه انتخابی
    val userAnswers: StateFlow<Map<Int, Int>> = _userAnswers.asStateFlow()

    private val _remainingSeconds = MutableStateFlow(0)
    val remainingSeconds: StateFlow<Int> = _remainingSeconds.asStateFlow()

    private val _latestQuizResult = MutableStateFlow<QuizResultEntity?>(null)
    val latestQuizResult: StateFlow<QuizResultEntity?> = _latestQuizResult.asStateFlow()

    private var quizTimerJob: Job? = null
    private var quizEndSystemTime: Long = 0L
    private var activeQuizGrade: Int = 10
    private var activeQuizTopic: String = "همه مباحث"
    private var activeQuizDifficulty: String = "همه سطوح"
    private var totalQuizSeconds: Int = 600
    private var isQuizFinishing: Boolean = false

    suspend fun getTopicsForGrade(grade: Int): List<String> {
        return database.questionDao().getTopicsByGrade(grade)
    }

    suspend fun getAvailableQuizQuestionCount(grade: Int, topic: String?, difficulty: String?): Int {
        val effectiveTopic = if (topic == "همه مباحث" || topic.isNullOrBlank()) null else topic
        val effectiveDiff = if (difficulty == "همه سطوح" || difficulty.isNullOrBlank()) null else difficulty
        return database.questionDao().getQuizQuestionsCount(grade, effectiveTopic, effectiveDiff)
    }

    fun startCustomQuiz(
        grade: Int,
        topic: String?,
        difficulty: String?,
        count: Int,
        durationMinutes: Int
    ) {
        viewModelScope.launch {
            val effectiveTopic = if (topic == "همه مباحث" || topic.isNullOrBlank()) null else topic
            val effectiveDiff = if (difficulty == "همه سطوح" || difficulty.isNullOrBlank()) null else difficulty

            val questions = database.questionDao().getQuizQuestions(
                grade = grade,
                topic = effectiveTopic,
                difficulty = effectiveDiff,
                limit = count
            )

            if (questions.isEmpty()) {
                return@launch
            }

            isQuizFinishing = false
            _activeQuestions.value = questions
            _currentQuestionIndex.value = 0
            _userAnswers.value = emptyMap()
            activeQuizGrade = grade
            activeQuizTopic = topic ?: "همه مباحث"
            activeQuizDifficulty = difficulty ?: "همه سطوح"
            totalQuizSeconds = durationMinutes * 60

            // محاسبه زمان پایان بر اساس ساعت پیوسته سیستم جهت حفظ زمان در تغییر وضعیت
            quizEndSystemTime = SystemClock.elapsedRealtime() + (totalQuizSeconds * 1000L)
            _remainingSeconds.value = totalQuizSeconds

            startTimer()
            _currentScreen.value = Screen.QuizActive
        }
    }

    private fun startTimer() {
        quizTimerJob?.cancel()
        quizTimerJob = viewModelScope.launch {
            while (true) {
                val remainingMillis = quizEndSystemTime - SystemClock.elapsedRealtime()
                if (remainingMillis <= 0) {
                    _remainingSeconds.value = 0
                    finishActiveQuiz()
                    break
                }
                _remainingSeconds.value = (remainingMillis / 1000).toInt()
                delay(1000)
            }
        }
    }

    /**
     * انصراف قطعی از آزمون:
     * تایمر لغو می‌شود، وضعیت سوال‌ها و پاسخ‌ها پاک می‌شود، و هیچ نتیجه یا مدالی ثبت نمی‌شود.
     */
    fun cancelActiveQuiz() {
        quizTimerJob?.cancel()
        quizTimerJob = null
        isQuizFinishing = false
        _activeQuestions.value = emptyList()
        _userAnswers.value = emptyMap()
        _remainingSeconds.value = 0
        _currentQuestionIndex.value = 0
        _latestQuizResult.value = null
        _currentScreen.value = Screen.Dashboard
    }

    fun selectQuizAnswer(questionIndex: Int, optionIndex: Int) {
        val currentAnswers = _userAnswers.value.toMutableMap()
        currentAnswers[questionIndex] = optionIndex
        _userAnswers.value = currentAnswers
        // بدون پخش صدای درست/غلط در جریان آزمون برای جلوگیری از لو رفتن پاسخ و تقلب
    }

    fun clearQuizAnswer(questionIndex: Int) {
        val currentAnswers = _userAnswers.value.toMutableMap()
        currentAnswers.remove(questionIndex)
        _userAnswers.value = currentAnswers
    }

    fun nextQuestion() {
        if (_currentQuestionIndex.value < _activeQuestions.value.size - 1) {
            _currentQuestionIndex.value += 1
        }
    }

    fun prevQuestion() {
        if (_currentQuestionIndex.value > 0) {
            _currentQuestionIndex.value -= 1
        }
    }

    fun goToQuestion(index: Int) {
        if (index in _activeQuestions.value.indices) {
            _currentQuestionIndex.value = index
        }
    }

    fun finishActiveQuiz() {
        if (isQuizFinishing) return
        isQuizFinishing = true
        quizTimerJob?.cancel()
        quizTimerJob = null

        val questions = _activeQuestions.value
        val answers = _userAnswers.value
        var correctCount = 0
        var wrongCount = 0

        questions.forEachIndexed { index, q ->
            val chosen = answers[index]
            if (chosen != null) {
                if (chosen == q.correctOptionIndex) {
                    correctCount++
                } else {
                    wrongCount++
                }
            }
        }
        val unansweredCount = questions.size - (correctCount + wrongCount)
        val spentSeconds = totalQuizSeconds - _remainingSeconds.value
        val answeredCount = correctCount + wrongCount

        // در صورتی که کاربر به هیچ سوالی پاسخ نداده باشد (آزمون کاملاً سفید):
        // نتیجه‌ای در دیتابیس ذخیره نمی‌شود تا میانگین درصدها و آمار تخریب نشود.
        // وضعیت آزمون فعال پاک شده و کاربر به صفحه داشبورد بازمی‌گردد.
        if (answeredCount == 0) {
            isQuizFinishing = false
            _activeQuestions.value = emptyList()
            _userAnswers.value = emptyMap()
            _remainingSeconds.value = 0
            _currentQuestionIndex.value = 0
            _latestQuizResult.value = null
            _currentScreen.value = Screen.Dashboard
            return
        }

        // محاسبه استاندارد درصد با احتساب یک‌سوم نمره منفی کنکور
        val percentage = if (questions.isNotEmpty()) {
            val raw = ((correctCount * 3.0 - wrongCount) / (questions.size * 3.0)) * 100.0
            maxOf(0.0, raw)
        } else {
            0.0
        }

        val resultEntity = QuizResultEntity(
            grade = activeQuizGrade,
            topic = activeQuizTopic,
            difficulty = activeQuizDifficulty,
            totalQuestions = questions.size,
            correctCount = correctCount,
            wrongCount = wrongCount,
            unansweredCount = unansweredCount,
            percentage = Math.round(percentage * 10.0) / 10.0,
            durationSeconds = maxOf(1, spentSeconds)
        )

        viewModelScope.launch {
            val resultId = database.quizDao().insertQuizResult(resultEntity)
            _latestQuizResult.value = resultEntity.copy(id = resultId)

            checkAndAwardBadges(correctCount, wrongCount, percentage, activeQuizDifficulty)

            // افزودن امتیاز عملکرد واقعی به حساب کاربر
            val earnedScore = (correctCount * 10) + if (percentage >= 80.0) 30 else 10
            database.settingsDao().addScore(earnedScore)

            _currentScreen.value = Screen.QuizResult
        }
    }

    private suspend fun checkAndAwardBadges(
        correct: Int,
        wrong: Int,
        percentage: Double,
        difficulty: String
    ) {
        // ۱. مدال اولین آزمون
        unlockBadgeWithPoints("FIRST_QUIZ")

        // ۲. مدال درصد ۱۰۰
        if (percentage >= 99.9) {
            unlockBadgeWithPoints("PERFECT_SCORE")
        }

        // ۳. مدال قهرمان کنکور
        if (difficulty == "کنکوری" && percentage >= 50.0) {
            unlockBadgeWithPoints("KONKUR_HERO")
        }
    }

    /**
     * باز کردن مدال به صورت اتمیک و دریافت امتیاز مستقیماً از موجودیت مدال (منبع واحد حقیقت)
     */
    private suspend fun unlockBadgeWithPoints(badgeKey: String) {
        val badgeDao = database.badgeDao()
        if (badgeDao.safeUnlockBadge(badgeKey) > 0) {
            val badge = badgeDao.getBadgeByKey(badgeKey)
            if (badge != null && badge.points > 0) {
                database.settingsDao().addScore(badge.points)
            }
        }
    }

    // ==========================================
    // دفترچه اشتباهات (Mistakes Notebook)
    // ==========================================
    fun saveMistake(
        question: MathQuestionEntity,
        userChosenOptionIndex: Int,
        mistakeReason: String,
        userNote: String
    ) {
        viewModelScope.launch {
            val chosenText = when (userChosenOptionIndex) {
                0 -> question.option1
                1 -> question.option2
                2 -> question.option3
                3 -> question.option4
                else -> "بدون پاسخ"
            }
            val correctText = when (question.correctOptionIndex) {
                0 -> question.option1
                1 -> question.option2
                2 -> question.option3
                else -> question.option4
            }

            database.mistakeDao().insertMistake(
                MistakeNoteEntity(
                    questionId = question.id,
                    grade = question.grade,
                    topic = question.topic,
                    questionText = question.questionText,
                    correctOption = correctText,
                    userChosenOption = chosenText,
                    explanation = question.explanation,
                    mistakeReason = mistakeReason,
                    userNote = userNote
                )
            )
        }
    }

    fun resolveMistake(mistakeId: Long) {
        viewModelScope.launch {
            database.mistakeDao().updateResolvedStatus(mistakeId, true)
            if (database.badgeDao().safeUnlockBadge("RESOLVE_MISTAKES") > 0) {
                database.settingsDao().addScore(80)
            }
        }
    }

    fun deleteMistake(mistakeId: Long) {
        viewModelScope.launch {
            database.mistakeDao().deleteMistakeById(mistakeId)
        }
    }

    // ==========================================
    // برنامه مطالعه (Study Plan)
    // ==========================================
    fun addStudyTask(title: String, topic: String, grade: Int, targetMinutes: Int, dateString: String) {
        viewModelScope.launch {
            database.studyTaskDao().insertTask(
                StudyTaskEntity(
                    title = title,
                    topic = topic,
                    grade = grade,
                    targetMinutes = targetMinutes,
                    dateString = dateString
                )
            )
        }
    }

    fun toggleStudyTask(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            database.studyTaskDao().updateCompletion(taskId, isCompleted)
        }
    }

    fun deleteStudyTask(taskId: Long) {
        viewModelScope.launch {
            database.studyTaskDao().deleteTask(taskId)
        }
    }

    // ==========================================
    // تنظیمات (Settings)
    // ==========================================
    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            database.settingsDao().setSoundEnabled(enabled)
            soundManager.isSoundEnabled = enabled
        }
    }

    fun setAiEnabled(enabled: Boolean) {
        viewModelScope.launch {
            database.settingsDao().setAiEnabled(enabled)
        }
    }

    // ==========================================
    // چت و تعامل با دستیار هوشمند Gemini
    // ==========================================
    data class ChatMessage(val isUser: Boolean, val text: String, val modelUsed: String? = null)

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                isUser = false,
                text = "سلام! من دستیار درسی برسام هستم. آماده‌ام به سوالات ریاضی پایه‌های دهم، یازدهم و دوازدهم پاسخ بدم و مسائل رو گام‌به‌گام برات تحلیل کنم."
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    fun askAiAssistant(questionText: String, isComplexMath: Boolean = false) {
        if (questionText.isBlank() || _isAiLoading.value) return

        val userMsg = ChatMessage(isUser = true, text = questionText)
        _chatMessages.value = _chatMessages.value + userMsg
        _isAiLoading.value = true

        viewModelScope.launch {
            val result = aiRepository.askMathAssistant(questionText, isComplexMath)
            _isAiLoading.value = false

            val replyMsg = when (result) {
                is AiAssistantResult.Success -> ChatMessage(
                    isUser = false,
                    text = result.reply,
                    modelUsed = result.modelUsed
                )
                is AiAssistantResult.Disabled -> ChatMessage(
                    isUser = false,
                    text = "🔒 ${result.message}\n\n(می‌توانی از منوی تنظیمات بالا، اتصال هوش مصنوعی را روشن کنی تا پاسخ تشریحی دریافت نمایی.)"
                )
                is AiAssistantResult.Offline -> ChatMessage(
                    isUser = false,
                    text = "📡 ${result.message}\n\nنکته: تمرین‌ها، فرمول‌ها و کارنامه‌های آزمون بدون نیاز به اینترنت فعال هستند."
                )
                is AiAssistantResult.Error -> ChatMessage(
                    isUser = false,
                    text = "⚠️ ${result.userMessage}"
                )
            }
            _chatMessages.value = _chatMessages.value + replyMsg
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
        quizTimerJob?.cancel()
    }
}
