package com.example.bookmark;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bookmark.Adapter.MessageAdapter;
import com.example.bookmark.Model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.bookmark.data.model.User;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    TextView markertxt, desc;
    Button back;
    ListView listView;
    FirebaseAuth auth;
    FirebaseUser User;
    String UserID;
    ListView msgList;

    ArrayList<String> comments = new ArrayList<>();
    ArrayList<String> dividedStars = new ArrayList<>();
    ArrayList<String> dividedComments = new ArrayList<>();

    ArrayList<String> usernames = new ArrayList<>();
    ArrayList<String> ids = new ArrayList<>();

    @NonNull
    ArrayList<String> seperateComments(String input) {
        int countB = 0;
        ArrayList<Integer> places = new ArrayList<>();
        ArrayList<String> comments = new ArrayList<>();
        ArrayList<String> comTrim = new ArrayList<>();

        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '}') {
                countB++;
                places.add(i);
            }
        }

        boolean isFirst = true;
        String[] parts = input.split("[}]");
        for (int i = 0; i < (countB - 2); i++) {
            if (isFirst) {
                String comm = parts[i].substring(11, parts[i].length());
                isFirst = false;
                comments.add(comm);
            } else {
                String comm = parts[i].substring(1, parts[i].length());
                comments.add(comm);
            }
        }

        isFirst = true;
        for (int i = 0; i < comments.size(); i++) {
            if (isFirst) {
                String temp = comments.get(i);
                temp = temp.substring(22);
                comments.set(i, temp);
                isFirst = false;
            } else {
                String temp = comments.get(i);
                temp = temp.substring(23);
                comments.set(i, temp);
            }
        }
        return comments;
    }

    ArrayList<String> getStarRatings(ArrayList<String> input, String title) {
        ArrayList<String> stars = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            String temp = input.get(i);
            String[] parts = temp.split(",");
            String[] location = parts[2].split("=");
            if (location[1].equals(title)) {
                stars.add(parts[0]);
            }
        }
        return stars;
    }

    ArrayList<String> getComments(ArrayList<String> input, String title) {
        ArrayList<String> com = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            String temp = input.get(i);
            String[] parts = temp.split(",");
            String[] location = parts[2].split("=");
            String[] getCom = parts[1].split("=");
            if (location[1].equals(title)) {
                com.add(getCom[1]);
            }
        }
        return com;
    }

    void getNames(String input) {
        ArrayList<String> names = new ArrayList<>();

        String test = "username";
        String[] parts = input.split(",");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
            if (parts[i].substring(0,8).equals(test)){
                usernames.add(parts[i].substring(9, parts[i].length() - 1));
            }
        }
    }

    void getIDs(String input) {
        ArrayList<String> IDs = new ArrayList<>();

        String test = "id";
        String[] parts = input.split(",");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
            if (parts[i].substring(0,2).equals(test)){
                ids.add(parts[i].substring(3, parts[i].length()));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        listView = findViewById(R.id.listViewX);
        auth = FirebaseAuth.getInstance();
        User = auth.getCurrentUser();
        UserID = User.getUid();

        ArrayList<String> list = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.list_item, list);
        listView.setAdapter(adapter);

        getUsers();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments");//.child("Location");
        ValueEventListener eventListener = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                comments.clear();
                dividedStars.clear();
                dividedComments.clear();
                try{


                String orignal = dataSnapshot.child(UserID).getValue().toString();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    if (!snapshot.getValue().toString().equals(orignal)) {

                        String theID = snapshot.getKey();
                        int index = ids.indexOf(theID);
                        String userN = usernames.get(index);

                        comments = seperateComments(snapshot.getValue().toString());
                        dividedStars = getStarRatings(comments, getIntent().getStringExtra("title"));
                        dividedComments = getComments(comments, getIntent().getStringExtra("title"));

                        for (int i = 0; i < dividedStars.size(); i++) {
                            String temp = userN + ": " + dividedComments.get(i) + " " + dividedStars.get(i);
                            //String temp = dividedComments.get(i) + " " + dividedStars.get(i);
                            list.add(temp);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }catch (Exception e){
                    Toast.makeText(DetailsActivity.this, "Need to enter own review first", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
                Intent intent = new Intent(DetailsActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        reference.addListenerForSingleValueEvent(eventListener);

        markertxt = findViewById(R.id.marker);
        desc = findViewById(R.id.descriptiontxt);
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String space = "\n";
        markertxt.setText(space + title);
        desc.setText(description);

        back = (Button) findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailsActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });
    }

    private void getUsers() {

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("Users");//.child("Location");
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    getNames(snapshot.getValue().toString());
                    getIDs(snapshot.getValue().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}