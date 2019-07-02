package com.example.jpantas.sspeach;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MessagesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    View v;
    ItemClickListener itemClickListener;



    public MessagesViewHolder(View itemView) {
        super(itemView);
        v = itemView;
    }



    public void setMessage(Context c, String username, String text) {

        RelativeLayout relativeLayout = v.findViewById(R.id.balloon);
        TextView user_name = v.findViewById(R.id.username);
         /*RelativeLayout.LayoutParams params_1 =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        params_1.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
        params_1.addRule(RelativeLayout.ALIGN_PARENT_START, 0);

        user_name.setLayoutParams(params_1);
        user_name.setGravity(Gravity.CENTER);
        user_name.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        user_name.setText(username);

        RelativeLayout.LayoutParams params_2 =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        params_2.addRule(RelativeLayout.END_OF, 0);
        params_2.addRule(RelativeLayout.START_OF, R.id.username);
        message_text.setLayoutParams(params_2);*/

         relativeLayout.setBackgroundResource(0);

        RelativeLayout.LayoutParams params_2 =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        params_2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        TextView message_text = v.findViewById(R.id.message_text);
        message_text.setLayoutParams(params_2);
        message_text.setText(text);
        message_text.setBackgroundResource(R.drawable.black_background_border);

    }

    public void setOthersMessage(Context c, String username, String text) {

        TextView user_name = v.findViewById(R.id.username);
        user_name.setText(username);

        TextView message_text = v.findViewById(R.id.message_text);
        message_text.setText(text);

    }


    @Override
    public void onClick(View view) {

    }
}




