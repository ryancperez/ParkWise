package com.example.parkwise;


public class motorActivation {


    public void run(int signal) {
        if(signal == 3)
            System.out.println("OPENED!");
        else
            System.out.println("CLOSED!");
//        String serverAddress = "192.168.1.58";
//        int serverPort = 8080;
//        Socket socket = null;
//        DataOutputStream outputStream = null;
//        try {
//            System.out.println("Attempting to connect to Arduino...");
//            socket = new Socket(serverAddress, serverPort);
//
//            if (socket.isConnected()) {
//                System.out.println("Connected to Arduino.");
//                outputStream = new DataOutputStream(socket.getOutputStream());
//
//                // Send integers to the Arduino
//                int commandInt = signal; // Replace with the integer you want to send (to activate send 3)
//                outputStream.writeShort((short) commandInt); // Send a 16-bit integer
//                outputStream.flush();
//                System.out.println("Integer sent to Arduino: " + commandInt);
//            } else {
//                System.out.println("Failed to connect to Arduino.");
//            }
//        }  catch (IOException e) {
//            System.err.println("Error while connecting to Arduino: " + e.getMessage());
//        } finally {
//            try {
//                if (outputStream != null) {
//                    outputStream.close();
//                }
//                if (socket != null) {
//                    socket.close();
//                }
//            } catch (IOException e) {
//                System.err.println("Error while closing resources: " + e.getMessage());
//            }
//        }
    }
}
