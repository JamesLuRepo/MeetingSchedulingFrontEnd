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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.comp2100_6442_androidproject.R;
import com.example.comp2100_6442_androidproject.domain.Gps;
import com.example.comp2100_6442_androidproject.domain.Meeting;
import com.example.comp2100_6442_androidproject.domain.ScheduleShow;
import com.example.comp2100_6442_androidproject.domain.User;
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

public class GroupInfo extends AppCompatActivity {
    private static final String TAG = "GroupInfo:";
    Gson gson;

    TextView groupName;
    TextView groupDescription;

    String groupNameString;
    String groupDescriptionString;
    String gpsId;

    List<ScheduleShow> scheduleList;
    List<String> scheduleNames;
    List<String> scheduleDescriptions;
    List<String> scheduleMid;

    List<User> users;

    //initialize
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        gson = new Gson();
        scheduleDescriptions = new ArrayList<>();
        scheduleNames = new ArrayList<>();
        scheduleMid = new ArrayList<>();
        users= new ArrayList<>();

        groupName = (TextView) findViewById(R.id.groupNameTextView);
        groupDescription = (TextView) findViewById(R.id.groupDescriptionTextView);

        SharedPreferences sharedPreferences = getSharedPreferences("localDataBase", Context.MODE_PRIVATE);
        groupNameString = sharedPreferences.getString("gpsName", "");
        groupDescriptionString = sharedPreferences.getString("gpsDescription", "");
        gpsId = sharedPreferences.getString("gpsId", "");


        Log.d(TAG, "onCreate: gpsId"+gpsId);


        groupName.setText(groupNameString);
        groupDescription.setText(groupDescriptionString);
        getGroupMembers(gpsId);
        getMeetings(gpsId);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {// delay 5 millis and then run this
                progressDialog.dismiss();
                updateMeetings();
                updateGroupMembers();

            }
            //waiting seconds
        }, 5000);

    }

    //right top of the screen
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_group_info, menu);
        return super.onCreateOptionsMenu(menu);

    }

    //when come back to this activity
    @Override
    protected void onResume() {
        super.onResume();

        getGroupMembers(gpsId);
        getMeetings(gpsId);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {// delay 5 millis and then run this
                progressDialog.dismiss();
                updateMeetings();
                updateGroupMembers();

            }
            //waiting seconds
        }, 5000);
    }

    //to add meeting in the group
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_meeting) {
            Intent intent1 = new Intent(this, MeetingAdd.class);
            Bundle bundle = new Bundle();
            bundle.putString("gpsId",gpsId);
            intent1.putExtras(bundle);
            startActivity(intent1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //update the meetings viewList
    private void updateMeetings(){

        ListView listView = (ListView) findViewById(R.id.listViewOfMeetingsInGroupInfo);
        List<Map<String, Object>> listItems = new ArrayList<>();

        for (int i = 0; i < scheduleNames.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("icon", R.mipmap.meetings_img);
            map.put("name", "M"+scheduleMid.get(i)+":"+scheduleNames.get(i));
            map.put("description", scheduleDescriptions.get(i));
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
                Toast.makeText(GroupInfo.this, map.get("name").toString().split(":")[1], Toast.LENGTH_SHORT).show();
            }
        });
    }
    //update groupMembers listView
    private void updateGroupMembers(){
        ListView listView = (ListView) findViewById(R.id.listViewOfMembersInGroupInfo);
        List<Map<String, Object>> listItems = new ArrayList<>();

        for (int i = 0; i < users.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("icon", R.mipmap.img06);
            map.put("name", users.get(i).getName());
            map.put("email", users.get(i).getEmail());
            listItems.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, listItems,
                R.layout.item, new String[]{"name", "icon", "email"},
                new int[]{R.id.name, R.id.img, R.id.info});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> map = (Map<String, Object>) parent.getItemAtPosition(position);
                Toast.makeText(GroupInfo.this, map.get("name").toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    //network connection
    private void getMeetings(String gpsId) {
        String parameter = "?gid=" + gpsId;
        Call task = ConnectionTemplate.getConnection("/getMeetings", parameter);
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
                    scheduleList = gson.fromJson(bodyString, new TypeToken<List<ScheduleShow>>() {
                    }.getType());
                    Log.d(TAG, "gpsList: " + scheduleList);
                    scheduleNames.clear();
                    scheduleDescriptions.clear();
                    scheduleMid.clear();
                    for (ScheduleShow scheduleShow : scheduleList) {
                        scheduleNames.add(scheduleShow.getName());
                        scheduleDescriptions.add(scheduleShow.getInfo());
                        scheduleMid.add(scheduleShow.getMid()+"");
                    }
                }
            }
        });
    }

    //network connection
    private void getGroupMembers(String gpsId) {
        String parameter = "?gid=" + gpsId;
        Call task = ConnectionTemplate.getConnection("/getGroupMembers", parameter);
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
                    users.clear();
                    users = gson.fromJson(bodyString, new TypeToken<List<User>>() {
                    }.getType());
                    Log.d(TAG, "users: " + users);

                }
            }
        });
    }
}
