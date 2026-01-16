package at.qe.skeleton.mappers;

import at.qe.skeleton.dtos.UserxDTO;
import at.qe.skeleton.dtos.UserxMeDTO;
import at.qe.skeleton.dtos.UserxUpdateDTO;
import at.qe.skeleton.model.Userx;
import at.qe.skeleton.services.UserxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Mapping between UserxTypes and UserxDTOs.
 * This class is part of the skeleton project provided for students of the course "Software
 * Architecture" offered by Innsbruck University.
 */
@Service
public class UserxMapper {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserxMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public UserxDTO mapTo(Userx user) {
        if (user == null) {
            return null;
        }
        Userx creator = user.getCreateUser();
        UserxDTO dto = new UserxDTO(
                user.getId(), 
                creator != null ? creator.getId() : null,
                user.getCreateDate(), 
                user.getUpdateUser() != null ? user.getUpdateUser().getId() : null, 
                user.getUpdateDate(),
                user.getUsername(), 
                user.getFirstName(), 
                user.getLastName(), 
                user.getEmail(), 
                user.getPhone(), 
                user.isEnabled(),
                user.isDeleted(),
                user.getRoles(),
                user.getChannels()
        );
        
        return dto;
    }

    public void apply(Userx user, UserxUpdateDTO dto) {
        if (dto.firstName() != null) user.setFirstName(dto.firstName());
        if (dto.lastName() != null) user.setLastName(dto.lastName());
        if (dto.email() != null) user.setEmail(dto.email());
        if (dto.phone() != null) user.setPhone(dto.phone());
        if (dto.password() != null) user.setPassword(passwordEncoder.encode(dto.password()));
        if (dto.roles() != null && !dto.roles().isEmpty()) user.setRoles(dto.roles());
    }
}