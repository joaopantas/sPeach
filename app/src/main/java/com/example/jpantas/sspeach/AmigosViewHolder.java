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

public class AmigosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    View v;
    ItemClickListener itemClickListener;

    public AmigosViewHolder(View itemView) {
        super(itemView);
        v = itemView;
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(View.OnClickListener clickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setUser(Context c, String name, String image_url) {
        TextView useremail = v.findViewById(R.id.username);
        useremail.setText(name);

       /* ProfilePictureView profilePictureView = v.findViewById(R.id.friendProfilePicture);
        Log.d("NAMASTE uid",user_uid);
        profilePictureView.setProfileId(user_uid);*/
        ImageView imageView = v.findViewById(R.id.friendProfilePicture);
        Picasso.get().load(image_url).into(imageView);

        //TODO present link that takes to list of participants in that group
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition());
    }


}


