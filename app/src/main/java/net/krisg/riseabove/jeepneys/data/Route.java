package net.krisg.riseabove.jeepneys.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by KrisEmmanuel on 9/29/2014.
 */
public class Route {
    private long id;
    private String routeNumber;


    public Route(long id, String routeNumber) {
        this.id = id;
        this.routeNumber = routeNumber;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRouteNumber() {
        return routeNumber;
    }

    public void setRouteNumber(String routeNumber) {
        this.routeNumber = routeNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route)) return false;

        Route route = (Route) o;

        if (!routeNumber.equals(route.routeNumber)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return routeNumber.hashCode();
    }

    public static ContentValues makeContentValues(String routeNumber)
    {
        ContentValues values = new ContentValues();
        values.put(JeepneysContract.RouteEntry.COLUMN_ROUTENUMBER, routeNumber);
        return values;
    }
    public static long getRouteId(SQLiteDatabase db, String routeNumber)
    {
        String[] columns = {
                JeepneysContract.RouteEntry._ID
        };

        String whereClause = JeepneysContract.RouteEntry.COLUMN_ROUTENUMBER + "=?";

        String[] whereArgs = new String[] {
                routeNumber
        };

        Cursor cursor = db.query(
                JeepneysContract.RouteEntry.TABLE_NAME,
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
            id = cursor.getLong(0);
            cursor.close();
            return id;
        }
        cursor.close();
        return -1;
    }
    public static Route getRoute(SQLiteDatabase db, long id)
    {
        Route route = null;
        String[] columns = {
                JeepneysContract.RouteEntry._ID,
                JeepneysContract.RouteEntry.COLUMN_ROUTENUMBER
        };

        String whereClause = JeepneysContract.RouteEntry._ID + "=?";

        String[] whereArgs = new String[] {
                Long.toString(id)
        };

        Cursor cursor = db.query(
                JeepneysContract.RouteEntry.TABLE_NAME,
                columns,
                whereClause, // selection
                whereArgs, // selectionArgs
                null, // groupBy
                null, // having
                null // orderBy
        );
        if(cursor.getCount() <= 0)
        {
            cursor.close();
            return null;
        }
        if(cursor.moveToFirst()) // this assumes only unique vertex id per lat lng
        {
            long tRouteId = cursor.getLong( cursor.getColumnIndex(JeepneysContract.RouteEntry._ID));
            String tRouteNumber = cursor.getString( cursor.getColumnIndex(JeepneysContract.RouteEntry.COLUMN_ROUTENUMBER));
            route = new Route(tRouteId, tRouteNumber);
            cursor.close();
            return route;
        }

        cursor.close();
        return route;

    }



}
