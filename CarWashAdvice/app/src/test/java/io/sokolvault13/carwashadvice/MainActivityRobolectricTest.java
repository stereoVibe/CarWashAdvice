package io.sokolvault13.carwashadvice;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;
import static org.robolectric.Robolectric.setupActivity;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 25)
public class MainActivityRobolectricTest {
    @Before
    public void setUp() throws Exception {
        setupActivity(MainActivity.class);
    }

    @Test
    public void shouldReturnTrue(){
        assertEquals(1, 1);
    }
}