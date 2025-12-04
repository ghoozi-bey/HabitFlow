package com.example.habitflow;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    EditText edtFirstname, edtLastname, edtEmail, edtPassword;
    Button btnUpdate;
    DatabaseHelper dbHelper;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new DatabaseHelper(this);

        edtFirstname = findViewById(R.id.edtFirstname);
        edtLastname = findViewById(R.id.edtLastname);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnUpdate = findViewById(R.id.btnUpdate);

        // Récupérer l'email de l'utilisateur depuis Dashboard
        userEmail = getIntent().getStringExtra("userEmail");

        loadUserData();

        btnUpdate.setOnClickListener(v -> updateUser());
    }

    private void loadUserData() {
        Cursor cursor = dbHelper.getUserByEmail(userEmail);
        if (cursor.moveToFirst()) {
            String firstname = cursor.getString(cursor.getColumnIndexOrThrow("firstname"));
            String lastname = cursor.getString(cursor.getColumnIndexOrThrow("lastname"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));

            edtFirstname.setText(firstname != null ? firstname : "");
            edtLastname.setText(lastname != null ? lastname : "");
            edtEmail.setText(email != null ? email : "");
            edtPassword.setText(password != null ? password : "");
        }
        cursor.close();
    }

    private void updateUser() {
        String firstname = edtFirstname.getText().toString().trim();
        String lastname = edtLastname.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // Vérifier si le nouvel email existe déjà
        if (!email.equals(userEmail) && dbHelper.isEmailExists(email)) {
            Toast.makeText(this, "Cet email existe déjà", Toast.LENGTH_SHORT).show();
            return;
        }

        //  Mettre à jour la base
        dbHelper.getWritableDatabase().execSQL(
                "UPDATE users SET firstname=?, lastname=?, email=?, password=? WHERE email=?",
                new Object[]{firstname, lastname, email, password, userEmail}
        );

        Toast.makeText(this, "Profil mis à jour avec succès", Toast.LENGTH_SHORT).show();
        finish();
    }
}
