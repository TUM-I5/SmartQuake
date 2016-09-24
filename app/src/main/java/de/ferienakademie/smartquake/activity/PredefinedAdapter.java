package de.ferienakademie.smartquake.activity;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import de.ferienakademie.smartquake.R;

/**
 * Created by ivana on 23.09.16.
 */
public class PredefinedAdapter extends CursorAdapter {
    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
        }
    }

    public PredefinedAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
        //int viewType = getItemViewType(cursor.getPosition());
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_start_activity, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        //viewHolder.iconView.setImageResource();
        //viewHolder.dateView.setText(Utility.getFriendlyDayString(context, dateInMillis));
        //String description = cursor.getString(ForecastFragment.COL_WEATHER_DESC);

       // viewHolder.iconView.setContentDescription(description);

    }
}
