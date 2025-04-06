package com.stepup.activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.stepup.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddAddressActivity extends AppCompatActivity implements OnMapReadyCallback {

    private EditText etName, etPhoneNumber, etAddress;
    private Button  btnComplete ;
    private ImageView btnSelectFromMap, btnBack;
    private static final int REQUEST_LOCATION = 100;

    private GoogleMap mMap;
    private LatLng selectedLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etName = findViewById(R.id.etName);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etAddress = findViewById(R.id.etAddress);
        btnSelectFromMap = findViewById(R.id.btnSelectFromMap);
        btnComplete = findViewById(R.id.btnComplete);
        btnBack = findViewById(R.id.btnBack); // Khởi tạo nút Back

        // Sự kiện quay lại
        btnBack.setOnClickListener(v -> {
            finish(); // Quay về Activity trước
        });

        // Khởi tạo Map Fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragmentContainer);
        mapFragment.getMapAsync(this);
        etAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // Khi mất focus (thoát khỏi EditText)
                if (!hasFocus) {
                    String address = etAddress.getText().toString();
                    if (!address.isEmpty()) {
                        selectedLatLng = getLatLngFromAddress(address);
                        if (selectedLatLng != null) {
                            showMapLocation(selectedLatLng, address);
                        } else {
                            Toast.makeText(AddAddressActivity.this, "Địa chỉ không hợp lệ", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        // Kiểm tra Intent từ MapsActivity
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("address")) {
            String address = intent.getStringExtra("address");
            etAddress.setText(address);
            selectedLatLng = getLatLngFromAddress(address);
        }

        // Chọn vị trí từ MapsActivity
        btnSelectFromMap.setOnClickListener(v -> {
            Intent mapIntent = new Intent(AddAddressActivity.this, MapsActivity.class);
            startActivityForResult(mapIntent, REQUEST_LOCATION);
        });

        // Hoàn thành
        btnComplete.setOnClickListener(v -> {
            String address = etAddress.getText().toString();
            if (address.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập địa chỉ", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Địa chỉ đã được lưu!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOCATION && resultCode == RESULT_OK) {
            String address = data.getStringExtra("address");
            etAddress.setText(address);

            // Hiển thị địa chỉ trên bản đồ
            selectedLatLng = getLatLngFromAddress(address);
            if (mMap != null && selectedLatLng != null) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(selectedLatLng).title(address));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 15));
            }
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (selectedLatLng != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(selectedLatLng).title("Vị trí đã chọn"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 15));
        }
    }

    // Hàm lấy tọa độ từ địa chỉ
    private LatLng getLatLngFromAddress(String address) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocationName(address, 1);
            if (addressList != null && !addressList.isEmpty()) {
                Address location = addressList.get(0);
                return new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    // Hàm hiển thị vị trí trên bản đồ
    private void showMapLocation(LatLng latLng, String title) {
        if (mMap != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title(title));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
    }
}
