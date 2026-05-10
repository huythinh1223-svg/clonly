package network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class LoginClient {

    public String login(
            String username,
            String password
    ) throws Exception {

        Socket socket = new Socket("localhost", 8888);

        PrintWriter out =
                new PrintWriter(
                        socket.getOutputStream(),
                        true
                );

        BufferedReader in =
                new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()
                        )
                );

        String request =
                "LOGIN|" + username + "|" + password;

        out.println(request);

        String response = in.readLine();

        socket.close();

        return response;
    }
}