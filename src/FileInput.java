import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;

public class FileInput {

    // наименование файла для импорта
    private String filepath = null;

    // конструктор
    public FileInput(String filepath) {
        this.filepath = filepath;
    }

    // сохранить файл в списке
    public ArrayList<StrXML> xmlIn = new ArrayList<>();

    // сохранить файл в списке с параметрами
    public void setStrXML(String name, String type, String value, String required, String digitOnly, String readOnly) {
        // инициализируем все параметры
        StrXML strXML = new StrXML();
        strXML.setName(name);
        strXML.setType(type);
        strXML.setValue(value);
        strXML.setRequired(required);
        strXML.setDigitOnly(digitOnly);
        strXML.setReadOnly(readOnly);

        // запишем в разобранном виде
        xmlIn.add(strXML);
    }

    // загрузить файл
    public void loadFile() {
        File xmlFile = new File(filepath);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();
//            System.out.println("Корневой элемент: " + document.getDocumentElement().getNodeName());

            NodeList nodeList = document.getElementsByTagName("Field");

            for (int i = 0; i < nodeList.getLength(); i++) {
                // для отладки
/*                System.out.print("name: \"" + getTagValue("name", (Element) nodeList.item(i)) + "\"; ");
                System.out.print("type: \"" + getTagValue("type", (Element) nodeList.item(i)) + "\"; ");
                System.out.print("value: \"" + getTagValue("value", (Element) nodeList.item(i)) + "\"; ");
                System.out.print("required: \"" + getTagValue("required", (Element) nodeList.item(i)) + "\"; ");
                System.out.print("digitOnly: \"" + getTagValue("digitOnly", (Element) nodeList.item(i)) + "\"; ");
                System.out.print("readOnly: \"" + getTagValue("readOnly", (Element) nodeList.item(i)) + "\"; ");
                System.out.println("");*/

                // сохраним
                setStrXML(getTagValue("name", (Element) nodeList.item(i)),
                        getTagValue("type", (Element) nodeList.item(i)),
                        getTagValue("value", (Element) nodeList.item(i)),
                        getTagValue("required", (Element) nodeList.item(i)),
                        getTagValue("digitOnly", (Element) nodeList.item(i)),
                        getTagValue("readOnly", (Element) nodeList.item(i)));

            }


        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    // получаем значение элемента по указанному тегу
    private static String getTagValue(String tag, Element element) {
        try {
            NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
            Node node = (Node) nodeList.item(0);
            return node.getNodeValue();
        } catch (NullPointerException e) {
            // значение тэга отсутствует, считем его пустым
        }
        return "";
    }

    // хранение строки из файла xml
    public class StrXML {
        private String name;
        private String type;
        private String value;
        private String required;
        private String digitOnly;
        private String readOnly;

        public void setName(String name) {
            this.name = name;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public void setRequired(String required) {
            this.required = required;
        }

        public void setDigitOnly(String digitOnly) {
            this.digitOnly = digitOnly;
        }

        public void setReadOnly(String readOnly) {
            this.readOnly = readOnly;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getValue() {
            return value;
        }

        public String getRequired() {
            return required;
        }

        public String getDigitOnly() {
            return digitOnly;
        }

        public String getReadOnly() {
            return readOnly;
        }
    }
}
