package net.krisg.riseabove.jeepneys;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import net.krisg.riseabove.jeepneys.data.DEdge;
import net.krisg.riseabove.jeepneys.data.DVertex;
import net.krisg.riseabove.jeepneys.data.Edge;
import net.krisg.riseabove.jeepneys.data.JeepneysContract;
import net.krisg.riseabove.jeepneys.data.JeepneysDbHelper;
import net.krisg.riseabove.jeepneys.data.Path;
import net.krisg.riseabove.jeepneys.data.Route;
import net.krisg.riseabove.jeepneys.data.Vertex;
import net.krisg.riseabove.jeepneys.data.Waypoint;
import net.krisg.riseabove.jeepneys.utilities.GooglePlayServiceUtility;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MapsActivity extends FragmentActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener
{
    private static final String LOG_TAG = MapsActivity.class.getSimpleName();


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private static final int GPS_ERRORDIALOG_REQUEST = 9001;

    LocationClient mLocationClient;
    private boolean autodetectOrigin = true;
    private LatLng lastLocation = null; // use in onLocationChange to minimize refreshing of originMarker

    Marker marker; // misc marker used to mark searched result location
    Marker originMarker; // used for the origin location
    Marker destinationMarker; // used for the destination location
    Polyline originToDestinationLine;

    @SuppressWarnings("unused")
    private static final double
            LOC_CITU_LAT = 10.295264,
            LOC_CITU_LNG = 123.880506;
    private static final float MAP_DEFAULTZOOM = 11;
    private static final float MAP_PREFEREDZOOM = 15;

    boolean editRoutesMode = false;
    boolean editVertexMode = false;

    Circle originCircle;
    Circle destinationCircle;
    private static final double defaultFindRadius = 150.0;
    private double originRadius = defaultFindRadius;
    private double destinationRadius = defaultFindRadius; // meters
    private static final double radiusIncrement = 10.0;

    ArrayList<Marker> mapVertexMarkers = new ArrayList<Marker>();
    int mapVertexMarkers_indexNearestToOrigin = -1;
    int mapVertexMarkers_indexNearestToDestination = -1;

    ArrayList<Marker> plottingMarkers = new ArrayList<Marker>();
    Polyline plottingLine;
    private double plottingTotalDist = 0.0;


    ArrayList<Marker> editVertexNewMarkers = new ArrayList<Marker>();

    int ico_mapVertex = R.drawable.ic_circleadd2;
    int ico_addVertex = R.drawable.ic_circleadd;
    int ico_addRoute = R.drawable.silvergreen_marker_icon;

    int ico_originMarker = R.drawable.ic_grayuser;
    int ico_originMarkerAutodetectLocation = R.drawable.ic_blueball;

    ArrayList<DVertex> mapDVertices = new ArrayList<DVertex>();

    ArrayList<Marker> calcPathMarkers = new ArrayList<Marker>();
    Polyline calcPathLine;
    List<DVertex> shortPathToDestination = new ArrayList<DVertex>();


    int indexOfOriginInMapDVertices = -1;
    int indexOfDestinationInMapDVertices = -1;

    public void editVertexRefresh(View view)
    {
        readMapVertex(true);
    }

    private void readMapVertex(boolean visible)
    {
        JeepneysDbHelper dbHelper = new JeepneysDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {
                JeepneysContract.VertexEntry._ID,
                JeepneysContract.VertexEntry.COLUMN_LATITUDE,
                JeepneysContract.VertexEntry.COLUMN_LONGITUDE
        };
        Cursor cursor = db.query(
                JeepneysContract.VertexEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null
        );
        double lat,lng;

        //clearMapVertex();
        MarkerOptions markerOptions;

        cursor.moveToFirst();
        while(cursor.isAfterLast() == false)
        {
            lat = cursor.getDouble(cursor.getColumnIndex(JeepneysContract.VertexEntry.COLUMN_LATITUDE));
            lng = cursor.getDouble(cursor.getColumnIndex(JeepneysContract.VertexEntry.COLUMN_LONGITUDE));

            markerOptions = new MarkerOptions()
                    //.title( cursor.getString(cursor.getColumnIndex(JeepneysContract.VertexEntry._ID)) )
                    //.title("Vertex#:" + cursor.getString(cursor.getColumnIndex(JeepneysContract.VertexEntry._ID)) +" lat:" + lat + ",lng:" + lng)
                    .position(new LatLng(lat,lng))
                    .anchor(.5f,.5f)
                    .visible(visible)
                    .icon(BitmapDescriptorFactory.fromResource(ico_mapVertex));

            mapVertexMarkers.add(mMap.addMarker(markerOptions));


            cursor.moveToNext();
        }

        cursor.close();
        db.close();
    }
    private void displayMapPoints()
    {
        for(int i=0; i < mapVertexMarkers.size(); i++)
        {

        }

    }
    private void clearMapVertex()
    {
        if(mapVertexMarkers.size()>0)
        {
            for(int i= mapVertexMarkers.size()-1; i > -1; i--)
            {
                mapVertexMarkers.get(i).remove();
                mapVertexMarkers.remove(i);
            }

        }
    }
    public void editVertexClear(View view)
    {
        clearMapVertex();
    }

    private void addNewVertex(LatLng latLng)
    {
        MarkerOptions markerOptions = new MarkerOptions()
                //.title("Vertex lat:" + latLng.latitude + ",lng:" + latLng.longitude)
                .position(latLng)
                .anchor(.5f,.5f)
                //.infoWindowAnchor(.2f,.3f)
                .icon(BitmapDescriptorFactory.fromResource(ico_addVertex))
                ;
        editVertexNewMarkers.add(mMap.addMarker(markerOptions));
        editVertexStatusText();
    }
    private void clearEditVertex()
    {

        if(editVertexNewMarkers.size()>0)
        {
            for(int i= editVertexNewMarkers.size()-1; i > -1; i--)
            {
                editVertexNewMarkers.get(i).remove();
                editVertexNewMarkers.remove(i);
            }

        }

        editVertexStatusText();
    }
    private void editVertexStatusText()
    {

        String str = "#NewVertices:" + editVertexNewMarkers.size();

        TextView statusText = (TextView) findViewById(R.id.tv_editPointsStatusText);
        statusText.setText(str);
    }
    public void editVertexUndoLastMarker(View view)
    {
        if(editVertexNewMarkers.size() > 0)
        {
            editVertexNewMarkers.get(editVertexNewMarkers.size() - 1).remove();
            editVertexNewMarkers.remove(editVertexNewMarkers.size() - 1);
            editVertexStatusText();
        }

    }
    public void editVertexCommit(View view)
    {
        JeepneysDbHelper dbHelper = new JeepneysDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        ContentValues values;

        long vertexRowId = -1;
        double lat,lng;
        String sql;
        for(int i=0; i< editVertexNewMarkers.size(); i++)
        {
            lat = editVertexNewMarkers.get(i).getPosition().latitude;
            lng = editVertexNewMarkers.get(i).getPosition().longitude;


            if(Vertex.isVertexCoordinateUnique(db, lat, lng)) // Add only if point with latlng is not in the table
            {
                values = Vertex.makeContentValues(lat, lng);
                try
                {
                    vertexRowId = db.insertOrThrow(JeepneysContract.VertexEntry.TABLE_NAME, null, values);
                }catch(SQLException e)
                {
                    Log.d(LOG_TAG, e.getMessage());
                }

            }
            else
            {
                toastMsg("A point on that coordinate already exists.");
            }



        }

        db.close();

    }

    public void editRoutesCommit(View view)
    {
        JeepneysDbHelper dbHelper = new JeepneysDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        // check fields
        EditText editText_routeNumber = (EditText)findViewById(R.id.editTextRouteNumber);
        EditText editText_description = (EditText)findViewById(R.id.editTextPathDescription);

        String routeNumber = editText_routeNumber.getText().toString().trim();
        String pathDescription = editText_description.getText().toString().trim();



        if(routeNumber.isEmpty())
        {
            toastMsg("please fill the route number");
            return;
        }
        if(pathDescription.trim().isEmpty())
        {
            toastMsg("please fill the path description");
            return;
        }
        if(plottingMarkers.size() < 2)
        {
            toastMsg("Error, there is no paths drawn");
            return;
        }


        // Adding of Edge
        double distance;
        long targetVertexId;
        long sourceVertexId;
        ContentValues edgeValues;
        long newEdgeRowId = -1;

        ArrayList<Edge> newWaypointEdges = new ArrayList<Edge>();

        double sourceVertexLat, sourceVertexLng, targetVertexLat, targetVertexLng;
        Vertex sourceVertex, targetVertex;

        for(int i=0; i < plottingMarkers.size() - 1; i++)
        {

            // 1 2
            // 0 1
            //
            distance = Mapper.distVincenty(plottingMarkers.get(i).getPosition(), plottingMarkers.get(i+1).getPosition());

            sourceVertexLat = plottingMarkers.get(i).getPosition().latitude;
            sourceVertexLng = plottingMarkers.get(i).getPosition().longitude;
            targetVertexLat = plottingMarkers.get(i+1).getPosition().latitude;
            targetVertexLng = plottingMarkers.get(i+1).getPosition().longitude;

            // These vertices must already exist.
            sourceVertexId = Vertex.getVertexId(db, sourceVertexLat, sourceVertexLng);
            targetVertexId = Vertex.getVertexId(db, targetVertexLat, targetVertexLng);

            if(Edge.isEdgeExists(db, sourceVertexId, targetVertexId))
            {
                // If edge already exists use existing edge ID
                newEdgeRowId = Edge.getEdgeId(db, sourceVertexId, targetVertexId);
            }
            else
            {
                // create new edge, and use it's new edge ID
                edgeValues = Edge.makeContentValues(distance, sourceVertexId, targetVertexId);
                try
                {
                    newEdgeRowId = db.insertOrThrow(JeepneysContract.EdgeEntry.TABLE_NAME, null, edgeValues);
                }catch(SQLException e)
                {
                    Log.d(LOG_TAG, e.getMessage());
                }
            }




            sourceVertex = new Vertex(sourceVertexId, new BigDecimal(Double.toString(sourceVertexLat)), new BigDecimal(Double.toString(sourceVertexLng)));
            targetVertex = new Vertex(targetVertexId, new BigDecimal(Double.toString(targetVertexLat)), new BigDecimal(Double.toString(targetVertexLng)));

            if(newEdgeRowId != -1)
            {
                newWaypointEdges.add(new Edge(newEdgeRowId, sourceVertex, targetVertex, new BigDecimal(Double.toString(distance))));
            }
        }


        // Adding of Route
        long idRoute = Route.getRouteId(db, routeNumber);

        ContentValues routeValues;
        long newRouteRowId = -1;


        if(idRoute != -1)
        {
            // already exists
            ;
        }
        else
        {
            // register new Route number
            routeValues = Route.makeContentValues(routeNumber);
            try
            {
                newRouteRowId = db.insertOrThrow(JeepneysContract.RouteEntry.TABLE_NAME, null, routeValues);
            }catch(SQLException e)
            {
                Log.d(LOG_TAG, e.getMessage());
            }
        }

        // Adding of Waypoint
        ContentValues waypointValues;

        long testWaypointRowId = -1;


        long newWaypointRowId = Waypoint.getMaxId(db) + 1; // +1 to increment the id, Waypoint table is not auto increment

        Waypoint newWaypoint = new Waypoint(newWaypointRowId, newWaypointEdges);

        for(int i=0; i < newWaypoint.getEdges().size(); i++)
        {
            waypointValues = Waypoint.makeContentValues(newWaypointRowId, i, newWaypoint.getEdges().get(i).getId());
            try
            {
                testWaypointRowId = db.insertOrThrow(JeepneysContract.WaypointEntry.TABLE_NAME, null, waypointValues);

                if(testWaypointRowId != newWaypointRowId)
                {
                    Log.d(LOG_TAG, "testWaypointRowId:" + testWaypointRowId + " and newWaypointRowId:" + newWaypointRowId +" NOT EQUAL!!!");
                }

            }catch(SQLException e)
            {
                Log.d(LOG_TAG, e.getMessage());
            }

        }
        if(testWaypointRowId == -1)
        {
            Log.d(LOG_TAG, "testWaypointRowId is -1");
        }


        String debug1 = "NEW routeId:" + newRouteRowId + " wpId:" + newWaypointRowId  + "desc:" + pathDescription;
        Log.d(LOG_TAG, debug1);

        // Adding of Path
        ContentValues pathValues;
        long newPathRowId = Path.getMaxId(db) + 1; // manual set of ID

        long testNewPathRowId = -1;


        pathValues = Path.makeContentValues(newPathRowId, newRouteRowId, newWaypointRowId, pathDescription);
        try
        {
            testNewPathRowId = db.insertOrThrow(JeepneysContract.PathEntry.TABLE_NAME, null, pathValues);

            if(testNewPathRowId != newPathRowId)
            {
                Log.e(LOG_TAG, "testNewPathRowId IS NOT EQUAL to newPathRowId");

            }

        }catch(SQLException e)
        {
            Log.d(LOG_TAG, e.getMessage());
        }
        if(newPathRowId == -1)
        {
            Log.d(LOG_TAG, "newPathRowId is -1");
        }


        //toastMsg(test);
        db.close();
    }

    private void markCalcPath(List<DVertex> path)
    {
        clearCalcPathMarkers();

        JeepneysDbHelper dbHelper = new JeepneysDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        for(int i=0; i < path.size(); i++)
        {
            String[] latLangString = Vertex.queryId(db, path.get(i).vertexId);
            ;
            LatLng latLng = new LatLng(Double.parseDouble(latLangString[0]), Double.parseDouble(latLangString[1]));

            MarkerOptions markerOptions = new MarkerOptions()
                    //.title("Origin Marker")
                    .position(latLng)
                    .draggable(false)
                    .position(latLng)
                    .anchor(.5f, .5f)
                            //.snippet("This is your origin marker")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_system_red))
                    ;
            calcPathMarkers.add(mMap.addMarker(markerOptions));
        }
    }
    private void clearCalcPathMarkers()
    {
        if(calcPathMarkers.size()>0)
        {
            for(int i= calcPathMarkers.size()-1; i > -1; i--)
            {
                calcPathMarkers.get(i).remove();
                calcPathMarkers.remove(i);
            }
        }
        if(calcPathLine != null)
        {
            calcPathLine.remove();
            calcPathLine = null;

        }
    }

    private void drawCalcPathLineGuide()
    {

        if(calcPathLine != null)
        {
            calcPathLine.remove();
            calcPathLine = null;

        }

        PolylineOptions options = new PolylineOptions()
                .width(7)
                .geodesic(true)
                .color(Color.GREEN);
        if(calcPathMarkers.size() > 1) // needs to be more than 1 marker
        {
            for(int i=0; i < calcPathMarkers.size(); i++)
            {
                options.add(calcPathMarkers.get(i).getPosition());
            }
            calcPathLine = mMap.addPolyline(options);

        }

    }

    private void populate_mapDVertices_vertex()
    {
        JeepneysDbHelper dbHelper = new JeepneysDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {
                JeepneysContract.VertexEntry._ID
        };
        Cursor cursor = db.query(
                JeepneysContract.VertexEntry.TABLE_NAME,
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
            db.close();
            return;
        }

        // populate mapDVertices with the database vertex table, idVertex
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            long vertexId = cursor.getLong(cursor.getColumnIndex(JeepneysContract.VertexEntry._ID));
            DVertex vertex = new DVertex(vertexId);
            if(!mapDVertices.contains(vertex))
            {
                mapDVertices.add(vertex);
            }
        }
        cursor.close();

        db.close();
    }
    private void populate_mapDVertices_adjacencies()
    {
        JeepneysDbHelper dbHelper = new JeepneysDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] columns = {
                JeepneysContract.EdgeEntry.COLUMN_DISTANCE,
                JeepneysContract.EdgeEntry.COLUMN_VERTEX_IDVERTEXTARGET
        };

        String whereClause = JeepneysContract.EdgeEntry.COLUMN_VERTEX_IDVERTEXSOURCE + "=?";

        String[] whereArgs;
        Cursor cursor;

        // populate mapDVertices adjacencies with DEdge's
        for(int i=0; i < mapDVertices.size(); i++)
        {
            whereArgs = new String[] {
                    Long.toString( mapDVertices.get(i).vertexId ) // Where element i is the source, get all it's target
            };
            cursor = db.query(
                    JeepneysContract.EdgeEntry.TABLE_NAME,
                    columns,
                    whereClause, // selection
                    whereArgs, // selectionArgs
                    null, // groupBy
                    null, // having
                    null // orderBy
            );

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                double distance = Double.parseDouble(cursor.getString(cursor.getColumnIndex(JeepneysContract.EdgeEntry.COLUMN_DISTANCE)));
                long targetVertexId = cursor.getLong(cursor.getColumnIndex(JeepneysContract.EdgeEntry.COLUMN_VERTEX_IDVERTEXTARGET));

                // Find from mapDVertices to get DVertex element with vertexId of targetVertexId
                // then put it in the element i adjacencies
                for(int j=0; j < mapDVertices.size(); j++)
                {
                    if(mapDVertices.get(j).vertexId == targetVertexId)
                    {
                        DVertex edgeTarget = mapDVertices.get(j);
                        DEdge newDEdge = new DEdge(edgeTarget, distance);

                        if(!mapDVertices.get(i).adjacencies.contains(newDEdge))
                        {
                            mapDVertices.get(i).adjacencies.add(newDEdge);
                        }
                    }
                }

            }
            cursor.close();
        }
        db.close();
    }

    private Polyline plotWaypoint(long id)
    {
        Polyline wpPolyLine = null;
        JeepneysDbHelper dbHelper = new JeepneysDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Waypoint wp = Waypoint.getWaypoint(db, id);

        if(wp != null)
        {
            PolylineOptions options = new PolylineOptions()
                    .width(4)
                    .color(Color.GREEN);

            //options.add
            options.add(wp.getEdge(0).getSource().getPosition());
            for(int i=0; i < wp.getEdges().size() ; i++)
            {

                //Log.d(LOG_TAG, "edge i:" + i + "id:" +wp.getEdges().get(i).toString() );
                options.add(wp.getEdge(i).getTarget().getPosition()); // adds element i's target.
            }
            wpPolyLine = mMap.addPolyline(options);

        }



        db.close();

        return wpPolyLine;
    }

    private void drawRoutePlottingLineGuide()
    {
        if(plottingLine != null)
        {
            plottingLine.remove();
            plottingLine = null;

        }

        PolylineOptions options = new PolylineOptions()
                .width(3)
                .color(Color.BLUE);
        if(plottingMarkers.size() > 1) // needs to be more than 1 marker
        {
            for(int i=0; i < plottingMarkers.size(); i++)
            {
                options.add(plottingMarkers.get(i).getPosition());
            }
            plottingLine = mMap.addPolyline(options);

        }

    }
    private void calcRoutePlottingTotalDistance()
    {
        plottingTotalDist = 0.0;

        if(plottingMarkers.size() > 0)
        {
            //double prevLat = plottingMarkers.get(0).getPosition().latitude;
            //double prevLong = plottingMarkers.get(0).getPosition().longitude;

            LatLng prevLatlng = plottingMarkers.get(0).getPosition();

            for(int i=0; i < plottingMarkers.size(); i++)
            {
                if(i>0)
                {
                    /*plottingTotalDist += Mapper.calculateDistance(
                            prevLat,
                            prevLong,
                            plottingMarkers.get(i).getPosition().latitude,
                            plottingMarkers.get(i).getPosition().longitude
                    );*/
                    // plottingTotalDist += Mapper.calculationByDistance(prevLatlng, plottingMarkers.get(i).getPosition());
                    plottingTotalDist += Mapper.distVincenty(prevLatlng, plottingMarkers.get(i).getPosition());

                }
                //prevLat = plottingMarkers.get(i).getPosition().latitude;
                //prevLong = plottingMarkers.get(i).getPosition().longitude;
                prevLatlng = plottingMarkers.get(i).getPosition();
            }
        }

    }
    private void clearRoutePlotting()
    {
        plottingTotalDist = 0.0;
        if(plottingMarkers.size()>0)
        {
            for(int i=plottingMarkers.size()-1; i > -1; i--)
            {
                plottingMarkers.get(i).remove();
                plottingMarkers.remove(i);
            }

        }
        if(plottingLine != null)
        {
            plottingLine.remove();
            plottingLine = null;

        }
        plottingStatusText();
    }
    private void addRouteMarker(Marker marker)
    {
        // Not to add new markers that has the same positions as the elements in the list
        if(checkMarkerPositionAlreadyInList(plottingMarkers,marker))
        {
            return;
        }

        MarkerOptions markerOptions = new MarkerOptions()
                //.title("Origin Marker")
                .position(marker.getPosition())
                        //.draggable(true)
                        //.anchor(.5f,.5f)
                        //.snippet("This is your origin marker")
                .icon(BitmapDescriptorFactory.fromResource(ico_addRoute))
                ;

        plottingMarkers.add(mMap.addMarker(markerOptions));

        drawRoutePlottingLineGuide();
        calcRoutePlottingTotalDistance();
        plottingStatusText();
    }
    private boolean checkMarkerPositionAlreadyInList(ArrayList<Marker> markerList, Marker marker)
    {
        for(Marker m: markerList)
        {
            if(m.getPosition().latitude == marker.getPosition().latitude &&
                    m.getPosition().longitude == marker.getPosition().longitude
                    )
            {
                return true;
            }
        }
        return false;
    }

    private void plottingStatusText()
    {

        //String str = "#Markers:" + plottingMarkers.size() + " TotalDist:" + plottingTotalDist + "km";
        String str = "#Markers:" + plottingMarkers.size() + " TotalDist:" + plottingTotalDist + "m";

        TextView plottingStatusText = (TextView) findViewById(R.id.tv_plottingStatusText);
        plottingStatusText.setText(str);
    }
    public void plottingUndoLastMarker(View view)
    {
        if(plottingMarkers.size() > 0)
        {
            plottingMarkers.get(plottingMarkers.size() - 1).remove();
            plottingMarkers.remove(plottingMarkers.size() - 1);
            drawRoutePlottingLineGuide();
            plottingStatusText();
            calcRoutePlottingTotalDistance();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        MapStateManager mgr = new MapStateManager(this);
        mgr.saveMapState(mMap);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        /*if(isGooglePlayServicesOK())
        {
            Toast.makeText(this, "Ready to Map!", Toast.LENGTH_SHORT).show();
            setUpMapIfNeeded();
        }*/
        setUpMapIfNeeded();
    }

    private int highlightNearestVertextWithinMarker(Marker marker, double radius)
    {
        // This returns the index of the nearest marker

        if(mapVertexMarkers == null || mapVertexMarkers.isEmpty() || marker == null)
        {
            return -1;
        }
        int nearestToMarkerIndex = -1;
        double nearestToMarkerDistance = Double.POSITIVE_INFINITY;
        double dist;

        for(int i=0; i< mapVertexMarkers.size(); i++)
        {
            if(Mapper.isInsideRadius(marker.getPosition(), radius, mapVertexMarkers.get(i).getPosition()))
            {
                dist = (Mapper.distVincenty(marker.getPosition(), mapVertexMarkers.get(i).getPosition()));

                // update nearest vertex
                if(nearestToMarkerDistance > dist)
                {
                    nearestToMarkerDistance = dist;
                    nearestToMarkerIndex = i;
                }

            }

        }
        if(nearestToMarkerIndex != -1)
        {
            mapVertexMarkers.get(nearestToMarkerIndex).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cross));
        }
        return nearestToMarkerIndex;
    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        MapStateManager mgr = new MapStateManager(this);


        if(GooglePlayServiceUtility.isPlayServiceAvailable(this))
        {


        }

        CameraPosition position = mgr.getSavedCameraPosition();
        if(position != null)
        {
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
            mMap.moveCamera(update);
        }



        mMap.setMapType(mgr.getSavedMapType());
        restoreMapPreference();
    }

    private void restoreMapPreference()
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int mapType = Integer.parseInt(sharedPref.getString("maptypes", "0"));
        switch (mapType)
        {
            case 0:
            {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            }
            case 1:
            {
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            }
            case 2:
            {
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            }
            case 3:
            {
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            }
            default:
            {
                break;
            }
        }


    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {

        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.getUiSettings().setZoomControlsEnabled(false);
                /*
                // Info window
                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter()
                {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        View v = getLayoutInflater().inflate(R.layout.marker_info_window,null);
                        TextView tvLocality = (TextView)v.findViewById(R.id.tv_locality);
                        TextView tvLat = (TextView)v.findViewById(R.id.tv_lat);
                        TextView tvLng = (TextView)v.findViewById(R.id.tv_lng);
                        TextView tvSnippet = (TextView)v.findViewById(R.id.tv_snippet);

                        LatLng ll = marker.getPosition();


                        tvLocality.setText(marker.getTitle());
                        tvSnippet.setText(marker.getSnippet());


                        tvLat.setText("Latitude: " + ll.latitude);
                        tvLng.setText("Longitude: " + ll.longitude);

                        return v;
                    }
                });*/

                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {

                        /*
                        List<Address> list = null;
                        list = getGeocode(latLng);
                        Address address = list.get(0);



                        String toastString = addressToString(address);
                        Toast.makeText(MapsActivity.this, toastString, Toast.LENGTH_SHORT).show();
                        */

                        if(editVertexMode)
                        {
                            addNewVertex(latLng);
                        }
                        else if(!editVertexMode && !editRoutesMode)
                        {
                            // if mapVertexMarkers isnt in the map, init it but hidden. Used for origin and destination nearest markers
                            if(mapVertexMarkers.isEmpty())
                            {
                                readMapVertex(false);
                            }

                            MapsActivity.this.setDestinationMarker(latLng.latitude,latLng.longitude);
                            // FOR TESTING
                            //String toastString = "Lat:" + latLng.latitude + ", Long:" + latLng.longitude;
                            //Toast.makeText(MapsActivity.this, toastString, Toast.LENGTH_SHORT).show();
                        }




                    }
                });

                mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {

                        /*List<Address> list = null;
                        list = getGeocode(latLng);
                        Address address = list.get(0);
                        String toastString = addressToString(address);
                        Toast.makeText(MapsActivity.this, toastString, Toast.LENGTH_SHORT).show();
                        */
                        if(!editRoutesMode && !editVertexMode )
                        {
                            // if mapVertexMarkers isnt in the map, init it but hidden. Used for origin and destination nearest markers
                            if(mapVertexMarkers.isEmpty())
                            {
                                readMapVertex(false);
                            }

                            MapsActivity.this.setOriginMarker(latLng.latitude, latLng.longitude);
                            // FOR TESTING
                            //String toastString = "Lat:" + latLng.latitude + ", Long:" + latLng.longitude;
                        }
                    }
                });

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        if(editRoutesMode)
                        {
                            addRouteMarker(marker);

                            marker.hideInfoWindow();
                            return true;

                        }
                        return false;
                    }
                });

                mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {

                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {

                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {

                        LatLng latLng = marker.getPosition();
                        /*List<Address> list = null;

                        list = getGeocode(latLng);

                        Address address = list.get(0);

                        String toastString = addressToString(address);

                        //marker.setTitle(address.getLocality());
                        //marker.setSnippet(address.getCountryName());


                        Toast.makeText(MapsActivity.this, toastString, Toast.LENGTH_SHORT).show();*/

                        marker.showInfoWindow();

                        // FOR TESTING
                        String toastString = "Lat:" + latLng.latitude + ", Long:" + latLng.longitude;


                        // Update Origin to Destination Line guide
                        if(originMarker!= null && destinationMarker != null)
                        {
                            drawOriginToDestinationLine();
                        }

                    }
                });
                setUpMap();
            }
        }
    }


    private List<Address> getGeocode(LatLng latLng)
    {
        Geocoder gc = new Geocoder(MapsActivity.this);
        List<Address> list = null;

        try {
            list = gc.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    private void clearOriginAndDestinationMarkers()
    {
        if(originMarker != null)
        {
            originMarker.remove();
            originMarker = null;
        }
        if(destinationMarker != null)
        {
            destinationMarker.remove();
            destinationMarker = null;
        }
        if(originToDestinationLine != null)
        {
            originToDestinationLine.remove();
            originToDestinationLine = null;
        }
        if(originCircle != null)
        {
            originCircle.remove();
            originCircle = null;
        }
        if(destinationCircle != null)
        {
            destinationCircle.remove();
            destinationCircle = null;
        }
    }

    private Circle drawCircle(LatLng latLng,double radius,int fillColor,int strokeColor,int strokeWidth)
    {
        CircleOptions circleOptions = new CircleOptions()
                .center(latLng)
                .radius(radius)
                .fillColor(fillColor)
                .strokeColor(strokeColor)
                .strokeWidth(strokeWidth)
                ;
        return mMap.addCircle(circleOptions);
    }

    private void setOriginMarker(double lat, double lng)
    {
        LatLng latLng = new LatLng(lat,lng);


        if(originMarker != null)
        {
            originMarker.remove();
            originMarker = null;
        }

        MarkerOptions originMarkerOptions = new MarkerOptions()
                .title("Origin Marker")
                .position(latLng)
                .draggable(true)
                .anchor(.5f,.5f)
                .snippet("This is your origin marker")
                .icon(BitmapDescriptorFactory.fromResource((autodetectOrigin)?ico_originMarkerAutodetectLocation:ico_originMarker))
                ;
        originMarker = mMap.addMarker(originMarkerOptions);

        originRadius = defaultFindRadius; // reset to default find radius
        do {

            if(originCircle != null)
            {
                originCircle.remove();
                originCircle = null;
            }
            originCircle = drawCircle(latLng,originRadius,0x330000FF,0x660000FF,2);


            // to index the mapVertexMarkers for the nearest marker to origin
            mapVertexMarkers_indexNearestToOrigin = highlightNearestVertextWithinMarker(originMarker, originRadius);

            if(mapVertexMarkers_indexNearestToOrigin != -1)
            {
                break;
            }
            else
            {
                originRadius += radiusIncrement; // increment the find radius if no path is found within the radius
            }
        }while(mapVertexMarkers_indexNearestToOrigin == -1);


        if(destinationMarker != null)
        {
            drawOriginToDestinationLine();

            clearCalcPathMarkers();
            calcPaths_OriginToDestination();
        }
    }
    private void setDestinationMarker(double lat, double lng)
    {
        LatLng latLng = new LatLng(lat,lng);

        if(destinationMarker != null)
        {
            destinationMarker.remove();
            destinationMarker = null;
        }

        MarkerOptions destinationMarkerOptions = new MarkerOptions()
                .title("Destination Marker")
                .position(latLng)
                .draggable(true)
                .anchor(.5f,.5f)
                .snippet("This is your destination marker")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.target_green))
                ;
        destinationMarker = mMap.addMarker(destinationMarkerOptions);


        destinationRadius = defaultFindRadius; // reset to default find radius
        do {
            if(destinationCircle != null)
            {
                destinationCircle.remove();
                destinationCircle = null;
            }

            destinationCircle = drawCircle(latLng,destinationRadius,0x33FF0000,0x66FF0000,2);

            // to index the mapVertexMarkers for the nearest marker to destination
            mapVertexMarkers_indexNearestToDestination = highlightNearestVertextWithinMarker(destinationMarker, destinationRadius);

            if(mapVertexMarkers_indexNearestToDestination != -1)
            {
                break;
            }
            else
            {
                destinationRadius += radiusIncrement; // increment the find radius if no path is found within the radius
            }
        }while(mapVertexMarkers_indexNearestToDestination == -1);

        if(originMarker != null)
        {
            drawOriginToDestinationLine();

            clearCalcPathMarkers();
            calcPaths_OriginToDestination();

        }

    }
    private void drawOriginToDestinationLine()
    {
        if(originToDestinationLine != null)
        {
            originToDestinationLine.remove();
            originToDestinationLine = null;
        }
        PolylineOptions options = new PolylineOptions()
                .add(originMarker.getPosition())
                .add(destinationMarker.getPosition())
                .color(Color.BLUE)
                .visible(true)
                .width(3)
                ;
        originToDestinationLine = mMap.addPolyline(options);
    }

    /**
     * Converts Address multi-line address into one-line string.
     * @param address
     * @return
     */
    private String addressToString(Address address)
    {
        String addressString = "";
        if(address.getAddressLine(0) != null)
        {
            for(int i=0; i<address.getMaxAddressLineIndex(); i++)
            {
                addressString += address.getAddressLine(i) + ".";

            }
        }
        return addressString;
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        gotoLocation(LOC_CITU_LAT, LOC_CITU_LNG, MAP_DEFAULTZOOM);
        mMap.setMyLocationEnabled(true);

        // Get current location programatically
        mLocationClient = new LocationClient(this,this,this);
        mLocationClient.connect(); // public void onConnected(Bundle bundle) if successful
    }

    public boolean isGooglePlayServicesOK()
    {
        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(isAvailable == ConnectionResult.SUCCESS)
        {
            return true;
        }
        else if (GooglePlayServicesUtil.isUserRecoverableError((isAvailable)))
        {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, this, GPS_ERRORDIALOG_REQUEST);
            dialog.show();
        }
        else
        {
            Toast.makeText(this, "Can't connect to Google Play services", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    private void gotoLocation(double lat,double lng)
    {
        LatLng latLng = new LatLng(lat,lng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
        mMap.moveCamera(cameraUpdate);
    }
    private void gotoLocation(double lat,double lng, float zoom)
    {
        LatLng latLng = new LatLng(lat,lng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.moveCamera(cameraUpdate);
    }
    private void gotoLocationAnimateCamera(double lat, double lng, float zoom)
    {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng), zoom) );

    }

    public void geoLocate(View view) throws IOException
    {
        hideSoftKeyboard(view);
        EditText editTextLocation = (EditText) findViewById(R.id.editTextLocation);
        String location = editTextLocation.getText().toString();

        if(location.length() == 0)
        {
            Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show();
            return;
        }

        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(location, 1); // only 1 result
        Address address = list.get(0); // get first result

        String locality = address.getLocality();

        double lat = address.getLatitude();
        double lng = address.getLongitude();

        //gotoLocation(lat,lng,MAP_PREFEREDZOOM);
        gotoLocationAnimateCamera(lat, lng,MAP_PREFEREDZOOM);
        //setMarker(locality,addressToString(address), lat,lng);
        setDestinationMarker(lat,lng);


        //Toast.makeText(this, locality, Toast.LENGTH_LONG).show();

    }


    private void setMarker(String locality, String snippetString, double lat, double lng)
    {
        if(marker != null)
        {
            marker.remove();
        }
        MarkerOptions markerOptions = new MarkerOptions()
                .title(locality)
                .position(new LatLng(lat, lng))
                //.draggable(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.greenflag_icon))
                //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
                ;
        if(snippetString.length() > 0)
        {
            markerOptions.snippet(snippetString);
        }

        marker = mMap.addMarker(markerOptions);
    }

    private void hideSoftKeyboard(View view)
    {
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    protected void gotoCurrentLocation()
    {
        Location currentLocation = mLocationClient.getLastLocation();
        if(currentLocation == null)
        {
            Toast.makeText(this, "Current location isn't available",Toast.LENGTH_SHORT).show();

        }
        else
        {
            LatLng ll = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, MAP_DEFAULTZOOM);
            mMap.animateCamera(update);
        }
    }

    // GooglePlayServicesClient.ConnectionCallbacks
    @Override
    public void onConnected(Bundle bundle) {
        // connected to location service
        LocationRequest request = LocationRequest.create();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int locationUpdateInterval = Integer.parseInt(sharedPref.getString("locationRequestInterval", "5000"));
        request.setInterval(locationUpdateInterval);
        request.setFastestInterval(1000);

        int locationRequestAccuracy = Integer.parseInt(sharedPref.getString("locationRequestAccuracy", "5000"));
        switch (locationRequestAccuracy)
        {
            case 0:
            {
                request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                break;
            }
            case 1:
            {
                request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                break;
            }
            case 2:
            {
                request.setPriority(LocationRequest.PRIORITY_LOW_POWER);
                break;
            }
            case 3:
            {
                request.setPriority(LocationRequest.PRIORITY_NO_POWER);
                break;
            }
            default:
            {
                break;
            }
        }


        mLocationClient.requestLocationUpdates(request,this);

    }

    @Override
    public void onDisconnected() {

    }
    //GooglePlayServicesClient.OnConnectionFailedListener
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    //com.google.android.gms.location.LocationListener;
    @Override
    public void onLocationChanged(Location location) {

        if(lastLocation == null || lastLocation.latitude != location.getLatitude() ||
                lastLocation.longitude != location.getLongitude()
                )
        {
            lastLocation = new LatLng(location.getLatitude(), location.getLongitude());
            updateCurrentLocation(location);
        }
    }
    private void updateCurrentLocation(Location location)
    {
        String currLocationText = "Your GPS location: " + location.getLatitude() + "N," + location.getLongitude() + "E";

        TextView tv_currLocation = (TextView)findViewById(R.id.tv_currLocation);
        tv_currLocation.setText(currLocationText);

        if(autodetectOrigin && !editRoutesMode && !editVertexMode)
        {
            // if mapVertexMarkers isnt in the map, init it but hidden. Used for origin and destination nearest markers
            if(mapVertexMarkers.isEmpty())
            {
                readMapVertex(false);
            }

            MapsActivity.this.setOriginMarker(location.getLatitude(), location.getLongitude());
        }
    }


    public void onToggleEditVertexClicked(View view)
    {
        boolean on = ((ToggleButton) view).isChecked();
        if(on)
        {
            editVertexPanelEnable(true);
            editRoutesPanelEnable(false);
            ((ToggleButton) findViewById(R.id.toggle_editRoutes)).setChecked(false);
        }
        else
        {
            editVertexPanelEnable(false);
        }
    }

    public void onToggleEditRoutesClicked(View view)
    {
        boolean on = ((ToggleButton) view).isChecked();
        if(on)
        {
            editRoutesPanelEnable(true);
            editVertexPanelEnable(false);
            ((ToggleButton) findViewById(R.id.toggle_editVertex)).setChecked(false);
        }
        else
        {
            editRoutesPanelEnable(false);
        }
    }
    private void editVertexPanelEnable(boolean visibility)
    {
        View layerEditVertex = (View) findViewById(R.id.panel_editVertices);
        if(visibility)
        {
            editVertexMode = true;
            layerEditVertex.setVisibility(View.VISIBLE);
            clearOriginAndDestinationMarkers();

            clearMapVertex();
            readMapVertex(true);
        }
        else
        {
            clearEditVertex();

            editVertexMode = false;
            layerEditVertex.setVisibility(View.GONE);
        }
    }

    private void editRoutesPanelEnable(boolean visibility)
    {
        View layerEditRoutes = (View) findViewById(R.id.panel_editRoutes);
        if(visibility)
        {
            editRoutesMode = true;
            layerEditRoutes.setVisibility(View.VISIBLE);
            clearOriginAndDestinationMarkers();

            clearMapVertex();
            readMapVertex(true);
        }
        else
        {


            editRoutesMode = false;
            layerEditRoutes.setVisibility(View.GONE);
            clearRoutePlotting();
        }

    }

    public void setDebugText(String str)
    {
        TextView debugText = (TextView) findViewById(R.id.tv_debugText);
        debugText.setText(str);
    }

    public void toastMsg(String msg)
    {
        Toast.makeText(MapsActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
    public void theTestButton(View view)
    {
        JeepneysDbHelper dbHelper = new JeepneysDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //plotWaypoint(1);

        //Edge testEdge = Edge.getEdge(db, 1);
        //toastMsg(" " + testEdge.toString() );

        //ArrayList<Long> wpId = Waypoint.waypointIdThatHaveEdges(db, 10);

        //ArrayList<Edge> eList = Edge.convertToEdges(db, shortPathToDestination);

        //ArrayList<Path> pList = Path.pathsThatHaveWaypointId(db, 1);

        // int count = Waypoint.waypointCountEdgeId(db, 1, 2);

        //int edgeIndex = Waypoint.getIndexOfEdge(db, 3, 15);



        updateRouteToTake_details1();

        //toastMsg( "test:" + pathListInShortestPath() );


        db.close();
    }

    private void updateRouteToTake_details1()
    {
        JeepneysDbHelper dbHelper = new JeepneysDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int numberOfRoutesToTakeToDisplay = 5;

        ArrayList<Path> plistInShortestPath = pathListInShortestPath();

        String details1text = "Take Routes: ";
        String lastRoute = "";

        for(int i=0; numberOfRoutesToTakeToDisplay > 0 && i<plistInShortestPath.size(); i++)
        {
            String routeNumber = Route.getRoute(db,plistInShortestPath.get(i).getIdRoute()).getRouteNumber();

            if(routeNumber.equals(lastRoute))
            {
                ; //
            }
            else
            {
                lastRoute = routeNumber;
                details1text +=  routeNumber + "->" ;

                numberOfRoutesToTakeToDisplay--;
            }


        }

        updateDetails1(details1text);
        db.close();
    }

    public void calcPaths_OriginToDestination()
    {
        if(originMarker == null || destinationMarker == null ||
                mapVertexMarkers_indexNearestToOrigin == -1 ||
                mapVertexMarkers_indexNearestToDestination == -1
                )
        {
            return;
        }

        JeepneysDbHelper dbHelper = new JeepneysDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        populate_mapDVertices_vertex();
        populate_mapDVertices_adjacencies();


        // the nearest to origin and the nearest to destination
        long originMarker_vertexId = Vertex.getVertexId(db, mapVertexMarkers.get(mapVertexMarkers_indexNearestToOrigin).getPosition());
        long destinationMarker_vertexId = Vertex.getVertexId(db, mapVertexMarkers.get(mapVertexMarkers_indexNearestToDestination).getPosition());

        int debugFound = 0; // must not exceed 2! 1 for origin and 1 for destination
        boolean isOriginVertexFound = false;
        boolean isDestinationVertexFound = false;

        indexOfOriginInMapDVertices = -1;
        indexOfDestinationInMapDVertices = -1;

        for(int i=0; i<mapDVertices.size();i++)
        {
            if(mapDVertices.get(i).vertexId == originMarker_vertexId)
            {
                isOriginVertexFound = true;
                indexOfOriginInMapDVertices = i;
                debugFound++;
            }
            if(mapDVertices.get(i).vertexId == destinationMarker_vertexId)
            {
                isDestinationVertexFound = true;
                indexOfDestinationInMapDVertices = i;
                debugFound++;
            }

        }
        Log.d(LOG_TAG, "debugFound:" + debugFound);

        if(indexOfOriginInMapDVertices != -1)
        {
            Dijkstra.computePaths(mapDVertices.get(indexOfOriginInMapDVertices)); // Origin
        }
        else
        {
            Log.e(LOG_TAG, "indexOfOriginInMapDVertices is -1");
        }

        if(indexOfDestinationInMapDVertices != -1)
        {
            shortPathToDestination = Dijkstra.getShortestPathTo(mapDVertices.get(indexOfDestinationInMapDVertices));
            markCalcPath(shortPathToDestination);
            drawCalcPathLineGuide();

            Log.d(LOG_TAG,"Distance : " + mapDVertices.get(indexOfDestinationInMapDVertices).minDistance);
            Log.d(LOG_TAG, "Path: " + shortPathToDestination);


            //String details1text = "Path: " + shortPathToDestination;

            String details2text = "Distance (m) : " + mapDVertices.get(indexOfDestinationInMapDVertices).minDistance;

            //updateDetails1(details1text);

            updateDetails2(details2text);

            //updateRouteToTake_details1();

        }
        else
        {
            Log.e(LOG_TAG, "indexOfDestinationInMapDVertices is -1");

        }



        /* for (DVertex v : mapDVertices)
        {
            //System.out.println("\nDistance to " + v + ": " + v.minDistance);
            Log.d(LOG_TAG,"Distance to " + v + ": " + v.minDistance);

            List<DVertex> path = Dijkstra.getShortestPathTo(v);
            //System.out.println("Path: " + path);


            Log.d(LOG_TAG, "Path: " + path);
        } */

        db.close();
    }

    private ArrayList<Path> pathListInShortestPath()
    {
        /*
        1. convert shortestPath vertices to edges
        2. query all waypoints that has the edge i
        3.      query all paths that has waypoint j
        4.          find which path is the longest within the "shortest path" and use it
         */

        // This will return the Path to be travelled by
        ArrayList<Path> pathListOfShortestPath = new ArrayList<Path>();

        JeepneysDbHelper dbHelper = new JeepneysDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Convert to Edges
        ArrayList<Edge> edgeList = Edge.convertToEdges(db, shortPathToDestination);


        if(edgeList.size() <= 0)
        {
            Log.e(LOG_TAG, "edgeIdList is <= 0");

        }
        else
        {
            for(int i=0; i<edgeList.size(); i++ )
            {
                // Waypoints that have this edge i
                ArrayList<Waypoint> waypointList = Waypoint.waypointThatHaveEdges(db, edgeList.get(i).getId());

                for(int j=0; j < waypointList.size(); j++)
                {

                    // List of Paths that uses this waypoint
                    ArrayList<Path> pathList = Path.pathsThatHaveWaypointId(db, waypointList.get(j).getId() );


                    for(int k=0; k < pathList.size(); k++)
                    {
                        // temp list path to determine which path is longest (having traverse more edges) in the shortestPathToDestination
                        ArrayList<WeightedPath> weightedPaths = new ArrayList<WeightedPath>();
                        weightedPaths.add(new WeightedPath(0, pathList.get(k) )); // put in WeightedPath


                        Waypoint waypointOf_kInList = Waypoint.getWaypoint(db, pathList.get(k).getIdWaypoint() );

                        int edgeIndexOfEdgeInList = Waypoint.getIndexOfEdge(
                                db,
                                pathList.get(k).getIdWaypoint(),
                                edgeList.get(i).getId()
                        );


                        for(int m=0;
                            m < 1
                            //(edgeIndexOfEdgeInList + m) < Waypoint.countEdges(db, edgeList.get( i + m ).getId())// Waypoint.countEdges(db, waypointOf_kInList.getId())
                                ;
                                m++) //&& (i+m) < edgeList.size()
                        {

                            if(
                                    waypointOf_kInList.getEdge( edgeIndexOfEdgeInList + m ).getId()
                                    ==
                                    edgeList.get( i + m ).getId())
                            {
                                weightedPaths.get(k).incrementWeight();

                            }
                            else
                            {
                                break;
                            }
                        }
                        Collections.reverse(weightedPaths);

                        i += weightedPaths.get(0).getWeight() - 1 ;

                        pathListOfShortestPath.add(weightedPaths.get(0).getPath());

                        Log.d(LOG_TAG, "" + weightedPaths);
                    }




                }

            }
        }


        db.close();
        return pathListOfShortestPath;
    }




    class WeightedPath implements Comparator<WeightedPath>, Comparable<WeightedPath>
    {
        int weight;
        Path path;

        WeightedPath(int weight, Path path) {
            this.weight = weight;
            this.path = path;
        }

        public void incrementWeight()
        {
            this.weight++;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public Path getPath() {
            return path;
        }

        public void setPath(Path path) {
            this.path = path;
        }

        @Override
        public int compareTo(WeightedPath another) {
            return this.weight - another.weight;
        }

        @Override
        public int compare(WeightedPath lhs, WeightedPath rhs) {
            return lhs.weight - rhs.weight;
        }

        @Override
        public String toString() {
            return "WeightedPath{" +
                    "weight=" + weight +
                    ", path=" + path +
                    '}';
        }
    }



    private void updateDetails1(String text)
    {
        TextView details1Text = (TextView)findViewById(R.id.tv_details1);
        details1Text.setText(text);
    }
    private void updateDetails2(String text)
    {
        TextView details2Text = (TextView)findViewById(R.id.tv_details2);
        details2Text.setText(text);
    }

    public void onToggleAutodetectOriginClicked(View view)
    {
        boolean on = ((ToggleButton) view).isChecked();

        if(on)
        {
            autodetectOrigin = true;

            updateAppMessageText("");

            if(originMarker!=null)
            {
                originMarker.setIcon(BitmapDescriptorFactory.fromResource(ico_originMarkerAutodetectLocation));
            }

        }
        else
        {
            autodetectOrigin = false;
            updateAppMessageText("Origin is set to manual. Longclick the map to manually set your origin.");
            if(originMarker!=null)
            {
                originMarker.setIcon(BitmapDescriptorFactory.fromResource(ico_originMarker));
            }
        }
    }

    private void updateAppMessageText(String text)
    {
        TextView appMessageText = (TextView)findViewById(R.id.tv_appMessage);
        appMessageText.setText(text);
    }
}
