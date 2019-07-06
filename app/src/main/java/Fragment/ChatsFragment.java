package Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.jpantas.sspeach.ChatActivity;
import com.example.jpantas.sspeach.ChatsViewHolder;
import com.example.jpantas.sspeach.R;
import com.example.jpantas.sspeach.ViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import MAIN_CLASSES.Chat;
import MAIN_CLASSES.User;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ChatsFragment extends Fragment {

    public ChatsFragment() {
    }

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef, mRefMessages, mRefGroups;
    private HashMap<String, Boolean> chats;
    int row_index = -1;
    RecyclerView mRecyclerView;
    private String chatid,chatname;

    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    int messages_number;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //TODO limit of 15 letters
        View rootView = inflater.inflate(R.layout.fragment_chats, container, false);

        //TODO read groups images
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("Chats");
        mRefGroups = mFirebaseDatabase.getReference("Groups");
        mRefMessages = mFirebaseDatabase.getReference("Messages");

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        chats = new HashMap<>();

        mRecyclerView = rootView.findViewById(R.id.chats_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query query = mRef.orderByChild("members/"+mCurrentUserId).equalTo(true);

        FirebaseRecyclerOptions<Chat> options =
                new FirebaseRecyclerOptions.Builder<Chat>()
                        .setQuery(query, Chat.class)
                        .build();

        final FirebaseRecyclerAdapter<Chat, ChatsViewHolder> fra = new FirebaseRecyclerAdapter<Chat, ChatsViewHolder>(
                options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, final int position, @NonNull final Chat model) {

                chatid = getRef(position).getKey();

                mRefMessages.child(chatid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        messages_number = (int) dataSnapshot.getChildrenCount();
                        holder.setChat(getApplicationContext(), model.getTopic(), model.getFirst_message(), messages_number);

                        if(model.getSeen().get(mCurrentUserId).equals(false)){
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
                        final Intent intent = new Intent(getActivity(), ChatActivity.class);
                        String group_id = model.getGroupid();

                        mRefGroups.child(group_id).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                intent.putExtra("chatid", getRef(position).getKey());
                                intent.putExtra("groupname", dataSnapshot.getValue().toString());
                                startActivity(intent);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
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

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                int position = viewHolder.getAdapterPosition();
                String chatId = fra.getRef(position).getKey();

                mRef.child(chatId).child("members").child(mCurrentUserId).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(), "You successfully abandoned this chat", Toast.LENGTH_LONG).show();

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

    public static ChatsFragment newInstance() {
        return new ChatsFragment();
    }
}
