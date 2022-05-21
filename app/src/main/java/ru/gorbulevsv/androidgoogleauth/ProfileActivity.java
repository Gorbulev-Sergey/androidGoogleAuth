package ru.gorbulevsv.androidgoogleauth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.SignInAccount;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {
    TextView textName, textEmail;
    Button buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        textName = findViewById(R.id.textName);
        textEmail = findViewById(R.id.textEmail);
        buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });

        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null) {
            textName.setText(signInAccount.getDisplayName());
            textEmail.setText(signInAccount.getEmail());
        }
    }
}