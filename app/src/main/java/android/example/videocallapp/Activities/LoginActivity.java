package android.example.videocallapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.example.videocallapp.R;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private EditText emailBox , passwordBox;
    private Button loginButton , createButton;
    private TextView forgotPassword;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Getting reference  of firebase varibles
        auth = FirebaseAuth.getInstance();

        //If user is already signed in he will not be asked again to sign in
        if(auth.getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this , DashboardActivity.class));
            finish();
        }

        //Getting refrence of layout variables
        emailBox = findViewById(R.id.emailBox);
        passwordBox = findViewById(R.id.passwordBox);
        loginButton = findViewById(R.id.loginButton);
        createButton = findViewById(R.id.createButton);
        forgotPassword = findViewById(R.id.forgotPassword);

        //Added click listener to login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email , pass;

                //Getting the data entered by user
                email = emailBox.getText().toString();
                pass = passwordBox.getText().toString();

                //Checking if email and password is entered or not
                if(email.length() == 0){
                    emailBox.setError("Email is required");
                }else if(pass.length() == 0){
                    passwordBox.setError("Password is required");
                }else{
                    //Here google authentication occurs
                    auth.signInWithEmailAndPassword(email , pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                startActivity(new Intent(LoginActivity.this , DashboardActivity.class));
                                Toast.makeText(LoginActivity.this , "logged in." , Toast.LENGTH_SHORT).show();
                            }else{
                                //Here error is thrown by Google Auth like if it is an invalid or unregistered mail
                                Toast.makeText(LoginActivity.this , task.getException().getLocalizedMessage() , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        //Added click listener to signup button
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this , SignupActivity.class));
            }
        });

        //Added click listener to forgot password
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email , pass;
                email = emailBox.getText().toString();

                //Checks if mail is entered or not
                if(email.length() == 0){
                    emailBox.setError("Email is required");
                }else{
                    //Firebase Auth sends pass word reset mail to the user's registered gmail
                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this , "Password reset mail has been sent sucessfully" , Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(LoginActivity.this , task.getException().getLocalizedMessage() , Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });

    }
    public void onBackPressed() {
        finishAffinity();
    }
}