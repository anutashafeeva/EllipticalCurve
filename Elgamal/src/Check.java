import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Check {
    public Check() throws IOException {

        BufferedReader in_EC = new BufferedReader(new FileReader("ellipticalCurve.txt"));
        BufferedReader in_PK = new BufferedReader(new FileReader("publicKey.txt"));
        BufferedReader in_SK = new BufferedReader(new FileReader("secretKey.txt"));
        BufferedReader in_M = new BufferedReader(new FileReader("message.txt"));
        BufferedReader in_sign = new BufferedReader(new FileReader("signature.txt"));

        BigInteger p = new BigInteger(in_EC.readLine());
        BigInteger B = new BigInteger(in_EC.readLine());
        BigInteger xq = new BigInteger(in_EC.readLine());
        BigInteger yq = new BigInteger(in_EC.readLine());
        BigInteger r = new BigInteger(in_EC.readLine());
        BigInteger xp = new BigInteger(in_PK.readLine());
        BigInteger yp = new BigInteger(in_PK.readLine());
        BigInteger l = new BigInteger(in_SK.readLine());
        BigInteger m = new BigInteger(in_sign.readLine());
        BigInteger xR = new BigInteger(in_sign.readLine());
        BigInteger yR = new BigInteger(in_sign.readLine());
        BigInteger s = new BigInteger(in_sign.readLine());

        if (m.equals(BigInteger.ZERO)){
            System.out.println("Подпись недействительна");
            return;
        }

        if (f(xR, yR, p).equals(BigInteger.ZERO)){
            System.out.println("Подпись недействительна");
            return;
        }

        BigInteger xleft = null, yleft = null;
        BigInteger s1 = s;
        String ss = "";
        while (s1.compareTo(BigInteger.ONE) > 0) {
            ss += String.valueOf(s1.mod(BigInteger.valueOf(2)));
            s1 = s1.divide(BigInteger.valueOf(2));
        }
        ss += s1;

        List<BigInteger> ans = new ArrayList<>();
        BigInteger xleft1 = xq, yleft1 = yq;
        for (int ii = 0; ii < ss.length(); ii++) {
            if (ss.charAt(ii) == '1') {
                if (xleft == null && yleft == null) {
                    xleft = xleft1;
                    yleft = yleft1;
                } else {
                    ans = kmult(xleft, yleft, xleft1, yleft1, p);
                    xleft = ans.get(0);
                    yleft = ans.get(1);
                }
            }
            ans = kmult(xleft1, yleft1, xleft1, yleft1, p);
            xleft1 = ans.get(0);
            yleft1 = ans.get(1);
        }

        BigInteger xxright = null, yyright = null;
        BigInteger f1 = f(xR, yR, p);
        String ff = "";
        while (f1.compareTo(BigInteger.ONE) > 0) {
            ff += String.valueOf(f1.mod(BigInteger.valueOf(2)));
            f1 = f1.divide(BigInteger.valueOf(2));
        }
        ff += f1;

        ans = new ArrayList<>();
        BigInteger xxright1 = xp, yyright1 = yp;
        for (int ii = 0; ii < ff.length(); ii++) {
            if (ff.charAt(ii) == '1') {
                if (xxright == null && yyright == null) {
                    xxright = xxright1;
                    yyright = yyright1;
                } else {
                    ans = kmult(xxright, yyright, xxright1, yyright1, p);
                    xxright = ans.get(0);
                    yyright = ans.get(1);
                }
            }
            ans = kmult(xxright1, yyright1, xxright1, yyright1, p);
            xxright1 = ans.get(0);
            yyright1 = ans.get(1);
        }

        BigInteger xx1right = null, yy1right = null;
        BigInteger m1 = m;
        String mm = "";
        while (m1.compareTo(BigInteger.ONE) > 0) {
            mm += String.valueOf(m1.mod(BigInteger.valueOf(2)));
            m1 = m1.divide(BigInteger.valueOf(2));
        }
        mm += m1;

        ans = new ArrayList<>();
        BigInteger xx1right1 = xR, yy1right1 = yR;
        for (int ii = 0; ii < mm.length(); ii++) {
            if (mm.charAt(ii) == '1') {
                if (xx1right == null && yy1right == null) {
                    xx1right = xx1right1;
                    yy1right = yy1right1;
                } else {
                    ans = kmult(xx1right, yy1right, xx1right1, yy1right1, p);
                    xx1right = ans.get(0);
                    yy1right = ans.get(1);
                }
            }
            ans = kmult(xx1right1, yy1right1, xx1right1, yy1right1, p);
            xx1right1 = ans.get(0);
            yy1right1 = ans.get(1);
        }

        BigInteger xright = null, yright = null;
        ans = kmult(xxright, yyright, xx1right, yy1right, p);
        xright = ans.get(0);
        yright = ans.get(1);

        if (!xleft.equals(xright) || !yleft.equals(yright)) {
            System.out.println("Подпись недействительна");
            return;
        }
        else {
            System.out.println("Подпись подлинная");
        }

        in_EC.close();
        in_M.close();
        in_PK.close();
        in_sign.close();
        in_SK.close();
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

    public BigInteger f(BigInteger x, BigInteger y, BigInteger p){
        return x.add(y).mod(p);
    }
}
