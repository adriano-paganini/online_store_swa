import { TUserDTO, UserxRole } from '../DTO/userx.types';
import { Badge } from './ui/badge';

/**
 * Renders the roles of a user as tags (such beautiful).
 * @param rowData
 */
export const rolesBodyTemplate = (rowData: TUserDTO) => {
  return (
    <>
      {rowData.roles.map((role: UserxRole) => {
        return (
          <Badge
            key={role}
            variant="default"
          >
            {role}
          </Badge>
        );
      })}
    </>
  );
};
