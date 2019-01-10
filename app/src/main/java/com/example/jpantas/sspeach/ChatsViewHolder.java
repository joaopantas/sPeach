package com.example.jpantas.sspeach;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    View v;
    ItemClickListener itemClickListener;

    public ChatsViewHolder(View itemView) {
        super(itemView);
        v = itemView;
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(View.OnClickListener clickListener) {
        this.itemClickListener = itemClickListener;
    }


    public void setChat(Context c, String name, int mmnr) {
        TextView chatname = v.findViewById(R.id.chatname);
        chatname.setText(name);
        TextView membersnumber = v.findViewById(R.id.membersnumber);
        membersnumber.setText(String.valueOf(mmnr));
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition());
    }


}


