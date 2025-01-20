import { Role, User } from "@/models/Users";
import { useAuth0 } from "@auth0/auth0-react";
import { createContext, useContext, useEffect, useState } from "react";
import { useUserService } from "@/services/userService";

interface UserContextInterface {
  user: User | undefined;
}

const UserContext = createContext<UserContextInterface | undefined>(undefined);

export const useUserContext = () => {
  const context = useContext(UserContext);
  if (context === undefined) {
    throw new Error("useUserContext got no context!");
  }
  return context;
};

type Props = {
  children: JSX.Element;
};

export const UserProvider = ({ children }: Props) => {
  const userService = useUserService();
  const { user, isAuthenticated } = useAuth0();
  const [lvhUser, setLvhUser] = useState<User | undefined>();

  useEffect(() => {
    if (isAuthenticated && user) {
      userService
        .getSessionUser()
        .then((res) => {
          console.log("Setting user");
          console.log(lvhUser);
          setLvhUser(res.data);
        })
        .catch(() => {
          console.log(user);
          console.log("Error fetching session user");
        });
    }
    // only run the call if the auth0 state changes
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [user, isAuthenticated]);

  return (
    <UserContext.Provider value={{ user: lvhUser }}>
      {children}
    </UserContext.Provider>
  );
};
