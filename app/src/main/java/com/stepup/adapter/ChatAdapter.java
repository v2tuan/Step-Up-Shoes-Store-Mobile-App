package com.stepup.adapter;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;
import com.stepup.R;
import com.stepup.config.MessageTimeFormatter;
import com.stepup.model.ConversationDTO;
import com.stepup.model.MessageDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Map<String, Bitmap> avatarCache = new HashMap<>();

    private List<MessageDTO> chatMessages;

    private ConversationDTO conversationDTO;

    Bitmap bitmap;
    public static final int SENT = 0;
    public static final int RECEIVE = 1;
    public static final int SESSION = 2;


    public ChatAdapter(List<MessageDTO> chatMessages) {
        this.chatMessages = chatMessages;
    }

    public ChatAdapter(List<MessageDTO> chatMessages, ConversationDTO conversationDTO) {
        this.chatMessages = chatMessages;
        this.conversationDTO = conversationDTO;
    }

    public ChatAdapter(List<MessageDTO> chatMessages, Bitmap bitmap) {
        this.chatMessages = chatMessages;
        this.bitmap = bitmap;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(parent.getContext());
        switch (viewType){
            case SENT:
                View itemview0 = li.inflate(R.layout.item_container_sent_message, parent, false);
                return new SentViewHolder(itemview0);
            case RECEIVE:
                View itemview1 = li.inflate(R.layout.item_container_received_message, parent, false);
                return new ReceiveViewHolder(itemview1);
            case SESSION:
                View itemview2 = li.inflate(R.layout.item_container_join_message, parent, false);
                return new SessionViewHolder(itemview2);
            default:
                break;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)){
            case SENT:
                SentViewHolder sentViewHolder = (SentViewHolder) holder;
                sentViewHolder.textMessage.setText(chatMessages.get(position).getContent());
                sentViewHolder.textDateTime.setText(MessageTimeFormatter.formatTime(chatMessages.get(position).getCreatedAt()));
                break;
            case RECEIVE:
                ReceiveViewHolder receiveViewHolder = (ReceiveViewHolder) holder;
                receiveViewHolder.textMessage.setText(chatMessages.get(position).getContent());
                receiveViewHolder.textDateTime.setText(MessageTimeFormatter.formatTime(chatMessages.get(position).getCreatedAt()));
                if(chatMessages.get(position).getSender() != null) {
                    Glide.with(holder.itemView.getContext()) // Lấy context từ itemView
                            .load(chatMessages.get(position).getSender().getProfileImage()) // Load ảnh từ item (có thể là URL hoặc resource)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(((ReceiveViewHolder) holder).avatar);         // Hiển thị ảnh trong ImageView pic
                }
                break;
            case SESSION:
                SessionViewHolder joinViewHolder = (SessionViewHolder) holder;
//                if(chatMessages.get(position).getType().toString().equals("JOIN"))
//                    joinViewHolder.textMessage.setText(chatMessages.get(position).getSender() + " joined!");
//                else
//                    joinViewHolder.textMessage.setText(chatMessages.get(position).getSender() + " leaved!");
                break;
        }
    }

    @Override
    public int getItemViewType(int position){
        if(chatMessages.get(position).getSender() != null && chatMessages.get(position).getSender().getEmail().equals(conversationDTO.getCustomer().getEmail()))
            return SENT;
//        else if (chatMessages.get(position).getType().toString().equals("JOIN") || chatMessages.get(position).getType().toString().equals("LEAVE")) {
//            return SESSION;
//        }
        else
            return RECEIVE;
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public void addMessage(MessageDTO message) {
        chatMessages.add(message);
        notifyItemInserted(chatMessages.size() - 1);
    }

    static class SentViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage, textDateTime;

        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
            textDateTime = itemView.findViewById(R.id.textDateTime);
        }
    }

    static class ReceiveViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage, textDateTime;
        RoundedImageView avatar;

        public ReceiveViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            avatar = itemView.findViewById(R.id.imageProfile);
        }
    }

    static class SessionViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;

        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.joinText);
        }
    }
}
