import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int fileCount = 0;
        while (true) {
            System.out.println("Введите путь к файлу: ");
            String path = sc.nextLine();
            File file = new File(path);
            boolean fileExists = file.exists();
            boolean isDirectory = file.isDirectory();
            if (!fileExists) {
                System.out.println("Такого файла не существует");
                continue;
            }
            if (isDirectory) {
                System.out.println("Это путь к папке, а не к файлу");
                continue;
            }
            fileCount++;
            System.out.println("Путь указан верно");
            System.out.println("Это файл номер " + fileCount);
            try {
                FileReader fileReader = new FileReader(path);
                BufferedReader reader =
                        new BufferedReader(fileReader);
                String line;
                int count = 0;
                int max = 0;
                int min = Integer.MAX_VALUE;
                while ((line = reader.readLine()) != null) {
                    count++;
                    int length = line.length();
                    if (length > 1024) {
                        throw new TooLongLineException("В файле содержится строка длиннее 1024 символов. " +
                                "Пожалуйста, исправьте файл и попробуйте снова.");
                    }
                    if (length > max) {
                        max = length;
                    }
                    if (length < min) {
                        min = length;
                    }
                }
                System.out.println("Количество строк в файле: " + count);
                System.out.println("Максимальная длина строки: " + max);
                System.out.println("Минимальная длина строки: " + min);
            } catch (TooLongLineException e) {
                System.err.println(e.getMessage());
                break;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}