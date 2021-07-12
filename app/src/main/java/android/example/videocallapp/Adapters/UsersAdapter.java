package android.example.videocallapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.example.videocallapp.Activities.ChatActivity;
import android.example.videocallapp.Models.User;
import android.example.videocallapp.R;
import android.example.videocallapp.databinding.RowConversationBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder>{

    Context context;
    ArrayList<User>users;


    public UsersAdapter(Context context , ArrayList<User>users){
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflating the layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_conversation , parent , false);
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        //Getting user from specific positon in array list
        User user = users.get(position);

        //Gettting UID of current user
        String senderId = FirebaseAuth.getInstance().getUid();

        //Creating custom unique id
        String senderRoom = senderId + user.getUid();

        //
        FirebaseDatabase.getInstance().getReference()
                .child("Chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                            long time = snapshot.child("lastMsgTime").getValue(Long.class);
                            //showing last message
                            holder.binding.lastMsg.setText(lastMsg);
                            //showing last message time
                            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                            holder.binding.msgTime.setText(dateFormat.format(new Date(time)));
                        }else{
                            holder.binding.lastMsg.setText("Tap to chat");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //Setting user's name
        holder.binding.username.setText(user.getName());

        //Setting user's photo
        if(user.getProfileImage() != null)
        Glide.with(context).load(user.getProfileImage())
            .placeholder(R.drawable.avatar)
                .into(holder.binding.imageView2);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //It will open personal chat section
                Intent intent = new Intent(context , ChatActivity.class);
                intent.putExtra("name" , user.getName());
                intent.putExtra("uid" , user.getUid());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {
        RowConversationBinding binding;
        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowConversationBinding.bind(itemView);
        }
    }
}
