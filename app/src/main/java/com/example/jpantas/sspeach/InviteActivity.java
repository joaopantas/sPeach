package com.example.jpantas.sspeach;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import MAIN_CLASSES.User;

import static com.facebook.FacebookSdk.getApplicationContext;

public class InviteActivity extends AppCompatActivity {

    Button nextBtn;
    private FirebaseAuth mAuth;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    RecyclerView mRecyclerView;
    private List<User> users;
    String Userkey, mCurrent_state, user_id;
    DatabaseReference mFriendReq, mFriend, mRootRef;
    FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCurrent_state = "not_friends";
        setContentView(R.layout.activity_invite);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        nextBtn = findViewById(R.id.next);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InviteActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //TODO read groups images
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("Users");
        mFriendReq = mFirebaseDatabase.getReference("Friend_req");
        mFriend = mFirebaseDatabase.getReference("Friends");
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mRecyclerView = findViewById(R.id.card_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(mRef, User.class)
                        .build();

        FirebaseRecyclerAdapter<User, ViewHolder> fra = new FirebaseRecyclerAdapter<User, ViewHolder>(
                options) {
            @Override
            protected void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull User model) {

                //TODO visualise every user but himself
                Log.d("NAMASTE NAME", model.getName());
                holder.setUser(getApplicationContext(), model.getName());
                users = new ArrayList<>();

                //add models (that have appeared on screen) to listarray
                users.add(model);

                user_id = getRef(position).getKey();
                Log.d("NAMASTE User key", user_id);
                Log.d("NAMASTE currentuser key", mCurrentUser.getUid());
                users = new ArrayList<>();

                //visualise every user but himself
                if (mCurrentUser.getUid().equals(user_id)) {
                    holder.itemView.setVisibility(View.GONE);
                } else {
                    Log.d("NAMASTE NAME", model.getName());
                    holder.setUser(getApplicationContext(), model.getName());
                    Userkey = getRef(position).getKey();
                    //add models (that have appeared on screen) to listarray
                    users.add(model);
                }

                //update buttons content
                mFriendReq.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(getRef(position).getKey())) {

                            String req_type = dataSnapshot.child(getRef(position).getKey()).child("request_type").getValue().toString();

                            if (req_type.equals("sent")) {

                                mCurrent_state = "req_sent";
                                holder.getInvite_Btn().setText("CANCEL INVITE");

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                //actions
                holder.getInvite_Btn().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //enabled while data changes occur
                        holder.getInvite_Btn().setEnabled(false);
                        nextBtn.setText("CONTINUE");

                        //if not_friends
                        if (mCurrent_state.equals("not_friends")) {
                            Log.d("NAMASTE Userkey invited", getRef(position).getKey());

                            //notifications
                            DatabaseReference newNotificationref = mRootRef.child("notifications").child(getRef(position).getKey()).push();
                            String newNotificationId = newNotificationref.getKey();

                            HashMap<String, String> notificationData = new HashMap<>();
                            notificationData.put("from", mCurrentUser.getUid());
                            notificationData.put("type", "request");

                            //change for friends request DB and notifications DB
                            Map requestMap = new HashMap();
                            requestMap.put("Friend_req/" + mCurrentUser.getUid() + "/" + getRef(position).getKey() + "/request_type", "sent");
                            requestMap.put("Friend_req/" + getRef(position).getKey() + "/" + mCurrentUser.getUid() + "/request_type", "received");
                            requestMap.put("notifications/" + getRef(position).getKey() + "/" + newNotificationId, notificationData);

                            mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                    if (databaseError != null) {

                                        Toast.makeText(InviteActivity.this, "There was some error in sending request", Toast.LENGTH_SHORT).show();

                                    } else {

                                        mCurrent_state = "req_sent";
                                        holder.getInvite_Btn().setText("CANCEL INVITE");

                                    }
                                    holder.getInvite_Btn().setEnabled(true);
                                }
                            });
                        }

                        //if invite had already been sent
                        if (mCurrent_state.equals("req_sent")) {
                            mFriendReq.child(mCurrentUser.getUid()).child(getRef(position).getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mFriendReq.child(getRef(position).getKey()).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            holder.getInvite_Btn().setEnabled(true);
                                            mCurrent_state = "not_friends";
                                            holder.getInvite_Btn().setText("INVITE");

                                        }
                                    });

                                }
                            });

                        }
                    }
                });


            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cell_user, parent, false);

                return new ViewHolder(view);
            }
        };
        fra.startListening();
        mRecyclerView.setAdapter(fra);

    }

}
