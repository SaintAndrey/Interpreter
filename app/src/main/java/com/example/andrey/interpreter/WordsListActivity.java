package com.example.andrey.interpreter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.List;

public class WordsListActivity extends AppCompatActivity {

    private ImageButton mTranslatorActivity;
    private RecyclerView mRecyclerViewHistory;
    private RecyclerView mRecyclerViewFavorite;
    private HistoryAdapter mAdapterHistory;
    private FavoriteAdapter mAdapterFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words_list);

        mTranslatorActivity = (ImageButton) findViewById(R.id.translate_activity);

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tabSpec;

        tabSpec = tabHost.newTabSpec("History");
        tabSpec.setIndicator("История");
        tabSpec.setContent(R.id.recyclerTab1);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("Favorite");
        tabSpec.setIndicator("Избранное");
        tabSpec.setContent(R.id.recyclerTab2);
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTabByTag("History");

        tabHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUI();
            }
        });

        mRecyclerViewHistory = (RecyclerView) findViewById(R.id.recyclerTab1);
        mRecyclerViewHistory.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mRecyclerViewFavorite = (RecyclerView) findViewById(R.id.recyclerTab2);
        mRecyclerViewFavorite.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        updateUI();

        mTranslatorActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateUI() {
        WordsListQuery query = new WordsListQuery(getApplicationContext());
        List<ListItem> items = query
                .getWords(TranslatorDbSchema.TranslatorTable.Cols.HISTORY + " = ?");
        mAdapterHistory = new HistoryAdapter(items);
        mRecyclerViewHistory.setAdapter(mAdapterHistory);

        items = query
                .getWords(TranslatorDbSchema.TranslatorTable.Cols.FAVORITE + " = ?");
        mAdapterFavorite = new FavoriteAdapter(items);
        mRecyclerViewFavorite.setAdapter(mAdapterFavorite);
    }

    private class HistoryHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private ListItem mItem;

        private TextView mTextNative;
        private TextView mTextForeign;
        private CheckBox mCheckBoxFavorite;
        private TextView mLangs;

        public HistoryHolder(View itemView) {
            super(itemView);
            mTextNative = (TextView) itemView.findViewById(R.id.native_text_in_words_list);
            mTextForeign = (TextView) itemView.findViewById(R.id.foreign_text_in_words_list);
            mCheckBoxFavorite = (CheckBox) itemView.findViewById(R.id.checkBox_in_words_list);
            mLangs = (TextView) itemView.findViewById(R.id.langs_in_words_list);

            mCheckBoxFavorite.setOnClickListener(this);
        }

        public void bindHistory(ListItem item) {
            mItem = item;
            mTextNative.setText(mItem.getNativeText());
            mTextForeign.setText(mItem.getForeignText());
            mCheckBoxFavorite.setChecked(mItem.isFavorite());
            mLangs.setText(mItem.getLangs());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                if (v.getId() == R.id.checkBox_in_words_list) {
                    mItem.setFavorite(mCheckBoxFavorite.isChecked());
                    new WordsListQuery(getApplicationContext()).updateItemWord(mItem);
                }
                updateUI();
            }
        }

    }

    private void showPopupMenu(View v, final String text) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.popup_delete_menu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                new WordsListQuery(getApplicationContext())
                        .deleteItemWord(text);
                updateUI();
                return true;
            }
        });

        popupMenu.show();
    }

    private class HistoryAdapter extends RecyclerView.Adapter<HistoryHolder> {

        private List<ListItem> mListHistory;

        public HistoryAdapter(List<ListItem> items) {
            mListHistory = items;
        }

        @Override
        public HistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            View view = layoutInflater
                    .inflate(R.layout.words_list_item, parent, false);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = mRecyclerViewHistory.getChildLayoutPosition(v);
                    showPopupMenu(v, mListHistory.get(pos).getNativeText());
                    return true;
                }
            });
            return new HistoryHolder(view);
        }

        @Override
        public void onBindViewHolder(HistoryHolder holder, int position) {
            ListItem item = mListHistory.get(position);
            holder.bindHistory(item);
        }

        @Override
        public int getItemCount() {
            return mListHistory.size();
        }

    }


    private class FavoriteHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ListItem mItem;

        private TextView mTextNative;
        private TextView mTextForeign;
        private CheckBox mCheckBoxFavorite;
        private TextView mLangs;

        public FavoriteHolder(View itemView) {
            super(itemView);
            mTextNative = (TextView) itemView.findViewById(R.id.native_text_in_words_list);
            mTextForeign = (TextView) itemView.findViewById(R.id.foreign_text_in_words_list);
            mCheckBoxFavorite = (CheckBox) itemView.findViewById(R.id.checkBox_in_words_list);
            mLangs = (TextView) itemView.findViewById(R.id.langs_in_words_list);
        }

        public void bindHistory(ListItem item) {
            mItem = item;
            mTextNative.setText(mItem.getNativeText());
            mTextForeign.setText(mItem.getForeignText());
            mCheckBoxFavorite.setChecked(mItem.isFavorite());
            mLangs.setText(mItem.getLangs());

            mCheckBoxFavorite.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                if (v.getId() == R.id.checkBox_in_words_list) {
                    mItem.setFavorite(mCheckBoxFavorite.isChecked());
                    new WordsListQuery(getApplicationContext()).updateItemWord(mItem);
                }
                updateUI();
            }
        }
    }

    private class FavoriteAdapter extends RecyclerView.Adapter<FavoriteHolder> {

        private List<ListItem> mListFavorite;

        public FavoriteAdapter(List<ListItem> items) {
            mListFavorite = items;
        }

        @Override
        public FavoriteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            View view = layoutInflater
                    .inflate(R.layout.words_list_item, parent, false);
            return new FavoriteHolder(view);
        }

        @Override
        public void onBindViewHolder(FavoriteHolder holder, int position) {
            ListItem item = mListFavorite.get(position);
            holder.bindHistory(item);
        }

        @Override
        public int getItemCount() {
            return mListFavorite.size();
        }
    }
}
