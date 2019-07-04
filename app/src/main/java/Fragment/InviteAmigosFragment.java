package Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.Toast;

import com.example.jpantas.sspeach.ViewHolder;
import com.facebook.login.widget.ProfilePictureView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.jpantas.sspeach.R;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import MAIN_CLASSES.Group;
import MAIN_CLASSES.User;

import static com.facebook.FacebookSdk.getApplicationContext;

public class InviteAmigosFragment extends Fragment {

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef, mFriendReq, mFriend, mRootRef;
    FirebaseUser mCurrentUser;

    private List<User> users;
    private List<String> states;
    int row_index = -1;
    RecyclerView mRecyclerView;
    String Userkey, mCurrent_state, user_id;

    Button invite_Btn;
    Button decline_Btn;

    public InviteAmigosFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_inviteamigos, container, false);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        states = new ArrayList<>();

        //TODO read groups images
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mRef = mFirebaseDatabase.getReference("Users");
        mFriendReq = mFirebaseDatabase.getReference("Friend_req");
        mFriend = mFirebaseDatabase.getReference("Friends");

        mRecyclerView = rootView.findViewById(R.id.inviteamigos_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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
            protected void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull final User model) {

                user_id = getRef(position).getKey();
                Log.d("NAMASTE User key", user_id);
                //Log.d("NAMASTE currentuser key", mCurrentUser.getUid());
                users = new ArrayList<>();

                //visualise every user but himself
                if (mCurrentUser.getUid().equals(user_id)) {
                    holder.itemView.setVisibility(View.GONE);
                    states.add("temp");
                } else {
                    Log.d("NAMASTE NAME", model.getName());

                    holder.setUser(getApplicationContext(), model.getName(), model.getUri());

                    Userkey = getRef(position).getKey();
                    //add models (that have appeared on screen) to listarray
                    users.add(model);
                    states.add("temp");
                }
                //initialize arraylist with same size as list of users
                Log.d("NAMASTE states",states.toString());

                //update buttons content
                mFriendReq.child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Log.d("NAMASTE entrou", "friendreq SIII");
                        if (!mCurrentUser.getUid().equals(getRef(position).getKey())) {
                            mCurrent_state = "not_friends";
                            Log.d("NAMASTE username", getRef(position).getKey());
                            if (dataSnapshot.hasChild(getRef(position).getKey())) {

                                String req_type = dataSnapshot.child(getRef(position).getKey()).child("request_type").getValue().toString();

                                if (req_type.equals("received")) {

                                    //TODO save mcurrent states to array and retrieve it inside onclicklistener
                                    mCurrent_state = "req_received";
                                    holder.getInvite_Btn().setText("ACCEPT FRIEND");
                                    holder.getDecline_Btn().setEnabled(true);
                                    holder.getDecline_Btn().setVisibility(View.VISIBLE);
                                    holder.getDecline_Btn().setText("DECLINE FRIEND");
                                    Log.d("NAMASTE", "ENTROU 1");

                                } else if (req_type.equals("sent")) {

                                    mCurrent_state = "req_sent";
                                    holder.getInvite_Btn().setText("CANCEL INVITE");
                                    holder.getDecline_Btn().setEnabled(false);
                                    holder.getDecline_Btn().setVisibility(View.INVISIBLE);
                                    Log.d("NAMASTE", "ENTROU 2");

                                }


                            } else {

                                mFriend.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.hasChild(getRef(position).getKey())) {

                                            mCurrent_state = "friends";
                                            holder.getInvite_Btn().setText("UNFRIEND");
                                            holder.getDecline_Btn().setEnabled(false);
                                            holder.getDecline_Btn().setVisibility(View.INVISIBLE);
                                            Log.d("NAMASTE", "ENTROU 3");

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                            Log.d("NAMASTE position", String.valueOf(position));
                            //TODO avoid arraylist that adds infinite numbers intead of replacing numbers
                            states.set(position,mCurrent_state);
                            Log.d("NAMASTE states",states.toString());
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

                        Log.d("NAMASTE states position", states.toString());
                        Log.d("NAMASTE model name",model.getName().toString());
                        //if not_friends
                        if (states.get(position).equals("not_friends")) {
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
                            Log.d("NAMASTE", "ENTROU 4");

                            mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                    if (databaseError != null) {

                                        Toast.makeText(getActivity(), "There was some error in sending request", Toast.LENGTH_SHORT).show();

                                    } else {

                                        mCurrent_state = "req_sent";
                                        holder.getInvite_Btn().setText("CANCEL INVITE");

                                    }
                                    holder.getInvite_Btn().setEnabled(true);
                                }
                            });
                        }

                        //if invite had already been sent
                        if (states.get(position).equals("req_sent")) {
                            mFriendReq.child(mCurrentUser.getUid()).child(getRef(position).getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mFriendReq.child(getRef(position).getKey()).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            holder.getInvite_Btn().setEnabled(true);
                                            mCurrent_state = "not_friends";
                                            holder.getInvite_Btn().setText("INVITE");

                                            holder.getDecline_Btn().setVisibility(View.INVISIBLE);
                                            holder.getDecline_Btn().setEnabled(false);

                                        }
                                    });

                                    Log.d("NAMASTE", "ENTROU 5");

                                }
                            });

                        }

                        if (states.get(position).equals("req_received")) {
                            final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                            Map friendsMap = new HashMap();
                            friendsMap.put("Friends/" + mCurrentUser.getUid() + "/" + getRef(position).getKey() + "/date", currentDate);
                            friendsMap.put("Friends/" + getRef(position).getKey() + "/" + mCurrentUser.getUid() + "/date", currentDate);


                            friendsMap.put("Friend_req/" + mCurrentUser.getUid() + "/" + getRef(position).getKey(), null);
                            friendsMap.put("Friend_req/" + getRef(position).getKey() + "/" + mCurrentUser.getUid(), null);

                            Log.d("NAMASTE", "ENTROU 6");

                            mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                    if (databaseError == null) {

                                        holder.getInvite_Btn().setEnabled(true);
                                        mCurrent_state = "friends";
                                        holder.getInvite_Btn().setText("UNFRIEND");

                                        holder.getDecline_Btn().setVisibility(View.INVISIBLE);
                                        holder.getDecline_Btn().setEnabled(false);

                                    } else {

                                        String error = databaseError.getMessage();

                                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                        }

                        if (states.get(position).equals("friends")) {

                            Map unfriendMap = new HashMap();
                            unfriendMap.put("Friends/" + mCurrentUser.getUid() + "/" + getRef(position).getKey(), null);
                            unfriendMap.put("Friends/" + getRef(position).getKey() + "/" + mCurrentUser.getUid(), null);
                            Log.d("NAMASTE", "ENTROU 7");

                            mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                                    if (databaseError == null) {

                                        mCurrent_state = "not_friends";
                                        holder.getInvite_Btn().setText("INVITE");

                                        holder.getDecline_Btn().setVisibility(View.INVISIBLE);
                                        holder.getDecline_Btn().setEnabled(false);

                                    } else {

                                        String error = databaseError.getMessage();

                                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();

                                    }

                                    holder.getInvite_Btn().setEnabled(true);

                                }
                            });

                        }
                    }
                });

                holder.getDecline_Btn().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("NAMASTE", "ENTROU 8");

                        mFriendReq.child(mCurrentUser.getUid()).child(getRef(position).getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                mFriendReq.child(getRef(position).getKey()).child(mCurrentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        holder.getInvite_Btn().setEnabled(true);
                                        mCurrent_state = "not_friends";
                                        holder.getInvite_Btn().setText("INVITE");

                                        holder.getDecline_Btn().setVisibility(View.INVISIBLE);
                                        holder.getDecline_Btn().setEnabled(false);

                                    }
                                });
                            }
                        });

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

    public static InviteAmigosFragment newInstance() {
        return new InviteAmigosFragment();
    }

}
