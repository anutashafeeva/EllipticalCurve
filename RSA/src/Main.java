import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        Scanner sc = new Scanner(System.in);

        System.out.println("Выберите:");
        System.out.println("1. Генерация ключей");
        System.out.println("2. Шифрование");
        System.out.println("3. Дешифрование");

        int met = sc.nextInt();
        if (met == 1)
            new KeyGeneration();
        if (met == 2)
            new Encryption();
        if (met == 3)
            new Decryption();
    }
}
