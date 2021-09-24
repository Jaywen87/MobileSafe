package com.wenjie.mobilesafe;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.wenjie.mobilesafe.db.BlackNumberDBOpenHelper;
import com.wenjie.mobilesafe.db.dao.BlackNumberDao;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

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
        //dao.add("13723744131","1");
        //dao.add("18937688491", "2");

        Random random = new Random();
        long basenumber = 13723744132L;
        for (int i = 0; i < 300; i++) {
           // Log.i(TAG, "onCreate:fd " + String.valueOf(basenumber + i)+ ","+String.valueOf(random.nextInt(3)+1));
            dao.add(String.valueOf(basenumber + i),String.valueOf(random.nextInt(3)+1));
        }
    }
}