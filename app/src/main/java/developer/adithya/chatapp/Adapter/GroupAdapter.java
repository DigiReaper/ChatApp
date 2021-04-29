package developer.adithya.chatapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.CookieHandler;
import java.util.ArrayList;

import developer.adithya.chatapp.Model.Chat;
import developer.adithya.chatapp.Model.Users;
import developer.adithya.chatapp.R;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private ArrayList<Chat> ChatArraylist;
    private Context gContext;


    public GroupAdapter(ArrayList<Chat> chatArraylist, Context gContext) {
        ChatArraylist = chatArraylist;
        this.gContext = gContext;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(gContext).inflate(R.layout.chat_item_right ,parent , false);
            GroupAdapter.ViewHolder holder = new GroupAdapter.ViewHolder(view);
            return holder;
        }else {
            View view = LayoutInflater.from(gContext).inflate(R.layout.chat_item_group ,parent , false);
            GroupAdapter.ViewHolder holder = new GroupAdapter.ViewHolder(view);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Chat chat = ChatArraylist.get(position);

        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        assert fUser != null;

        if (!chat.getSender().equals(fUser.getUid())){
            DatabaseReference dbUsers = FirebaseDatabase.getInstance().getReference("Users");
            dbUsers.child(chat.getSender()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Users tUser = snapshot.getValue(Users.class);
                    assert tUser != null;
                    if (tUser.getImageUrl().equals("default")){
                        holder.profileImg.setImageResource(R.drawable.ic_default);
                    }else {
                        Glide.with(gContext)
                                .asBitmap()
                                .load(tUser.getImageUrl())
                                .into(holder.profileImg);
                    }



                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }

        holder.message.setText(chat.getMessage());

        if (!chat.getSender().equals(fUser.getUid())){
            DatabaseReference dbUsers = FirebaseDatabase.getInstance().getReference("Users");
            dbUsers.child(chat.getSender()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Users tUser = snapshot.getValue(Users.class);
                    assert tUser != null;
                    holder.username.setText(tUser.getName());

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (ChatArraylist.get(position).getSender().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }

    @Override
    public int getItemCount() {
        return ChatArraylist.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView message , username ;
        private ImageView profileImg ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.msgtxt);
            username = itemView.findViewById(R.id.msgUsername);
            profileImg = itemView.findViewById(R.id.imageView);

        }
    }
}
