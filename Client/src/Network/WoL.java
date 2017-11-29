package Network;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

public class WoL {

    private final int PORT = 9;
    private Logger logger;
    private DatagramPacket packet;

    public WoL(Logger logger){
        this.logger = logger;
    }

    public void wake(String ip, String mac){
        try{
            DatagramSocket socket = new DatagramSocket();
            socket.send(getPacket(ip, mac));
            socket.close();
        }
        catch (Exception e) {
            logger.info("wol failed to send packet, " + e.toString());
        }
    }

    public String formatBroadcastIP() throws Exception{
        List<String> ipList = new ArrayList<>();
        String finalIp = "";

        Enumeration<NetworkInterface> iFaces = NetworkInterface.getNetworkInterfaces();

        while(iFaces.hasMoreElements()){
            NetworkInterface networkInterface = iFaces.nextElement();
            if(networkInterface.isUp()){
                Enumeration<InetAddress> ips = networkInterface.getInetAddresses();
                while (ips.hasMoreElements()){
                    ipList.add(ips.nextElement().getHostAddress());
                }
            }
        }

        for(String ip : ipList){
            if(ip.startsWith("192") || ip.startsWith("172") || ip.startsWith("10")){
                finalIp = ip;
            }
        }

        if(!finalIp.isEmpty()) {
            int dot = finalIp.lastIndexOf('.');
            finalIp = finalIp.substring(0, dot + 1);
            return finalIp + "255";
        }

        return finalIp;
    }

    public byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macStr.split("(\\:|\\-)");

        if (hex.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address.");
        }

        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in MAC address.");
        }

        return bytes;
    }

    public DatagramPacket getPacket(String ip, String mac) {
        try {
            byte[] macBytes = getMacBytes(mac);
            byte[] bytes = new byte[6 + 16 * macBytes.length];
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) 0xff;
            }
            for (int i = 6; i < bytes.length; i += macBytes.length) {
                System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
            }

            InetAddress address = InetAddress.getByName(ip);
            packet = new DatagramPacket(bytes, bytes.length, address, PORT);
        }
        catch (Exception e) {
            logger.info("wol failed to format packet, " + e.toString());
        }

        return packet;
    }
}
