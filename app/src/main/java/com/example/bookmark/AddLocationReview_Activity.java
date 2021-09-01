package com.example.bookmark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class  AddLocationReview_Activity extends AppCompatActivity {

    private EditText  LocationComment;
    TextView LocationName;
    FirebaseAuth auth;
    FirebaseUser User;
    DatabaseReference UserReference;
    Button AddMoreLocationReviews;
    Button SaveLocations;
    Button skip;
    Button Suggest;
    RatingBar ratingStars;
    Dialog dialog;
    double numberOfStars = 0;
    ArrayList<String> arrayList;
    String UserId;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location_review);
        ratingStars = findViewById(R.id.fiveStars);
        ratingStars.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                numberOfStars = ratingBar.getRating();
            }
        });
        LocationName = findViewById(R.id.text_view);
        LocationComment = findViewById(R.id.Comment);

        LocationName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(AddLocationReview_Activity.this);
                dialog.setContentView(R.layout.dialog_searchable_spinner);
                //set custom height and width
                dialog.getWindow().setLayout(1000, 1200);
                //set transparent background
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                //show dialog
                dialog.show();

                //Initialize and assign variable
                EditText editText = dialog.findViewById(R.id.edit_text);
                ListView listView = dialog.findViewById(R.id.list_view);
                //initialize array adapter
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddLocationReview_Activity.this, android.R.layout.simple_list_item_1, arrayList);
                //set adapter
                listView.setAdapter(adapter);

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        //Filter array List
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
                        //when item selected from list
                        //set selected item on LocationName
                        LocationName.setText(adapter.getItem(i));
//                        resultX.setText(adapter.getItem(i));
                        dialog.dismiss();
                    }
                });

            }
        });

        Suggest = findViewById(R.id.SuggestButton);
        auth = FirebaseAuth.getInstance();
        User = auth.getCurrentUser();
        UserReference = FirebaseDatabase.getInstance().getReference().child("Users");
        AddMoreLocationReviews = (Button) findViewById(R.id.AddMoreLocations);
        SaveLocations = (Button) findViewById(R.id.Continue);
        skip = (Button) findViewById(R.id.SKIP);
        ButtonFunctionality();
        StoreLocationInfoIntoFireBase();

        //Custom Drop Down Code
        arrayList = new ArrayList<>();

        arrayList.add("Charging Bull");
        arrayList.add("Chrysler Building");
        arrayList.add("Empire State Building");
        arrayList.add("Statue of Liberty");
        arrayList.add("World Trade Center");

        Suggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddLocationReview_Activity.this, SuggestLocationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

    }
    private void ButtonFunctionality()
    {
        AddMoreLocationReviews.setOnClickListener(v -> {
            StoreLocationInfoIntoFireBase();
        });
        SaveLocations.setOnClickListener(v -> {
            StoreLastLocationInfo();
        });
        skip.setOnClickListener(v -> {
            Intent i = new Intent(AddLocationReview_Activity.this, MapsActivity.class);
            startActivity(i);
        });
    }

    private void StoreLocationInfoIntoFireBase()
    {
        UserId = User.getUid();
        String txt_LocationName = LocationName.getText().toString();
        String txt_LocationComment = LocationComment.getText().toString();
        if (TextUtils.isEmpty(txt_LocationName) || TextUtils.isEmpty(txt_LocationComment)) {
            Toast.makeText(AddLocationReview_Activity.this, "All fields are required.",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            HashMap<String, Object> LocationInformation = new HashMap<>();
            LocationInformation.put("LocationName", txt_LocationName);
            LocationInformation.put("LocationComment", txt_LocationComment);
            LocationInformation.put("Star Rating", numberOfStars);
            LocationInformation.put("Date_Time", formatter.format(calendar.getTime()));

            UserReference.child(UserId).child("Location").push().setValue(LocationInformation).addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    Toast.makeText(AddLocationReview_Activity.this,"Location added",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AddLocationReview_Activity.this, AddLocationReview_Activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddLocationReview_Activity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            });
            StoreComments();
        }
    }

    private void StoreLastLocationInfo()
    {
        UserId = User.getUid();
        String txt_LocationName = LocationName.getText().toString();
        String txt_LocationComment = LocationComment.getText().toString();
        if (TextUtils.isEmpty(txt_LocationName) || TextUtils.isEmpty(txt_LocationComment)) {
            Toast.makeText(AddLocationReview_Activity.this, "All fields are required.",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            HashMap<String, Object> LocationInformation = new HashMap<>();
            LocationInformation.put("LocationName", txt_LocationName);
            LocationInformation.put("LocationComment", txt_LocationComment);
            LocationInformation.put("Star Rating", numberOfStars);
            LocationInformation.put("Date_Time", formatter.format(calendar.getTime()));
            UserReference.child(UserId).child("Location").push().setValue(LocationInformation).addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    Toast.makeText(AddLocationReview_Activity.this,"Location added",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AddLocationReview_Activity.this, MapsActivity.class);    //THIS NEEDS TO BE CHANGED TO MAP VIEW
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddLocationReview_Activity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            });

            StoreComments();


        }
    }
    private void StoreComments(){
//        Register_Activity userName;
//        String x = userName.getName();
        UserReference = FirebaseDatabase.getInstance().getReference().child("Comments");
        String txt_LocationName = LocationName.getText().toString();
        String txt_LocationComment = LocationComment.getText().toString();
        HashMap<String, Object> Information = new HashMap<>();
//        Information.put("User", x);
        Information.put("LocationName", txt_LocationName);
        Information.put("LocationComment", txt_LocationComment);
        Information.put("Star Rating", numberOfStars);
        UserReference.child(UserId).child("Location").push().setValue(Information).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddLocationReview_Activity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

//
////        Register_Activity userName = new Register_Activity();
////        String x = userName.getUserName()
//    UserReference = FirebaseDatabase.getInstance().getReference().child("Comments");
//    //        UserReference.child("Users").child("Location")
////                .orderByChild("name")
////                .equalTo("Empire State Building")
////                .addListenerForSingleValueEvent(new ValueEventListener() {
////                    @Override
////                    public void onDataChange(DataSnapshot dataSnapshot) {
////                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
////                            String clubkey = childSnapshot.getKey();
////                        }
////                    }
////                    @Override
////                    public void onCancelled(@NonNull DatabaseError databaseError) {
////
////                    }
////                });;
//    String txt_LocationName = LocationName.getText().toString();
//    String txt_LocationComment = LocationComment.getText().toString();
//    HashMap<String, Object> Information = new HashMap<>();
////        Information.put("User", x);
//        Information.put("LocationName", txt_LocationName);
//        Information.put("LocationComment", txt_LocationComment);
//        Information.put("Star Rating", numberOfStars);
//        UserReference.child(UserId).child("Location").push().setValue(Information).addOnSuccessListener(new OnSuccessListener() {
//        @Override
//        public void onSuccess(Object o) {
//        }
//    }).addOnFailureListener(new OnFailureListener() {
//        @Override
//        public void onFailure(@NonNull Exception e) {
//            Toast.makeText(AddLocationReview_Activity.this, e.toString(), Toast.LENGTH_SHORT).show();
//        }
//    });
//}

}