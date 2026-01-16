package at.qe.skeleton.mappers;

import at.qe.skeleton.dtos.UserxMeDTO;
import at.qe.skeleton.dtos.UserxMeUpdateDTO;
import at.qe.skeleton.model.Userx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserxMeMapper {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserxMeMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    public UserxMeDTO mapTo(Userx user) {
        if (user == null) {
            return null;
        }
        return new UserxMeDTO(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getRoles()
        );
    }

    public void apply(Userx user, UserxMeUpdateDTO dto) {
        if (dto.username() != null) user.setUsername(dto.username());
        if (dto.firstName() != null) user.setFirstName(dto.firstName());
        if (dto.lastName() != null) user.setLastName(dto.lastName());
        if (dto.email() != null) user.setEmail(dto.email());
        if (dto.phone() != null) user.setPhone(dto.phone());
        if (dto.password() != null) user.setPassword(passwordEncoder.encode(dto.password()));
    }
}
