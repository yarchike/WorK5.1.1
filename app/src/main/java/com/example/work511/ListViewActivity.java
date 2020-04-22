package com.example.work511;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListViewActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int REQUSET_CODE_PERMISSION_WRITE_STORAGE = 10;
    static final String KEY1 = "Key1";
    static final String KEY2 = "Key2";
    static final String DATAS = "DataS";
    private List<Map<String, String>> simpleAdapterContent = new ArrayList<>();
    private ListView list;
    private SharedPreferences sharedPref;
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


        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            loadFile();
            btnAdd.setOnClickListener(this);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUSET_CODE_PERMISSION_WRITE_STORAGE);
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUSET_CODE_PERMISSION_WRITE_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadFile();
                    Button btnAdd = findViewById(R.id.buttonAdd);
                    btnAdd.setOnClickListener(this);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.no_access), Toast.LENGTH_SHORT);
                    toast.show();
                }
        }

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

            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "list.txt");
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                while ((str = br.readLine()) != null) {
                    result = result.concat(str);
                    Log.d(LOG_TAG, str);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
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
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "list.txt");
            String temp = load();
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                bw.write(input + ";");
                bw.close();
                FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
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

