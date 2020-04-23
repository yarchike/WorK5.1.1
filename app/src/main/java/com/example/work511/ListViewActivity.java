package com.example.work511;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListViewActivity extends AppCompatActivity implements View.OnClickListener {
    static final String KEY1 = "Key1";
    static final String KEY2 = "Key2";
    static final String DATAS = "DataS";
    private List<Map<String, String>> simpleAdapterContent = new ArrayList<>();
    private ListView list;
    private SwipeRefreshLayout swipeLayout;
    private BaseAdapter listContentAdapter;
    final String LOG_TAG = "myLogs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        Button btnAdd = findViewById(R.id.buttonAdd);
        Toolbar toolbar = findViewById(R.id.toolbar);
        swipeLayout = findViewById(R.id.swiperefresh);
        list = findViewById(R.id.list);
        setSupportActionBar(toolbar);

        loadFile();
        btnAdd.setOnClickListener(this);

        listContentAdapter = createAdapter(simpleAdapterContent);
        list.setAdapter(listContentAdapter);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                simpleAdapterContent.clear();
                loadFile();
                listContentAdapter.notifyDataSetChanged();
                swipeLayout.setRefreshing(false);
            }
        });
    }

    @NonNull
    private BaseAdapter createAdapter(List<Map<String, String>> values) {
        return new MyCustomAdapter(values, getApplicationContext());
    }

    public void loadFile() {
        String result = load();

        String[] arrayContent = result.split(";");
        for (int i = 0; i < arrayContent.length; i++) {
            Map<String, String> temp = new HashMap<>();
            temp.put(KEY1, arrayContent[i].trim());
            temp.put(KEY2, String.valueOf(arrayContent[i].trim().length()));
            simpleAdapterContent.add(temp);
        }
    }

    public String load() {
        String str = "";
        String result = "";
        if (isExternalStorageWritable()) {

            File file = new File(getApplicationContext().getExternalFilesDir(
                    null), "list.txt");
            try (BufferedReader br = new BufferedReader(new FileReader(file));) {
                while ((str = br.readLine()) != null) {
                    result = result.concat(str);
                    Log.d(LOG_TAG, str);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        EditText eText = findViewById(R.id.newList);
        String input = eText.getText().toString();
        if (isExternalStorageWritable()) {
            File file = new File(getApplicationContext().getExternalFilesDir(
                    null), "list.txt");
            String temp = load();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {

                bw.write(input + ";");
                bw.close();
                FileWriter fw = new FileWriter(file, true);
                BufferedWriter bwrite = new BufferedWriter(fw);
                bwrite.write(temp);
                bwrite.close();
                Log.d(LOG_TAG, "Файл записан на SD: " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            eText.setText(null);
            simpleAdapterContent.clear();
            loadFile();
            listContentAdapter.notifyDataSetChanged();

        }
    }

}

