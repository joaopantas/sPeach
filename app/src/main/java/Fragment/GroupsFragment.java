package Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.jpantas.sspeach.ChatActivity;
import com.example.jpantas.sspeach.GroupsViewHolder;
import com.example.jpantas.sspeach.R;
import com.example.jpantas.sspeach.ViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import MAIN_CLASSES.Chat;
import MAIN_CLASSES.Group;
import MAIN_CLASSES.User;

import static com.facebook.FacebookSdk.getApplicationContext;

public class GroupsFragment extends Fragment {

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    private List<Group> groups;
    int row_index = -1;
    RecyclerView mRecyclerView;
    SeekBar seekBar;
    String Groupkey, groupid, groupname;
    Button sPeach, createChatBtn;
    TextView ProgressLabel;
    int number_members, number_selected_members;
    TextInputEditText chatname;

    public GroupsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //TODO limit of 15 letters
        View rootView = inflater.inflate(R.layout.fragment_groups, container, false);

        //TODO read groups images
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("Groups");

        mRecyclerView = rootView.findViewById(R.id.groups_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        sPeach = rootView.findViewById(R.id.startspeach);
        sPeach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("NAMASTE group key", Groupkey);
                newsPeach(Groupkey);
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Group> options =
                new FirebaseRecyclerOptions.Builder<Group>()
                        .setQuery(mRef, Group.class)
                        .build();

        FirebaseRecyclerAdapter<Group, GroupsViewHolder> fra = new FirebaseRecyclerAdapter<Group, GroupsViewHolder>(
                options) {
            @Override
            protected void onBindViewHolder(@NonNull final GroupsViewHolder holder, final int position, @NonNull final Group model) {

                Log.d("checker", "entrou");
                groupid = getRef(position).getKey();

                holder.setGroup(getApplicationContext(), model.getName());

                groups = new ArrayList<>();

                //add models (that have appeared on screen) to listarray
                groups.add(model);

                //Select only one item from groups by clicking (for issue creation)
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Groupkey = getRef(position).getKey();
                        //groups.get(row_index).getEmail();
                        holder.itemView.setBackgroundColor(Color.parseColor("#F8F8FA"));
                        sPeach.setVisibility(View.VISIBLE);
                        sPeach.setEnabled(true);

                    }

                    /*@Override
                    public void onClick(View view, int position) {
                        row_index=position;
                        Log.d("checkernewrow", String.valueOf(row_index));
                        // save selected model position to Common
                        Common.currentItem = groups.get(row_index);
                    }*/

                });
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
    }

    private void newsPeach(final String key) {
        final Dialog d = new Dialog(getActivity());
        d.setTitle("New Chat");
        d.setContentView(R.layout.dialog_newchat);
        seekBar = d.findViewById(R.id.seekBar);
        createChatBtn = d.findViewById(R.id.createChatBtn);
        chatname = d.findViewById(R.id.nameTxt);

        createChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO create new chat characteristics
                //"ProgressLabel.getText()" random numbers between 0 and number_members

                final Random random = new Random();
                final Set<Integer> intSet = new HashSet<>();
                while (intSet.size() < number_selected_members) {
                    intSet.add(random.nextInt(number_members));
                }
                final int[] ints = new int[intSet.size()];
                final Iterator<Integer> iter = intSet.iterator();
                for (int i = 0; iter.hasNext(); ++i) {
                    ints[i] = iter.next();
                }

                Log.d("NAMASTE numbers 1", String.valueOf(ints[1]));
                d.dismiss();
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("list_members_num", ints);
                intent.putExtra("groupkey", key);
                intent.putExtra("chatname", chatname.getText().toString());
                startActivity(intent);

            }
        });

        mRef.child(key).child("members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                number_members = 0;
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    number_members = number_members + 1;
                }
                Log.d("NAMASTE NUMBER MEMBERS", String.valueOf(number_members));
                seekBar.setMax(number_members);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        int progress = seekBar.getProgress();
        ProgressLabel = d.findViewById(R.id.numbermembers);
        ProgressLabel.setText("Progress: " + progress);

        d.show();
    }

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            ProgressLabel.setText("Progress: " + progress);
            if (progress > 2) {
                createChatBtn.setEnabled(true);
                createChatBtn.setVisibility(View.VISIBLE);
            } else {
                createChatBtn.setEnabled(false);
                createChatBtn.setVisibility(View.INVISIBLE);
            }
            number_selected_members = progress;
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

