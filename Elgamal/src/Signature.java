import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Signature {
    public Signature() throws IOException {

        Scanner sc = new Scanner(System.in);
        BufferedReader in_EC = new BufferedReader(new FileReader("ellipticalCurve.txt"));
        BufferedReader in_PK = new BufferedReader(new FileReader("publicKey.txt"));
        BufferedReader in_SK = new BufferedReader(new FileReader("secretKey.txt"));
        BufferedReader in_M = new BufferedReader(new FileReader("message.txt"));

        BigInteger p = new BigInteger(in_EC.readLine());
        BigInteger B = new BigInteger(in_EC.readLine());
        BigInteger xq = new BigInteger(in_EC.readLine());
        BigInteger yq = new BigInteger(in_EC.readLine());
        BigInteger r = new BigInteger(in_EC.readLine());
        BigInteger xp = new BigInteger(in_PK.readLine());
        BigInteger yp = new BigInteger(in_PK.readLine());
        BigInteger l = new BigInteger(in_SK.readLine());

        String str = "", sm = "";
        while ((str = in_M.readLine()) != null) {
            sm += str + "\n";
        }
        byte[] bytes = sm.getBytes();
        BigInteger m = new BigInteger(bytes);

        if (m.compareTo(r) >= 0) {
            System.out.println("Сообщение слишком большое. Требуется создать другие ключи");
            return;
        }

        System.out.println("Выберите шаг протокола:");
        System.out.println("1. Первый шаг");
        System.out.println("2. Второй шаг");
        System.out.println("3. Третий шаг");
        System.out.println("4. Четвертый шаг");
        System.out.println("5. Пятый шаг");

        int step = sc.nextInt();

        if (step == 1) {

            while (true) {

                PrintWriter out_step1 = new PrintWriter(new FileWriter("step1.txt"));

                BigInteger k1 = new BigInteger(String.valueOf((int) (Math.random() * Integer.MAX_VALUE))).mod(r);
                while (k1.equals(BigInteger.ZERO))
                    k1 = new BigInteger(String.valueOf((int) (Math.random() * Integer.MAX_VALUE))).mod(r);

                BigInteger k11 = k1;
                String kk = "";
                while (k11.compareTo(BigInteger.ONE) > 0) {
                    kk += String.valueOf(k11.mod(BigInteger.valueOf(2)));
                    k11 = k11.divide(BigInteger.valueOf(2));
                }
                kk += k11;

                boolean fl = true;
                List<BigInteger> ans = new ArrayList<>();
                BigInteger xr = null, yr = null;
                BigInteger xr1 = xq, yr1 = yq;
                for (int ii = 0; ii < kk.length(); ii++) {
                    if (kk.charAt(ii) == '1') {
                        if (xr == null && yr == null) {
                            xr = xr1;
                            yr = yr1;
                        } else {
                            ans = kmult(xr, yr, xr1, yr1, p);
                            if (ans == null) {
                                fl = false;
                                break;
                            }
                            xr = ans.get(0);
                            yr = ans.get(1);
                        }
                    }
                    ans = kmult(xr1, yr1, xr1, yr1, p);
                    if (ans == null) {
                        fl = false;
                        break;
                    }
                    xr1 = ans.get(0);
                    yr1 = ans.get(1);
                }
                if (fl) {
                    if (!f(xr, yr, p).equals(BigInteger.ZERO)) {
                        out_step1.print(k1 + "\n" + xr + "\n" + yr);
                        System.out.println("k' = " + k1 + "\n" +
                                "R' = (" + xr + ", " + yr + ")");

                        out_step1.close();
                        in_EC.close();
                        in_M.close();
                        in_PK.close();
                        in_SK.close();

                        return;
                    }
                }
            }
        }

        if (step == 2) {
            while (true) {

                BufferedReader in_step1 = new BufferedReader(new FileReader("step1.txt"));
                PrintWriter out_step2 = new PrintWriter(new FileWriter("step2.txt"));

                BigInteger k1 = new BigInteger(in_step1.readLine());
                BigInteger xr = new BigInteger(in_step1.readLine());
                BigInteger yr = new BigInteger(in_step1.readLine());

                if (!yr.pow(2).mod(p).equals(xr.pow(3).add(B).mod(p))) {
                    System.out.println("Протокол прерван, т.к. точка R' не принадлежит ЭК");
                    return;
                }
                BigInteger alph = new BigInteger(String.valueOf((int) (Math.random() * Integer.MAX_VALUE))).mod(r);
                while (alph.equals(BigInteger.ZERO))
                    alph = new BigInteger(String.valueOf((int) (Math.random() * Integer.MAX_VALUE))).mod(r);
                BigInteger alph1 = alph;
                String aa = "";
                while (alph1.compareTo(BigInteger.ONE) > 0) {
                    aa += String.valueOf(alph1.mod(BigInteger.valueOf(2)));
                    alph1 = alph1.divide(BigInteger.valueOf(2));
                }
                aa += alph1;

                boolean fl = true;
                List<BigInteger> ans = new ArrayList<>();
                BigInteger xR = null, yR = null;
                BigInteger xR1 = xr, yR1 = yr;
                for (int ii = 0; ii < aa.length(); ii++) {
                    if (aa.charAt(ii) == '1') {
                        if (xR == null && yR == null) {
                            xR = xR1;
                            yR = yR1;
                        } else {
                            ans = kmult(xR, yR, xR1, yR1, p);
                            if (ans == null) {
                                fl = false;
                                break;
                            }
                            xR = ans.get(0);
                            yR = ans.get(1);
                        }
                    }
                    ans = kmult(xR1, yR1, xR1, yR1, p);
                    if (ans == null) {
                        fl = false;
                        break;
                    }
                    xR1 = ans.get(0);
                    yR1 = ans.get(1);
                }
                if (fl) {
                    if (!f(xR, yR, p).equals(BigInteger.ZERO)) {
                        BigInteger bett = f(xR, yR, p).multiply(f(xr, yr, p).modInverse(r)).mod(r);
                        BigInteger m1 = alph.multiply(bett.modInverse(r)).multiply(m).mod(r);

                        out_step2.print(alph + "\n" + xR + "\n" + yR + "\n" + bett + "\n" + m1);
                        System.out.println("alpha = " + alph + "\n" +
                                "R = (" + xR + ", " + yR + ")" + "\n" +
                                "betta = " + bett + "\n" +
                                "m' = " + m1);

                        in_M.close();
                        in_EC.close();
                        in_PK.close();
                        in_SK.close();
                        in_step1.close();
                        out_step2.close();

                        return;
                    }
                }
            }
        }

        if (step == 3) {

            BufferedReader in_step1 = new BufferedReader(new FileReader("step1.txt"));
            BufferedReader in_step2 = new BufferedReader(new FileReader("step2.txt"));
            PrintWriter out_step3 = new PrintWriter(new FileWriter("step3.txt"));

            BigInteger k1 = new BigInteger(in_step1.readLine());
            BigInteger xr = new BigInteger(in_step1.readLine());
            BigInteger yr = new BigInteger(in_step1.readLine());
            BigInteger alph = new BigInteger(in_step2.readLine());
            BigInteger xR = new BigInteger(in_step2.readLine());
            BigInteger yR = new BigInteger(in_step2.readLine());
            BigInteger bett = new BigInteger(in_step2.readLine());
            BigInteger m1 = new BigInteger(in_step2.readLine());

            if (m1.equals(BigInteger.ZERO)) {
                System.out.println("Протокол прерван, т.к. m' = 0");
                return;
            }
            BigInteger s1 = l.multiply(f(xr, yr, p)).add(k1.multiply(m1)).mod(r);
            System.out.println("s' = " + s1);
            out_step3.print(s1);

            in_EC.close();
            in_M.close();
            in_PK.close();
            in_SK.close();
            in_step1.close();
            in_step2.close();
            out_step3.close();
        }

        if (step == 4) {

            BufferedReader in_step3 = new BufferedReader(new FileReader("step3.txt"));
            BufferedReader in_step2 = new BufferedReader(new FileReader("step2.txt"));
            BufferedReader in_step1 = new BufferedReader(new FileReader("step1.txt"));
            PrintWriter out_step4 = new PrintWriter(new FileWriter("step4.txt"));

            BigInteger k1 = new BigInteger(in_step1.readLine());
            BigInteger xr = new BigInteger(in_step1.readLine());
            BigInteger yr = new BigInteger(in_step1.readLine());
            BigInteger s1 = new BigInteger(in_step3.readLine());
            BigInteger alph = new BigInteger(in_step2.readLine());
            BigInteger xR = new BigInteger(in_step2.readLine());
            BigInteger yR = new BigInteger(in_step2.readLine());
            BigInteger bett = new BigInteger(in_step2.readLine());
            BigInteger m1 = new BigInteger(in_step2.readLine());

            BigInteger s11 = s1;
            String ss = "";
            while (s11.compareTo(BigInteger.ONE) > 0) {
                ss += String.valueOf(s11.mod(BigInteger.valueOf(2)));
                s11 = s11.divide(BigInteger.valueOf(2));
            }
            ss += s11;

            List<BigInteger> ans = new ArrayList<>();
            BigInteger xleft = null, yleft = null;
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

            BigInteger f1 = f(xr, yr, p);
            String ff = "";
            while (f1.compareTo(BigInteger.ONE) > 0) {
                ff += String.valueOf(f1.mod(BigInteger.valueOf(2)));
                f1 = f1.divide(BigInteger.valueOf(2));
            }
            ff += f1;

            ans = new ArrayList<>();
            BigInteger xxright = null, yyright = null;
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

            BigInteger m11 = m1;
            String mm = "";
            while (m11.compareTo(BigInteger.ONE) > 0) {
                mm += String.valueOf(m11.mod(BigInteger.valueOf(2)));
                m11 = m11.divide(BigInteger.valueOf(2));
            }
            mm += m11;

            ans = new ArrayList<>();
            BigInteger xx1right = null, yy1right = null;
            BigInteger xx1right1 = xr, yy1right1 = yr;
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

            BigInteger s = s1.multiply(bett).mod(r);
            System.out.println("s = " + s);

            out_step4.print(s);

            in_EC.close();
            in_M.close();
            in_PK.close();
            in_SK.close();
            in_step1.close();
            in_step2.close();
            in_step3.close();
            out_step4.close();
        }

        if (step == 5) {

            PrintWriter out_sign = new PrintWriter(new FileWriter("signature.txt"));
            BufferedReader in_step2 = new BufferedReader(new FileReader("step2.txt"));
            BufferedReader in_step4 = new BufferedReader(new FileReader("step4.txt"));

            BigInteger alph = new BigInteger(in_step2.readLine());
            BigInteger xR = new BigInteger(in_step2.readLine());
            BigInteger yR = new BigInteger(in_step2.readLine());
            BigInteger s = new BigInteger(in_step4.readLine());

            out_sign.print(m + "\n" + xR + "\n" + yR + "\n" + s);
            System.out.println("m = " + m + "\n" +
                    "R = (" + xR + ", " + yR + ")" + "\n" +
                    "s = " + s);

            in_EC.close();
            in_M.close();
            in_PK.close();
            in_SK.close();
            in_step2.close();
            in_step4.close();
            out_sign.close();

            return;
        }

        in_EC.close();
        in_M.close();
        in_PK.close();
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

    public BigInteger f(BigInteger x, BigInteger y, BigInteger p) {
        return x.add(y).mod(p);
    }
}
