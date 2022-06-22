package org.vap.demo.factory;

import com.github.javafaker.Faker;
import org.vap.demo.domain.Participant;

import java.util.ArrayList;
import java.util.List;

public class ParticipantFactory {

    private static final Faker faker = new Faker();

    public Participant create() {
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        return new Participant(
                String.format("%s %s", firstName, lastName),
                String.format("%s_%s@test.com", firstName.toLowerCase(), lastName.toLowerCase()));
    }

    public List<Participant> create(int howMany) {
        List<Participant> participants = new ArrayList<>(howMany);
        for (int i = 0; i < howMany; i++) {
            participants.add(create());
        }
        return participants;
    }
}
