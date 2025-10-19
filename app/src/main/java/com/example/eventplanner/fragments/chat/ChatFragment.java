package com.example.eventplanner.fragments.chat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventplanner.R;
import com.example.eventplanner.adapters.ChatAdapter;
import com.example.eventplanner.adapters.ChatMessageAdapter;
import com.example.eventplanner.clients.utils.UserIdUtils;
import com.example.eventplanner.databinding.FragmentChatBinding;
import com.example.eventplanner.model.chat.Chat;
import com.example.eventplanner.model.chat.ChatMessage;
import com.example.eventplanner.model.user.BaseUser;
import com.example.eventplanner.utils.JsonLog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private ChatViewModel viewModel;
    private ChatAdapter chatAdapter;
    private ChatMessageAdapter messageAdapter;
    private long myId;
    private String previousMessage = "";

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myId = UserIdUtils.getUserId(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupRecyclerViews();
        setupClickListeners();
        setupViewModel();
        loadData();

        return root;
    }

    private void setupRecyclerViews() {
        RecyclerView rvChats = binding.rvChats;
        rvChats.setLayoutManager(new LinearLayoutManager(requireContext()));
        chatAdapter = new ChatAdapter(new ArrayList<>(), this::onChatClick, myId);
        rvChats.setAdapter(chatAdapter);

        RecyclerView rvMessages = binding.rvMessages;
        rvMessages.setLayoutManager(new LinearLayoutManager(requireContext()));
        messageAdapter = new ChatMessageAdapter(new ArrayList<>(), myId);
        rvMessages.setAdapter(messageAdapter);
    }

    private void setupClickListeners() {
        MaterialButton btnSendMessage = binding.btnSendMessage;
        btnSendMessage.setOnClickListener(v -> sendMessage());

        MaterialButton btnBlockUser = binding.btnBlockUser;
        btnBlockUser.setOnClickListener(v -> toggleBlockUser());

        TextInputEditText etNewMessage = binding.etNewMessage;
        etNewMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setNewMessage(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        MaterialButton btnBack = binding.btnBack;
        LinearLayout layoutChatWindow = binding.chatWindow;
        LinearLayout layoutAllChats = binding.allChats;
        btnBack.setOnClickListener(v -> {
            layoutChatWindow.setVisibility(View.GONE);
            layoutAllChats.setVisibility(View.VISIBLE);
        });

        MaterialButton btnRefreshChats = binding.btnRefreshChats;
        btnRefreshChats.setOnClickListener(v -> refreshChats());

        MaterialButton btnRefreshMessages = binding.btnRefreshMessages;
        btnRefreshMessages.setOnClickListener(v -> refreshMessages());
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        viewModel.setMyId(myId);

        viewModel.getMyChats().observe(getViewLifecycleOwner(), chats -> {
            chatAdapter.updateChats(chats);
        });

        viewModel.getCurrentChat().observe(getViewLifecycleOwner(), chat -> {
            JsonLog.d("Chat", chat, "Chat ");
            if (chat != null) {
                showChatWindow();
                updateChatUI(chat);
                updateChatHeader(chat.getUser2());
            } else {
                showEmptyState();
            }
        });

        viewModel.getChatFriend().observe(getViewLifecycleOwner(), friend -> {
            if (friend != null) {
                updateChatHeader(friend);
            }
        });

        viewModel.getNewMessage().observe(getViewLifecycleOwner(), message -> {
            Editable currentText = binding.etNewMessage.getText();

            if (currentText == null || !message.equals(currentText.toString())) {
                int cursorPosition = binding.etNewMessage.getSelectionStart();
                binding.etNewMessage.setText(message);

                if (message != null && cursorPosition <= message.length()) {
                    binding.etNewMessage.setSelection(cursorPosition);
                } else {
                    binding.etNewMessage.setSelection(message.length());
                }
            }
            previousMessage = message;
        });
    }

    private void loadData() {
        viewModel.loadMyChats();
    }

    private void refreshChats() {
        viewModel.loadMyChats();
        Toast.makeText(requireContext(), "Chats refreshed", Toast.LENGTH_SHORT).show();
    }

    private void refreshMessages() {
        Long friendId = viewModel.getChatFriendId().getValue();
        if (friendId != null) {
            viewModel.loadChatWithUser(friendId);
            Toast.makeText(requireContext(), "Messages refreshed", Toast.LENGTH_SHORT).show();
        }
    }

    private void onChatClick(Chat chat) {
        viewModel.selectChat(chat);
        binding.allChats.setVisibility(View.GONE);
        binding.chatWindow.setVisibility(View.VISIBLE);
    }

    private void sendMessage() {
        viewModel.sendMessage();
    }

    private void toggleBlockUser() {
        if (viewModel.hasBlockedUser()) {
            viewModel.unblockUser();
            Toast.makeText(requireContext(), "User unblocked", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.blockUser();
            Toast.makeText(requireContext(), "User blocked", Toast.LENGTH_SHORT).show();
        }
    }

    private void showChatWindow() {
        binding.chatWindow.setVisibility(View.VISIBLE);
        binding.allChats.setVisibility(View.GONE);
//        binding.layoutEmptyState.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        binding.chatWindow.setVisibility(View.GONE);
        binding.allChats.setVisibility(View.VISIBLE);
//        binding.layoutEmptyState.setVisibility(View.VISIBLE);
    }

    private void updateChatUI(Chat chat) {
        List<ChatMessage> messages = chat.getMessages() != null ? chat.getMessages() : new ArrayList<>();
        messageAdapter.updateMessages(messages);

        updateBlockedStateUI(chat);

        updateBlockButton(chat);
    }

    private void updateChatHeader(BaseUser friend) {
        String name = friend.getFirstName() != null && friend.getLastName() != null ?
                friend.getFirstName() + " " + friend.getLastName() :
                friend.getEmail();
        binding.tvChatFriendName.setText(name);
    }

    private void updateBlockedStateUI(Chat chat) {
        LinearLayout layoutBlockedMessage = binding.layoutBlockedMessage;
        TextView tvBlockedMessage = binding.tvBlockedMessage;
        LinearLayout layoutInputArea = binding.layoutInputArea;

        if (viewModel.chatBlocked()) {
            layoutBlockedMessage.setVisibility(View.VISIBLE);
            layoutInputArea.setVisibility(View.GONE);

            if (viewModel.bothBlocked()) {
                tvBlockedMessage.setText("You and this user blocked each other");
            } else if (viewModel.hasBlockedUser()) {
                tvBlockedMessage.setText("You blocked this user");
            } else {
                tvBlockedMessage.setText("This user blocked you");
            }
        } else {
            layoutBlockedMessage.setVisibility(View.GONE);
            layoutInputArea.setVisibility(View.VISIBLE);
        }
    }

    private void updateBlockButton(Chat chat) {
        MaterialButton btnBlockUser = binding.btnBlockUser;

        if (viewModel.canBlockUser()) {
            btnBlockUser.setVisibility(View.VISIBLE);
            if (viewModel.hasBlockedUser()) {
                btnBlockUser.setText("Unblock");
            } else {
                btnBlockUser.setText("Block");
            }
        } else {
            btnBlockUser.setVisibility(View.GONE);
        }
    }

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    public static ChatFragment newInstanceWithUser(long userId) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putLong("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            long userId = getArguments().getLong("userId", -1);
            if (userId != -1) {
                viewModel.loadChatWithUser(userId);
            }
        }
    }
}
