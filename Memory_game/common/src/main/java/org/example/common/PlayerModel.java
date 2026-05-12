package org.example.common;

import lombok.*;

import java.net.InetAddress;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PlayerModel {
    //private UUID id;
    private String name;
    private InetAddress adressIP;
    private Integer score=0;
    private boolean connected = true;
    // temporary reconnect token
    private String reconnectId;
}
