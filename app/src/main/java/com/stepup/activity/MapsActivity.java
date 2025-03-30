package com.stepup.activity;

import android.Manifest;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.stepup.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private AutoCompleteTextView etSearchLocation;
    private Button btnSearch, btnSelect;
    private LatLng selectedLatLng;

    private Marker locationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Khởi tạo Views
        etSearchLocation = findViewById(R.id.etSearchLocation);
        btnSearch = findViewById(R.id.btnSearch);
        btnSelect = findViewById(R.id.btnSelect);

        // Khởi tạo Google Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // Xử lý nút tìm kiếm
        btnSearch.setOnClickListener(v -> searchLocation(etSearchLocation.getText().toString()));

        // Xử lý nút chọn vị trí
        btnSelect.setOnClickListener(v -> {
            if (selectedLatLng != null) {
                String address = getAddressFromLatLng(selectedLatLng);
                Intent intent = new Intent();
                intent.putExtra("address", address);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Toast.makeText(this, "Vui lòng chọn vị trí", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng defaultLocation = new LatLng(10.762622, 106.660172);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));

        // Xử lý chọn vị trí trên bản đồ
        mMap.setOnMapClickListener(latLng -> {
            selectedLatLng = latLng;
            mMap.clear();

            // Hiển thị marker
            locationMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Vị trí đã chọn"));

            // Lấy địa chỉ từ LatLng
            etSearchLocation.setText(getAddressFromLatLng(latLng));
        });
        mMap.setOnMarkerClickListener(marker -> {
            LatLng markerPosition = marker.getPosition();
            String address = getAddressFromLatLng(markerPosition);

            // Hiển thị địa chỉ lên thanh tìm kiếm
            etSearchLocation.setText(address);

            // Cập nhật tọa độ đã chọn
            selectedLatLng = markerPosition;

            return false; // False để tiếp tục hành vi mặc định (di chuyển camera)
        });
    }

    // HÀM TÌM KIẾM VỊ TRÍ
    private void searchLocation(String location) {
        if (location.isEmpty()) {
            Toast.makeText(this, "Nhập địa chỉ cần tìm", Toast.LENGTH_SHORT).show();
            return;
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocationName(location, 1);
            if (addressList != null && !addressList.isEmpty()) {
                Address address = addressList.get(0);
                selectedLatLng = new LatLng(address.getLatitude(), address.getLongitude());

                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(selectedLatLng).title(address.getAddressLine(0)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 15));
            } else {
                Toast.makeText(this, "Không tìm thấy vị trí", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // HÀM LẤY ĐỊA CHỈ TỪ LATLNG
    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            return addresses != null && !addresses.isEmpty() ? addresses.get(0).getAddressLine(0) : "Không xác định";
        } catch (IOException e) {
            e.printStackTrace();
            return "Không xác định";
        }
    }
}
