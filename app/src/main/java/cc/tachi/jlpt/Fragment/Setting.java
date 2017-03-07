package cc.tachi.jlpt.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import cc.tachi.jlpt.R;

/**
 * Created by tachi on 2017-03-06.
 */

public class Setting extends Fragment{
    private ListView setting;
    private ArrayAdapter adapter;
    private Setting_Usage setting_usage;
    private Setting_About setting_about;
    private FragmentManager fm;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting, container, false);
        setting = (ListView) view.findViewById(R.id.setting);
        adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1);
        adapter.add("用法");
        adapter.add("关于");
        setting.setAdapter(adapter);
        fm = getActivity().getSupportFragmentManager();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        setting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        setting_usage = new Setting_Usage();
                        fm.beginTransaction().addToBackStack(null).replace(R.id.id_content, setting_usage).commit();
                        break;
                    case 1:
                        setting_about = new Setting_About();
                        fm.beginTransaction().addToBackStack(null).replace(R.id.id_content, setting_about).commit();
                        break;
                    default:
                        break;
                }
            }
        });
        super.onActivityCreated(savedInstanceState);
    }
}
