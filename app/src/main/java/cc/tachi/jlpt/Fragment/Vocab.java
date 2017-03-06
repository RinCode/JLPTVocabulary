package cc.tachi.jlpt.Fragment;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cc.tachi.jlpt.R;

/**
 * Created by tachi on 2017-03-06.
 *
 */

public class Vocab extends Fragment {
    private ListView vocablist;
    private ArrayList<Map<String, Object>> datalist;
    private SimpleAdapter adapter;
    private SQLiteDatabase db;
    private Bundle bundle;
    private SharedPreferences preferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vocab, container, false);
        bundle = getArguments();
        getActivity().setTitle(bundle.getString("level"));
        vocablist = (ListView) view.findViewById(R.id.vocablist);
        datalist = new ArrayList<Map<String, Object>>();
        adapter = new SimpleAdapter(getActivity(), datalist, R.layout.item, new String[]{"kanji", "hiragana", "meaning"}, new int[]{R.id.kanji, R.id.hiragana, R.id.meaning});
        vocablist.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        db = getActivity().openOrCreateDatabase(bundle.getString("level")+".db", getActivity().MODE_PRIVATE, null);
        datalist.clear();
        Cursor c = db.rawQuery("select * from jlpt", null);
        if (c != null) {
            while (c.moveToNext()) {
                final HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("kanji", c.getString(c.getColumnIndex("kanji")));
                map.put("hiragana", c.getString(c.getColumnIndex("hiragana")));
                map.put("meaning", c.getString(c.getColumnIndex("simplified_chinese")));
                datalist.add(map);
            }
            c.close();
        }
        db.close();
        if (datalist.isEmpty()) {
            final HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("kanji","无");
            map.put("hiragana", "无");
            map.put("meaning", "无");
            datalist.add(map);
        }
        adapter.notifyDataSetChanged();

        //设置已记录的滚动位置
        preferences = getActivity().getSharedPreferences("ScrollPos", getActivity().MODE_PRIVATE);
        String pos = preferences.getString(bundle.getString("level"), "0");
        vocablist.setSelection(Integer.parseInt(pos));


        vocablist.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                //设置滚动位置
                if (i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("ScrollPos", getActivity().MODE_PRIVATE).edit();
                    editor.putString(bundle.getString("level"), String.valueOf(vocablist.getFirstVisiblePosition()));
                    editor.apply();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
        super.onActivityCreated(savedInstanceState);
    }
}
