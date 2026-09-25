package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ai.RecommendationEngine
import com.example.data.local.InitialData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("GATE 2027 Prep", appName)
  }

  @Test
  fun `test recommendation engine generates high priority recommendations for weak areas`() {
    val engine = RecommendationEngine()
    val topics = InitialData.getTopics()
    val questions = InitialData.getQuestions()
    val mockTests = InitialData.getMockTests()

    val cseRecs = engine.generateHeuristicRecommendations(topics, questions, mockTests, "CSE")
    assertTrue(cseRecs.isNotEmpty())
    assertNotNull(cseRecs.first().rationale)
    assertTrue(cseRecs.first().actionText.isNotBlank())

    val daRecs = engine.generateHeuristicRecommendations(topics, questions, mockTests, "DA")
    assertTrue(daRecs.isNotEmpty())
    assertEquals("DA", daRecs.first().stream)
  }
}

