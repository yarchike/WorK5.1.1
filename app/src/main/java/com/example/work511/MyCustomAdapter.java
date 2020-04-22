package com.example.work511;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import static com.example.work511.ListViewActivity.KEY1;
import static com.example.work511.ListViewActivity.KEY2;


public class MyCustomAdapter extends BaseAdapter implements ListAdapter {

    private List<Map<String, String>> mValues;
    private Context mContext;

    public MyCustomAdapter(List<Map<String, String>> values, Context applicationContext) {
        mValues = values;
        mContext = applicationContext;
    }

    @Override
    public int getCount() {
        return mValues.size();
    }

    @Override
    public Object getItem(int i) {
        return mValues.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_layout, null);
        }

        Map<String, String> map = mValues.get(i);
        TextView title = view.findViewById(R.id.TextOne);
        TextView subtitle = view.findViewById(R.id.TextTwo);
        title.setText(map.get(KEY1));
        subtitle.setText(map.get(KEY2));

        Button btn = view.findViewById(R.id.buttonDel);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mValues.remove(i);
                notifyDataSetChanged();
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                TextView text = v.findViewById(R.id.TextTwo);
                Toast.makeText(mContext, text.getText().toString(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        return view;
    }
}
