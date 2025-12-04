package com.example.habitflow;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailInput;
    private Button btnRecover;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailInput = findViewById(R.id.emailForgot);
        btnRecover = findViewById(R.id.btnRecover);
        db = new DatabaseHelper(this);

        btnRecover.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Veuillez entrer votre email.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Récupération du mot de passe depuis la base
            String password = db.getPasswordByEmail(email);

            if (password != null) {
                // Affichage dans une boîte de dialogue
                showPasswordDialog(password);

            } else {
                Toast.makeText(this, "Aucun compte trouvé avec cet email.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPasswordDialog(String password) {
        new AlertDialog.Builder(this)
                .setTitle("Votre mot de passe")
                .setMessage("Votre mot de passe est :\n\n" + password)
                .setPositiveButton("OK", null)
                .show();
    }
}

