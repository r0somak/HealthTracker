package com.example.healthtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    EditText _emailText;
    EditText _passwordText;
    EditText _passwordConfirmText;
    MaterialButton _createUserButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        _emailText = (EditText)findViewById(R.id.input_email);
        _passwordText = (EditText)findViewById(R.id.input_password);
        _passwordConfirmText = (EditText)findViewById(R.id.input_password_confirm);
        _createUserButton = (MaterialButton)findViewById(R.id.btn_create_acc);

        _createUserButton.setOnClickListener(v -> createAccount());
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    private void createAccount()
    {
        if (!validate()) {
            onCreationFailed();
            return;
        }
        _createUserButton.setEnabled(false);

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        mDatabase.child("users").child(user.getUid()).child("email").setValue(user.getEmail());
                        Toast.makeText(SignUpActivity.this, "Account created.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        onCreationFailed();
                    }
                });
    }

    private void addUserToDb()
    {

    }

    public void onCreationFailed() {
        Toast.makeText(getBaseContext(), "Account creation failed", Toast.LENGTH_LONG).show();

        _createUserButton.setEnabled(true);
    }

    public boolean validate()
    {
        boolean valid = true;
        boolean validEmail = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String passwordConfirmed = _passwordConfirmText.getText().toString();

        try
        {
            InternetAddress emadd = new InternetAddress(email);
            emadd.validate();
        }catch (AddressException ex)
        {
            _emailText.setError("Email not correct: ");
            validEmail = false;
        }
        if(validEmail)
        {
            _emailText.setError(null);
        }

        if(!password.equals(passwordConfirmed))
        {
            _passwordConfirmText.setError("Passwords don`t match");
            valid = false;
        }else
        {
            _passwordConfirmText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}