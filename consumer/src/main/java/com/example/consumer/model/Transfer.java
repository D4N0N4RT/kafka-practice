package com.example.consumer.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "transfers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transfer {

    @Id
    private UUID id;

    private Double amount;

    private UUID senderId;

    private UUID receiverId;

    @Enumerated(EnumType.STRING)
    private TransferStatus status;

    @JsonCreator
    public Transfer(@JsonProperty("id") String id,
                    @JsonProperty("amount") Double amount,
                    @JsonProperty("senderId") String senderId,
                    @JsonProperty("receiverId") String receiverId) {
        this.id = UUID.fromString(id);
        this.amount = amount;
        this.senderId = UUID.fromString(senderId);
        this.receiverId = UUID.fromString(receiverId);
    }
}
