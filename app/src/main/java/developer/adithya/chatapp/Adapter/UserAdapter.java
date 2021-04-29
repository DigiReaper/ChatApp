package developer.adithya.chatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import developer.adithya.chatapp.MessageActivity;
import developer.adithya.chatapp.Model.Users;
import developer.adithya.chatapp.R;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context mcontext;
    private ArrayList<Users> usersArrayList;
    private boolean isChat;
    private static final String TAG = "UserAdapter";



    //Constructor


    public UserAdapter(Context mcontext, ArrayList<Users> usersArrayList, boolean isChat) {
        this.mcontext = mcontext;
        this.usersArrayList = usersArrayList;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.user_item , parent , false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: invoked!!!!");

        Users user = usersArrayList.get(position);


        holder.UName.setText(user.getName());

        if (!user.getImageUrl().equals("default")){
            Glide.with(mcontext)
                    .asBitmap()
                    .load(user.getImageUrl())
                    .into(holder.UImg);

        }else {
            holder.UImg.setImageResource(R.drawable.ic_default);
        };

        if (isChat){
            if (user.getStatus().equals("online")){
                holder.UStatus.setVisibility(View.VISIBLE);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mcontext , MessageActivity.class);
                i.putExtra("UserID" , user.getId());
                mcontext.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView UName , UStatus;
        private ImageView UImg;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            UName = itemView.findViewById(R.id.textView6);
            UImg = itemView.findViewById(R.id.imageView);
            UStatus = itemView.findViewById(R.id.textView11);


        }
    }
}
