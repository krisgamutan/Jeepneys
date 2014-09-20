package net.krisg.riseabove.jeepneys;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import net.krisg.riseabove.jeepneys.utilities.GooglePlayServiceUtility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener

{
    boolean mapPlottingMode = false;
    ArrayList<Marker> plottingMarkers = new ArrayList<Marker>();
    Polyline plottingLine;
    private double plottingTotalDist = 0.0;

    private void drawPlottingLineGuide()
    {

        if(plottingLine != null)
        {
            plottingLine.remove();
            plottingLine = null;

        }

        PolylineOptions options = new PolylineOptions()
                .width(3)
                .color(Color.BLUE);
        if(plottingMarkers.size() > 1)
        {
            for(int i=0; i < plottingMarkers.size(); i++)
            {
                options.add(plottingMarkers.get(i).getPosition());
            }
            plottingLine = mMap.addPolyline(options);

        }

    }
    private void calcPlottingTotalDistance()
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
                    plottingTotalDist += Mapper.calculationByDistance(prevLatlng, plottingMarkers.get(i).getPosition());

                }
                //prevLat = plottingMarkers.get(i).getPosition().latitude;
                //prevLong = plottingMarkers.get(i).getPosition().longitude;
                prevLatlng = plottingMarkers.get(i).getPosition();
            }
        }

    }
    private void clearPlotting()
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
    private void addPlottingMarker(LatLng latLng)
    {
        MarkerOptions markerOptions = new MarkerOptions()
                //.title("Origin Marker")
                .position(latLng)
                //.draggable(true)
                //.anchor(.5f,.5f)
                //.snippet("This is your origin marker")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_red_pin))
                ;



        plottingMarkers.add(mMap.addMarker(markerOptions));

        drawPlottingLineGuide();
        calcPlottingTotalDistance();
        plottingStatusText();

    }
    private void plottingStatusText()
    {

        String str = "#Markers:" + plottingMarkers.size() + " TotalDist:" + plottingTotalDist + "km";

        TextView plottingStatusText = (TextView) findViewById(R.id.tv_plottingStatusText);
        plottingStatusText.setText(str);
    }
    public void plottingUndoLastMarker(View view)
    {
        if(plottingMarkers.size() > 0)
        {
            plottingMarkers.get(plottingMarkers.size() - 1).remove();
            plottingMarkers.remove(plottingMarkers.size() - 1);
            drawPlottingLineGuide();
            plottingStatusText();
            calcPlottingTotalDistance();
        }

    }
    private void removePlottingMarker(Marker marker)
    {
        plottingMarkers.remove((Marker)marker);
    }


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private static final int GPS_ERRORDIALOG_REQUEST = 9001;

    LocationClient mLocationClient;

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
                });

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
                        if(mapPlottingMode)
                        {
                            addPlottingMarker(latLng);

                        }
                        else
                        {
                            MapsActivity.this.setDestinationMarker(latLng.latitude,latLng.longitude);
                        }


                        // FOR TESTING
                        String toastString = "Lat:" + latLng.latitude + ", Long:" + latLng.longitude;

                        Toast.makeText(MapsActivity.this, toastString, Toast.LENGTH_SHORT).show();


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
                        if(!mapPlottingMode)
                        {
                            MapsActivity.this.setOriginMarker(latLng.latitude, latLng.longitude);
                        }


                        // FOR TESTING
                        String toastString = "Lat:" + latLng.latitude + ", Long:" + latLng.longitude;

                    }
                });

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
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
    }
    private void setOriginMarker(double lat, double lng)
    {
        if(originMarker != null)
        {
            originMarker.remove();
            originMarker = null;
        }

        MarkerOptions originMarkerOptions = new MarkerOptions()
                .title("Origin Marker")
                .position(new LatLng(lat, lng))
                .draggable(true)
                .anchor(.5f,.5f)
                .snippet("This is your origin marker")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.person_blueicon))
                ;
        originMarker = mMap.addMarker(originMarkerOptions);

        if(destinationMarker != null)
        {
            drawOriginToDestinationLine();
        }

    }
    private void setDestinationMarker(double lat, double lng)
    {
        if(destinationMarker != null)
        {
            destinationMarker.remove();
            destinationMarker = null;
        }

        MarkerOptions destinationMarkerOptions = new MarkerOptions()
                .title("Destination Marker")
                .position(new LatLng(lat, lng))
                .draggable(true)
                .anchor(.5f,.5f)
                .snippet("This is your destination marker")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.target_green))
                ;
        destinationMarker = mMap.addMarker(destinationMarkerOptions);

        if(originMarker != null)
        {
            drawOriginToDestinationLine();
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
                .color(Color.RED)
                .width(9)
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
        String msg = "Location: " + location.getLatitude() + "," + location.getLongitude();

    }

    public void onTogglePlottingClicked(View view)
    {
        View layerPlotting = (View) findViewById(R.id.layer_plotting);

        boolean on = ((ToggleButton) view).isChecked();
        if(on)
        {
            mapPlottingMode = true;
            layerPlotting.setVisibility(View.VISIBLE);
            clearOriginAndDestinationMarkers();
        }
        else
        {
            mapPlottingMode = false;
            layerPlotting.setVisibility(View.GONE);
            clearPlotting();
        }
    }

    public void debugStatus(String str)
    {
        TextView debugText = (TextView) findViewById(R.id.tv_debugText);
        debugText.setText(str);
    }

}
