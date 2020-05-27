package com.example.comp2100_6442_androidproject.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.comp2100_6442_androidproject.R;
import com.example.comp2100_6442_androidproject.domain.ScheduleShow;
import com.example.comp2100_6442_androidproject.utils.ConnectionTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OneKeySchedule extends AppCompatActivity {
    private static final String TAG = "OneKeySchedule:";
    String email;

    Gson gson;
    List<ScheduleShow> availableScheduleList;
    List<ScheduleShow> unavailableScheduleList;

//Initialize the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_key_schedule);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        gson = new Gson();

        SharedPreferences sp = getSharedPreferences("localDataBase", Context.MODE_PRIVATE);
        email = sp.getString("email", "");
        availableScheduleList = new ArrayList<>();
        unavailableScheduleList = new ArrayList<>();

        getAvailableScheduleShowList();
        getUnavailableScheduleShowList();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Calculating...");
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {// delay 5 millis and then run this
                progressDialog.dismiss();
                updateAvailableMeetingListView();
                updateUnavailableMeetingListView();
            }
            //waiting seconds
        }, 6000);

    }
    //update the unavailable meeting list
    private void updateUnavailableMeetingListView(){
        ListView listView = (ListView) findViewById(R.id.listViewOfUnavailableMeetings);
        List<Map<String, Object>> listItems = new ArrayList<>();

        for (ScheduleShow scheduleShow : unavailableScheduleList) {
            Map<String, Object> map = new HashMap<>();
            map.put("icon", R.mipmap.meetings_img);
            map.put("name", "M" + scheduleShow.getMid() + ":" + scheduleShow.getName());
            map.put("description", scheduleShow.getInfo());
            listItems.add(map);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, listItems,
                R.layout.item, new String[]{"name", "icon", "description"},
                new int[]{R.id.name, R.id.img, R.id.info});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> map = (Map<String, Object>) parent.getItemAtPosition(position);
                Toast.makeText(OneKeySchedule.this, map.get("name").toString().split(":")[1], Toast.LENGTH_SHORT).show();
            }
        });
    }

    //update the available meeting list
    private void updateAvailableMeetingListView(){
        ListView listView = (ListView) findViewById(R.id.listViewOfAvailableMeetings);
        List<Map<String, Object>> listItems = new ArrayList<>();

        for (ScheduleShow scheduleShow : availableScheduleList) {
            Map<String, Object> map = new HashMap<>();
            map.put("icon", R.mipmap.meetings_img);
            map.put("name", "M" + scheduleShow.getMid() + ":" + scheduleShow.getName());
            map.put("description", scheduleShow.getInfo());
            listItems.add(map);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, listItems,
                R.layout.item, new String[]{"name", "icon", "description"},
                new int[]{R.id.name, R.id.img, R.id.info});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> map = (Map<String, Object>) parent.getItemAtPosition(position);
                Toast.makeText(OneKeySchedule.this, map.get("name").toString().split(":")[1], Toast.LENGTH_SHORT).show();
            }
        });
    }


    //network connection
    private void getAvailableScheduleShowList() {
        String parameter = "?email=" + this.email;
        Call task = ConnectionTemplate.getConnection("/availableMeeting", parameter);
        task.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure: " + e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                int code = response.code();
                Log.d(TAG, "code: " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    ResponseBody body = response.body();

                    String bodyString = body.string();
                    Log.d(TAG, "body: " + bodyString);
                    availableScheduleList = gson.fromJson(bodyString, new TypeToken<List<ScheduleShow>>() {
                    }.getType());
                    Log.d(TAG, "availableScheduleList: " + availableScheduleList);

                }
            }
        });
    }
    //network connection
    private void getUnavailableScheduleShowList() {
        String parameter = "?email=" + this.email;
        Call task = ConnectionTemplate.getConnection("/unavailableMeeting", parameter);
        task.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure: " + e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                int code = response.code();
                Log.d(TAG, "code: " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    ResponseBody body = response.body();

                    String bodyString = body.string();
                    Log.d(TAG, "body: " + bodyString);
                    unavailableScheduleList = gson.fromJson(bodyString, new TypeToken<List<ScheduleShow>>() {
                    }.getType());
                    Log.d(TAG, "unavailableScheduleList: " + unavailableScheduleList);
                }
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

}
