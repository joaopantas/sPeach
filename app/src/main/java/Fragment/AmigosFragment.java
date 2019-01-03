package Fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.jpantas.sspeach.R;
import com.example.jpantas.sspeach.ViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import MAIN_CLASSES.Chat;
import MAIN_CLASSES.Group;
import MAIN_CLASSES.User;

import static com.facebook.FacebookSdk.getApplicationContext;

public class AmigosFragment extends Fragment{

    public AmigosFragment() {
    }
    TextInputEditText groupnameinput;
    String groupname;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    private List<User> users;
    HashMap<String, Boolean> groupusers;
    int row_index = -1;
    RecyclerView mRecyclerView;
    Button createGroupBtn,saveGroupBtn;
    Group grouptosave;
    String groupkey;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //TODO limit of 15 letters
        View rootView = inflater.inflate(R.layout.fragment_amigos, container, false);

        //TODO read groups images
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("Groups");

        mRecyclerView = rootView.findViewById(R.id.inviteamigos_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        createGroupBtn = rootView.findViewById(R.id.createGroupBtn);
        createGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newGroup();
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(mRef, User.class)
                        .build();

        //TODO make query for the users that are friends. OR search in database for friends class
        FirebaseRecyclerAdapter<User, ViewHolder> fra = new FirebaseRecyclerAdapter<User, ViewHolder>(
                options) {
            @Override
            protected void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull final User model) {

                Log.d("checker", "entrou");

                //TODO add number of members in a chat
                holder.setUser(getApplicationContext(), model.getEmail());
                users = new ArrayList<>();

                //add models (that have appeared on screen) to listarray
                users.add(model);

                //TODO change text when invite/remove friend is clicked (lapit)
                //TODO send invite
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //add/remove user for the new group
                        if(model.isSelected()){
                            Log.d("NAMASTE members", "removed");
                            groupusers.remove(getRef(row_index).getKey());
                        }
                        else{
                            Log.d("NAMASTE members", "added");
                            groupusers.put(getRef(row_index).getKey(),true);
                        }
                        model.setSelected(!model.isSelected());
                        holder.itemView.setBackgroundColor(model.isSelected() ? Color.CYAN : Color.WHITE);

                    }
                });
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cell_friend, parent, false);

                return new ViewHolder(view);
            }
        };
        fra.startListening();
        mRecyclerView.setAdapter(fra);
    }

    private void newGroup() {
        final Dialog d = new Dialog(getApplicationContext());
        d.setTitle("New Group");
        d.setContentView(R.layout.dialog_newgroup);

        saveGroupBtn = d.findViewById(R.id.saveBtn);
        groupnameinput = d.findViewById(R.id.nameTxt);

        groupnameinput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                groupname = charSequence.toString();
                if (groupname!= null){
                    saveGroupBtn.setEnabled(true);
                    saveGroupBtn.setVisibility(View.VISIBLE);
                }
                else if (groupname == null){
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

                grouptosave.setMembers(groupusers);
                grouptosave.setName(groupname);

                //save group to firebase
                groupkey = mRef.push().getKey();
                mRef.child(groupkey).setValue(grouptosave);
                d.dismiss();

            }
        });

        d.show();
    }

}
