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

    public TextView getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(TextView lastMessage) {
        this.lastMessage = lastMessage;
    }

    TextView lastMessage;

    public ChatsViewHolder(View itemView) {
        super(itemView);
        v = itemView;
        itemView.setOnClickListener(this);
        lastMessage = itemView.findViewById(R.id.message);
    }

    public void setItemClickListener(View.OnClickListener clickListener) {
        this.itemClickListener = itemClickListener;
    }


    public void setChat(Context c, String name, String messageTxt, int mmnr) {
        TextView chatname = v.findViewById(R.id.topic);
        chatname.setText(name);
        TextView message = v.findViewById(R.id.message);
        message.setText(messageTxt);
        TextView membersnumber = v.findViewById(R.id.membersnumber);
        membersnumber.setText(String.valueOf(mmnr));
    }


    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition());
    }


}


