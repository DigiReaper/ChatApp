package developer.adithya.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;

import developer.adithya.chatapp.Adapter.GroupAdapter;
import developer.adithya.chatapp.Adapter.MessageAdapter;
import developer.adithya.chatapp.Model.Chat;
import developer.adithya.chatapp.Model.ChatList;
import developer.adithya.chatapp.Model.Users;

public class MessageActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser fUser;
    private DatabaseReference dbUsers , dbChats , dbRecents;
    private static final String TAG = "MessageActivity";

    //Uname and Uimg refer to reciever
    private TextView Uname;
    private ImageView Uimg;

    private Toolbar toolbar;
    private ImageButton sendBtn;
    private EditText message;
    private String recieverUID , recieverImage , messageLocation, messageLocation2;
    private RecyclerView msgRecView;
    private ArrayList<Chat> chatList = new ArrayList<>();

    private ValueEventListener seenlistner;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        //recieverUID is the firebase id of the reciever.
        Intent i = getIntent();
        recieverUID = i.getStringExtra("UserID");

        firebaseAuth = FirebaseAuth.getInstance();
        fUser = firebaseAuth.getCurrentUser();
        dbUsers = FirebaseDatabase.getInstance().getReference("Users");
        dbChats = FirebaseDatabase.getInstance().getReference("Chats");
        dbRecents = FirebaseDatabase.getInstance().getReference("recents").child(fUser.getUid()).child(recieverUID);




        //Uname and Uimg refer to reciever
        Uname = findViewById(R.id.textView7);
        Uimg = findViewById(R.id.imageView);

        toolbar = findViewById(R.id.toolbar);
        sendBtn = findViewById(R.id.imageButton);
        message = findViewById(R.id.editTextMessage);
        msgRecView = findViewById(R.id.msgRecView);
        msgRecView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager  = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        msgRecView.setLayoutManager(linearLayoutManager);

        //Looking in reciever's profile for the location of messages in the db
        //if message location is not present it is set to default
        if (!recieverUID.equals("DgQUkjmnUFYKbq8GnnbnEV2HDpV2")){


            dbUsers.child(recieverUID).child("message").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        messageLocation = snapshot.getValue(String.class);
                        Log.d(TAG, "onDataChange: onstart  message location  "  + messageLocation);
                    }else {
                        messageLocation = "default";
                        Log.d(TAG, "onDataChange: onstart  set null");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }


        UpdateUI();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = message.getText().toString();
                if (!msg.isEmpty()){
                    sendmessage(fUser.getUid() , recieverUID,  msg);
                }
                message.setText("");
            }
        });

    }



    private void sendmessage(String sender, String reciever, String msg) {

//        Chat chat = new Chat(sender , reciever , msg);
        HashMap<String , Object> cMap = new HashMap<>();
        
        cMap.put("sender" , sender);
        cMap.put("reciever" , reciever);
        cMap.put("message" , msg);
        cMap.put("isseen" , false);


        if (!recieverUID.equals("DgQUkjmnUFYKbq8GnnbnEV2HDpV2")){
            dbChats.child(messageLocation).push().setValue(cMap);
            Log.d(TAG, "sendmessage: 1    " + messageLocation);
            SaveRecentChat();
        }else {
            Log.d(TAG, "sendmessage: Group");
            dbChats.child("DgQUkjmnUFYKbq8GnnbnEV2HDpV2").push().setValue(cMap);
            SaveRecentChat();
        }


//        if (messageLocation.equals("default")){
//            dbChats.child(messageLocation2).push().setValue(chat);
//            Log.d(TAG, "sendmessage: 2    "  + messageLocation2);
//        }else {
//            dbChats.child(messageLocation).push().setValue(chat);
//            Log.d(TAG, "sendmessage: 1    " + messageLocation);
//        }



    }

    private void UpdateUI() {


        dbUsers.child(recieverUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //snapshot contains the object of the reciever
                Users Iuser = snapshot.getValue(Users.class);
                assert Iuser != null;

                Uname.setText(Iuser.getName());

                //get the messages of the reciever and display to user
                recieverImage = Iuser.getImageUrl();
                if (recieverUID.equals("DgQUkjmnUFYKbq8GnnbnEV2HDpV2")){
                    GroupChat();
                }else {
                    UpdateMessages(recieverImage );
                }



                if (Iuser.getImageUrl().equals("default")){
                    Uimg.setImageResource(R.drawable.ic_default);

                }else {
                    Glide.with(getApplicationContext())
                            .asBitmap()
                            .load(Iuser.getImageUrl())
                            .into(Uimg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void GroupChat() {
        chatList.clear();

        Log.d(TAG, "GroupChat: invoked");


            dbChats.child("DgQUkjmnUFYKbq8GnnbnEV2HDpV2").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    chatList.clear();
                    for (DataSnapshot s :
                            snapshot.getChildren()) {

                        Chat mychat = s.getValue(Chat.class);
                        Log.d(TAG, "onDataChange: " + mychat);
                        chatList.add(mychat);


                        GroupAdapter groupAdapter = new GroupAdapter( chatList  ,MessageActivity.this );
                        msgRecView.setAdapter(groupAdapter);

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


    }

    private void UpdateMessages(String Uname ) {
        chatList.clear();

        Log.d(TAG, "UpdateMessages: invoked");

        if (messageLocation.equals("default")) {
            Log.d(TAG, "UpdateMessages: null invoked");
            dbChats.child(fUser.getUid()+"_msg").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d(TAG, "onDataChange:  2   " + snapshot.getKey());
                    messageLocation = snapshot.getKey();

                    chatList.clear();
                    for (DataSnapshot s :
                            snapshot.getChildren()) {

                        Chat mychat = s.getValue(Chat.class);
                        Log.d(TAG, "onDataChange: " + mychat);
                        chatList.add(mychat);


                        //save new message location to db
                        dbUsers.child(fUser.getUid()).child("message").child(recieverUID).setValue(snapshot.getKey());


                        MessageAdapter messageAdapter = new MessageAdapter(MessageActivity.this, chatList, Uname ) ;
                        msgRecView.setAdapter(messageAdapter);

                    }
                    SeenMessage();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            
        }
        else {
            dbChats.child(messageLocation).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d(TAG, "onDataChange: 2  ");

                    chatList.clear();
                    for (DataSnapshot s :
                            snapshot.getChildren()) {

                        Chat mychat = s.getValue(Chat.class);
                        Log.d(TAG, "onDataChange: " + mychat);
                        chatList.add(mychat);

                        MessageAdapter messageAdapter = new MessageAdapter(MessageActivity.this, chatList, Uname );
                        msgRecView.setAdapter(messageAdapter);

                    }
                    SeenMessage();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }



    }

    private void SaveRecentChat() {
        dbRecents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    dbRecents.child("id").setValue(recieverUID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SeenMessage(){

        seenlistner = dbChats.child(messageLocation).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s :
                        snapshot.getChildren()) {

                    Chat chat = s.getValue(Chat.class);

                    assert chat != null;
                    if (chat.getReciever().equals(fUser.getUid()) ){
                        HashMap<String , Object> chatmap = new HashMap<>();

                        chatmap.put("isseen" , true);
                        s.getRef().updateChildren(chatmap);

                    }

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        CheckStatus("offline");
        if (!recieverUID.equals("DgQUkjmnUFYKbq8GnnbnEV2HDpV2")){
            dbChats.child(messageLocation).removeEventListener(seenlistner);
        }

    }
    private void CheckStatus(String status){
        HashMap<String , Object> map = new HashMap<>();
        map.put("status" , status);

        dbUsers.child(fUser.getUid()).updateChildren(map);
    }

}