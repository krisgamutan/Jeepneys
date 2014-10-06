package net.krisg.riseabove.jeepneys.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by KrisEmmanuel on 9/22/2014.
 */
public class JeepneysContract {
    public static final String CONTENT_AUTHORITY = "net.krisg.riseabove.jeepneys";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ROUTE = "route";
    public static final String PATH_VERTEX = "vertex";
    public static final String PATH_LOCATION = "location";
    public static final String PATH_LOCATIONCATEGORY = "locationcategory";
    public static final String PATH_EDGE = "edge";
    public static final String PATH_JEEPNEY = "jeepney";
    public static final String PATH_WAYPOINT = "waypoint";
    public static final String PATH_PATH = "path";



    public static final class RouteEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ROUTE).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_ROUTE;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_ROUTE;
        public static final String TABLE_NAME = "route";

        public static final String COLUMN_ROUTENUMBER = "routeNumber";

        public static Uri buildRouteUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }
    public static final class VertexEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VERTEX).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_VERTEX;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_VERTEX;
        public static final String TABLE_NAME = "vertex";

        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";

        public static Uri buildPointUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
    public static final class LocationEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String TABLE_NAME = "location";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_VERTEX_IDVERTEX = "Vertex_idVertex";

        public static final String COLUMN_LOCATIONCATEGORY_IDLOCATIONCATEGORY = "LocationCategory_idLocationCategory";

        public static final String COLUMN_PHOTO1 = "photo1";
        public static final String COLUMN_PHOTO2 = "photo2";
        public static final String COLUMN_PHOTO3 = "photo3";
        public static final String COLUMN_DESCRIPTION = "description";

        public static Uri buildLocationUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
    public static final class LocationCategoryEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATIONCATEGORY).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_LOCATIONCATEGORY;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_LOCATIONCATEGORY;
        public static final String TABLE_NAME = "locationcategory";

        public static final String COLUMN_NAME = "name";

        public static Uri buildLocationUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }



    public static final class EdgeEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_EDGE).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_EDGE;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_EDGE;
        public static final String TABLE_NAME = "edge";

        public static final String COLUMN_VERTEX_IDVERTEXTARGET = "Vertex_idVertexTarget";
        public static final String COLUMN_VERTEX_IDVERTEXSOURCE = "Vertex_idVertexSource";
        public static final String COLUMN_DISTANCE = "distance";

        public static Uri buildVectorUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


    public static final class JeepneyEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_JEEPNEY).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_JEEPNEY;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_JEEPNEY;
        public static final String TABLE_NAME = "jeepney";

        public static final String COLUMN_PLATENUMBER = "platenumber";
        public static final String COLUMN_ROUTE_IDROUTE = "Route_idRoute";

        public static Uri buildJeepneyUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }
    public static final class WaypointEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WAYPOINT).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_WAYPOINT;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_WAYPOINT;
        public static final String TABLE_NAME = "waypoint";

        public static final String COLUMN_EDGEINDEX = "edgeIndex";
        public static final String COLUMN_EDGE_IDEDGE = "Edge_idEdge";

        public static Uri buildWaypointUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
    public static final class PathEntry implements BaseColumns
    {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PATH).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_PATH;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_PATH;
        public static final String TABLE_NAME = "path";

        public static final String COLUMN_ROUTE_IDROUTE = "Route_idRoute";
        public static final String COLUMN_WAYPOINT_IDWAYPOINT = "Waypoint_idWaypoint";
        public static final String COLUMN_DESCRIPTION = "description";

        public static Uri buildPathUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }



}
