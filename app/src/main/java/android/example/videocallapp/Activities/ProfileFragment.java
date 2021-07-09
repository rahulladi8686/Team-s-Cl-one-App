package android.example.videocallapp.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.example.videocallapp.R;
import android.example.videocallapp.Models.User;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

public class ProfileFragment extends Fragment {

    FirebaseDatabase mdatabase;
    FirebaseAuth auth;
    FirebaseStorage storage;
    ImageView profilePic;
    TextView name , email;
    User muser;
    Button logout;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        mdatabase = FirebaseDatabase.getInstance();

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profilePic = (ImageView)view.findViewById(R.id.profilePic);
        name = (TextView) view.findViewById(R.id.name);
        email = (TextView) view.findViewById(R.id.mail);
        logout = (Button) view.findViewById(R.id.button2);

        mdatabase.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //users.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    User user = snapshot1.getValue(User.class);
                    if(user.getUid().equals(FirebaseAuth.getInstance().getUid()))
                    {
                        muser = user;
                        if(muser.getProfileImage() != null)
                        Glide.with(getContext()).load(muser.getProfileImage())
                        .placeholder(R.drawable.avatar)
                        .into(profilePic);
                        email.setText(user.getEmail());
                        name.setText(user.getName());
                    }
                }

                //usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(container.getContext() , MainActivity.class));
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //startActivity(new Intent(container.getContext() , VideocallFragment.class));
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(container.getContext());
                builder.setMessage("Do you want to logout?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

//
//        Glide.with(getContext()).load(muser.getProfileImage())
//                .placeholder(R.drawable.avatar)
//                .into(profilePic);

//        profilePic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");
//                startActivityForResult(intent, 45);
//            }
//        });

//        setup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String name = textView.getText().toString();
//                if(name.isEmpty()) {
//                    textView.setError("Please type a name");
//                    return;
//                }
//                if(selectedImage != null){
//                    StorageReference reference = storage.getReference().child("Profiles").child(auth.getUid());
//                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                            if(task.isSuccessful()){
//                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                    @Override
//                                    public void onSuccess(Uri uri) {
//                                        String imageUrl = uri.toString();
//                                        String uid = auth.getUid();
//                                        String name = textView.getText().toString();
//                                        User user = new User(name , imageUrl , uid);
//                                        database
//                                                .getReference()
//                                                .child("Users")
//                                                .child(uid)
//                                                .setValue(user)
//                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                    @Override
//                                                    public void onSuccess(Void aVoid) {
//
//                                                    }
//                                                });
//                                    }
//                                });
//                            }
//                        }
//                    });
//                }
//            }
//        });

        return view;
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(data != null){
//            if(data.getData() != null){
//                profilePic.setImageURI(data.getData());
//                selectedImage = data.getData();
//            }
//        }
//    }
}