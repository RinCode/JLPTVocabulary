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

public class Setting_Usage extends Fragment {
    private TextView description;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_usage, container, false);
        description = (TextView) view.findViewById(R.id.description);
        description.setText("\t本软件用于JLPT单词的学习，主要功能为桌面小组件。\n" +
                "使用方法：\n" +
                "1.长按桌面空白处，点击小组件\n" +
                "2.拖拽本软件小组件到空白区域\n" +
                "3.每次解锁屏幕后小组件单词即可自动更新\n" +
                "\n" +
                "注意：可在单词列表滑动到期望位置后点击右上角菜单“展示到小组件”设置新的开始显示的位置。");
        return view;
    }
}