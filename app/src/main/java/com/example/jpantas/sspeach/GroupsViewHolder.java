package com.example.jpantas.sspeach;

        import android.content.Context;
        import android.support.v7.widget.RecyclerView;
        import android.view.View;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.squareup.picasso.Picasso;

public class GroupsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    View v;
    ItemClickListener itemClickListener;


    public GroupsViewHolder(View itemView) {
        super(itemView);
        v = itemView;
        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(View.OnClickListener clickListener) {
        this.itemClickListener = itemClickListener;
    }


    public void setGroup(Context c, String group, String image_url) {
        TextView groupname = v.findViewById(R.id.groupname);
        groupname.setText(group);

        ImageView seeMembersPicture = itemView.findViewById(R.id.members_profile_picture);
        Picasso.get().load(image_url).into(seeMembersPicture);

    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v,getAdapterPosition());
    }


}




