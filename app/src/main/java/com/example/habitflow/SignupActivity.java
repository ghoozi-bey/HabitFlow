package com.example.habitflow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    EditText lastnameEditText, firstnameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    Button signupButton;
    DatabaseHelper dbHelper; // Helper SQLite

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        // Initialiser le helper
        dbHelper = new DatabaseHelper(this);

        // Liaison des éléments XML
        lastnameEditText = findViewById(R.id.lastnameEditText);
        firstnameEditText = findViewById(R.id.firstnameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        signupButton = findViewById(R.id.signupButton);

        // Action lors du clic sur "S'inscrire"
        signupButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String lastname = lastnameEditText.getText().toString().trim();
        String firstname = firstnameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        // Vérification des champs
        if (lastname.isEmpty() || firstname.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
            return;
        }

        // Vérifier si l'email existe déjà
        if (dbHelper.isEmailExists(email)) {
            Toast.makeText(this, "Erreur : Email déjà utilisé", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ajouter l'utilisateur dans la base
        boolean inserted = dbHelper.addUser(firstname, lastname, email, password);
        if (inserted) {
            Toast.makeText(this, "Inscription réussie pour " + firstname + " " + lastname, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Erreur lors de l'inscription", Toast.LENGTH_SHORT).show();
        }
    }

    // Redirection vers LoginActivity
    public void goToLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}

