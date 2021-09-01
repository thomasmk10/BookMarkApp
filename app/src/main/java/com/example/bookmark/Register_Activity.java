package com.example.bookmark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.w3c.dom.NameList;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register_Activity extends AppCompatActivity {

    EditText inputEmail;
    EditText inputPassword;

//    public EditText getInputName() {
//        return inputName;
//    }

    EditText inputName;
    EditText inputNumber;
    TextView AlreadyHaveAccount;
    FirebaseAuth auth;
    FirebaseUser User;
    DatabaseReference dbReference;
    ProgressDialog LoadingBar;
//    String Name = "";


//    public Register_Activity(){
//    }
////    public String getName() {
////        return Name;
////    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_);
        inputName = findViewById(R.id.name);
        inputEmail = findViewById(R.id.Email);
        inputPassword = findViewById(R.id.password);
        inputNumber = findViewById(R.id.PhoneNumber);
        AlreadyHaveAccount = findViewById(R.id.AlreadyHaveAccount);

        auth = FirebaseAuth.getInstance();
        User = auth.getCurrentUser();
        dbReference = FirebaseDatabase.getInstance().getReference().child("Users");
        LoadingBar = new ProgressDialog(this);
        buttonFunctionality();

    }

    private void buttonFunctionality()
    {
        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(v -> {
            register();
        });
        AlreadyHaveAccount.setOnClickListener(v -> {
            Intent intent = new Intent(Register_Activity.this, Login_Activity.class);
            startActivity(intent);
        });
    }

    private void register() {
        String txt_username = inputName.getText().toString();
        String txt_email = inputEmail.getText().toString();
        String txt_password = inputPassword.getText().toString();
        String txt_phoneNumber = inputNumber.getText().toString();
        isValidEmail(txt_email);
        isValidPhoneNumber(txt_phoneNumber);
        if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) ||
                TextUtils.isEmpty(txt_password) || TextUtils.isEmpty(txt_phoneNumber)) {
            Toast.makeText(Register_Activity.this, "All fields are required.",
                    Toast.LENGTH_SHORT).show();
        } else if (txt_password.length() < 6) {
            Toast.makeText(Register_Activity.this, "The password entered is not valid.",
                    Toast.LENGTH_SHORT).show();
        } else if (!isValidPhoneNumber(txt_phoneNumber)) {
            Toast.makeText(Register_Activity.this, "This phone number entered is not valid.",
                    Toast.LENGTH_SHORT).show();
        } else if (!isValidEmail(txt_email)) {
            Toast.makeText(Register_Activity.this, "This email entered is not valid.",
                    Toast.LENGTH_SHORT).show();
        } else if (txt_username.length() < 3) {
            Toast.makeText(Register_Activity.this, "This username is too short.",
                    Toast.LENGTH_SHORT).show();
        } else if (txt_username.length() > 10) {
            Toast.makeText(Register_Activity.this, "This username is too long.",
                    Toast.LENGTH_SHORT).show();
        } else {
            LoadingBar.setTitle("Registering");
            LoadingBar.setMessage("Please wait");
            LoadingBar.setCanceledOnTouchOutside(false);
            LoadingBar.show();
            auth.createUserWithEmailAndPassword(txt_email, txt_password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                FirebaseUser firebaseUser = auth.getCurrentUser();
                                assert firebaseUser != null;
                                String userid = firebaseUser.getUid();

                                dbReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("id", userid);
                                hashMap.put("username", txt_username);
                                hashMap.put("imageURL", "default");
                                hashMap.put("password", txt_password);
                                hashMap.put("email", txt_email);
                                hashMap.put("phoneN", txt_phoneNumber);

                                dbReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Intent intent = new Intent(Register_Activity.this, AddLocationReview_Activity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(Register_Activity.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void register2(final String username, String email, String password){

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userid = firebaseUser.getUid();

                            dbReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", username);
                            hashMap.put("imageURL", "default");

                            dbReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Intent intent = new Intent(Register_Activity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(Register_Activity.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    static boolean isValidEmail(String email){
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }
    static boolean isValidPhoneNumber(String s){
        String patterns
                = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
                + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$"
                + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$";
        //1) Begins with 0 or 91
        // 2) Then contains 7 or 8 or 9.
        // 3) Then contains 9 digits
        Pattern p = Pattern.compile(patterns);
        Matcher m = p.matcher(s);
        return (m.find() && m.group().equals(s));
    }
}
