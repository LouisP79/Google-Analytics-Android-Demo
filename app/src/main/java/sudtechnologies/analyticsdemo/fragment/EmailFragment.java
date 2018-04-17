package sudtechnologies.analyticsdemo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.perf.metrics.Trace;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sudtechnologies.analyticsdemo.R;
import sudtechnologies.analyticsdemo.activity.MainActivity;

/**
 * Created by sud on 02/03/18.
 */

public class EmailFragment extends Fragment implements Validator.ValidationListener{

    private static final String ARG_TRACE = "trace";
    private static final String TRACE_LOGIN_SUCCESS = "login_email_succes";
    private static final String TRACE_LOGIN_ERROR = "login_email_error";

    @BindView(R.id.et_email)
    @NotEmpty(messageResId = R.string.validate_no_empty)
    @Email(messageResId = R.string.validate_email)
    EditText etEmail;

    @BindView(R.id.et_password)
    @NotEmpty(messageResId = R.string.validate_no_empty)
    @Length(min = 6, messageResId = R.string.validate_password_lenght)
    EditText etPassword;

    private static EmailFragment fragment;
    protected Validator validator;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private MainActivity mainActivity;
    private Bundle params;
    private Trace myTrace;

    public EmailFragment() {
        // Required empty public constructor
    }

    public static EmailFragment newInstance(Trace trace) {
        if(fragment==null)
            fragment = new EmailFragment();

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
        View view = inflater.inflate(R.layout.fragment_email, container, false);
        ButterKnife.bind(this, view);

        validator = new Validator(this);
        validator.setValidationListener(this);

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

    @OnClick(R.id.btn_login_email)
    public void onLoginEmail(){
        validator.validate();
    }

    @Override
    public void onValidationSucceeded() {
        mainActivity.showDialog(getString(R.string.loading));
        params.putString("event", "LogInEmail");
        mAuth.signInWithEmailAndPassword(etEmail.getText().toString(),
                                        etPassword.getText().toString())
                .addOnCompleteListener(task -> {
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

    private void loginEvent(){
        // [START login event]
        mainActivity.logEvent("login", params);
        // [END login event]
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            EditText view = (EditText) error.getView();
            String message = error.getCollatedErrorMessage(getContext());
            view.setError(message);
        }
    }
}
