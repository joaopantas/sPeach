package Fragment;

import android.app.ActionBar;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

public class AmigosFragment extends Fragment {
    /*
 implements Dialog.EditNameDialogListener {*/

    public AmigosFragment() {
    }

    EditText groupnameinput;
    String groupname, mCurrent_user_id, friendname, frienduid;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef, mUsersDatabase, mRefGroups;
    private List<User> users;
    HashMap<String, Boolean> groupusers;
    int row_index = -1;
    RecyclerView mRecyclerView;
    Button createGroupBtn, saveGroupBtn;
    Group grouptosave;
    String groupkey;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //TODO limit of 15 letters
        View rootView = inflater.inflate(R.layout.fragment_amigos, container, false);

        //TODO read groups images
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("Friends").child(mCurrent_user_id);
        mRef.keepSynced(true);
        mRefGroups = mFirebaseDatabase.getReference("Groups");
        mRefGroups.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
        groupusers = new HashMap<>();

        //add current user to group
        groupusers.put(mCurrent_user_id, true);

        mRecyclerView = rootView.findViewById(R.id.amigos_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        createGroupBtn = rootView.findViewById(R.id.createGroupBtn);
        createGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newGroup();
            }
        });

        Log.d("NAMASTE", "FRIENDS");
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Friend> options =
                new FirebaseRecyclerOptions.Builder<Friend>()
                        .setQuery(mRef, Friend.class)
                        .build();

        //TODO make query for the users that are friends. OR search in database for friends class
        FirebaseRecyclerAdapter<Friend, AmigosViewHolder> fra = new FirebaseRecyclerAdapter<Friend, AmigosViewHolder>(
                options) {
            @Override
            protected void onBindViewHolder(@NonNull final AmigosViewHolder holder, final int position, @NonNull final Friend model) {

                Log.d("NAMASTE user name", getRef(position).getKey());
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

                //TODO change text when invite/remove friend is clicked (lapit)
                //TODO send invite
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.d("NAMASTE", "selected");
                        //add/remove user for the new group
                        if (model.isSelected()) {
                            Log.d("NAMASTE members", "removed");
                            Log.d("NAMASTE members", getRef(position).getKey());

                            groupusers.remove(getRef(position).getKey());
                        } else {
                            Log.d("NAMASTE members", "added");
                            Log.d("NAMASTE members", getRef(position).getKey());

                            groupusers.put(getRef(position).getKey(), true);
                        }
                        model.setSelected(!model.isSelected());
                        holder.itemView.setBackgroundColor(model.isSelected() ? Color.CYAN : Color.WHITE);

                        //TODO change to more than 2 rather than more than 0
                        // groupuser with more than 2 elements enables the start of a group
                        if (groupusers.size() > 2) {
                            createGroupBtn.setEnabled(true);
                            createGroupBtn.setVisibility(View.VISIBLE);
                        } else if (groupusers.size() == 1) {
                            Toast.makeText(getActivity(), "You need to select one more element", Toast.LENGTH_LONG).show();
                            createGroupBtn.setEnabled(false);
                            createGroupBtn.setVisibility(View.INVISIBLE);
                        } else {
                            Toast.makeText(getActivity(), "You need to select two or more friends to start a group", Toast.LENGTH_LONG).show();
                            createGroupBtn.setEnabled(false);
                            createGroupBtn.setVisibility(View.INVISIBLE);
                        }
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

    private void newGroup() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setTitle("New Group");
        dialog.setContentView(R.layout.dialog_newgroup);
        groupnameinput = dialog.findViewById(R.id.groupname);
        saveGroupBtn = dialog.findViewById(R.id.saveBtn);

        groupnameinput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                groupname = charSequence.toString();
                if (groupname != null) {
                    saveGroupBtn.setEnabled(true);
                    saveGroupBtn.setVisibility(View.VISIBLE);
                } else if (groupname == null) {
                    saveGroupBtn.setEnabled(false);
                    saveGroupBtn.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        saveGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                grouptosave = new Group();
                grouptosave.setMembers(groupusers);
                grouptosave.setName(groupname);
                grouptosave.setCreator(mCurrent_user_id);

                //save group to firebase
                groupkey = mRef.push().getKey();
                mRefGroups.child(groupkey).setValue(grouptosave);
                dialog.dismiss();

            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
    }

   /* @Override
    public void onFinishEditDialog(String inputText) {
        Toast.makeText(getApplicationContext(), "Hi, " + inputText, Toast.LENGTH_SHORT).show();


    }*/


}
