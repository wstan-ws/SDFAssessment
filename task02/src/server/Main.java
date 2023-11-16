package server;

import java.net.Socket;

public class Main {

    public static void main(String[] args) throws Exception {

        int port = 0;
        String ip = null;

        if (args.length == 2) {
            ip = args[0];
            port = Integer.parseInt(args[1]);
        } else if (args.length == 1) {
            ip = "localhost";
            port = Integer.parseInt(args[0]);
        } else if (args.length <= 0) {
            ip = "localhost";
            port = 3000;
        } else {
            System.err.print("Too many arguments entered");
            System.exit(1);
        }

        System.out.printf("Connecting to %s...\n", ip);
        Socket socket = new Socket(ip, port);
        System.out.printf("Connected! Listening on port %d\n", port);

        Sorter sorter = new Sorter();
        sorter.read(socket);
        sorter.sort();
        sorter.pick();  
        sorter.write(socket);
        sorter.readResult(socket);
        
        socket.close();

    }    
}
