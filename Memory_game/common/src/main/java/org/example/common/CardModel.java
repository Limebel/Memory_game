package org.example.common;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class CardModel {
    private Integer Value;
    private Boolean ifFlipped;
    private Boolean ifMatched;
}
