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
 * Created by nick on 25/07/15.
 */
public class FailedTaskAdapter extends CursorAdapter {

    public FailedTaskAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static class ViewHolder {
        public final TextView mTitle;
        public final TextView mPaymentStatus;
        public final TextView mPaymentAmount;


        public ViewHolder(View view) {
            mTitle = (TextView) view.findViewById(R.id.failed_task_title);
            mPaymentStatus = (TextView) view.findViewById(R.id.failed_task_payment_status);
            mPaymentAmount = (TextView) view.findViewById(R.id.failed_task_paymend_amount);

        }
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_failed_task, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String title = cursor.getString(cursor.getColumnIndexOrThrow(Contract.KEY_TITLE));
        viewHolder.mTitle.setText(title);

        int paymentAmount = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.KEY_PENALTY));
        viewHolder.mPaymentAmount.setText("$" + paymentAmount);

        int payed = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.KEY_PAYED));
        String paymentStatus;
        if (payed == 0){
            paymentStatus = "Payment Pending";
        } else  {
            paymentStatus = "Payed";
        }
        viewHolder.mPaymentStatus.setText(paymentStatus);

    }
}
