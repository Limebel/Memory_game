package org.example.common;

import lombok.*;

import java.net.InetAddress;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PlayerModel {
    private String name;
    private InetAddress adressIP;
    private Integer score=0;
    private boolean connected = true;


    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        PlayerModel other = (PlayerModel) obj;

        return Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
