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

    private FirebaseDatabase mdatabase;
    private FirebaseAuth auth;
    private FirebaseStorage storage;

    private ImageView profilePic;
    private TextView name , email;
    private User muser;
    private Button logout;

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

        //Getting reference  of firebase varibles
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        mdatabase = FirebaseDatabase.getInstance();

        //Inflating the layout
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //Getting reference  of layout varibles
        profilePic = (ImageView)view.findViewById(R.id.profilePic);
        name = (TextView) view.findViewById(R.id.name);
        email = (TextView) view.findViewById(R.id.mail);
        logout = (Button) view.findViewById(R.id.button2);

        //Here we get reference to database and then to specific child "Users"
        mdatabase.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren()){

                    //Getting the user's refernce present in database
                    User user = snapshot1.getValue(User.class);

                    //Comparing the user from data base to the logged in user
                    if(user.getUid().equals(FirebaseAuth.getInstance().getUid()))
                    {
                        muser = user;
                        if(muser.getProfileImage() != null)
                            //Setting user's profile uisng Glide
                            Glide.with(getContext()).load(muser.getProfileImage())
                            .placeholder(R.drawable.avatar)
                            .into(profilePic);
                            //Setting user's name and email
                            email.setText(user.getEmail());
                            name.setText(user.getName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Added onclick listener to logout
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // If user selects yes then he gets logged out or else remains logged in
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(container.getContext() , MainActivity.class));
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                //Createds a dialog to show yes or no option
                AlertDialog.Builder builder = new AlertDialog.Builder(container.getContext());
                builder.setMessage("Do you want to logout?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });
        return view;
    }
}