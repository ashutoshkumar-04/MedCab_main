package com.example.medcab_main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity
{
    private Button WelcomeCustomerBtn;
    private Button WelcomeDriverBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        WelcomeCustomerBtn = (Button) findViewById(R.id.customerLogin_btn);
        WelcomeDriverBtn =   (Button) findViewById(R.id.driverLogin_btn);

        WelcomeCustomerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent LoginRegCustomerIntent = new Intent(WelcomeActivity.this, CustomerRegisterActivity.class);
                startActivity(LoginRegCustomerIntent);
            }
        });

        WelcomeDriverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LoginRegDriverIntent = new Intent(WelcomeActivity.this, DriverRegisterActivity.class);
                startActivity(LoginRegDriverIntent);
            }
        });
    }
}