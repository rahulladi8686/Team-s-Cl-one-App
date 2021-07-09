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
    FirebaseAuth auth;
    FirebaseDatabase mdatabase;
    FirebaseFirestore database;
    EditText nameBox, emailBox, passwordBox;
    Button loginButton, signupButton;
    ImageView profilePic;
    Uri selectedImage;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        mdatabase = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        nameBox = findViewById(R.id.nameBox);
        emailBox = findViewById(R.id.emailBox);
        passwordBox = findViewById(R.id.passwordBox);
        signupButton = findViewById(R.id.createButton);
        loginButton = findViewById(R.id.loginButton);
        profilePic = findViewById(R.id.profilePic);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 45);
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, pass, name;
                email = emailBox.getText().toString();
                pass = passwordBox.getText().toString();
                name = nameBox.getText().toString();


                User user = new User();
                user.setEmail(email);
                user.setName(name);
                user.setPass(pass);

                if (name.length() == 0) {
                    nameBox.setError("Name is required");
                } else if (email.length() == 0) {
                    emailBox.setError("Email is required");
                } else if (pass.length() == 0) {
                    passwordBox.setError("Password is required");
                } else {
                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                if(selectedImage == null){
                                    String uid = auth.getUid();
                                    user.setUid(uid);
                                    mdatabase.getReference().child("Users")
                                            .child(auth.getUid())
                                            .setValue(user);
                                    database.collection("Users")
                                            .document().set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                        }
                                    });
                                    Toast.makeText(SignupActivity.this, "Account is created.", Toast.LENGTH_SHORT).show();
                                }else{
                                    String uid = auth.getUid();
                                    user.setUid(uid);
                                    Toast.makeText(SignupActivity.this, "Account is created.", Toast.LENGTH_SHORT).show();
                                    StorageReference reference = storage.getReference().child("Profiles").child(auth.getUid());
                                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if(task.isSuccessful()){
                                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        user.setProfileImage(uri.toString());
                                                        mdatabase
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
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this , LoginActivity.class));
            }
        });
    }

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