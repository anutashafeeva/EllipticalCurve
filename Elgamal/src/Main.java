import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);

        System.out.println("Выберите действие:");
        System.out.println("1. Генерация ключей");
        System.out.println("2. Подпись сообщения");
        System.out.println("3. Проверка подписи");

        int met = sc.nextInt();
        if (met == 1){
            Keys keys = new Keys();
        }
        if (met == 2){
            Signature signature = new Signature();
        }
        if (met == 3){
            Check check = new Check();
        }
    }
}