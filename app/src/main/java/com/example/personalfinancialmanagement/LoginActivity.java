package com.example.personalfinancialmanagement;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth auth;
    private Intent intent;
    private ProgressBar progressBar;
    private EditText editTextpassword, editTextemail;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private ValueEventListener a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();


        progressBar = findViewById(R.id.login_progress);
        progressBar.bringToFront();
        editTextpassword = findViewById(R.id.password_edit_text);
        editTextemail = findViewById(R.id.email_edit_text);

        //sign in
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);

        //intent to register activity
        findViewById(R.id.email_sign_up_button).setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.email_sign_up_button:
                //go into register activity
                intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
//                finish();
                break;
            case R.id.email_sign_in_button:


                String email = editTextemail.getText().toString().trim();
                String password = editTextpassword.getText().toString().trim();


                //validation
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Email address cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Password cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(getApplicationContext(), "Invalid email", Toast.LENGTH_SHORT).show();
                    return;
                }

                //set progess bar
                progressBar.setVisibility(View.VISIBLE);

                //create user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.

                                user = auth.getCurrentUser();
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                } else {
                                    user = auth.getCurrentUser();
                                    if (user != null) {

                                        a = new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                if (dataSnapshot.exists()) {
//                                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                        startActivity(intent);


//                                                    }


                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                            }
                                        };
                                        databaseReference.child(user.getUid()).child("User_Information").addListenerForSingleValueEvent(a);
                                    }
                                }
                            }
                        });
                break;
        }

    }

}