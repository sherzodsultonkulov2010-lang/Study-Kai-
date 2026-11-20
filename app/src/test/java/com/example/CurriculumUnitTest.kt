package com.example

import com.example.data.curriculum.EnglishCurriculum
import com.example.data.curriculum.MathCurriculum
import com.example.data.curriculum.MathFormulas
import com.example.util.MathEvaluator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CurriculumUnitTest {
    @Test
    fun englishCurriculumHas500TopicsWith30QuestionsEach() {
        val topics = EnglishCurriculum.allTopics
        assertEquals(500, topics.size)
        val cefrCounts = topics.groupingBy { it.cefr }.eachCount()
        assertEquals(6, cefrCounts.size)
        // Verify sample topics across A1..C2 each generate 30 valid MCQs
        listOf(topics.first(), topics[100], topics[250], topics[400], topics.last()).forEach { topic ->
            val questions = EnglishCurriculum.generateTestQuestions(topic)
            assertEquals(30, questions.size)
            questions.forEach { q ->
                assertEquals(4, q.options.size)
                assertTrue(q.correctIndex in 0..3)
            }
        }
    }

    @Test
    fun mathCurriculumHasGrades0To11With70TestsAnd30QuestionsEach() {
        assertEquals((0..11).toList(), MathCurriculum.grades)
        for (grade in 0..11) {
            val tests = MathCurriculum.getTestsForGrade(grade)
            assertEquals(70, tests.size)
            val sampleFirst = MathCurriculum.generateTestQuestions(tests.first())
            val sampleLast = MathCurriculum.generateTestQuestions(tests.last())
            assertEquals(30, sampleFirst.size)
            assertEquals(30, sampleLast.size)
            (sampleFirst + sampleLast).forEach { q ->
                assertEquals(4, q.options.size)
                assertTrue(q.correctIndex in 0..3)
            }
        }
    }

    @Test
    fun mathFormulasAndEvaluatorWork() {
        assertTrue(MathFormulas.allFormulas.size >= 40)
        val res = MathEvaluator.evaluate("x^2 - 4", 3.0)
        assertNotNull(res)
        assertEquals(5.0, res!!, 1e-6)
        val trig = MathEvaluator.evaluate("cos(0) + 2*3", 0.0)
        assertEquals(7.0, trig!!, 1e-6)
    }

    @Test
    fun localizedQuestionsMatchSelectedLanguage() {
        val topic = EnglishCurriculum.allTopics.first()
        val uzEng = EnglishCurriculum.generateTestQuestions(topic, com.example.i18n.AppLanguage.UZBEK)
        val ruEng = EnglishCurriculum.generateTestQuestions(topic, com.example.i18n.AppLanguage.RUSSIAN)
        assertEquals(30, uzEng.size)
        assertEquals(30, ruEng.size)
        assertTrue(uzEng.first().prompt.contains("qoidasi"))
        assertTrue(ruEng.first().prompt.contains("Выберите"))

        val trMeta = MathCurriculum.getTestsForGrade(0, com.example.i18n.AppLanguage.TURKISH).first()
        val trMath = MathCurriculum.generateTestQuestions(trMeta, com.example.i18n.AppLanguage.TURKISH)
        assertEquals(30, trMath.size)
        assertTrue(trMath.first().prompt.contains("0. Sınıf"))
    }

    @Test
    fun adminPasswordHashIsSaltedAndVerified() {
        val hash1 = com.example.util.SecurityUtil.hashAdminPassword("MySecret#2026")
        val hash2 = com.example.util.SecurityUtil.hashAdminPassword("MySecret#2026")
        org.junit.Assert.assertNotEquals(hash1, hash2)
        assertTrue(com.example.util.SecurityUtil.verifyAdminPassword("MySecret#2026", hash1))
        org.junit.Assert.assertFalse(com.example.util.SecurityUtil.verifyAdminPassword("wrongpass", hash1))
    }
}
