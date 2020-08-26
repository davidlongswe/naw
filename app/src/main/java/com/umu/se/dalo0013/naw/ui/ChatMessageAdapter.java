package com.umu.se.dalo0013.naw.ui;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.library.bubbleview.BubbleTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.umu.se.dalo0013.naw.R;
import com.umu.se.dalo0013.naw.data.ChatMessage;
import java.util.ArrayList;

import util.UserProfileApi;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {

    Context ctx;
    ArrayList<ChatMessage> messageList;

    public ChatMessageAdapter(Context context, ArrayList<ChatMessage> messList) {
        this.ctx = context;
        this.messageList = messList;
    }

    @NonNull
    @Override
    public ChatMessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage chatMessage = messageList.get(position);
        holder.username.setText(chatMessage.getMessageUser());
        holder.userMessageTimeSent.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", chatMessage.getMessageTime()));
        if(!chatMessage.getMessageUser().equals(UserProfileApi.getInstance().getUsername())){
            holder.contactMessageContent.setText(chatMessage.getMessageText());
            holder.contactMessageContent.setVisibility(View.VISIBLE);
            holder.userMessageContent.setVisibility(View.INVISIBLE);
            holder.messageInfoLinearLayout.setGravity(Gravity.END);
        }else{
            holder.userMessageContent.setText(chatMessage.getMessageText());
            holder.userMessageContent.setVisibility(View.VISIBLE);
            holder.contactMessageContent.setVisibility(View.INVISIBLE);
            holder.messageInfoLinearLayout.setGravity(Gravity.START);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public TextView userMessageTimeSent;
        public TextView userMessageContent;
        public TextView contactMessageContent;
        public LinearLayout messageInfoLinearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.message_user);
            messageInfoLinearLayout = itemView.findViewById(R.id.chat_item_det_ll);
            userMessageTimeSent = itemView.findViewById(R.id.message_time);
            userMessageContent = (BubbleTextView)itemView.findViewById(R.id.current_user_message_text);
            contactMessageContent = (BubbleTextView)itemView.findViewById(R.id.contact_message_text);
        }
    }
}

