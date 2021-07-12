package android.example.videocallapp.Adapters;

import android.content.Context;
import android.example.videocallapp.Models.Message;
import android.example.videocallapp.Models.User;
import android.example.videocallapp.R;
import android.example.videocallapp.databinding.DeleteLayoutBinding;
import android.example.videocallapp.databinding.ItemRecieveBinding;
import android.example.videocallapp.databinding.ItemRecieveGroupBinding;
import android.example.videocallapp.databinding.ItemSentBinding;
import android.example.videocallapp.databinding.ItemSentGroupBinding;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import android.content.Context;
import android.example.videocallapp.Models.Message;
import android.example.videocallapp.R;
import android.example.videocallapp.databinding.ItemRecieveBinding;
import android.example.videocallapp.databinding.ItemSentBinding;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Message> messages;

    final int ITEM_SENT = 1;
    final int ITEM_RECIEVE = 2;

    String senderRoom , recieverRoom;

    public MessagesAdapter(Context context , ArrayList<Message> messages , String senderRoom , String recieverRoom){
        this.context = context;
        this.messages = messages;
        this.senderRoom = senderRoom;
        this.recieverRoom = recieverRoom;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflating the layout
        if(viewType == ITEM_SENT){
            View view = LayoutInflater.from(context).inflate(R.layout.item_sent , parent , false);
            return new SentViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.item_recieve , parent , false);
            return new RecieverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        //Checking recivers or sender's message
        if(FirebaseAuth.getInstance().getUid().equals(message.getSenderId())){
            return ITEM_SENT;
        }else{
            return ITEM_RECIEVE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //Getting Message from specific positon in array list
        Message message = messages.get(position);

        //Reactions array
        int reactions[] = { R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry};

        //Getting the feature of reactions
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();

        //Setting popup to set feeling for a message
        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if(holder.getClass() == SentViewHolder.class){
                SentViewHolder viewHolder = (SentViewHolder)holder;
                viewHolder.binding.feeling.setImageResource(reactions[pos]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }else{
                RecieverViewHolder viewHolder = (RecieverViewHolder)holder;
                viewHolder.binding.feeling.setImageResource(reactions[pos]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }

            //Setting feeling to the message
            message.setFeeling(pos);

            //Putting message in the firebase
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Chats")
                    .child(senderRoom)
                    .child("Messages")
                    .child(message.getMessageId()).setValue(message);

            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Chats")
                    .child(recieverRoom)
                    .child("Messages")
                    .child(message.getMessageId()).setValue(message);
            return true; // true is closing popup, false is requesting a new selection
        });



        if(holder.getClass() == SentViewHolder.class){
            //Getting instance
            SentViewHolder viewHolder = (SentViewHolder)holder;
            //Setting message
            viewHolder.binding.message.setText(message.getMessage());

            //Checking if the message is photo
            if(message.getMessage() == "photo" || message.getMessage().equals("photo")){
                viewHolder.binding.imageView.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.GONE);
                Glide.with(context)
                        .load(message.getImageUrl())
                        .placeholder(R.drawable.place_holder)
                        .into(viewHolder.binding.imageView);
            }

            //Checking if there is any feeling attached to the message
            if(message.getFeeling() >= 0){
                viewHolder.binding.feeling.setImageResource((reactions[(int)message.getFeeling()]));
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }else{
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }


            viewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v , event);
                    return false;
                }
            });

            viewHolder.binding.imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v , event);
                    return false;
                }
            });
        }else{
            //Getting instance
            RecieverViewHolder viewHolder =  (RecieverViewHolder)holder;
            //Setting message
            viewHolder.binding.message.setText(message.getMessage());

            //Checking if the message is photo
            if(message.getMessage() == "photo" || message.getMessage().equals("photo")){
                viewHolder.binding.imageView.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.GONE);
                Glide.with(context)
                        .load(message.getImageUrl())
                        .placeholder(R.drawable.place_holder)
                        .into(viewHolder.binding.imageView);
            }

            //Checking if there is any feeling attached to the message
            if(message.getFeeling() >= 0){
                viewHolder.binding.feeling.setImageResource((reactions[(int)message.getFeeling()]));
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }else{
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }

            viewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v , event);
                    return false;
                }
            });
            viewHolder.binding.imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v , event);
                    return false;
                }
            });
        }
    }

    //Getting the total count of messages
    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class SentViewHolder extends RecyclerView.ViewHolder{

        ItemSentBinding binding;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSentBinding.bind(itemView);
        }
    }

    public class RecieverViewHolder extends RecyclerView.ViewHolder{
        ItemRecieveBinding binding;
        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemRecieveBinding.bind(itemView);
        }
    }
}