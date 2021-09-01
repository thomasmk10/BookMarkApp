package com.example.bookmark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SuggestLocationActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser User;
    DatabaseReference UserReference;
    String UserId;
    EditText monumentSuggestion;
    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest_location);

        back = findViewById(R.id.BackSuggest);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SuggestLocationActivity.this, MapsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        auth = FirebaseAuth.getInstance();
        User = auth.getCurrentUser();
        UserReference = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    public void onDisplay(View v){
        UserId = User.getUid();
        monumentSuggestion = (EditText) findViewById(R.id.SuggestEditText);
        String message = monumentSuggestion.getText().toString();
        HashMap<String, Object> Suggestions = new HashMap<>();
        Suggestions.put("MonumentSuggestion", message);
        UserReference.child(UserId).child("Suggestions").push().setValue(Suggestions).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(SuggestLocationActivity.this,"Location added",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SuggestLocationActivity.this, MapsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SuggestLocationActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

