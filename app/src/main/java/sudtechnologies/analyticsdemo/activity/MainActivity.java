package sudtechnologies.analyticsdemo.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import io.fabric.sdk.android.Fabric;
import sudtechnologies.analyticsdemo.BuildConfig;
import sudtechnologies.analyticsdemo.R;
import sudtechnologies.analyticsdemo.fragment.AnonymousFragment;
import sudtechnologies.analyticsdemo.fragment.EmailFragment;
import sudtechnologies.analyticsdemo.fragment.GoogleFragment;
import sudtechnologies.analyticsdemo.fragment.PhoneFragment;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private BottomNavigationView navigation;
    protected ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        //identified the user [Crashlytics]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            Crashlytics.setUserIdentifier(user.getUid()!=null?user.getUid():getString(R.string.anonymous_id));
            Crashlytics.setUserName(user.getDisplayName()!=null?user.getDisplayName():getString(R.string.anonymous_name));
            Crashlytics.setUserEmail(user.getEmail()!=null?user.getEmail():getString(R.string.anonymous_email));
        }else
            Crashlytics.setUserIdentifier(getString(R.string.anonymous_id));

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        //[RemoteConfig]
        mFirebaseRemoteConfig.setConfigSettings(new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build());

        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        //[RemoteConfig]

        changeFragment(null);

        // [START start app event]
        Bundle params = new Bundle();
        params.putString("event", "app has started");
        mFirebaseAnalytics.logEvent("start_app", params);
        // [END start app event]

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(item -> {
            // [START menu event]
            Bundle bundle = new Bundle();
            bundle.putString("id_button", String.valueOf(item.getItemId()));
            switch (item.getItemId()) {
                case R.id.navigation_email:
                    bundle.putString("name_button", getString(R.string.title_email));
                    changeFragment(EmailFragment.newInstance());
                    break;
                case R.id.navigation_google:
                    bundle.putString("name_button", getString(R.string.title_google));
                    changeFragment(GoogleFragment.newInstance());
                    break;
                case R.id.navigation_phone:
                    bundle.putString("name_button", getString(R.string.title_phone));
                    changeFragment(PhoneFragment.newInstance());
                    break;
                case R.id.navigation_anonymous:
                    bundle.putString("name_button", getString(R.string.title_anonymous));
                    changeFragment(AnonymousFragment.newInstance());
                    break;
            }
            bundle.putString("type_button", "menu");
            mFirebaseAnalytics.logEvent("button_menu", bundle);
            Log.i("EVENTO INFO",bundle.toString());
            // [END menu event]
            return true;
        });
    }

    public FirebaseRemoteConfig getmFirebaseRemoteConfig() {
        return mFirebaseRemoteConfig;
    }

    public void changeFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.container,
                fragment==null?EmailFragment.newInstance():fragment).commit();
    }

    public void hideMenu(){
        navigation.setVisibility(View.GONE);
    }

    public void showMenu(){
        navigation.setVisibility(View.VISIBLE);
        navigation.setSelectedItemId(R.id.navigation_email);
    }

    public void showDialog(String message){
        dialog = new ProgressDialog(this);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void closeDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    public void logEvent(String event, Bundle bundle){
        mFirebaseAnalytics.logEvent(event, bundle);
    }

}
