package com.beeminder.gtbee.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.beeminder.gtbee.R;
import com.beeminder.gtbee.Utility;
import com.beeminder.gtbee.data.Contract;

import java.util.Calendar;


/**
 * Created by nick on 11/06/15.
 */
public class TaskAdapter extends CursorAdapter {

    public TaskAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static class ViewHolder {
        public final TextView dateTopView;
        public final TextView dateBottomView;
        public final TextView titleView;
        public final TextView penaltyView;


        public ViewHolder(View view) {
            dateTopView = (TextView) view.findViewById(R.id.list_item_date_top);
            dateBottomView = (TextView) view.findViewById(R.id.list_item_date_bottom);
            titleView = (TextView) view.findViewById(R.id.list_item_title);
            penaltyView = (TextView) view.findViewById(R.id.list_item_penalty);

        }
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_task, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String title = cursor.getString(cursor.getColumnIndex(Contract.KEY_TITLE));
        viewHolder.titleView.setText(title);

        Long dueDate = cursor.getLong(cursor.getColumnIndex(Contract.KEY_DUE_DATE));
        viewHolder.dateTopView.setText(new Utility().mainScreenTop(dueDate));
        viewHolder.dateBottomView.setText(new Utility().mainScreenBottom(dueDate));

        Long currentTime = Calendar.getInstance().getTimeInMillis();
        if ((dueDate - currentTime) < 0){
            view.setBackgroundResource(R.color.Primary_light);
        } else {
            view.setBackgroundColor(Color.WHITE);
        }

        int penalty = cursor.getInt(cursor.getColumnIndex(Contract.KEY_PENALTY));
        viewHolder.penaltyView.setText(new Utility().formatPenalty(penalty));


    }

}
