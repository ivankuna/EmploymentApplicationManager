package hr.betaSoft.tools;

import lombok.Getter;
import lombok.Setter;

public class UserIdTracker {

    @Getter
    @Setter
    private static long userId;

    @Getter
    private static final long ADMIN_ID = 1;
}
