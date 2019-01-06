package Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;

import com.example.jpantas.sspeach.ViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.jpantas.sspeach.R;

import java.util.ArrayList;
import java.util.List;

import MAIN_CLASSES.Group;
import MAIN_CLASSES.User;

import static com.facebook.FacebookSdk.getApplicationContext;

public class InviteAmigosFragment extends Fragment {

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    private List<User> users;
    int row_index = -1;
    Button inviteBtn;
    RecyclerView mRecyclerView;
    String Userkey;

    public InviteAmigosFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView =inflater.inflate(R.layout.fragment_inviteamigos, container, false);

        //TODO read groups images
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("Users");

        mRecyclerView = rootView.findViewById(R.id.inviteamigos_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        inviteBtn = rootView.findViewById(R.id.inviteBtn);

        inviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO send invite to Userkey

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

        FirebaseRecyclerAdapter<User, ViewHolder> fra = new FirebaseRecyclerAdapter<User, ViewHolder>(
                options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull User model) {

                holder.setUser(getApplicationContext(), model.getEmail());
                users = new ArrayList<>();

                //add models (that have appeared on screen) to listarray
                users.add(model);

                //TODO change text when invite/remove friend is clicked (lapit)
                //TODO send invite
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (row_index == position) {
                            inviteBtn.setEnabled(false);
                            inviteBtn.setVisibility(View.INVISIBLE);
                            //groups.get(row_index).getEmail();
                            row_index=-1;
                        }
                        else{
                            inviteBtn.setEnabled(true);
                            inviteBtn.setVisibility(View.VISIBLE);
                            Userkey = getRef(row_index).getKey();
                            row_index = position;
                        }
                    }
                });
                holder.itemView.setBackgroundColor(Color.parseColor("#F8F8FA"));
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
