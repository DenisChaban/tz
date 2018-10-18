import java.io.File;

public class Job extends Thread {

    private String name;
    private int cntFiles;


    public Job(String name) {
        this.name = name;
        cntFiles=0;
    }

    @Override
    public void run() {
        try {
            while (true) {
                System.out.println("Выполняется: " + currentThread().getName());

                String FileName = Main.getFileName();

                if (FileName != null && !FileName.isEmpty()) {
                    System.out.println(currentThread().getName() + " файл для обработки \"" + FileName + "\"");

                    // загрузим XML
                    FileInput fileInput = new FileInput(FileName);
                    fileInput.loadFile();

                    // выгрузим XML
                    FileOutput fo = new FileOutput(fileInput, "./SEND/" + FileName);
                    fo.saveFile();
                    fo.saveHTTP();

                    // перенесем исходный файл в архив
                    File file = new File(FileName);
                    file.renameTo(new File("./ARCHIVE/" +
                            file.getName().replace(".xml", "") +
                            "_"+ name+ "_" + cntFiles++ +".xml"
                    ));
                }

                sleep(3000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
