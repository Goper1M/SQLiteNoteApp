package com.spoonexample.sqlitenoteapp1;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase mDatabase;
    private NoteAdapter mAdapter;
    private FloatingActionButton mNewNote2;


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

        FloatingActionButton fab = findViewById(R.id.button_createNote);
        mNewNote2 = fab;
        createNewNote();
    }

    public void createNewNote() {
        mNewNote2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), NoteTaking.class);
                intent.putExtra("noteId", -1);
                startActivity(intent);
            }
        });
    }

    // this will get all the items out of the noteList Table.
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
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            // the x-position of the foreground view is changed while user is swiping the view.
            @Override
            public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                final View foregroundView = ((NoteAdapter.NoteViewHolder) viewHolder).viewForeground;
                // used by ItemTouchHelper to detect whenever there is UI change on the view.
                // We use this function to keep the background view in a static position and move the foreground view.
                // i'm talking about getDefaultUiUtil();
                getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
            }

            // i think this just clears the view
            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                final View foregroundView = ((NoteAdapter.NoteViewHolder) viewHolder).viewForeground;
                getDefaultUIUtil().clearView(foregroundView);
            }

            // when we select something.
//            @Override
//            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
//                final View foregroundView = ((NoteAdapter.NoteViewHolder) viewHolder).viewForeground;
//                getDefaultUIUtil().onSelected(foregroundView);
//            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                final View foregroundView = ((NoteAdapter.NoteViewHolder) viewHolder).viewForeground;
                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
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
