package Fragment;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jpantas.sspeach.ChatActivity;
import com.example.jpantas.sspeach.ChatsViewHolder;
import com.example.jpantas.sspeach.PublicChatActivity;
import com.example.jpantas.sspeach.PublicChatsViewHolder;
import com.example.jpantas.sspeach.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import MAIN_CLASSES.Chat;
import MAIN_CLASSES.Message;
import MAIN_CLASSES.PublicChat;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PublicChatsFragment extends Fragment {

    public PublicChatsFragment() {
    }

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRefChats, mRefMessages, mRefGroups,mRefFriends, mRefThemes, mRefUsers;
    private HashMap<String, Boolean> chats;
    int row_index = -1;
    RecyclerView mRecyclerView;
    String chatid, topicTxt;
    EditText chatTopic, chatTheme;
    FloatingActionButton sPeach;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    int number_selected_members, static_progres, chatThemeSize, number_members;
    SeekBar seekBar;
    Button createChatBtn;
    TextView ProgressLabel;
    Random random;
    PublicChat new_chat;
    Message new_message;
    HashMap<String, String> encrypted_ids;
    HashMap<String,String> members;
    HashMap<String, Boolean> seenMembers;
    private List<String> all_themes_subjects, chat_themes_subjects;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //TODO limit of 15 letters
        View rootView = inflater.inflate(R.layout.fragment_public_chats, container, false);

        //TODO read groups images
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRefChats = mFirebaseDatabase.getReference("PublicChats");
        mRefGroups = mFirebaseDatabase.getReference("Groups");
        mRefFriends = mFirebaseDatabase.getReference("Friends");
        mRefThemes = mFirebaseDatabase.getReference("Themes");
        mRefUsers = mFirebaseDatabase.getReference("Users");
        mRefMessages = mFirebaseDatabase.getReference("Messages");

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        chats = new HashMap<>();

        mRecyclerView = rootView.findViewById(R.id.chats_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        sPeach = rootView.findViewById(R.id.startspeach);
        sPeach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sPeach.hide();
                sPeach.setEnabled(false);
                newsPeach();
            }
        });

        all_themes_subjects = new ArrayList<String>();
        chat_themes_subjects = new ArrayList<String>();

        //download of themes' subjects and shuffle
        mRefThemes.child("Animals").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    all_themes_subjects.add(snap.getKey().toString());
                }
                Collections.shuffle(all_themes_subjects);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        //TODO!!!!!!
        Query query = mRefChats.orderByChild("members/"+mCurrentUserId).startAt("false");
        //Query query = mRefChats.child("members").orderByKey().equalTo(mCurrentUserId);

        FirebaseRecyclerOptions<PublicChat> options =
                new FirebaseRecyclerOptions.Builder<PublicChat>()
                        .setQuery(query, PublicChat.class)
                        .build();

        final FirebaseRecyclerAdapter<PublicChat, PublicChatsViewHolder> fra = new FirebaseRecyclerAdapter<PublicChat, PublicChatsViewHolder>(
                options) {
            @Override
            protected void onBindViewHolder(@NonNull final PublicChatsViewHolder holder, final int position, @NonNull final PublicChat model) {
                final String chatid = getRef(position).getKey();

                mRefUsers.child(model.getCreator()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String creator = dataSnapshot.child("name").getValue().toString();

                        Log.d("NAMASTE members", model.getMembers().toString());
                        Log.d("NAMASTE member", model.getMembers().get(mCurrentUserId) + " ");

                        if (model.getEncryptedid().size()>=model.getSize()){
                            holder.setChat(getApplicationContext(), model.getFirst_message(), creator, model.getTopic(), String.valueOf(Collections.frequency(model.getMembers().values(), "true")), String.valueOf(model.getSize()), "FULL");
                        }else if(model.getMembers().get(mCurrentUserId).equals("false")) {
                            holder.setChat(getApplicationContext(), model.getFirst_message(), creator, model.getTopic(), String.valueOf(Collections.frequency(model.getMembers().values(),"true")), String.valueOf(model.getSize()), "JOIN");
                        }else if(model.getMembers().get(mCurrentUserId).equals("true") && model.getSeen().get(mCurrentUserId).equals(true)){
                            holder.setChat(getApplicationContext(), model.getFirst_message(), creator, model.getTopic(), String.valueOf(Collections.frequency(model.getMembers().values(), "true")), String.valueOf(model.getSize()), "LEAVE");
                        }else{
                            holder.setChat(getApplicationContext(), model.getFirst_message(), creator, model.getTopic(), String.valueOf(Collections.frequency(model.getMembers().values(), "true")), String.valueOf(model.getSize()), "LEAVE");
                            holder.getLastMessage().setTypeface(null, Typeface.BOLD);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                //TODO change text when invite/remove friend is clicked (lapit)
                //TODO send invite
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //open the chat that is of interest to user (lapit)
                        final Intent intent = new Intent(getActivity(), PublicChatActivity.class);

                        if(model.getEncryptedid().size() >= model.getSize()){
                            Toast.makeText(getActivity(), "The chat is already full", Toast.LENGTH_LONG).show();
                        }
                        else if(model.getMembers().get(mCurrentUserId).equals("true")) {

                            intent.putExtra("chatid", getRef(position).getKey());
                            intent.putExtra("groupname", "public");
                            startActivity(intent);

                            //TODO change this else with unable to write message in chatactivity
                        }else{
                            Toast.makeText(getActivity(), "You need to join the chat in order to check its status", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                holder.getJoinBtn().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Intent intent = new Intent(getActivity(), PublicChatActivity.class);

                        if(model.getEncryptedid().size() >= model.getSize()){
                            Toast.makeText(getActivity(), "The chat is already full", Toast.LENGTH_LONG).show();
                        }
                        else if(model.getMembers().get(mCurrentUserId).equals("false")) {

                            mRefChats.child(chatid).child("members/"+mCurrentUserId).setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    intent.putExtra("chatid", chatid);
                                    intent.putExtra("groupname", "public");
                                    intent.putExtra("joined", "true");
                                    startActivity(intent);

                                }
                            });

                        }else{
                            mRefChats.child(chatid).child("members/"+mCurrentUserId).setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(getActivity(), "You stopped watching this chat", Toast.LENGTH_LONG).show();

                                }
                            });
                        }
                    }
                });

            }

            @NonNull
            @Override
            public PublicChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cell_public_chat, parent, false);

                return new PublicChatsViewHolder(view);
            }
        };
        fra.startListening();
        mRecyclerView.setAdapter(fra);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                int position = viewHolder.getAdapterPosition();
                final String chatId = fra.getRef(position).getKey();

                mRefChats.child(chatId).child("members").child(mCurrentUserId).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        mRefChats.child(chatId).child("encryptedid").child(mCurrentUserId).removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getActivity(), "You successfully removed this chat from your feed", Toast.LENGTH_LONG).show();
                                    }
                                });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("NAMASTE", "onFailure: " + e.getLocalizedMessage());
                        }
                    });

                fra.notifyDataSetChanged();
            }
        }).attachToRecyclerView(mRecyclerView);
    }

    private void newsPeach() {
        final Dialog d = new Dialog(getActivity());

        //d.setTitle("New Chat");
        d.setContentView(R.layout.dialog_newpublicchat);
        seekBar = d.findViewById(R.id.seekBar);
        createChatBtn = d.findViewById(R.id.createChatBtn);
        chatTopic = d.findViewById(R.id.topicTxt);
        chatTheme = d.findViewById(R.id.themeTxt);
        static_progres = -1;
        members = new HashMap<>();
        seenMembers = new HashMap<>();

        //listener for theme editing
        chatTopic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                topicTxt=charSequence.toString();
                if (static_progres > 2 && chatThemeSize > 5 && topicTxt.contains("#") ) {
                    createChatBtn.setEnabled(true);
                    createChatBtn.setVisibility(View.VISIBLE);
                } else {
                    createChatBtn.setEnabled(false);
                    createChatBtn.setVisibility(View.INVISIBLE);
                }            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //listener for theme editing
        chatTheme.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                chatThemeSize = charSequence.length();

                if (static_progres > 2 && chatThemeSize > 5 && topicTxt.contains("#") ) {
                    createChatBtn.setEnabled(true);
                    createChatBtn.setVisibility(View.VISIBLE);
                } else {
                    createChatBtn.setEnabled(false);
                    createChatBtn.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //set max seekbar
        mRefFriends.child(mCurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                number_members = (int) dataSnapshot.getChildrenCount();
                Log.d("NAMASTE NUMBER MEMBERS", String.valueOf(number_members));
                seekBar.setMax(number_members + 1);

                members.put(mCurrentUserId,"true");
                seenMembers.put(mCurrentUserId, true);
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    //add keys of friends to array
                    members.put(snap.getKey(), "false");
                    seenMembers.put(snap.getKey(), false);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        createChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO create new chat characteristics
                //"ProgressLabel.getText()" random numbers between 0 and number_members

                random = new Random();
                int memberNumber = random.nextInt(number_selected_members);

                new_chat = new PublicChat();
                encrypted_ids = new HashMap<>();
                encrypted_ids.put(mCurrentUserId, all_themes_subjects.get(memberNumber));
                new_chat.setTopic(chatTopic.getText().toString());
                new_chat.setFirst_message(chatTheme.getText().toString());
                new_chat.setMembers(members);
                new_chat.setGroupid("public");
                new_chat.setEncryptedid(encrypted_ids);
                new_chat.setCreator(mCurrentUserId);
                new_chat.setSize(number_selected_members);
                new_chat.setSeen(seenMembers);

                new_message = new Message();
                new_message.setFrom(all_themes_subjects.get(memberNumber));
                new_message.setMessage(chatTheme.getText().toString());
                String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                new_message.setTime(currentDate);

                final String chat_key = mRefChats.push().getKey();
                final String message_key = mRefMessages.child(chat_key).push().getKey();

                //set new chat
                mRefChats.child(chat_key).setValue(new_chat, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                       //set new message
                        mRefMessages.child(chat_key).child(message_key).setValue(new_message, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                d.dismiss();
                                Intent intent = new Intent(getActivity(), PublicChatActivity.class);
                                //intent.putExtra("list_members_num", ints);
                                intent.putExtra("groupname", "public");
                                intent.putExtra("chatid", chat_key);
                                startActivity(intent);
                            }
                        });

                    }
                });

            }
        });

        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        int progress = seekBar.getProgress();
        ProgressLabel = d.findViewById(R.id.numbermembers);
        ProgressLabel.setText("Seats: " + progress);

        d.show();
        Window window = d.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
    }

    //seekbar listener
    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            ProgressLabel.setText("Elements: " + progress);

            static_progres = progress;

            if (static_progres > 2 && chatThemeSize > 5 && topicTxt.contains("#") ) {
                createChatBtn.setEnabled(true);
                createChatBtn.setVisibility(View.VISIBLE);
            } else {
                createChatBtn.setEnabled(false);
                createChatBtn.setVisibility(View.INVISIBLE);
            }

            number_selected_members = static_progres;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
        }
    };


}
