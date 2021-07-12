package android.example.videocallapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.example.videocallapp.R;
import android.example.videocallapp.Models.User;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseDatabase mDatabase;
    private FirebaseFirestore database;
    private FirebaseStorage storage;

    private EditText nameBox, emailBox, passwordBox;
    private Button loginButton, signupButton;
    private ImageView profilePic;
    private Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        
        //Getting reference  of firebase varibles
        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        //Getting refrence of layout variables
        nameBox = findViewById(R.id.nameBox);
        emailBox = findViewById(R.id.emailBox);
        passwordBox = findViewById(R.id.passwordBox);
        signupButton = findViewById(R.id.createButton);
        loginButton = findViewById(R.id.loginButton);
        profilePic = findViewById(R.id.profilePic);
        
        //Added click listener to profile pic to get the the image from phone's storage
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 45);
            }
        });

        //Added click listener to signup button
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, pass, name;
                
                //Getting data typed by user
                email = emailBox.getText().toString();
                pass = passwordBox.getText().toString();
                name = nameBox.getText().toString();
                
                
                //Creating a User object and storing the user entered data
                User user = new User();
                user.setEmail(email);
                user.setName(name);
                user.setPass(pass);

                //Making sure name and email is entered
                if (name.length() == 0) {
                    nameBox.setError("Name is required");
                } else if (email.length() == 0) {
                    emailBox.setError("Email is required");
                } else if (pass.length() == 0) {
                    passwordBox.setError("Password is required");
                } else {
                    //Getting connected to firebase authentication
                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                if(selectedImage == null){
                                    //Getting the present user's UID
                                    String uid = auth.getUid();
                                    user.setUid(uid);

                                    //storing the User object in the firebase database
                                    mDatabase.getReference().child("Users")
                                            .child(auth.getUid())
                                            .setValue(user);

                                    //storing the user data in firestore
                                    database.collection("Users")
                                            .document().set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                        }
                                    });
                                    Toast.makeText(SignupActivity.this, "Account is created.", Toast.LENGTH_SHORT).show();
                                }else{
                                    //Getting the present user's UID
                                    String uid = auth.getUid();
                                    user.setUid(uid);

                                    Toast.makeText(SignupActivity.this, "Account is created.", Toast.LENGTH_SHORT).show();

                                    //Getting refrence of storage in firebase to store image
                                    StorageReference reference = storage.getReference().child("Profiles").child(auth.getUid());

                                    //Storing the image in Firebase storage
                                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if(task.isSuccessful()){
                                                //If image upload is sucessful we get the url of uploaded image
                                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        //here we are storing url of profile pic in user object
                                                        user.setProfileImage(uri.toString());

                                                        //storing the User object in the firebase database
                                                        mDatabase
                                                                .getReference()
                                                                .child("Users")
                                                                .child(uid)
                                                                .setValue(user)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {

                                                                    }
                                                                });
                                                    }
                                                });
                                            }
                                        }
                                    });

                                    //storing the user data in firestore
                                    database.collection("Users")
                                            .document().set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            FirebaseAuth.getInstance().signOut();
                                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(SignupActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
        
        //this listener takes to the login page
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(SignupActivity.this , LoginActivity.class));
            }
        });
    }

    //Here we get the uri of the selected image 
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null){
            if(data.getData() != null){
                profilePic.setImageURI(data.getData());
                selectedImage = data.getData();
            }
        }
    }
}