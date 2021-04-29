package developer.adithya.chatapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import developer.adithya.chatapp.Adapter.UserAdapter;
import developer.adithya.chatapp.Model.Users;
import developer.adithya.chatapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsersFragment extends Fragment {
    private RecyclerView UsersRecView;
    private UserAdapter userAdapter;
    private ArrayList<Users> mUsers;
    private static final String TAG = "UsersFragment";


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UsersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UsersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UsersFragment newInstance(String param1, String param2) {
        UsersFragment fragment = new UsersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_users, container, false);

        UsersRecView = view.findViewById(R.id.UsersRecView);
        UsersRecView.setHasFixedSize(true);
        UsersRecView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers = new ArrayList<>();
        ReadUsers();
        return view;

    }

    private void ReadUsers() {
        final FirebaseUser firebaseUser =FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mUsers.clear();
                for (DataSnapshot s :
                        snapshot.getChildren()) {

                    Users Tuser = s.getValue(Users.class);
                    Log.d(TAG, "onDataChange: " + Tuser);

                    assert Tuser != null;
                    if (!Tuser.getId().equals(firebaseUser.getUid())){
                        mUsers.add(Tuser);
                    }

                    Log.d(TAG, "onDataChange: Adapter Set");

                    UserAdapter UA = new UserAdapter(getContext() , mUsers , false);
                    UsersRecView.setAdapter(UA);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}