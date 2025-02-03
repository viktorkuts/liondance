import { User } from "@/models/Users";
import { useAuth0 } from "@auth0/auth0-react";
import { createContext, useContext, useEffect, useState } from "react";
import { useUserService } from "@/services/userService";
import { useTranslation } from "react-i18next";

interface UserContextInterface {
  user: User | undefined;
  isLoading: boolean;
}

const UserContext = createContext<UserContextInterface | undefined>(undefined);

export const useUserContext = () => {
  const { t } = useTranslation();
  const context = useContext(UserContext);
  if (context === undefined) {
    throw new Error(t("useUserContext got no context!"));
  }
  return context;
};

type Props = {
  children: JSX.Element;
};

export const UserProvider = ({ children }: Props) => {
  const { t } = useTranslation();
  const userService = useUserService();
  const { user, isAuthenticated } = useAuth0();
  const [lvhUser, setLvhUser] = useState<User | undefined>();
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
    setIsLoading(true);
    if (isAuthenticated && user) {
      userService
        .getSessionUser()
        .then((res) => {
          console.log(t("Setting user"));
          console.log(lvhUser);
          setLvhUser(res.data);
        })
        .catch(() => {
          console.log(user);
          console.log(t("Error fetching session user"));
        });
    }
    setIsLoading(false);
    // only run the call if the auth0 state changes
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [user, isAuthenticated]);

  return (
    <UserContext.Provider value={{ user: lvhUser, isLoading: isLoading }}>
      {children}
    </UserContext.Provider>
  );
};
