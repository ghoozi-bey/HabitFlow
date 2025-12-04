package com.example.habitflow;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    Button loginButton;
    TextView forgotPasswordText;
    ProgressBar progressBar;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        try {
            dbHelper = new DatabaseHelper(this);

            emailEditText = findViewById(R.id.email);
            passwordEditText = findViewById(R.id.password);
            loginButton = findViewById(R.id.btnLogin);
            forgotPasswordText = findViewById(R.id.textView2);
            progressBar = findViewById(R.id.progressBar);

            //  Clic login
            loginButton.setOnClickListener(v -> loginUser());

            //  Mot de passe oublié
            forgotPasswordText.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur à l'ouverture : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void loginUser() {
        try {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            loginButton.setEnabled(false);

            new Handler().postDelayed(() -> {

                boolean isValid = dbHelper.checkUser(email, password);

                progressBar.setVisibility(View.GONE);
                loginButton.setEnabled(true);

                if (isValid) {
                    String userName = dbHelper.getUserFullNameByEmail(email);
                    if (userName == null || userName.isEmpty()) {
                        userName = "Utilisateur";
                    }

                    Toast.makeText(this, "Bienvenue " + userName, Toast.LENGTH_SHORT).show();

                    //  Passage du nom ET de l'email pour le profil
                    Intent intent = new Intent(this, DashboardActivity.class);
                    intent.putExtra("userName", userName);
                    intent.putExtra("userEmail", email);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
                }

            }, 1200);

        } catch (Exception e) {
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
            loginButton.setEnabled(true);
            Toast.makeText(this, "Erreur lors du login : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void goToSignup(View view) {
        try {
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur navigation : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
