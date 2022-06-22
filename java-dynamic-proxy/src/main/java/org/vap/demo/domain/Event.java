package org.vap.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Event {
    private String name;
    private List<Participant> participants;
}
