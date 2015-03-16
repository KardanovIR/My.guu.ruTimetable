package ru.guu.my.myguuruclient;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Инал on 15.03.2015.
 */
public class TimetableAdapter extends CursorAdapter {


    private final int VIEW_NEAREST_CLASS = 0;
    private final int VIEW_REGULAR_CLASS = 1;


    public TimetableAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = (viewType == VIEW_NEAREST_CLASS) ? R.layout.list_item_nearest_class : R.layout.list_item_timetable;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    private String convertCursorRowToUXFormat(Cursor cursor) {

        return cursor.getString(TimetableFragment.COL_SUBJECT_REAL_NAME) +
                " - " +
                cursor.getString(TimetableFragment.COL_SUBJECT_REAL_NAME) + "-" +
                cursor.getString(TimetableFragment.COL_SUBJECT_REAL_NAME) + " in " +
                cursor.getString(TimetableFragment.COL_SUBJECT_REAL_NAME) + "-" +
                cursor.getString(TimetableFragment.COL_SUBJECT_REAL_NAME);
    }


    @Override
    public int getItemViewType(int position) {
        Cursor cursor = (Cursor) getItem(position);
        int viewType;
        if (cursor != null) {
            viewType = cursor.getInt(TimetableFragment.COL_DAY_NUMBER) == (Utils.getTodayWeekDayNumber() + 2) ? VIEW_NEAREST_CLASS : VIEW_REGULAR_CLASS;
        } else {
            viewType = VIEW_REGULAR_CLASS;
        }
        return viewType;
    }

    @Override
    public int getViewTypeCount() {
        return 2; //Nearest/now and regular
    }

    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.dayAbbrTView.setText(cursor.getString(TimetableFragment.COL_DAY_ABBR));
        viewHolder.timeTView.setText(Utils.getTimeRange(cursor.getString(TimetableFragment.COL_START_TIME), cursor.getString(TimetableFragment.COL_FINISH_TIME)));
        viewHolder.subjectNameTView.setText(cursor.getString(TimetableFragment.COL_SUBJECT_REAL_NAME));
        viewHolder.classRoomTView.setText(cursor.getString(TimetableFragment.COL_ROOM));
    }


    public static class ViewHolder {
        public final TextView dayAbbrTView;
        public final TextView timeTView;
        public final TextView subjectNameTView;
        public final TextView classRoomTView;

        public ViewHolder(View view) {
            dayAbbrTView = (TextView) view.findViewById(R.id.list_item_day_abbr_textview);
            timeTView = (TextView) view.findViewById(R.id.list_item_time_textview);
            subjectNameTView = (TextView) view.findViewById(R.id.list_item_subject_name_textview);
            classRoomTView = (TextView) view.findViewById(R.id.list_item_classroom_textview);
        }

    }
}
