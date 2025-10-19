package com.example.eventplanner.dto.chat;

import com.example.eventplanner.model.utils.ChatStatus;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto {
    private long toId;
    private List<Long> messageIds;
    private ChatStatus status;
}
