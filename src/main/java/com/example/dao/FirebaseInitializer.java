package com.example.dao;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import java.io.FileInputStream;

public class FirebaseInitializer {
    private static Firestore db;
    public static Firestore getFirestore() {
        if (db == null) {
            try {
                FileInputStream serviceAccount = new FileInputStream("/Users/srikumarv/Downloads/CraftNote-13-32-35/oops-5313a-firebase-adminsdk-agkvx-b64e4f418f.json");
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .setDatabaseUrl("https://oops-5313a.firebaseio.com/")
                        .build();            
                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options);
                    System.out.println("[FirebaseInit] Firebase initialized successfully.");
                } else {
                    System.out.println("[FirebaseInit] Firebase already initialized.");
                }

                db = FirestoreClient.getFirestore();
                System.out.println("[FirebaseInit] Firestore instance created.");

            } catch (Exception e) {
                System.err.println("[FirebaseInit] ERROR: Firebase initialization failed.");
                e.printStackTrace();
                db = null;  // Make sure to mark it as failed
            }
        }else {
            System.out.println("[FirebaseInit] Firestore already available.");
        }
        return db;
    }
}

