package com.netah.hakkam.numyah.mind.data.repository

import com.netah.hakkam.numyah.mind.data.local.content.JsonLearningContentDataSource
import com.netah.hakkam.numyah.mind.extension.CoroutinesTestRule
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.util.Locale
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LocalLearningContentRepositoryTests {

    private lateinit var repository: LocalLearningContentRepository

    @get:Rule
    var coroutinesRule = CoroutinesTestRule()

    @Before
    fun setup() {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        repository = LocalLearningContentRepository(
            jsonLearningContentDataSource = JsonLearningContentDataSource(
                jsonLoader = { TEST_LEARNING_JSON },
                moshi = moshi
            )
        )
    }

    @Test
    fun getCatalog_returnsLocalizedEnglishCourse() = coroutinesRule.runBlockingTest {
        val catalog = repository.getCatalog(Locale.ENGLISH).first()

        assertEquals("learning-v1", catalog.version)
        assertEquals("Courses", catalog.title)
        assertEquals(1, catalog.courses.size)
        assertEquals("Tree of Life overview", catalog.courses.first().title)
        assertEquals(3, catalog.courses.first().availableSectionCount)
        assertEquals(11, catalog.courses.first().totalSectionCount)
    }

    @Test
    fun getCourse_returnsSpanishCourseCopy() = coroutinesRule.runBlockingTest {
        val course = repository.getCourse("tree-overview", Locale("es")).first()

        assertEquals("Panorama del Arbol de la Vida", course?.title)
        assertTrue(course?.description?.contains("Introduccion") == true)
        assertEquals("Introduccion", course?.sections?.first()?.title)
    }

    @Test
    fun getSection_returnsRequestedSectionContent() = coroutinesRule.runBlockingTest {
        val section = repository.getSection("tree-overview", "yesod", Locale.ENGLISH).first()

        assertEquals("Yesod", section?.title)
        assertEquals(3, section?.order)
        assertEquals(2, section?.content?.size)
        assertTrue(section?.summary?.contains("relationship") == true)
    }

    private companion object {
        val TEST_LEARNING_JSON = """
            {
              "version": "learning-v1",
              "title": {
                "en": "Courses",
                "es": "Cursos"
              },
              "courses": [
                {
                  "id": "tree-overview",
                  "title": {
                    "en": "Tree of Life overview",
                    "es": "Panorama del Arbol de la Vida"
                  },
                  "subtitle": {
                    "en": "A practical reading path",
                    "es": "Un recorrido practico de lectura"
                  },
                  "description": {
                    "en": "Introduction, Malkuth, and Yesod are currently available.",
                    "es": "Introduccion, Malkuth y Yesod estan disponibles por ahora."
                  },
                  "estimatedMinutes": 24,
                  "totalSectionCount": 11,
                  "sections": [
                    {
                      "id": "introduction",
                      "title": {
                        "en": "Introduction",
                        "es": "Introduccion"
                      },
                      "summary": {
                        "en": "An orientation to the Tree.",
                        "es": "Una orientacion al Arbol."
                      },
                      "readingTimeMinutes": 8,
                      "order": 1,
                      "content": [
                        {
                          "en": "The Tree is a map.",
                          "es": "El Arbol es un mapa."
                        }
                      ]
                    },
                    {
                      "id": "malkuth",
                      "title": {
                        "en": "Malkuth",
                        "es": "Malkuth"
                      },
                      "summary": {
                        "en": "Material life and embodiment.",
                        "es": "Vida material y cuerpo."
                      },
                      "readingTimeMinutes": 8,
                      "order": 2,
                      "content": [
                        {
                          "en": "Malkuth explores practical reality.",
                          "es": "Malkuth explora la realidad practica."
                        }
                      ]
                    },
                    {
                      "id": "yesod",
                      "title": {
                        "en": "Yesod",
                        "es": "Yesod"
                      },
                      "summary": {
                        "en": "Yesod explores relationship and inner foundation.",
                        "es": "Yesod explora la relacion y la base interior."
                      },
                      "readingTimeMinutes": 8,
                      "order": 3,
                      "content": [
                        {
                          "en": "Yesod is a relational foundation.",
                          "es": "Yesod es una base relacional."
                        },
                        {
                          "en": "Balance includes time with others and alone.",
                          "es": "El balance incluye tiempo con otros y a solas."
                        }
                      ]
                    }
                  ]
                }
              ]
            }
        """.trimIndent()
    }
}
