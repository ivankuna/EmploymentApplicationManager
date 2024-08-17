package hr.betaSoft.tools;

import lombok.Getter;
import lombok.Setter;

public class FormTracker {

    @Getter
    private static final Integer SHOW_ALL = 0;

    @Getter
    private static final Integer SIGN_UP = 1;

    @Getter
    private static final Integer UPDATE = 2;

    @Getter
    private static final Integer SIGN_OUT = 3;

    @Getter
    private static final Integer SHOW_NOT_SENT = -1;
}