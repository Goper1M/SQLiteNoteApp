package com.spoonexample.sqlitenoteapp1;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.ContentValues.TAG;
import static java.util.Date.parse;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private Context mContext;
    private static Cursor mCursor;


    public NoteAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView mTitle, mBody, mTime;
        RelativeLayout viewBackground, viewForeground;

        public NoteViewHolder(@NonNull final View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.title);
            mBody = itemView.findViewById(R.id.body);
            mTime = itemView.findViewById(R.id.time);
            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // getting the title, body, and position from the database...?
                    int position = (int) itemView.getTag();
                    CharSequence title = mTitle.getText();
                    CharSequence body = mBody.getText();

                    // passing data using intent
                    Intent intent = new Intent(v.getContext(), NoteTaking.class);
                    intent.putExtra("body", body);
                    intent.putExtra("title", title);
                    intent.putExtra("noteId", position);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //have a question.. WHAT IS mCONTEXT???
        // it is the environment you are in
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_card_item, null);
        NoteViewHolder VH = new NoteViewHolder(v);
        return VH;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder viewHolder, int i) {
        // how to read: if mCursor.moveToPosition() is FALSE or if the
        // not really sure what this does yet.
        // ok i really need this but i don't know why ASK JAMES
        if (!mCursor.moveToPosition(i)) {
            return;
        }
        //reading the string out of our dataBase using mCursor
        String title = mCursor.getString(mCursor.getColumnIndex(Note.NoteEntry.COLUMN_TITLE));
        String body = mCursor.getString(mCursor.getColumnIndex(Note.NoteEntry.COLUMN_BODY));
        // reading the id out of our database.
        int id = mCursor.getInt(mCursor.getColumnIndex(Note.NoteEntry.COLUMN_ID));
        // time of current note
        String time = mCursor.getString (mCursor.getColumnIndex(Note.NoteEntry.COLUMN_TIMESTAMP));

        //getting the current date and 4days before it.
        LocalDate today = LocalDate.now();
        LocalDate todayMinus1 = today.minusDays(1);
        String yesterdayMinus1 = (todayMinus1.getDayOfWeek().toString());
        LocalDate todayMinus2 = today.minusDays(2);
        String yesterdayMinus2 = (todayMinus2.getDayOfWeek().toString());
        LocalDate todayMinus3 = today.minusDays(3);
        String yesterdayMinus3 = (todayMinus3.getDayOfWeek().toString());
        LocalDate todayMinus4 = today.minusDays(4);
        String yesterdayMinus4 = (todayMinus4.getDayOfWeek().toString());
        // formatting the date to my pattern.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyy");
        String mToday = today.format(formatter);
        String mTodayMinus1 = todayMinus1.format(formatter);
        String mTodayMinus2 = todayMinus2.format(formatter);
        String mTodayMinus3 = todayMinus3.format(formatter);
        String mTodayMinus4 = todayMinus4.format(formatter);



//        // current time
//        String timeStamp = new SimpleDateFormat("M/d/yyyy h:mm a").format(new Timestamp(System.currentTimeMillis()));
//        // splitting current date/time
//        String[] tmp2;
//        String currentDate, currentTime;
//        tmp2 = timeStamp.split(" ");
//        currentDate = tmp2[0];
//        currentTime = tmp2[1] + " " + tmp2[2];

        // passed in the time of the note to be parsed.
        Date dateOfNote = null;
        try {
            dateOfNote = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // applied a different format for the note time.
        String result = new SimpleDateFormat("M/d/yyyy h:mm a").format(dateOfNote);

        // split note date/time into two strings.
        String [] temp;
        String noteTime, noteDate;
        temp = result.split(" ");
        noteDate = temp[0];
        noteTime = temp[1] + " " + temp[2];

        // check if note was wrote today,yesterday, etc....
        // and display it
        if(noteDate.equals(mToday)) {
            viewHolder.mTime.setText(noteTime);
        }else if(noteDate.equals(mTodayMinus1)){
            viewHolder.mTime.setText("Yesterday");
        }else if (noteDate.equals(mTodayMinus2)){
            viewHolder.mTime.setText(yesterdayMinus2);
        }else if (noteDate.equals(mTodayMinus3)){
            viewHolder.mTime.setText(yesterdayMinus3);
        }else if (noteDate.equals(mTodayMinus4)){
            viewHolder.mTime.setText(yesterdayMinus4);
        }else{
            viewHolder.mTime.setText(noteDate);
        }

        //This is a whole item in an entry (i.e. the VIEW)
        viewHolder.itemView.setTag(id);
        viewHolder.mTitle.setText(title);
        viewHolder.mBody.setText(body);
    }

    @Override
    public int getItemCount() {
        // getting the count of the table(I think?)
        // ask JAMES about this.
        return mCursor.getCount();
    }


    public void swapCursor(Cursor newCursor) {
        // how to read: if mCursor is NOT empty/if the mCursor is FULL
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = newCursor;

        if (newCursor != null) {
            // guessing that this allows the cards to "reorganize"
            notifyDataSetChanged();
        }
    }
}
