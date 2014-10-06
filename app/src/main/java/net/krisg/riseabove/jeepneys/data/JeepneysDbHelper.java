package net.krisg.riseabove.jeepneys.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.krisg.riseabove.jeepneys.data.JeepneysContract.JeepneyEntry;
import net.krisg.riseabove.jeepneys.data.JeepneysContract.LocationCategoryEntry;
import net.krisg.riseabove.jeepneys.data.JeepneysContract.LocationEntry;
import net.krisg.riseabove.jeepneys.data.JeepneysContract.PathEntry;
import net.krisg.riseabove.jeepneys.data.JeepneysContract.VertexEntry;
import net.krisg.riseabove.jeepneys.data.JeepneysContract.RouteEntry;
import net.krisg.riseabove.jeepneys.data.JeepneysContract.EdgeEntry;
import net.krisg.riseabove.jeepneys.data.JeepneysContract.WaypointEntry;



/**
 * Created by KrisEmmanuel on 9/22/2014.
 */
public class JeepneysDbHelper extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "jeepneys.db";
    public JeepneysDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_ROUTE_TABLE =
                "CREATE TABLE " + RouteEntry.TABLE_NAME + "(" +
                        RouteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," +
                        RouteEntry.COLUMN_ROUTENUMBER + " TEXT NOT NULL" +
                        ");";

        final String SQL_CREATE_VERTEX_TABLE =
                "CREATE TABLE " + VertexEntry.TABLE_NAME + "(" +
                        VertexEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," +
                        VertexEntry.COLUMN_LATITUDE + " TEXT NOT NULL," +
                        VertexEntry.COLUMN_LONGITUDE + " TEXT NOT NULL" +
                        ");";


        final String SQL_CREATE_EDGE_TABLE =
                "CREATE TABLE " + EdgeEntry.TABLE_NAME + "(" +
                        EdgeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        EdgeEntry.COLUMN_DISTANCE + " TEXT," +
                        EdgeEntry.COLUMN_VERTEX_IDVERTEXTARGET + " INTEGER NOT NULL ," +
                        EdgeEntry.COLUMN_VERTEX_IDVERTEXSOURCE + " INTEGER NOT NULL ," +
                        //CONSTRAINT "fk_Edge_Vertex1"
                        "FOREIGN KEY(" + EdgeEntry.COLUMN_VERTEX_IDVERTEXTARGET + ")" +
                        "REFERENCES " + VertexEntry.TABLE_NAME + "(" + VertexEntry._ID + ")," +
                        //CONSTRAINT "fk_Edge_Vertex2"
                        "FOREIGN KEY(" + EdgeEntry.COLUMN_VERTEX_IDVERTEXSOURCE + ")" +
                        "REFERENCES " + VertexEntry.TABLE_NAME + "(" + VertexEntry._ID + ")" +
                        ");";


        final String SQL_CREATE_LOCATIONCATEGORY_TABLE =
                "CREATE TABLE " + LocationCategoryEntry.TABLE_NAME + "(" +
                        LocationCategoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        LocationCategoryEntry.COLUMN_NAME + " TEXT" +
                        ");";


        final String SQL_CREATE_JEEPNEY_TABLE =
                "CREATE TABLE " + JeepneyEntry.TABLE_NAME + "(" +
                        JeepneyEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," +
                        JeepneyEntry.COLUMN_PLATENUMBER + " TEXT," +
                        JeepneyEntry.COLUMN_ROUTE_IDROUTE + " INTEGER NOT NULL ," +
                        "FOREIGN KEY(" + JeepneyEntry.COLUMN_ROUTE_IDROUTE + ")" +
                        "REFERENCES " + RouteEntry.TABLE_NAME + "(" + RouteEntry._ID + ")" +
                        ");";

        final String SQL_CREATE_WAYPOINT_TABLE =
                "CREATE TABLE " + WaypointEntry.TABLE_NAME + "(" +
                        WaypointEntry._ID + " INTEGER NOT NULL ," +
                        WaypointEntry.COLUMN_EDGEINDEX + " INTEGER NOT NULL," +
                        WaypointEntry.COLUMN_EDGE_IDEDGE + " INTEGER NOT NULL," +
                        //CONSTRAINT "fk_Waypoint_Vector1"
                        "FOREIGN KEY(" + WaypointEntry.COLUMN_EDGE_IDEDGE + ")" +
                        "REFERENCES " + EdgeEntry.TABLE_NAME + "(" + EdgeEntry._ID + ")" +
                        ");";


        final String SQL_CREATE_PATH_TABLE =
                "CREATE TABLE " + PathEntry.TABLE_NAME + "(" +
                PathEntry._ID + " INTEGER NOT NULL ," +
                PathEntry.COLUMN_ROUTE_IDROUTE + " INTEGER NOT NULL ," +
                PathEntry.COLUMN_WAYPOINT_IDWAYPOINT + " INTEGER NOT NULL ," +
                PathEntry.COLUMN_DESCRIPTION + " TEXT," +
                "PRIMARY KEY(" + PathEntry._ID + "," + PathEntry.COLUMN_ROUTE_IDROUTE + "," + PathEntry.COLUMN_WAYPOINT_IDWAYPOINT + ")," +
                "FOREIGN KEY(" + PathEntry.COLUMN_ROUTE_IDROUTE + ")" +
                "REFERENCES " + RouteEntry.TABLE_NAME + "(" + RouteEntry._ID + ")," +
                "FOREIGN KEY(" + PathEntry.COLUMN_WAYPOINT_IDWAYPOINT + ")" +
                "REFERENCES " + WaypointEntry.TABLE_NAME + "(" + WaypointEntry._ID + ")" +
                ");";



        final String SQL_CREATE_LOCATION_TABLE =
                "CREATE TABLE " + LocationEntry.TABLE_NAME + "(" +
                        LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        LocationEntry.COLUMN_NAME + " TEXT NOT NULL," +
                        LocationEntry.COLUMN_VERTEX_IDVERTEX + " INTEGER NOT NULL ," +
                        LocationEntry.COLUMN_LOCATIONCATEGORY_IDLOCATIONCATEGORY + " INTEGER NOT NULL," +
                        LocationEntry.COLUMN_PHOTO1 + " BLOB," +
                        LocationEntry.COLUMN_PHOTO2 + " BLOB," +
                        LocationEntry.COLUMN_PHOTO3 + " BLOB," +
                        LocationEntry.COLUMN_DESCRIPTION + " TEXT," +
                        "FOREIGN KEY(" + LocationEntry.COLUMN_VERTEX_IDVERTEX + ")" +
                        "REFERENCES " + VertexEntry.TABLE_NAME + "(" + VertexEntry._ID + ")," +
                        "FOREIGN KEY(" + LocationEntry.COLUMN_LOCATIONCATEGORY_IDLOCATIONCATEGORY + ")" +
                        "REFERENCES " + LocationCategoryEntry.TABLE_NAME + "(" + LocationCategoryEntry._ID + ")" +
                        ");";



        db.execSQL(SQL_CREATE_ROUTE_TABLE);
        db.execSQL(SQL_CREATE_VERTEX_TABLE);
        db.execSQL(SQL_CREATE_EDGE_TABLE);
        db.execSQL(SQL_CREATE_LOCATIONCATEGORY_TABLE);
        db.execSQL(SQL_CREATE_JEEPNEY_TABLE);
        db.execSQL(SQL_CREATE_WAYPOINT_TABLE);
        db.execSQL(SQL_CREATE_PATH_TABLE);
        db.execSQL(SQL_CREATE_LOCATION_TABLE);



        

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RouteEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + VertexEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EdgeEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LocationCategoryEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + JeepneyEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WaypointEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PathEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME);
        onCreate(db);
    }

}
