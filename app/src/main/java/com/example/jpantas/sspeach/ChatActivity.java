package com.example.jpantas.sspeach;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

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

    private String mChatId;
    private Toolbar mChatToolbar;
    private TextView mTitleView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatToolbar = (Toolbar)findViewById(R.id.chat_app_bar);
        setSupportActionBar(mChatToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        mChatId = getIntent().getStringExtra("chatname");




        //get array with members numbers and key of group
        intent = getIntent();
        Bundle b = intent.getExtras();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        back = findViewById(R.id.back);
        newchat = new Chat();
        members = new HashMap<>();

        DatabaseReference mRef;

        mRef = mFirebaseDatabase.getReference("Chats");

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);

        actionBar.setCustomView(action_bar_view);

        if (mChatId!=null){
            mRef.child(mChatId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    chatname = dataSnapshot.child("name").getValue().toString();
                    //getSupportActionBar().setTitle(chatname);
                    mTitleView = (TextView) findViewById(R.id.custom_bar_title);
                    System.out.println("nome: " + chatname);
                    mTitleView.setText(chatname);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        //------------ Custom Action Bar Items -----------

        //mTitleView = (TextView) findViewById(R.id.custom_bar_title);
        //System.out.println("nome: " + chatname);
        //mTitleView.setText(chatname);











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
