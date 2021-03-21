package com.example.healthtracker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    MaterialButton _logOutButton;
    TextView _userDisplayName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
        }
        _userDisplayName = (TextView)findViewById(R.id.user_display_name);
        _logOutButton = (MaterialButton)findViewById(R.id.btn_logout);
        _logOutButton.setOnClickListener(v -> signOut());

        _userDisplayName.setText("Hello " + getUserName() + "!");
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            currentUser.reload();
//        }
//    }

    @Nullable
    private String getUserPhotoUrl() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getPhotoUrl() != null) {
            return user.getPhotoUrl().toString();
        }
        return null;
    }

    private String getUserName() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getDisplayName() != null) {
            if(user.getDisplayName().equals(""))
            {
                return "Anonymous, please update your user name";
            }else{
                return user.getDisplayName();
            }
        }else
        {
            return "Anonymous!";
        }
    }

    private void signOut() {
        mAuth.signOut();
        startActivity(new Intent(this, SignInActivity.class));
        finish();
    }


}