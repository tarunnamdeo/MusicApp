package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    String[] song_names = {"bensound", "sunny", "terenaal"};
    private boolean pos = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.activity_listview, song_names);
        ListView listView = findViewById(R.id.list_item);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this, adapter.getItem(i).toString(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, PlayingActivity.class);
                intent.putExtra("name", adapter.getItem(i).toString());
                startActivity(intent);
            }
        });


        final WebView webView = findViewById(R.id.web_view);
        final Button webViewbtn = findViewById(R.id.web_view_btn);
        webViewbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (pos) {
                    webViewbtn.setText("Go Back");
                    webView.setVisibility(View.VISIBLE);
                    webView.setWebViewClient(new WebViewClient());
                    webView.loadUrl("file:///android_asset/android.html");
                    pos = false;
                } else {
                    webViewbtn.setText("WebView");
                    webView.setVisibility(View.GONE);
                    pos = true;
                }
            }
        });

    }


}
