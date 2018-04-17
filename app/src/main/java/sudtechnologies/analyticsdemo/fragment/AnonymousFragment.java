package sudtechnologies.analyticsdemo.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

import java.util.Random;

import butterknife.ButterKnife;
import butterknife.OnClick;
import sudtechnologies.analyticsdemo.R;
import sudtechnologies.analyticsdemo.activity.MainActivity;

/**
 * Created by sud on 02/03/18.
 */

public class AnonymousFragment extends Fragment{

    private static final String ARG_TRACE = "trace";
    private static final String TRACE_LOGIN_SUCCESS = "login_anonymous_succes";
    private static final String TRACE_LOGIN_ERROR = "login_anonymous_error";

    private static AnonymousFragment fragment;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Trace myTrace;

    private MainActivity mainActivity;
    private Bundle params;

    public AnonymousFragment() {
        // Required empty public constructor
    }

    public static AnonymousFragment newInstance(Trace trace) {

        if(fragment==null)
            fragment = new AnonymousFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARG_TRACE, trace);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            myTrace = getArguments().getParcelable(ARG_TRACE);
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
        View view = inflater.inflate(R.layout.fragment_anonymous, container, false);
        ButterKnife.bind(this, view);

        mainActivity = (MainActivity) getActivity();

        params = new Bundle();

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

    private void loginEvent(){
        // [START login event]
        mainActivity.logEvent("login", params);
        // [END login event]
    }

    @OnClick(R.id.btn_login_anonymous)
    public void onClickLoginAnonymous(){
        mainActivity.showDialog(getString(R.string.loading));
        params.putString("event", "LogInAnonymous");
        mAuth.signInAnonymously().addOnCompleteListener(task -> {
            if(!task.isSuccessful()){
                mainActivity.closeDialog();
                params.putString("status", "error");
                // [Performance]
                myTrace.incrementCounter(TRACE_LOGIN_ERROR);
                // [Performance]

                Crashlytics.log(task.toString());

                loginEvent();
                Toast.makeText(getContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    
}
