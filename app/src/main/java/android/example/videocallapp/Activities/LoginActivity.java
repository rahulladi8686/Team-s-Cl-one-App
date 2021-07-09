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

    EditText emailBox , passwordBox;
    Button loginButton , createButton;
    TextView forgotPassword;
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this , DashboardActivity.class));
            finish();
        }

        emailBox = findViewById(R.id.emailBox);
        passwordBox = findViewById(R.id.passwordBox);
        loginButton = findViewById(R.id.loginButton);
        createButton = findViewById(R.id.createButton);
        forgotPassword = findViewById(R.id.forgotPassword);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email , pass;
                email = emailBox.getText().toString();
                pass = passwordBox.getText().toString();
                if(email.length() == 0){
                    emailBox.setError("Email is required");
                }else if(pass.length() == 0){
                    passwordBox.setError("Password is required");
                }else{
                    auth.signInWithEmailAndPassword(email , pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                startActivity(new Intent(LoginActivity.this , DashboardActivity.class));
                                Toast.makeText(LoginActivity.this , "logged in." , Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(LoginActivity.this , task.getException().getLocalizedMessage() , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this , SignupActivity.class));
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email , pass;
                email = emailBox.getText().toString();
                if(email.length() == 0){
                    emailBox.setError("Email is required");
                }else{
                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this , "password reset mail has been sent sucessfully" , Toast.LENGTH_SHORT).show();
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