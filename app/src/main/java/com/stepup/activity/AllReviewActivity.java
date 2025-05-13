package com.stepup.activity;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.stepup.databinding.ActivityAllReviewBinding;
import com.stepup.adapter.ReviewAdapter;
import com.stepup.model.ReviewResponse;

import java.util.ArrayList;
import java.util.List;

public class AllReviewActivity extends AppCompatActivity {
    private ActivityAllReviewBinding binding;
    private List<ReviewResponse> reviewList;
    private ReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get reviews from Intent
        reviewList = getIntent().getParcelableArrayListExtra("reviews");
        binding.rvCount.setText(String.format("Reviews (%d)", reviewList.size()));
        if (reviewList.isEmpty()) {
            reviewList = new ArrayList<>();
            binding.tvNoReviews.setVisibility(View.VISIBLE);
            binding.rvReviews.setVisibility(View.GONE);
        } else {
            binding.tvNoReviews.setVisibility(View.GONE);
            binding.rvReviews.setVisibility(View.VISIBLE);
        }
        // Initialize RecyclerView
        reviewAdapter = new ReviewAdapter(this, reviewList);
        binding.rvReviews.setLayoutManager(new LinearLayoutManager(this));
        binding.rvReviews.setAdapter(reviewAdapter);

        // Back button
        binding.btnBack.setOnClickListener(v -> finish());
    }
}