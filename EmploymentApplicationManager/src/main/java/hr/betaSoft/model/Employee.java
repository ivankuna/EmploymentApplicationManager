package hr.betaSoft.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import hr.betaSoft.security.model.User;
import hr.betaSoft.tools.FormTracker;
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
import java.util.Objects;
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

    public static final List<String> SALARY_TYPE = Arrays.asList("Neto", "Bruto");

    public static final List<String> REASON_FOR_DEFINITE = Arrays.asList("","povećani opseg poslova - sezona", "do nastupanja ili kraja događaja", "do izvršenja posla", "zamjena radnika");

    public static final List<String> REASON_FOR_SIGN_OUT = Arrays.asList("Istek Ugovora o radu ", "Sporazumni raskid", "Otkaz - poslovno uvjetovani", "Otkaz - osobno uvjetovani",
            "Ostvareno pravo na mirovinu", "Otkaz radnika", "Otkaz uvjetovan skrivljenim ponašanjem radnika", "Izvanredni otkaz", "Gubitak državljanstva ili istek radne dozvole");

    public static final List<String> REASON_FOR_UPDATE = Arrays.asList("određeno-neodređeno", "neodređeno-određeno", "nepuno-puno", "puno-nepuno", "broj sati nepuno",
            "stjecanje državljanstva", "stjecanje mirovine", "invalidnost", "mlađi od 30 godina","novo radno mjesto");

    public static final List<String> NON_WORKING_DAYS = Arrays.asList("Ponedjeljak", "Utorak", "Srijeda", "Četvrtak", "Petak", "Subota", "Nedjelja");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
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
    private BigDecimal additionalWorkHours;

    @Column
    private String workingHours;

    @Column
    private BigDecimal hoursForPartTime;

    @Column
    private List<String> nonWorkingDays;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date dateOfSignUp;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date dateOfSignOut;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date dateOfUpdate;

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
    private String timeOfUpdateSent;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date dateOfUpdateReal;

    @Column
    private List<String> reasonForUpdate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date dateOfSignOutReal;

    @Column
    private String reasonForSignOut;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private boolean fromSignUp;

    @Column
    private boolean fromSignOut;

    @Column
    private boolean fromUpdate;

    @Column
    private String noteSignUp;

    @Column
    private String noteSignOut;

    @Column
    private String noteUpdate;

    @Column
    private Integer numSignUp;

    @Column
    private Integer numSignOut;

    @Column
    private Integer numUpdate;

    private String numApp;

    private Date dateApp;

    private Date dateAppReal;

    private String timeApp;

    private boolean statusField;

    private String idApp;

    public String getCompany() {

        if (user != null) {
            return user.getCompany();
        }
        return null;
    }

    public void setCompany(String company) {

        if (user != null) {
            user.setCompany(company);
        }
    }

//    @Override
    public String toString(Integer currentFormId) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String emailTxt = "";
        String formattedDateOfBirth = "";
        String formattedDateOfSignUp = "";
        String formattedDateOfSignOut = "";
        String formattedExpiryDateOfWorkPermit = "";
        String formattedDateOfUpdate = "";
        String formattedDateOfSignOutReal = "";
        String formattedDateOfUpdateReal = "";

        if (dateOfBirth != null) {
            formattedDateOfBirth = sdf.format(dateOfBirth);
        }
        if (dateOfSignUp != null) {
            formattedDateOfSignUp = sdf.format(dateOfSignUp);
        }
        if (dateOfSignOut != null) {
            formattedDateOfSignOut = sdf.format(dateOfSignOut);
        }
        if (dateOfUpdate != null) {
            formattedDateOfSignOut = sdf.format(dateOfUpdate);
        }
        if (expiryDateOfWorkPermit != null) {
            formattedExpiryDateOfWorkPermit = sdf.format(expiryDateOfWorkPermit);
        }
        if (dateOfUpdateSent != null) {
            formattedDateOfUpdate = sdf.format(dateOfUpdateSent);
        }
        if (dateOfSignOutReal != null) {
            formattedDateOfSignOutReal = sdf.format(dateOfSignOutReal);
        }
        if (dateOfUpdateReal != null) {
            formattedDateOfUpdateReal = sdf.format(dateOfUpdateReal);
        }

//        if (FormTracker.getFormId() == FormTracker.getSIGN_UP()) {
        if (Objects.equals(currentFormId, FormTracker.getSIGN_UP())) {
            emailTxt =
                    " \nPOSLODAVAC" +
                            " \n Tvrtka: " + getUser().getCompany() +
                            " \n OIB: " + getUser().getOib() +
                            " \n\nPODACI O RADNIKU" +
                            " \n  1.  OIB: " + oib +
                            " \n  2.  Ime: " + firstName +
                            " \n  3.  Prezime: " + lastName +
                            " \n  4.  Spol: " + gender +
                            " \n  5.  Datum rođenja: " + formattedDateOfBirth +
                            " \n  6.  Adresa - Ulica i broj: " + address +
                            " \n  7.  Poštanski broj i grad: " + city +
                            " \n  8.  Stvarna stručna sprema: " + professionalQualification +
                            " \n  9.  Naziv najviše završene škole: " + highestLevelOfEducation +
                            " \n 10.  IBAN - tekuću račun - redovni: " + ibanRegular +
                            " \n 11.  IBAN - tekuću račun - zaštićeni: " + ibanProtected +
                            " \n 12.  Radno mjesto: " + employmentPosition +
                            " \n 13.  Mjesto rada - Grad: " + cityOfEmployment +
                            " \n 14.  Potrebna stručna sprema: " + requiredProfessionalQualification +
                            " \n 15.  Ugovor o radu: " + employmentContract +
                            " \n 16.  Razlog - na određeno: " + reasonForDefinite +
                            " \n 17.  Dodatni radi: " + additionalWork +
                            " \n 17a. Dodatni rad - sati: " + additionalWorkHours +
                            " \n 18.  Radno vrijeme: " + workingHours +
                            " \n 18a. Sati nepuno: " + hoursForPartTime +
                            " \n 19.  Neradni dani u tjednu: " + nonWorkingDays +
                            " \n 20.  Datum prijave: " + formattedDateOfSignUp +
                            " \n 21.  Datum odjave - za određeno: " + formattedDateOfSignOut +
                            " \n 22.  Iznos osnovne plaće: " + basicSalary + " " + salaryType +
                            " \n 23.  Strani državljanin: " + foreignNational +
                            " \n 23a. Radna dozvola vrijedi do: " + formattedExpiryDateOfWorkPermit +
                            " \n 24.  Umirovljenik: " + retiree +
                            " \n 25.  Mladi od 30 godina: " + youngerThanThirty +
                            " \n 26.  Prvo zaposlenje: " + firstEmployment +
                            " \n 27.  Invalid: " + disability +
                            " \n 28.  Napomena: " + noteSignUp
            ;
//        } else if (FormTracker.getFormId() == FormTracker.getSIGN_OUT()) {
        } else if (Objects.equals(currentFormId, FormTracker.getSIGN_OUT())) {
            emailTxt =
                    " \nPOSLODAVAC" +
                            " \n Tvrtka: " + getUser().getCompany() +
                            " \n OIB: " + getUser().getOib() +
                            " \n\nPODACI O RADNIKU" +
                            " \n  1.  OIB: " + oib +
                            " \n  2.  Ime: " + firstName +
                            " \n  3.  Prezime: " + lastName +
                            " \n  4.  Datum prijave: " + formattedDateOfSignUp +
                            " \n  5.  Datum zadnje promjene: " + formattedDateOfUpdate +
                            " \n  6.  Datum odjave - iz Prijave: " + formattedDateOfSignOut +
                            " \n  7.  Datum odjave - stvarni: " + formattedDateOfSignOutReal +
                            " \n  8.  Razlog odjave: " + reasonForSignOut +
                            " \n  9.  Napomena: " + noteSignOut
            ;
//        } else if (FormTracker.getFormId() == FormTracker.getUPDATE()) {
        } else if (Objects.equals(currentFormId, FormTracker.getUPDATE())) {
            emailTxt =
                    " \nPOSLODAVAC" +
                            " \n Tvrtka: " + getUser().getCompany() +
                            " \n OIB: " + getUser().getOib() +
                            " \n\nPODACI O RADNIKU" +
                            " \n  1.  OIB: " + oib +
                            " \n  2.  Ime: " + firstName +
                            " \n  3.  Prezime: " + lastName +
                            " \n  4.  Datum prijave: " + formattedDateOfSignUp +
                            " \n  5.  Datum zadnje promjene: " + formattedDateOfUpdate +
                            " \n  6.  Datum promjene: " + formattedDateOfUpdateReal +
                            " \n  7.  Razlog promjene: " + reasonForUpdate +
                            " \n  8.  Napomena: " + noteUpdate
            ;
        }

        String result = emailTxt.replaceAll("\\bnull\\b", "").replaceAll("\\btrue\\b", "DA").replaceAll("\\bfalse\\b", "NE");

        return result;
    }

    public List<String> hasEmptyAttributes(Integer formId) {

        String[] attributeValues = new String[]{};

        if (Objects.equals(formId, FormTracker.getSIGN_UP())) {
            attributeValues = new String[]{"OIB", "Ime", "Prezime", "Spol", "Datum rođenja", "Adresa", "Poštanski broj i grad", "Stvarna stručna sprema",
                                           "Radno mjesto", "Mjesto rada - Grad", "Potrebna stručna sprema", "Ugovor o radu",
                                           "Neradni dani u tjednu", "Datum prijave", "Iznos osnovne plaće", "Bruto / Neto"};
        } else if (Objects.equals(formId, FormTracker.getSIGN_OUT())) {
            attributeValues = new String[]{"OIB", "Ime", "Prezime", "Datum odjave - stvarni", "Razlog odjave"};
        } else if (Objects.equals(formId, FormTracker.getUPDATE())) {
            attributeValues = new String[]{"OIB", "Ime", "Prezime", "Datum promjene", "Razlog promjene"};
        }

        List<String> emptyAttributes = new ArrayList<>();

        Stream.of(attributeValues)
                .filter(attributeName -> {
                    Object value = getValueByName(attributeName);
                    if (value instanceof String) {
                        return ((String) value).isEmpty();
                    } else if (value instanceof List<?>) {
                        return ((List<?>) value).isEmpty();
                    }
                    return value == null;
                })
                .forEach(emptyAttributes::add);

        if (Objects.equals(formId, FormTracker.getSIGN_UP())) {
            if (employmentContract.equals("Određeno") && (dateOfSignOut == null || dateOfSignOut.toString().trim().isEmpty())) {
                emptyAttributes.add("Datum odjave");
            }
            if (employmentContract.equals("Određeno") && (reasonForDefinite == null || reasonForDefinite.trim().isEmpty())) {
                emptyAttributes.add("Razlog - na određeno");
            }
            if (additionalWork && (additionalWorkHours == null || additionalWorkHours.compareTo(BigDecimal.ZERO) == 0)) {
                emptyAttributes.add("Dodatni rad - sati");
            }
            if (!additionalWork && workingHours.equals("Nepuno") && (hoursForPartTime == null || hoursForPartTime.compareTo(BigDecimal.ZERO) == 0)) {
                emptyAttributes.add("Sati nepuno");
            }
            if (foreignNational && (expiryDateOfWorkPermit == null || expiryDateOfWorkPermit.toString().trim().isEmpty())) {
                emptyAttributes.add("Radna dozvola vrijedi do");
            }
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
            case "Poštanski broj i grad":
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
            case "Sati nepuno":
                return hoursForPartTime;
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
            case "Datum zadnje promjene":
                return dateOfUpdate;
            case "Datum odjave - iz Prijave":
                return dateOfSignOut;
            case "Datum odjave - stvarni":
                return dateOfSignOutReal;
            case "Razlog odjave":
                return reasonForSignOut;
            case "Datum promjene":
                return dateOfUpdateReal;
            case "Razlog promjene":
                return reasonForUpdate;
            default:
                throw new IllegalArgumentException("Unknown attribute name: " + attributeName);
        }
    }
}