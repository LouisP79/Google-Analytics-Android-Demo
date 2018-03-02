package sudtechnologies.analyticsdemo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sudtechnologies.analyticsdemo.R;
import sudtechnologies.analyticsdemo.activity.MainActivity;

/**
 * Created by sud on 02/03/18.
 */

public class CheckedFragment extends Fragment{

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.tv_email)
    TextView tvEmail;

    private static CheckedFragment fragment;
    private MainActivity mainActivity;

    public CheckedFragment() {
        // Required empty public constructor
    }

    public static CheckedFragment newInstance(/*String param1*/) {

        if(fragment==null)
            fragment = new CheckedFragment();

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cheked, container, false);
        ButterKnife.bind(this, view);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mainActivity = (MainActivity) getActivity();

        if(user!=null){
            tvEmail.setText(user.getEmail());
            tvName.setText(user.getDisplayName());
        }

        // Inflate the layout for this fragment
        return view;
    }

    @OnClick(R.id.btn_logout)
    public void onClickLogout(){
        FirebaseAuth.getInstance().signOut();
        mainActivity.changeFragment(EmailFragment.newInstance());
        mainActivity.showMenu();
        // [START logout event]
        Bundle params = new Bundle();
        params.putString("event", "LogOut");
        params.putString("status", "succefull");
        mainActivity.logEvent("logout", params);
        // [END logout event]
    }
    
}
