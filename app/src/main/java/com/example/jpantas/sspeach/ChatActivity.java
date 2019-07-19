package com.example.jpantas.sspeach;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessagingService;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import MAIN_CLASSES.Chat;
import MAIN_CLASSES.Group;
import MAIN_CLASSES.MessageText;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ChatActivity extends AppCompatActivity {

    Intent intent;
    String key,chatname;
    int[] numbers_members;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRefGroups,mRefChats, mRef, mRefUserChats, mRefMessages,mRefPublicChats;
    int counter;
    HashMap<String,Boolean> members;
    Chat newchat;
    Button backBtn;
    ImageButton sendBtn;
    EditText messageTxt;
    private FirebaseAuth mAuth;
    private String mCurrentUserId, mEncCurrentUserId, newEncryptedId;
    private String mChatId, mGroupName;
    private Toolbar mChatToolbar;
    TextView toolbarTextView;
    private ActionBar actionBar;
    RecyclerView mRecyclerView;
    LinearLayoutManager linearLayoutManager;
    ArrayList<String> chat_messages;
    int tamanho;
    HashMap<String, Boolean> seenMembers;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatId = getIntent().getStringExtra("chatid");
        mGroupName = getIntent().getStringExtra("groupname");

        Log.d("NAMASTE groupname", mGroupName);

        mChatToolbar = findViewById(R.id.chat_app_bar);
        setSupportActionBar(mChatToolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(mGroupName);
        actionBar.setSubtitle(mGroupName);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mRecyclerView = findViewById(R.id.messages_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mCurrentUserId = mAuth.getCurrentUser().getUid();
        chat_messages = new ArrayList<>();

        mRefMessages = mFirebaseDatabase.getReference().child("Messages").child(mChatId);

        Log.d("NAMASTE mRefMessages", mRefMessages.toString());
        //get array with members numbers and key of group

        //backBtn = findViewById(R.id.back);
        sendBtn = findViewById(R.id.chat_send_btn);
        messageTxt = findViewById(R.id.chat_message_view);

        newchat = new Chat();
        members = new HashMap<>();

        mRefChats = mFirebaseDatabase.getReference("Chats");
        mRefPublicChats = mFirebaseDatabase.getReference("PublicChats");
        mRef = mFirebaseDatabase.getReference();

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mFirebaseDatabase.getReference("Chats").child(mChatId).child("encryptedid").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mEncCurrentUserId = dataSnapshot.child(mCurrentUserId).getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mRefChats.child(mChatId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatname = dataSnapshot.child("topic").getValue().toString();
                actionBar.setTitle(chatname);
                //mTitleView = (TextView) findViewById(R.id.custom_bar_title);
                Log.d("NAMASTE NOME", chatname);
                //mTitleView.setText(chatname);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //set seen as true for current user and opened chat
        mRefChats.child(mChatId).child("seen/"+mCurrentUserId).setValue(true);

        //send messages button
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        /*backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });*/
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<MessageText> options =
                new FirebaseRecyclerOptions.Builder<MessageText>()
                        .setQuery(mRefMessages, MessageText.class)
                        .build();

        final FirebaseRecyclerAdapter<MessageText, MessagesViewHolder> fra = new FirebaseRecyclerAdapter<MessageText, MessagesViewHolder>(
                options) {
            @Override
            protected void onBindViewHolder(@NonNull final MessagesViewHolder holder, final int position, @NonNull final MessageText model) {

                Log.d("NAMASTE from", model.getFrom().toString());

                String from_user = model.getFrom().toString();

                if (from_user.equals(mEncCurrentUserId)) {
                    holder.setMessage(getApplicationContext(), from_user, model.getMessage().toString());
                }else{
                    holder.setOthersMessage(getApplicationContext(), from_user, model.getMessage().toString());
                }
            }

            @NonNull
            @Override
            public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_balloon, parent, false);

                return new MessagesViewHolder(view);
            }
        };
        fra.startListening();
        mRecyclerView.setAdapter(fra);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                //startActivity(intent);
                //return true;
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendMessage(){

        String message = messageTxt.getText().toString();
        seenMembers = new HashMap<>();

        if (!TextUtils.isEmpty(message)){

            //String current_user_ref = "Messages/" + mCurrentUserId + "/" + mChatId ;
            String chat_user_ref = "Messages/" + mChatId;

            DatabaseReference user_message_push = mRef.child("Messages").child(mChatId).push();
            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("from", mEncCurrentUserId);
            String currentDate = DateFormat.getDateTimeInstance().format(new Date());
            messageMap.put("time", currentDate);

            Map messageUserMap = new HashMap();
            //messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" +  push_id, messageMap);

            // The topic name can be optionally prefixed with "/topics/".
            String topic = mChatId;


            // See documentation on defining a message payload.
            /*Message message = android.os.Message.builder()
                    .putData("user", "850")
                    .setTopic(topic)
                    .build();

            // Send a message to the devices subscribed to the provided topic.
            String response = FirebaseMessaging.getInstance().send(message);
            // Response is a message ID string.
            Log.d("Successfully sent message: ", response);
            */


            mRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable final DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                messageTxt.getText().clear();

                mRefChats.child(mChatId).child("seen").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snap : dataSnapshot.getChildren()) {

                            if(snap.getKey().equals(mCurrentUserId)){
                                seenMembers.put(snap.getKey(), true);
                            }else{
                                seenMembers.put(snap.getKey(), false);
                            }
                        }

                        mRefChats.child(mChatId).child("seen").setValue(seenMembers);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                mRefMessages.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        tamanho = Math.toIntExact(dataSnapshot.getChildrenCount());
                        Log.d("NAMASTE size", String.valueOf(tamanho));
                        linearLayoutManager.scrollToPosition(tamanho-1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                if (databaseError != null){
                    Log.d("namaste", databaseError.getMessage().toString());
                }
                }
            });
        }
    }
}
