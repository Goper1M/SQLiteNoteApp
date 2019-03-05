package com.spoonexample.sqlitenoteapp1;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    private Button mNewNote;
    private SQLiteDatabase mDatabase;
    public static NoteAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Database stuff
        NoteDataBase dbDataBase = new NoteDataBase(this);
        mDatabase = dbDataBase.getWritableDatabase();
        // Recycler stuff
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        mAdapter = new NoteAdapter(this, getAllData());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        slideToDelete(recyclerView);

        mNewNote = findViewById(R.id.button_createNote);
        createNewNote();
    }

    public void createNewNote() {
        mNewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), NoteTaking.class);
                intent.putExtra("noteId", -1);
                startActivity(intent);
            }
        });
    }

    public Cursor getAllData() {
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

    public void slideToDelete(RecyclerView recyclerView) {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                removeItem((int) viewHolder.itemView.getTag());
            }
        }).attachToRecyclerView(recyclerView);
    }

    public void removeItem(int id) {
        mDatabase.delete(Note.NoteEntry.TABLE_NAME, Note.NoteEntry.COLUMN_ID + "=" + id, null);
        mAdapter.swapCursor(getAllData());
    }
}
