package examples;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class getDevInfo {
    public static final String PATH_TO_VENDORS_MAC_DATA =
            "/home/konstantin/ProgramFiles/nethacking-master/vendors MAC database/oui.txt";

    public String getDeviceInfo(String macAddress) throws IOException {
        try {
            String mac = macAddress.replaceAll("\\s", "");
            mac = mac.substring(0, 6).toLowerCase();
            BufferedReader reader = new BufferedReader(new FileReader(PATH_TO_VENDORS_MAC_DATA));
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }

                if (line.toLowerCase().startsWith(mac)) {
                    reader.close();
                    return getVendorsName(line) + ": " + macAddress;
                }

            }
        } catch (FileNotFoundException e) {
            System.out.println("Ooops... There are problems with file reading! ");
        }
        return "Unknown device: " + macAddress;
    }

    private String getVendorsName(String line) {
        line = line.substring(22);
        String[] splitLine = line.split(" ");
        line = splitLine[0] + " " + splitLine[1];
        return line;
    }


}
