package hr.betaSoft.security.secModel;

import hr.betaSoft.model.Employee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="users")
public class
User {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String username;

    @Column(nullable=false)
    private String oib;

    @Column(nullable=false)
    private String company;

    @Column(nullable=false)
    private String address;

    @Column(nullable=false)
    private String city;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false)
    private String telephone;

    @Column(nullable=false)
    private String email;

    @Column(nullable=false)
    private String emailToSend;

    @Column(nullable=false)
    private String password;

    @Column(nullable=false)
    private boolean showAllApplications;

    @OneToMany(mappedBy = "user")
    private List<Employee> employees;

    @ManyToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinTable(
            name="users_roles",
            joinColumns={@JoinColumn(name="USER_ID", referencedColumnName="ID")},
            inverseJoinColumns={@JoinColumn(name="ROLE_ID", referencedColumnName="ID")})
    private List<Role> roles = new ArrayList<>();

    @Override
    public String toString() {
        return "Korisnik: " + username + ", " + oib;
    }
}
