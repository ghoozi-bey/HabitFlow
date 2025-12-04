package com.example.habitflow; // 🔥 change avec ton package !

import android.util.Log;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.HashMap;
import java.util.Map;

public class FirestoreHelper {

    private static final String TAG = "FirestoreHelper";

    // Collections
    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_HABITS = "habits";

    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private CollectionReference habitsRef;

    public FirestoreHelper() {
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection(COLLECTION_USERS);
        habitsRef = db.collection(COLLECTION_HABITS);
    }

    // ---------------------------
    // 🔥 USERS
    // ---------------------------

    public void addUser(String firstname, String lastname, String email, String password) {
        Map<String, Object> user = new HashMap<>();
        user.put("firstname", firstname);
        user.put("lastname", lastname);
        user.put("email", email);
        user.put("password", password);

        usersRef.add(user)
                .addOnSuccessListener(documentReference ->
                        Log.d(TAG, "Utilisateur ajouté : " + documentReference.getId()))
                .addOnFailureListener(e -> Log.e(TAG, "Erreur ajout utilisateur", e));
    }

    public void updateUser(String documentId, Map<String, Object> updates) {
        usersRef.document(documentId)
                .update(updates)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Utilisateur mis à jour"))
                .addOnFailureListener(e -> Log.e(TAG, "Erreur mise à jour utilisateur", e));
    }

    public void deleteUser(String documentId) {
        usersRef.document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Utilisateur supprimé"))
                .addOnFailureListener(e -> Log.e(TAG, "Erreur suppression utilisateur", e));
    }

    public void getAllUsers(FirestoreGetUsersCallback callback) {
        usersRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> callback.onSuccess(queryDocumentSnapshots))
                .addOnFailureListener(e -> callback.onFailure(e));
    }

    public interface FirestoreGetUsersCallback {
        void onSuccess(Iterable<QueryDocumentSnapshot> documents);
        void onFailure(Exception e);
    }

    // ---------------------------
    // 🔥 HABITS
    // ---------------------------

    public void addHabit(int id, String title, String description, String date, String time, boolean isCompleted) {
        Map<String, Object> habit = new HashMap<>();
        habit.put("id", id);
        habit.put("title", title);
        habit.put("description", description);
        habit.put("date", date);
        habit.put("time", time);
        habit.put("isCompleted", isCompleted);

        habitsRef.add(habit)
                .addOnSuccessListener(documentReference ->
                        Log.d(TAG, "Habit ajouté : " + documentReference.getId()))
                .addOnFailureListener(e -> Log.e(TAG, "Erreur ajout habit", e));
    }

    public void updateHabit(String documentId, Map<String, Object> updates) {
        habitsRef.document(documentId)
                .update(updates)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Habit mis à jour"))
                .addOnFailureListener(e -> Log.e(TAG, "Erreur mise à jour habit", e));
    }

    public void deleteHabit(String documentId) {
        habitsRef.document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Habit supprimé"))
                .addOnFailureListener(e -> Log.e(TAG, "Erreur suppression habit", e));
    }

    public void getAllHabits(FirestoreGetHabitsCallback callback) {
        habitsRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> callback.onSuccess(queryDocumentSnapshots))
                .addOnFailureListener(e -> callback.onFailure(e));
    }

    public interface FirestoreGetHabitsCallback {
        void onSuccess(Iterable<QueryDocumentSnapshot> documents);
        void onFailure(Exception e);
    }
}
