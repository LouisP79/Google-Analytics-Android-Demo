package sudtechnologies.analyticsdemo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.OnClick;
import sudtechnologies.analyticsdemo.R;
import sudtechnologies.analyticsdemo.activity.MainActivity;

/**
 * Created by sud on 02/03/18.
 */

public class PhoneFragment extends Fragment {

    private static PhoneFragment fragment;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private MainActivity mainActivity;
    private Bundle params;

    public PhoneFragment() {
        // Required empty public constructor
    }

    public static PhoneFragment newInstance(/*String param1*/) {

        if (fragment == null)
            fragment = new PhoneFragment();

        Bundle args = new Bundle();
        /*args.putString(ARG_PARAM1, param1);*/
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            /*mParam1 = getArguments().getString(ARG_PARAM1);*/
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phone, container, false);
        ButterKnife.bind(this, view);

        mainActivity = (MainActivity) getActivity();

        params = new Bundle();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() != null) {
                mainActivity.changeFragment(CheckedFragment.newInstance());
                mainActivity.hideMenu();
                mainActivity.closeDialog();
                params.putString("status", "succefull");
                loginEvent();
            }
        };

        // Inflate the layout for this fragment
        return view;
    }

    @OnClick(R.id.btn_login_phone)
    public void onCLickPhone() {
        //mainActivity.showDialog(getString(R.string.loading));
        params.putString("event", "LogInPhone");

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "",
                60,
                TimeUnit.SECONDS,
                getActivity(),
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        Toast.makeText(getContext(),"ok",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Toast.makeText(getContext(),"NO",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loginEvent(){
        // [START login event]
        mainActivity.logEvent("login", params);
        // [END login event]
    }

}
