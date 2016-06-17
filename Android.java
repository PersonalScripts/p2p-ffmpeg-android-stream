         private String establishP2PConnection() throws IOException {
 
            String p2pConnectionURL;
 
            DatagramSocket clientSocket = new DatagramSocket();
 
            // prepare Data
            byte[] sendData = "Hello Server this is Android :)".getBytes();
 
            // send Data to Server with fix IP (X.X.X.X)
            // Client1 uses port 1234, Client2 uses port 1235
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("public-ip-address.com"), 1235);
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
 
            p2pConnectionURL = "udp://"+ip+":"+port+"?localport="+localPort;
            clientSocket.close();
            return p2pConnectionURL;
        }