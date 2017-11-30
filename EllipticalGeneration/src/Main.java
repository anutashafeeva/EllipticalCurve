import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {

        Scanner sc = new Scanner(System.in);
        Random random = new Random();

        System.out.println("Введите l - длина характеристики поля");
        int l = sc.nextInt();
        System.out.println("Введите m - параметр безопасности");
        int m = sc.nextInt();

        BigInteger p = BigInteger.ONE, r = BigInteger.ONE;
        BigInteger N = BigInteger.ONE, b = BigInteger.ONE;
        BigInteger x0 = BigInteger.ZERO, y0 = BigInteger.ZERO;
        BigInteger x11 = BigInteger.ONE, y11 = BigInteger.ONE;
        BigInteger c = BigInteger.ONE, d = BigInteger.ONE;
        List<BigInteger> Prime = new ArrayList<>();
        int number = 0;
        int step = 1;

        BigInteger firstp = BigInteger.probablePrime(l, random);
        if (firstp.mod(BigInteger.valueOf(6)).equals(BigInteger.ONE))
            Prime.add(firstp);
        BigInteger left = firstp.subtract(BigInteger.ONE);
        BigInteger right = firstp.add(BigInteger.ONE);
        while (left.bitLength() == l) {
            if (left.isProbablePrime(100) && left.mod(BigInteger.valueOf(6)).equals(BigInteger.ONE))
                Prime.add(left);
            left = left.subtract(BigInteger.ONE);
        }
        while (right.bitLength() == l) {
            if (right.isProbablePrime(100) && right.mod(BigInteger.valueOf(6)).equals(BigInteger.ONE))
                Prime.add(right);
            right = right.add(BigInteger.ONE);
        }
        Collections.sort(Prime);

        while (true) {
            if (step == 1) {
                if (number == Prime.size()) {
                    System.out.println("Нельзя построить эллиптическую кривую");
                    return;
                }
                p = Prime.get(number);
                step = 2;
            }

            if (step == 2) {
                ArrayList<BigInteger> cd = DecomposD(p, BigInteger.valueOf(-3));
                if (cd.size() == 0)
                    return;
                c = cd.get(0);
                d = cd.get(1);
                step = 3;
            }

            if (step == 3) {
                List<BigInteger> T = new ArrayList<>();
                T.add(c.add(d.multiply(BigInteger.valueOf(3))));
                T.add(c.add(d.multiply(BigInteger.valueOf(3))).multiply(BigInteger.valueOf(-1)));
                T.add(c.subtract(d.multiply(BigInteger.valueOf(3))));
                T.add(c.subtract(d.multiply(BigInteger.valueOf(3))).multiply(BigInteger.valueOf(-1)));
                T.add(c.multiply(BigInteger.valueOf(2)));
                T.add(c.multiply(BigInteger.valueOf(2)).multiply(BigInteger.valueOf(-1)));

                boolean fl = false;
                for (int i = 0; i < 6; i++) {
                    N = p.add(BigInteger.ONE).add(T.get(i));
                    if (N.isProbablePrime(100)) {
                        r = N;
                        fl = true;
                        break;
                    } else if (N.mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO) && (N.divide(BigInteger.valueOf(2))).isProbablePrime(100)) {
                        r = N.divide(BigInteger.valueOf(2));
                        fl = true;
                        break;
                    } else if (N.mod(BigInteger.valueOf(3)).equals(BigInteger.ZERO) && (N.divide(BigInteger.valueOf(3))).isProbablePrime(100)) {
                        r = N.divide(BigInteger.valueOf(3));
                        fl = true;
                        break;
                    } else if (N.mod(BigInteger.valueOf(6)).equals(BigInteger.ZERO) && (N.divide(BigInteger.valueOf(6))).isProbablePrime(100)) {
                        r = N.divide(BigInteger.valueOf(6));
                        fl = true;
                        break;
                    }
                }
                if (fl)
                    step = 4;
                else {
                    step = 1;
                    number++;
                }
            }

            if (step == 4) {
                boolean fl = true;
                if (p.equals(r))
                    fl = false;
                else {
                    for (int i = 1; i <= m; i++) {
                        if (p.modPow(BigInteger.valueOf(i), r).equals(BigInteger.ONE))
                            fl = false;
                    }
                }
                if (fl)
                    step = 5;
                else {
                    step = 1;
                    number++;
                }
            }

            if (step == 5) {
                x0 = BigInteger.ZERO;
                y0 = BigInteger.ZERO;
                while (x0.equals(BigInteger.ZERO))
                    x0 = BigInteger.ONE.add(BigInteger.valueOf(random.nextLong())).mod(p);
                if (x0.compareTo(BigInteger.ZERO) == -1)
                    x0 = x0.add(p);
                while (y0.equals(BigInteger.ZERO))
                    y0 = BigInteger.ONE.add(BigInteger.valueOf(random.nextLong())).mod(p);
                if (y0.compareTo(BigInteger.ZERO) == -1)
                    y0 = y0.add(p);
                b = ((y0.pow(2)).subtract(x0.pow(3))).mod(p);
                while (b.compareTo(BigInteger.ZERO) == -1)
                    b = b.add(p);

                if (((N.equals(r) && !Sol2(b, p) && !Sol3(b, p)) ||
                        (N.equals(r.multiply(BigInteger.valueOf(6))) && Sol2(b, p) && Sol3(b, p)) ||
                        (N.equals(r.multiply(BigInteger.valueOf(2))) && !Sol2(b, p) && Sol3(b, p)) ||
                        (N.equals(r.multiply(BigInteger.valueOf(3))) && Sol2(b, p) && !Sol3(b, p))))
                    step = 6;
                else
                    step = 5;
            }

            if (step == 6) {
                PrintWriter out_x = new PrintWriter(new FileWriter("x.txt"));
                PrintWriter out_y = new PrintWriter(new FileWriter("y.txt"));
                BigInteger x1 = x0, y1 = y0, x2, y2;
                BigInteger alph, alph1;
                out_x.println(x1);
                out_y.println(y1);
                for (BigInteger i = BigInteger.ONE; i.compareTo(N.subtract(BigInteger.ONE)) == -1; i = i.add(BigInteger.ONE)) {
                    if (!x0.equals(x1)) {
                        alph1 = x1.subtract(x0).mod(p);
                        if (alph1.equals(BigInteger.ZERO)) {
                            step = 5;
                            break;
                        }
                        alph1 = alph1.modInverse(p);
                        alph = y1.subtract(y0).multiply(alph1).mod(p);
                    } else {
                        alph1 = y0.multiply(BigInteger.valueOf(2)).mod(p);
                        if (alph1.equals(BigInteger.ZERO)) {
                            step = 5;
                            break;
                        }
                        alph1 = alph1.modInverse(p);
                        alph = x0.pow(2).multiply(BigInteger.valueOf(3)).multiply(alph1).mod(p);
                    }
                    x2 = x0.multiply(BigInteger.ONE.negate()).subtract(x1).add(alph.pow(2)).mod(p);
                    while (x2.compareTo(BigInteger.ZERO) == -1)
                        x2 = x2.add(p);
                    y2 = y0.multiply(BigInteger.ONE.negate()).add(x0.subtract(x2).multiply(alph)).mod(p);
                    while (y2.compareTo(BigInteger.ZERO) == -1)
                        y2 = y2.add(p);
                    out_x.println(x2);
                    out_y.println(y2);
                    x1 = x2;
                    y1 = y2;
                }

                if (x0.equals(x1)) {
                    step = 7;
                    out_x.close();
                    out_y.close();
                }
                else
                    step = 5;
            }

            if (step == 7) {
                BigInteger alph1, alph;
                BigInteger Q = N.divide(r);
                x11 = x0;
                y11 = y0;
                BigInteger x12, y12;
                for (BigInteger i = BigInteger.ZERO; i.compareTo(Q.subtract(BigInteger.ONE)) == -1; i = i.add(BigInteger.ONE)) {
                    if (!x0.equals(x11)) {
                        alph1 = x11.subtract(x0).mod(p);
                        alph1 = alph1.modInverse(p);
                        alph = y11.subtract(y0).multiply(alph1).mod(p);
                    } else {
                        alph1 = y0.multiply(BigInteger.valueOf(2)).mod(p);
                        alph1 = alph1.modInverse(p);
                        alph = x0.pow(2).multiply(BigInteger.valueOf(3)).multiply(alph1).mod(p);
                    }
                    x12 = x0.multiply(BigInteger.valueOf(-1)).subtract(x11).add(alph.pow(2)).mod(p);
                    while (x12.compareTo(BigInteger.ZERO) == -1)
                        x12 = x12.add(p);
                    y12 = y0.multiply(BigInteger.valueOf(-1)).add(x0.subtract(x12).multiply(alph)).mod(p);
                    while (y12.compareTo(BigInteger.ZERO) == -1)
                        y12 = y12.add(p);
                    x11 = x12;
                    y11 = y12;
                }
                step = 8;
            }

            if (step == 8) {
                System.out.println("p = " + p + "\n" +
                        "B = " + b + "\n" +
                        "Q = (" + x11 + ", " + y11 + ")" + "\n" +
                        "r = " + r);
                break;
            }
        }
    }

    public static ArrayList<BigInteger> DecomposD(BigInteger p, BigInteger D) {
        List<BigInteger> cd = new ArrayList<>();
        BigInteger u;
        if (Y(D, p) == -1) {
            System.out.println("Решений нет");
        } else {
            u = Sol(D, p);

            int i = 0;
            List<BigInteger> U = new ArrayList<>(), M = new ArrayList<>();
            U.add(u);
            M.add(p);
            D = D.multiply(BigInteger.valueOf(-1));
            while (true) {
                BigInteger mi = U.get(i).pow(2).add(D).divide(M.get(i));
                BigInteger ui = U.get(i).mod(mi).min(mi.subtract(U.get(i).mod(mi)).add(p).mod(p));
                if (mi.equals(BigInteger.ONE))
                    break;
                i++;
                M.add(mi);
                U.add(ui);
            }
            int j = 0;
            List<BigInteger> A = new ArrayList<>(), B = new ArrayList<>();
            A.add(U.get(i));
            B.add(BigInteger.ONE);
            while (true) {
                if (i == 0) {
                    cd.add(A.get(j));
                    cd.add(B.get(j));
                    break;
                } else {
                    BigInteger znam = A.get(j).pow(2).add(D.multiply(B.get(j).pow(2)));
                    if (D.multiply(B.get(j)).add(A.get(j).multiply(U.get(i - 1))).mod(znam).equals(BigInteger.ZERO))
                        A.add(D.multiply(B.get(j)).add(A.get(j).multiply(U.get(i - 1))).divide(znam));
                    else
                        A.add(D.multiply(B.get(j)).subtract(A.get(j).multiply(U.get(i - 1))).divide(znam));
                    if (A.get(j).multiply(BigInteger.valueOf(-1)).add(U.get(i - 1).multiply(B.get(j))).mod(znam).equals(BigInteger.ZERO))
                        B.add(A.get(j).multiply(BigInteger.valueOf(-1)).add(U.get(i - 1).multiply(B.get(j))).divide(znam));
                    else
                        B.add(A.get(j).multiply(BigInteger.valueOf(-1)).subtract(U.get(i - 1).multiply(B.get(j))).divide(znam));
                    i--;
                    j++;
                }
            }
        }
        return (ArrayList<BigInteger>) cd;
    }

    public static int Y(BigInteger a, BigInteger n) {
        if (a.mod(n).equals(BigInteger.ZERO))
            return 0;

        int res = 1;
        if (a.compareTo(BigInteger.ZERO) == -1 && n.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3))) {
            res *= -1;
            a = a.multiply(BigInteger.valueOf(-1));
        }
        while (true) {
            a = a.mod(n);
            int k = 0;
            while (a.mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO)) {
                a = a.divide(BigInteger.valueOf(2));
                k++;
            }
            if (k % 2 == 1 && (n.mod(BigInteger.valueOf(8)).equals(BigInteger.valueOf(3)) || n.mod(BigInteger.valueOf(8)).equals(BigInteger.valueOf(5))))
                res *= -1;
            if (a.equals(BigInteger.ONE))
                break;
            BigInteger tmp = a;
            a = n;
            n = tmp;
            if ((n.subtract(BigInteger.ONE)).multiply(a.subtract(BigInteger.ONE)).divide(BigInteger.valueOf(4)).mod(BigInteger.valueOf(2)).equals(BigInteger.ONE))
                res *= -1;
        }
        return res;
    }

    public static BigInteger Sol(BigInteger a, BigInteger p) {
        if (Y(a, p) == -1)
            return BigInteger.valueOf(-1);
        BigInteger n = BigInteger.ZERO;
        if (Y(a, p) == 1) {
            for (BigInteger i = BigInteger.valueOf(2); i.compareTo(p) == -1; i = i.add(BigInteger.ONE)) {
                if (Y(i, p) == -1) {
                    n = i;
                    break;
                }
            }
        }
        BigInteger h = p.subtract(BigInteger.ONE);
        long k = 0;
        while (h.mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO)) {
            h = h.divide(BigInteger.valueOf(2));
            k++;
        }
        BigInteger a1 = a.modPow(h.add(BigInteger.ONE).divide(BigInteger.valueOf(2)), p);
        BigInteger a2 = a.modInverse(p);

        BigInteger n1 = n.modPow(h, p);
        BigInteger n2 = BigInteger.ONE;
        for (int i = 0; i < k - 1; i++) {
            BigInteger b = a1.multiply(n2).mod(p);
            BigInteger c = a2.multiply(b.pow(2)).mod(p);
            BigInteger d = c.modPow(BigInteger.valueOf((int) Math.pow(2, k - 2 - i)), p);
            int ji = 0;
            if (d.equals(BigInteger.ONE))
                ji = 0;
            else if (d.equals(p.subtract(BigInteger.ONE)))
                ji = 1;
            n2 = n2.multiply(n1.pow((int) Math.pow(2, i) * ji)).mod(p);
        }
        return a1.multiply(n2).mod(p);
    }

    public static boolean Sol3(BigInteger a, BigInteger p) {
        BigInteger q = (p.subtract(BigInteger.ONE)).gcd(BigInteger.valueOf(3));
        if (a.modPow(p.subtract(BigInteger.ONE).divide(q), p).equals(BigInteger.ONE))
            return true;
        else
            return false;
    }

    public static boolean Sol2(BigInteger a, BigInteger p) {
        if (Y(a, p) == 1)
            return true;
        else
            return false;
    }
}