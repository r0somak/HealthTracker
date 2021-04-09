package com.example.healthtracker;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserSettingsActivity extends AppCompatActivity {
    private static final String TAG = "SignInActivity";
    FirebaseUser user;
    EditText _usernameText;
    MaterialButton _updateUsrBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        _usernameText = findViewById(R.id.input_usr);
        _updateUsrBtn = findViewById(R.id.btn_update);

        _usernameText.setText(user.getDisplayName());
        _updateUsrBtn.setOnClickListener(v -> updateUsername());
    }

    public void updateUsername() {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(_usernameText.getText().toString())
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getBaseContext(), "Username changed", Toast.LENGTH_LONG).show();
                            _usernameText.setText("");
                            Log.d(TAG, "User profile updated.");
                        }
                    }
                });
    }
}