package cc.tachi.jlpt.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cc.tachi.jlpt.R;

/**
 * Created by tachi on 2017-03-06.
 *
 */

public class FirstScreen extends Fragment{
    private TextView nowpos;
    private TextView kanji;
    private TextView hiragana;
    private TextView meaning;
    private SharedPreferences preferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.firstscreen, container, false);
        nowpos= (TextView) view.findViewById(R.id.nowpos);
        kanji= (TextView) view.findViewById(R.id.kanji);
        hiragana= (TextView) view.findViewById(R.id.hiragana);
        meaning= (TextView) view.findViewById(R.id.meaning);
        preferences = getActivity().getSharedPreferences("ShowPos", getActivity().MODE_PRIVATE);
        String level= preferences.getString("level", "n5");
        String pos = String.valueOf(Integer.parseInt(preferences.getString("pos", "1"))-1);
        nowpos.setText("当前位置："+level+"单词，第"+pos+"个。");
        return view;
    }
}
