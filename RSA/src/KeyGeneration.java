import java.io.*;
import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

public class KeyGeneration {

    public KeyGeneration() throws IOException {
        PrintWriter out_s = new PrintWriter(new FileWriter("SecretKey.txt"));
        PrintWriter out_p = new PrintWriter(new FileWriter("PublicKey.txt"));
        Scanner sc = new Scanner(System.in);
        Random random = new Random();

        System.out.println("Введите длину чисел p и q");
        int pl = sc.nextInt();
        int ql = sc.nextInt();

        BigInteger p = BigInteger.probablePrime(pl, random);
        while (!p.mod(BigInteger.valueOf(6)).equals(BigInteger.valueOf(5)))
            p = BigInteger.probablePrime(pl, random);
        int maxNumb = 1000;
        BigInteger q = BigInteger.probablePrime(ql, random);
        while (q.equals(p) || !q.mod(BigInteger.valueOf(6)).equals(BigInteger.valueOf(5))) {
            if (maxNumb == 0) {
                System.out.println("Невозможно подобрать простые числа p, q");
                return;
            }
            q = BigInteger.probablePrime(ql, random);
            maxNumb--;
        }


        BigInteger n = q.multiply(p);

        BigInteger phi = p.add(BigInteger.ONE).multiply(q.add(BigInteger.ONE));
        BigInteger e = BigInteger.probablePrime((int) (Math.random()*(n.bitLength()-2)) + 2, random);
        while (!e.gcd(phi).equals(BigInteger.ONE) || e.compareTo(BigInteger.ONE) != 1 || e.equals(n)) {
            e = BigInteger.probablePrime((int) (Math.random()*(n.bitLength()-2)) + 2, random);
        }
        BigInteger d = e.modInverse(phi);

        System.out.println("Созданы открытый и закрытый ключи");
        out_p.print(n + "\n" + e);
        out_s.print(d);

        out_p.close();
        out_s.close();
    }
}