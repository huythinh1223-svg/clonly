package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class SignupClinet {

    public String createAccount(
            String full,
            String user,
            String mail,
            String pass
    ) throws Exception {

        Socket socket = new Socket("localhost", 8888);

        DataOutputStream out =
                new DataOutputStream(socket.getOutputStream());

        DataInputStream in =
                new DataInputStream(socket.getInputStream());

        out.writeUTF("CREATE_ACCOUNT");

        out.writeUTF(full);
        out.writeUTF(user);
        out.writeUTF(mail);
        out.writeUTF(pass);

        String response = in.readUTF();

        socket.close();

        return response;
    }
}