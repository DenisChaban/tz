
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

public class FileOutput {

    // счета
    private Account accountXML;
    // фио
    private Fio fioXML;
    // счетчики
    private ArrayList<Counter> counterXML = new ArrayList<>();
    // адреса
    private ArrayList<Addresses> addressesXML = new ArrayList<>();
    // суммы
    private ArrayList<Sum> sumXML = new ArrayList<>();

    // данные для передачи
    private String dataSend = "";

    private String fileName;

    // конструктор
    public FileOutput(FileInput fileInput, String fileName) {
        this.fileName = fileName;
        for (int i = 0; i < fileInput.xmlIn.size(); i++) {
            // Счет
            if (fileInput.xmlIn.get(i).getName().equals("MY_ACC")) {
                setAccount(fileInput.xmlIn.get(i).getName(),
                        fileInput.xmlIn.get(i).getRequired().equals("1") ? "true" : "false",
                        fileInput.xmlIn.get(i).getDigitOnly().equals("1") ? "true" : "false",
                        fileInput.xmlIn.get(i).getValue());
            }

            // ФИО
            if (fileInput.xmlIn.get(i).getName().equals("MY_FIO")) {
                setFio(fileInput.xmlIn.get(i).getName(),
                        fileInput.xmlIn.get(i).getReadOnly().equals("1") ? "true" : "false",
                        fileInput.xmlIn.get(i).getValue()
                );
            }

            // адрес
            if (fileInput.xmlIn.get(i).getName().equals("MY_ADDRESS")) {

                String tmp = fileInput.xmlIn.get(i).getValue();
                String[] tmpSplit = tmp.split(",");

                String street = "", house = "", flat = "";

                if (tmpSplit.length > 0) {
                    street = tmpSplit[0];
                }

                if (tmpSplit.length > 1) {
                    house = tmpSplit[1];
                }

                if (tmpSplit.length > 2) {
                    flat = tmpSplit[2];
                }

                setAddresses(fileInput.xmlIn.get(i).getName(),
                        fileInput.xmlIn.get(i).getReadOnly().equals("1") ? "true" : "false",
                        // сначала до первой запятой
                        street,
                        // от первой запятой до второй запятой
                        house,
                        // от второй запятой до конца
                        flat);
            }

            // количество
            if (fileInput.xmlIn.get(i).getName().contains("COUNT")) {
                setCounter(fileInput.xmlIn.get(i).getName(),
                        fileInput.xmlIn.get(i).getRequired().equals("1") ? "true" : "false",
                        fileInput.xmlIn.get(i).getValue());
            }

            // суммы
            if (fileInput.xmlIn.get(i).getName().contains("SUM")) {
                DecimalFormat myFormater = new DecimalFormat("#0.00", new DecimalFormatSymbols(Locale.US));

                setSum(fileInput.xmlIn.get(i).getName(),
                        fileInput.xmlIn.get(i).getRequired().equals("") ? "" : fileInput.xmlIn.get(i).getRequired().equals("1") ? "true" : "false",
                        fileInput.xmlIn.get(i).getReadOnly().equals("") ? "" : fileInput.xmlIn.get(i).getReadOnly().equals("1") ? "true" : "false",
                        myFormater.format(Double.parseDouble(fileInput.xmlIn.get(i).getValue())));
            }
        }
    }

    // Поток для записи файла
    private BufferedWriter bufferedWriter;

    // задать информацию о счете
    public void setAccount(String account, String required, String digitOnly, String value) {
        Account acc = new Account(account, required, digitOnly, value);
        accountXML = acc;
    }

    // задать информацию о ФИО
    public void setFio(String name, String readOnly, String value) {
        Fio fio = new Fio(name, readOnly, value);
        fioXML = fio;
    }

    // задать информацию о адресах
    public void setAddresses(String name, String readOnly, String street, String house, String flat) {
        Addresses addresses = new Addresses(name, readOnly, street, house, flat);
        addressesXML.add(addresses);
    }

    // задать информацию о счетчиках
    public void setCounter(String name, String required, String value) {
        Counter counter = new Counter(name, required, value);
        counterXML.add(counter);
    }

    // задатьл информаицю о суммах
    public void setSum(String name, String required, String readOnly, String value) {
        Sum sum = new Sum(name, required, readOnly, value);
        sumXML.add(sum);
    }

    // сохранить информацию в файл
    public void saveFile() {
        File file = new File(fileName);
        try {
            FileWriter fileWriter = new FileWriter(file);
            bufferedWriter = new BufferedWriter(fileWriter);


            bufferedWriter.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>" + "\n" +
                    " <Data> " + "\n");

            // выгружаем счета
            if (accountXML != null) {
                accountXML.writeStr();
            }

            // выгружаем ФИО
            if (fioXML != null) {
                fioXML.writeStr();
            }

            // выгружаем адреса
            if (addressesXML != null) {
                for (int i = 0; i < addressesXML.size(); i++) {
                    addressesXML.get(i).writeStr();
                }
            }

            // выгружаем счетчики
            if (counterXML != null) {
                for (int i = 0; i < counterXML.size(); i++) {
                    counterXML.get(i).writeStr();
                }
            }

            // выгружаем суммы
            if (sumXML != null) {
                for (int i = 0; i < sumXML.size(); i++) {
                    sumXML.get(i).writeStr();
                }
            }

            bufferedWriter.write("</Data>");
            bufferedWriter.flush();
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // отправить запрос по http
    public void saveHTTP() {
        String targetURL = "http://www.cft.ru/";
        String urlParameters = dataSend;
        HttpURLConnection connection = null;

        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length",
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

//            System.out.println(urlParameters);

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if  Java  version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
//                System.out.println(line);
            }
            rd.close();
//            System.out.println(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
            //return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

    }

    // Информация о счете
    private class Account {
        private String name;
        private String required;
        private String digitOnly;
        private String value;

        // Консструктор
        public Account(String name, String required, String digitOnly, String value) {
            this.name = name;
            this.required = required;
            this.digitOnly = digitOnly;
            this.value = value;
        }

        // сохранить в файл
        public void writeStr() throws IOException {
            String ret = "<Account ";

            if (this.name != null && !this.name.isEmpty()) {
                ret += "name=\"" + this.name + "\" ";
            }

            if (this.required != null && !this.required.isEmpty()) {
                ret += "required=\"" + this.required + "\" ";
            }

            if (this.digitOnly != null && !this.digitOnly.isEmpty()) {
                ret += "digitOnly=\"" + this.digitOnly + "\" ";
            }

            if (this.value != null && !this.value.isEmpty()) {
                ret += "value=\"" + this.value + "\" ";
            }

            ret += "/>";

            dataSend += ret + "\n";

            bufferedWriter.write(ret + "\n");
        }
    }

    // информация о ФИО
    private class Fio {
        private String name;
        private String readOnly;
        private String value;

        // Консструктор
        public Fio(String name, String readOnly, String value) {
            this.name = name;
            this.readOnly = readOnly;
            this.value = value;
        }

        // сохранить в файл
        public void writeStr() throws IOException {
            String ret = "<Fio ";

            if (this.name != null && !this.name.isEmpty()) {
                ret += "name=\"" + this.name + "\" ";
            }

            if (this.readOnly != null && !this.readOnly.isEmpty()) {
                ret += "readOnly=\"" + this.readOnly + "\" ";
            }

            if (this.value != null && !this.value.isEmpty()) {
                ret += "value=\"" + this.value + "\" ";
            }

            ret += "/>";

            dataSend += ret + "\n";

            bufferedWriter.write(ret + "\n");
        }
    }

    // информация об адресе
    private class Addresses {
        private String name;
        private String readOnly;
        private String street;
        private String house;
        private String flat;

        // Консструктор
        public Addresses(String name, String readOnly, String street, String house, String flat) {
            this.name = name;
            this.readOnly = readOnly;
            this.street = street;
            this.house = house;
            this.flat = flat;
        }

        // сохранить в файл
        public void writeStr() throws IOException {
            String ret = "<Address ";

            if (this.name != null && !this.name.isEmpty()) {
                ret += "name=\"" + this.name + "\" ";
            }

            if (this.readOnly != null && !this.readOnly.isEmpty()) {
                ret += "readOnly=\"" + this.readOnly + "\" ";
            }

            if (this.street != null && !this.street.isEmpty()) {
                ret += "street=\"" + this.street + "\" ";
            }

            if (this.house != null && !this.house.isEmpty()) {
                ret += "house=\"" + this.house + "\" ";
            }

            if (this.flat != null && !this.flat.isEmpty()) {
                ret += "flat=\"" + this.flat + "\" ";
            }

            ret += "/>";

            dataSend += ret + "\n";
            bufferedWriter.write(ret + "\n");
        }
    }

    // информация о счетчиках
    private class Counter {
        private String name;
        private String required;
        private String value;

        // Консструктор
        public Counter(String name, String required, String value) {
            this.name = name;
            this.required = required;
            this.value = value;
        }

        // сохранить в файл
        public void writeStr() throws IOException {
            String ret = "<Counter ";

            if (this.name != null && !this.name.isEmpty()) {
                ret += "name=\"" + this.name + "\" ";
            }

            if (this.required != null && !this.required.isEmpty()) {
                ret += "required=\"" + this.required + "\" ";
            }

            if (this.value != null && !this.value.isEmpty()) {
                ret += "value=\"" + this.value + "\" ";
            }

            ret += "/>";

            dataSend += ret + "\n";

            bufferedWriter.write(ret + "\n");
        }

    }

    // информация о суммах
    private class Sum {
        private String name;
        private String required;
        private String readOnly;
        private String value;

        // Консструктор
        public Sum(String name, String required, String readOnly, String value) {
            this.name = name;
            this.required = required;
            this.readOnly = readOnly;
            this.value = value;
        }

        // сохранить в файл
        public void writeStr() throws IOException {
            String ret = "<Sum ";

            if (this.name != null && !this.name.isEmpty()) {
                ret += "name=\"" + this.name + "\" ";
            }

            if (this.required != null && !this.required.isEmpty()) {
                ret += "required=\"" + this.required + "\" ";
            }

            if (this.readOnly != null && !this.readOnly.isEmpty()) {
                ret += "readOnly=\"" + this.readOnly + "\" ";
            }

            if (this.value != null && !this.value.isEmpty()) {
                ret += "value=\"" + this.value + "\" ";
            }

            ret += "/>";

            dataSend += ret + "\n";

            bufferedWriter.write(ret + "\n");
        }

    }
}
