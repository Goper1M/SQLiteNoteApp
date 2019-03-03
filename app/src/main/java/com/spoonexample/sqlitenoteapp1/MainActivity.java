package com.spoonexample.sqlitenoteapp1;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    private Button mNewNote;
    private SQLiteDatabase mDatabase;
    private NoteAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Database stuff
        NoteDataBase dbDataBase = new NoteDataBase(this);
        mDatabase = dbDataBase.getWritableDatabase();
        // Recycler stuff
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new NoteAdapter(this, getAllData());
        recyclerView.setAdapter(mAdapter);

        mNewNote = findViewById(R.id.button_createNote);
        createNewNote();
    }

    public void createNewNote(){
        mNewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), NoteTaking.class);
                intent.putExtra("noteId", -1);
                startActivity(intent);
            }
        });
    }

    public Cursor getAllData(){
        return mDatabase.query(
                Note.NoteEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                Note.NoteEntry.COLUMN_TIMESTAMP + " DESC"
        );
    }
}
