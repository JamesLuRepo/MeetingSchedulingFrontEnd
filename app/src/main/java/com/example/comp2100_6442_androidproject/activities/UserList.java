package com.example.comp2100_6442_androidproject.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.comp2100_6442_androidproject.R;
import com.example.comp2100_6442_androidproject.domain.ScheduleShow;
import com.example.comp2100_6442_androidproject.domain.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserList extends AppCompatActivity {

    //initialize the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String searchResultUsersJSON = bundle.getString("searchResultUsersJSON");
        Gson gson = new Gson();
        List<User> users = gson.fromJson(searchResultUsersJSON, new TypeToken<List<User>>() {
        }.getType());


        ListView listView = (ListView) findViewById(R.id.listviewOfUser);
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
                // the progressDialog to wait for the respond
                String selectedEmail = map.get("email").toString();
                SharedPreferences sharedPreferences = getSharedPreferences("localDataBase", Context.MODE_PRIVATE);
                String userEmail = sharedPreferences.getString("email", "");

                if (selectedEmail.equals(userEmail)) {
                    Toast.makeText(UserList.this, "Cannot select yourself!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = getIntent();
                    Bundle bundle = new Bundle();
                    bundle.putString("selectedEmail", selectedEmail);
                    intent.putExtras(bundle);
                    setResult(932, intent);
                    finish();
                }
            }
        });
    }
}
