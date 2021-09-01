package com.example.bookmark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class Login_Activity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    Button SignInButton;
    Button RegisterAccount;
    ProgressDialog LoadingBar;
    FirebaseAuth auth;
    TextView passReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);
        inputEmail = findViewById(R.id.Email);
        inputPassword = findViewById(R.id.password);
        auth = FirebaseAuth.getInstance();
        LoadingBar = new ProgressDialog(this);
        passReset = findViewById(R.id.forgotPassText);

        ButtonFunctionality();
        passReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password?");
                passwordResetDialog.setMessage("Enter your Email to receive Reset Link");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail = resetMail.getText().toString();
                        auth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Login_Activity.this, "Email Sent", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login_Activity.this, "Error. Reset not sent"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                passwordResetDialog.create().show();
            }
        });



    }
    private void ButtonFunctionality()
    {
        SignInButton = findViewById(R.id.BTNlogin);
        SignInButton.setOnClickListener(v -> {
            login();
        });

        RegisterAccount = (Button) findViewById(R.id.BTNregister);
        RegisterAccount.setOnClickListener(v -> {
            Intent registerUser = new Intent(Login_Activity.this,Register_Activity.class);
            startActivity(registerUser);
        });
    }

    private void login()
    {
        String txt_email = inputEmail.getText().toString();
        String txt_password = inputPassword.getText().toString();
        if (TextUtils.isEmpty(txt_email) && TextUtils.isEmpty(txt_password)) {
            Toast.makeText(Login_Activity.this, "All fields are required.", Toast.LENGTH_SHORT).show();
        }
        else if (!isValidEmail(txt_email)) {
            Toast.makeText(Login_Activity.this, "This email entered is not valid.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            LoadingBar.setTitle("Logging in");
            LoadingBar.setMessage("Please wait");
            LoadingBar.setCanceledOnTouchOutside(false);
            LoadingBar.show();
            auth.signInWithEmailAndPassword(txt_email,txt_password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    LoadingBar.dismiss();
                    Toast.makeText(Login_Activity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login_Activity.this, AddLocationReview_Activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    LoadingBar.dismiss();
                    Toast.makeText(Login_Activity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    static boolean isValidEmail(String email){
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }
}