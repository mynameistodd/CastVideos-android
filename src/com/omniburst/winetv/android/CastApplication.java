/*
 * Copyright (C) 2013 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.omniburst.winetv.android;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.sample.castcompanionlibrary.cast.VideoCastManager;
import com.google.sample.castcompanionlibrary.utils.Utils;
import com.omniburst.winetv.android.settings.CastPreference;

import java.util.HashMap;

/**
 * The {@link Application} for this demo application.
 */
public class CastApplication extends Application {
    private static String PROPERTY_ID;
    private static String APPLICATION_ID;
    private static VideoCastManager mCastMgr = null;
    public static final double VOLUME_INCREMENT = 0.05;
    private static Context mAppContext;
    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    /*
     * (non-Javadoc)
     * @see android.app.Application#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = getApplicationContext();
        APPLICATION_ID = getString(R.string.app_id);
        PROPERTY_ID = getString(R.string.ga_trackingId);
        Utils.saveFloatToPreference(getApplicationContext(),
                VideoCastManager.PREFS_KEY_VOLUME_INCREMENT, (float) VOLUME_INCREMENT);

    }

    public static VideoCastManager getCastManager(Context context) {
        if (null == mCastMgr) {
            mCastMgr = VideoCastManager.initialize(context, APPLICATION_ID,
                    null, null);
            mCastMgr.enableFeatures(
                    VideoCastManager.FEATURE_NOTIFICATION |
                            VideoCastManager.FEATURE_LOCKSCREEN |
                            VideoCastManager.FEATURE_DEBUGGING
            );

        }
        mCastMgr.setContext(context);
        String destroyOnExitStr = Utils.getStringFromPreference(context,
                CastPreference.TERMINATION_POLICY_KEY);
        mCastMgr.setStopOnDisconnect(null != destroyOnExitStr
                && CastPreference.STOP_ON_DISCONNECT.equals(destroyOnExitStr));
        return mCastMgr;
    }

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(R.xml.app_tracker)
                    : analytics.newTracker(PROPERTY_ID);
//            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
//                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker)
//                    : analytics.newTracker(R.xml.ecommerce_tracker);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
//        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
//        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }
}
