package hr.betaSoft.security.userdto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto
{
    private Long id;

    @NotEmpty(message = "Korisničko ime mora biti upisano!")
    private String username;

    @NotEmpty(message = "OIB mora biti upisan!")
    private String oib;

    @NotEmpty(message = "Naziv firme mora biti upisan!")
    private String company;

    @NotEmpty(message = "Adresa mora biti upisana!")
    private String address;

    @NotEmpty(message = "Naziv grada mora biti upisan!")
    private String city;

    @NotEmpty(message = "Ime mora biti upisano!")
    private String firstName;

    @NotEmpty(message = "Prezime mora biti upisano!")
    private String lastName;

    @NotEmpty(message = "Broj telefona mora biti upisan!")
    private String telephone;

    @NotEmpty(message = "Email mora biti upisan!")
    @Email
    private String email;

    @NotEmpty(message = "Email za slanje mora biti upisan!")
    @Email
    private String emailToSend;

    @NotEmpty(message = "Lozinka mora biti upisana!")
    private String password;
}
