package Generator;

import Courier.User;

import java.util.UUID;

public class Randomizer {
    public static User randomUser() {
        return new User()
                .setLogin("login_" + UUID.randomUUID())
                .setPassword("pass_" + UUID.randomUUID())
                .setFirstName("name" + UUID.randomUUID());
    }
}
