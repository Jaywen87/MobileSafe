package com.wenjie.mobilesafe;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.wenjie.mobilesafe.db.BlackNumberDBOpenHelper;
import com.wenjie.mobilesafe.db.dao.BlackNumberDao;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.wenjie.mobilesafe", appContext.getPackageName());
    }

    @Test
    public void addTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        BlackNumberDao dao = new BlackNumberDao(appContext);
        dao.add("13723744131","1");
        dao.add("18937688491", "2");
    }
}