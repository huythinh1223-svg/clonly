package service;

import network.SignupClinet;

public class AuthService {

    private SignupClinet authClient =
            new SignupClinet();

    public String createAccount(
            String full,
            String user,
            String mail,
            String pass
    ) throws Exception {

        return authClient.createAccount(
                full,
                user,
                mail,
                pass
        );
    }
}