package com.example.habitflow;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import android.os.Build;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DashboardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton btnMenu;
    private Button btnLogout, btnProfile;
    private CalendarView calendarView;
    private ListView listeActivites;
    private TextView txtHistorique;
    private Spinner spinnerSort;

    private final ArrayList<String> historiqueActivites = new ArrayList<>();
    private Calendar selectedDate = Calendar.getInstance();

    private static final String PREFS_NAME = "HabitFlowPrefs";
    private static final String ACTIVITES_KEY = "AllActivities";
    private static final String WELCOME_KEY = "LastWelcomeDate";

    private String userName = "Utilisateur";
    private String userEmail = "";

    // 🔥 Firebase
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // 🔥 Initialiser Firebase
        db = FirebaseFirestore.getInstance();

        // 🔹 Vérifier permission notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
                    android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        // 🔹 Initialisation des vues
        drawerLayout = findViewById(R.id.drawerLayout);
        btnMenu = findViewById(R.id.btnMenu);
        btnLogout = findViewById(R.id.btnLogout);
        calendarView = findViewById(R.id.calendarView);
        listeActivites = findViewById(R.id.listeActivites);
        txtHistorique = findViewById(R.id.txtHistorique);
        btnProfile = findViewById(R.id.btnProfile);
        spinnerSort = findViewById(R.id.spinnerSort);

        // 🔹 Limites calendrier
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.YEAR, -50);
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.YEAR, 50);
        calendarView.setMinDate(minDate.getTimeInMillis());
        calendarView.setMaxDate(maxDate.getTimeInMillis());

        // 🔹 Infos utilisateur
        userName = getIntent().getStringExtra("userName");
        if (userName == null) userName = "Utilisateur";
        userEmail = getIntent().getStringExtra("userEmail");
        if (userEmail == null) userEmail = "";

        showDailyWelcome();

        // 🔹 Menu
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
            intent.putExtra("userEmail", userEmail);
            startActivity(intent);
        });

        // 🔹 Spinner tri
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Plus récente d'abord", "Plus ancienne d'abord"});
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(adapterSpinner);
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                updateHistorique(getDateText(selectedDate));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        loadActivities();

        selectedDate.setTimeInMillis(calendarView.getDate());
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate.set(year, month, dayOfMonth);
            openAddActivityDialog();
        });

        updateHistorique(getDateText(selectedDate));
    }

    private void showDailyWelcome() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String lastDate = prefs.getString(WELCOME_KEY, "");
        String today = getDateText(Calendar.getInstance());
        if (today.equals(lastDate)) return;

        new AlertDialog.Builder(this)
                .setTitle("Bienvenue !")
                .setMessage("Bienvenue " + userName + " 👋")
                .setPositiveButton("OK", null)
                .show();

        prefs.edit().putString(WELCOME_KEY, today).apply();
    }

    private String getDateText(Calendar c) {
        return c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);
    }

    private void updateHistorique(String date) {
        txtHistorique.setText("Activités du " + date);

        ArrayList<String> activitiesForDay = new ArrayList<>();
        for (String activity : historiqueActivites) {
            if (activity.contains("(" + date + ")")) activitiesForDay.add(activity);
        }

        if (spinnerSort.getSelectedItemPosition() == 0) {
            Collections.reverse(activitiesForDay);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                activitiesForDay);
        listeActivites.setAdapter(adapter);
    }


    private void openAddActivityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nouvelle activité");

        final EditText activityNameInput = new EditText(this);
        activityNameInput.setHint("Nom de l'activité");

        final TimePicker timePicker = new TimePicker(this);
        timePicker.setIs24HourView(true);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(activityNameInput);
        layout.addView(timePicker);
        builder.setView(layout);

        builder.setPositiveButton("Confirmer", (dialog, which) -> {
            String activityName = activityNameInput.getText().toString().trim();
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();

            if (activityName.isEmpty()) {
                Toast.makeText(this, "Veuillez entrer un nom d'activité", Toast.LENGTH_SHORT).show();
                return;
            }

            String fullActivity = activityName + " à "
                    + hour + "h" + String.format("%02d", minute)
                    + " (" + getDateText(selectedDate) + ")";

            historiqueActivites.add(fullActivity);

            saveActivities();
            updateHistorique(getDateText(selectedDate));

            // 🔥 Sauvegarde Firebase
            saveActivityToFirebase(activityName, hour, minute, getDateText(selectedDate));

            // 🔔 Notification immédiate
            Intent intent = new Intent(DashboardActivity.this, ReminderReceiver.class);
            intent.putExtra("activityName", activityName);
            sendBroadcast(intent);

            Toast.makeText(this, "Activité enregistrée", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Annuler", null);
        builder.show();
    }

    // 🔥 Fonction Firebase
    private void saveActivityToFirebase(String name, int hour, int minute, String date) {
        String id = String.valueOf(System.currentTimeMillis());

        db.collection("users")
                .document(userEmail)
                .collection("activities")
                .document(id)
                .set(new ActivityModel(name, hour, minute, date));
    }

    private void saveActivities() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> set = new HashSet<>(historiqueActivites);
        prefs.edit().putStringSet(ACTIVITES_KEY, set).apply();
    }

    private void loadActivities() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(ACTIVITES_KEY, new HashSet<>());
        historiqueActivites.clear();
        historiqueActivites.addAll(set);
    }

    // 🔥 Modèle Firebase
    public static class ActivityModel {
        public String name;
        public int hour;
        public int minute;
        public String date;

        public ActivityModel() {}

        public ActivityModel(String name, int hour, int minute, String date) {
            this.name = name;
            this.hour = hour;
            this.minute = minute;
            this.date = date;
        }
    }
}
