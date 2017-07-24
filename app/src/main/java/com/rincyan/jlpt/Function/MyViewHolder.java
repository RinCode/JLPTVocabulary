package com.rincyan.jlpt.Function;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rincyan.jlpt.R;

/**
 * Created by tachi on 2017-03-21.
 */

public class MyViewHolder extends RecyclerView.ViewHolder {


    public TextView kanji;
    public TextView hiragana;
    public TextView meaning;
    public LinearLayout layout;

    public MyViewHolder(View itemView) {
        super(itemView);
        kanji = (TextView) itemView.findViewById(R.id.kanji);
        hiragana = (TextView) itemView.findViewById(R.id.hiragana);
        meaning = (TextView) itemView.findViewById(R.id.meaning);
        layout = (LinearLayout) itemView.findViewById(R.id.item_recycler_ll);

    }
}
