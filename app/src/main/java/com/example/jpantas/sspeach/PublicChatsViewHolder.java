package com.example.jpantas.sspeach;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PublicChatsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    View v;
    ItemClickListener itemClickListener;

    public Button getJoinBtn() {
        return joinBtn;
    }

    public void setJoinBtn(Button joinBtn) {
        this.joinBtn = joinBtn;
    }

    Button joinBtn;

    public PublicChatsViewHolder(View itemView) {
        super(itemView);
        v = itemView;
        itemView.setOnClickListener(this);

        joinBtn = itemView.findViewById(R.id.joinBtn);
        joinBtn.setOnClickListener(this);
    }

    public void setItemClickListener(View.OnClickListener clickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setChat(Context c, String message, String creator, String topic, String activeMembers, String watchMembers, String btnTxt ) {
        TextView messageTxt = v.findViewById(R.id.message);
        messageTxt.setText(message);
        TextView creatorTxt = v.findViewById(R.id.creator);
        creatorTxt.setText(creator);
        TextView topicTxt = v.findViewById(R.id.topic);
        topicTxt.setText(topic);
        TextView activeMembersTxt = v.findViewById(R.id.active_members);
        activeMembersTxt.setText(activeMembers);
        TextView watchMembersTxt = v.findViewById(R.id.watch_members);
        watchMembersTxt.setText(watchMembers);

        joinBtn.setText(btnTxt);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition());
    }
}