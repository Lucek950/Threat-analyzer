package com.example.admin.inzynierka;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener; //wtf
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class  MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMarkerDragListener, GoogleApiClient.ConnectionCallbacks,
        LocationListener{

    private static final String TAG = "MapsActivity";
    private static final float DEFAULT_ZOOM = 17f;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    int PROXIMITY_RADIUS = 10000;
    double latitude, longitude;
    double end_latitude, end_longitude;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLocationPermissionsGranted = false;

    DatabaseReference zagrozeniaDbReference;
    private Marker marker;
    FloatingActionButton btn_GPS, btn_zagrozenia, b_info;
    TextView tv_szerokosc,tv_dlugosc;
    Dialog myDialog;
    WyswietlZagrozenia wyswietlZagrozenia;
    float[] markerKolorEdit = {300.0F,300.0F,310.0F,320.0F,330.0F,340.0F};
    String[] sTOPNIE_ZAGROZENIA = {"","BARDZO NISKIE", "NISKIE","ŚREDNIE","WYSOKIE","BARDZO WYSOKIE" };

    public void initVariable(){
        btn_GPS = (FloatingActionButton) findViewById(R.id.b_location);
        b_info = (FloatingActionButton) findViewById(R.id.b_info);
        btn_zagrozenia = (FloatingActionButton) findViewById(R.id.btn_zagrozenia);
        tv_szerokosc = (TextView) findViewById(R.id.view_szerokosc);
        tv_dlugosc = (TextView) findViewById(R.id.view_dlugosc);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        initVariable();
        getLocationPermission();

        if (!CheckGooglePlayServices()) {
            Log.d(TAG, "Finishing test case since Google Play Services are not available");
            finish();
        } else Log.d(TAG,"Google Play Services available.");
    }

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result, 0).show();
            }return false;
        }return true;
    }

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if(mMap != null){
           mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
               @Override
               public View getInfoWindow(Marker marker) {
                   return null;
               }

               @Override
               public View getInfoContents(Marker marker) {
                   View v = getLayoutInflater().inflate(R.layout.info_window,null);

                   TextView titleMarker = (TextView) v.findViewById(R.id.tv_title);
                   TextView snippetMarker = (TextView) v.findViewById(R.id.tv_snippet);
                   TextView latiMarker = (TextView) v.findViewById(R.id.tv_lati);
                   TextView langMarker = (TextView) v.findViewById(R.id.tv_lang);

                   LatLng ll = marker.getPosition();

                   titleMarker.setText(marker.getTitle());
                   snippetMarker.setText(marker.getSnippet());
                   latiMarker.setText("Szerokość: "+ll.latitude);
                   langMarker.setText("Długość: "+ll.longitude);
                   return v;
               }
           });
        }

        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
            else{
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            init();
        }
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMarkerClickListener(this);
    }

    protected synchronized void buildGoogleApiClient(){
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();
    }

    private void init(){
        btn_GPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
            }
        });

        final boolean[] filtr = {true};

        btn_zagrozenia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(filtr[0] ==true) {
                    myDialog = new Dialog(MapsActivity.this);
                    myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    myDialog.setContentView(R.layout.content_typy);
                    myDialog.getWindow();
                    myDialog.show();
                }
                else
                    filtr[0] = true;
            }
        });

        btn_zagrozenia.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                wyswietlZagrozenia = new WyswietlZagrozenia(marker,zagrozeniaDbReference,mMap);
                mMap.clear();
                wyswietlZagrozenia.napasc();
                wyswietlZagrozenia.kradziez();
                wyswietlZagrozenia.wlamanie();
                wyswietlZagrozenia.rozboje();
                wyswietlZagrozenia.inne();
                filtr[0] = false;
                return false;
            }
        });
        wyswietlZagrozenia = new WyswietlZagrozenia(marker,zagrozeniaDbReference,mMap);
        wyswietlZagrozenia.napasc();
        wyswietlZagrozenia.kradziez();
        wyswietlZagrozenia.wlamanie();
        wyswietlZagrozenia.rozboje();
        wyswietlZagrozenia.inne();
        wyswietlZagrozenia.policja();
        dodajZagrozenia();
        infoMarker();
    }

    private void infoMarker(){
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            int stopien;
            String typ;
            String opis;
            @Override
            public void onInfoWindowClick(final Marker marker) {
                final String infoTitle = marker.getTitle();
                final LatLng latlng = marker.getPosition();
                String infoID = latlng.latitude + " " + latlng.longitude;
                infoID = infoID.replace(".", ",");

                if(infoTitle.equals("Policja")) {
                    Toast.makeText(MapsActivity.this, "UPS, tego miejsca nie edytujesz", Toast.LENGTH_LONG).show();
                }
                else{
                    zagrozeniaDbReference = FirebaseDatabase.getInstance().getReference(infoTitle).child(infoID);
                    zagrozeniaDbReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Zagrozenia zagrozenia = dataSnapshot.getValue(Zagrozenia.class);
                            stopien = zagrozenia.getZagrozeniaStopien();
                            typ = zagrozenia.getZagrozeniaTYP();
                            opis = zagrozenia.getZagrozeniaOpis();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                    final View v = getLayoutInflater().inflate(R.layout.alert_stopien, null);
                    builder.setView(v);

                    final String finalInfoID = infoID;
                    builder.setPositiveButton("Nie, nie jest...", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (stopien >= 5) stopien = 5;
                            else stopien++;

                            zagrozeniaDbReference = FirebaseDatabase.getInstance().getReference(infoTitle).child(finalInfoID).child("zagrozeniaStopien");
                            zagrozeniaDbReference.setValue(stopien);
                            marker.remove();
                            mMap.addMarker(new MarkerOptions().position(latlng).title(typ).snippet("Zagrożenie:\n    " + sTOPNIE_ZAGROZENIA[stopien] + "\nOpis:\n    " + opis)).setIcon(BitmapDescriptorFactory.defaultMarker(markerKolorEdit[stopien]));
                        }
                    });
                    builder.setNegativeButton("Tak, jest bezpiecznie...", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (stopien <= 1) zagrozeniaDbReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    zagrozeniaDbReference = FirebaseDatabase.getInstance().getReference(infoTitle).child(finalInfoID);
                                    zagrozeniaDbReference.setValue(null);
                                    marker.remove();
                                }
                            });
                            else {
                                stopien--;
                                zagrozeniaDbReference = FirebaseDatabase.getInstance().getReference(infoTitle).child(finalInfoID).child("zagrozeniaStopien");
                                zagrozeniaDbReference.setValue(stopien);
                                marker.remove();
                                mMap.addMarker(new MarkerOptions().position(latlng).title(typ).snippet("Zagrożenie:\n    " + sTOPNIE_ZAGROZENIA[stopien] + "\nOpis:\n    " + opis)).setIcon(BitmapDescriptorFactory.defaultMarker(markerKolorEdit[stopien]));
                            }
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });
    }

    private void dodajZagrozenia(){
        mMap.setOnMapLongClickListener(
                new GoogleMap.OnMapLongClickListener() {
                    public void onMapLongClick(final LatLng latlng) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                        final View v = getLayoutInflater().inflate(R.layout.dialog_dodaj_zagrozenia, null);
                        builder.setTitle("Dodajesz zagrożenie");
                        builder.setView(v);
                        builder.setCancelable(false);

                        final Spinner spinnerTypy = (Spinner) v.findViewById(R.id.typyZagrozenia);
                        ArrayAdapter<String> adapterTypy = new ArrayAdapter<String>(MapsActivity.this,
                                android.R.layout.simple_spinner_item,
                                getResources().getStringArray(R.array.listaTypow));
                        adapterTypy.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerTypy.setAdapter(adapterTypy);

                        final Spinner spinnerStopien = (Spinner) v.findViewById(R.id.stopienZagrozenia);
                        ArrayAdapter<String> adapterStopien = new ArrayAdapter<String>(MapsActivity.this,
                                android.R.layout.simple_spinner_item,
                                getResources().getStringArray(R.array.stopienieZagrozenia));
                        adapterStopien.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerStopien.setAdapter(adapterStopien);

                        final EditText editText_ZagrozeniaOpis = (EditText) v.findViewById(R.id.editText_ZagrozeniaOpis);

                        builder.setPositiveButton("Dodaj", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!spinnerTypy.getSelectedItem().toString().equalsIgnoreCase("Wybierz typ przestepstwa")) {
                                    if (!spinnerStopien.getSelectedItem().toString().equalsIgnoreCase("Wybierz stopień zagrożenia")) {

                                        String zagrozeniaTYP = spinnerTypy.getSelectedItem().toString().trim();
                                        String stopien = spinnerStopien.getSelectedItem().toString().trim();
                                        int zagrozeniaStopien = 1;
                                        switch (stopien){
                                            case "bardzo niski":
                                                zagrozeniaStopien = 1;
                                                break;
                                            case "niski":
                                                zagrozeniaStopien = 2;
                                                break;
                                            case "średni":
                                                zagrozeniaStopien = 3;
                                                break;
                                            case "wysoki":
                                                zagrozeniaStopien = 4;
                                                break;
                                            case "bardzo wysoki":
                                                zagrozeniaStopien = 5;
                                                break;
                                        }

                                        String zagrozeniaOpis = editText_ZagrozeniaOpis.getText().toString().trim();

                                        if(!zagrozeniaTYP.equalsIgnoreCase("Inne")){

                                            zagrozeniaDbReference = FirebaseDatabase.getInstance().getReference().child(zagrozeniaTYP);
                                            MarkerOptions markerOptions = new MarkerOptions();
                                            markerOptions.position(latlng);
                                            markerOptions.title(zagrozeniaTYP);
                                            markerOptions.snippet("Zagrożenie:\n    "+sTOPNIE_ZAGROZENIA[zagrozeniaStopien]+"\nOpis:\n    "+zagrozeniaOpis);
                                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(markerKolorEdit[zagrozeniaStopien]));
                                            mMap.addMarker(markerOptions);
                                            String position = latlng.latitude + " " + latlng.longitude;

                                            String id = position.replace(".", ",");
                                            Zagrozenia zagrozenia = new Zagrozenia(id, zagrozeniaTYP, zagrozeniaOpis, latlng.latitude, latlng.longitude, zagrozeniaStopien);
                                            zagrozeniaDbReference.child(id).setValue(zagrozenia);
                                        } else{
                                            if(!TextUtils.isEmpty(zagrozeniaOpis)){
                                                zagrozeniaDbReference = FirebaseDatabase.getInstance().getReference().child(zagrozeniaTYP);
                                                MarkerOptions markerOptions = new MarkerOptions();
                                                markerOptions.position(latlng);
                                                markerOptions.title(zagrozeniaTYP);
                                                markerOptions.snippet("Zagrożenie:\n    "+sTOPNIE_ZAGROZENIA[zagrozeniaStopien]+"\nOpis:\n    "+zagrozeniaOpis);
                                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(markerKolorEdit[zagrozeniaStopien]));
                                                mMap.addMarker(markerOptions);
                                                String position = latlng.latitude + " " + latlng.longitude;

                                                String id = position.replace(".", ",");
                                                Zagrozenia zagrozenia = new Zagrozenia(id, zagrozeniaTYP, zagrozeniaOpis, latlng.latitude, latlng.longitude, zagrozeniaStopien);
                                                zagrozeniaDbReference.child(id).setValue(zagrozenia);
                                            } else Toast.makeText(MapsActivity.this, "Podaj opis dla "+zagrozeniaTYP+"!!!", Toast.LENGTH_LONG).show();
                                        }
                                    } else Toast.makeText(MapsActivity.this, "Podano niepoprawne dane", Toast.LENGTH_LONG).show();
                                } else Toast.makeText(MapsActivity.this, "Podano niepoprawne dane", Toast.LENGTH_LONG).show();
                            }
                        });
                        builder.setNegativeButton("Powrót", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    public void onClick(View v)
    {
        wyswietlZagrozenia = new WyswietlZagrozenia(marker,zagrozeniaDbReference,mMap);
        TextView viewInfo = (TextView) findViewById(R.id.view_Info);
        switch(v.getId())
        {
            case R.id.btn_1:
                viewInfo.setText("Napaść");
                mMap.clear();
                wyswietlZagrozenia.napasc();
                myDialog.dismiss();
                break;

            case R.id.btn_2:
                viewInfo.setText("Rozboje");
                mMap.clear();
                wyswietlZagrozenia.rozboje();
                myDialog.dismiss();
                break;

            case R.id.btn_3:
                viewInfo.setText("Kradzieże");
                mMap.clear();
                wyswietlZagrozenia.kradziez();
                myDialog.dismiss();
                break;

            case R.id.btn_4:
                viewInfo.setText("Włamania");
                mMap.clear();
                wyswietlZagrozenia.wlamanie();
                myDialog.dismiss();
                break;

            case R.id.btn_5:
                viewInfo.setText("Inne zagrożenia lub informacje");
                mMap.clear();
                wyswietlZagrozenia.inne();
                myDialog.dismiss();
                break;

            case R.id.btn_policja:
                viewInfo.setText("Przestępstwa zebrane od policji");
                mMap.clear();
                wyswietlZagrozenia.policja();
                break;

            case R.id.b_info:
                Intent intent = new Intent(MapsActivity.this, InstrukcjaActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    public void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            Location locationA = new Location("A");
                            LatLng latLng;
                            if (currentLocation == null) {
                                latLng = new LatLng(50.8660773, 20.6285676);
                                Toast.makeText(MapsActivity.this, "Sprawdź czy lokalizacja jest włączona po więcej informacji wybierz '?'", Toast.LENGTH_SHORT).show();
                                tv_szerokosc.setText("         Szerokość: bład");
                                tv_dlugosc.setText("         Dlugość: błąd");
                            }
                            else {
                                latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                String szerokosc = "         Szerokość: " + currentLocation.getLatitude();
                                String dlugosc = "         Dlugość: " + currentLocation.getLongitude();
                                tv_szerokosc.setText(szerokosc);
                                tv_dlugosc.setText(dlugosc);
                            }
                            moveCamera(latLng, DEFAULT_ZOOM, "My Location");
                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { //onRequestPermissionsResultYES
        switch(requestCode){
            case MY_PERMISSIONS_REQUEST_LOCATION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        if(client==null){
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                    initMap();
                }
                else Toast.makeText(this, " Odmowa zgody! ",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        end_latitude = marker.getPosition().latitude;
        end_longitude =  marker.getPosition().longitude;

        Log.d(TAG,""+end_latitude);
        Log.d(TAG,""+end_longitude);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();

        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }
}