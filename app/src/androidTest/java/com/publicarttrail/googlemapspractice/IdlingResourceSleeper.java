package com.publicarttrail.googlemapspractice;

import androidx.test.espresso.IdlingResource;

/**
 * Have functions to sleep the processor because assertions are not linked to
 * {@link IdlingResource} to do assertions, so should be used before asserts if there's an
 * idle process.
 */
public class IdlingResourceSleeper {

    private static final int SLEEPS_LIMIT = 50;
    private static final int SLEEPS_TIME = 10;

    /**
     * Used to sleep {@link IdlingResourceSleeper#SLEEPS_LIMIT} times and
     * {@link IdlingResourceSleeper#SLEEPS_TIME} ms until idlingResource.isIdleNow() is false.
     *
     * @param idlingResource
     */
    public static void sleep(IdlingResource idlingResource) {
        int sleeps = 0;
        while (!idlingResource.isIdleNow() || sleeps < SLEEPS_LIMIT) {
            android.os.SystemClock.sleep(SLEEPS_TIME);
            sleeps++;
        }

    }
}