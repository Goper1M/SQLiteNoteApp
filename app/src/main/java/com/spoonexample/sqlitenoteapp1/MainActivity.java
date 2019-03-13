package com.spoonexample.sqlitenoteapp1;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;


public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase mDatabase;
    private NoteAdapter mAdapter;
    private FloatingActionButton addNote;
    private CoordinatorLayout coordinatorLayout;
    private Cursor mCursor;
    private static final String TAG = "MainActivity";




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


        FloatingActionButton fab = findViewById(R.id.button_createNote);
        addNote = fab;
        coordinatorLayout = findViewById(R.id.coorinatorLayout);

        slideToDelete(recyclerView);
        createNewNote();
    }

    public void createNewNote() {
        addNote.setOnClickListener(new View.OnClickListener() {
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

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                final View foregroundView = ((NoteAdapter.NoteViewHolder) viewHolder).viewForeground;
                // Changing the dX to half results in the slider only appearing half the width!
                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX / 2, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                // the position is pulled from the adapter when
                // we set the tag to an itemView, here we are getting that tag.
                int position = (int)viewHolder.itemView.getTag();
                // telling the cursor to go into the database and
                // query the TABLE_NAME for all columns at the position being clicked
                // limit: limits the number of rows that the query will return.
                mCursor = mDatabase.query(
                        Note.NoteEntry.TABLE_NAME,
                        null,
                        "_id = " + position,
                        null,
                        null,
                        null,
                        null,
                "1");
                // this is when to go?
                // i forgot how to read this again.
                if (!mCursor.moveToFirst()) {
                    return;
                }
                // this is only possible if our cursor is pointing in the right direction.
                // setting all our data from database into variables.
                String title = mCursor.getString(mCursor.getColumnIndex(Note.NoteEntry.COLUMN_TITLE));
                String body = mCursor.getString(mCursor.getColumnIndex(Note.NoteEntry.COLUMN_BODY));
                String timeStamp = (mCursor.getString(mCursor.getColumnIndex(Note.NoteEntry.COLUMN_TIMESTAMP)));
                int id = mCursor.getInt(mCursor.getColumnIndex(Note.NoteEntry._ID));

                snackBar(title, body, timeStamp, id);
                removeItem((int) viewHolder.itemView.getTag());

                mCursor.close();
            }
        }).attachToRecyclerView(recyclerView);
    }

    public void removeItem(int id) {
        mDatabase.delete(Note.NoteEntry.TABLE_NAME, Note.NoteEntry.COLUMN_ID + "=" + id, null);
        mAdapter.swapCursor(getAllData());
    }
    // what does this final mean? ASK JAMES
    public void snackBar(final String title, final String body, final String timeStamp, final int id){
            final Snackbar snackbar = Snackbar.make(coordinatorLayout, "Are you sure you want to delete?", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(Note.NoteEntry.COLUMN_TITLE,title);
                        contentValues.put(Note.NoteEntry.COLUMN_BODY,body);
                        contentValues.put(Note.NoteEntry.COLUMN_ID,id);
                        contentValues.put(Note.NoteEntry.COLUMN_TIMESTAMP, timeStamp);
                        mDatabase.insert(Note.NoteEntry.TABLE_NAME, null, contentValues);
                        // this will get all our data again and bring back the deleted item for the user to see.
                        mAdapter.swapCursor(getAllData());
                    }
                });
        snackbar.show();
    }
}
