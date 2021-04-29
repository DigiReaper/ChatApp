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
import developer.adithya.chatapp.Model.ChatList;
import developer.adithya.chatapp.Model.Users;
import developer.adithya.chatapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "ChatsFragment";

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private ArrayList<Users> usersArrayList;
    private FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference reference , dbUsers ;
    private ArrayList<ChatList> Chatlist;




    // TODO: Rename and change types of parameters
    private int mParam1;
//    private String mParam2;

    public ChatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ChatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatsFragment newInstance(int param1) {
        ChatsFragment fragment = new ChatsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById(R.id.recentRecView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager l = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(l);

        Chatlist= new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("recents").child(fUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Chatlist.clear();

                for (DataSnapshot s :
                        snapshot.getChildren()) {

                    //s contains the each individual receiver's id the user has chatted with
                    ChatList chatlist = s.getValue(ChatList.class);
                    Chatlist.add(chatlist);
                }
                //userList contains all the recent chats ids
                renderChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        renderChats();

        return view;
    }

    private void renderChats() {
        //usersArrayList contains all receivers who recently chatted with user
        usersArrayList = new ArrayList<>();

        dbUsers = FirebaseDatabase.getInstance().getReference("Users");
        dbUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersArrayList.clear();

                //snapshot has all users in db
                for (DataSnapshot s :
                        snapshot.getChildren()) {

                    //s is an individual user in the db
                    Users user = s.getValue(Users.class);
                    assert user != null;
                    for (ChatList c:Chatlist
                    ) {
                        // c has the id of the individual recent chat
                        if (user.getId().equals(c.getId())){
                            //we check if the individual user is in our recent chats
                            usersArrayList.add(user);
                        }
                    }
//                    if (!user.getId().equals("DgQUkjmnUFYKbq8GnnbnEV2HDpV2")){
//
//                    }

                }

//                dbUsers.child("DgQUkjmnUFYKbq8GnnbnEV2HDpV2").addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        Users Guser = snapshot.getValue(Users.class);
//                        usersArrayList.add(Guser);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
                adapter = new UserAdapter(getContext() , usersArrayList, true);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}