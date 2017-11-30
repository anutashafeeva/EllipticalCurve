import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Decryption {

    public Decryption() throws IOException {
        BufferedReader in_encr = new BufferedReader(new FileReader("cipher.txt"));
        BufferedReader in_p = new BufferedReader(new FileReader("publicKey.txt"));
        BufferedReader in_s = new BufferedReader(new FileReader("secretKey.txt"));
        PrintWriter out_decr = new PrintWriter(new FileWriter("M1.txt"));

        BigInteger n = new BigInteger(in_p.readLine());
        BigInteger e = new BigInteger(in_p.readLine());
        BigInteger d = new BigInteger(in_s.readLine());

        String dd = "";
        while (d.compareTo(BigInteger.ONE) > 0) {
            dd += String.valueOf(d.mod(BigInteger.valueOf(2)));
            d = d.divide(BigInteger.valueOf(2));
        }
        dd += d;

        List<BigInteger> points = new ArrayList<>();
        String s = "";
        while ((s = in_encr.readLine()) != null)
            points.add(new BigInteger(s));

        StringBuilder sb = new StringBuilder("");
        for (int ind = 0; ind < points.size(); ind += 2) {


            BigInteger m = points.get(ind);
            BigInteger y = points.get(ind + 1);

            List<BigInteger> ans = new ArrayList<>();

            BigInteger x1 = null, y1 = null;
            BigInteger x2 = m, y2 = y;
            for (int i = 0; i < dd.length(); i++) {
                if (dd.charAt(i) == '1') {
                    if (x1 == null && y1 == null) {
                        x1 = x2;
                        y1 = y2;
                    } else {
                        ans = kmult(x1, y1, x2, y2, n);
                        x1 = ans.get(0);
                        y1 = ans.get(1);
                    }
                }
                ans = kmult(x2, y2, x2, y2, n);
                x2 = ans.get(0);
                y2 = ans.get(1);
            }

            String x = String.valueOf(x1);
            while (x.length() % 3 != 0)
                x = "0" + x;
            byte[] bytes = new byte[x.length()/3];
            for (int i = 0; i < bytes.length; i++) {
                int xx = Integer.parseInt(x.substring(0, 3));
                if (xx > 127)
                    xx -= 256;
                bytes[i] = (byte) xx;
                x = x.substring(3, x.length());
            }
            String mes = new String(bytes);
            sb.append(mes);
            out_decr.print(mes);
        }

        //out_decr.print(sb);

        in_encr.close();
        in_p.close();
        in_s.close();
        out_decr.close();

    }

    public List<BigInteger> kmult(BigInteger x1, BigInteger y1, BigInteger x2, BigInteger y2, BigInteger n) {

        List<BigInteger> ans = new ArrayList<>();

        BigInteger alph1, alph;
        BigInteger xx, yy;

        if (!x1.equals(x2)) {
            alph1 = x2.subtract(x1).mod(n);
            try {
                alph1 = alph1.modInverse(n);
            } catch (ArithmeticException ex) {
                //System.out.println(alph1);
                return null;
            }
            alph = y2.subtract(y1).multiply(alph1).mod(n);
        } else {
            alph1 = y1.multiply(BigInteger.valueOf(2)).mod(n);
            try {
                alph1 = alph1.modInverse(n);
            } catch (ArithmeticException ex) {
                //System.out.println(alph1);
                return null;
            }
            alph = x1.pow(2).multiply(BigInteger.valueOf(3)).multiply(alph1).mod(n);
        }
        xx = x1.multiply(BigInteger.valueOf(-1)).subtract(x2).add(alph.pow(2)).mod(n);
        while (xx.compareTo(BigInteger.ZERO) == -1)
            xx = xx.add(n);
        yy = y1.multiply(BigInteger.valueOf(-1)).add(x1.subtract(xx).multiply(alph)).mod(n);
        while (yy.compareTo(BigInteger.ZERO) == -1)
            yy = yy.add(n);

        ans.add(xx);
        ans.add(yy);

        return ans;
    }
}