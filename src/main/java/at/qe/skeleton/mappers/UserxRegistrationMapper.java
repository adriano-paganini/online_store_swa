package at.qe.skeleton.mappers;

import at.qe.skeleton.dtos.UserxRegistrationDTO;
import at.qe.skeleton.model.Userx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserxRegistrationMapper {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserxRegistrationMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    public Userx mapFrom(UserxRegistrationDTO dto) {
        Userx user = new Userx();
        user.setUsername(dto.username());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setEmail(dto.email());
        user.setPhone(dto.phone());
        return user;
    }
}
