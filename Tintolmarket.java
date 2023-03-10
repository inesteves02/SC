import java.util.Scanner;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Tintolmarket {
    
    private static ObjectInputStream in;
    private static ObjectOutputStream out;

    // enviar user e pass ao server
    public static void main(String[] args) {
        try {
            // iniciar socket
            Socket clientSocket = new Socket("localhost", 23456);

            // iniciar streams
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());

            if (clientLogin()){
                clientInteraction();
            }

            // fechar streams
            in.close();
            out.close();
        } catch (Exception e) {
            System.err.println("Erro");
        }

    }

    private boolean clientLogin() {
        try {
            Scanner sc = new Scanner(System.in);

            System.out.print("Inserir username: ");
            String user = sc.nextLine();

            System.out.print("Inserir password: ");
            String pass = sc.nextLine();

            out.writeObject(user);
            out.writeObject(pass);

            boolean answer = (Boolean) in.readObject();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clientInteraction() {
       
    } 
}
