package hr.betaSoft.tools;

import lombok.Getter;
import lombok.Setter;

public class FormTracker {

    @Getter
    private static final int SHOW_ALL = 0;

    @Getter
    private static final int SIGN_UP = 1;

    @Getter
    private static final int UPDATE = 2;

    @Getter
    private static final int SIGN_OUT = 3;

    @Getter
    private static final int SHOW_NOT_SENT = -1;

    @Getter
    @Setter
    private static int formId;
}