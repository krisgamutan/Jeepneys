package net.krisg.riseabove.jeepneys.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by KrisEmmanuel on 9/29/2014.
 */
public class Path {
    private long id;
    private long idRoute;
    private long idWaypoint;
    private String description;

    //private Route route;
    //private Waypoint waypoint;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdRoute() {
        return idRoute;
    }

    public void setIdRoute(long idRoute) {
        this.idRoute = idRoute;
    }

    public long getIdWaypoint() {
        return idWaypoint;
    }

    public void setIdWaypoint(long idWaypoint) {
        this.idWaypoint = idWaypoint;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Path(long id, long idRoute, long idWaypoint, String description) {
        this.id = id;
        this.idRoute = idRoute;
        this.idWaypoint = idWaypoint;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Path)) return false;

        Path path = (Path) o;

        if (id != path.id) return false;
        if (idRoute != path.idRoute) return false;
        if (idWaypoint != path.idWaypoint) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (idRoute ^ (idRoute >>> 32));
        result = 31 * result + (int) (idWaypoint ^ (idWaypoint >>> 32));
        return result;
    }

    public static long getMaxId(SQLiteDatabase db)
    {
        int max = -1;


        String[] columns = {
                "MAX(" + JeepneysContract.PathEntry._ID + ")"
        };

        Cursor cursor = db.query(
                JeepneysContract.PathEntry.TABLE_NAME,
                columns,
                null, // selection
                null, // selectionArgs
                null, // groupBy
                null, // having
                null // orderBy
        );
        if(cursor.getCount() <= 0)
        {
            cursor.close();
            return -1;
        }
        if(cursor.moveToFirst())
        {
            max = cursor.getInt(0);

            cursor.close();
            return max;
        }
        cursor.close();
        return -1;
    }



    public static ContentValues makeContentValues(long idPath, long idRoute, long idWaypoint, String description)
    {
        ContentValues values = new ContentValues();
        values.put(JeepneysContract.PathEntry._ID, idPath);
        values.put(JeepneysContract.PathEntry.COLUMN_ROUTE_IDROUTE, idRoute);
        values.put(JeepneysContract.PathEntry.COLUMN_WAYPOINT_IDWAYPOINT, idWaypoint);
        values.put(JeepneysContract.PathEntry.COLUMN_DESCRIPTION, description);
        return values;
    }

    public static Path getPath(SQLiteDatabase db, long id)
    {
        Path path = null;

        String[] columns = {
                JeepneysContract.PathEntry._ID,
                JeepneysContract.PathEntry.COLUMN_ROUTE_IDROUTE,
                JeepneysContract.PathEntry.COLUMN_WAYPOINT_IDWAYPOINT,
                JeepneysContract.PathEntry.COLUMN_DESCRIPTION
        };

        String whereClause = JeepneysContract.PathEntry._ID + "=?";

        String[] whereArgs = new String[] {
                Long.toString(id)
        };

        Cursor cursor = db.query(
                JeepneysContract.PathEntry.TABLE_NAME,
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

        if(cursor.moveToFirst())
        {
            long tId = cursor.getLong( cursor.getColumnIndex(JeepneysContract.PathEntry._ID));
            long tRouteId = cursor.getLong( cursor.getColumnIndex(JeepneysContract.PathEntry.COLUMN_ROUTE_IDROUTE));
            long tWaypointId = cursor.getLong( cursor.getColumnIndex(JeepneysContract.PathEntry.COLUMN_WAYPOINT_IDWAYPOINT));
            String tDescription = cursor.getString( cursor.getColumnIndex(JeepneysContract.PathEntry.COLUMN_DESCRIPTION));

            path = new Path(tId, tRouteId, tWaypointId, tDescription );

            cursor.close();
            return path;
        }

        cursor.close();


        return path;
    }


}
