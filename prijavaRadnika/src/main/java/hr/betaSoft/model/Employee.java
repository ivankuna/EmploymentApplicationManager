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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employees")
public class Employee {

    private static final long serialVersionUID = 1L;

    public static final List<String> GENDER = Arrays.asList("Muško", "Žensko");

    public static final List<String> PROFESSIONAL_QUALIFICATION = Arrays.asList("NKV", "PKV", "NSS", "KV", "SSS", "VKV", "VŠS", "VSS");

    public static final List<String> EMPLOYMENT_CONTRACT = Arrays.asList("Određeno", "Neodređeno");

    public static final List<String> WORKING_HOURS = Arrays.asList("Puno", "Nepuno");

    public static final List<String> SALARY_TYPE = Arrays.asList("Bruto", "Neto");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column
    private String gender;

    @Column(unique=true)
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

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date dateOfSignUpSent;

    @Column
    private String timeOfSignUpSent;

    @Column
    private boolean signOutSent;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date dateOfSignOutSent;

    @Column
    private String timeOfSignOutSent;

    @Column
    private boolean updateSent;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date dateOfUpdateSent;

    @Column
    private String timeUpdateSent;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private boolean fromSignUp;

    @Column
    private boolean fromSignOut;

    @Column
    private boolean fromUpdate;


    @Override
    public String toString() {

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String formattedDateOfBirth = "";
        String formattedDateOfSignUp = "";
        String formattedDateOfSignOut = "";
        String formattedExpiryDateOfWorkPermit = "";

        if (dateOfBirth != null) {
            formattedDateOfBirth = sdf.format(dateOfBirth);
        }
        if (dateOfSignUp != null) {
            formattedDateOfSignUp = sdf.format(dateOfSignUp);
        }
        if (dateOfSignOut != null) {
            formattedDateOfSignOut = sdf.format(dateOfSignOut);
        }
        if (expiryDateOfWorkPermit != null) {
            formattedExpiryDateOfWorkPermit = sdf.format(expiryDateOfWorkPermit);
        }

        String emailTxt =
                "\nPOSLODAVAC" +
                "\n Tvrtka: " + getUser().getCompany() +
                "\n OIB: " + getUser().getOib() +
                "\n\nPODACI O RADNIKU" +
                "\n  1.  OIB: " + oib +
                "\n  2.  Ime: " + firstName +
                "\n  3.  Prezime: " + lastName +
                "\n  4.  Spol: " + gender +
                "\n  5.  Datum rođenja: " + formattedDateOfBirth +
                "\n  6.  Adresa - Ulica i broj: " + address +
                "\n  7.  Grad i poštanski broj: " + city +
                "\n  8.  Stvarna stručna sprema: " + professionalQualification +
                "\n  9.  Naziv najviše završene škole: " + highestLevelOfEducation +
                "\n 10.  IBAN - tekuću račun - redovni: " + ibanRegular +
                "\n 11.  IBAN - tekuću račun - zaštićeni: " + ibanProtected +
                "\n 12.  Radno mjesto: " + employmentPosition +
                "\n 13.  Mjesto rada - Grad: " + cityOfEmployment +
                "\n 14.  Potrebna stručna sprema: " + requiredProfessionalQualification +
                "\n 15.  Ugovor o radu: " + employmentContract +
                "\n 16.  Razlog - na određeno: " + reasonForDefinite +
                "\n 17.  Dodatni radi: " + additionalWork +
                "\n 17a. Dodatni rad - sati: " + additionalWorkHours +
                "\n 18.  Radno vrijeme: " + workingHours +
                "\n 18a. Sati nepuno: " + hoursForPartTime +
                "\n 19.  Neradni dani u tjednu: " + nonWorkingDays +
                "\n 20.  Datum prijave: " + formattedDateOfSignUp +
                "\n 21.  Datum odjave - za određeno: " + formattedDateOfSignOut +
                "\n 22.  Iznos osnovne plaće: " + basicSalary + " " + salaryType +
                "\n 23.  Strani državljanin: " + foreignNational +
                "\n 23a. Radna dozvola vrijedi do: " + formattedExpiryDateOfWorkPermit +
                "\n 24.  Umirovljenik: " + retiree +
                "\n 25.  Mladi od 30 godina: " + youngerThanThirty +
                "\n 26.  Prvo zaposlenje: " + firstEmployment +
                "\n 27.  Invalid: " + disability +
                "\n 28.  Napomena: " + note
                ;

        String result = emailTxt.replaceAll("\\bnull\\b", "").replaceAll("\\btrue\\b", "DA").replaceAll("\\bfalse\\b", "NE");
        return result;
    }

    public List<String> hasEmptyAttributes() {

        List<String> emptyAttributes = new ArrayList<>();

        Stream.of("OIB", "Ime", "Prezime", "Spol", "Datum rođenja", "Adresa", "Grad i poštanski broj", "Stvarna stručna sprema",
                        "Radno mjesto", "Mjesto rada - Grad", "Potrebna stručna sprema", "Ugovor o radu", "Radno vrijeme",
                        "Neradni dani u tjednu", "Datum prijave", "Iznos osnovne plaće", "Bruto / Neto")
                .filter(attributeName -> {
                    Object value = getValueByName(attributeName);
                    return value == null || (value instanceof String && ((String) value).isEmpty());
                })
                .forEach(emptyAttributes::add);

        if (employmentContract.equals("Određeno") && (dateOfSignOut == null || dateOfSignOut.toString().trim().isEmpty())) {
            emptyAttributes.add("Datum odjave");
        }
        if (employmentContract.equals("Određeno") && (reasonForDefinite == null || reasonForDefinite.trim().isEmpty())) {
            emptyAttributes.add("Razlog - na određeno");
        }
        if (additionalWork && (additionalWorkHours == null || additionalWorkHours == 0)) {
            emptyAttributes.add("Dodatni rad - sati");
        } else if (foreignNational && (expiryDateOfWorkPermit == null || expiryDateOfWorkPermit.toString().trim().isEmpty())) {
            emptyAttributes.add("Radna dozvola vrijedi do");
        }

        return emptyAttributes;
    }

    private Object getValueByName(String attributeName) {
        switch (attributeName) {
            case "OIB":
                return oib;
            case "Ime":
                return firstName;
            case "Prezime":
                return lastName;
            case "Spol":
                return gender;
            case "Datum rođenja":
                return dateOfBirth;
            case "Adresa":
                return address;
            case "Grad i poštanski broj":
                return city;
            case "Stvarna stručna sprema":
                return professionalQualification;
            case "Radno mjesto":
                return employmentPosition;
            case "Mjesto rada - Grad":
                return cityOfEmployment;
            case "Potrebna stručna sprema":
                return requiredProfessionalQualification;
            case "Ugovor o radu":
                return employmentContract;
            case "Dodatni rad":
                return additionalWork;
            case "Radno vrijeme":
                return workingHours;
            case "Neradni dani u tjednu":
                return nonWorkingDays;
            case "Datum prijave":
                return dateOfSignUp;
            case "Iznos osnovne plaće":
                return basicSalary;
            case "Bruto / Neto":
                return salaryType;
            case "Strani državljanin":
                return foreignNational;
            case "Umirovljenik":
                return retiree;
            case "Mladi od 30 godina":
                return youngerThanThirty;
            case "Prvo zaposlenje":
                return firstEmployment;
            case "Invalid":
                return disability;
            default:
                throw new IllegalArgumentException("Unknown attribute name: " + attributeName);
        }
    }
}