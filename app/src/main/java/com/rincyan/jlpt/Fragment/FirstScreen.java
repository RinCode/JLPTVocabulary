package com.rincyan.jlpt.Fragment;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.rincyan.jlpt.R;

/**
 * Created by tachi on 2017-03-06.
 */

public class FirstScreen extends Fragment {
    private ListView searchlist;
    private ArrayList<Map<String, Object>> datalist;
    private SimpleAdapter adapter;
    private TextView nowpos;
    private TextView kanji;
    private TextView hiragana;
    private TextView meaning;
    private EditText searcttext;
    private Button searchbtn;
    private SharedPreferences preferences;
    private SQLiteDatabase db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.firstscreen, container, false);
        nowpos = (TextView) view.findViewById(R.id.nowpos);
        kanji = (TextView) view.findViewById(R.id.nowposvocabkanji);
        hiragana = (TextView) view.findViewById(R.id.nowposvocabhiriagana);
        meaning = (TextView) view.findViewById(R.id.nowposvocabmeaning);
        searcttext = (EditText) view.findViewById(R.id.searchtext);
        searchbtn = (Button) view.findViewById(R.id.searchbtn);
        searchlist = (ListView) view.findViewById(R.id.searchresult);

        datalist = new ArrayList<Map<String, Object>>();
        adapter = new SimpleAdapter(getActivity(), datalist, R.layout.item, new String[]{"kanji", "hiragana", "meaning"}, new int[]{R.id.kanji, R.id.hiragana, R.id.meaning});
        searchlist.setAdapter(adapter);

        preferences = getActivity().getSharedPreferences("ShowPos", getActivity().MODE_PRIVATE);
        String level = preferences.getString("level", "n5");
        String pos = String.valueOf(Integer.parseInt(preferences.getString("pos", "1")) - 1);
        nowpos.setText("当前小组件显示位置：" + level + "单词，第" + pos + "个。");
        db = getActivity().openOrCreateDatabase(level + ".db", getActivity().MODE_PRIVATE, null);
        Cursor c = db.rawQuery("select * from jlpt where _id = " + pos, null);
        if (c != null) {
            while (c.moveToNext()) {
                kanji.setText(c.getString(c.getColumnIndex("kanji")));
                hiragana.setText(c.getString(c.getColumnIndex("hiragana")));
                meaning.setText(c.getString(c.getColumnIndex("simplified_chinese")));
            }
            c.close();
        }
        db.close();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Objects.equals(searcttext.getText().toString(), "")) {
                    datalist.clear();
                    for (int level = 2; level <= 5; level++) {
                        db = getActivity().openOrCreateDatabase("n" + String.valueOf(level) + ".db", getActivity().MODE_PRIVATE, null);
                        Cursor c = db.rawQuery("select * from jlpt where kanji like '%" + searcttext.getText().toString() + "%'", null);
                        if (c != null) {
                            while (c.moveToNext()) {
                                final HashMap<String, Object> map = new HashMap<String, Object>();
                                map.put("kanji", c.getString(c.getColumnIndex("kanji"))+"(n"+String.valueOf(level)+")");
                                map.put("hiragana", c.getString(c.getColumnIndex("hiragana")));
                                map.put("meaning", c.getString(c.getColumnIndex("simplified_chinese")));
                                datalist.add(map);
                            }
                            c.close();
                        }
                        db.close();
                        if (datalist.isEmpty()) {
                            final HashMap<String, Object> map = new HashMap<String, Object>();
                            map.put("kanji", "无");
                            map.put("hiragana", "");
                            map.put("meaning", "");
                            datalist.add(map);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

        super.onActivityCreated(savedInstanceState);
    }
}
