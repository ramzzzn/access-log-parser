import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                int countAllRequests = 0;
                int countYandexBot = 0;
                int countGoogleBot = 0;
                String regex = "^(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+\\[(.*?)]\\s+\"(\\w+)\\s+([^\"]+)\"\\s+(\\d{3})\\s+(\\d+)\\s+\"([^\"]*)\"\\s+\"([^\"]*)\"";
                Pattern pattern = Pattern.compile(regex);
                while ((line = reader.readLine()) != null) {
                    Matcher matcher = pattern.matcher(line);
                    countAllRequests++;
                    int length = line.length();
                    if (length > 1024) {
                        throw new TooLongLineException("В файле содержится строка длиннее 1024 символов. " +
                                "Пожалуйста, исправьте файл и попробуйте снова.");
                    }
                    if (matcher.matches()) {
                        String userAgent = matcher.group(10);
                        Pattern patternBrackets = Pattern.compile("\\([^)]*\\)");
                        Matcher matcherBrackets = patternBrackets.matcher(userAgent);
                        List<String> brackets = new ArrayList<>();
                        while (matcherBrackets.find()) {
                            brackets.add(matcherBrackets.group());
                        }
                        if (!brackets.isEmpty()) {
                            String lastBrackets = brackets.get(brackets.size() - 1);
                            String[] parts = lastBrackets.split(";");
                            List<String> fragmentsList = new ArrayList<>();
                            for (String part : parts) {
                                fragmentsList.add(part.trim());
                            }
                            if (fragmentsList.size() >= 2) {
                                String fragment = fragmentsList.get(1);
                                String[] fragmentParts = fragment.split("/");
                                String botName = fragmentParts[0];
                                if (botName.equals("YandexBot")) {
                                    countYandexBot++;
                                }
                                if (botName.equals("Googlebot")) {
                                    countGoogleBot++;
                                }
                            }
                        }
                    }
                }
                double percentageYandexBot = (countYandexBot * 100.0) / countAllRequests;
                double percentageGoogleBot = (countGoogleBot * 100.0) / countAllRequests;
                System.out.println("Доля запросов YandexBot от общего числа: " + String.format("%.3f", percentageYandexBot) + "%");
                System.out.println("Доля запросов Googlebot от общего числа: " + String.format("%.3f", percentageGoogleBot) + "%");
            } catch (TooLongLineException e) {
                System.err.println(e.getMessage());
                break;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}