package Fragment;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jpantas.sspeach.ChatActivity;
import com.example.jpantas.sspeach.GroupsViewHolder;
import com.example.jpantas.sspeach.MembersActivity;
import com.example.jpantas.sspeach.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import MAIN_CLASSES.Chat;
import MAIN_CLASSES.Group;
import MAIN_CLASSES.Message;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

public class GroupsFragment extends Fragment {

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRefGroups, mRefChats, mRefThemes, mRefMessages;
    private List<Group> groups;
    private List<String> themes_subjects;
    int row_index = -1;
    RecyclerView mRecyclerView;
    SeekBar seekBar;
    String Groupkey, groupid, groupname, mCurrent_user_id;
    Button createChatBtn;
    FloatingActionButton sPeach;
    TextView ProgressLabel;
    int number_members, number_selected_members;
    EditText chatname, theme;
    private FirebaseAuth mAuth;
    HashMap<String,Boolean> members_selected;
    Chat new_chat;
    HashMap<String, String> encrypted_ids;
    Random random;
    Set<Integer> intSet, encrypted_names_intSet;
    int counter, size, lastClicked, static_progres, chatNameSize, themeSize;
    ImageView iconFAB;
    Message new_message;

    String creator, first_message, topicTxt;
    HashMap<String, Boolean> seenMembers;

    Boolean clicked;
    public GroupsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //TODO limit of 15 letters
        View rootView = inflater.inflate(R.layout.fragment_groups, container, false);

        //TODO read groups images
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRefGroups = mFirebaseDatabase.getReference("Groups");
        mRefChats = mFirebaseDatabase.getReference("Chats");
        mRefThemes = mFirebaseDatabase.getReference("Themes");
        mRefMessages = mFirebaseDatabase.getReference("Messages");

        themes_subjects = new ArrayList<>();
        encrypted_ids = new HashMap<>();

        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mRecyclerView = rootView.findViewById(R.id.groups_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        clicked = false;
        chatNameSize = 0;
        lastClicked=-1;

        iconFAB = new ImageView(getActivity());
        iconFAB.setImageResource(R.drawable.speach2);

        sPeach = rootView.findViewById(R.id.startspeach);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d("NAMASTE entrou", "groups");

        Query myGroups = mRefGroups.orderByChild("creator").equalTo(mCurrent_user_id);

        Log.d("NAMASTE current", mCurrent_user_id);

        FirebaseRecyclerOptions<Group> options =
                new FirebaseRecyclerOptions.Builder<Group>()
                        .setQuery(myGroups, Group.class)
                        .build();

        final FirebaseRecyclerAdapter<Group, GroupsViewHolder> fra = new FirebaseRecyclerAdapter<Group, GroupsViewHolder>(
                options) {
            @Override
            protected void onBindViewHolder(@NonNull final GroupsViewHolder holder, final int position, @NonNull final Group model) {

                groupid = getRef(position).getKey();
                holder.itemView.setTag(groupid);

                holder.setGroup(getApplicationContext(), model.getName(), model.getUri());

                groups = new ArrayList<>();

                //add models (that have appeared on screen) to listarray
                groups.add(model);

                if (position == lastClicked) {
                    holder.itemView.setBackgroundColor(getResources().getColor(R.color.colorPurple));
                } else {
                    holder.itemView.setBackgroundColor(Color.WHITE);
                }

                //Select only one item from groups by clicking (for issue creation)
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    Groupkey = getRef(position).getKey();
                    //groups.get(row_index).getEmail();

                    if (clicked && position == lastClicked) {
                        sPeach.hide();
                        sPeach.setEnabled(false);
                        clicked = false;
                        lastClicked = -1;

                    }else{
                        sPeach.show();
                        sPeach.setEnabled(true);
                        clicked = true;
                        lastClicked = position;

                    }
                    notifyDataSetChanged();
                    }

                    /*@Override
                    public void onClick(View view, int position) {
                        row_index=position;
                        Log.d("checkernewrow", String.valueOf(row_index));
                        // save selected model position to Common
                        Common.currentItem = groups.get(row_index);
                    }*/

                });

                //Check members button
                /*holder.getSeeMembers_Btn().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent membersIntent = new Intent(getActivity(), MembersActivity.class);
                        membersIntent.putExtra("groupkey", getRef(position).getKey());
                        startActivityForResult(membersIntent, 1);
                    }
                });*/

            }

            @NonNull
            @Override
            public GroupsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cell_group, parent, false);

                return new GroupsViewHolder(view);
            }
        };
        fra.startListening();
        mRecyclerView.setAdapter(fra);

        sPeach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sPeach.hide();
                sPeach.setEnabled(false);
                lastClicked = -1;
                fra.notifyDataSetChanged();
                newsPeach(Groupkey);
            }
        });


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            //TODO confirm delete of group after swipe with undo warning.
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                String groupId = (String) viewHolder.itemView.getTag();
                mRefGroups.child(groupId).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(), "You successfully deleted this group", Toast.LENGTH_LONG).show();

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

    public static GroupsFragment newInstance() {
        return new GroupsFragment();
    }

    private void newsPeach(final String key) {
        final Dialog d = new Dialog(getActivity());

        d.setTitle("New Chat");
        d.setContentView(R.layout.dialog_newchat);
        seekBar = d.findViewById(R.id.seekBar);
        //TODO add textview near seekbar where it is displayed: "elements in the group: 2/27"
        createChatBtn = d.findViewById(R.id.createChatBtn);
        chatname = d.findViewById(R.id.nameTxt);
        theme = d.findViewById(R.id.themeTxt);

        chatname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                topicTxt = charSequence.toString();
                if (static_progres > 2 && chatNameSize > 5 && topicTxt.contains("#")) {
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

        theme.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                chatNameSize = charSequence.length();

                if (static_progres > 2 && chatNameSize > 5 && topicTxt.contains("#")) {
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

        //TODO add new themes and selection dropdown button
        mRefThemes.child("Animals").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    themes_subjects.add(snap.getKey().toString());
                }
                Collections.shuffle(themes_subjects);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mRefGroups.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                number_members = 0;
                groupname = dataSnapshot.child("name").getValue().toString();
                Log.d("NAMASTE GROUP NAME", groupname + " " + dataSnapshot.child("members").getChildren().toString());
                for (DataSnapshot snap : dataSnapshot.child("members").getChildren()) {
                    number_members = number_members + 1;
                }
                Log.d("NAMASTE NUMBER MEMBERS", String.valueOf(number_members));
                seekBar.setMax(number_members);
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
                intSet = new HashSet<>();

                Log.d("NAMASTE number_selected_members", String.valueOf(number_selected_members));
                while (intSet.size() < number_selected_members) {
                    int num_member = random.nextInt(number_members);
                    Log.d("NAMASTE MEMBER NUMBER 1", String.valueOf(num_member));
                    intSet.add(num_member);
                }

                final int[] ints = new int[intSet.size()];
                final Iterator<Integer> iter = intSet.iterator();
                for (int i = 0; iter.hasNext(); ++i) {
                    ints[i] = iter.next();
                }

                counter = 0;
                members_selected = new HashMap<>();
                seenMembers = new HashMap<>();
                new_chat = new Chat();

                mRefGroups.child(key).child("members").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snap : dataSnapshot.getChildren()) {

                            if(ArrayUtils.contains(ints, counter) && members_selected.size() == ints.length-1 && !members_selected.containsKey(mCurrent_user_id)) {
                                members_selected.put(mCurrent_user_id,true);
                                encrypted_ids.put(mCurrent_user_id, themes_subjects.get(counter) );
                                seenMembers.put(mCurrent_user_id,true);


                            }
                            else if (ArrayUtils.contains(ints, counter) )
                            {
                                //add keys of wanted users to array
                                members_selected.put(snap.getKey(),true);
                                seenMembers.put(snap.getKey(),false);
                                encrypted_ids.put(snap.getKey(), themes_subjects.get(counter) );
                            }
                            counter = counter + 1;
                        }

                        new_chat.setTopic(chatname.getText().toString());
                        new_chat.setMembers(members_selected);
                        new_chat.setGroupid(key);
                        new_chat.setEncryptedid(encrypted_ids);
                        new_chat.setFirst_message(theme.getText().toString());
                        new_chat.setSeen(seenMembers);

                        new_message = new Message();
                        new_message.setFrom(encrypted_ids.get(mCurrent_user_id));
                        new_message.setMessage(theme.getText().toString());
                        String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                        new_message.setTime(currentDate);


                        final String chat_key = mRefChats.push().getKey();
                        final String message_key = mRefMessages.child(chat_key).push().getKey();

                        mRefChats.child(chat_key).setValue(new_chat, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                mRefMessages.child(chat_key).child(message_key).setValue(new_message).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        d.dismiss();
                                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                                        //intent.putExtra("list_members_num", ints);
                                        intent.putExtra("groupname", groupname);
                                        intent.putExtra("chatid", chat_key);
                                        startActivity(intent);
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        int progress = seekBar.getProgress();
        ProgressLabel = d.findViewById(R.id.numbermembers);
        ProgressLabel.setText("Elements: " + progress);

        d.show();
        Window window = d.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
    }

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            ProgressLabel.setText("Elements: " + progress);

            static_progres = progress;
            if (static_progres > 2 && chatNameSize > 5 && topicTxt.contains("#")) {
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

