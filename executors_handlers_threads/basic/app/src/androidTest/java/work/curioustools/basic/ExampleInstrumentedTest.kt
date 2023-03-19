package work.curioustools.basic

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented work.curioustools.basic.utils.test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under work.curioustools.basic.utils.test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("work.curioustools.basic", appContext.packageName)
    }
}