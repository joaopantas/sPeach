package com.example.jpantas.sspeach;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.squareup.picasso.Picasso;


public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    View v;
    ItemClickListener itemClickListener;
    Button invite_Btn;
    Button decline_Btn;

    public Button getInvite_Btn() {
        return invite_Btn;
    }

    public void setInvite_Btn(Button invite_Btn) {
        this.invite_Btn = invite_Btn;
    }

    public Button getDecline_Btn() {
        return decline_Btn;
    }

    public void setDecline_Btn(Button decline_Btn) {
        this.decline_Btn = decline_Btn;
    }

    public ViewHolder(View itemView) {
        super(itemView);
        v = itemView;
        itemView.setOnClickListener(this);

        invite_Btn = itemView.findViewById(R.id.inviteBtn);
        invite_Btn.setOnClickListener(this);

        decline_Btn = itemView.findViewById(R.id.declineBtn);
        decline_Btn.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }


    public void setChat(Context c, String name, int mmnr) {
        TextView chatname = v.findViewById(R.id.chatname);
        chatname.setText(name);
        TextView membersnumber = v.findViewById(R.id.membersnumber);
        membersnumber.setText(mmnr);
    }

    public void setUser(Context c, String name, String image_url){
        TextView useremail = v.findViewById(R.id.email);
        useremail.setText(name);

        /*ProfilePictureView profilePictureView = v.findViewById(R.id.userProfilePicture);
        Log.d("NAMASTE uid",user_uid);
        profilePictureView.setProfileId(user_uid);*/
        ImageView imageView = v.findViewById(R.id.userProfilePicture);
        Picasso.get().load(image_url).into(imageView);

        }

    public void setGroup(Context c, String group) {
        TextView groupname = v.findViewById(R.id.groupname);
        groupname.setText(group);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition());

    }

}
