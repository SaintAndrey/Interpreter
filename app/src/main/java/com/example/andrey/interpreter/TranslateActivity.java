package com.example.andrey.interpreter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
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
    private RecyclerView mDictionaryRecyclerView;
    private DictionaryAdapter mAdapter;
    private CheckBox mFavoriteCheckBox;

    private Map<String, String> mTranslatorMap;
    private List<String> mSortedLands;
    private String JSON;
    private Cache mCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        mCache = new Cache();

        mNativeLang = (TextView) findViewById(R.id.native_lang);
        mNativeLang.setText("Английский");
        mForeignLang = (TextView) findViewById(R.id.foreign_lang);
        mForeignLang.setText("Русский");
        mSwapLangs = (TextView) findViewById(R.id.swap_lang);
        mTranslatedText = (TextView) findViewById(R.id.translated_word);
        mTranslatedText.setMovementMethod(new ScrollingMovementMethod());
        mInputText = (EditText) findViewById(R.id.translate_field);
        mFavoriteCheckBox = (CheckBox) findViewById(R.id.checkBox);

        mDictionaryRecyclerView = (RecyclerView) findViewById(R.id.translator_recycler_view);
        mDictionaryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getLangs();

        mFavoriteCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addInDb();
            }
        });

        mNativeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v, mForeignLang.getText().toString());
            }
        });

        mForeignLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v, mNativeLang.getText().toString());
            }
        });

        mSwapLangs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapLangs();
            }
        });

        mInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mFavoriteCheckBox.setChecked(false);
                if (mInputText.getText().toString().isEmpty()) {
                    mFavoriteCheckBox.setVisibility(View.GONE);
                    mTranslatedText.setText("");
                    mDictionaryRecyclerView.setVisibility(View.GONE);
                } else {
                    mFavoriteCheckBox.setVisibility(View.VISIBLE);
                    mDictionaryRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                getChange();
            }
        });

        if (savedInstanceState != null) {
            mNativeLang.setText(savedInstanceState.getString(KEY_NATIVE_LANG));
            mForeignLang.setText(savedInstanceState.getString(KEY_FOREIGN_LANG));
            mInputText.setText(savedInstanceState.getString(KEY_TEXT));
            getChange();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_NATIVE_LANG, mNativeLang.getText().toString());
        outState.putString(KEY_FOREIGN_LANG, mForeignLang.getText().toString());
        outState.putString(KEY_TEXT, mInputText.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Если поле для ввода пустое, то сразу переходим в другую активность
        if (mInputText.getText().toString().isEmpty()) {
            Intent intent = new Intent(TranslateActivity.this, WordsListActivity.class);
            startActivity(intent);
            return super.onOptionsItemSelected(item);
        }

        // Если нет, то добавляем/обновляем запись, и переходим в активность
        addInDb();
        Intent intent = new Intent(TranslateActivity.this, WordsListActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    // Поиск по БД
    // Если находим нужный элемент, то возвращаем его
    private ListItem searchInDb() {
        WordsListQuery query = new WordsListQuery(getApplicationContext());
        ListItem item = query.getWord(mInputText.getText().toString(), doStringLangs());
        if (item != null) {
            return item;
        }
        return null;
    }

    // Назначение слушателя на ChechBox
    // Если данная запись не существует в БД, то добавляет ее
    // Если существует, то обновляет эту запись, с изменением поля favorite
    private void addInDb() {
        if (searchInDb() == null) {
            new WordsListQuery(getApplicationContext()).addItemWord(createWordItem());
        } else {
            new WordsListQuery(getApplicationContext()).updateItemWord(createWordItem());
        }
    }

    // Изменить результаты
    private void getChange() {
        if (searchInDb() != null) {
            // Поиск по БД
            ListItem item = searchInDb();
            mTranslatedText.setText(item.getForeignText());
            mFavoriteCheckBox.setChecked(item.isFavorite());
            updateList(new ParserDictionary().doParse(item.getJSONFile()));
            Log.d("cache", "Db");
        } else if(mCache.searchInCache(mInputText.getText().toString(), doStringLangs())) {
            // Поиск по кэшу
            Cache.ItemCache item = mCache
                    .getItemCache(mInputText.getText().toString(), doStringLangs());
            mTranslatedText.setText(item.getForeignText());
            updateList(item.getDictionaries());
            Log.d("cache", "Cache");
        } else {
            // Запрос в интернет
            doRequest();
            mCache.addInCashe(
                    mInputText.getText().toString(),
                    mTranslatedText.getText().toString(),
                    doStringLangs(),
                    new ParserDictionary().doParse(JSON)
            );
            Log.d("cache", "Internet");
        }
    }

    // Формирование элемента ListItem
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
        }
    }

    // Возращаем валидную строку пару значений языков
    private String doStringLangs() {
        return mTranslatorMap.get(mNativeLang.getText().toString())
                + "-"
                + mTranslatorMap.get(mForeignLang.getText().toString());
    }

    // Отобразить PopupMenu с выбором поддерживаемых языков
    private void showPopupMenu(View v, final String anotherLang) {
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
                // Если языки совпадают, то вместо замены мы меняем языки местами
                if (item.getTitle().equals(anotherLang)) {
                    swapLangs();
                } else {
                    textView.setText(item.getTitle());
                    getChange();
                }
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

        getChange();
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
        mAdapter = new DictionaryAdapter(dictionaries);
        mDictionaryRecyclerView.setAdapter(mAdapter);
    }

}
