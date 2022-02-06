package com.example.admin.inzynierka;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WyswietlZagrozenia {
    public Marker marker;
    public DatabaseReference zagrozeniaDbReference;
    public GoogleMap mMap;
    String[] sTOPNIE_ZAGROZENIA = {"","BARDZO NISKIE", "NISKIE","ŚREDNIE","WYSOKIE","BARDZO WYSOKIE" };
    
    public WyswietlZagrozenia(Marker marker, DatabaseReference zagrozeniaDbReference, GoogleMap mMap){
        this.marker=marker;
        this.zagrozeniaDbReference=zagrozeniaDbReference;
        this.mMap=mMap;
    }

    public void napasc(){
        zagrozeniaDbReference=FirebaseDatabase.getInstance().getReference().child("Napaść");
        zagrozeniaDbReference.push().setValue(marker);
        zagrozeniaDbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot a : dataSnapshot.getChildren()) {
                    Zagrozenia zagrozenia = a.getValue(Zagrozenia.class);
                    LatLng location = new LatLng(zagrozenia.szerokosc, zagrozenia.dlugosc);
                    float[] markerKolor = {290.0F,290.0F,280.0F,270.0F,260.0F,250.0F};
                    mMap.addMarker(new MarkerOptions().position(location).title(zagrozenia.zagrozeniaTYP).snippet("Zagrożenie:\n    "+sTOPNIE_ZAGROZENIA[zagrozenia.zagrozeniaStopien]+"\nOpis:\n    "+zagrozenia.zagrozeniaOpis)).
                            setIcon(BitmapDescriptorFactory.defaultMarker(markerKolor[zagrozenia.zagrozeniaStopien]));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void rozboje(){
        zagrozeniaDbReference=FirebaseDatabase.getInstance().getReference().child("Rozboje");
        zagrozeniaDbReference.push().setValue(marker);
        zagrozeniaDbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot a : dataSnapshot.getChildren()) {
                    Zagrozenia zagrozenia = a.getValue(Zagrozenia.class);
                    LatLng location = new LatLng(zagrozenia.szerokosc, zagrozenia.dlugosc);
                    float[] markerKolor = {200.0F,200.0F,210.0F,220.0F,230.0F,240.0F};
                    mMap.addMarker(new MarkerOptions().position(location).title(zagrozenia.zagrozeniaTYP).snippet("Zagrożenie:\n    "+sTOPNIE_ZAGROZENIA[zagrozenia.zagrozeniaStopien]+"\nOpis:\n    "+zagrozenia.zagrozeniaOpis)).
                            setIcon(BitmapDescriptorFactory.defaultMarker(markerKolor[zagrozenia.zagrozeniaStopien]));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void kradziez(){
        zagrozeniaDbReference=FirebaseDatabase.getInstance().getReference().child("Kradzież");
        zagrozeniaDbReference.push().setValue(marker);
        zagrozeniaDbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot a : dataSnapshot.getChildren()) {
                    Zagrozenia zagrozenia = a.getValue(Zagrozenia.class);
                    LatLng location = new LatLng(zagrozenia.szerokosc, zagrozenia.dlugosc);
                    float[] markerKolor = {180.0F,180.0F,170.0F,160.0F,150.0F,140.0F};
                    mMap.addMarker(new MarkerOptions().position(location).title(zagrozenia.zagrozeniaTYP).snippet("Zagrożenie:\n    "+sTOPNIE_ZAGROZENIA[zagrozenia.zagrozeniaStopien]+"\nOpis:\n    "+zagrozenia.zagrozeniaOpis)).
                            setIcon(BitmapDescriptorFactory.defaultMarker(markerKolor[zagrozenia.zagrozeniaStopien]));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void wlamanie(){
        zagrozeniaDbReference=FirebaseDatabase.getInstance().getReference().child("Włamanie");
        zagrozeniaDbReference.push().setValue(marker);
        zagrozeniaDbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot a : dataSnapshot.getChildren()) {
                    Zagrozenia zagrozenia = a.getValue(Zagrozenia.class);
                    LatLng location = new LatLng(zagrozenia.szerokosc, zagrozenia.dlugosc);
                    float[] markerKolor = {60.0F,60.0F,70.0F,80.0F,90.0F,100.0F};
                    mMap.addMarker(new MarkerOptions().position(location).title(zagrozenia.zagrozeniaTYP).snippet("Zagrożenie:\n    "+sTOPNIE_ZAGROZENIA[zagrozenia.zagrozeniaStopien]+"\nOpis:\n    "+zagrozenia.zagrozeniaOpis)).
                            setIcon(BitmapDescriptorFactory.defaultMarker(markerKolor[zagrozenia.zagrozeniaStopien]));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void inne() {
        zagrozeniaDbReference = FirebaseDatabase.getInstance().getReference().child("Inne");
        zagrozeniaDbReference.push().setValue(marker);
        zagrozeniaDbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot a : dataSnapshot.getChildren()) {
                    Zagrozenia zagrozenia = a.getValue(Zagrozenia.class);
                    LatLng location = new LatLng(zagrozenia.szerokosc, zagrozenia.dlugosc);
                    float[] markerKolor = {40.0F, 40.0F, 30.0F, 20.0F, 10.0F, 0.0F};
                    mMap.addMarker(new MarkerOptions().position(location).title(zagrozenia.zagrozeniaTYP).snippet("Zagrożenie:\n    " + sTOPNIE_ZAGROZENIA[zagrozenia.zagrozeniaStopien] + "\nOpis:\n    " + zagrozenia.zagrozeniaOpis)).
                            setIcon(BitmapDescriptorFactory.defaultMarker(markerKolor[zagrozenia.zagrozeniaStopien]));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

        public void policja(){
            zagrozeniaDbReference=FirebaseDatabase.getInstance().getReference().child("Policja");
            zagrozeniaDbReference.push().setValue(marker);
            zagrozeniaDbReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot a : dataSnapshot.getChildren()) {
                        Zagrozenia zagrozenia = a.getValue(Zagrozenia.class);
                        LatLng location = new LatLng(zagrozenia.szerokosc, zagrozenia.dlugosc);
                        int[] obiekty = {R.drawable.ic_1, R.drawable.ic_1, R.drawable.ic_2, R.drawable.ic_3, R.drawable.ic_4, R.drawable.ic_5};
                        mMap.addMarker(new MarkerOptions().position(location).title(zagrozenia.zagrozeniaTYP).snippet("Zagrożenie:\n    "+zagrozenia.zagrozeniaOpis+"\nStopień zagrożenia:\n    "+sTOPNIE_ZAGROZENIA[zagrozenia.zagrozeniaStopien])).
                                setIcon(BitmapDescriptorFactory.fromResource(obiekty[zagrozenia.zagrozeniaStopien]));
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
    }
}
