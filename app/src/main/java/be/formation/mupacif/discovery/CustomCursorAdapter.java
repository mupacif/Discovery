package be.formation.mupacif.discovery;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import be.formation.mupacif.discovery.db.InterestDAO;

/**
 * Created by student on 20-03-17.
 */

public class CustomCursorAdapter extends RecyclerView.Adapter<CustomCursorAdapter.InterestViewHolder> {

    private Cursor cursor;
    private Context context;

    public CustomCursorAdapter(Context context)
    {
        this.context = context;
    }

    @Override
    public InterestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.interest_layout,parent,false);
        return new InterestViewHolder(view);
    }

    /**
     * Called by the recyclerview when data is displayed at a specified position
     * @param holder view holder to bind cursor data to
     * @param position position of the data
     */
    @Override
    public void onBindViewHolder(InterestViewHolder holder, int position) {



        int idTitle = cursor.getColumnIndex(InterestDAO.COL_TITLE);

        String description = cursor.getString(idTitle);

        cursor.moveToPosition(position);
        holder.interstNameView.setText(description);
    }

    @Override
    public int getItemCount() {
        if(cursor == null)
        return 0;
        else
            return cursor.getCount();
    }

    class InterestViewHolder extends RecyclerView.ViewHolder
    {

        TextView interstNameView;
        public InterestViewHolder(View itemView) {
            super(itemView);
            interstNameView = (TextView)itemView.findViewById(R.id.tv_interestTitle);
        }
    }

    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (cursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = cursor;
        this.cursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

}
