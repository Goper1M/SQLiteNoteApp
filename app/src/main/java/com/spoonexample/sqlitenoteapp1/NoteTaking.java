package com.spoonexample.sqlitenoteapp1;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class NoteTaking extends AppCompatActivity {
    private EditText mTitle, mBody;
    private Button mButtonSave;
    private SQLiteDatabase mDatabase;
    private NoteAdapter mAdapter;
    private int mId;
    private String title, body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_taking);

        NoteDataBase dbDataBase = new NoteDataBase(this);
        // if you want to add things to the database you first need to call the getWritableDataBase();
        mDatabase = dbDataBase.getWritableDatabase();

        mTitle = findViewById(R.id.edit_title);
        mBody = findViewById(R.id.edit_body);
        mButtonSave = findViewById(R.id.button_save);

        Intent intent = getIntent();
//        mId = intent.getIntExtra("noteId", -1);
        mId = intent.getIntExtra("noteId", -1);
        title = intent.getStringExtra("title");
        body = intent.getStringExtra("body");

//        Bundle extras = intent.getExtras();
//        int user_id = extras.getInt("noteID");

        Cursor result = dbDataBase.getAllData();
        mTitle.setText(title);
        mBody.setText(body);
        // if you're editting a note
//        if(noteId != -1){
//            // get the body of the note from the database.
//            mTitle.setText(title);
//            StringBuilder stringBuilder = new StringBuilder();
//            result.moveToPosition(noteId);
//            stringBuilder.append(result.getString(3));
//        }


        mSaveData();



    }
    public void mSaveData(){
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mBody is getting turned into a text than a string, than trimmed meaning it will delete empty space in the beginning and end, than getting the length of it.
                if (mBody.getText().toString().trim().length() == 0 || mTitle.getText().toString().trim().length() == 0){
                    return;
                }

                //check to see if this you're editing or making a new note.

                //putting the body and title text into variables
                String body = mBody.getText().toString();
                String title = mTitle.getText().toString();

                //putting the variables into the database
                ContentValues contentValues = new ContentValues();
                contentValues.put(Note.NoteEntry.COLUMN_TITLE, title);
                contentValues.put(Note.NoteEntry.COLUMN_BODY, body);

                //putting all the contentValues into the database named TABLE_NAME using the function insert().
//                mDatabase.insert(Note.NoteEntry.TABLE_NAME, null, contentValues);
                mDatabase.update(Note.NoteEntry.TABLE_NAME, contentValues, "_id = " + mId,null );
//                mAdapter.swapCursor(getAllData());

                //clearing the contents of the body and title.
                mBody.getText().clear();
                mTitle.getText().clear();

                //go back to MainActivity.. right?...
                Intent intent = new Intent(NoteTaking.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


    public void getTitleAndBody (){

    }
}
