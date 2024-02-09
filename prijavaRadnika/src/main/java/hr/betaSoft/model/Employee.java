package hr.betaSoft.model;

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

    @Column(nullable=false)
    private String gender;

    @Column(nullable=false)
    private String oib;

    @Column(nullable=true)
    private String jmbg;

    @Column(nullable=false)
    private Date dateOfBirth;

    @Column(nullable=false)
    private String address;

    @Column(nullable=false)
    private String city;

    @Column(nullable=true)
    private String hzmoInsuranceNumber;

    @Column(nullable=false)
    private String highestProfessionalQualification;

    @Column(nullable=false)
    private String highestLevelOfEducation;

    @Column(nullable=false)
    private String employmentPosition;

    @Column(nullable=false)
    private String employmentContract;

    @Column(nullable=true)
    private String reasonForDefinite;

    @Column(nullable=false)
    private String workingHours;

    @Column(nullable=true)
    private Integer hoursForPartTime;

    @Column(nullable=false)
    private Date dateOfSignUp;

    @Column(nullable=true)
    private Date dateOfSignOut;

    @Column(nullable=false)
    private BigDecimal basicSalary;

    @Column(nullable=false)
    private String salaryType;

    @Column(nullable=false)
    private boolean foreignNational;

    @Column(nullable=true)
    private Date expiryDateOfWorkPermit;

    @Column(nullable=false)
    private boolean retiree;

    @Column(nullable=false)
    private boolean youngerThanThirty;

    @Column(nullable=false)
    private boolean firstEmployment;

    @Column(nullable=false)
    private boolean disability;

    @Column(nullable=true)
    private String note;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


}
