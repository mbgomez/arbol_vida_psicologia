package com.netah.hakkam.numyah.mind.app.observability

import android.content.Context
import android.os.Bundle
import com.netah.hakkam.numyah.mind.BuildConfig
import com.netah.hakkam.numyah.mind.domain.model.SephiraId
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

enum class AssessmentEntrySource(val analyticsValue: String) {
    HOME("home"),
    ASSESSMENTS("assessments"),
    RESULTS("results")
}

enum class AssessmentEntryMode(val analyticsValue: String) {
    START("start"),
    RESUME("resume"),
    START_FRESH("start_fresh")
}

enum class OnboardingCompletionMethod(val analyticsValue: String) {
    FINISH("finish"),
    SKIP("skip")
}

enum class ResultsSessionScope(val analyticsValue: String) {
    LATEST("latest"),
    SAVED("saved")
}

enum class SettingsChangeKey(val analyticsValue: String) {
    LANGUAGE("language"),
    THEME("theme"),
    HONESTY_NOTICE("honesty_notice")
}

enum class NonFatalIssueKey(val analyticsValue: String) {
    ASSESSMENT_LOAD_FAILED("assessment_load_failed"),
    ASSESSMENT_SAVE_ANSWER_FAILED("assessment_save_answer_failed"),
    ASSESSMENT_CONTINUE_FAILED("assessment_continue_failed"),
    ASSESSMENT_GO_BACK_FAILED("assessment_go_back_failed"),
    RESULTS_LOAD_FAILED("results_load_failed"),
    SEPHIRA_DETAIL_LOAD_FAILED("sephira_detail_load_failed"),
    HISTORY_LOAD_FAILED("history_load_failed"),
    LEARN_CATALOG_LOAD_FAILED("learn_catalog_load_failed"),
    LEARN_COURSE_LOAD_FAILED("learn_course_load_failed"),
    LEARN_SECTION_LOAD_FAILED("learn_section_load_failed"),
    LEARN_SECTION_COMPLETE_FAILED("learn_section_complete_failed"),
    TESTER_VERIFICATION_NON_FATAL("tester_verification_non_fatal")
}

interface AppTelemetry {
    fun initialize()
    fun trackOnboardingCompleted(method: OnboardingCompletionMethod)
    fun trackAssessmentEntry(source: AssessmentEntrySource, mode: AssessmentEntryMode)
    fun trackAssessmentFreshStartConfirmed(source: AssessmentEntrySource)
    fun trackAssessmentCompleted(completedSephirotCount: Int)
    fun trackResultsDetailOpened(sephiraId: SephiraId, sessionScope: ResultsSessionScope)
    fun trackHistorySessionOpened()
    fun trackLearnSectionOpened(courseId: String, sectionId: String, sectionOrder: Int)
    fun trackSettingChanged(key: SettingsChangeKey, value: String)
    fun trackOnboardingReplayed()
    fun recordNonFatal(
        key: NonFatalIssueKey,
        throwable: Throwable? = null,
        attributes: Map<String, String> = emptyMap()
    )
}

@Singleton
class DefaultAppTelemetry @Inject constructor(
    context: Context
) : AppTelemetry {

    private val firebaseBridge = FirebaseTelemetryBridge.createIfEnabled(context)

    override fun initialize() {
        if (!BuildConfig.DEBUG) {
            return
        }
        when {
            BuildConfig.ENABLE_TESTER_OBSERVABILITY && firebaseBridge != null -> {
                Timber.i("Tester observability enabled with Firebase backend.")
            }
            BuildConfig.ENABLE_TESTER_OBSERVABILITY -> {
                Timber.i("Tester observability enabled without Firebase backend; using Timber only.")
            }
            else -> {
                Timber.i("Tester observability disabled; debug logs remain local only.")
            }
        }
    }

    override fun trackOnboardingCompleted(method: OnboardingCompletionMethod) {
        logEvent(
            name = "onboarding_completed",
            parameters = mapOf("completion_method" to method.analyticsValue)
        )
    }

    override fun trackAssessmentEntry(
        source: AssessmentEntrySource,
        mode: AssessmentEntryMode
    ) {
        logEvent(
            name = "assessment_entry",
            parameters = mapOf(
                "source" to source.analyticsValue,
                "mode" to mode.analyticsValue
            )
        )
    }

    override fun trackAssessmentFreshStartConfirmed(source: AssessmentEntrySource) {
        logEvent(
            name = "assessment_start_fresh_confirmed",
            parameters = mapOf("source" to source.analyticsValue)
        )
    }

    override fun trackAssessmentCompleted(completedSephirotCount: Int) {
        logEvent(
            name = "assessment_completed",
            parameters = mapOf("completed_sephirot_count" to completedSephirotCount.toString())
        )
    }

    override fun trackResultsDetailOpened(
        sephiraId: SephiraId,
        sessionScope: ResultsSessionScope
    ) {
        logEvent(
            name = "results_detail_opened",
            parameters = mapOf(
                "sephira_id" to sephiraId.name.lowercase(),
                "session_scope" to sessionScope.analyticsValue
            )
        )
    }

    override fun trackHistorySessionOpened() {
        logEvent(name = "history_session_opened")
    }

    override fun trackLearnSectionOpened(
        courseId: String,
        sectionId: String,
        sectionOrder: Int
    ) {
        logEvent(
            name = "learn_section_opened",
            parameters = mapOf(
                "course_id" to courseId,
                "section_id" to sectionId,
                "section_order" to sectionOrder.toString()
            )
        )
    }

    override fun trackSettingChanged(key: SettingsChangeKey, value: String) {
        logEvent(
            name = "settings_changed",
            parameters = mapOf(
                "setting_key" to key.analyticsValue,
                "setting_value" to value
            )
        )
    }

    override fun trackOnboardingReplayed() {
        logEvent(name = "onboarding_replayed")
    }

    override fun recordNonFatal(
        key: NonFatalIssueKey,
        throwable: Throwable?,
        attributes: Map<String, String>
    ) {
        val mergedAttributes = linkedMapOf("issue_key" to key.analyticsValue).apply {
            putAll(attributes)
        }
        val message = mergedAttributes.entries.joinToString(
            prefix = "Non-fatal issue recorded: ${key.analyticsValue}",
            separator = ", "
        ) { (attributeKey, attributeValue) ->
            "$attributeKey=$attributeValue"
        }

        if (throwable != null) {
            Timber.e(throwable, message)
        } else {
            Timber.e(message)
        }

        firebaseBridge?.recordNonFatal(
            key = key.analyticsValue,
            throwable = throwable,
            attributes = mergedAttributes
        )
    }

    private fun logEvent(
        name: String,
        parameters: Map<String, String> = emptyMap()
    ) {
        if (BuildConfig.DEBUG) {
            val detail = if (parameters.isEmpty()) {
                name
            } else {
                "$name ${parameters.entries.joinToString(separator = " ") { "${it.key}=${it.value}" }}"
            }
            Timber.i("Telemetry %s", detail)
        }

        firebaseBridge?.logEvent(name, parameters)
    }
}

private class FirebaseTelemetryBridge private constructor(
    private val analyticsBridge: FirebaseAnalyticsBridge,
    private val crashlyticsBridge: FirebaseCrashlyticsBridge
) {

    fun logEvent(name: String, parameters: Map<String, String>) {
        analyticsBridge.logEvent(name, parameters)
    }

    fun recordNonFatal(
        key: String,
        throwable: Throwable?,
        attributes: Map<String, String>
    ) {
        crashlyticsBridge.recordNonFatal(key, throwable, attributes)
    }

    companion object {
        fun createIfEnabled(context: Context): FirebaseTelemetryBridge? {
            if (!BuildConfig.ENABLE_TESTER_OBSERVABILITY || !BuildConfig.ENABLE_FIREBASE_BACKEND) {
                return null
            }

            val analyticsBridge = FirebaseAnalyticsBridge.create(context) ?: return null
            val crashlyticsBridge = FirebaseCrashlyticsBridge.create() ?: return null
            return FirebaseTelemetryBridge(
                analyticsBridge = analyticsBridge,
                crashlyticsBridge = crashlyticsBridge
            )
        }
    }
}

private class FirebaseAnalyticsBridge private constructor(
    private val analyticsInstance: Any,
    private val logEventMethod: java.lang.reflect.Method
) {

    fun logEvent(
        name: String,
        parameters: Map<String, String>
    ) {
        val bundle = Bundle().apply {
            parameters.forEach { (key, value) ->
                putString(key, value)
            }
        }
        logEventMethod.invoke(analyticsInstance, name, bundle)
    }

    companion object {
        fun create(context: Context): FirebaseAnalyticsBridge? {
            return runCatching {
                val analyticsClass = Class.forName("com.google.firebase.analytics.FirebaseAnalytics")
                val getInstanceMethod = analyticsClass.getMethod("getInstance", Context::class.java)
                val analyticsInstance = getInstanceMethod.invoke(null, context)
                val logEventMethod = analyticsClass.getMethod(
                    "logEvent",
                    String::class.java,
                    Bundle::class.java
                )
                FirebaseAnalyticsBridge(
                    analyticsInstance = analyticsInstance,
                    logEventMethod = logEventMethod
                )
            }.getOrNull()
        }
    }
}

private class FirebaseCrashlyticsBridge private constructor(
    private val crashlyticsInstance: Any,
    private val recordExceptionMethod: java.lang.reflect.Method,
    private val setCustomKeyMethod: java.lang.reflect.Method,
    private val logMethod: java.lang.reflect.Method
) {

    fun recordNonFatal(
        key: String,
        throwable: Throwable?,
        attributes: Map<String, String>
    ) {
        attributes.forEach { (attributeKey, attributeValue) ->
            setCustomKeyMethod.invoke(crashlyticsInstance, attributeKey, attributeValue)
        }
        logMethod.invoke(crashlyticsInstance, key)
        recordExceptionMethod.invoke(
            crashlyticsInstance,
            throwable ?: IllegalStateException("Recoverable issue: $key")
        )
    }

    companion object {
        fun create(): FirebaseCrashlyticsBridge? {
            return runCatching {
                val crashlyticsClass = Class.forName("com.google.firebase.crashlytics.FirebaseCrashlytics")
                val getInstanceMethod = crashlyticsClass.getMethod("getInstance")
                val crashlyticsInstance = getInstanceMethod.invoke(null)
                FirebaseCrashlyticsBridge(
                    crashlyticsInstance = crashlyticsInstance,
                    recordExceptionMethod = crashlyticsClass.getMethod(
                        "recordException",
                        Throwable::class.java
                    ),
                    setCustomKeyMethod = crashlyticsClass.getMethod(
                        "setCustomKey",
                        String::class.java,
                        String::class.java
                    ),
                    logMethod = crashlyticsClass.getMethod("log", String::class.java)
                )
            }.getOrNull()
        }
    }
}
