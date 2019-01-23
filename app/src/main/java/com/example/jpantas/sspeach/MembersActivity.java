package com.example.jpantas.sspeach;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jpantas.sspeach.MainActivity;
import com.example.jpantas.sspeach.R;
import com.example.jpantas.sspeach.AmigosViewHolder;
import com.facebook.login.widget.ProfilePictureView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import MAIN_CLASSES.Chat;
import MAIN_CLASSES.Friend;
import MAIN_CLASSES.Group;
import MAIN_CLASSES.User;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MembersActivity extends AppCompatActivity {
    /*
 implements Dialog.EditNameDialogListener {*/


    EditText groupnameinput;
    String groupname, mCurrent_user_id, friendname, frienduid;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef, mUsersDatabase, mRefGroups;
    private List<User> users;
    HashMap<String, Boolean> groupusers;
    int row_index = -1;
    RecyclerView mRecyclerView;
    Button createGroupBtn, backBtn;
    Group grouptosave;
    String groupkey;
    private FirebaseAuth mAuth;
    Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO limit of 15 letters
        setContentView(R.layout.activity_members);

        //get array with members numbers and key of group
        intent = getIntent();
        Bundle b = intent.getExtras();

        if (b.get("groupkey")!= null) {
            groupkey = (String) b.get("groupkey");
        }

        //TODO read groups images
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("Friends").child(mCurrent_user_id);
        mRef.keepSynced(true);
        mRefGroups = mFirebaseDatabase.getReference("Groups").child(groupkey).child("members");
        mRefGroups.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
        groupusers = new HashMap<>();

        //add current user to group
        groupusers.put(mCurrent_user_id, true);

        mRecyclerView = findViewById(R.id.amigos_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Log.d("NAMASTE", "FRIENDS");
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Boolean> options =
                new FirebaseRecyclerOptions.Builder<Boolean>()
                        .setQuery(mRefGroups, Boolean.class)
                        .build();

        //TODO make query for the users that are friends. OR search in database for friends class
        FirebaseRecyclerAdapter<Boolean, AmigosViewHolder> fra = new FirebaseRecyclerAdapter<Boolean, AmigosViewHolder>(
                options) {
            @Override
            protected void onBindViewHolder(@NonNull final AmigosViewHolder holder, final int position, @NonNull final Boolean model) {

                Log.d("NAMASTE user name", getRef(position).getKey());
                Log.d("NAMASTE model", model.toString());

                frienduid = getRef(position).getKey();

                mUsersDatabase.child(frienduid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        friendname = dataSnapshot.child("name").getValue().toString();
                        String uri = dataSnapshot.child("uri").getValue().toString();

                        holder.setUser(getApplicationContext(), friendname, uri);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public AmigosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cell_friend, parent, false);

                return new AmigosViewHolder(view);
            }
        };
        fra.startListening();
        mRecyclerView.setAdapter(fra);
    }

   /* @Override
    public void onFinishEditDialog(String inputText) {
        Toast.makeText(getApplicationContext(), "Hi, " + inputText, Toast.LENGTH_SHORT).show();


    }*/


}
