package com.example.admin.inzynierka;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class InstrukcjaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrukcja);
    }

    public void onClick(View v)
    {
        Dialog myDialog = new Dialog(InstrukcjaActivity.this);
        switch(v.getId())
        {
            case R.id.btn_pola:
                myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                myDialog.setContentView(R.layout.alert_pola);
                myDialog.getWindow();
                myDialog.show();
                break;

            case R.id.btn_przyciski:
                myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                myDialog.setContentView(R.layout.alert_przyciski);
                myDialog.getWindow();
                myDialog.show();
                break;
        }
    }
}
