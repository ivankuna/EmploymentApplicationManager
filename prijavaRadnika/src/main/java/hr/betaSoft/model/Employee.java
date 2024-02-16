package hr.betaSoft.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import hr.betaSoft.security.secModel.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="employees")
public class Employee {

    private static final long serialVersionUID = 1L;

    public static final List<String> GENDER = Arrays.asList("Muško", "Žensko");

    public static final List<String>  PROFESSIONAL_QUALIFICATION = Arrays.asList("NKV", "PKV", "NSS", "KV", "SSS", "VKV", "VŠS", "VSS");

    public static final List<String> EMPLOYMENT_CONTRACT = Arrays.asList("Određeno", "Neodređeno");

    public static final List<String> WORKING_HOURS = Arrays.asList("Puno", "Nepuno");

    public static final List<String> SALARY_TYPE = Arrays.asList("Bruto", "Neto");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String firstName;

    @Column(nullable=false)
    private String lastName;

    @Column
    private String gender;

    @Column
    private String oib;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @Column
    private String address;

    @Column
    private String city;

    @Column
    private String professionalQualification;

    @Column
    private String highestLevelOfEducation;

    @Column
    private String ibanRegular;

    @Column
    private String ibanProtected;

    @Column
    private String employmentPosition;

    @Column
    private String cityOfEmployment;

    @Column
    private String requiredProfessionalQualification;

    @Column
    private String employmentContract;

    @Column
    private String reasonForDefinite;

    @Column
    private boolean additionalWork;

    @Column
    private Integer additionalWorkHours;

    @Column
    private String workingHours;

    @Column
    private Integer hoursForPartTime;

    @Column
    private String nonWorkingDays;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date dateOfSignUp;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date dateOfSignOut;

    @Column
    private BigDecimal basicSalary;

    @Column
    private String salaryType;

    @Column
    private boolean foreignNational;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date expiryDateOfWorkPermit;

    @Column
    private boolean retiree;

    @Column
    private boolean youngerThanThirty;

    @Column
    private boolean firstEmployment;

    @Column
    private boolean disability;

    @Column
    private String note;

    @Column
    private boolean signUpSent;

    @Column
    private boolean signOutSent;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date dateOfSignUpSent;

    @Column
    private String timeOfSignUpSent;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date dateOfSignOutSent;

    @Column
    private String timeOfSignOutSent;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

@Override
public String toString() {
    return  "\nPOSLODAVAC" +
            "\n Tvrtka: " + getUser().getCompany() +
            "\n OIB: " + getUser().getOib() +
            "\n\nPODACI O RADNIKU" +
            "\n OIB: " + oib +
            "\n Ime: " + firstName +
            "\n Prezime: " + lastName +
            "\n Spol: " + gender +
            "\n Datum rođenja: " + dateOfBirth +
            "\n Adresa - Ulica i broj: " + address +
            "\n Grad i poštanski broj: " + city +
            "\n Stvarna stručna sprema: " + professionalQualification +
            "\n Radno mjesto: " + employmentPosition +
            "\n Mjesto rada - Grad: " + cityOfEmployment +
            "\n Potrebna stručna sprema: " + requiredProfessionalQualification +
            "\n Ugovor o radu: " + employmentContract +
            "\n Dodatni rad: " + additionalWork +
            "\n Dodatni rad - sati: " + additionalWorkHours +
            "\n Radno vrijeme: " + workingHours +
            "\n Sati nepuno: " + hoursForPartTime +
            "\n Neradni dani u tjednu: " + nonWorkingDays +
            "\n Datum prijave: " + dateOfSignUp +
            "\n Datum odjave - za određeno: " + dateOfSignOut +
            "\n Iznos osnovne plaće: " + basicSalary + "€" +
            "\n Strani državljanin: " + foreignNational +
            "\n Radna dozvola vrijedi do: " + expiryDateOfWorkPermit +
            "\n Umirovljenik: " + retiree +
            "\n Mladi od 30 godina: " + youngerThanThirty +
            "\n Prvo zaposlenje: " + firstEmployment +
            "\n Invalid: " + disability
            ;
}

    public boolean hasEmptyAttributes() {
        boolean isEmpty = Stream.of(oib, firstName, lastName, gender, dateOfBirth, address, city,
                        professionalQualification, employmentPosition, cityOfEmployment,
                        requiredProfessionalQualification, employmentContract, additionalWork,
                        workingHours, nonWorkingDays, dateOfSignUp, basicSalary, salaryType,
                        foreignNational, retiree, youngerThanThirty,firstEmployment, disability)
                .anyMatch(value -> value == null || (value instanceof String && ((String) value).isEmpty()));

        if (employmentContract.equals("Određeno") && ((dateOfSignOut == null || dateOfSignOut.toString().trim().isEmpty())
                || (reasonForDefinite == null || reasonForDefinite.trim().isEmpty()))) {
            isEmpty = true;
        }        else if (additionalWork && (additionalWorkHours == null || additionalWorkHours == 0)) {
            isEmpty = true;
        } else if (foreignNational && (expiryDateOfWorkPermit == null || expiryDateOfWorkPermit.toString().trim().isEmpty())) {
            isEmpty = true;
        }

        return isEmpty;
    }
}
