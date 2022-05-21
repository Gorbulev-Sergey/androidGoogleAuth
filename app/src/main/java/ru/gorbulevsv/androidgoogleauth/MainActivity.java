package ru.gorbulevsv.androidgoogleauth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private GoogleSignInClient googleSignInClient;

    EditText editEmail, editPassword;
    Button buttonRegistration, buttonAuthorisation, buttonGoogleAuth;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createRequest();

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {

            } else {
                Toast.makeText(this, "Вы не вошли", Toast.LENGTH_SHORT).show();
            }
        };

        buttonGoogleAuth = findViewById(R.id.buttonGoogleAuth);
        buttonGoogleAuth.setOnClickListener(view -> {
            SignIn();
        });

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        buttonRegistration = findViewById(R.id.buttonRegisration);
        buttonRegistration.setOnClickListener(view -> {
            registration(editEmail.getText().toString(), editPassword.getText().toString());
        });

        buttonAuthorisation = findViewById(R.id.buttonAuthtorization);
        buttonAuthorisation.setOnClickListener(view -> {
            authorization(editEmail.getText().toString(), editPassword.getText().toString());
        });
    }

    private void createRequest() {
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, options);
    }

    private void SignIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Не удалось авторизоваться", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Для регистрации через email
    public void registration(String email, String password) {
        if (!email.isEmpty() && !password.isEmpty()) {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    editEmail.setText("");
                    editPassword.setText("");
                    Toast.makeText(this, "Регистрация прошла успешно", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Email и пароль не должны быть пустыми", Toast.LENGTH_SHORT).show();
        }
    }

    // Для входа через email
    public void authorization(String email, String password) {
        if (!email.isEmpty() && !password.isEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    editEmail.setText("");
                    editPassword.setText("");
                    Toast.makeText(this, "Авторизация прошла успешно", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Ошибка авторизации", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Email и пароль не должны быть пустыми", Toast.LENGTH_SHORT).show();
        }
    }
}