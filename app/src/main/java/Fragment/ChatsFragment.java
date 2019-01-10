package Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.jpantas.sspeach.ChatActivity;
import com.example.jpantas.sspeach.ChatsViewHolder;
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
import java.util.List;

import MAIN_CLASSES.Chat;
import MAIN_CLASSES.User;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ChatsFragment extends Fragment {

    public ChatsFragment() {
    }

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;
    private List<Chat> chats;
    int row_index = -1;
    RecyclerView mRecyclerView;
    private String chatid,chatname;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //TODO limit of 15 letters
        View rootView = inflater.inflate(R.layout.fragment_chats, container, false);

        //TODO read groups images
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("Chats");

        mRecyclerView = rootView.findViewById(R.id.chats_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Chat> options =
                new FirebaseRecyclerOptions.Builder<Chat>()
                        .setQuery(mRef, Chat.class)
                        .build();

        FirebaseRecyclerAdapter<Chat, ChatsViewHolder> fra = new FirebaseRecyclerAdapter<Chat, ChatsViewHolder>(
                options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, final int position, @NonNull final Chat model) {

                Log.d("checker", "entrou");

                //TODO add number of members in a chat
                chats = new ArrayList<>();
                chatid = getRef(position).getKey();

                mRef.child(chatid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        chatname = dataSnapshot.child("name").getValue().toString();
                        holder.setChat(getApplicationContext(), chatname,20);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                //add models (that have appeared on screen) to listarray
                chats.add(model);

                //TODO change text when invite/remove friend is clicked (lapit)
                //TODO send invite
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //open the chat that is of interest to user (lapit)
                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cell_chat, parent, false);

                return new ChatsViewHolder(view);
            }
        };
        fra.startListening();
        mRecyclerView.setAdapter(fra);

    }
}
