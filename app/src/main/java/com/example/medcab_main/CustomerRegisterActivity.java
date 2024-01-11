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

public class CustomerRegisterActivity extends AppCompatActivity {

    private Button customerLoginButton;
    private Button customerRegisterButton;
    private TextView customerRegisterLink;
    private TextView customerStatus;
    private EditText customerEmail;
    private EditText customerPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_register);

        mAuth = FirebaseAuth.getInstance();

        customerLoginButton = findViewById(R.id.customerLoginButton);
        customerRegisterButton = findViewById(R.id.customer_register_btn);
        customerRegisterLink = findViewById(R.id.register_customer_link);
        customerStatus = findViewById(R.id.customerLoginStatus);
        customerEmail = findViewById(R.id.customerLoginEmail);
        customerPassword = findViewById(R.id.customerLoginPassword);
        loadingBar = new ProgressDialog(this);

        customerRegisterButton.setVisibility(View.INVISIBLE);
        customerRegisterButton.setEnabled(false);

        customerRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customerLoginButton.setVisibility(View.INVISIBLE);
                customerRegisterLink.setVisibility(View.INVISIBLE);
                customerStatus.setText("Register Customer");

                customerRegisterButton.setVisibility(View.VISIBLE);
                customerRegisterButton.setEnabled(true);
            }
        });

        customerRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = customerEmail.getText().toString();
                String password = customerPassword.getText().toString();

                registerCustomer(email, password);
            }
        });

        customerLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = customerEmail.getText().toString();
                String password = customerPassword.getText().toString();

                signInCustomer(email, password);
            }
        });
    }

    private void signInCustomer(String email, String password) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(CustomerRegisterActivity.this, "Please enter valid email and password!", Toast.LENGTH_SHORT).show();
            return;
        }

        loadingBar.setTitle("Customer Login");
        loadingBar.setMessage("Please Wait");
        loadingBar.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loadingBar.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(CustomerRegisterActivity.this, "Customer Logged-In Successfully!", Toast.LENGTH_SHORT).show();

                            Intent customerIntent = new Intent(CustomerRegisterActivity.this, CustomerMapsActivity.class);
                            startActivity(customerIntent);
                        } else {
                            Toast.makeText(CustomerRegisterActivity.this, "Customer Login Unsuccessful, Please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void registerCustomer(String email, String password) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(CustomerRegisterActivity.this, "Please enter valid email and password!", Toast.LENGTH_SHORT).show();
            return;
        }

        loadingBar.setTitle("Customer Registration");
        loadingBar.setMessage("Please Wait");
        loadingBar.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loadingBar.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(CustomerRegisterActivity.this, "Customer Registered Successfully!", Toast.LENGTH_SHORT).show();

                            Intent customerIntent = new Intent(CustomerRegisterActivity.this, CustomerMapsActivity.class);
                            startActivity(customerIntent);
                        } else {
                            Toast.makeText(CustomerRegisterActivity.this, "Customer Registration Unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}