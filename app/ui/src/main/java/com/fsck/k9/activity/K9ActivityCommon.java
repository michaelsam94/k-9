package com.fsck.k9.activity;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.fsck.k9.K9;
import com.fsck.k9.K9.Theme;
import com.fsck.k9.activity.misc.SwipeGestureDetector;
import com.fsck.k9.activity.misc.SwipeGestureDetector.OnSwipeGestureListener;
import com.fsck.k9.ui.R;


/**
 * This class implements functionality common to most activities used in K-9 Mail.
 *
 * @see K9Activity
 * @see K9ListActivity
 */
public class K9ActivityCommon {
    /**
     * Creates a new instance of {@link K9ActivityCommon} bound to the specified activity.
     *
     * @param activity
     *         The {@link Activity} the returned {@code K9ActivityCommon} instance will be bound to.
     *
     * @return The {@link K9ActivityCommon} instance that will provide the base functionality of the
     *         "K9" activities.
     */
    public static K9ActivityCommon newInstance(Activity activity) {
        return new K9ActivityCommon(activity);
    }

    public static void setLanguage(Context context, String language) {
        Locale locale;
        if (TextUtils.isEmpty(language)) {
            locale = Resources.getSystem().getConfiguration().locale;
        } else if (language.length() == 5 && language.charAt(2) == '_') {
            // language is in the form: en_US
            locale = new Locale(language.substring(0, 2), language.substring(3));
        } else {
            locale = new Locale(language);
        }

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.locale = locale;
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }


    /**
     * Base activities need to implement this interface.
     *
     * <p>The implementing class simply has to call through to the implementation of these methods
     * in {@link K9ActivityCommon}.</p>
     */
    public interface K9ActivityMagic {
        void setupGestureDetector(OnSwipeGestureListener listener);
    }


    private Activity mActivity;
    private GestureDetector mGestureDetector;


    private K9ActivityCommon(Activity activity) {
        mActivity = activity;
        setLanguage(mActivity, K9.getK9Language());
        mActivity.setTheme(getK9ThemeResourceId());
        if(K9.enableIncoginto()) applyIncogintoToTheme(mActivity);
    }

    /**
     * Call this before calling {@code super.dispatchTouchEvent(MotionEvent)}.
     */
    public void preDispatchTouchEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            mGestureDetector.onTouchEvent(event);
        }
    }


    public void applyIncogintoToTheme(Context context){
        switch (K9.getK9Theme()){
            case DARK:
                context.setTheme(R.style.Theme_K9_Dark_Incoginto);
                break;
            case LIGHT:
                context.setTheme(R.style.Theme_K9_Light_Incoginto);
                break;
        }
    }

    /**
     * Get the background color of the theme used for this activity.
     *
     * @return The background color of the current theme.
     */
    public int getThemeBackgroundColor() {
        TypedArray array = mActivity.getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.colorBackground });

        int backgroundColor = array.getColor(0, 0xFF00FF);

        array.recycle();

        return backgroundColor;
    }

    /**
     * Call this if you wish to use the swipe gesture detector.
     *
     * @param listener
     *         A listener that will be notified if a left to right or right to left swipe has been
     *         detected.
     */
    public void setupGestureDetector(OnSwipeGestureListener listener) {
        mGestureDetector = new GestureDetector(mActivity,
                new SwipeGestureDetector(mActivity, listener));
    }

    public static int getK9ThemeResourceId(Theme themeId) {
        return (themeId == Theme.LIGHT) ? R.style.Theme_K9_Light : R.style.Theme_K9_Dark;
    }

    public static int getK9ActionBarThemeResourceId() {
        return (getK9ThemeResourceId() == R.style.Theme_K9_Light) ?
                R.style.Theme_K9_Light_ActionBar : R.style.Theme_K9_Dark_ActionBar;
    }

    public static int getK9ThemeResourceId() {
        return getK9ThemeResourceId(K9.getK9Theme());
    }
}
