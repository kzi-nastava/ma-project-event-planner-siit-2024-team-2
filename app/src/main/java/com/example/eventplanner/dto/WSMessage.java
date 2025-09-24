package com.example.eventplanner.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WSMessage {
    private String message;
    private String title;
    private String fromId;
    private String toId;
    private String topic;
    private String subtopic;
    private long timestamp;
}