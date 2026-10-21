package com.example.data.bank

import com.example.data.local.entity.MathQuestionEntity

/**
 * بانک ۶۰ سوال استاندارد و تاییدشده ریاضی برای پایه‌های دهم، یازدهم و دوازدهم
 * توزیع دقیق: ۵ سوال در هر یک از ۴ سطح (آسان، متوسط، سخت، کنکوری) به ازای هر پایه
 */
object BarsamQuestionBank {

    val questions: List<MathQuestionEntity> = listOf(
        // ==========================================
        // پایه دهم (Grade 10) - ۲۰ سوال
        // ==========================================

        // --- آسان (Easy) ---
        MathQuestionEntity(
            id = "G10_EASY_01",
            grade = 10,
            difficulty = "آسان",
            topic = "مجموعه، الگو و دنباله",
            questionText = "در یک دنباله حسابی جملات اول و دوم به ترتیب ۳ و ۷ هستند. جمله پنجم کدام است؟",
            option1 = "۱۵",
            option2 = "۱۷",
            option3 = "۱۹",
            option4 = "۲۱",
            correctOptionIndex = 2,
            explanation = "قدرنسبت دنباله: d = ۷ - ۳ = ۴ است. جمله عمومی دنباله حسابی: a_n = a_1 + (n - 1)d است. بنابراین جمله پنجم: a_5 = ۳ + ۴ × ۴ = ۳ + ۱۶ = ۱۹ می‌باشد."
        ),
        MathQuestionEntity(
            id = "G10_EASY_02",
            grade = 10,
            difficulty = "آسان",
            topic = "مثلثات",
            questionText = "مقدار عددی عبارت sin(30°) + cos(60°) کدام است؟",
            option1 = "۰",
            option2 = "۱/۲",
            option3 = "۱",
            option4 = "√۳",
            correctOptionIndex = 2,
            explanation = "می‌دانیم sin(30°) = ۱/۲ و cos(60°) = ۱/۲ است. حاصل جمع: ۱/۲ + ۱/۲ = ۱ خواهد بود."
        ),
        MathQuestionEntity(
            id = "G10_EASY_03",
            grade = 10,
            difficulty = "آسان",
            topic = "معادله درجه دوم",
            questionText = "ریشه‌های معادله x² - 5x + 6 = 0 کدامند؟",
            option1 = "۲ و ۳",
            option2 = "-۲ و -۳",
            option3 = "۱ و ۶",
            option4 = "-۱ و -۶",
            correctOptionIndex = 0,
            explanation = "با استفاده از تجزیه به روش جمله مشترک: (x - 2)(x - 3) = 0 حاصل می‌شود. ریشه‌ها x = 2 و x = 3 هستند."
        ),
        MathQuestionEntity(
            id = "G10_EASY_04",
            grade = 10,
            difficulty = "آسان",
            topic = "توان‌های گویا",
            questionText = "حاصل عبارت (8)^(2/3) کدام است؟",
            option1 = "۲",
            option2 = "۴",
            option3 = "۸",
            option4 = "۱۶",
            correctOptionIndex = 1,
            explanation = "عدد ۸ برابر با ۲ به توان ۳ است: (2³)^(2/3) = 2^(3 × 2/3) = 2² = ۴."
        ),
        MathQuestionEntity(
            id = "G10_EASY_05",
            grade = 10,
            difficulty = "آسان",
            topic = "تابع",
            questionText = "اگر f(x) = 2x - 3 باشد، مقدار f(4) کدام است؟",
            option1 = "۳",
            option2 = "۵",
            option3 = "۷",
            option4 = "۹",
            correctOptionIndex = 1,
            explanation = "با قرار دادن ۴ به جای x داریم: f(4) = ۲(۴) - ۳ = ۸ - ۳ = ۵."
        ),

        // --- متوسط (Medium) ---
        MathQuestionEntity(
            id = "G10_MED_01",
            grade = 10,
            difficulty = "متوسط",
            topic = "مجموعه، الگو و دنباله",
            questionText = "در دنباله هندسی ..., 2, 6, 18 مجموع ۵ جمله اول کدام است؟",
            option1 = "۱۶۲",
            option2 = "۲۴۲",
            option3 = "۴۸۶",
            option4 = "۷۲۸",
            correctOptionIndex = 1,
            explanation = "جمله اول a = 2 و قدرنسبت q = 3 است. فرمول مجموع: S_n = a(q^n - 1) / (q - 1). داریم: S_5 = 2(3^5 - 1) / (3 - 1) = 2(243 - 1) / 2 = 242."
        ),
        MathQuestionEntity(
            id = "G10_MED_02",
            grade = 10,
            difficulty = "متوسط",
            topic = "مثلثات",
            questionText = "اگر tan(x) = 3/4 و انتهای کمان x در ربع اول باشد، مقدار cos(x) کدام است؟",
            option1 = "۳/۵",
            option2 = "۴/۵",
            option3 = "۵/۴",
            option4 = "۱/۲",
            correctOptionIndex = 1,
            explanation = "در مثلث قائم‌الزاویه با اضلاع ۳ و ۴، اندازه وتر ۵ است. کسینوس برابر ضلع مجاور تقسیم بر وتر است: cos(x) = ۴/۵."
        ),
        MathQuestionEntity(
            id = "G10_MED_03",
            grade = 10,
            difficulty = "متوسط",
            topic = "معادله و نامعادله",
            questionText = "مجموعه جواب نامعادله |2x - 1| < 5 کدام بازه است؟",
            option1 = "(-3, 2)",
            option2 = "(-2, 3)",
            option3 = "(1, 5)",
            option4 = "(-5, 5)",
            correctOptionIndex = 1,
            explanation = "نامعادله قدر مطلقی معادل است با: -5 < 2x - 1 < 5. با افزودن ۱ به طرفین: -4 < 2x < 6 و با تقسیم بر ۲: -2 < x < 3 به دست می‌آید که بازه (-2, 3) است."
        ),
        MathQuestionEntity(
            id = "G10_MED_04",
            grade = 10,
            difficulty = "متوسط",
            topic = "تابع",
            questionText = "دامنه تابع f(x) = √(2x - 6) کدام است؟",
            option1 = "[3, +∞)",
            option2 = "(3, +∞)",
            option3 = "(-∞, 3]",
            option4 = "[0, +∞)",
            correctOptionIndex = 0,
            explanation = "عبارت زیر رادیکال با فرجه زوج باید بزرگتر یا مساوی صفر باشد: 2x - 6 ≥ 0 => 2x ≥ 6 => x ≥ 3. بنابراین دامنه [3, +∞) است."
        ),
        MathQuestionEntity(
            id = "G10_MED_05",
            grade = 10,
            difficulty = "متوسط",
            topic = "شمارش و احتمال",
            questionText = "تاس سالمی را پرتاب می‌کنیم. احتمال اینکه عدد رو شده عددی اول باشد کدام است؟",
            option1 = "۱/۶",
            option2 = "۱/۳",
            option3 = "۱/۲",
            option4 = "۲/۳",
            correctOptionIndex = 2,
            explanation = "فضای نمونه ۶ حالت دارد: {1, 2, 3, 4, 5, 6}. پیشامد اعداد اول: {2, 3, 5} شامل ۳ عضو است. احتمال = ۳/۶ = ۱/۲ است."
        ),

        // --- سخت (Hard) ---
        MathQuestionEntity(
            id = "G10_HARD_01",
            grade = 10,
            difficulty = "سخت",
            topic = "معادله درجه دوم",
            questionText = "به ازای کدام مقدار m، معادله x² - 4x + m = 0 دارای ریشه مضاعف است؟",
            option1 = "۲",
            option2 = "۴",
            option3 = "۸",
            option4 = "۱۶",
            correctOptionIndex = 1,
            explanation = "شرط ریشه مضاعف برابر صفر بودن دلتا (Δ = 0) است. Δ = b² - 4ac = (-4)² - 4(1)(m) = 16 - 4m = 0 => 4m = 16 => m = 4."
        ),
        MathQuestionEntity(
            id = "G10_HARD_02",
            grade = 10,
            difficulty = "سخت",
            topic = "مثلثات",
            questionText = "حاصل ساده‌شده عبارت (sin(x) + cos(x))² - 2sin(x)cos(x) همواره برابر کدام است؟",
            option1 = "۰",
            option2 = "۱",
            option3 = "2sin(x)",
            option4 = "cos(2x)",
            correctOptionIndex = 1,
            explanation = "اتحاد مربع دو جمله‌ای را باز می‌کنیم: sin²(x) + cos²(x) + 2sin(x)cos(x) - 2sin(x)cos(x). دو جمله قرینه ساده شده و باقی‌مانده sin²(x) + cos²(x) = ۱ خواهد بود."
        ),
        MathQuestionEntity(
            id = "G10_HARD_03",
            grade = 10,
            difficulty = "سخت",
            topic = "مجموعه، الگو و دنباله",
            questionText = "در یک دنباله حسابی a_3 = 11 و a_7 = 23 است. مجموع ۱۰ جمله اول دنباله کدام است؟",
            option1 = "۱۶۵",
            option2 = "۱۷۵",
            option3 = "۱۸۵",
            option4 = "۱۹۵",
            correctOptionIndex = 2,
            explanation = "تفاضل جملات: a_7 - a_3 = 4d => 23 - 11 = 12 => d = 3. جمله اول: a_1 = a_3 - 2d = 11 - 6 = 5. مجموع ۱۰ جمله: S_10 = (10/2)[2(5) + 9(3)] = 5[10 + 27] = 5 × 37 = 185."
        ),
        MathQuestionEntity(
            id = "G10_HARD_04",
            grade = 10,
            difficulty = "سخت",
            topic = "تابع",
            questionText = "اگر رأس سهمی y = x² - 6x + c بر روی محور xها قرار داشته باشد، مقدار c کدام است؟",
            option1 = "۳",
            option2 = "۶",
            option3 = "۹",
            option4 = "-۹",
            correctOptionIndex = 2,
            explanation = "طول رأس سهمی: x_s = -b/(2a) = 6/2 = 3. عرض رأس سهمی باید صفر باشد: y(3) = 3² - 6(3) + c = 0 => 9 - 18 + c = 0 => c = 9."
        ),
        MathQuestionEntity(
            id = "G10_HARD_05",
            grade = 10,
            difficulty = "سخت",
            topic = "شمارش و احتمال",
            questionText = "با ارقام ۱، ۲، ۳، ۴، ۵ چند عدد سه رقمی بدون تکرار ارقام می‌توان ساخت؟",
            option1 = "۲۰",
            option2 = "۶۰",
            option3 = "۱۲۰",
            option4 = "۱۲۵",
            correctOptionIndex = 1,
            explanation = "تعداد کل از اصل ضرب: برای صدگان ۵ انتخاب، دهگان ۴ انتخاب و یکان ۳ انتخاب داریم: ۵ × ۴ × ۳ = ۶۰ عدد."
        ),

        // --- کنکوری (Konkuri) ---
        MathQuestionEntity(
            id = "G10_KONK_01",
            grade = 10,
            difficulty = "کنکوری",
            topic = "معادله درجه دوم",
            questionText = "اگر α و β ریشه‌های معادله 2x² - 3x - 1 = 0 باشند، حاصل 1/α + 1/β کدام است؟",
            option1 = "۳",
            option2 = "-۳",
            option3 = "۲/۳",
            option4 = "-۱/۳",
            correctOptionIndex = 1,
            explanation = "مجموع ریشه‌ها S = α + β = -b/a = 3/2 و حاصل‌ضرب ریشه‌ها P = αβ = c/a = -1/2 است. حاصل عبارت: 1/α + 1/β = (α + β)/(αβ) = (3/2) / (-1/2) = -3."
        ),
        MathQuestionEntity(
            id = "G10_KONK_02",
            grade = 10,
            difficulty = "کنکوری",
            topic = "مثلثات",
            questionText = "اگر sin(x) - cos(x) = 1/2 باشد، مقدار عددی sin(2x) کدام است؟",
            option1 = "۱/۴",
            option2 = "۱/۲",
            option3 = "۳/۴",
            option4 = "۳/۸",
            correctOptionIndex = 2,
            explanation = "طرفین را به توان دو می‌رسانیم: (sin x - cos x)² = (1/2)² => sin²x + cos²x - 2sinx cosx = 1/4 => 1 - sin(2x) = 1/4 => sin(2x) = 1 - 1/4 = 3/4."
        ),
        MathQuestionEntity(
            id = "G10_KONK_03",
            grade = 10,
            difficulty = "کنکوری",
            topic = "تابع",
            questionText = "اگر f(2x + 1) = 4x² + 2x + 1 باشد، ضابطه f(x) کدام است؟",
            option1 = "x² + x + 1",
            option2 = "x² - x + 1",
            option3 = "x² - 2x + 1",
            option4 = "2x² - x + 1",
            correctOptionIndex = 1,
            explanation = "متغیر تغییر می‌دهیم: t = 2x + 1 => x = (t - 1)/2. در نتیجه: f(t) = 4((t - 1)/2)² + 2((t - 1)/2) + 1 = (t - 1)² + (t - 1) + 1 = t² - 2t + 1 + t - 1 + 1 = t² - t + 1. پس f(x) = x² - x + 1 است."
        ),
        MathQuestionEntity(
            id = "G10_KONK_04",
            grade = 10,
            difficulty = "کنکوری",
            topic = "مجموعه، الگو و دنباله",
            questionText = "در یک دنباله هندسی نزولی نامتناهی، جمله اول برابر ۶ و مجموع نامتناهی جملات برابر ۱۰ است. قدرنسبت دنباله کدام است؟",
            option1 = "۱/۵",
            option2 = "۲/۵",
            option3 = "۳/۵",
            option4 = "۴/۵",
            correctOptionIndex = 1,
            explanation = "فرمول مجموع حد نامتناهی دنباله هندسی S = a / (1 - q) است: 10 = 6 / (1 - q) => 1 - q = 6/10 = 3/5 => q = 1 - 3/5 = 2/5."
        ),
        MathQuestionEntity(
            id = "G10_KONK_05",
            grade = 10,
            difficulty = "کنکوری",
            topic = "شمارش و احتمال",
            questionText = "از کیسه‌ای حاوی ۴ مهره سفید و ۶ مهره سیاه، ۲ مهره همزمان و به تصادف خارج می‌کنیم. احتمال همرنگ بودن دو مهره کدام است؟",
            option1 = "۷/۱۵",
            option2 = "۸/۱۵",
            option3 = "۴/۱۵",
            option4 = "۲/۵",
            correctOptionIndex = 0,
            explanation = "کل حالات انتخابی: C(10, 2) = 45. حالات هر دو سفید: C(4, 2) = 6. حالات هر دو سیاه: C(6, 2) = 15. مجموع حالات مطلوب = 6 + 15 = 21. احتمال = 21/45 = 7/15."
        ),

        // ==========================================
        // پایه یازدهم (Grade 11) - ۲۰ سوال
        // ==========================================

        // --- آسان (Easy) ---
        MathQuestionEntity(
            id = "G11_EASY_01",
            grade = 11,
            difficulty = "آسان",
            topic = "هندسه تحلیلی و جبر",
            questionText = "فاصله دو نقطه A(1, 2) و B(4, 6) در صفحه مختصات کدام است؟",
            option1 = "۳",
            option2 = "۴",
            option3 = "۵",
            option4 = "۷",
            correctOptionIndex = 2,
            explanation = "فاصله دو نقطه: AB = √((x2 - x1)² + (y2 - y1)²) = √((4 - 1)² + (6 - 2)²) = √(3² + 4²) = √(9 + 16) = √25 = 5."
        ),
        MathQuestionEntity(
            id = "G11_EASY_02",
            grade = 11,
            difficulty = "آسان",
            topic = "توابع نمایی و لگاریتمی",
            questionText = "حاصل عبارت log₂(32) کدام است؟",
            option1 = "۳",
            option2 = "۴",
            option3 = "۵",
            option4 = "۶",
            correctOptionIndex = 2,
            explanation = "عدد ۳۲ را به صورت توان ۲ می‌نویسیم: ۳۲ = ۲^۵. بنابراین: log₂(2⁵) = 5."
        ),
        MathQuestionEntity(
            id = "G11_EASY_03",
            grade = 11,
            difficulty = "آسان",
            topic = "تابع",
            questionText = "ضابطه وارون تابع f(x) = 3x - 6 کدام است؟",
            option1 = "(x - 6)/3",
            option2 = "(x + 6)/3",
            option3 = "3x + 6",
            option4 = "(x + 2)/3",
            correctOptionIndex = 1,
            explanation = "y = 3x - 6 => y + 6 = 3x => x = (y + 6)/3. با جابجایی نمادها: f⁻¹(x) = (x + 6)/3 حاصل می‌شود."
        ),
        MathQuestionEntity(
            id = "G11_EASY_04",
            grade = 11,
            difficulty = "آسان",
            topic = "مثلثات",
            questionText = "زاویه ۱۳۵ درجه معادل چند رادیان است؟",
            option1 = "π/4",
            option2 = "2π/3",
            option3 = "3π/4",
            option4 = "5π/6",
            correctOptionIndex = 2,
            explanation = "تبدیل درجه به رادیان: R = D × (π / 180) => R = 135 × (π / 180) = 3π/4 رادیان."
        ),
        MathQuestionEntity(
            id = "G11_EASY_05",
            grade = 11,
            difficulty = "آسان",
            topic = "حد و پیوستگی",
            questionText = "حاصل حد lim(x→2) [x² + 3x - 2] کدام است؟",
            option1 = "۶",
            option2 = "۸",
            option3 = "۱۰",
            option4 = "۱۲",
            correctOptionIndex = 1,
            explanation = "با جاگذاری مستقیم مقدار ۲ به جای x: 2² + 3(2) - 2 = 4 + 6 - 2 = 8 حاصل می‌شود."
        ),

        // --- متوسط (Medium) ---
        MathQuestionEntity(
            id = "G11_MED_01",
            grade = 11,
            difficulty = "متوسط",
            topic = "تابع",
            questionText = "اگر f(x) = 2x + 1 و g(x) = x² - 1 باشد، مقدار g(f(2)) کدام است؟",
            option1 = "۹",
            option2 = "۱۵",
            option3 = "۲۴",
            option4 = "۲۵",
            correctOptionIndex = 2,
            explanation = "ابتدا f(2) را حساب می‌کنیم: f(2) = 2(2) + 1 = 5. سپس: g(5) = 5² - 1 = 25 - 1 = 24 به دست می‌آید."
        ),
        MathQuestionEntity(
            id = "G11_MED_02",
            grade = 11,
            difficulty = "متوسط",
            topic = "حد و پیوستگی",
            questionText = "حاصل حد مبهم lim(x→3) [(x² - 9)/(x - 3)] کدام است؟",
            option1 = "۰",
            option2 = "۳",
            option3 = "۶",
            option4 = "وجود ندارد",
            correctOptionIndex = 2,
            explanation = "صورت کسر اتحاد مزدوج است: (x - 3)(x + 3)/(x - 3) = x + 3. با میل کردن x به سمت ۳، حاصل ۳ + ۳ = ۶ می‌شود."
        ),
        MathQuestionEntity(
            id = "G11_MED_03",
            grade = 11,
            difficulty = "متوسط",
            topic = "توابع نمایی و لگاریتمی",
            questionText = "جواب معادله log(x) + log(x - 3) = 1 (در مبنای ۱۰) کدام است؟",
            option1 = "۵",
            option2 = "-۲",
            option3 = "۵ و -۲",
            option4 = "۱۰",
            correctOptionIndex = 0,
            explanation = "log(x(x - 3)) = 1 => x² - 3x = 10 => x² - 3x - 10 = 0 => (x - 5)(x + 2) = 0. با توجه به شرط دامنه لگاریتم (x > 3)، تنها جواب x = 5 قابل قبول است."
        ),
        MathQuestionEntity(
            id = "G11_MED_04",
            grade = 11,
            difficulty = "متوسط",
            topic = "مثلثات",
            questionText = "اتحاد cos(2x) بر حسب sin(x) برابر کدام گزینه است؟",
            option1 = "1 - 2sin²(x)",
            option2 = "2sin²(x) - 1",
            option3 = "1 - sin²(x)",
            option4 = "2sin(x)cos(x)",
            correctOptionIndex = 0,
            explanation = "بر اساس فرمول‌های کمان دوبرابر: cos(2x) = cos²(x) - sin²(x) = (1 - sin²(x)) - sin²(x) = 1 - 2sin²(x)."
        ),
        MathQuestionEntity(
            id = "G11_MED_05",
            grade = 11,
            difficulty = "متوسط",
            topic = "آمار و احتمال",
            questionText = "واریانس مجموعه‌ای از ۵ داده یکسان (۵، ۵، ۵، ۵، ۵) کدام است؟",
            option1 = "۰",
            option2 = "۱",
            option3 = "۵",
            option4 = "۲۵",
            correctOptionIndex = 0,
            explanation = "چون تمام داده‌ها دقیقاً با میانگین برابرند، انحراف هر داده از میانگین صفر بوده و واریانس دقیقاً برابر با صفر است."
        ),

        // --- سخت (Hard) ---
        MathQuestionEntity(
            id = "G11_HARD_01",
            grade = 11,
            difficulty = "سخت",
            topic = "هندسه تحلیلی و جبر",
            questionText = "فاصله نقطه A(2, -1) از خط 3x + 4y - 12 = 0 کدام است؟",
            option1 = "۱",
            option2 = "۲",
            option3 = "۳",
            option4 = "۴",
            correctOptionIndex = 1,
            explanation = "فرمول فاصله نقطه از خط: d = |ax0 + by0 + c| / √(a² + b²) = |3(2) + 4(-1) - 12| / √(3² + 4²) = |6 - 4 - 12| / 5 = |-10| / 5 = 10 / 5 = 2."
        ),
        MathQuestionEntity(
            id = "G11_HARD_02",
            grade = 11,
            difficulty = "سخت",
            topic = "حد و پیوستگی",
            questionText = "اگر تابع f(x) به صورت (x² - 4)/(x - 2) برای x ≠ 2 و f(2) = a در نقطه x = 2 پیوسته باشد، مقدار a کدام است؟",
            option1 = "۲",
            option2 = "۴",
            option3 = "۰",
            option4 = "-۲",
            correctOptionIndex = 1,
            explanation = "حد تابع در ۲: lim(x→2) (x - 2)(x + 2)/(x - 2) = lim (x + 2) = 4. برای پیوستگی باید حد با مقدار تابع در ۲ برابر باشد: a = f(2) = 4."
        ),
        MathQuestionEntity(
            id = "G11_HARD_03",
            grade = 11,
            difficulty = "سخت",
            topic = "مثلثات",
            questionText = "دوره تناوب تابع y = 3sin(4x) + 1 کدام است؟",
            option1 = "π/4",
            option2 = "π/2",
            option3 = "π",
            option4 = "2π",
            correctOptionIndex = 1,
            explanation = "فرمول دوره تناوب توابع مثلثاتی sin(bx): T = 2π / |b| است. در اینجا b = 4 است، پس: T = 2π / 4 = π/2."
        ),
        MathQuestionEntity(
            id = "G11_HARD_04",
            grade = 11,
            difficulty = "سخت",
            topic = "توابع نمایی و لگاریتمی",
            questionText = "دامنه تابع f(x) = log₂(4 - x²) کدام بازه است؟",
            option1 = "[-2, 2]",
            option2 = "(-2, 2)",
            option3 = "(0, 2)",
            option4 = "(-∞, 2)",
            correctOptionIndex = 1,
            explanation = "عبارت جلوی لگاریتم باید اکیداً مثبت باشد: 4 - x² > 0 => x² < 4 => -2 < x < 2 که بازه باز (-2, 2) است."
        ),
        MathQuestionEntity(
            id = "G11_HARD_05",
            grade = 11,
            difficulty = "سخت",
            topic = "آمار و احتمال",
            questionText = "اگر P(A) = 0.6 و P(B) = 0.5 و P(A ∩ B) = 0.3 باشد، مقدار احتمال شرطی P(A | B) کدام است؟",
            option1 = "۰.۳",
            option2 = "۰.۵",
            option3 = "۰.۶",
            option4 = "۰.۷۵",
            correctOptionIndex = 2,
            explanation = "فرمول احتمال شرطی: P(A | B) = P(A ∩ B) / P(B) = 0.3 / 0.5 = 3/5 = 0.6 است."
        ),

        // --- کنکوری (Konkuri) ---
        MathQuestionEntity(
            id = "G11_KONK_01",
            grade = 11,
            difficulty = "کنکوری",
            topic = "هندسه تحلیلی و جبر",
            questionText = "فاصله بین دو خط موازی به معادلات 2x - y + 3 = 0 و 2x - y - 7 = 0 کدام است؟",
            option1 = "√۵",
            option2 = "2√5",
            option3 = "4√5",
            option4 = "۱۰",
            correctOptionIndex = 1,
            explanation = "فاصله دو خط موازی ax + by + c1 = 0 و ax + by + c2 = 0 برابر است با: d = |c1 - c2| / √(a² + b²) = |3 - (-7)| / √(2² + (-1)²) = 10 / √5 = (10√5)/5 = 2√5."
        ),
        MathQuestionEntity(
            id = "G11_KONK_02",
            grade = 11,
            difficulty = "کنکوری",
            topic = "مثلثات",
            questionText = "مجموع جواب‌های معادله مثلثاتی sin(x) = 1/2 در بازه [0, 2π] کدام است؟",
            option1 = "π/2",
            option2 = "π",
            option3 = "3π/2",
            option4 = "2π",
            correctOptionIndex = 1,
            explanation = "در بازه [0, 2π] سینوس در دو زاویه برابر ۱/۲ است: x1 = π/6 و x2 = 5π/6. مجموع آن‌ها: π/6 + 5π/6 = 6π/6 = π."
        ),
        MathQuestionEntity(
            id = "G11_KONK_03",
            grade = 11,
            difficulty = "کنکوری",
            topic = "حد و پیوستگی",
            questionText = "حاصل حد lim(x→+∞) [(3x² - 2x + 1)/(5x² + 4)] کدام است؟",
            option1 = "۰",
            option2 = "۳/۵",
            option3 = "+∞",
            option4 = "۱/۲",
            correctOptionIndex = 1,
            explanation = "در بی‌نهایت، نسبت جملات با بزرگترین توان تعیین‌کننده است: lim (3x² / 5x²) = 3/5."
        ),
        MathQuestionEntity(
            id = "G11_KONK_04",
            grade = 11,
            difficulty = "کنکوری",
            topic = "توابع نمایی و لگاریتمی",
            questionText = "اگر log₂(3) = a باشد، حاصل log₄(18) بر حسب a کدام است؟",
            option1 = "a + 1",
            option2 = "(2a + 1)/2",
            option3 = "(a + 2)/2",
            option4 = "2a + 1",
            correctOptionIndex = 1,
            explanation = "تغییر مبنا به ۲ می‌دهیم: log₄(18) = log₂(18) / log₂(4) = log₂(2 × 3²) / 2 = (log₂2 + 2log₂3) / 2 = (1 + 2a)/2."
        ),
        MathQuestionEntity(
            id = "G11_KONK_05",
            grade = 11,
            difficulty = "کنکوری",
            topic = "توابع نمایی و لگاریتمی",
            questionText = "اگر نقطه (2, 8) روی منحنی تابع نمایی f(x) = a · 2^(bx) باشد و f(0) = 2، مقدار a + b کدام است؟",
            option1 = "۲",
            option2 = "۳",
            option3 = "۴",
            option4 = "۵",
            correctOptionIndex = 1,
            explanation = "f(0) = a · 2⁰ = a => a = 2. حال f(2) = 8 => 2 · 2^(2b) = 8 => 2^(2b) = 4 = 2² => 2b = 2 => b = 1. بنابراین: a + b = 2 + 1 = 3."
        ),

        // ==========================================
        // پایه دوازدهم (Grade 12) - ۲۰ سوال
        // ==========================================

        // --- آسان (Easy) ---
        MathQuestionEntity(
            id = "G12_EASY_01",
            grade = 12,
            difficulty = "آسان",
            topic = "مشتق",
            questionText = "مشتق تابع f(x) = 4x³ - 5x² + 7x - 2 کدام است؟",
            option1 = "12x² - 10x + 7",
            option2 = "12x² - 5x + 7",
            option3 = "4x² - 10x + 7",
            option4 = "12x³ - 10x²",
            correctOptionIndex = 0,
            explanation = "با استفاده از قاعده مشتق چندجمله‌ای‌ها: f'(x) = 4(3x²) - 5(2x) + 7 = 12x² - 10x + 7 حاصل می‌شود."
        ),
        MathQuestionEntity(
            id = "G12_EASY_02",
            grade = 12,
            difficulty = "آسان",
            topic = "مثلثات",
            questionText = "ماکزیمم مقدار تابع f(x) = 3cos(x) - 1 کدام است؟",
            option1 = "۲",
            option2 = "۳",
            option3 = "۴",
            option4 = "۱",
            correctOptionIndex = 0,
            explanation = "بیشترین مقدار cos(x) برابر ۱ است. در نتیجه: max = 3(1) - 1 = 2."
        ),
        MathQuestionEntity(
            id = "G12_EASY_03",
            grade = 12,
            difficulty = "آسان",
            topic = "مشتق",
            questionText = "شیب خط مماس بر منحنی y = x² در نقطه به طول x = 3 کدام است؟",
            option1 = "۳",
            option2 = "۶",
            option3 = "۹",
            option4 = "۱۲",
            correctOptionIndex = 1,
            explanation = "شیب خط مماس برابر مشتق تابع در نقطه است: y' = 2x => m = y'(3) = 2(3) = 6."
        ),
        MathQuestionEntity(
            id = "G12_EASY_04",
            grade = 12,
            difficulty = "آسان",
            topic = "حد در بی‌نهایت",
            questionText = "حاصل حد lim(x→∞) [(5x + 2)/(x - 1)] کدام است؟",
            option1 = "۰",
            option2 = "۲",
            option3 = "۵",
            option4 = "∞",
            correctOptionIndex = 2,
            explanation = "در بی‌نهایت با فاکتورگیری یا قاعده هم‌ارزی بزرگترین توان‌ها: lim (5x / x) = 5 حاصل می‌شود."
        ),
        MathQuestionEntity(
            id = "G12_EASY_05",
            grade = 12,
            difficulty = "آسان",
            topic = "هندسه مقاطع مخروطی",
            questionText = "مختصات مرکز دایره به معادله (x - 2)² + (y + 3)² = 25 کدام است؟",
            option1 = "(-2, 3)",
            option2 = "(2, -3)",
            option3 = "(2, 3)",
            option4 = "(-2, -3)",
            correctOptionIndex = 1,
            explanation = "معادله استاندارد دایره: (x - h)² + (y - k)² = R² است. مرکز دایره C(h, k) = (2, -3) است."
        ),

        // --- متوسط (Medium) ---
        MathQuestionEntity(
            id = "G12_MED_01",
            grade = 12,
            difficulty = "متوسط",
            topic = "مشتق",
            questionText = "اگر f(x) = (2x + 1)⁴ باشد، مقدار f'(1) کدام است؟",
            option1 = "۵۴",
            option2 = "۱۰۸",
            option3 = "۲۱۶",
            option4 = "۳۲۴",
            correctOptionIndex = 2,
            explanation = "مشتق زنجیره‌ای: f'(x) = 4(2x + 1)³ · (2) = 8(2x + 1)³. برای x = 1: f'(1) = 8(2(1) + 1)³ = 8(3³) = 8 × 27 = 216."
        ),
        MathQuestionEntity(
            id = "G12_MED_02",
            grade = 12,
            difficulty = "متوسط",
            topic = "کاربرد مشتق",
            questionText = "طول نقاط بحرانی تابع f(x) = x³ - 3x² + 5 کدام است؟",
            option1 = "۰ و ۱",
            option2 = "۰ و ۲",
            option3 = "۱ و ۲",
            option4 = "-۱ و ۲",
            correctOptionIndex = 1,
            explanation = "مشتق اول را صفر قرار می‌دهیم: f'(x) = 3x² - 6x = 0 => 3x(x - 2) = 0 => x = 0 و x = 2 نقاط بحرانی هستند."
        ),
        MathQuestionEntity(
            id = "G12_MED_03",
            grade = 12,
            difficulty = "متوسط",
            topic = "حد در بی‌نهایت",
            questionText = "حاصل حد lim(x→4) [(√x - 2)/(x - 4)] کدام است؟",
            option1 = "۱/۲",
            option2 = "۱/۴",
            option3 = "۰",
            option4 = "۱",
            correctOptionIndex = 1,
            explanation = "با ضرب صورت و مخرج در مزدوج (√x + 2): (x - 4) / ((x - 4)(√x + 2)) = 1 / (√x + 2). با میل کردن x به سمت ۴: ۱ / (۲ + ۲) = ۱/۴."
        ),
        MathQuestionEntity(
            id = "G12_MED_04",
            grade = 12,
            difficulty = "متوسط",
            topic = "مثلثات",
            questionText = "معادله مثلثاتی cos(2x) = 1 در بازه بسته [0, 2π] چند جواب دارد؟",
            option1 = "۱",
            option2 = "۲",
            option3 = "۳",
            option4 = "۴",
            correctOptionIndex = 2,
            explanation = "cos(2x) = 1 => 2x = 2kπ => x = kπ. در بازه [0, 2π] مقادیر k = 0, 1, 2 جواب‌های x = 0, π, 2π را می‌دهند که دقیقاً ۳ جواب است."
        ),
        MathQuestionEntity(
            id = "G12_MED_05",
            grade = 12,
            difficulty = "متوسط",
            topic = "کاربرد مشتق",
            questionText = "مجموع دو عدد مثبت برابر ۱۰ است. بیشترین مقدار حاصل‌ضرب آن‌ها کدام است؟",
            option1 = "۲۰",
            option2 = "۲۴",
            option3 = "۲۵",
            option4 = "۳۰",
            correctOptionIndex = 2,
            explanation = "x + y = 10 => y = 10 - x. حاصل‌ضرب P(x) = x(10 - x) = 10x - x². مشتق: P'(x) = 10 - 2x = 0 => x = 5 => y = 5. بیشترین حاصل‌ضرب: ۵ × ۵ = ۲۵."
        ),

        // --- سخت (Hard) ---
        MathQuestionEntity(
            id = "G12_HARD_01",
            grade = 12,
            difficulty = "سخت",
            topic = "مشتق",
            questionText = "مشتق تابع f(x) = (x² + 1)/(x - 1) در نقطه x = 2 کدام است؟",
            option1 = "۱",
            option2 = "-۱",
            option3 = "۳",
            option4 = "-۳",
            correctOptionIndex = 1,
            explanation = "مشتق کسری: f'(x) = [2x(x - 1) - (x² + 1)(1)] / (x - 1)². در x = 2 داریم: f'(2) = [2(2)(1) - (4 + 1)] / (1)² = (4 - 5) / 1 = -1."
        ),
        MathQuestionEntity(
            id = "G12_HARD_02",
            grade = 12,
            difficulty = "سخت",
            topic = "کاربرد مشتق",
            questionText = "طول نقطه عطف نمودار تابع f(x) = x³ - 6x² + 9x کدام است؟",
            option1 = "۱",
            option2 = "۲",
            option3 = "۳",
            option4 = "۴",
            correctOptionIndex = 1,
            explanation = "نقطه عطف ریشه مشتق دوم است که علامت آن تغییر کند: f'(x) = 3x² - 12x + 9 => f''(x) = 6x - 12 = 0 => 6x = 12 => x = 2."
        ),
        MathQuestionEntity(
            id = "G12_HARD_03",
            grade = 12,
            difficulty = "سخت",
            topic = "حد در بی‌نهایت",
            questionText = "معادله مجانب افقی تابع f(x) = (2x² + 3)/(x² - 1) کدام است؟",
            option1 = "y = 1",
            option2 = "y = 2",
            option3 = "x = 1",
            option4 = "y = 0",
            correctOptionIndex = 1,
            explanation = "مجانب افقی حد تابع در بی‌نهایت است: lim(x→±∞) (2x² + 3)/(x² - 1) = 2. بنابراین خط y = 2 مجانب افقی است."
        ),
        MathQuestionEntity(
            id = "G12_HARD_04",
            grade = 12,
            difficulty = "سخت",
            topic = "کاربرد مشتق",
            questionText = "کمترین مقدار تابع f(x) = x³ - 3x در بازه بسته [0, 2] کدام است؟",
            option1 = "۰",
            option2 = "-۱",
            option3 = "-۲",
            option4 = "۲",
            correctOptionIndex = 2,
            explanation = "مشتق: f'(x) = 3x² - 3 = 0 => x = 1 (که در بازه است). مقادیر را در نقاط بحرانی و مرزها مقایسه می‌کنیم: f(0) = 0 و f(1) = 1 - 3 = -2 و f(2) = 8 - 6 = 2. کمترین مقدار برابر -2 است."
        ),
        MathQuestionEntity(
            id = "G12_HARD_05",
            grade = 12,
            difficulty = "سخت",
            topic = "هندسه مقاطع مخروطی",
            questionText = "شعاع دایره به معادله x² + y² - 4x + 6y - 3 = 0 کدام است؟",
            option1 = "۳",
            option2 = "۴",
            option3 = "۵",
            option4 = "۱۶",
            correctOptionIndex = 1,
            explanation = "مربع کامل می‌کنیم: (x - 2)² - 4 + (y + 3)² - 9 - 3 = 0 => (x - 2)² + (y + 3)² = 16. بنابراین R² = 16 و شعاع R = 4 است."
        ),

        // --- کنکوری (Konkuri) ---
        MathQuestionEntity(
            id = "G12_KONK_01",
            grade = 12,
            difficulty = "کنکوری",
            topic = "مشتق",
            questionText = "شعاع یک بادکنک کروی با آهنگ ثابت ۲ سانتی‌متر بر ثانیه افزایش می‌یابد. در لحظه‌ای که شعاع r = 5 سانتی‌متر است، آهنگ افزایش حجم آن بر حسب π کدام است؟ (V = 4/3 π r³)",
            option1 = "100π",
            option2 = "200π",
            option3 = "300π",
            option4 = "400π",
            correctOptionIndex = 1,
            explanation = "قاعده زنجیره‌ای در آهنگ‌های وابسته: dV/dt = 4πr² · (dr/dt). با جایگذاری r = 5 و dr/dt = 2 داریم: dV/dt = 4π(25)(2) = 200π."
        ),
        MathQuestionEntity(
            id = "G12_KONK_02",
            grade = 12,
            difficulty = "کنکوری",
            topic = "مشتق",
            questionText = "اگر f(x) = x³ + x - 2 و g تابع وارون f باشد، مقدار g'(0) کدام است؟",
            option1 = "۱/۲",
            option2 = "۱/۴",
            option3 = "۴",
            option4 = "۱",
            correctOptionIndex = 1,
            explanation = "می‌دانیم g'(y0) = 1 / f'(x0) به شرطی که f(x0) = y0. در اینجا f(x0) = 0 => x0³ + x0 - 2 = 0 => x0 = 1. مشتق f'(x) = 3x² + 1 => f'(1) = 4. بنابراین: g'(0) = 1/4."
        ),
        MathQuestionEntity(
            id = "G12_KONK_03",
            grade = 12,
            difficulty = "کنکوری",
            topic = "مشتق",
            questionText = "اگر تابع دوضابطه‌ای f(x) = ax² + b برای x ≤ 1 و f(x) = 4x - 1 برای x > 1 در نقطه x = 1 مشتق‌پذیر باشد، مقدار a + b کدام است؟",
            option1 = "۲",
            option2 = "۳",
            option3 = "۴",
            option4 = "۵",
            correctOptionIndex = 1,
            explanation = "مشتق‌پذیری مستلزم پیوستگی است: a(1)² + b = 4(1) - 1 => a + b = 3. همچنین برابری مشتق چپ و راست: 2a(1) = 4 => a = 2 => b = 1. در هر صورت a + b = 3 است."
        ),
        MathQuestionEntity(
            id = "G12_KONK_04",
            grade = 12,
            difficulty = "کنکوری",
            topic = "کاربرد مشتق",
            questionText = "بیشترین مساحت مستطیلی با اضلاع موازی محورهای مختصات که دو رأس آن روی محور xها و دو رأس دیگر روی سهمی y = 12 - x² (بالای محور x) باشند، کدام است؟",
            option1 = "۱۶",
            option2 = "۲۴",
            option3 = "۳۲",
            option4 = "۶۴",
            correctOptionIndex = 2,
            explanation = "طول قاعده مستطیل 2x و ارتفاع 12 - x² است. تابع مساحت: S(x) = 2x(12 - x²) = 24x - 2x³. مشتق: S'(x) = 24 - 6x² = 0 => x² = 4 => x = 2. بیشترین مساحت: S(2) = 2(2)(12 - 4) = 4 × 8 = 32."
        ),
        MathQuestionEntity(
            id = "G12_KONK_05",
            grade = 12,
            difficulty = "کنکوری",
            topic = "حد در بی‌نهایت",
            questionText = "حاصل حد راست lim(x→2⁺) [([x] - 2)/(x - 2)] کدام است؟",
            option1 = "۰",
            option2 = "۱",
            option3 = "+∞",
            option4 = "وجود ندارد",
            correctOptionIndex = 0,
            explanation = "وقتی x به سمت راست ۲ میل می‌کند (مثلاً x = ۲.۱)، جزء صحیح [x] دقیقاً برابر ۲ است. بنابراین صورت کسر دقیقاً برابر ۲ - ۲ = ۰ مطلق می‌شود. صفر مطلق تقسیم بر عدد غیرصفر برابر با صفر است."
        )
    )
}
