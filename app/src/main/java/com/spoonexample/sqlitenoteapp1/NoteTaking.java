package com.spoonexample.sqlitenoteapp1;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;


public class NoteTaking extends AppCompatActivity {
    private EditText mTitle, mBody;
    private SQLiteDatabase mDatabase;
    private int mId;
    private String title, body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_taking);

        // if you want to add things to the database you first need to call the getWritableDataBase();
        NoteDataBase dbDataBase = new NoteDataBase(this);
        mDatabase = dbDataBase.getWritableDatabase();

        mTitle = findViewById(R.id.edit_title);
        mBody = findViewById(R.id.edit_body);

        //receiving the passed intents here
        Intent intent = getIntent();
        mId = intent.getIntExtra("noteId", -1);
        title = intent.getStringExtra("title");
        body = intent.getStringExtra("body");

        //setting the title and body
        mTitle.setText(title);
        mBody.setText(body);
    }
    @Override
    public void onBackPressed() {
        // if its empty
        if (mBody.getText().toString().trim().length() == 0 || mTitle.getText().toString().trim().length() == 0){
            Intent intent = new Intent(NoteTaking.this, MainActivity.class);
            startActivity(intent);
            return;
        }
        //putting the body and title text into variables
        String body = mBody.getText().toString();
        String title = mTitle.getText().toString();

        //putting the variables into the database
        ContentValues contentValues = new ContentValues();
        contentValues.put(Note.NoteEntry.COLUMN_TITLE, title);
        contentValues.put(Note.NoteEntry.COLUMN_BODY, body);

        //check to see if you're creating or editing
        if (mId == -1){
            //putting all the contentValues into the database named TABLE_NAME using the function insert().
            mDatabase.insert(Note.NoteEntry.TABLE_NAME, null, contentValues);
        }else{
            //updating all the contentValues of the database named TABLE_NAME using the function update().
            mDatabase.update(Note.NoteEntry.TABLE_NAME, contentValues, "_id = " + mId,null );
            // need to tell the adapter or someone that
            // a note has been updated.., so move it
            // it to the top.
            // ** maybe i don't need to do this and leave the note where it is.
        }
//        //go back to MainActivity..
        Intent intent = new Intent(NoteTaking.this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
