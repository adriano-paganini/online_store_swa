import { UserDTO } from '../DTO/userx.types';
import { Badge } from './ui/badge';

/**
 * Renders the roles of a user as tags (such beautiful).
 * @param rowData
 */
export const rolesBodyTemplate = (rowData: UserDTO) => {
  return (
    <>
      {rowData.roles.map((role) => {
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
