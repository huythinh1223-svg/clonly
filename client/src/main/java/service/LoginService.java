package service;

import network.LoginClient;

public class LoginService {

    private final LoginClient loginClient = new LoginClient();

    public String login(
            String username,
            String password
    ) throws Exception {

        return loginClient.login(
                username,
                password
        );
    }
}