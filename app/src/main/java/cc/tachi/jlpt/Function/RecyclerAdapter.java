package cc.tachi.jlpt.Function;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

import cc.tachi.jlpt.R;

/**
 *
 */
public class RecyclerAdapter extends RecyclerView.Adapter {


    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Map<String, Object>> lists = new ArrayList<>();

    public RecyclerAdapter(Context context, ArrayList<Map<String, Object>> lists) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.lists = lists;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new MyViewHolder(inflater.inflate(R.layout.item, null, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder.kanji.setText(lists.get(position).get("kanji").toString());
        viewHolder.hiragana.setText(lists.get(position).get("hiragana").toString());
        viewHolder.meaning.setText(lists.get(position).get("meaning").toString());
        viewHolder.layout.scrollTo(0, 0);
    }

    @Override
    public int getItemCount() {
        if (lists != null) {
            return lists.size();
        } else {
            return 0;
        }
    }

    public void removeRecycle(int position) {
        lists.remove(position);
        notifyDataSetChanged();
        if (lists.size() == 0) {
            Toast.makeText(context, "已经没数据啦", Toast.LENGTH_SHORT).show();
        }
    }

}
