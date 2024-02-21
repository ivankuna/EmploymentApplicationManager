package hr.betaSoft.tools;

import lombok.Getter;
import lombok.Setter;

public class FormTracker {

    // GUIDE:
    // Prijava -> 1
    // Promjena -> 2
    // Odjava -> 3

    @Getter
    @Setter
    private static int formId;
}
