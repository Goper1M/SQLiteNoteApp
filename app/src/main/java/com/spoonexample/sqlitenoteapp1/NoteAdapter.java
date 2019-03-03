package com.spoonexample.sqlitenoteapp1;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private Context mContext;
    private static Cursor mCursor;


    public NoteAdapter(Context context, Cursor cursor){
        mContext = context;
        mCursor = cursor;
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView mTitle, mBody;

        // this is for clicking later.
        // so i didn't end up needing this used the itemView instead.
        RelativeLayout mParentLayout;

        public NoteViewHolder(@NonNull final View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.title);
            mBody = itemView.findViewById(R.id.body);
            mParentLayout = itemView.findViewById(R.id.relativeParentCardView);

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
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_card_item,null);
        NoteViewHolder VH = new NoteViewHolder(v);
        return VH;
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder viewHolder, int i) {
        // how to read: if mCursor.moveToPosition() is FALSE
        // not really sure what this does yet.
        // ok i really need this but i don't know why ASK JAMES
        if(!mCursor.moveToPosition(i)){
            return;
        }
        //reading the string out of our dataBase using mCursor
        String title = mCursor.getString(mCursor.getColumnIndex(Note.NoteEntry.COLUMN_TITLE));
        // not going to use this because we only want the title displaying in the cardViews.
        String body = mCursor.getString(mCursor.getColumnIndex(Note.NoteEntry.COLUMN_BODY));
        // reading the id out of our database.
        int id = mCursor.getInt(mCursor.getColumnIndex(Note.NoteEntry.COLUMN_ID));

        //This is a whole item in an entry (eg. a CARDVIEW)
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


    public void swapCursor (Cursor newCursor){
        // how to read: if mCursor is NOT empty/if the mCursor is FULL
        if(mCursor != null){
            mCursor.close();
        }
        mCursor = newCursor;

        if(newCursor != null){
            // guessing that this allows the cards to "reorganize"
            notifyDataSetChanged();
        }
    }
}
