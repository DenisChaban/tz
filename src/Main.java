import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import static java.lang.Thread.sleep;


public class Main {

    // список файлов для обработки
    private static volatile HashSet<String> listFiles = new HashSet<>();

    // список заданий
    private static ArrayList<Job> arrayJob = new ArrayList<>();

    // получить название метода
    public static synchronized String getFileName(){
        String result = "";

        for(String res : listFiles){
            result = res;
            break;
        }

        if (result != null && !result.isEmpty()) {
             Main.listFiles.remove(result);
        }

        return result;
    }

    // создать директорию
    private static void createDir(File newDir) {
        if (!newDir.exists()) {
            System.out.println("creating directory: " + newDir.getName());

            try {
                newDir.mkdir();
                System.out.println("\tDIR created");
            } catch (SecurityException se) {
                se.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

        System.out.println("Запуск адаптера по отправлке файлов по http");

        // создается папка для отправленных запросов
        createDir(new File("SEND"));
        // создается папка для обработанных файлов
        createDir(new File("ARCHIVE"));
        // создается папка для файлов c ошибками
        createDir(new File("ERROR"));

        for (int i = 0; i<3; i++){
            Job newJob = new Job(String.valueOf(i));
            Main.arrayJob.add(newJob);
            newJob.start();
        }


        try {
            while (true) {
                System.out.println("Сканирование каталога с файлами");

                String dirPath = ".";
                File file = new File(dirPath);
                File[] files = file.listFiles();

                // список файлов в каталоге
                System.out.println("Файлы в каталоге:");
                if (listFiles.isEmpty()) {
                    for (File f : files) {
                        if (f.getName().contains(".xml")) {
                            System.out.println("\t\"" + f.getName() + "\"");

                            // допишем файл в обработку
                            synchronized (Main.class) {
                                listFiles.add(f.getName());
                            }
                        }
                    }
                }

                sleep(5000);
//                break;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



    }



}
