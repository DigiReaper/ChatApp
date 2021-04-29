package developer.adithya.chatapp.Adapter;

import android.content.Context;
import android.util.Log;
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

import java.util.ArrayList;

import developer.adithya.chatapp.Model.Chat;
import developer.adithya.chatapp.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private Context pContext;
    private ArrayList<Chat> ChatArraylist;
    private String imageUrl;
    private static final String TAG = "MessageAdapter";

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;


    public MessageAdapter(Context pContext, ArrayList<Chat> chatArraylist, String imageUrl) {
        this.pContext = pContext;
        ChatArraylist = chatArraylist;
        this.imageUrl = imageUrl;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(pContext).inflate(R.layout.chat_item_right ,parent , false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }else {
            View view = LayoutInflater.from(pContext).inflate(R.layout.chat_item_left ,parent , false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: Bind invoked");
        Chat chat = ChatArraylist.get(position);



        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        assert fUser != null;
//        if (!ChatArraylist.get(position).getSender().equals(fUser.getUid())){
//            //username is recieved from MessageActivity
//            holder.username.setText(username);
//        }
        if (!ChatArraylist.get(position).getSender().equals(fUser.getUid())){
            //imageUrl is recieved from MessageActivity
            if (!imageUrl.equals("default")){
                Glide.with(pContext)
                        .asBitmap()
                        .load(imageUrl)
                        .into(holder.profileImg);

            }else {
                holder.profileImg.setImageResource(R.drawable.ic_default);
            }
        }

        holder.message.setText(chat.getMessage());

        if (ChatArraylist.get(position).getSender().equals(fUser.getUid())){
            if (chat.isIsseen()){
                Log.d(TAG, "onBindViewHolder: Is Seen");
                holder.isseen.setImageResource(R.drawable.ic_seen);
            }

        }




    }

    @Override
    public int getItemCount() {
        return ChatArraylist.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView message , username ;
        private ImageView profileImg , isseen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.msgtxt);
//            username = itemView.findViewById(R.id.msgUsername);
            profileImg = itemView.findViewById(R.id.imageView);
            isseen = itemView.findViewById(R.id.seenImg);
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
}
