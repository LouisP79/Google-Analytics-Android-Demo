package sudtechnologies.analyticsdemo.activity;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import sudtechnologies.analyticsdemo.R;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // [START start app event]
        Bundle params = new Bundle();
        params.putString("event", "app has started");
        mFirebaseAnalytics.logEvent("start_app", params);
        // [END start app event]

        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(item -> {
            // [START menu event]
            Bundle bundle = new Bundle();
            bundle.putString("id_button"/*FirebaseAnalytics.Param.ITEM_ID*/, String.valueOf(item.getItemId()));
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    bundle.putString("name_button"/*FirebaseAnalytics.Param.ITEM_NAME*/, getString(R.string.title_home));
                    break;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    bundle.putString("name_button"/*FirebaseAnalytics.Param.ITEM_NAME*/, getString(R.string.title_dashboard));
                    break;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    bundle.putString("name_button"/*FirebaseAnalytics.Param.ITEM_NAME*/, getString(R.string.title_notifications));
                    break;
            }
            bundle.putString("type_button"/*FirebaseAnalytics.Param.CONTENT_TYPE*/, "menu");
            mFirebaseAnalytics.logEvent("button_menu"/*FirebaseAnalytics.Event.SELECT_CONTENT*/, bundle);
            Log.i("EVENTO INFO",bundle.toString());
            // [END menu event]
            return true;
        });
    }

    @Override
    protected void onDestroy() {
        // [START finish app event]
        Bundle params = new Bundle();
        params.putString("event", "app has finished");
        mFirebaseAnalytics.logEvent("finish_app", params);
        // [END finish app event]
        super.onDestroy();
    }


}
