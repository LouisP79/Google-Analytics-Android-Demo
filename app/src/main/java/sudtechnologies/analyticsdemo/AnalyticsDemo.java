package sudtechnologies.analyticsdemo;

import android.app.Application;

import com.facebook.appevents.AppEventsLogger;

/**
 * Created by sud on 08/03/18.
 */

public class AnalyticsDemo extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppEventsLogger.activateApp(this);
    }
}
