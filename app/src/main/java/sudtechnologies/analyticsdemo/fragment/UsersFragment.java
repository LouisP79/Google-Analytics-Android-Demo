package sudtechnologies.analyticsdemo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import sudtechnologies.analyticsdemo.R;
import sudtechnologies.analyticsdemo.activity.MainActivity;
import sudtechnologies.analyticsdemo.adapters.UserAdapter;
import sudtechnologies.analyticsdemo.model.User;

/**
 * Created by sud on 02/03/18.
 */

public class UsersFragment extends Fragment{

    private static final String USER = "user";

    @BindView(R.id.rv_users)
    RecyclerView rvUsers;

    private static UsersFragment fragment;
    private MainActivity mainActivity;
    private FirebaseFirestore firestore;
    private UserAdapter adapter;
    private List<User> users;
    private ListenerRegistration listenerRegistration;

    public UsersFragment() {
        // Required empty public constructor
    }

    public static UsersFragment newInstance() {

        if(fragment==null)
            fragment = new UsersFragment();

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        ButterKnife.bind(this, view);

        mainActivity = (MainActivity) getActivity();

        firestore = FirebaseFirestore.getInstance();

        users = new ArrayList<>();
        adapter = new UserAdapter(getContext(),users);
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUsers.setAdapter(adapter);

        loadData();

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        listenerRegistration.remove();
    }

    private void loadData() {
        mainActivity.showDialog(getString(R.string.loading));
        listenerRegistration = firestore.collection(USER)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(getContext(),getString(R.string.error_firestore_get),Toast.LENGTH_SHORT).show();
                        return;
                    }

                    this.users.clear();

                    for (QueryDocumentSnapshot users : queryDocumentSnapshots) {
                        User user = new User();

                        user.setName((String) users.get(User.NAME));
                        user.setLastName((String) users.get(User.LAST_NAME));

                        Log.e("USER FIRESTORE",users.getData().toString());

                        List<String> emls = new ArrayList<>();
                        /*for(DocumentSnapshot emails : users.get(User.EMAILS)){

                        }*/
                        user.setEmails(emls);

                        this.users.add(user);
                    }

                    adapter.notifyDataSetChanged();
                    mainActivity.closeDialog();
                });
    }

    @OnClick(R.id.btn_checked)
    public void onClickCheked(){
        mainActivity.changeFragment(CheckedFragment.newInstance());
    }
    
}
