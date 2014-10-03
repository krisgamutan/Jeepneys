package net.krisg.riseabove.jeepneys.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.maps.model.LatLng;

import java.math.BigDecimal;

/**
 * Created by KrisEmmanuel on 9/26/2014.
 */
public class Vertex {
    private long id;
    private BigDecimal latitude;
    private BigDecimal longitude;

    private static final String LOG_TAG = Vertex.class.getSimpleName();


    public Vertex(long id, BigDecimal latitude, BigDecimal longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vertex)) return false;

        Vertex vertex = (Vertex) o;

        if (!latitude.equals(vertex.latitude)) return false;
        if (!longitude.equals(vertex.longitude)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = latitude.hashCode();
        result = 31 * result + longitude.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    public static ContentValues makeContentValues(String lat,String lng)
    {
        ContentValues values = new ContentValues();
        values.put(JeepneysContract.VertexEntry.COLUMN_LATITUDE, lat);
        values.put(JeepneysContract.VertexEntry.COLUMN_LONGITUDE, lng);
        return values;
    }

    public LatLng getPosition()
    {
        LatLng position = new LatLng(latitude.doubleValue(), longitude.doubleValue());
        return position;
    }

    public static boolean isVertexCoordinateUnique(SQLiteDatabase db, String lat, String lng)
    {
        String sql;


        String[] columns = {
                "COUNT(" + JeepneysContract.VertexEntry._ID + ")"
        };

        String whereClause = JeepneysContract.VertexEntry.COLUMN_LATITUDE + "=? AND " + JeepneysContract.VertexEntry.COLUMN_LONGITUDE + "=?";

        String[] whereArgs = new String[] {
                lat,
                lng
        };

        Cursor cursor = db.query(
                JeepneysContract.VertexEntry.TABLE_NAME,
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
            return true;
        }
        cursor.close();
        return false;
    }
    public static boolean isVertexIdExists(SQLiteDatabase db, long id)
    {
        String sql;
        String[] args = { Long.toString(id)};

        sql = "SELECT * FROM " + JeepneysContract.VertexEntry.TABLE_NAME + " WHERE " +
                JeepneysContract.VertexEntry._ID + "=?";

        Cursor cursor = db.rawQuery(sql,args);
        if(cursor.getCount() > 0)
        {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public static long getVertexId(SQLiteDatabase db, LatLng latlng)
    {

        return getVertexId(db,    Double.toString(latlng.latitude), Double.toString(latlng.longitude));
    }

    public static long getVertexId(SQLiteDatabase db, String lat, String lng)
    {

        // Test: Id: 5
        // Lat: 10.29984542494113
        // Lng: 123.89739844948053

        String[] columns = {
                JeepneysContract.VertexEntry._ID
        };

        String whereClause = JeepneysContract.VertexEntry.COLUMN_LATITUDE + "=? AND " + JeepneysContract.VertexEntry.COLUMN_LONGITUDE + "=?";

        String[] whereArgs = new String[] {
                lat,
                lng
        };

        Cursor cursor = db.query(
                JeepneysContract.VertexEntry.TABLE_NAME,
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
        if(cursor.moveToFirst()) // this assumes only unique vertex id per lat lng
        {
            id = cursor.getLong( cursor.getColumnIndex(JeepneysContract.VertexEntry._ID) );
            cursor.close();
            return id;
        }
        cursor.close();
        return -1;

    }
    public static String[] queryId(SQLiteDatabase db, long id)
    {
        // returns Lat and Lng, in String[0]..[1]
        String[] columns = {
                JeepneysContract.VertexEntry.COLUMN_LATITUDE,
                JeepneysContract.VertexEntry.COLUMN_LONGITUDE
        };

        String whereClause = JeepneysContract.VertexEntry._ID + "=?";

        String[] whereArgs = new String[] {
                Long.toString(id)
        };

        Cursor cursor = db.query(
                JeepneysContract.VertexEntry.TABLE_NAME,
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

            String[] result = new String[]{
                    Double.toString(cursor.getDouble( 0 ))
                    ,Double.toString(cursor.getDouble( 1 ))

            };
            cursor.close();
            return result;
        }
        cursor.close();
        return null;
    }
    public static Vertex getVertex(SQLiteDatabase db, long id)
    {
        Vertex vertex = null;
        String[] columns = {
                JeepneysContract.VertexEntry._ID,
                JeepneysContract.VertexEntry.COLUMN_LATITUDE,
                JeepneysContract.VertexEntry.COLUMN_LONGITUDE
        };

        String whereClause = JeepneysContract.VertexEntry._ID + "=?";

        String[] whereArgs = new String[] {
                Long.toString(id)
        };

        Cursor cursor = db.query(
                JeepneysContract.VertexEntry.TABLE_NAME,
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
            vertex = new Vertex(
                    cursor.getLong( cursor.getColumnIndex(JeepneysContract.VertexEntry._ID)),
                    new BigDecimal(cursor.getString( cursor.getColumnIndex( JeepneysContract.VertexEntry.COLUMN_LATITUDE))),
                    new BigDecimal(cursor.getString( cursor.getColumnIndex( JeepneysContract.VertexEntry.COLUMN_LONGITUDE)))
                    );

            cursor.close();
            return vertex;
        }
        cursor.close();

        return vertex;
    }

}
