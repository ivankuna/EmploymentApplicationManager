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
    private String highestProfessionalQualification;

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
    private String requiredProfessionalQualifications;

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
}
