package service;

import network.SignupClient;

public class AuthService {

    private SignupClient authClient =
            new SignupClient();

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