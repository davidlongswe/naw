package com.umu.se.dalo0013.naw;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.umu.se.dalo0013.naw.data.ChatMessage;
import com.umu.se.dalo0013.naw.ui.ChatMessageAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import util.UserProfileApi;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference root;
    private EmojiconEditText emojiconEditText;
    private ImageView emojiButton, submitButton;
    private EmojIconActions emojIconActions;
    private RelativeLayout linearLayout;
    private ChatMessageAdapter chatMessageAdapter;
    private ArrayList<ChatMessage> chatMessages;
    private RecyclerView chatMessageRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatMessages = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        Intent intent = getIntent();
        String contactUserName = intent.getStringExtra("username");
        setTitle("Talking to " + contactUserName);
        String contactUserId = intent.getStringExtra("id");

        assert contactUserId != null;
        String oneToOneRoomId = currentUser.getUid() + "_" + contactUserId;
        String oneToOneRoomIdReversed = contactUserId + "_" + currentUser.getUid();
        if(currentUser.getUid().charAt(0) < contactUserId.charAt(0)){
            root = database.getReference().getRoot().child(oneToOneRoomId);
        }else if(currentUser.getUid().charAt(0) == contactUserId.charAt(0)) {
            if (currentUser.getUid().charAt(7) < contactUserId.charAt(7)) {
                root = database.getReference().getRoot().child(oneToOneRoomId);
            }
        } else{
            root = database.getReference().getRoot().child(oneToOneRoomIdReversed);
        }

        linearLayout = findViewById(R.id.chat_activity_layout);
        emojiButton = findViewById(R.id.emoticon_button);
        submitButton = findViewById(R.id.send_message_button);
        emojiconEditText = findViewById(R.id.emoticon_edit_text);
        emojIconActions = new EmojIconActions(getApplicationContext(), linearLayout, emojiconEditText, emojiButton);
        emojIconActions.ShowEmojIcon();

        setUpChatRecyclerView();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitMessageAndUpdateFireStore(v);
            }
        });

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                appendConversation(snapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                appendConversation(snapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void submitMessageAndUpdateFireStore(View v) {
        v.startAnimation(AnimationUtils.loadAnimation(ChatActivity.this, R.anim.image_on_click_animation));
        String enteredText = emojiconEditText.getText().toString().trim();
        ChatMessage chatMessage = new ChatMessage(enteredText, UserProfileApi.getInstance().getUsername());

        Map<String, Object> map = new HashMap<String, Object>();
        String tempKey = root.push().getKey();
        root.updateChildren(map);

        assert tempKey != null;
        DatabaseReference messageRoot = root.child(tempKey);
        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("message", chatMessage);
        messageRoot.updateChildren(map1);

        emojiconEditText.setText("");
        emojiconEditText.setFocusable(true);
    }

    private void setUpChatRecyclerView() {
        chatMessageRecyclerView = findViewById(R.id.chat_recycler_view);
        chatMessageRecyclerView.setHasFixedSize(true);
        chatMessageRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        chatMessageAdapter = new ChatMessageAdapter(this, chatMessages);
        chatMessageRecyclerView.setAdapter(chatMessageAdapter);
    }

    private void appendConversation(DataSnapshot snapshot) {
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            Iterator i = dataSnapshot.getChildren().iterator();
            while(i.hasNext()){
                String message = (String) ((DataSnapshot)i.next()).getValue();
                Long timeSent = (Long) ((DataSnapshot)i.next()).getValue();
                String messageSender = (String) ((DataSnapshot)i.next()).getValue();
                ChatMessage chatMessage = new ChatMessage(message, messageSender);
                chatMessages.add(chatMessage);
                chatMessageAdapter.notifyDataSetChanged();
                chatMessageRecyclerView.scrollToPosition(chatMessages.size() - 1);
            }
        }
    }

}