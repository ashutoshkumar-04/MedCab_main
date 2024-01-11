package com.example.medcab_main;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CustomerMapsActivity extends FragmentActivity implements OnMapReadyCallback,
        com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private Button logoutButton;
    private Button bookRideButton; // Add this button for booking rides

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize UI elements
        logoutButton = findViewById(R.id.CustomerlogoutButton);
        bookRideButton = findViewById(R.id.customer_book_btn); // Initialize the "Book Ride" button

        // Set click listener for the logout button
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent welcomeIntent = new Intent(CustomerMapsActivity.this, WelcomeActivity.class);
                welcomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(welcomeIntent);
                finish();
            }
        });

        // Set click listener for the "Book Ride" button
        bookRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the method to update the customer's location in the database
                updateCustomerLocationInDatabase();
            }
        });

        // Retrieve and display the customer's location
        retrieveCustomerLocation();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Check if location permissions are granted
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
            return;
        }

        // Enable My Location layer
        if (mMap != null) {
            mMap.setMyLocationEnabled(true);

            // Request location updates when the map is ready
            requestLocationUpdates();
        } else {
            Log.e("MapError", "GoogleMap is null");
        }
    }

    private void requestLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Handle new location updates
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                }
            }
        }, null);
    }

    private void retrieveCustomerLocation() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userID = currentUser.getUid();
            DatabaseReference customerLocationRef = FirebaseDatabase.getInstance().getReference().child("customersLocation").child(userID);

            // Listen for changes in the customer's location
            customerLocationRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Retrieve and handle the customer's location data
                        double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                        double longitude = dataSnapshot.child("longitude").getValue(Double.class);

                        // Do something with the location data (e.g., update marker on the map)
                        LatLng customerLocation = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions().position(customerLocation).title("Customer Location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(customerLocation));

                        Log.d("FirebaseData", "Customer Location: " + latitude + ", " + longitude);
                    } else {
                        Log.d("FirebaseData", "Customer Location is null");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("FirebaseError", "Error: " + databaseError.getMessage());
                }
            });
        }
    }

    private void updateCustomerLocationInDatabase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userID = currentUser.getUid();
            DatabaseReference customerLocationRef = FirebaseDatabase.getInstance().getReference().child("customersLocation").child(userID);

            // Get the last known location
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            // Update the customer's location in the database
                            customerLocationRef.child("latitude").setValue(location.getLatitude());
                            customerLocationRef.child("longitude").setValue(location.getLongitude());

                            // Query and display available drivers
                            queryAndDisplayAvailableDrivers(location.getLatitude(), location.getLongitude());
                        }
                    });
        }
    }

    private void queryAndDisplayAvailableDrivers(double customerLatitude, double customerLongitude) {
        DatabaseReference driversLocationRef = FirebaseDatabase.getInstance().getReference().child("driversLocation");

        // Add a ValueEventListener to listen for changes in the drivers' locations
        driversLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMap.clear(); // Clear previous markers on the map

                for (DataSnapshot driverSnapshot : dataSnapshot.getChildren()) {
                    // Retrieve driver's location data
                    double driverLatitude = driverSnapshot.child("latitude").getValue(Double.class);
                    double driverLongitude = driverSnapshot.child("longitude").getValue(Double.class);

                    // Create LatLng object for driver's location
                    LatLng driverLocation = new LatLng(driverLatitude, driverLongitude);

                    // Add marker for each driver on the map
                    mMap.addMarker(new MarkerOptions().position(driverLocation).title("Driver Location"));
                }

                // Zoom the camera to show both customer and drivers on the map
                LatLng customerLocation = new LatLng(customerLatitude, customerLongitude);
                mMap.addMarker(new MarkerOptions().position(customerLocation).title("Customer Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(customerLocation));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(customerLocation, 12.0f));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Error: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        // Handle location changes if needed
    }
}
