package sudtechnologies.analyticsdemo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sudtechnologies.analyticsdemo.R;

/**
 * Created by sud on 02/03/18.
 */

public class AnonymousFragment extends Fragment{

    private static AnonymousFragment fragment;

    public AnonymousFragment() {
        // Required empty public constructor
    }

    public static AnonymousFragment newInstance(/*String param1*/) {

        if(fragment==null)
            fragment = new AnonymousFragment();

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_anonymous, container, false);
    }

    
}
