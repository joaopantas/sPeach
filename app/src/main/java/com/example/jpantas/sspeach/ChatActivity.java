package com.example.jpantas.sspeach;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //get array with members numbers and key of group
        intent = getIntent();
        Bundle b = intent.getExtras();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        //to be run when the chat is firstly created
        if (b.get("groupkey")!= null) {
            key = (String) b.get("groupkey");
            Log.d("NAMASTE GROUP KEY", String.valueOf(key));
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
                        if ( Arrays.asList(numbers_members).contains(counter) ){
                            //add keys of wanted users to array
                            Log.d("NAMASTE MEMBERS keys", dataSnapshot.getKey());
                            members.put(dataSnapshot.getKey(),true);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        newchat.setName(chatname);
        newchat.setMembers(members);

        mRefChats = mFirebaseDatabase.getReference("Chats");
        chatkey = mRefChats.push().getKey();
        mRefChats.child(chatkey).setValue(newchat);


        //CHAT!!!!



    }
}
