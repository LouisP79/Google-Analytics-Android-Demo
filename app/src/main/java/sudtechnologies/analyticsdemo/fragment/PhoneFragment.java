package sudtechnologies.analyticsdemo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sudtechnologies.analyticsdemo.R;
import sudtechnologies.analyticsdemo.activity.MainActivity;

/**
 * Created by sud on 02/03/18.
 */

public class PhoneFragment extends Fragment {

    private static final int SECONDS = 120;

    @BindView(R.id.et_phone)
    EditText etPhone;

    @BindView(R.id.et_code)
    EditText etCode;

    @BindView(R.id.btn_check_code)
    Button btnCheckCode;

    @BindView(R.id.btn_resend_code)
    Button btnResendCode;

    private static PhoneFragment fragment;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private MainActivity mainActivity;
    private Bundle params;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            mainActivity.closeDialog();
            mainActivity.showDialog(getString(R.string.loading));
            params.putString("event", "LogInPhone");
            authFirebase(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            mainActivity.closeDialog();
            if (e instanceof FirebaseAuthInvalidCredentialsException)
                Toast.makeText(getContext(),getString(R.string.error_phone_bad_request,e.getMessage()),Toast.LENGTH_SHORT).show();
            else if (e instanceof FirebaseTooManyRequestsException)
                Toast.makeText(getContext(),getString(R.string.error_phone_timeout),Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(String verificationId,
                               PhoneAuthProvider.ForceResendingToken token) {
            // Save verification ID and resending token so we can use them later
            mVerificationId = verificationId;
            mResendToken = token;

            mainActivity.closeDialog();
            Toast.makeText(getContext(),getString(R.string.sms_sended),Toast.LENGTH_SHORT).show();

            btnCheckCode.setEnabled(true);
            btnResendCode.setEnabled(true);
        }

    };

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
        if(!etPhone.getText().toString().isEmpty())
            sentCode();
        else
            validate(etPhone, R.string.validate_no_empty);
    }

    @OnClick(R.id.btn_check_code)
    public void onCLickCheckCode() {
        if(!etCode.getText().toString().isEmpty())
            validateCode();
        else
            validate(etCode, R.string.validate_no_empty);
    }

    @OnClick(R.id.btn_resend_code)
    public void onCLickResendCode() {
        if(!etPhone.getText().toString().isEmpty())
            resentCode();
        else
            validate(etPhone, R.string.validate_no_empty);
    }

    private void validate(EditText view, int error) {
        view.setError(getString(error));
    }

    private void resentCode(){
        mainActivity.showDialog(getString(R.string.sending_code));
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                etPhone.getText().toString(),
                SECONDS,
                TimeUnit.SECONDS,
                getActivity(),
                mCallBack,
                mResendToken);
    }

    private void sentCode(){
        mainActivity.showDialog(getString(R.string.sending_code));
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                etPhone.getText().toString(),
                SECONDS,
                TimeUnit.SECONDS,
                getActivity(),
                mCallBack);
    }

    private void validateCode(){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, etCode.getText().toString());
        mainActivity.showDialog(getString(R.string.loading));
        params.putString("event", "LogInPhone");
        authFirebase(credential);
    }

    private void authFirebase(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
            if(!task.isSuccessful()){
                mainActivity.closeDialog();
                params.putString("status", "error");
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
}
