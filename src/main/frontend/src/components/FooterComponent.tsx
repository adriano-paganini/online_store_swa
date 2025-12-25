import React from 'react';
import { useUser } from '../Contexts/authenticatedUserContext';
import { rolesBodyTemplate } from './rolesBodyTemplate';

export const FooterComponent: React.FC = () => {
  // get user context
  const { currentUser } = useUser();

  return (
    <footer className="border-lightgrey fixed bottom-0 left-0 flex h-[45px] w-full items-center justify-between border-t bg-[#fff8f3] text-black">
      <span className="px-5">
        Logged in as: {currentUser?.firstName} {currentUser?.lastName} ({currentUser?.username})
      </span>
      <span className="flex flex-row items-center gap-2 px-5">
        Roles:&ensp; {currentUser ? rolesBodyTemplate(currentUser) : null}
      </span>
    </footer>
  );
};
