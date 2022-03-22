package com.sophimp.are

import android.text.SpannableStringBuilder
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sophimp.are.utils.Util
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.reflect.Method
import java.lang.reflect.Type

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.sophimp.are.test", appContext.packageName)
    }

    @Test
    fun getSpansTest() {
        val fmt = "%24s: %s%n"
        try {
            val c = SpannableStringBuilder::class.java
            val allMethods = c.declaredMethods
            for (m in allMethods) {
                if (m.name != "getSpans") {
                    continue
                }
                System.out.format("%s%n", m.toGenericString())
                System.out.format(fmt, "ReturnType", m.returnType)
                System.out.format(fmt, "GenericReturnType", m.genericReturnType)
                val pType = m.parameterTypes
                val gpType: Array<Type> = m.genericParameterTypes
                for (i in pType.indices) {
                    System.out.format(fmt, "ParameterType", pType[i])
                    System.out.format(fmt, "GenericParameterType", gpType[i])
                }
                val xType = m.exceptionTypes
                val gxType: Array<Type> = m.genericExceptionTypes
                for (i in xType.indices) {
                    System.out.format(fmt, "ExceptionType", xType[i])
                    System.out.format(fmt, "GenericExceptionType", gxType[i])
                }
            }
            // production code should handle these exceptions more gracefully
        } catch (x: ClassNotFoundException) {
            x.printStackTrace()
        }

        try {
            var getSpanMethodWithNoSort: Method? = SpannableStringBuilder::class.java.getDeclaredMethod(
                "getSpans",
                Int::class.java,
                Int::class.java,
                Class::class.java,
                Boolean::class.java
            )
            Util.log(getSpanMethodWithNoSort.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}