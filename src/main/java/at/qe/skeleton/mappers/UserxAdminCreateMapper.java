package at.qe.skeleton.mappers;

import at.qe.skeleton.dtos.UserxAdminCreateDTO;
import at.qe.skeleton.model.Userx;
import org.springframework.stereotype.Service;

/**
 * Mapping between UserxCreateDTO and UserxTypes.
 * This class is part of the skeleton project provided for students of the
 * course "Software Architecture" offered by Innsbruck University.
 */
@Service
public class UserxAdminCreateMapper implements DTOMapper<Userx, UserxAdminCreateDTO> {

    @Override
    public UserxAdminCreateDTO mapTo(Userx entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Userx mapFrom(UserxAdminCreateDTO dto) {
        Userx user = new Userx();
        user.setUsername(dto.username());
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setPhone(dto.phone());
        //TODO: REMOVE ENABLED COMPLETELY
        user.setEnabled(false);
        user.setRoles(dto.roles());

        return user;
    }

}
