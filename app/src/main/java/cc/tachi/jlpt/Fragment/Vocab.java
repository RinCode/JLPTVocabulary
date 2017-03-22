package cc.tachi.jlpt.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cc.tachi.jlpt.Function.MyRecyclerView;
import cc.tachi.jlpt.Function.RecyclerAdapter;
import cc.tachi.jlpt.Function.OnRecyclerItemClickListener;
import cc.tachi.jlpt.R;

/**
 * Created by tachi on 2017-03-06.
 */

public class Vocab extends Fragment {
    private MyRecyclerView vocablist;
    private ArrayList<Map<String, Object>> datalist;
    private RecyclerAdapter adapter;
    private SQLiteDatabase db;
    private Bundle bundle;
    private SharedPreferences preferences;
    private int MY_DATA_CHECK_CODE = 0;
    private TextToSpeech myTTS;
    private LinearLayoutManager manager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.vocab, container, false);
        bundle = getArguments();
        getActivity().setTitle(bundle.getString("level"));
        vocablist = (MyRecyclerView) view.findViewById(R.id.vocablist);
        datalist = new ArrayList<Map<String, Object>>();
        adapter = new RecyclerAdapter(getActivity(), datalist);
        manager = new LinearLayoutManager(getActivity());
        vocablist.setLayoutManager(manager);
        vocablist.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        db = getActivity().openOrCreateDatabase(bundle.getString("level") + ".db", getActivity().MODE_PRIVATE, null);
        datalist.clear();
        Cursor c = db.rawQuery("select * from jlpt", null);
        if (c != null) {
            while (c.moveToNext()) {
                if (c.getInt(c.getColumnIndex("checked")) ==0) {
                    final HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("kanji", c.getString(c.getColumnIndex("kanji")));
                    map.put("hiragana", c.getString(c.getColumnIndex("hiragana")));
                    map.put("meaning", c.getString(c.getColumnIndex("simplified_chinese")));
                    datalist.add(map);
                }
            }
            c.close();
        }
        db.close();
        if (datalist.isEmpty()) {
            final HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("kanji", "无");
            map.put("hiragana", "无");
            map.put("meaning", "无");
            datalist.add(map);
        }
        adapter.notifyDataSetChanged();

        //设置已记录的滚动位置
        preferences = getActivity().getSharedPreferences("ScrollPos", getActivity().MODE_PRIVATE);
        String posraw = preferences.getString(bundle.getString("level"), "0|0");
        String [] temp = null;
        temp = posraw.split("\\|");
        manager.scrollToPositionWithOffset(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));

        vocablist.addOnItemTouchListener(new OnRecyclerItemClickListener(vocablist) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder viewHolder) {

            }

            @Override
            public void onItemLOngClick(RecyclerView.ViewHolder viewHolder) {
                try {
                    myTTS.speak(datalist.get(viewHolder.getPosition()).get("kanji").toString(), TextToSpeech.QUEUE_FLUSH, null);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "TTS不可用", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        vocablist.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                View topView = manager.getChildAt(0);
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("ScrollPos", getActivity().MODE_PRIVATE).edit();
                editor.putString(bundle.getString("level"), String.valueOf(manager.getPosition(topView)) + "|" + String.valueOf(topView.getTop()));
                editor.apply();
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        try {
            Intent checkTTSIntent = new Intent();
            checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == MY_DATA_CHECK_CODE) {
                if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                    myTTS = new TextToSpeech(getActivity(), new TTSListener());
                } else {
                    Intent installTTSIntent = new Intent();
                    installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(installTTSIntent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                    Toast.makeText(getActivity(), "TTS不可用", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (myTTS != null) {
            myTTS.shutdown();
        }
    }
}
