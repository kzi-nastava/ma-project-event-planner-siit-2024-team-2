package com.example.eventplanner.fragments.chat;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.eventplanner.clients.repositories.chat.ChatRepository;
import com.example.eventplanner.clients.repositories.user.UserRepository;
import com.example.eventplanner.dto.chat.ChatDto;
import com.example.eventplanner.dto.chat.ChatMessageDto;
import com.example.eventplanner.model.chat.Chat;
import com.example.eventplanner.model.chat.ChatMessage;
import com.example.eventplanner.model.user.BaseUser;
import com.example.eventplanner.model.utils.ChatStatus;
import com.example.eventplanner.model.utils.PagedModel;
import com.example.eventplanner.utils.JsonLog;
import com.example.eventplanner.utils.ObserverTracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChatViewModel extends ViewModel {

    private final ChatRepository chatRepository = new ChatRepository();
    private final UserRepository userRepository = new UserRepository();
    private final ObserverTracker tracker = new ObserverTracker();

    private final MutableLiveData<List<Chat>> myChats = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Chat> currentChat = new MutableLiveData<>();
    private final MutableLiveData<BaseUser> chatFriend = new MutableLiveData<>();
    private final MutableLiveData<String> newMessage = new MutableLiveData<>("");
    private final MutableLiveData<Long> myId = new MutableLiveData<>();
    private final MutableLiveData<Long> chatFriendId = new MutableLiveData<>();

    public LiveData<List<Chat>> getMyChats() {
        return myChats;
    }

    public LiveData<Chat> getCurrentChat() {
        return currentChat;
    }

    public LiveData<BaseUser> getChatFriend() {
        return chatFriend;
    }

    public LiveData<String> getNewMessage() {
        return newMessage;
    }

    public LiveData<Long> getMyId() {
        return myId;
    }

    public LiveData<Long> getChatFriendId() {
        return chatFriendId;
    }

    public void loadMyChats() {
        tracker.observeOnce(chatRepository.getAllMyChats(0, -1), new MutableLiveData<PagedModel<Chat>>() {
            @Override
            public void setValue(PagedModel<Chat> pagedModel) {
                if (pagedModel != null && pagedModel.getContent() != null) {
                    myChats.setValue(pagedModel.getContent());
                } else {
                    myChats.setValue(new ArrayList<>());
                }
            }
        }, true);
    }

    public void loadChatWithUser(long user2Id) {
        tracker.observeOnce(chatRepository.getMineUser2Id(user2Id), chat -> {
            if (chat != null) {
                selectChat(chat);
            } else {
                ChatDto chatDto = new ChatDto(user2Id, new ArrayList<>(), ChatStatus.ALL_SEEN);
                tracker.observeOnce(chatRepository.addChat(chatDto), this::selectChat);
            }
        });
    }

    public void selectChat(Chat chat) {
        if (chat.getMessages() != null) {
            chat.getMessages().sort(Comparator.comparing(ChatMessage::getSentAt));
        }

        Long myIdValue = myId.getValue();
        if (myIdValue != null) {
            if (chat.getUser1().getId().equals(myIdValue)) {
                chatFriend.setValue(chat.getUser2());
                chatFriendId.setValue(chat.getUser2().getId());
            } else {
                chatFriend.setValue(chat.getUser1());
                chatFriendId.setValue(chat.getUser1().getId());
            }
        }

        currentChat.setValue(chat);
    }

    public void sendMessage() {
        String messageText = newMessage.getValue();
        Long friendId = chatFriendId.getValue();
        Chat currentChatValue = currentChat.getValue();
        
        if (messageText != null && !messageText.trim().isEmpty() && friendId != null && currentChatValue != null) {
            ChatMessageDto messageDto = new ChatMessageDto(messageText.trim(), friendId, false);
            
            tracker.observeOnce(chatRepository.sendMessage(messageDto), message -> {
                if (message != null && currentChatValue != null) {
                    // Send message to chat
                    tracker.observeOnce(chatRepository.sendMessageToChat(currentChatValue.getId(), message),
                        chat -> {
                            if (chat != null) {
                                currentChat.setValue(chat);
                                // Move chat to top of list
                                List<Chat> chats = myChats.getValue();
                                if (chats != null) {
                                    chats.remove(chat);
                                    chats.add(0, chat);
                                    myChats.setValue(new ArrayList<>(chats));
                                }
                            }
                        });
                }
            });
            
            newMessage.setValue("");
        }
    }

    public void setNewMessage(String message) {
        newMessage.setValue(message);
    }

    public void setMyId(long id) {
        myId.setValue(id);
    }

    public void createNewChat(long toUserId) {
        ChatDto chatDto = new ChatDto(toUserId, new ArrayList<>(), ChatStatus.ALL_SEEN);
        tracker.observeOnce(chatRepository.addChat(chatDto), currentChat, true);
    }

    public void blockUser() {
        Long friendId = chatFriendId.getValue();
        if (friendId != null) {
            tracker.observeOnce(userRepository.blockUser(friendId), new MutableLiveData<Void>() {
                @Override
                public void setValue(Void result) {
                    // Update current chat to reflect blocking
                    Chat chat = currentChat.getValue();
                    if (chat != null) {
                        Long myIdValue = myId.getValue();
                        if (myIdValue != null) {
                            if (chat.getUser1().getId().equals(myIdValue)) {
                                chat.setUser1BlockedUser2(true);
                            } else {
                                chat.setUser2BlockedUser1(true);
                            }
                            chat.setMessages(new ArrayList<>()); // Clear messages when blocked
                            currentChat.setValue(chat);
                        }
                    }
                }
            }, true);
        }
    }

    public void unblockUser() {
        Long friendId = chatFriendId.getValue();
        if (friendId != null) {
            tracker.observeOnce(userRepository.unblockUser(friendId), new MutableLiveData<Void>() {
                @Override
                public void setValue(Void result) {
                    // Update current chat to reflect unblocking
                    Chat chat = currentChat.getValue();
                    if (chat != null) {
                        Long myIdValue = myId.getValue();
                        if (myIdValue != null) {
                            if (chat.getUser1().getId().equals(myIdValue)) {
                                chat.setUser1BlockedUser2(false);
                            } else {
                                chat.setUser2BlockedUser1(false);
                            }
                            currentChat.setValue(chat);
                            
                            // Refresh the chat to get new messages
                            loadChatWithUser(friendId);
                        }
                    }
                }
            }, true);
        }
    }

    public boolean canBlockUser() {
        return chatFriendId.getValue() != null && currentChat.getValue() != null;
    }

    public boolean hasBlockedUser() {
        Chat chat = currentChat.getValue();
        Long myIdValue = myId.getValue();
        if (chat == null || myIdValue == null) return false;
        
        return chat.getUser1().getId().equals(myIdValue) ? 
            chat.isUser1BlockedUser2() : chat.isUser2BlockedUser1();
    }

    public boolean bothBlocked() {
        Chat chat = currentChat.getValue();
        if (chat == null) return false;
        return chat.isUser1BlockedUser2() && chat.isUser2BlockedUser1();
    }

    public boolean chatBlocked() {
        Chat chat = currentChat.getValue();
        if (chat == null) return false;
        return chat.isUser1BlockedUser2() || chat.isUser2BlockedUser1();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        tracker.clear();
    }
}
