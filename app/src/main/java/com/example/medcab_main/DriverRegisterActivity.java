package com.example.medcab_main;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class DriverRegisterActivity extends AppCompatActivity
{

    private Button driverLoginButton;
    private Button driverRegisterButton;
    private TextView driverRegisterLink;
    private TextView driverStatus;
    private EditText driverEmail;
    private EditText driverPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_register);

        mAuth = FirebaseAuth.getInstance();

        driverLoginButton = (Button) findViewById(R.id.driverLoginButton);
        driverRegisterButton = (Button) findViewById(R.id.driver_register_btn);
        driverRegisterLink = (TextView) findViewById(R.id.register_driver_link);
        driverStatus = (TextView) findViewById(R.id.driverLoginStatus);
        driverEmail = (EditText) findViewById(R.id.driverLoginEmail);
        driverPassword = (EditText) findViewById(R.id.driverLoginPassword);
        loadingBar = new ProgressDialog(this);



        driverRegisterButton.setVisibility(View.INVISIBLE);
        driverRegisterButton.setEnabled(false);

        driverRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                driverLoginButton.setVisibility(View.INVISIBLE);
                driverRegisterLink.setVisibility(View.INVISIBLE);
                driverStatus.setText("Register Driver");

                driverRegisterButton.setVisibility(View.VISIBLE);
                driverRegisterButton.setEnabled(true);

            }
        });

        driverRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = driverEmail.getText().toString();
                String password = driverPassword.getText().toString();

                RegisterDriver(email,password);
            }
        });

        driverLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = driverEmail.getText().toString();
                String password = driverPassword.getText().toString();

                SignInDriver(email,password);
            }
        });

    }

    private void SignInDriver(String email, String password) {

        if (TextUtils.isEmpty(email)){
            Toast.makeText(DriverRegisterActivity.this, "Please Enter Your Email !", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password)){
            Toast.makeText(DriverRegisterActivity.this, "Please Enter Your Password !", Toast.LENGTH_SHORT).show();
        }

        else{

            loadingBar.setTitle("Driver Login");
            loadingBar.setMessage("Please Wait");
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(DriverRegisterActivity.this, "Driver Logged-In Successfully ! ", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent driverIntent = new Intent(DriverRegisterActivity.this,DriverMapsActivity.class);
                                startActivity(driverIntent);

                            }

                            else{
                                Toast.makeText(DriverRegisterActivity.this, "Driver Login Unsuccessful, Please try again", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }

                        }
                    });
        }

    }


    private void RegisterDriver(String email, String password) {

        if (TextUtils.isEmpty(email)){
            Toast.makeText(DriverRegisterActivity.this, "Please Enter Your Email !", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password)){
            Toast.makeText(DriverRegisterActivity.this, "Please Enter Your Password !", Toast.LENGTH_SHORT).show();
        }

        else{

            loadingBar.setTitle("Driver Registration");
            loadingBar.setMessage("Please Wait");
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(DriverRegisterActivity.this, "Driver Registered Successful ! ", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent driverIntent = new Intent(DriverRegisterActivity.this,DriverMapsActivity.class);
                                startActivity(driverIntent);

                            }

                            else{
                                Toast.makeText(DriverRegisterActivity.this, "Driver Registration Unsuccessful", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }

                        }
                    });
        }
    }
}