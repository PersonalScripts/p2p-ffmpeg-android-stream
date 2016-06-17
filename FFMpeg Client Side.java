package com.company;
 
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
 
public class ClientSide {
 
    public static void main(String[] args) throws IOException, InterruptedException {
 
        DatagramSocket clientSocket = new DatagramSocket();
 
        // prepare Data
        byte[] sendData = "Hello".getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("public-ip-address.com"),
                1234);
        clientSocket.send(sendPacket);
       
        // receive Data ==> Format:"<IP of other Client>-<Port of other Client>"
        DatagramPacket receivePacket = new DatagramPacket(new byte[1024], 1024);
        clientSocket.receive(receivePacket);
 
        // Convert Response to IP and Port
        String response = new String(receivePacket.getData());
        String[] splitResponse = response.split("-");
        InetAddress ip = InetAddress.getByName(splitResponse[0].substring(1));
 
        int port = Integer.parseInt(splitResponse[1]);
 
        // output converted Data for check
        System.out.println("IP: " + ip + " PORT: " + port);
 
        // close socket and open new socket with SAME localport
        int localPort = clientSocket.getLocalPort();
        clientSocket.close();
        clientSocket = new DatagramSocket(localPort);
 
        // set Timeout for receiving Data
        clientSocket.setSoTimeout(1000);
 
        clientSocket.close();
 
        String path = "E:\\ffmpeg-win64-static\\bin";
        String commands = "ffmpeg" + " -f dshow -video_size 640x360 -rtbufsize 702000k -framerate 30 "
                + "-i video=\"Integrated Camera\":audio=\"Microphone (5- Logitech USB Headset H340)\" "
                + "-r 30 -threads 4 -vcodec libx264 -pix_fmt yuv420p -tune zerolatency -preset ultrafast" + " -f mpegts udp:/" + ip + ":" + port + "?localport="
                + localPort;
 
        runScript(path, commands);
    }
 
    private static boolean runScript(String path, String cmd) throws IOException, InterruptedException {
        List<String> commands = new ArrayList<>();
        commands.add("cmd");
        commands.add("/c");
        commands.add(cmd);
        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.directory(new File(path));
        pb.redirectErrorStream(true);
        Process process = pb.start();
        flushInputStreamReader(process);
        int exitCode = process.waitFor();
        return exitCode == 0;
    }
 
    private static void flushInputStreamReader(Process process) throws IOException, InterruptedException {
        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = input.readLine()) != null) {
            System.out.println(line);
        }
    }
}