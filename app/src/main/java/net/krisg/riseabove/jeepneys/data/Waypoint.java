package net.krisg.riseabove.jeepneys.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by KrisEmmanuel on 9/29/2014.
 */
public class Waypoint {
    private long id;
    private ArrayList<Edge> edges = new ArrayList<Edge>(); // uses this index for edgeIndex of the table

    private static final String LOG_TAG = Waypoint.class.getSimpleName();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public void setEdges(ArrayList<Edge> edges) {
        this.edges = edges;
    }

    public Waypoint(long id, ArrayList<Edge> edges) {
        this.id = id;
        this.edges = edges;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Waypoint)) return false;

        Waypoint waypoint = (Waypoint) o;

        if (!edges.equals(waypoint.edges)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return edges.hashCode();
    }


    public Edge getEdge(int index)
    {
        return edges.get(index);
    }

    public static long getMaxId(SQLiteDatabase db)
    {
        int max = -1;

        String[] columns = {
                "MAX(" + JeepneysContract.WaypointEntry._ID + ")"
        };

        Cursor cursor = db.query(
                JeepneysContract.WaypointEntry.TABLE_NAME,
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
    public static ContentValues makeContentValues(long idWaypoint, int edgeIndex, long idEdge)
    {
        ContentValues values = new ContentValues();
        values.put(JeepneysContract.WaypointEntry._ID, idWaypoint);
        values.put(JeepneysContract.WaypointEntry.COLUMN_EDGEINDEX, edgeIndex);
        values.put(JeepneysContract.WaypointEntry.COLUMN_EDGE_IDEDGE, idEdge);
        return values;
    }

    public static Waypoint getWaypoint(SQLiteDatabase db, long id)
    {
        Waypoint waypoint = null;
        String[] columns = {
                JeepneysContract.WaypointEntry._ID,
                JeepneysContract.WaypointEntry.COLUMN_EDGEINDEX,
                JeepneysContract.WaypointEntry.COLUMN_EDGE_IDEDGE
        };

        String whereClause = JeepneysContract.WaypointEntry._ID + "=?";

        String[] whereArgs = new String[] {
                Long.toString(id)
        };

        String orderBy = JeepneysContract.WaypointEntry.COLUMN_EDGEINDEX + " ASC"; // must be ASC to match the edge index and loop index

        Cursor cursor = db.query(
                JeepneysContract.WaypointEntry.TABLE_NAME,
                columns,
                whereClause, // selection
                whereArgs, // selectionArgs
                null, // groupBy
                null, // having
                orderBy // orderBy
        );
        if(cursor.getCount() <= 0)
        {
            cursor.close();
            return null;
        }

        ArrayList<Edge> tEdges = new ArrayList<Edge>();

        // check cursor count and max edgeIndex ?

        //int i=0;

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            long tWaypointId = cursor.getLong( cursor.getColumnIndex(JeepneysContract.WaypointEntry._ID));
            int tEdgeIndex = cursor.getInt( cursor.getColumnIndex(JeepneysContract.WaypointEntry.COLUMN_EDGEINDEX));
            long tEdgeId = cursor.getLong( cursor.getColumnIndex(JeepneysContract.WaypointEntry.COLUMN_EDGE_IDEDGE));

            if(tWaypointId != id) // checking id must match the returned cursor edge_id
            {
                Log.e(LOG_TAG,"getWaypoint param id does not match tEdgeId");
                return null;
            }

            //Log.d(LOG_TAG, "tEdgeId:" + tEdgeId);

            Edge tEdge = Edge.getEdge(db, tEdgeId);

            // checking of tEdgeIndex and loop index

            //Log.d(LOG_TAG, "i:"+i+"edgeIdx:"+tEdgeIndex+",tEdge:" + tEdge);

            tEdges.add(tEdge);

            //i++;
        }

        waypoint = new Waypoint(id, tEdges);

        cursor.close();
        return waypoint;
    }

    public static int waypointCountEdgeId(SQLiteDatabase db, long waypointId, long edgeId)
    {
        int count = -1;
        String[] columns = {
                "COUNT(" + JeepneysContract.WaypointEntry.COLUMN_EDGE_IDEDGE + ")"
        };

        String whereClause = JeepneysContract.WaypointEntry.COLUMN_EDGE_IDEDGE + "=? AND " + JeepneysContract.WaypointEntry._ID + "=?";

        String[] whereArgs = new String[] {
                Long.toString(edgeId),
                Long.toString(waypointId)
        };

        Cursor cursor = db.query(
                JeepneysContract.WaypointEntry.TABLE_NAME,
                columns,
                whereClause, // selection
                whereArgs, // selectionArgs
                null, // groupBy
                null, // having
                null // orderBy
        );



        if(cursor.getCount() <= 0) // No waypoints found having that edgesId
        {
            cursor.close();
            //db.close();
            return 0;
        }
        else
        {
            cursor.moveToFirst();
            count = cursor.getInt( 0 );
            cursor.close();
        }
        //db.close();
        return count;
    }

    public static ArrayList<Long> waypointIdThatHaveEdges(SQLiteDatabase db, long edgeId)
    {
        ArrayList<Long> waypointIdList = new ArrayList<Long>();

        String[] columns = {
                JeepneysContract.WaypointEntry._ID
        };

        String whereClause = JeepneysContract.WaypointEntry.COLUMN_EDGE_IDEDGE + "=?";

        String[] whereArgs = new String[] {
                Long.toString(edgeId)
        };

        Cursor cursor = db.query(
                JeepneysContract.WaypointEntry.TABLE_NAME,
                columns,
                whereClause, // selection
                whereArgs, // selectionArgs
                null, // groupBy
                null, // having
                null // orderBy
        );

        if(cursor.getCount() <= 0) // No waypoints found having that edgesId list
        {
            cursor.close();
            //db.close();
            return null;
        }


        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            waypointIdList.add(new Long( cursor.getLong( cursor.getColumnIndex(JeepneysContract.WaypointEntry._ID)) ));

        }
        cursor.close();


        //db.close();
        return waypointIdList;
    }

    // Returns -1 is there's no EdgeId in that waypointId
    public static int getIndexOfEdge(SQLiteDatabase db, long waypointId, long edgeId)
    {
        int edgeIndex = -1;

        String[] columns = {
                JeepneysContract.WaypointEntry.COLUMN_EDGEINDEX
        };

        String whereClause = JeepneysContract.WaypointEntry.COLUMN_EDGE_IDEDGE + "=? AND " + JeepneysContract.WaypointEntry._ID + "=?";

        String[] whereArgs = new String[] {
                Long.toString(edgeId),
                Long.toString(waypointId)
        };

        Cursor cursor = db.query(
                JeepneysContract.WaypointEntry.TABLE_NAME,
                columns,
                whereClause, // selection
                whereArgs, // selectionArgs
                null, // groupBy
                null, // having
                null // orderBy
        );

        if(cursor.getCount() <= 0) // No waypoints found having that edgesId list
        {
            cursor.close();
            //db.close();
            Log.d(LOG_TAG, "no edgeIndex of that edgeId in that waypointId");
            return -1;

        }
        if(cursor.getCount() > 1)
        {
            cursor.close();
            //db.close();
            Log.e(LOG_TAG, "WARNING: there is more than one edgeIndex of that edgeId in that waypointId");
            return -1;

        }


        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            edgeIndex = cursor.getInt( cursor.getColumnIndex(JeepneysContract.WaypointEntry.COLUMN_EDGEINDEX));

        }
        cursor.close();
        //db.close();
        return edgeIndex;
    }



    public static ArrayList<Waypoint> waypointThatHaveEdges(SQLiteDatabase db, long edgeId)
    {
        ArrayList<Waypoint> waypointList = new ArrayList<Waypoint>();

        String[] columns = {
                JeepneysContract.WaypointEntry._ID
        };

        String whereClause = JeepneysContract.WaypointEntry.COLUMN_EDGE_IDEDGE + "=?";

        String[] whereArgs = new String[] {
                Long.toString(edgeId)
        };

        Cursor cursor = db.query(
                JeepneysContract.WaypointEntry.TABLE_NAME,
                columns,
                whereClause, // selection
                whereArgs, // selectionArgs
                null, // groupBy
                null, // having
                null // orderBy
        );

        if(cursor.getCount() <= 0) // No waypoints found having that edgesId list
        {
            cursor.close();
            //db.close();
            return null;
        }


        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            waypointList.add(Waypoint.getWaypoint(db, cursor.getLong( cursor.getColumnIndex(JeepneysContract.WaypointEntry._ID))));

        }
        cursor.close();


        //db.close();
        return waypointList;
    }


    public static int countEdges(SQLiteDatabase db, long waypointId)
    {
        // the number of edges a waypoint has
        int count = -1;
        String[] columns = {
                "COUNT(" + JeepneysContract.WaypointEntry.COLUMN_EDGE_IDEDGE + ")"
        };

        String whereClause = JeepneysContract.WaypointEntry._ID + "=?";

        String[] whereArgs = new String[] {
                Long.toString(waypointId)
        };

        Cursor cursor = db.query(
                JeepneysContract.WaypointEntry.TABLE_NAME,
                columns,
                whereClause, // selection
                whereArgs, // selectionArgs
                null, // groupBy
                null, // having
                null // orderBy
        );



        if(cursor.getCount() <= 0) // No waypoints found having that edgesId
        {
            cursor.close();
            //db.close();
            return 0;
        }
        else
        {
            cursor.moveToFirst();
            count = cursor.getInt( 0 );
            cursor.close();
        }
        //db.close();
        return count;
    }




}
