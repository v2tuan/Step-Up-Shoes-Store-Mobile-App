package com.stepup.config;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.stepup.model.ChatMessageRequest;
import com.stepup.retrofit2.MyApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class WebSocketManager {
    private static final String TAG = "WebSocketManager";
    private static WebSocketManager instance;
    private StompClient stompClient;
    private Disposable topicSubscription;

    private WebSocketManager() {
        // Lấy token mới mỗi lần kết nối
        String token = MyApp.getContext()
                .getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                .getString("token", null);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    return chain.proceed(request);
                }).build();

//        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://10.0.2.2:8089/ws/websocket"); // Thay bằng IP Server nếu chạy trên device thật

        stompClient = Stomp.over(
                Stomp.ConnectionProvider.OKHTTP,
                "ws://10.0.2.2:8089/ws/websocket",
                null,
                okHttpClient
        );

        stompClient.lifecycle()
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.d(TAG, "WebSocket Connected");
                            break;
                        case ERROR:
                            Log.e(TAG, "Error", lifecycleEvent.getException());
                            break;
                        case CLOSED:
                            Log.d(TAG, "WebSocket Closed");
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            Log.w("STOMP", "Không nhận được heartbeat từ server");
                            break;
                    }
                });
    }

    public static WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }

    public void connect() {
        // Lấy token mới mỗi lần kết nối
        String token = MyApp.getContext()
                .getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                .getString("token", null);
        // Tạo danh sách headers
        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("Authorization", "Bearer " + token));

        stompClient.connect(headers);
    }

    public void disconnect() {
        if (stompClient != null) {
            stompClient.disconnect();
        }
    }

    public void sendMessage(ChatMessageRequest messageRequest) {
        Gson gson = new Gson();
        String json = gson.toJson(messageRequest);
        stompClient.send("/app/chat.sendMessage", json).subscribe();
    }

    public void subscribeToMessages(MessageListener listener) {
        topicSubscription = stompClient.topic("/topic/conversation")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d(TAG, "Received: " + topicMessage.getPayload());
                    listener.onNewMessage(topicMessage.getPayload());
                }, throwable -> Log.e(TAG, "Error in subscription", throwable));
    }

    public interface MessageListener {
        void onNewMessage(String message);
    }
}
