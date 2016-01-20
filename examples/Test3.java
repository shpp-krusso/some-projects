package examples;


import pcap.Convert;
import pcap.Pcap;
import pcap.Threads;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;

public class Test3 {

    public static void main(String[] args) throws IOException {
        String iface = "wlan0";

        System.out.println("There are devices detected in your network: ");

        Closeable c = Pcap.listen(iface, new Pcap.Listener() {
            ArrayList<String> exist = new ArrayList<>();
            public void onPacket(byte[] bytes) {

                String macAddress = Convert.bytes2hex(bytes);
                macAddress = macAddress.substring(18, 35);

                if (!exist.contains(macAddress)) {
                    exist.add(macAddress);
                    getDevInfo devInfo = new getDevInfo();
                    try {
                        System.out.println("<<< " + devInfo.getDeviceInfo(macAddress));
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        });

        String sourceMac = Convert.bytes2hex(Pcap.get(iface).getLinkLayerAddresses().get(0).getAddress());
        String sourceIp = Convert.dec2hex("192.168.1.71");
        String targetMac = "ff:ff:ff ff:ff:ff";

        for (int i = 0; i < 256; i++) {

            String targetIp = Convert.dec2hex("192.168.1." + i);

            byte[] packet = Convert.hex2bytes( // ----- Ethernet
                    targetMac,                 // Destination: ff:ff:ff:ff:ff:ff
                    sourceMac,                 // Source: __:__:__:__:__:__
                    "08 06",                   // Type: ARP (0x0806)
                    // ----- ARP
                    "00 01",                   // Hardware type: Ethernet (1)
                    "08 00",                   // Protocol type: IPv4 (0x0800)
                    "06",                      // Hardware size: 6
                    "04",                      // Protocol size: 4
                    "00 01",                   // Opcode: request (1)
                    sourceMac,                 // Sender MAC address: 6 bytes
                    sourceIp,                  // Sender IP address:  4 bytes
                    targetMac,                 // Target MAC address: 6 bytes
                    targetIp                   // Target IP address:  4 bytes
            );

            //System.out.println("Sending [" + Convert.bytes2hex(packet) + "]...");
            Threads.sleep(100);

            Pcap.send(iface, packet);
        }

        System.err.println("Press enter to shutdown");
        System.in.read();
        c.close();
    }
}
