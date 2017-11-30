import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Encryption {

    public Encryption() throws IOException {
        BufferedReader in_m = new BufferedReader(new FileReader("M.txt"));
        BufferedReader in_p = new BufferedReader(new FileReader("publicKey.txt"));
        PrintWriter out_encr = new PrintWriter(new FileWriter("cipher.txt"));
        BigInteger n = new BigInteger(in_p.readLine());
        BigInteger e = new BigInteger(in_p.readLine());

        String s = "", sm = "";
        while ((s = in_m.readLine()) != null) {
            sm += s + "\n";
        }

        StringBuilder sb = new StringBuilder(sm);
        List<BigInteger> stringList = new ArrayList<>();
        String ns = "";

        while (sb.length() != 0) {
            int x = 0;

            String ss = String.valueOf(sb.charAt(x));
            byte[] bytes = ss.getBytes();
            ns = byteToString(bytes);
            BigInteger m = new BigInteger(ns);
            if (m.compareTo(n) >= 0){
                System.out.println("Слишком маленькие значения p и q. Пожалуйста, выберите другие");
                return;
            }

            while (m.compareTo(n) == -1 && x+1 < sb.length()) {
                x++;

                ss = ss + String.valueOf(sb.charAt(x));
                bytes = ss.getBytes();
                ns = byteToString(bytes);
                m = new BigInteger(ns);
            }
            if (m.compareTo(n) >= 0){
                ss = ss.substring(0, ss.length() - 1);
            }

            bytes = ss.getBytes();
            ns = byteToString(bytes);
            if (sb.length() != 1 && ns.length() < 4){
                System.out.println("Слишком маленькие значения p и q. Пожалуйста, выберите другие");
                return;
            }
            m = new BigInteger(ns);
            stringList.add(m);
            sb = sb.delete(0, ss.length());
        }

        String ee = "";
        while (e.compareTo(BigInteger.ONE) > 0) {
            ee += String.valueOf(e.mod(BigInteger.valueOf(2)));
            e = e.divide(BigInteger.valueOf(2));
        }
        ee += e;

        List <BigInteger> result = new ArrayList<>();
        boolean fl = true;
        int maxNumb = 1000;
        while (fl) {

            if (maxNumb == 0) {
                System.out.println("Слишком маленькие значения p и q. Пожалуйста, выберите другие");
                return;
            }
            maxNumb--;
            result = new ArrayList<>();
            for (int ind = 0; ind < stringList.size(); ind++) {

                BigInteger m = stringList.get(ind);
                List<BigInteger> ans = new ArrayList<>();
                BigInteger y = BigInteger.valueOf((long) ((Math.random() * Long.MAX_VALUE)) + 2).multiply(BigInteger.valueOf((long) (Math.random() * Long.MAX_VALUE) + 2)).mod(n);

                BigInteger x1 = null, y1 = null;
                BigInteger x2 = m, y2 = y;
                for (int i = 0; i < ee.length(); i++) {
                    if (ee.charAt(i) == '1') {
                        if (x1 == null && y1 == null) {
                            x1 = x2;
                            y1 = y2;
                        } else {
                            ans = kmult(x1, y1, x2, y2, n);
                            if (ans == null) {
                                fl = false;
                                break;
                            }
                            x1 = ans.get(0);
                            y1 = ans.get(1);
                        }
                    }
                    ans = kmult(x2, y2, x2, y2, n);
                    if (ans == null) {
                        fl = false;
                        break;
                    }
                    x2 = ans.get(0);
                    y2 = ans.get(1);
                }

                if (!fl) {
                    fl = true;
                    break;
                }
                else {
                    result.add(x1);
                    result.add(y1);

                    if (ind == stringList.size() - 1)
                        fl = false;
                }
            }
        }

        for (int i = 0; i < result.size(); i++) {
            out_encr.println(result.get(i));
        }
        out_encr.close();
        in_m.close();
        in_p.close();
    }

    public List<BigInteger> kmult(BigInteger x1, BigInteger y1, BigInteger x2, BigInteger y2, BigInteger n){

        List<BigInteger> ans = new ArrayList<>();

        BigInteger alph1, alph;
        BigInteger xx, yy;

        if (!x1.equals(x2)) {
            alph1 = x2.subtract(x1).mod(n);
            try {
                alph1 = alph1.modInverse(n);
            } catch (ArithmeticException ex) {
                return null;
            }
            alph = y2.subtract(y1).multiply(alph1).mod(n);
        } else {
            alph1 = y1.multiply(BigInteger.valueOf(2)).mod(n);
            try {
                alph1 = alph1.modInverse(n);
            } catch (ArithmeticException ex) {
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

    public String byteToString(byte[] bytes){
        String ns = "";
        int[] ints = new int[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] > 0)
                ints[i] = bytes[i];
            else
                ints[i] = bytes[i] + 256;

            if (ints[i] < 10)
                ns += "0";
            if (ints[i] < 100)
                ns += "0";
            ns += String.valueOf(ints[i]);
        }
        return ns;
    }
}