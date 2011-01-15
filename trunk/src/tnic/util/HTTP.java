package tnic.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class HTTP {
   public static String get (String urlStr) {
        try {
            StringBuilder buf = new StringBuilder();
            URL url = new URL(urlStr);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                buf.append(line);
            }
            reader.close();
            return buf.toString();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
   }
   /*
   public static byte[] binaryGet (urlStr) {

   }
   */
}
