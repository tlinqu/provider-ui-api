package gov.samhsa.c2s.provideruiapi.service.dto;

import gov.samhsa.c2s.provideruiapi.infrastructure.dto.IdentifierDto;
import gov.samhsa.c2s.provideruiapi.infrastructure.dto.UmsAddressDto;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.List;

@Data
public class UserDto {
    private Long id;

    @NotBlank
    private String lastName;

    private String middleName;

    @NotBlank
    private String firstName;

    private String homeEmail;

    private String workEmail;

    @Past
    @NotNull
    private LocalDate birthDate;

    @NotBlank
    private String genderCode;

    private String socialSecurityNumber;

    private String homePhone;

    private String workPhone;

    private UmsAddressDto homeAddress;

    private UmsAddressDto workAddress;

    @NotNull
    private List<String> roles;

    @NotBlank
    private String locale;

    private boolean disabled;

    private String mrn;

    @Valid
    private List<IdentifierDto> identifiers;
}
