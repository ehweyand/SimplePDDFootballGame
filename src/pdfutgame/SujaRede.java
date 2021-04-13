package pdfutgame;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SujaRede {

    public static DatagramSocket socket = null;
    public static final int porta = 8000;
    public static InetAddress address = null;
    public static DatagramPacket packet = null;
    public static byte[] buffer = null;

    public static void enviaMensagem(String s) {
        try {
            socket = new DatagramSocket();
            address = InetAddress.getByName("192.168.0.9");
            socket.setBroadcast(true);

            buffer = s.getBytes();
            packet = new DatagramPacket(buffer, buffer.length, address, porta);
            socket.send(packet);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
