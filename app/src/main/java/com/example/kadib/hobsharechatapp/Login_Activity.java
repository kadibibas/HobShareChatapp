package com.example.kadib.hobsharechatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class Login_Activity extends AppCompatActivity {

    private TextInputLayout mloginEmail;
    private TextInputLayout mloginPassword;
    private Button mloginBtn;
    private Toolbar mToolbar;

    private FirebaseAuth mAuth;

    //ProgressDialog
    private ProgressDialog mLoginProgress;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mloginEmail = (TextInputLayout) findViewById(R.id.login_email);
        mloginPassword = (TextInputLayout)findViewById(R.id.login_password);
        mloginBtn = (Button)findViewById(R.id.login_btn);

        mLoginProgress = new ProgressDialog(this);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mloginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = mloginEmail.getEditText().getText().toString();
                String password = mloginPassword.getEditText().getText().toString();

                if(!TextUtils.isEmpty(email)|| !TextUtils.isEmpty(password)){

                    mLoginProgress.setTitle("Logging In");
                    mLoginProgress.setMessage("Please wait while we check your credentials.");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();

                    loginUser(email,password);

                }
            }
        });
    }

    private void loginUser(String email, String password) {


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    mLoginProgress.dismiss();

                    String current_user_id = mAuth.getCurrentUser().getUid();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Intent mainIntent = new Intent(Login_Activity.this, MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();


                        }
                    });




                } else {

                    mLoginProgress.hide();

                    String task_result = task.getException().getMessage().toString();

                    Toast.makeText(Login_Activity.this, "Error : " + task_result, Toast.LENGTH_LONG).show();

                }

            }
        });


    }
}