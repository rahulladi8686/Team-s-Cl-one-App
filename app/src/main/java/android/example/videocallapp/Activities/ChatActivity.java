package android.example.videocallapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.example.videocallapp.Adapters.MessagesAdapter;
import android.example.videocallapp.Models.Message;
import android.example.videocallapp.R;
import android.example.videocallapp.databinding.ActivityChatBinding;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    MessagesAdapter adapter;
    ArrayList<Message>messages;
    String senderRoom , recieverRoom , senderUid , recieverUid;

    FirebaseDatabase database;
    FirebaseStorage storage;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //setSupportActionBar(binding.toolbar);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading Image.....");
        dialog.setCancelable(false);

        String name = getIntent().getStringExtra("name");
        recieverUid = getIntent().getStringExtra("uid");
        senderUid = FirebaseAuth.getInstance().getUid();

        senderRoom = senderUid + recieverUid;
        recieverRoom = recieverUid + senderUid;

        messages = new ArrayList<Message>();
        adapter = new MessagesAdapter(this , messages , senderRoom , recieverRoom);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();


        database.getReference().child("Chats")
                .child(senderRoom)
                .child("Messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for (DataSnapshot snapshot1:snapshot.getChildren()){
                            Message message = snapshot1.getValue(Message.class);
                            message.setMessageId(snapshot1.getKey());
                            messages.add(message);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String messageText = binding.messageBox.getText().toString();

                Date date = new Date();
                Message message = new Message(messageText , senderUid , date.getTime());
                binding.messageBox.setText("");

                String randomKey = database.getReference().push().getKey();
                HashMap<String , Object>lastMsgObj = new HashMap<>();
                lastMsgObj.put("lastMsg" , message.getMessage());
                lastMsgObj.put("lastMsgTime" , message.getTimestamp());

                database.getReference().child("Chats").child(senderRoom).updateChildren(lastMsgObj);
                database.getReference().child("Chats").child(recieverRoom).updateChildren(lastMsgObj);

                database.getReference().child("Chats")
                        .child(senderRoom)
                        .child("Messages")
                        .child(randomKey)
                        .setValue(message)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                database.getReference().child("Chats")
                                        .child(recieverRoom)
                                        .child("Messages")
                                        .child(randomKey)
                                        .setValue(message)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        });
                            }
                        });

            }
        });

        binding.attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent , 20);
            }
        });



        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 20){
            if(data != null){
                if(data.getData() != null){
                    Uri selectedImage = data.getData();
                    Calendar calendar = Calendar.getInstance();
                    StorageReference reference = storage.getReference().child("Chats").child(calendar.getTimeInMillis() + "");
                    dialog.show();
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            dialog.dismiss();
                            if(task.isSuccessful()){
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String filepath = uri.toString();
                                        String messageText = binding.messageBox.getText().toString();

                                        Date date = new Date();
                                        Message message = new Message(messageText , senderUid , date.getTime());
                                        message.setMessage("photo");
                                        message.setImageUrl(filepath);
                                        binding.messageBox.setText("");

                                        String randomKey = database.getReference().push().getKey();
                                        HashMap<String , Object>lastMsgObj = new HashMap<>();
                                        lastMsgObj.put("lastMsg" , message.getMessage());
                                        lastMsgObj.put("lastMsgTime" , message.getTimestamp());

                                        database.getReference().child("Chats").child(senderRoom).updateChildren(lastMsgObj);
                                        database.getReference().child("Chats").child(recieverRoom).updateChildren(lastMsgObj);

                                        database.getReference().child("Chats")
                                                .child(senderRoom)
                                                .child("Messages")
                                                .child(randomKey)
                                                .setValue(message)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        database.getReference().child("Chats")
                                                                .child(recieverRoom)
                                                                .child("Messages")
                                                                .child(randomKey)
                                                                .setValue(message)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {

                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}