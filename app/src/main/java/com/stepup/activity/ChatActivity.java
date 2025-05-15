package com.stepup.activity;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.stepup.R;
import com.stepup.adapter.ChatAdapter;
import com.stepup.config.WebSocketManager;
import com.stepup.databinding.ActivityChatBinding;
import com.stepup.model.ChatMessageRequest;
import com.stepup.model.ConversationDTO;
import com.stepup.model.MessageDTO;
import com.stepup.retrofit2.APIService;
import com.stepup.retrofit2.RetrofitClient;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    private WebSocketManager webSocketManager;
    ActivityChatBinding binding;
    List<MessageDTO> messageDTOS;
    ChatAdapter chatAdapter;

    private ConversationDTO conversationDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        AndroidThreeTen.init(this);
        getConversation();

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getConversation() {
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<ConversationDTO> conversationDTOCall = apiService.getMyConversations();
        conversationDTOCall.enqueue(new Callback<ConversationDTO>() {
            @Override
            public void onResponse(Call<ConversationDTO> call, Response<ConversationDTO> response) {
                if(response.isSuccessful()){
                    conversationDTO = response.body();
                    getMessage();
                    messageDTOS = new ArrayList<>();
                    chatAdapter = new ChatAdapter(messageDTOS, conversationDTO);
                    binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                    binding.chatRecyclerView.setAdapter(chatAdapter);
                    binding.chatRecyclerView.setItemAnimator(new DefaultItemAnimator());

                    ProgressBar progressBar = binding.progressBar;

                    webSocketManager = WebSocketManager.getInstance();
                    webSocketManager.connect();

                    webSocketManager.subscribeToMessages(message -> {
                        Gson gson = new Gson();
                        MessageDTO chatMessage = gson.fromJson(message, MessageDTO.class); // Chuyển JSON thành model

                        if (!chatMessage.getSender().equals(conversationDTO.getCustomer().getEmail())) { // Chỉ hiển thị tin nhắn từ người khác
                            runOnUiThread(() -> {
                                progressBar.setVisibility(View.GONE); // Ẩn hoàn toàn, không chiếm không gian
                                chatAdapter.addMessage(chatMessage);
                                binding.chatRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1); // Cuộn xuống tin nhắn mới nhất
                            });
                        }
                    });


                    // Khởi tạo WebSocketService và kết nối
                    EditText editTextMessage = findViewById(R.id.inputMessage);
                    FrameLayout buttonSend = findViewById(R.id.layoutSend);

                    // Xử lý sự kiện nhấn nút gửi
                    buttonSend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String text = editTextMessage.getText().toString();
                            if (!text.isEmpty()) {
                                progressBar.setVisibility(View.GONE); // Ẩn hoàn toàn, không chiếm không gian

                                ChatMessageRequest messageRequest = new ChatMessageRequest();
                                messageRequest.setContent(text);
                                String isoString = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                                messageRequest.setCreatedAt(isoString);
                                messageRequest.setMessageType("TEXT");
                                messageRequest.setConversationId(conversationDTO.getId());
                                messageRequest.setFileUrl(null);
                                webSocketManager.sendMessage(messageRequest);
                                editTextMessage.setText("");

                                ////
//                                String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
//                                MessageDTO message = new MessageDTO();
//                                message.setContent(text);
//                                message.setMessageType("Text");
//                                message.setSender(conversationDTO.getCustomer());
//                                message.setCreatedAt(LocalDateTime.now().toString());
//                                chatAdapter.addMessage(message);
//                                binding.chatRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1); // Cuộn xuống tin nhắn mới nhất
                                ////
                            }
                        }
                    });
                }
                else {
                    Log.e(TAG, "Không thể lấy cuộc hội thoại của người dùng 1");
                }
            }

            @Override
            public void onFailure(Call<ConversationDTO> call, Throwable t) {

                Log.e(TAG, t.getMessage());
            }
        });
    }

    private void getMessage(){
        APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
        Call<List<MessageDTO>> call = apiService.getMessages(conversationDTO.getId());
        call.enqueue(new Callback<List<MessageDTO>>() {
            @Override
            public void onResponse(Call<List<MessageDTO>> call, Response<List<MessageDTO>> response) {
                if(response.isSuccessful()){
                    messageDTOS.clear(); // Xóa danh sách cũ nếu cần
                    messageDTOS.addAll(response.body());
                    chatAdapter.notifyDataSetChanged(); // Cập nhật toàn bộ adapter

                    binding.progressBar.setVisibility(View.GONE);
                    // Cuộn đến phần tử cuối cùng
                    binding.chatRecyclerView.scrollToPosition(messageDTOS.size() - 1);
                }
            }

            @Override
            public void onFailure(Call<List<MessageDTO>> call, Throwable t) {

            }
        });
    }
}