package sudtechnologies.analyticsdemo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.perf.metrics.Trace;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sudtechnologies.analyticsdemo.R;
import sudtechnologies.analyticsdemo.activity.MainActivity;

/**
 * Created by sud on 02/03/18.
 */

public class CheckedFragment extends Fragment{

    private static final String MESSAGE = "message";
    private static final String ALL_CAPS = "all_caps";
    private static final String ARG_TRACE = "trace";

    @BindView(R.id.tv_message)
    TextView tvMessage;

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.tv_email)
    TextView tvEmail;

    @BindView(R.id.tv_phone)
    TextView tvPhone;

    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;

    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private static CheckedFragment fragment;
    private MainActivity mainActivity;
    private Trace myTrace;

    public CheckedFragment() {
        // Required empty public constructor
    }

    public static CheckedFragment newInstance() {

        if(fragment==null)
            fragment = new CheckedFragment();

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cheked, container, false);
        ButterKnife.bind(this, view);

        mainActivity = (MainActivity) getActivity();

        myTrace = mainActivity.getMyTrace();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null){
            tvPhone.setText(user.getPhoneNumber()!=null&&user.getPhoneNumber()!=""?user.getPhoneNumber():getString(R.string.anonymous_phone));
            tvEmail.setText(user.getEmail()!=null&&user.getEmail()!=""?user.getEmail():getString(R.string.anonymous_email));
            tvName.setText(user.getDisplayName()!=null&&user.getDisplayName()!=""?user.getDisplayName():getString(R.string.anonymous_name));
            Picasso.with(getContext())
                    .load(user.getPhotoUrl())
                    .placeholder(R.drawable.ic_account_check)
                    .error(R.drawable.ic_account_check)
                    .into(ivAvatar);
        }

        //[RemoteConfig]
        mFirebaseRemoteConfig = ((MainActivity) getActivity()).getmFirebaseRemoteConfig();

        mFirebaseRemoteConfig.fetch(mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled() ? 0 : TimeUnit.HOURS.toSeconds(1))
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), getString(R.string.remote_config_fetch_success),
                                Toast.LENGTH_SHORT).show();

                        // After config data is successfully fetched, it must be activated before newly fetched
                        // values are returned.
                        mFirebaseRemoteConfig.activateFetched();
                    } else {
                        Toast.makeText(getContext(), getString(R.string.error_remote_config_fetch),
                                Toast.LENGTH_SHORT).show();
                        Crashlytics.log(task.toString());
                    }
                    displayMessage();
                });
        //[RemoteConfig]

        // Inflate the layout for this fragment
        return view;
    }

    private void displayMessage() {
        tvMessage.setText(mFirebaseRemoteConfig.getString(MESSAGE));
        tvMessage.setAllCaps(mFirebaseRemoteConfig.getBoolean(ALL_CAPS));
    }

    @OnClick(R.id.btn_logout)
    public void onClickLogout(){
        FirebaseAuth.getInstance().signOut();
        mainActivity.changeFragment(EmailFragment.newInstance(myTrace));
        mainActivity.showMenu();
        // [START logout event]
        Bundle params = new Bundle();
        params.putString("event", "LogOut");
        params.putString("status", "succefull");
        mainActivity.logEvent("logout", params);
        // [END logout event]
    }

    @OnClick(R.id.btn_forece_crash)
    public void onForceCrash(){
        Crashlytics.log("Force Crash!");
        Crashlytics.logException(new Exception("Ops!"));
        Crashlytics.getInstance().crash();
    }
    
}
