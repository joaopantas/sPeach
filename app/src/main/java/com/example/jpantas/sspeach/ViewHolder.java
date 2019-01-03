package com.example.jpantas.sspeach;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    View v;
    ItemClickListener itemClickListener;

    public ViewHolder(View itemView) {
        super(itemView);
        v = itemView;
        itemView.setOnClickListener(this);
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

    public void setUser(Context c, String email){
        TextView useremail = v.findViewById(R.id.email);
        useremail.setText(email);

        //TODO present link that takes to list of participants in that group
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
