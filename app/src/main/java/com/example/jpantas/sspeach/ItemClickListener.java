package com.example.jpantas.sspeach;

import android.view.View;

public interface ItemClickListener {
    void onClick(View view, int position);

    void send(String url);
    void iconButtonViewOnClick(View view, int position);

}
