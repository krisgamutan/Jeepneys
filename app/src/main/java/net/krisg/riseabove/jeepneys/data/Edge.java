package net.krisg.riseabove.jeepneys.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.math.BigDecimal;

/**
 * Created by KrisEmmanuel on 9/29/2014.
 */
public class Edge {

    private long id;
    private Vertex target;
    private Vertex source;
    private BigDecimal distance;

    private static final String LOG_TAG = Edge.class.getSimpleName();


    public Edge(long id, Vertex target, Vertex source, BigDecimal distance) {
        this.id = id;
        this.target = target;
        this.source = source;
        this.distance = distance;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Vertex getTarget() {
        return target;
    }

    public void setTarget(Vertex target) {
        this.target = target;
    }

    public Vertex getSource() {
        return source;
    }

    public void setSource(Vertex source) {
        this.source = source;
    }

    public BigDecimal getDistance() {
        return distance;
    }

    public void setDistance(BigDecimal distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge)) return false;

        Edge edge = (Edge) o;

        if (!distance.equals(edge.distance)) return false;
        if (!source.equals(edge.source)) return false;
        if (!target.equals(edge.target)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = target.hashCode();
        result = 31 * result + source.hashCode();
        result = 31 * result + distance.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "id=" + id +
                ", target=" + target +
                ", source=" + source +
                ", distance=" + distance +
                '}';
    }

    public static ContentValues makeContentValues(double distance, Vertex source, Vertex target)
    {
        ContentValues values = new ContentValues();

        values.put(JeepneysContract.EdgeEntry.COLUMN_DISTANCE, Double.toString(distance));
        values.put(JeepneysContract.EdgeEntry.COLUMN_VERTEX_IDVERTEXSOURCE, source.getId() );
        values.put(JeepneysContract.EdgeEntry.COLUMN_VERTEX_IDVERTEXTARGET, target.getId() );

        return values;
    }
    public static ContentValues makeContentValues(double distance, long sourceId, long targetId)
    {
        ContentValues values = new ContentValues();

        values.put(JeepneysContract.EdgeEntry.COLUMN_DISTANCE, Double.toString(distance));
        values.put(JeepneysContract.EdgeEntry.COLUMN_VERTEX_IDVERTEXSOURCE, sourceId );
        values.put(JeepneysContract.EdgeEntry.COLUMN_VERTEX_IDVERTEXTARGET, targetId );

        return values;
    }

    public static boolean isEdgeExists(SQLiteDatabase db, long sourceVertexId, long targetVertexId)
    {
        String[] columns = {
                JeepneysContract.EdgeEntry._ID
        };

        String whereClause = JeepneysContract.EdgeEntry.COLUMN_VERTEX_IDVERTEXSOURCE + "=? AND " +
                JeepneysContract.EdgeEntry.COLUMN_VERTEX_IDVERTEXTARGET + "=?";

        String[] whereArgs = new String[] {
                Long.toString(sourceVertexId),
                Long.toString(targetVertexId)
        };

        Cursor cursor = db.query(
                JeepneysContract.EdgeEntry.TABLE_NAME,
                columns,
                whereClause, // selection
                whereArgs, // selectionArgs
                null, // groupBy
                null, // having
                null // orderBy
        );
        if(cursor.getCount() > 0)
        {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }
    public static long getEdgeId(SQLiteDatabase db, long sourceId, long targetId)
    {

        String[] columns = {
                JeepneysContract.EdgeEntry._ID
        };

        String whereClause = JeepneysContract.EdgeEntry.COLUMN_VERTEX_IDVERTEXSOURCE + "=? AND " + JeepneysContract.EdgeEntry.COLUMN_VERTEX_IDVERTEXTARGET + "=?";

        String[] whereArgs = new String[] {
                Long.toString(sourceId),
                Long.toString(targetId)
        };

        Cursor cursor = db.query(
                JeepneysContract.EdgeEntry.TABLE_NAME,
                columns,
                whereClause, // selection
                whereArgs, // selectionArgs
                null, // groupBy
                null, // having
                null // orderBy
        );
        long id = -2;
        if(cursor.getCount() <= 0)
        {
            cursor.close();
            return -3;
        }
        if(cursor.moveToFirst()) // this assumes only unique vertex id per lat lng
        {
            id = cursor.getLong( cursor.getColumnIndex(JeepneysContract.EdgeEntry._ID));
            cursor.close();
            return id;
        }
        cursor.close();
        return -4;
    }
    public static Edge getEdge(SQLiteDatabase db, long id)
    {


        Edge edge = null;
        String[] columns = {
                JeepneysContract.EdgeEntry._ID,
                JeepneysContract.EdgeEntry.COLUMN_DISTANCE,
                JeepneysContract.EdgeEntry.COLUMN_VERTEX_IDVERTEXTARGET,
                JeepneysContract.EdgeEntry.COLUMN_VERTEX_IDVERTEXSOURCE
        };

        String whereClause = JeepneysContract.EdgeEntry._ID + "=?";

        String[] whereArgs = new String[] {
                Long.toString(id)
        };

        Cursor cursor = db.query(
                JeepneysContract.EdgeEntry.TABLE_NAME,
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
            long tEdgeId = cursor.getLong( cursor.getColumnIndex(JeepneysContract.EdgeEntry._ID));
            BigDecimal tEdgeDistance = new BigDecimal(cursor.getString( cursor.getColumnIndex(JeepneysContract.EdgeEntry.COLUMN_DISTANCE)));
            long tEdgeTargetVertextId = cursor.getLong( cursor.getColumnIndex(JeepneysContract.EdgeEntry.COLUMN_VERTEX_IDVERTEXTARGET));
            long tEdgeSourceVertexId = cursor.getLong(cursor.getColumnIndex(JeepneysContract.EdgeEntry.COLUMN_VERTEX_IDVERTEXSOURCE));

            Vertex tTargetVertex = Vertex.getVertex(db, tEdgeTargetVertextId);
            Vertex tSourceVertex = Vertex.getVertex(db, tEdgeSourceVertexId);

            if(tTargetVertex == null)
            {
                Log.d(LOG_TAG, "tTargetVertex is null, wont create new Edge");
                return null;
            }
            if(tSourceVertex == null)
            {
                Log.d(LOG_TAG, "tSourceVertex is null, wont create new Edge");
                return null;
            }
            edge = new Edge(tEdgeId,tTargetVertex,tSourceVertex,tEdgeDistance);

            cursor.close();
            return edge;
        }
        cursor.close();

        return edge;
    }

    public static long makeEdge()
    {
        long newRowId = -1;

        return newRowId;
    }


}
