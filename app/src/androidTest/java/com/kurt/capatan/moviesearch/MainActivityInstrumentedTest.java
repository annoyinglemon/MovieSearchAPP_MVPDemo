package com.kurt.capatan.moviesearch;

import android.content.ComponentName;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.IntDef;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.kurt.capatan.moviesearch.view.MainActivity;
import com.kurt.capatan.moviesearch.view.MovieDetailsActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityInstrumentedTest {

    @Rule
    public final IntentsTestRule<MainActivity> mainActivity = new IntentsTestRule<>(MainActivity.class);

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = getTargetContext();

        assertEquals("com.kurt.capatan.moviesearch", appContext.getPackageName());
    }

    @Test
    public void typeSearchQueryThenHitSearchIconWithResults() throws Exception {
        onView(withId(R.id.etSearch)).perform(ViewActions.typeText("man"));
        onView(withId(R.id.etSearch)).perform(new ClickDrawableAction(ClickDrawableAction.Right));
        onView(withId(R.id.rvMovies)).check(matches(hasDescendant(withId(R.id.cvMovieItem))));
    }

    @Test
    public void executeSearchThenTapOnAnItem() throws Exception {
        onView(withId(R.id.etSearch)).perform(ViewActions.typeText("man"));
        onView(withId(R.id.etSearch)).perform(new ClickDrawableAction(ClickDrawableAction.Right));
        onView(withId(R.id.rvMovies)).check(matches(hasDescendant(withId(R.id.cvMovieItem))));
        onView(withId(R.id.rvMovies)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
//        intended(hasComponent(new ComponentName(getTargetContext(), MovieDetailsActivity.class)));
        intended(hasComponent(new ComponentName(getTargetContext(), MovieDetailsActivity.class.getName())));
//        onView(withId(R.id.rvMovies)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withId(R.id.ivThumbImage)), click()));


    }


    public static class ClickDrawableAction implements ViewAction {
        public static final int Left = 0;
        public static final int Top = 1;
        public static final int Right = 2;
        public static final int Bottom = 3;

        @Location
        private final int drawableLocation;

        public ClickDrawableAction(@Location int drawableLocation)
        {
            this.drawableLocation = drawableLocation;
        }

        @Override
        public Matcher<View> getConstraints()
        {
            return allOf(isAssignableFrom(TextView.class), new BoundedMatcher<View, TextView>(TextView.class)
            {
                @Override
                protected boolean matchesSafely(final TextView tv)
                {
                    //get focus so drawables are visible and if the textview has a drawable in the position then return a match
                    return tv.requestFocusFromTouch() && tv.getCompoundDrawables()[drawableLocation] != null;

                }

                @Override
                public void describeTo(Description description)
                {
                    description.appendText("has drawable");
                }
            });
        }

        @Override
        public String getDescription()
        {
            return "click drawable ";
        }

        @Override
        public void perform(final UiController uiController, final View view)
        {
            TextView tv = (TextView)view;//we matched
            if(tv != null && tv.requestFocusFromTouch())//get focus so drawables are visible
            {
                //get the bounds of the drawable image
                Rect drawableBounds = tv.getCompoundDrawables()[drawableLocation].getBounds();

                //calculate the drawable click location for left, top, right, bottom
                final Point[] clickPoint = new Point[4];
                clickPoint[Left] = new Point(tv.getLeft() + (drawableBounds.width() / 2), (int)(tv.getPivotY() + (drawableBounds.height() / 2)));
                clickPoint[Top] = new Point((int)(tv.getPivotX() + (drawableBounds.width() / 2)), tv.getTop() + (drawableBounds.height() / 2));
                clickPoint[Right] = new Point(tv.getRight() + (drawableBounds.width() / 2), (int)(tv.getPivotY() + (drawableBounds.height() / 2)));
                clickPoint[Bottom] = new Point((int)(tv.getPivotX() + (drawableBounds.width() / 2)), tv.getBottom() + (drawableBounds.height() / 2));

                if(tv.dispatchTouchEvent(MotionEvent.obtain(android.os.SystemClock.uptimeMillis(), android.os.SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, clickPoint[drawableLocation].x, clickPoint[drawableLocation].y, 0)))
                    tv.dispatchTouchEvent(MotionEvent.obtain(android.os.SystemClock.uptimeMillis(), android.os.SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, clickPoint[drawableLocation].x, clickPoint[drawableLocation].y, 0));
            }
        }

        @IntDef({ Left, Top, Right, Bottom })
        @Retention(RetentionPolicy.SOURCE)
        public @interface Location{}
    }
}
