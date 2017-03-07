package cc.tachi.jlpt.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
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
    private int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech myTTS;

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
    public void onActivityCreated(final Bundle savedInstanceState) {
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

        vocablist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap content = (HashMap) adapterView.getItemAtPosition(i);
                myTTS.speak(content.get("hiragana").toString(), TextToSpeech.QUEUE_FLUSH, null);
                return false;
            }
        });

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

        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                myTTS = new TextToSpeech(getActivity(),new TTSListener());
            } else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class TTSListener implements TextToSpeech.OnInitListener {

        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                int result = myTTS.setLanguage(Locale.JAPANESE);
                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(getActivity(),"不支持",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myTTS.shutdown();
    }
}
