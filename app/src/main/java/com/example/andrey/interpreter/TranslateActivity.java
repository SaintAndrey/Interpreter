package com.example.andrey.interpreter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class TranslateActivity extends AppCompatActivity {
    private final static String KEY_NATIVE_LANG = "Native lang";
    private final static String KEY_FOREIGN_LANG = "Foreign lang";
    private final static String KEY_TEXT = "Text in EditText";

    private TextView mNativeLang;
    private TextView mForeignLang;
    private TextView mSwapLangs;
    private TextView mTranslatedText;
    private EditText mInputText;
    private Button mButtonTranslate;
    private RecyclerView mDictionaryRecyclerView;
    private DictionaryAdapter mAdapter;
    private ImageButton mWordsList;
    private CheckBox mFavoriteCheckBox;

    private Map<String, String> mTranslatorMap;
    private List<String> mSortedLands;
    private String JSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        mNativeLang = (TextView) findViewById(R.id.native_lang);
        mNativeLang.setText("Английский");
        mForeignLang = (TextView) findViewById(R.id.foreign_lang);
        mForeignLang.setText("Русский");
        mSwapLangs = (TextView) findViewById(R.id.swap_lang);
        mTranslatedText = (TextView) findViewById(R.id.translated_word);
        mTranslatedText.setMovementMethod(new ScrollingMovementMethod());
        mInputText = (EditText) findViewById(R.id.translate_field);
        mButtonTranslate = (Button) findViewById(R.id.button_translate);
        mWordsList = (ImageButton) findViewById(R.id.words_list);
        mFavoriteCheckBox = (CheckBox) findViewById(R.id.checkBox);

        mDictionaryRecyclerView = (RecyclerView) findViewById(R.id.translator_recycler_view);
        mDictionaryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getLangs();

        mButtonTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchInDb() == null) {
                    doRequest();
                } else {
                    ListItem item = searchInDb();
                    mTranslatedText.setText(item.getForeignText());
                    mFavoriteCheckBox.setChecked(item.isFavorite());
                    updateList(new ParserDictionary().doParse(item.getJSONFile()));
                }
            }
        });

        mNativeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        mForeignLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        mSwapLangs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapLangs();
            }
        });

        mWordsList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInDb();
                if (mInputText.getText().toString().isEmpty()) {
                    Intent intent = new Intent(TranslateActivity.this, WordsListActivity.class);
                    startActivity(intent);
                    return;
                }

                if (searchInDb() == null) {
                    new WordsListQuery(getApplicationContext()).addItemWord(createWordItem());
                } else {
                    new WordsListQuery(getApplicationContext()).updateItemWord(createWordItem());
                }
                Intent intent = new Intent(TranslateActivity.this, WordsListActivity.class);
                startActivity(intent);
            }
        });

        mInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mFavoriteCheckBox.setChecked(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (savedInstanceState != null) {
            mNativeLang.setText(savedInstanceState.getString(KEY_NATIVE_LANG));
            mForeignLang.setText(savedInstanceState.getString(KEY_FOREIGN_LANG));
            mInputText.setText(savedInstanceState.getString(KEY_TEXT));
            doRequest();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mInputText.getText().toString().isEmpty()) {
            return;
        }

        if (searchInDb() == null) {
            new WordsListQuery(getApplicationContext()).addItemWord(createWordItem());
        } else {
            new WordsListQuery(getApplicationContext()).updateItemWord(createWordItem());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_NATIVE_LANG, mNativeLang.getText().toString());
        outState.putString(KEY_FOREIGN_LANG, mForeignLang.getText().toString());
        outState.putString(KEY_TEXT, mInputText.getText().toString());
    }

    private ListItem searchInDb() {
        WordsListQuery query = new WordsListQuery(getApplicationContext());
        ListItem item = query.getWord(mInputText.getText().toString(), doStringLangs());
        if (item != null) {
            return item;
        }
        return null;
    }

    private ListItem createWordItem() {
        ListItem mWordItem = new ListItem();
        mWordItem.setNativeText(mInputText.getText().toString());
        mWordItem.setForeignText(mTranslatedText.getText().toString());
        mWordItem.setHistory(true);
        mWordItem.setLangs(doStringLangs());
        mWordItem.setFavorite(mFavoriteCheckBox.isChecked());
        mWordItem.setJSONFile(JSON);
        return mWordItem;
    }

    // Отправка запроса на получения перевода и синонимов
    private void doRequest() {
        if (!mInputText.getText().toString().isEmpty()) {
            try {

                RequestTranslator rt = new RequestTranslator();
                RequestDictionary rd = new RequestDictionary();
                rt.execute(doStringLangs(), mInputText.getText().toString());
                mTranslatedText.setText(rt.get());

                rd.execute(doStringLangs(), mInputText.getText().toString());
                updateList(rd.get());

                JSON = rd.getJSON();
            } catch (InterruptedException e) {
            } catch (ExecutionException e) {
            }
        } else {
            mTranslatedText.setText("");
        }
    }

    private String doStringLangs() {
        return mTranslatorMap.get(mNativeLang.getText().toString())
                + "-"
                + mTranslatorMap.get(mForeignLang.getText().toString());
    }

    // Отобразить PopupMenu с выбором поддерживаемых языков
    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.popup_menu);

        for (String lang :
                mSortedLands) {
            popupMenu.getMenu().add(lang);
        }

        final TextView textView = (TextView) v;

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                textView.setText(item.getTitle());
                return true;
            }
        });

        popupMenu.show();
    }

    // Отправка запроса на сервер Яндеск.Переводчик для получения поддерживаемых языков
    private void getLangs() {
        RequestLangs rl = new RequestLangs();
        rl.execute();
        try {
            mTranslatorMap = rl.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        mSortedLands = new ArrayList<>();
        for (Map.Entry entries :
                mTranslatorMap.entrySet()) {
            mSortedLands.add((String)entries.getKey());
        }

        Collections.sort(mSortedLands);
    }

    // Поменять языки местами
    private void swapLangs() {
        String tmp = mNativeLang.getText().toString();
        mNativeLang.setText(
                mForeignLang.getText().toString()
        );
        mForeignLang.setText(tmp);

        if (!mInputText.getText().toString().isEmpty() &&
                !mTranslatedText.getText().toString().isEmpty()) {
            tmp = mInputText.getText().toString();
            mInputText.setText(
                    mTranslatedText.getText().toString()
            );
            mTranslatedText.setText(tmp);
        }

        doRequest();
    }

    private class DictionaryHolder extends RecyclerView.ViewHolder {
        private TextView mNum;
        private TextView mTitleText;
        private TextView mTitleMean;
        private TextView mTitleExamples;


        public DictionaryHolder(View itemView) {
            super(itemView);
            mNum = (TextView) itemView.findViewById(R.id.item_num);
            mTitleText = (TextView) itemView.findViewById(R.id.item_translate_text);
            mTitleMean = (TextView) itemView.findViewById(R.id.item_mean_text);
            mTitleExamples = (TextView) itemView.findViewById(R.id.item_examples);
        }

        public void onBind(Dictionary d, int num) {
            mNum.setText(num + ".");

            mTitleText.setText(fillString(d.getTranslatedText()));

            if (!d.getMeansText().isEmpty()) {
                mTitleMean.setText("(" + fillString(d.getMeansText()) + ")");
            } else {
                mTitleMean.setVisibility(View.GONE);
            }

            StringBuilder exampleMap = new StringBuilder();
            for (Map.Entry entries :
                    d.getExamples().entrySet()) {
                exampleMap.append(entries.getKey() + " - " + entries.getValue() + "\n");
            }
            if (exampleMap.length() != 0) {
                mTitleExamples.setText(exampleMap.toString());
            }
        }

    }

    private class DictionaryAdapter extends RecyclerView.Adapter<DictionaryHolder> {

        List<Dictionary> mDict;

        public DictionaryAdapter(List<Dictionary> dict) {
            mDict = dict;
        }

        @Override
        public DictionaryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            View view = layoutInflater
                    .inflate(R.layout.translator_list_item, parent, false);
            return new DictionaryHolder(view);
        }

        @Override
        public void onBindViewHolder(DictionaryHolder holder, int position) {
            Dictionary dict = mDict.get(position);
            holder.onBind(dict, position + 1);
        }

        @Override
        public int getItemCount() {
            return mDict.size();
        }
    }

    private String fillString(List<String> list) {
        boolean first = true;
        StringBuilder result = new StringBuilder();
        for (String line :
                list) {
            if (first) {
                result.append(line);
                first = false;
            } else {
                result.append(", " + line);
            }
        }
        return result.toString();
    }

    public void updateList(List<Dictionary> dictionaries) {
        Log.d("adapter", dictionaries.toString());
        mAdapter = new DictionaryAdapter(dictionaries);
        mDictionaryRecyclerView.setAdapter(mAdapter);
    }

}
