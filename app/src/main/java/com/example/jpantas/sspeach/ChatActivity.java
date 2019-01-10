package com.example.jpantas.sspeach;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import MAIN_CLASSES.Chat;

public class ChatActivity extends AppCompatActivity {

    Intent intent;
    String key,chatname,chatkey;
    int[] numbers_members;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRefGroups,mRefChats;
    int counter;
    HashMap<String,Boolean> members;
    Chat newchat;
    Button back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //get array with members numbers and key of group
        intent = getIntent();
        Bundle b = intent.getExtras();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        back = findViewById(R.id.back);
        newchat = new Chat();
        members = new HashMap<>();

        //to be run when the chat is firstly created
        if (b.get("groupkey")!= null) {
            key = (String) b.get("groupkey");
            numbers_members = (int[]) b.get("list_members_num"); //mAuth.getCurrentUser().getUid();
            chatname = (String) b.get("chatname");

            mRefGroups = mFirebaseDatabase.getReference("Groups");

            counter = 0;
            //search the members that correspond to the randomly selected numbers
            mRefGroups.child(key).child("members").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        counter = counter + 1;
                        Log.d("NAMASTE counter", String.valueOf(counter));

                        if (ArrayUtils.contains(numbers_members, counter) ){
                            //add keys of wanted users to array
                            Log.d("NAMASTE MEMBERS keys", snap.getKey());
                            members.put(snap.getKey(),true);
                        }
                    }
                    newchat.setName(chatname);
                    newchat.setMembers(members);
                    newchat.setGroupid(key);

                    mRefChats = mFirebaseDatabase.getReference("Chats");
                    chatkey = mRefChats.push().getKey();
                    mRefChats.child(chatkey).setValue(newchat);


                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        //CHAT!!!!



    }
}
