package com.zgamelogic.data.messages;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "message history")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Message {
    @Id
    private long messageId;
    private Date date;
    private long userId;
    private long channelId;
    private String message;
}
