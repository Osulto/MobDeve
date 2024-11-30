package com.mobdeve.s19.stocksmart;

import android.content.Context;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.auth.ActionCodeSettings;
import java.util.Map;
import java.util.HashMap;

public class FirebaseAuthManager {
    public interface AuthCallback {
        void onSuccess(String userId);
        void onError(String error);
    }

    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public FirebaseAuthManager(Context context) {
        this.auth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();

        // Disable reCAPTCHA verification for development
        auth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);
    }

    private String usernameToEmail(String username) {
        return username + "@stocksmart.com";
    }

    public void registerUser(String username, String password, String businessName, AuthCallback callback) {
        String email = usernameToEmail(username);
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    String userId = result.getUser().getUid();
                    saveUserToFirestore(userId, username, businessName, callback);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    private void saveUserToFirestore(String userId, String username, String businessName, AuthCallback callback) {
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("businessName", businessName);
        user.put("createdAt", FieldValue.serverTimestamp());

        db.collection("businesses")
                .document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> callback.onSuccess(userId))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void loginUser(String username, String password, AuthCallback callback) {
        String email = usernameToEmail(username);
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> callback.onSuccess(result.getUser().getUid()))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public boolean isUserLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public void logoutUser() {
        auth.signOut();
    }
}