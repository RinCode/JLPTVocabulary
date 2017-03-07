package cc.tachi.jlpt.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cc.tachi.jlpt.R;

/**
 * Created by tachi on 2017-03-06.
 */

public class Setting_About extends Fragment {
    private TextView about;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_usage, container, false);
        about = (TextView) view.findViewById(R.id.description);
        about.setText("JLPT Vocabulary Test v0.2");
        return view;
    }
}
