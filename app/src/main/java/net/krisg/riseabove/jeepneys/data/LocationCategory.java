package net.krisg.riseabove.jeepneys.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by KrisEmmanuel on 10/6/2014.
 */
public class LocationCategory {

    private static final String LOG_TAG = LocationCategory.class.getSimpleName();


    private long idLocationCategory;
    private String name;

    public long getIdLocationCategory() {
        return idLocationCategory;
    }

    public void setIdLocationCategory(long idLocationCategory) {
        this.idLocationCategory = idLocationCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocationCategory(long idLocationCategory, String name) {
        this.idLocationCategory = idLocationCategory;
        this.name = name;
    }

    public static ContentValues makeContentValues(String name)
    {
        ContentValues values = new ContentValues();
        values.put(JeepneysContract.LocationCategoryEntry.COLUMN_NAME, name);
        return values;
    }

    public static long getLocationCategoryId(SQLiteDatabase db, String name)
    {
        String[] columns = {
                JeepneysContract.LocationCategoryEntry._ID
        };
        String whereClause = JeepneysContract.LocationCategoryEntry.COLUMN_NAME + "=?";

        String[] whereArgs = new String[] {
                name
        };
        Cursor cursor = db.query(
                JeepneysContract.LocationCategoryEntry.TABLE_NAME,
                columns,
                whereClause, // selection
                whereArgs, // selectionArgs
                null, // groupBy
                null, // having
                null // orderBy
        );
        long id = -1;
        if(cursor.getCount() <= 0)
        {
            cursor.close();
            return -1;
        }
        if(cursor.moveToFirst())
        {
            if(cursor.getCount() > 1)
            {
                Log.e(LOG_TAG, "cursor count is more than 1, there is more id matches that name");
            }

            id = cursor.getLong( cursor.getColumnIndex(JeepneysContract.LocationCategoryEntry._ID) );
            cursor.close();
            return id;
        }
        cursor.close();
        return -1;

    }

}
