package com.stepup.activity;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.stepup.AppUtils;
import com.stepup.R;
import com.stepup.adapter.ImageAdapter;
import com.stepup.databinding.ActivityRatingBinding;
import com.stepup.model.OrderItemResponse;
import com.stepup.model.ReviewDTO;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RatingActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_READ_MEDIA = 1001;
    private ActivityRatingBinding binding;
    private ImageView[] stars;
    private List<Uri> imageUris;
    private ImageAdapter imageAdapter;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private static final int MAX_IMAGES = 5;
    private int selectedRating = 0;
    private OrderItemResponse item;
    private Long orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityRatingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        item = getIntent().getParcelableExtra("orderItem");
        orderId = getIntent().getLongExtra("orderid", -1);

        if (item != null) {
            binding.productName.setText(item.getTitle());
            binding.color.setText(item.getProductVariant().getColor().getName());
            Glide.with(RatingActivity.this)
                    .load(item.getProductVariant().getColor().getColorImages().get(0).getImageUrl())
                    .apply(new RequestOptions().centerCrop())
                    .into(binding.picProduct);
        }

        // Initialize stars
        stars = new ImageView[]{
                binding.star1, binding.star2, binding.star3, binding.star4, binding.star5
        };

        for (int i = 0; i < stars.length; i++) {
            final int rating = i + 1;
            stars[i].setOnClickListener(v -> setRating(rating));
        }

        // Initialize image list and RecyclerView
        imageUris = new ArrayList<>();
        imageAdapter = new ImageAdapter(this, imageUris, image -> {
            if (image instanceof Uri) {
                imageUris.remove(image);
                imageAdapter.notifyDataSetChanged();
            }
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerView.setAdapter(imageAdapter);

        // Initialize image picker launcher
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                ClipData clipData = result.getData().getClipData();
                if (clipData != null) {
                    // Multiple images selected
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        if (imageUris.size() < MAX_IMAGES) {
                            imageUris.add(clipData.getItemAt(i).getUri());
                        }
                    }
                } else {
                    // Single image selected
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null && imageUris.size() < MAX_IMAGES) {
                        imageUris.add(imageUri);
                    }
                }
                imageAdapter.notifyDataSetChanged();

                if (imageUris.size() >= MAX_IMAGES) {
                    AppUtils.showDialogNotify(RatingActivity.this, R.drawable.error, "Không thể thêm quá tối đa 5 ảnh");
                }
            }
        });

        // Set click listener for add picture button
        binding.addPicBtn.setOnClickListener(v -> {
            if (checkImagePermission()) {
                launchImagePicker();
            }
        });

        binding.btnRating.setOnClickListener(v -> submitReview(item));

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private boolean checkImagePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        REQUEST_CODE_READ_MEDIA);
                return false;
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_READ_MEDIA);
                return false;
            }
        }
        return true;
    }

    private void launchImagePicker() {
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        intent.putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, MAX_IMAGES);
        imagePickerLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_READ_MEDIA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchImagePicker();
            } else {
                Toast.makeText(this, "Quyền truy cập ảnh bị từ chối", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setRating(int rating) {
        selectedRating = rating;
        for (int i = 0; i < stars.length; i++) {
            stars[i].setImageResource(i < rating ? R.drawable.star : R.drawable.staroutline);
        }
    }

    private void submitReview(OrderItemResponse item) {
        // Validate inputs
        if (selectedRating == 0) {
            AppUtils.showDialogNotify(RatingActivity.this, R.drawable.error, "Vui lòng chọn số sao");
            return;
        }

        String content = binding.etReview.getText().toString().trim();
        if (content.length() > 1000) {
            AppUtils.showDialogNotify(RatingActivity.this, R.drawable.error, "Nội dung đánh giá tối đa 1000 ký tự ");
            return;
        }

        if (imageUris.isEmpty()) {
            AppUtils.showDialogNotify(RatingActivity.this, R.drawable.error, "Vui lòng thêm ít nhất 1 ảnh");
            return;
        }

        if (item == null) {
            AppUtils.showDialogNotify(RatingActivity.this, R.drawable.error, "Không tìm thấy thông tin sản phẩm");
            return;
        }
        showLoading();
        // Prepare multipart request
        List<MultipartBody.Part> imageParts = new ArrayList<>();
        for (Uri uri : imageUris) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                byte[] imageBytes = getBytes(inputStream);
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageBytes);
                imageParts.add(MultipartBody.Part.createFormData("images", "image_" + System.currentTimeMillis() + ".jpg", requestFile));
            } catch (IOException e) {
                AppUtils.showDialogNotify(RatingActivity.this, R.drawable.error, "Lỗi khi xử lý ảnh");
                return;
            }
        }

        RequestBody productVariantId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(item.getProductVariant().getId()));
        RequestBody orderIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(orderId));
        RequestBody ratingBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(selectedRating));
        RequestBody contentBody = RequestBody.create(MediaType.parse("text/plain"), content);

        // Call API
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<String> call = apiService.submitReview(productVariantId, orderIdBody, contentBody, ratingBody, imageParts);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AppUtils.showDialogNotify(RatingActivity.this, R.drawable.ic_check, response.body());

                } else {

                    AppUtils.showDialogNotify(RatingActivity.this, R.drawable.error, "Gửi đánh giá thất bại!"+response.body());
                }
                hideLoading();
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                hideLoading();
                AppUtils.showDialogNotify(RatingActivity.this, R.drawable.error, "Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
    private void showLoading() {
        FrameLayout overlay = findViewById(R.id.overlay);
        overlay.setVisibility(View.VISIBLE);
        overlay.setClickable(true); // Chặn tương tác với các view bên dưới
    }

    // Ẩn process bar
    private void hideLoading() {
        FrameLayout overlay = findViewById(R.id.overlay);
        overlay.setVisibility(View.GONE);
        overlay.setClickable(false);
    }
}