package com.example.andrey.interpreter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;

public class WordsListActivity extends AppCompatActivity {

    private Button mTransltorActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words_list);

        mTransltorActivity = (Button) findViewById(R.id.translate_activity);

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabSpec;

        tabSpec = tabHost.newTabSpec("History");
        tabSpec.setIndicator("История");
        tabSpec.setContent(R.id.recylerTab1);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("Favorite");
        tabSpec.setIndicator("Избранное");
        tabSpec.setContent(R.id.recylerTab2);
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTabByTag("History");

        mTransltorActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
