package com.example.jpantas.sspeach;

        import android.content.Context;
        import android.support.v7.widget.RecyclerView;
        import android.view.View;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.TextView;

public class GroupsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    View v;
    ItemClickListener itemClickListener;
    Button seeMembers_Btn;

    public Button getSeeMembers_Btn() {
        return seeMembers_Btn;
    }

    public void setSeeMembers_Btn(Button seeMembers_Btn) {
        this.seeMembers_Btn = seeMembers_Btn;
    }

    public GroupsViewHolder(View itemView) {
        super(itemView);
        v = itemView;
        itemView.setOnClickListener(this);

        seeMembers_Btn = itemView.findViewById(R.id.membersBtn);
        seeMembers_Btn.setOnClickListener(this);
    }

    public void setItemClickListener(View.OnClickListener clickListener) {
        this.itemClickListener = itemClickListener;
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




