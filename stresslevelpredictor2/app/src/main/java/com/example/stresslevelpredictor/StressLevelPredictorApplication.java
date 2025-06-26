package com.example.stresslevelpredictor;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;

public class StressLevelPredictorApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Firebase debug logging (must be done before any other Firebase usage)
        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
