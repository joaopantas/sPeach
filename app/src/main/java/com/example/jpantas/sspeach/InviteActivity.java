package com.example.jpantas.sspeach;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class InviteActivity extends AppCompatActivity {

    Button nextBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_invite);
        FirebaseUser user = mAuth.getCurrentUser();


        nextBtn = findViewById(R.id.next);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InviteActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }


}
