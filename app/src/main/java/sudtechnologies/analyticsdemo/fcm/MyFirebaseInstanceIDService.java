package sudtechnologies.analyticsdemo.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import sudtechnologies.analyticsdemo.DinamicConstant;


/**
 * Created by Usuario on 7/03/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic(DinamicConstant.TOPIC);
        Log.e(TAG, "Refreshed token: " + refreshedToken);
    }
}
