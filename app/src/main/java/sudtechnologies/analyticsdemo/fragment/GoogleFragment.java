package sudtechnologies.analyticsdemo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.perf.metrics.Trace;

import butterknife.ButterKnife;
import butterknife.OnClick;
import sudtechnologies.analyticsdemo.R;
import sudtechnologies.analyticsdemo.activity.MainActivity;

/**
 * Created by sud on 02/03/18.
 */

public class GoogleFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    private static final int SIGN_IN_CODE = 777;
    private static final String ARG_TRACE = "trace";
    private static final String TRACE_LOGIN_SUCCESS = "login_google_succes";
    private static final String TRACE_LOGIN_ERROR = "login_google_error";

    private static GoogleFragment fragment;
    private GoogleApiClient googleApiClient;
    private MainActivity mainActivity;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Bundle params;
    private Trace myTrace;

    public GoogleFragment() {
        // Required empty public constructor
    }

    public static GoogleFragment newInstance(Trace trace) {

        if(fragment==null)
            fragment = new GoogleFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_TRACE, trace);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            myTrace = getArguments().getParcelable(ARG_TRACE);
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
        View view = inflater.inflate(R.layout.fragment_google, container, false);
        ButterKnife.bind(this, view);

        mainActivity = (MainActivity) getActivity();

        params = new Bundle();

        if(googleApiClient==null) {
            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            googleApiClient = new GoogleApiClient.Builder(getContext())
                    .enableAutoManage(getActivity(), this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                    .build();
        }

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            if(firebaseAuth.getCurrentUser()!=null){
                mainActivity.changeFragment(CheckedFragment.newInstance());
                mainActivity.hideMenu();
                mainActivity.closeDialog();
                params.putString("status", "succefull");
                // [Performance]
                myTrace.incrementCounter(TRACE_LOGIN_SUCCESS);
                // [Performance]
                loginEvent();
            }
        };

        // Inflate the layout for this fragment
        return view;
    }

    @OnClick(R.id.sign_in_button)
    public void onClickSignIn(){
        mainActivity.showDialog(getString(R.string.loading));
        params.putString("event", "LogInGoogle");
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,SIGN_IN_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SIGN_IN_CODE){
            GoogleSignInResult result =   Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                authFirebase(result.getSignInAccount());
            }else {
                mainActivity.closeDialog();
                params.putString("status", "errorGoogle");
                // [Performance]
                myTrace.incrementCounter(TRACE_LOGIN_ERROR);
                // [Performance]

                Crashlytics.log(result.toString());

                loginEvent();
                Toast.makeText(getContext(),getString(R.string.validate_google_error),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loginEvent(){
        // [START login event]
        mainActivity.logEvent("login", params);
        // [END login event]
    }

    private void authFirebase(GoogleSignInAccount signInAccount) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(),null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(task -> {
            if(!task.isSuccessful()){
                mainActivity.closeDialog();
                params.putString("status", "errorFirebase");
                // [Performance]
                myTrace.incrementCounter(TRACE_LOGIN_ERROR);
                // [Performance]

                Crashlytics.log(task.toString());

                loginEvent();
                Toast.makeText(getContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getContext(),getString(R.string.validate_google_error_conection),Toast.LENGTH_SHORT).show();
        mainActivity.closeDialog();
    }
}
