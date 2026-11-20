package com.example.data.curriculum

import com.example.i18n.AppLanguage
import kotlin.math.abs
import kotlin.math.max
import kotlin.random.Random

data class MathTestMeta(
    val grade: Int, // 0..11
    val testNumber: Int, // 1..70
    val title: String,
    val topicBranch: String
)

object MathCurriculum {
    val grades: List<Int> = (0..11).toList()

    private data class QuadTitle(val en: String, val uz: String, val tr: String, val ru: String) {
        fun get(lang: AppLanguage): String = when (lang) {
            AppLanguage.ENGLISH -> en
            AppLanguage.UZBEK -> uz
            AppLanguage.TURKISH -> tr
            AppLanguage.RUSSIAN -> ru
        }
    }

    private val gradeThemes: Map<Int, List<QuadTitle>> = mapOf(
        0 to listOf(
            QuadTitle("Counting Numbers 1–20", "1–20 gacha sanash", "1–20 Sayıları Sayma", "Счёт чисел от 1 до 20"),
            QuadTitle("Comparing More & Fewer", "Ko'p va kamni taqqoslash", "Çok ve Azı Karşılaştırma", "Сравнение больше и меньше"),
            QuadTitle("Basic Addition within 10", "10 ichida oddiy qo'shish", "10 İçinde Toplama", "Сложение в пределах 10"),
            QuadTitle("Basic Subtraction within 10", "10 ichida oddiy ayirish", "10 İçinde Çıkarma", "Вычитание в пределах 10"),
            QuadTitle("2D Shapes & Corners", "2D shakllar va burchaklar", "2D Şekiller ve Köşeler", "Плоские фигуры и углы"),
            QuadTitle("Ordering & Patterns", "Ketma-ketlik va tartib", "Sıralama ve Örüntüler", "Порядок и закономерности"),
            QuadTitle("Measuring Length & Weight", "Uzunlik va og'irlikni o'lchash", "Uzunluk ve Ağırlık Ölçme", "Измерение длины и массы")
        ),
        1 to listOf(
            QuadTitle("Place Value (Tens & Ones)", "Xona birliklari (o'nlik va birlik)", "Basamak Değeri (Onluk ve Birlik)", "Разряды (десятки и единицы)"),
            QuadTitle("Addition within 100", "100 ichida qo'shish", "100 İçinde Toplama", "Сложение в пределах 100"),
            QuadTitle("Subtraction within 100", "100 ichida ayirish", "100 İçinde Çıkarma", "Вычитание в пределах 100"),
            QuadTitle("Skip Counting by 2, 5, 10", "2, 5, 10 tadan sanash", "2, 5 ve 10'ar Sayma", "Счёт по 2, 5 и 10"),
            QuadTitle("Telling Time on Clocks", "Soatni aniqlash", "Saatleri Okuma", "Определение времени по часам"),
            QuadTitle("Counting Coins & Money", "Tangalar va pul hisobi", "Paraları Sayma", "Счёт монет и денег"),
            QuadTitle("Simple Word Problems", "Oddiy matnli masalalar", "Basit Problemler", "Простые текстовые задачи")
        ),
        2 to listOf(
            QuadTitle("Three-Digit Numbers", "Uch xonali sonlar", "Üç Basamaklı Sayılar", "Трёхзначные числа"),
            QuadTitle("Addition with Regrouping", "O'nlikdan o'tib qo'shish", "Eldeli Toplama", "Сложение с переходом через разряд"),
            QuadTitle("Subtraction with Regrouping", "Qarz olib ayirish", "Onluk Bozarak Çıkarma", "Вычитание с займом"),
            QuadTitle("Intro to Multiplication", "Ko'paytirishga kirish", "Çarpmaya Giriş", "Введение в умножение"),
            QuadTitle("Equal Groups & Arrays", "Teng guruhlar va qatorlar", "Eşit Gruplar ve Diziler", "Равные группы и ряды"),
            QuadTitle("Measuring Centimeters & Meters", "Santimetr va metr o'lchovlari", "Santimetre ve Metre Ölçme", "Сантиметры и метры"),
            QuadTitle("Bar Graphs & Data", "Ustunli diagrammalar", "Sütun Grafikleri ve Veri", "Столбчатые диаграммы")
        ),
        3 to listOf(
            QuadTitle("Multiplication Tables (1–12)", "Ko'paytirish jadvali (1–12)", "Çarpım Tablosu (1–12)", "Таблица умножения (1–12)"),
            QuadTitle("Division Facts & Remainders", "Bo'lish va qoldiqli bo'lish", "Bölme ve Kalanlı Bölme", "Деление и остаток"),
            QuadTitle("Unit Fractions & Number Lines", "Oddiy kasrlar va son o'qi", "Birim Kesirler ve Sayı Doğrusu", "Единичные дроби и числовая прямая"),
            QuadTitle("Perimeter of Polygons", "Ko'pburchaklar perimetri", "Çokgenlerin Çevresi", "Периметр многоугольников"),
            QuadTitle("Area of Rectangles", "To'rtburchak yuzi", "Dikdörtgen Alanı", "Площадь прямоугольника"),
            QuadTitle("Rounding to 10 & 100", "10 va 100 gacha yaxlitlash", "10 ve 100'e Yuvarlama", "Округление до 10 и 100"),
            QuadTitle("Multi-Step Word Problems", "Ko'p bosqichli masalalar", "Çok Adımlı Problemler", "Составные задачи")
        ),
        4 to listOf(
            QuadTitle("Multi-Digit Multiplication", "Ko'p xonali sonlarni ko'paytirish", "Çok Basamaklı Çarpma", "Многозначное умножение"),
            QuadTitle("Long Division Basics", "Burchakli bo'lish asoslari", "Uzun Bölme Temelleri", "Деление уголком"),
            QuadTitle("Equivalent Fractions", "Teng kasrlar", "Denk Kesirler", "Равные дроби"),
            QuadTitle("Adding & Subtracting Fractions", "Kasrlarni qo'shish va ayirish", "Kesirlerde Toplama ve Çıkarma", "Сложение и вычитание дробей"),
            QuadTitle("Decimals (Tenths & Hundredths)", "O'nli kasrlar", "Ondalık Kesirler", "Десятичные дроби"),
            QuadTitle("Angles & Triangles", "Burchaklar va uchburchaklar", "Açılar ve Üçgenler", "Углы и треугольники"),
            QuadTitle("Factors, Multiples & Primes", "Bo'luvchilar, karralilar va tub sonlar", "Çarpanlar, Katlar ve Asallar", "Делители, кратные и простые числа")
        ),
        5 to listOf(
            QuadTitle("Decimal Operations", "O'nli kasrlar ustida amallar", "Ondalık İşlemler", "Операции с десятичными дробями"),
            QuadTitle("Fraction Multiplication", "Kasrlarni ko'paytirish", "Kesirlerde Çarpma", "Умножение дробей"),
            QuadTitle("Fraction Division", "Kasrlarni bo'lish", "Kesirlerde Bölme", "Деление дробей"),
            QuadTitle("Volume of Rectangular Prisms", "Parallelepiped hajmi", "Dikdörtgenler Prizması Hacmi", "Объём прямоугольного параллелепипеда"),
            QuadTitle("Coordinate Plane (Quadrant I)", "Koordinata tekisligi", "Koordinat Düzlemi", "Координатная плоскость"),
            QuadTitle("Order of Operations (PEMDAS)", "Amallar tartibi", "İşlem Önceliği", "Порядок действий"),
            QuadTitle("Powers of Ten", "10 ning darajalari", "Onun Kuvvetleri", "Степени числа 10")
        ),
        6 to listOf(
            QuadTitle("Ratios & Unit Rates", "Nisbat va tezlik", "Oran ve Birim Oran", "Отношения и пропорции"),
            QuadTitle("Percentages & Discounts", "Foizlar va chegirmalar", "Yüzdeler ve İndirimler", "Проценты и скидки"),
            QuadTitle("Integers & Negative Numbers", "Butun va manfiy sonlar", "Tam Sayılar ve Negatif Sayılar", "Целые и отрицательные числа"),
            QuadTitle("Algebraic Expressions", "Algebraik ifodalar", "Cebirsel İfadeler", "Алгебраические выражения"),
            QuadTitle("One-Step Linear Equations", "Bir bosqichli chiziqli tenglamalar", "Bir Adımlı Denklemler", "Простые линейные уравнения"),
            QuadTitle("Area of Triangles & Trapezoids", "Uchburchak va trapetsiya yuzi", "Üçgen ve Yamuk Alanı", "Площадь треугольника и трапеции"),
            QuadTitle("Mean, Median & Mode", "O'rta arifmetik, mediana va moda", "Ortalama, Medyan ve Tepe Değer", "Среднее, медиана и мода")
        ),
        7 to listOf(
            QuadTitle("Proportional Relationships", "Proporsional bog'lanishlar", "Orantısal İlişkiler", "Пропорциональные зависимости"),
            QuadTitle("Integer Operations", "Butun sonlar bilan amallar", "Tam Sayılarla İşlemler", "Действия с целыми числами"),
            QuadTitle("Two-Step Equations & Inequalities", "Ikki bosqichli tenglamalar", "İki Adımlı Denklemler ve Eşitsizlikler", "Двухшаговые уравнения и неравенства"),
            QuadTitle("Scale Drawings", "Masshtabli chizmalar", "Ölçekli Çizimler", "Масштабные чертежи"),
            QuadTitle("Circle Circumference & Area", "Aylana uzunligi va doira yuzi", "Çember Çevresi ve Daire Alanı", "Длина окружности и площадь круга"),
            QuadTitle("Surface Area of Prisms", "Prizmalar sirti yuzi", "Prizmaların Yüzey Alanı", "Площадь поверхности призм"),
            QuadTitle("Probability & Compound Events", "Ehtimollik nazariyasi", "Olasılık ve Bileşik Olaylar", "Вероятность событий")
        ),
        8 to listOf(
            QuadTitle("Laws of Exponents", "Daraja xossalari", "Üslü Sayı Kuralları", "Свойства степеней"),
            QuadTitle("Scientific Notation", "Sonning standart shakli", "Bilimsel Gösterim", "Стандартный вид числа"),
            QuadTitle("Linear Functions & Slope", "Chiziqli funksiya va burchak koeffitsiyenti", "Doğrusal Fonksiyonlar ve Eğim", "Линейная функция и наклон"),
            QuadTitle("Systems of Linear Equations", "Chiziqli tenglamalar sistemasi", "Denklem Sistemleri", "Системы линейных уравнений"),
            QuadTitle("Pythagorean Theorem", "Pifagor teoremasi", "Pisagor Teoremi", "Теорема Пифагора"),
            QuadTitle("Transformations & Congruence", "Geometrik almashtirishlar", "Dönüşümler ve Eşlik", "Преобразования и равенство фигур"),
            QuadTitle("Volume of Cylinders, Cones & Spheres", "Silindr, konus va shar hajmi", "Silindir, Koni ve Küre Hacmi", "Объём цилиндра, конуса и шара")
        ),
        9 to listOf(
            QuadTitle("Factoring Polynomials", "Ko'phadlarni ko'paytuvchilarga ajratish", "Polinomları Çarpanlarına Ayırma", "Разложение многочленов на множители"),
            QuadTitle("Quadratic Equations", "Kvadrat tenglamalar", "İkinci Dereceden Denklemler", "Квадратные уравнения"),
            QuadTitle("Quadratic Formula & Discriminant", "Diskriminant va ildizlar formulasi", "Diskriminant ve Kök Formülü", "Дискриминант и формула корней"),
            QuadTitle("Domain, Range & Functions", "Funksiya aniqlanish va qiymat sohasi", "Tanım, Değer Kümesi ve Fonksiyonlar", "Область определения и функции"),
            QuadTitle("Linear Inequalities", "Chiziqli tengsizliklar", "Doğrusal Eşitsizlikler", "Линейные неравенства"),
            QuadTitle("Radical Expressions", "Ildizli ifodalar", "Köklü İfadeler", "Радикальные выражения"),
            QuadTitle("Exponential Growth & Decay", "Ko'rsatkichli o'sish va kamayish", "Üstel Büyüme ve Azalma", "Экспоненциальный рост")
        ),
        10 to listOf(
            QuadTitle("Right-Triangle Trigonometry", "To'g'ri burchakli uchburchak trigonometriyasi", "Dik Üçgen Trigonometrisi", "Тригонометрия прямоугольного треугольника"),
            QuadTitle("Unit Circle & Radians", "Birlik aylana va radian", "Birim Çember ve Radyan", "Единичная окружность и радианы"),
            QuadTitle("Logarithms & Exponential Equations", "Logarifmlar va tenglamalar", "Logaritma ve Üstel Denklemler", "Логарифмы и уравнения"),
            QuadTitle("Arithmetic & Geometric Sequences", "Arifmetik va geometrik progressiya", "Aritmetik ve Geometrik Diziler", "Арифметическая и геометрическая прогрессии"),
            QuadTitle("Complex Numbers", "Kompleks sonlar", "Karmaşık Sayılar", "Комплексные числа"),
            QuadTitle("Circle Theorems & Arcs", "Aylana va yoy teoremalari", "Çember Teoremleri ve Yaylar", "Теоремы об окружности и дугах"),
            QuadTitle("Permutations & Combinations", "O'rin almashtirish va guruhlashlar", "Permütasyon ve Kombinasyon", "Перестановки и сочетания")
        ),
        11 to listOf(
            QuadTitle("Limits & Continuity", "Limitlar va uzluksizlik", "Limit ve Süreklilik", "Пределы и непрерывность"),
            QuadTitle("Derivatives (Power & Chain Rule)", "Hosila va zanjir qoidasi", "Türev Kuralları", "Производная функции"),
            QuadTitle("Tangent Lines & Extrema", "Urinma va ekstremumlar", "Teğet Doğrular ve Ekstremum", "Касательные и экстремумы"),
            QuadTitle("Indefinite & Definite Integrals", "Aniq va aniqmas integrallar", "Belirli ve Belirsiz İntegraller", "Интегралы"),
            QuadTitle("Vectors & Dot Product", "Vektorlar va skalyar ko'paytma", "Vektörler ve Nokta Çarpım", "Векторы и скалярное произведение"),
            QuadTitle("Matrices & Determinants", "Matritsalar va determinantlar", "Matrisler ve Determinant", "Матрицы и определители"),
            QuadTitle("Conditional Probability & Bayes", "Shartli ehtimollik va Bayes", "Koşullu Olasılık ve Bayes", "Условная вероятность и Байес")
        )
    )

    fun gradeTag(grade: Int, language: AppLanguage): String = when (language) {
        AppLanguage.UZBEK -> "$grade-sinf"
        AppLanguage.TURKISH -> "$grade. Sınıf"
        AppLanguage.RUSSIAN -> "$grade класс"
        AppLanguage.ENGLISH -> "Grade $grade"
    }

    fun getTestsForGrade(grade: Int, language: AppLanguage = AppLanguage.ENGLISH): List<MathTestMeta> {
        val g = grade.coerceIn(0, 11)
        val themes = gradeThemes[g] ?: emptyList()
        return (1..70).map { idx ->
            val themeText = themes[(idx - 1) % themes.size].get(language)
            val setNum = ((idx - 1) / themes.size) + 1
            val setLabel = when (language) {
                AppLanguage.UZBEK -> "$setNum-to'plam"
                AppLanguage.TURKISH -> "Set $setNum"
                AppLanguage.RUSSIAN -> "Вариант $setNum"
                AppLanguage.ENGLISH -> "Practice Set $setNum"
            }
            MathTestMeta(
                grade = g,
                testNumber = idx,
                title = if (setNum == 1) themeText else "$themeText • $setLabel",
                topicBranch = themeText
            )
        }
    }

    /**
     * Generates 30 mathematically verified MCQs for (grade, testNumber)
     * fully localized in the selected Settings language.
     */
    fun generateTestQuestions(
        meta: MathTestMeta,
        language: AppLanguage = AppLanguage.ENGLISH
    ): List<QuizQuestion> {
        val rng = Random(meta.grade * 100003L + meta.testNumber * 7919L + 31337L)
        return (1..30).map { qId ->
            generateProblemForGrade(meta, qId, language, rng)
        }
    }

    private fun generateProblemForGrade(
        meta: MathTestMeta,
        qId: Int,
        lang: AppLanguage,
        rng: Random
    ): QuizQuestion {
        val subType = (meta.testNumber + qId) % 4
        return when (meta.grade) {
            0 -> genGrade0(meta, qId, subType, lang, rng)
            1 -> genGrade1(meta, qId, subType, lang, rng)
            2 -> genGrade2(meta, qId, subType, lang, rng)
            3 -> genGrade3(meta, qId, subType, lang, rng)
            4 -> genGrade4(meta, qId, subType, lang, rng)
            5 -> genGrade5(meta, qId, subType, lang, rng)
            6 -> genGrade6(meta, qId, subType, lang, rng)
            7 -> genGrade7(meta, qId, subType, lang, rng)
            8 -> genGrade8(meta, qId, subType, lang, rng)
            9 -> genGrade9(meta, qId, subType, lang, rng)
            10 -> genGrade10(meta, qId, subType, lang, rng)
            else -> genGrade11(meta, qId, subType, lang, rng)
        }
    }

    private fun prefix(meta: MathTestMeta, lang: AppLanguage): String =
        "[${gradeTag(meta.grade, lang)} • ${meta.topicBranch}]"

    private fun genGrade0(meta: MathTestMeta, qId: Int, sub: Int, lang: AppLanguage, rng: Random): QuizQuestion {
        val pfx = prefix(meta, lang)
        return when (sub) {
            0 -> {
                val a = rng.nextInt(1, 10)
                val b = rng.nextInt(1, 10)
                val ans = a + b
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx $a + $b nechiga teng?"
                    AppLanguage.TURKISH -> "$pfx $a + $b kaçtır?"
                    AppLanguage.RUSSIAN -> "$pfx Чему равно $a + $b?"
                    AppLanguage.ENGLISH -> "$pfx What is $a + $b?"
                }
                val exp = when (lang) {
                    AppLanguage.UZBEK -> "$a dan $b ta oldinga sanasak $ans chiqadi."
                    AppLanguage.TURKISH -> "$a üzerine $b sayıldığında sonuç $ans olur."
                    AppLanguage.RUSSIAN -> "Если к $a прибавить $b, получится $ans."
                    AppLanguage.ENGLISH -> "Count forward $b steps from $a to get $ans."
                }
                intMcq(qId, prompt, ans, exp, rng, minVal = 1)
            }
            1 -> {
                val a = rng.nextInt(5, 16)
                val b = rng.nextInt(1, a)
                val ans = a - b
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx Ayiring: $a - $b = ?"
                    AppLanguage.TURKISH -> "$pfx Çıkarın: $a - $b = ?"
                    AppLanguage.RUSSIAN -> "$pfx Вычислите: $a - $b = ?"
                    AppLanguage.ENGLISH -> "$pfx Subtract: $a - $b = ?"
                }
                val exp = when (lang) {
                    AppLanguage.UZBEK -> "$a dan $b ni ayirsak $ans qoladi."
                    AppLanguage.TURKISH -> "$a sayısından $b çıkarılınca $ans kalır."
                    AppLanguage.RUSSIAN -> "$a минус $b равно $ans."
                    AppLanguage.ENGLISH -> "Taking away $b from $a leaves $ans."
                }
                intMcq(qId, prompt, ans, exp, rng, minVal = 0)
            }
            2 -> {
                val a = rng.nextInt(3, 18)
                val step = rng.nextInt(1, 4)
                val ans = a + 3 * step
                val seq = "$a, ${a + step}, ${a + 2 * step}, ___"
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx Ketma-ketlikni qaysi son davom ettiradi: $seq?"
                    AppLanguage.TURKISH -> "$pfx Örüntüyü hangi sayı tamamlar: $seq?"
                    AppLanguage.RUSSIAN -> "$pfx Какое число продолжает ряд: $seq?"
                    AppLanguage.ENGLISH -> "$pfx Which number completes the pattern: $seq?"
                }
                val exp = when (lang) {
                    AppLanguage.UZBEK -> "Har bir qadamda $step qo'shiladi: ${a + 2 * step} + $step = $ans."
                    AppLanguage.TURKISH -> "Her adımda $step eklenir: ${a + 2 * step} + $step = $ans."
                    AppLanguage.RUSSIAN -> "Каждый шаг прибавляет $step: ${a + 2 * step} + $step = $ans."
                    AppLanguage.ENGLISH -> "Each step adds $step, so ${a + 2 * step} + $step = $ans."
                }
                intMcq(qId, prompt, ans, exp, rng, minVal = 1)
            }
            else -> {
                val shapes = listOf(
                    QuadTitle("Triangle", "Uchburchak", "Üçgen", "Треугольник") to 3,
                    QuadTitle("Square", "Kvadrat", "Kare", "Квадрат") to 4,
                    QuadTitle("Pentagon", "Beshburchak", "Beşgen", "Пятиугольник") to 5,
                    QuadTitle("Hexagon", "Oltiburchak", "Altıgen", "Шестиугольник") to 6
                )
                val pick = shapes[rng.nextInt(shapes.size)]
                val shapeName = pick.first.get(lang)
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx '$shapeName' shaklining nechta tomoni bor?"
                    AppLanguage.TURKISH -> "$pfx '$shapeName' şeklinin kaç kenarı vardır?"
                    AppLanguage.RUSSIAN -> "$pfx Сколько сторон имеет фигура '$shapeName'?"
                    AppLanguage.ENGLISH -> "$pfx How many sides does a $shapeName have?"
                }
                val exp = when (lang) {
                    AppLanguage.UZBEK -> "$shapeName ${pick.second} ta to'g'ri tomonga ega."
                    AppLanguage.TURKISH -> "$shapeName ${pick.second} kenara sahiptir."
                    AppLanguage.RUSSIAN -> "У фигуры '$shapeName' ровно ${pick.second} сторон(ы)."
                    AppLanguage.ENGLISH -> "A $shapeName has ${pick.second} straight sides."
                }
                intMcq(qId, prompt, pick.second, exp, rng, minVal = 2)
            }
        }
    }

    private fun genGrade1(meta: MathTestMeta, qId: Int, sub: Int, lang: AppLanguage, rng: Random): QuizQuestion {
        val pfx = prefix(meta, lang)
        return when (sub) {
            0 -> {
                val a = rng.nextInt(12, 65)
                val b = rng.nextInt(10, 35)
                val ans = a + b
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx Hisoblang: $a + $b"
                    AppLanguage.TURKISH -> "$pfx Hesaplayın: $a + $b"
                    AppLanguage.RUSSIAN -> "$pfx Вычислите: $a + $b"
                    AppLanguage.ENGLISH -> "$pfx Calculate: $a + $b"
                }
                intMcq(qId, prompt, ans, "$a + $b = $ans.", rng)
            }
            1 -> {
                val a = rng.nextInt(40, 99)
                val b = rng.nextInt(11, 39)
                val ans = a - b
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx Hisoblang: $a - $b"
                    AppLanguage.TURKISH -> "$pfx Hesaplayın: $a - $b"
                    AppLanguage.RUSSIAN -> "$pfx Вычислите: $a - $b"
                    AppLanguage.ENGLISH -> "$pfx Calculate: $a - $b"
                }
                intMcq(qId, prompt, ans, "$a - $b = $ans.", rng)
            }
            2 -> {
                val tens = rng.nextInt(2, 9)
                val ones = rng.nextInt(1, 9)
                val n = tens * 10 + ones
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx $n sonida o'nlik xonasidagi raqam qanday qiymatni bildiradi?"
                    AppLanguage.TURKISH -> "$pfx $n sayısında onlar basamağındaki rakamın basamak değeri nedir?"
                    AppLanguage.RUSSIAN -> "$pfx Какое значение обозначает цифра десятков в числе $n?"
                    AppLanguage.ENGLISH -> "$pfx In the number $n, what value does the tens digit represent?"
                }
                val exp = when (lang) {
                    AppLanguage.UZBEK -> "$tens raqami o'nlik xonasida turgani uchun ${tens * 10} ni bildiradi."
                    AppLanguage.TURKISH -> "$tens rakamı onlar basamağında olduğu için ${tens * 10} değerini gösterir."
                    AppLanguage.RUSSIAN -> "Цифра $tens стоит в разряде десятков и обозначает ${tens * 10}."
                    AppLanguage.ENGLISH -> "The digit $tens is in the tens place, representing ${tens * 10}."
                }
                intMcq(qId, prompt, tens * 10, exp, rng)
            }
            else -> {
                val nickels = rng.nextInt(2, 8)
                val pennies = rng.nextInt(1, 9)
                val total = nickels * 5 + pennies
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx $nickels ta 5 so'mlik va $pennies ta 1 so'mlik tanga jami necha bo'ladi?"
                    AppLanguage.TURKISH -> "$pfx $nickels adet 5 birimlik ve $pennies adet 1 birimlik madeni para toplam kaç eder?"
                    AppLanguage.RUSSIAN -> "$pfx Сколько будет всего, если взять $nickels монет(ы) по 5 и $pennies по 1?"
                    AppLanguage.ENGLISH -> "$pfx $nickels five-cent coins plus $pennies one-cent coins equal how many cents?"
                }
                intMcq(qId, prompt, total, "$nickels × 5 + $pennies = $total.", rng)
            }
        }
    }

    private fun genGrade2(meta: MathTestMeta, qId: Int, sub: Int, lang: AppLanguage, rng: Random): QuizQuestion {
        val pfx = prefix(meta, lang)
        return when (sub) {
            0 -> {
                val a = rng.nextInt(120, 580)
                val b = rng.nextInt(110, 390)
                val ans = a + b
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx Yig'indini toping: $a + $b"
                    AppLanguage.TURKISH -> "$pfx Toplamı bulun: $a + $b"
                    AppLanguage.RUSSIAN -> "$pfx Найдите сумму: $a + $b"
                    AppLanguage.ENGLISH -> "$pfx Find the sum: $a + $b"
                }
                intMcq(qId, prompt, ans, "$a + $b = $ans.", rng)
            }
            1 -> {
                val a = rng.nextInt(450, 950)
                val b = rng.nextInt(125, 395)
                val ans = a - b
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx Ayirmani toping: $a - $b"
                    AppLanguage.TURKISH -> "$pfx Farkı bulun: $a - $b"
                    AppLanguage.RUSSIAN -> "$pfx Найдите разность: $a - $b"
                    AppLanguage.ENGLISH -> "$pfx Find the difference: $a - $b"
                }
                intMcq(qId, prompt, ans, "$a - $b = $ans.", rng)
            }
            2 -> {
                val rows = rng.nextInt(2, 6)
                val cols = rng.nextInt(3, 9)
                val ans = rows * cols
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx Jadvalda $rows ta qator va har bir qatorda $cols ta yulduzcha bor. Jami yulduzchalar nechta?"
                    AppLanguage.TURKISH -> "$pfx Bir dizide $rows satır ve her satırda $cols yıldız varsa toplam kaç yıldız vardır?"
                    AppLanguage.RUSSIAN -> "$pfx В таблице $rows ряда(ов) по $cols звёздочек в каждом. Сколько всего звёздочек?"
                    AppLanguage.ENGLISH -> "$pfx An array has $rows rows with $cols stars in each row. Total stars?"
                }
                intMcq(qId, prompt, ans, "$rows × $cols = $ans.", rng)
            }
            else -> {
                val m = rng.nextInt(2, 9)
                val ans = m * 100
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx $m metr necha santimetrga teng?"
                    AppLanguage.TURKISH -> "$pfx $m metre kaç santimetredir?"
                    AppLanguage.RUSSIAN -> "$pfx Сколько сантиметров в $m метрах?"
                    AppLanguage.ENGLISH -> "$pfx How many centimeters are in $m meters?"
                }
                val exp = when (lang) {
                    AppLanguage.UZBEK -> "1 metr = 100 sm, demak $m metr = $ans sm."
                    AppLanguage.TURKISH -> "1 metre = 100 cm olduğundan $m metre = $ans cm."
                    AppLanguage.RUSSIAN -> "1 метр = 100 см, значит $m м = $ans см."
                    AppLanguage.ENGLISH -> "1 meter = 100 cm, so $m meters = $ans cm."
                }
                intMcq(qId, prompt, ans, exp, rng)
            }
        }
    }

    private fun genGrade3(meta: MathTestMeta, qId: Int, sub: Int, lang: AppLanguage, rng: Random): QuizQuestion {
        val pfx = prefix(meta, lang)
        return when (sub) {
            0 -> {
                val a = rng.nextInt(4, 13)
                val b = rng.nextInt(3, 13)
                val ans = a * b
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx Ko'paytiring: $a × $b"
                    AppLanguage.TURKISH -> "$pfx Çarpın: $a × $b"
                    AppLanguage.RUSSIAN -> "$pfx Умножьте: $a × $b"
                    AppLanguage.ENGLISH -> "$pfx Multiply: $a × $b"
                }
                intMcq(qId, prompt, ans, "$a × $b = $ans.", rng)
            }
            1 -> {
                val b = rng.nextInt(3, 12)
                val q = rng.nextInt(4, 12)
                val a = b * q
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx Bo'ling: $a ÷ $b"
                    AppLanguage.TURKISH -> "$pfx Bölün: $a ÷ $b"
                    AppLanguage.RUSSIAN -> "$pfx Разделите: $a ÷ $b"
                    AppLanguage.ENGLISH -> "$pfx Divide: $a ÷ $b"
                }
                intMcq(qId, prompt, q, "$b × $q = $a => $a ÷ $b = $q.", rng)
            }
            2 -> {
                val w = rng.nextInt(4, 15)
                val h = rng.nextInt(3, 12)
                val ans = 2 * (w + h)
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx To'g'ri to'rtburchakning bo'yi $w sm, eni $h sm. Perimetri necha sm?"
                    AppLanguage.TURKISH -> "$pfx Uzunluğu $w cm ve genişliği $h cm olan dikdörtgenin çevresi kaç cm'dir?"
                    AppLanguage.RUSSIAN -> "$pfx Длина прямоугольника $w см, ширина $h см. Найдите периметр (см)."
                    AppLanguage.ENGLISH -> "$pfx A rectangle has length $w cm and width $h cm. What is its perimeter (cm)?"
                }
                intMcq(qId, prompt, ans, "P = 2 × ($w + $h) = $ans cm.", rng)
            }
            else -> {
                val w = rng.nextInt(4, 14)
                val h = rng.nextInt(3, 11)
                val ans = w * h
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx Tomonlari $w m va $h m bo'lgan to'rtburchakning yuzi (m²) qancha?"
                    AppLanguage.TURKISH -> "$pfx Kenarları $w m ve $h m olan dikdörtgenin alanı (m²) nedir?"
                    AppLanguage.RUSSIAN -> "$pfx Стороны прямоугольника $w м и $h м. Найдите площадь (м²)."
                    AppLanguage.ENGLISH -> "$pfx A rectangle measures $w m by $h m. What is its area (m²)?"
                }
                intMcq(qId, prompt, ans, "S = $w × $h = $ans m².", rng)
            }
        }
    }

    private fun genGrade4(meta: MathTestMeta, qId: Int, sub: Int, lang: AppLanguage, rng: Random): QuizQuestion {
        val pfx = prefix(meta, lang)
        return when (sub) {
            0 -> {
                val a = rng.nextInt(14, 48)
                val b = rng.nextInt(6, 19)
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx Ko'paytmani hisoblang: $a × $b"
                    AppLanguage.TURKISH -> "$pfx Hesaplayın: $a × $b"
                    AppLanguage.RUSSIAN -> "$pfx Вычислите: $a × $b"
                    AppLanguage.ENGLISH -> "$pfx Compute: $a × $b"
                }
                intMcq(qId, prompt, a * b, "$a × $b = ${a * b}.", rng)
            }
            1 -> {
                val denom = rng.nextInt(5, 13)
                val n1 = rng.nextInt(1, denom - 1)
                val n2 = rng.nextInt(1, denom - n1 + 1)
                val correct = "${n1 + n2}/$denom"
                val dist = listOf("${n1 + n2 + 1}/$denom", "${max(1, n1 + n2 - 1)}/$denom", "${n1 + n2}/${denom * 2}")
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx Kasrlarni qo'shing: $n1/$denom + $n2/$denom"
                    AppLanguage.TURKISH -> "$pfx Kesirleri toplayın: $n1/$denom + $n2/$denom"
                    AppLanguage.RUSSIAN -> "$pfx Сложите дроби: $n1/$denom + $n2/$denom"
                    AppLanguage.ENGLISH -> "$pfx Add fractions: $n1/$denom + $n2/$denom"
                }
                val exp = when (lang) {
                    AppLanguage.UZBEK -> "Maxraj $denom o'zgarishsiz qoladi, suratlar qo'shiladi: $n1 + $n2 = ${n1 + n2}."
                    AppLanguage.TURKISH -> "Payda $denom sabit kalır, paylar toplanır: $n1 + $n2 = ${n1 + n2}."
                    AppLanguage.RUSSIAN -> "Знаменатель $denom остаётся прежним, числители складываются: $n1 + $n2 = ${n1 + n2}."
                    AppLanguage.ENGLISH -> "Keep denominator $denom and add numerators: $n1 + $n2 = ${n1 + n2}."
                }
                strMcq(qId, prompt, correct, dist, exp, rng)
            }
            2 -> {
                val primes = listOf(11, 13, 17, 19, 23, 29, 31)
                val p = primes[rng.nextInt(primes.size)]
                val composites = listOf(12, 15, 21, 27, 28, 33, 35).shuffled(rng).take(3)
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx Quyidagi sonlardan qaysi biri tub son?"
                    AppLanguage.TURKISH -> "$pfx Aşağıdaki sayılardan hangisi asal sayıdır?"
                    AppLanguage.RUSSIAN -> "$pfx Какое из этих чисел является простым?"
                    AppLanguage.ENGLISH -> "$pfx Which of these numbers is a prime number?"
                }
                val exp = when (lang) {
                    AppLanguage.UZBEK -> "$p faqat 1 ga va o'ziga bo'linadi, shuning uchun tub son."
                    AppLanguage.TURKISH -> "$p sadece 1'e ve kendisine bölünür, yani asal sayıdır."
                    AppLanguage.RUSSIAN -> "$p делится только на 1 и само на себя."
                    AppLanguage.ENGLISH -> "$p has exactly two positive divisors: 1 and $p."
                }
                strMcq(qId, prompt, "$p", composites.map { "$it" }, exp, rng)
            }
            else -> {
                val a1 = rng.nextInt(30, 85)
                val a2 = rng.nextInt(25, 75)
                val a3 = 180 - a1 - a2
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx Uchburchakning ikki burchagi $a1° va $a2°. Uchinchi burchagini toping (°)."
                    AppLanguage.TURKISH -> "$pfx Bir üçgenin iki açısı $a1° ve $a2° ise üçüncü açısı kaç derecedir?"
                    AppLanguage.RUSSIAN -> "$pfx Два угла треугольника равны $a1° и $a2°. Найдите третий угол (°)."
                    AppLanguage.ENGLISH -> "$pfx Two angles in a triangle measure $a1° and $a2°. Find the third angle (°)."
                }
                val exp = when (lang) {
                    AppLanguage.UZBEK -> "Uchburchak burchaklari yig'indisi 180°: 180 - ($a1 + $a2) = $a3°."
                    AppLanguage.TURKISH -> "Üçgenin iç açıları toplamı 180°'dir: 180 - ($a1 + $a2) = $a3°."
                    AppLanguage.RUSSIAN -> "Сумма углов треугольника 180°: 180 - ($a1 + $a2) = $a3°."
                    AppLanguage.ENGLISH -> "Triangle angles sum to 180°: 180 - ($a1 + $a2) = $a3°."
                }
                intMcq(qId, prompt, a3, exp, rng)
            }
        }
    }

    private fun genGrade5(meta: MathTestMeta, qId: Int, sub: Int, lang: AppLanguage, rng: Random): QuizQuestion {
        val pfx = prefix(meta, lang)
        return when (sub) {
            0 -> {
                val a = rng.nextInt(2, 8)
                val b = rng.nextInt(3, 9)
                val c = rng.nextInt(2, 7)
                val ans = a + b * c
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx Amallar tartibi bo'yicha hisoblang: $a + $b × $c"
                    AppLanguage.TURKISH -> "$pfx İşlem önceliğine göre hesaplayın: $a + $b × $c"
                    AppLanguage.RUSSIAN -> "$pfx Вычислите по порядку действий: $a + $b × $c"
                    AppLanguage.ENGLISH -> "$pfx Evaluate using order of operations: $a + $b × $c"
                }
                val exp = when (lang) {
                    AppLanguage.UZBEK -> "Avval ko'paytiriladi: $b × $c = ${b * c}, so'ng $a qo'shiladi = $ans."
                    AppLanguage.TURKISH -> "Önce çarpma yapılır: $b × $c = ${b * c}, sonra $a eklenir = $ans."
                    AppLanguage.RUSSIAN -> "Сначала умножение: $b × $c = ${b * c}, затем + $a = $ans."
                    AppLanguage.ENGLISH -> "Multiply first: $b × $c = ${b * c}, then add $a to get $ans."
                }
                intMcq(qId, prompt, ans, exp, rng)
            }
            1 -> {
                val l = rng.nextInt(3, 10)
                val w = rng.nextInt(2, 8)
                val h = rng.nextInt(2, 7)
                val ans = l * w * h
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx O'lchamlari $l × $w × $h sm bo'lgan to'g'ri burchakli parallelepiped hajmi necha sm³?"
                    AppLanguage.TURKISH -> "$pfx Boyutları $l × $w × $h cm olan dikdörtgenler prizmasının hacmi kaç cm³'tür?"
                    AppLanguage.RUSSIAN -> "$pfx Найдите объём прямоугольного параллелепипеда со сторонами $l × $w × $h см (см³)."
                    AppLanguage.ENGLISH -> "$pfx Volume of a rectangular prism with dimensions $l × $w × $h cm?"
                }
                intMcq(qId, prompt, ans, "V = $l × $w × $h = $ans cm³.", rng)
            }
            2 -> {
                val x = rng.nextInt(12, 65)
                val y = rng.nextInt(15, 45)
                val sum = (x + y) / 10.0
                val dist = listOf((x + y + 10) / 10.0, (x + y - 5) / 10.0, (x + y + 2) / 10.0).map { "%.1f".format(it) }
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx O'nli kasrlarni qo'shing: ${"%.1f".format(x / 10.0)} + ${"%.1f".format(y / 10.0)}"
                    AppLanguage.TURKISH -> "$pfx Ondalık sayıları toplayın: ${"%.1f".format(x / 10.0)} + ${"%.1f".format(y / 10.0)}"
                    AppLanguage.RUSSIAN -> "$pfx Сложите десятичные дроби: ${"%.1f".format(x / 10.0)} + ${"%.1f".format(y / 10.0)}"
                    AppLanguage.ENGLISH -> "$pfx Add decimals: ${"%.1f".format(x / 10.0)} + ${"%.1f".format(y / 10.0)}"
                }
                strMcq(qId, prompt, "%.1f".format(sum), dist, "${"%.1f".format(x / 10.0)} + ${"%.1f".format(y / 10.0)} = ${"%.1f".format(sum)}.", rng)
            }
            else -> {
                val num = rng.nextInt(2, 7)
                val mul = rng.nextInt(2, 6)
                val den = rng.nextInt(num + 1, 11)
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx Hisoblang: ($num/$den) × ${den * mul}"
                    AppLanguage.TURKISH -> "$pfx Sadeceleştirip hesaplayın: ($num/$den) × ${den * mul}"
                    AppLanguage.RUSSIAN -> "$pfx Вычислите: ($num/$den) × ${den * mul}"
                    AppLanguage.ENGLISH -> "$pfx Simplify: ($num/$den) × ${den * mul}"
                }
                intMcq(qId, prompt, num * mul, "$num × $mul = ${num * mul}.", rng)
            }
        }
    }

    private fun genGrade6(meta: MathTestMeta, qId: Int, sub: Int, lang: AppLanguage, rng: Random): QuizQuestion {
        val pfx = prefix(meta, lang)
        return when (sub) {
            0 -> {
                val pct = listOf(10, 15, 20, 25, 30, 40, 50)[rng.nextInt(7)]
                val base = rng.nextInt(2, 15) * 20
                val ans = (pct * base) / 100
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx $base sonining $pct% i nechiga teng?"
                    AppLanguage.TURKISH -> "$pfx $base sayısının %$pct'si kaçtır?"
                    AppLanguage.RUSSIAN -> "$pfx Сколько составляет $pct% от числа $base?"
                    AppLanguage.ENGLISH -> "$pfx What is $pct% of $base?"
                }
                intMcq(qId, prompt, ans, "$pct/100 × $base = $ans.", rng)
            }
            1 -> {
                val x = rng.nextInt(3, 24)
                val b = rng.nextInt(5, 22)
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx Tenglamani yeching: x + $b = ${x + b}. x = ?"
                    AppLanguage.TURKISH -> "$pfx x için çözün: x + $b = ${x + b}"
                    AppLanguage.RUSSIAN -> "$pfx Решите уравнение: x + $b = ${x + b}"
                    AppLanguage.ENGLISH -> "$pfx Solve for x: x + $b = ${x + b}"
                }
                intMcq(qId, prompt, x, "x = ${x + b} - $b = $x.", rng)
            }
            2 -> {
                val base = rng.nextInt(6, 18) * 2
                val height = rng.nextInt(4, 15)
                val ans = (base * height) / 2
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx Asosi $base va balandligi $height bo'lgan uchburchak yuzi qancha?"
                    AppLanguage.TURKISH -> "$pfx Tabanı $base ve yüksekliği $height olan üçgenin alanı nedir?"
                    AppLanguage.RUSSIAN -> "$pfx Основание треугольника $base, высота $height. Найдите площадь."
                    AppLanguage.ENGLISH -> "$pfx Area of a triangle with base $base and height $height?"
                }
                intMcq(qId, prompt, ans, "S = ½ × $base × $height = $ans.", rng)
            }
            else -> {
                val m = rng.nextInt(4, 14)
                val v1 = m - 4
                val v2 = m
                val v3 = m + 4
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx {$v1, $v2, $v3} sonlarining o'rta arifmetigini toping."
                    AppLanguage.TURKISH -> "$pfx {$v1, $v2, $v3} sayılarının aritmetik ortalamasını bulun."
                    AppLanguage.RUSSIAN -> "$pfx Найдите среднее арифметическое чисел {$v1, $v2, $v3}."
                    AppLanguage.ENGLISH -> "$pfx Find the mean (average) of {$v1, $v2, $v3}."
                }
                intMcq(qId, prompt, m, "($v1 + $v2 + $v3) ÷ 3 = $m.", rng)
            }
        }
    }

    private fun genGrade7(meta: MathTestMeta, qId: Int, sub: Int, lang: AppLanguage, rng: Random): QuizQuestion {
        val pfx = prefix(meta, lang)
        return when (sub) {
            0 -> {
                val a = rng.nextInt(2, 8)
                val x = rng.nextInt(2, 13)
                val b = rng.nextInt(3, 18)
                val rhs = a * x + b
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx x ni toping: ${a}x + $b = $rhs"
                    AppLanguage.TURKISH -> "$pfx x'i bulun: ${a}x + $b = $rhs"
                    AppLanguage.RUSSIAN -> "$pfx Найдите x: ${a}x + $b = $rhs"
                    AppLanguage.ENGLISH -> "$pfx Solve for x: ${a}x + $b = $rhs"
                }
                intMcq(qId, prompt, x, "${a}x = ${rhs - b} => x = $x.", rng)
            }
            1 -> {
                val a = rng.nextInt(-15, -2)
                val b = rng.nextInt(5, 20)
                val ans = a * -2 + b
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx Qiymatini toping: ($a) × (-2) + $b"
                    AppLanguage.TURKISH -> "$pfx Hesaplayın: ($a) × (-2) + $b"
                    AppLanguage.RUSSIAN -> "$pfx Вычислите: ($a) × (-2) + $b"
                    AppLanguage.ENGLISH -> "$pfx Evaluate: ($a) × (-2) + $b"
                }
                intMcq(qId, prompt, ans, "($a)×(-2) = ${-2 * a}; ${-2 * a} + $b = $ans.", rng)
            }
            2 -> {
                val r = rng.nextInt(2, 12)
                val correct = "${2 * r}π"
                val dist = listOf("${r}π", "${r * r}π", "${4 * r}π")
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx Radiusi r = $r bo'lgan aylana uzunligi (C) nimaga teng?"
                    AppLanguage.TURKISH -> "$pfx Yarıçapı r = $r olan çemberin çevresi nedir?"
                    AppLanguage.RUSSIAN -> "$pfx Найдите точную длину окружности радиуса r = $r."
                    AppLanguage.ENGLISH -> "$pfx Exact circumference of a circle with radius r = $r?"
                }
                strMcq(qId, prompt, correct, dist, "C = 2πr = 2π($r) = ${2 * r}π.", rng)
            }
            else -> {
                val fav = rng.nextInt(2, 6)
                val total = fav + rng.nextInt(3, 8)
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx Xaltada $fav ta qizil va ${total - fav} ta ko'k shar bor. Qizil shar chiqish ehtimolligi qancha?"
                    AppLanguage.TURKISH -> "$pfx Bir torbada $fav kırmızı ve ${total - fav} mavi bilye vardır. Kırmızı çekme olasılığı nedir?"
                    AppLanguage.RUSSIAN -> "$pfx В мешке $fav красных и ${total - fav} синих шаров. Вероятность вытащить красный?"
                    AppLanguage.ENGLISH -> "$pfx A bag has $fav red and ${total - fav} blue marbles. Probability of picking red?"
                }
                strMcq(qId, prompt, "$fav/$total", listOf("${total - fav}/$total", "1/$total", "$fav/${total - fav}"), "P = $fav / $total.", rng)
            }
        }
    }

    private fun genGrade8(meta: MathTestMeta, qId: Int, sub: Int, lang: AppLanguage, rng: Random): QuizQuestion {
        val pfx = prefix(meta, lang)
        return when (sub) {
            0 -> {
                val triples = listOf(Triple(3, 4, 5), Triple(5, 12, 13), Triple(6, 8, 10), Triple(8, 15, 17), Triple(9, 12, 15))
                val t = triples[rng.nextInt(triples.size)]
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx To'g'ri burchakli uchburchak katetlari ${t.first} va ${t.second}. Gipotenuza c = ?"
                    AppLanguage.TURKISH -> "$pfx Dik kenarları ${t.first} ve ${t.second} olan dik üçgende hipotenüs c = ?"
                    AppLanguage.RUSSIAN -> "$pfx Катеты прямоугольного треугольника равны ${t.first} и ${t.second}. Найдите гипотенузу c."
                    AppLanguage.ENGLISH -> "$pfx A right triangle has legs ${t.first} and ${t.second}. Hypotenuse c = ?"
                }
                intMcq(qId, prompt, t.third, "c = √(${t.first}² + ${t.second}²) = ${t.third}.", rng)
            }
            1 -> {
                val x1 = rng.nextInt(1, 5)
                val y1 = rng.nextInt(2, 9)
                val dx = rng.nextInt(2, 6)
                val m = rng.nextInt(-4, 6).let { if (it == 0) 3 else it }
                val x2 = x1 + dx
                val y2 = y1 + m * dx
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx ($x1, $y1) va ($x2, $y2) nuqtalardan o'tuvchi to'g'ri chiziq burchak koeffitsiyentini (m) toping."
                    AppLanguage.TURKISH -> "$pfx ($x1, $y1) ve ($x2, $y2) noktalarından geçen doğrunun eğimini (m) bulun."
                    AppLanguage.RUSSIAN -> "$pfx Найдите угловой коэффициент прямой через точки ($x1, $y1) и ($x2, $y2)."
                    AppLanguage.ENGLISH -> "$pfx Find the slope of the line through ($x1, $y1) and ($x2, $y2)."
                }
                intMcq(qId, prompt, m, "m = ($y2 - $y1)/($x2 - $x1) = $m.", rng)
            }
            2 -> {
                val e1 = rng.nextInt(2, 7)
                val e2 = rng.nextInt(2, 6)
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx 2^$e1 × 2^$e2 = 2^k bo'lsa, k nechiga teng?"
                    AppLanguage.TURKISH -> "$pfx 2^$e1 × 2^$e2 = 2^k ifadesinde k kaçtır?"
                    AppLanguage.RUSSIAN -> "$pfx Упростите 2^$e1 × 2^$e2 = 2^k. Чему равно k?"
                    AppLanguage.ENGLISH -> "$pfx Simplify 2^$e1 × 2^$e2 as 2^k. What is k?"
                }
                intMcq(qId, prompt, e1 + e2, "$e1 + $e2 = ${e1 + e2}.", rng)
            }
            else -> {
                val x = rng.nextInt(2, 9)
                val y = rng.nextInt(1, 8)
                val sum = x + y
                val diff = x - y
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx Sistemani yeching: x + y = $sum va x - y = $diff. x nechiga teng?"
                    AppLanguage.TURKISH -> "$pfx Denklem sistemini çözün: x + y = $sum ve x - y = $diff. x kaçtır?"
                    AppLanguage.RUSSIAN -> "$pfx Решите систему: x + y = $sum и x - y = $diff. Найдите x."
                    AppLanguage.ENGLISH -> "$pfx Solve the system: x + y = $sum and x - y = $diff. What is x?"
                }
                intMcq(qId, prompt, x, "2x = ${sum + diff} => x = $x.", rng)
            }
        }
    }

    private fun genGrade9(meta: MathTestMeta, qId: Int, sub: Int, lang: AppLanguage, rng: Random): QuizQuestion {
        val pfx = prefix(meta, lang)
        return when (sub) {
            0 -> {
                val r1 = rng.nextInt(1, 7)
                val r2 = rng.nextInt(r1 + 1, 10)
                val b = -(r1 + r2)
                val c = r1 * r2
                val eq = "x² ${if (b < 0) "- ${abs(b)}" else "+ $b"}x + $c = 0"
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx Kvadrat tenglama ildizlarini toping: $eq"
                    AppLanguage.TURKISH -> "$pfx Denklemin köklerini bulun: $eq"
                    AppLanguage.RUSSIAN -> "$pfx Найдите корни уравнения: $eq"
                    AppLanguage.ENGLISH -> "$pfx Find the solutions to $eq"
                }
                strMcq(qId, prompt, "x = $r1, $r2", listOf("x = ${-r1}, ${-r2}", "x = $r1, ${-r2}", "x = ${r1 + 1}, $r2"), "(x - $r1)(x - $r2) = 0 -> x = $r1, $r2.", rng)
            }
            1 -> {
                val a = rng.nextInt(1, 4)
                val b = rng.nextInt(3, 9)
                val c = rng.nextInt(1, 6)
                val disc = b * b - 4 * a * c
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx ${a}x² + ${b}x + $c = 0 tenglamaning diskriminantini (Δ = b² - 4ac) toping."
                    AppLanguage.TURKISH -> "$pfx ${a}x² + ${b}x + $c = 0 denkleminin diskriminantını (Δ = b² - 4ac) bulun."
                    AppLanguage.RUSSIAN -> "$pfx Вычислите дискриминант Δ = b² - 4ac для ${a}x² + ${b}x + $c = 0."
                    AppLanguage.ENGLISH -> "$pfx Compute the discriminant Δ = b² - 4ac for ${a}x² + ${b}x + $c = 0."
                }
                intMcq(qId, prompt, disc, "Δ = $b² - 4($a)($c) = $disc.", rng)
            }
            2 -> {
                val x = rng.nextInt(-3, 6)
                val a = rng.nextInt(2, 5)
                val k = rng.nextInt(1, 9)
                val ans = a * x * x - k
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx Agar f(x) = ${a}x² - $k bo'lsa, f($x) ni hisoblang."
                    AppLanguage.TURKISH -> "$pfx f(x) = ${a}x² - $k ise f($x) değeri kaçtır?"
                    AppLanguage.RUSSIAN -> "$pfx Дана функция f(x) = ${a}x² - $k. Найдите f($x)."
                    AppLanguage.ENGLISH -> "$pfx Given f(x) = ${a}x² - $k, evaluate f($x)."
                }
                intMcq(qId, prompt, ans, "f($x) = $a(${x * x}) - $k = $ans.", rng)
            }
            else -> {
                val k = rng.nextInt(2, 7)
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx y = x² - ${2 * k}x + 5 parabola uchining x-koordinatasini toping."
                    AppLanguage.TURKISH -> "$pfx y = x² - ${2 * k}x + 5 parabolünün tepe noktasının x koordinatı nedir?"
                    AppLanguage.RUSSIAN -> "$pfx Найдите x-координату вершины параболы y = x² - ${2 * k}x + 5."
                    AppLanguage.ENGLISH -> "$pfx What is the x-coordinate of the vertex of y = x² - ${2 * k}x + 5?"
                }
                intMcq(qId, prompt, k, "x_v = -b/(2a) = ${2 * k}/2 = $k.", rng)
            }
        }
    }

    private fun genGrade10(meta: MathTestMeta, qId: Int, sub: Int, lang: AppLanguage, rng: Random): QuizQuestion {
        val pfx = prefix(meta, lang)
        return when (sub) {
            0 -> {
                val pairs = listOf(
                    Triple("sin(30°)", "1/2", listOf("√3/2", "1", "0")),
                    Triple("cos(60°)", "1/2", listOf("√3/2", "√2/2", "0")),
                    Triple("tan(45°)", "1", listOf("0", "√3", "1/2")),
                    Triple("sin(90°)", "1", listOf("0", "-1", "1/2"))
                )
                val p = pairs[rng.nextInt(pairs.size)]
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx Aniq trigonometrik qiymatini toping: ${p.first}"
                    AppLanguage.TURKISH -> "$pfx Tam trigonometrik değeri bulun: ${p.first}"
                    AppLanguage.RUSSIAN -> "$pfx Найдите точное значение: ${p.first}"
                    AppLanguage.ENGLISH -> "$pfx Evaluate exact trigonometric value: ${p.first}"
                }
                strMcq(qId, prompt, p.second, p.third, "${p.first} = ${p.second}.", rng)
            }
            1 -> {
                val base = listOf(2, 3, 5)[rng.nextInt(3)]
                val exp = rng.nextInt(2, 5)
                var valNum = 1
                repeat(exp) { valNum *= base }
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx Hisoblang: log_$base($valNum)"
                    AppLanguage.TURKISH -> "$pfx Hesaplayın: log_$base($valNum)"
                    AppLanguage.RUSSIAN -> "$pfx Вычислите: log_$base($valNum)"
                    AppLanguage.ENGLISH -> "$pfx Evaluate log_$base($valNum)."
                }
                intMcq(qId, prompt, exp, "$base^$exp = $valNum => log_$base($valNum) = $exp.", rng, minVal = 1)
            }
            2 -> {
                val a1 = rng.nextInt(2, 11)
                val d = rng.nextInt(3, 8)
                val n = rng.nextInt(5, 11)
                val an = a1 + (n - 1) * d
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx Arifmetik progressiyada a₁ = $a1, ayirmasi d = $d. a_$n hadini toping."
                    AppLanguage.TURKISH -> "$pfx İlk terimi a₁ = $a1 ve ortak farkı d = $d olan aritmetik dizinin $n. terimi (a_$n) nedir?"
                    AppLanguage.RUSSIAN -> "$pfx В арифметической прогрессии a₁ = $a1, разность d = $d. Найдите a_$n."
                    AppLanguage.ENGLISH -> "$pfx Arithmetic sequence starts at a₁ = $a1 with common difference d = $d. Find term a_$n."
                }
                intMcq(qId, prompt, an, "a_$n = $a1 + ($n - 1)×$d = $an.", rng)
            }
            else -> {
                val n = rng.nextInt(4, 8)
                val k = 2
                val ans = (n * (n - 1)) / 2
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx C($n, $k) kombinatsiyalar sonini hisoblang."
                    AppLanguage.TURKISH -> "$pfx C($n, $k) kombinasyonunu hesaplayın."
                    AppLanguage.RUSSIAN -> "$pfx Вычислите число сочетаний C($n, $k)."
                    AppLanguage.ENGLISH -> "$pfx Compute the number of combinations C($n, $k)."
                }
                intMcq(qId, prompt, ans, "C($n, 2) = $n × ${n - 1} / 2 = $ans.", rng, minVal = 1)
            }
        }
    }

    private fun genGrade11(meta: MathTestMeta, qId: Int, sub: Int, lang: AppLanguage, rng: Random): QuizQuestion {
        val pfx = prefix(meta, lang)
        return when (sub) {
            0 -> {
                val c = rng.nextInt(2, 8)
                val p = rng.nextInt(2, 6)
                val x = rng.nextInt(1, 4)
                var xPow = 1
                repeat(p - 1) { xPow *= x }
                val derivAtX = c * p * xPow
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx Agar f(x) = ${c}x^$p bo'lsa, f'($x) hosilani toping."
                    AppLanguage.TURKISH -> "$pfx f(x) = ${c}x^$p fonksiyonu için f'($x) türevini bulun."
                    AppLanguage.RUSSIAN -> "$pfx Если f(x) = ${c}x^$p, найдите производную f'($x)."
                    AppLanguage.ENGLISH -> "$pfx If f(x) = ${c}x^$p, find f'($x)."
                }
                intMcq(qId, prompt, derivAtX, "f'(x) = ${c * p}x^${p - 1} => f'($x) = $derivAtX.", rng, minVal = 1)
            }
            1 -> {
                val k = rng.nextInt(1, 5)
                val b = rng.nextInt(2, 5)
                val ans = k * b * b * b
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx Aniq integralni hisoblang: ∫[0..$b] ${3 * k}x² dx"
                    AppLanguage.TURKISH -> "$pfx Belirli integrali hesaplayın: ∫[0..$b] ${3 * k}x² dx"
                    AppLanguage.RUSSIAN -> "$pfx Вычислите определённый интеграл: ∫[0..$b] ${3 * k}x² dx"
                    AppLanguage.ENGLISH -> "$pfx Evaluate the definite integral ∫[0..$b] ${3 * k}x² dx."
                }
                intMcq(qId, prompt, ans, "F(x) = ${k}x³ => F($b) - F(0) = $ans.", rng, minVal = 1)
            }
            2 -> {
                val a = rng.nextInt(2, 7)
                val b = rng.nextInt(1, 6)
                val c = rng.nextInt(1, 5)
                val d = rng.nextInt(2, 7)
                val dot = a * c + b * d
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx u = ($a, $b) va v = ($c, $d) vektorlarining skalyar ko'paytmasini (u · v) toping."
                    AppLanguage.TURKISH -> "$pfx u = ($a, $b) ve v = ($c, $d) vektörlerinin nokta çarpımını (u · v) bulun."
                    AppLanguage.RUSSIAN -> "$pfx Найдите скалярное произведение u · v для u = ($a, $b) и v = ($c, $d)."
                    AppLanguage.ENGLISH -> "$pfx Find the dot product u · v for u = ($a, $b) and v = ($c, $d)."
                }
                intMcq(qId, prompt, dot, "u · v = ($a×$c) + ($b×$d) = $dot.", rng)
            }
            else -> {
                val a = rng.nextInt(2, 8)
                val b = rng.nextInt(1, 6)
                val c = rng.nextInt(1, 5)
                val d = rng.nextInt(2, 8)
                val det = a * d - b * c
                val prompt = when (lang) {
                    AppLanguage.UZBEK -> "$pfx [[$a, $b], [$c, $d]] 2×2 matritsa determinantini toping."
                    AppLanguage.TURKISH -> "$pfx [[$a, $b], [$c, $d]] 2×2 matrisinin determinantını bulun."
                    AppLanguage.RUSSIAN -> "$pfx Найдите определитель матрицы 2×2: [[$a, $b], [$c, $d]]."
                    AppLanguage.ENGLISH -> "$pfx Find the determinant of 2×2 matrix [[$a, $b], [$c, $d]]."
                }
                intMcq(qId, prompt, det, "det = ($a×$d) - ($b×$c) = $det.", rng)
            }
        }
    }

    private fun intMcq(
        id: Int,
        prompt: String,
        correct: Int,
        explanation: String,
        rng: Random,
        minVal: Int = Int.MIN_VALUE
    ): QuizQuestion {
        val distractors = LinkedHashSet<Int>()
        val offsets = listOf(-3, -2, -1, 1, 2, 3, 4, -4, 5, -5, 10, -10).shuffled(rng)
        for (off in offsets) {
            val cand = correct + off
            if (cand != correct && cand >= minVal) {
                distractors.add(cand)
                if (distractors.size == 3) break
            }
        }
        var fallback = correct + 1
        while (distractors.size < 3) {
            if (fallback != correct && fallback >= minVal) distractors.add(fallback)
            fallback++
        }
        return strMcq(id, prompt, correct.toString(), distractors.map { it.toString() }, explanation, rng)
    }

    private fun strMcq(
        id: Int,
        prompt: String,
        correct: String,
        distractors: List<String>,
        explanation: String,
        rng: Random
    ): QuizQuestion {
        val options = (listOf(correct) + distractors.filter { it != correct }.distinct().take(3)).toMutableList()
        while (options.size < 4) {
            options.add("${options.size + 10}")
        }
        options.shuffle(rng)
        return QuizQuestion(
            id = id,
            prompt = prompt,
            options = options,
            correctIndex = options.indexOf(correct),
            explanation = explanation
        )
    }
}
