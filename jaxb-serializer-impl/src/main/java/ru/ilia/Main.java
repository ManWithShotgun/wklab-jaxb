package ru.ilia;



import org.xml.sax.SAXException;
import ru.ilia.model.Product;
import ru.ilia.model.Products;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ILIA on 21.04.2017.
 */
public class Main {

    private final static String IN_KEY = "-in";
    private final static String OUT_KEY = "-out";
    private final static String SCHEMA_PATH = "schemas/products.xsd";

    public static void main(String[] args) {
        String inPath="", outPath="";
        List<String> listArgs = Arrays.asList(args);
        if(listArgs.contains(IN_KEY)) {
            inPath = args[listArgs.indexOf(IN_KEY)+1];
            System.out.println("in path: "+inPath);
        }

        if(listArgs.contains(OUT_KEY)) {
            outPath = args[listArgs.indexOf(OUT_KEY)+1];
            System.out.println("out path: " + outPath);
        }
        if(inPath.isEmpty() && outPath.isEmpty()) {
            System.out.println("In or out path is empty");
            return;
        }

        Products products= unmarshalling(inPath);
        printProducts(products);
        marshaling(products, outPath);
    }

    public static void printProducts(Products products) {
        System.out.println("Products: ");
        for (Product product : products.getProduct()) {
            System.out.println("Product: ");
            System.out.println("\tName: " + product.getName());
            System.out.println("\tPrice: " + product.getPrice());
            System.out.println("\tAmount: " + product.getAmount());
            System.out.println("\tDescription: " + product.getDescription());
            System.out.println("\tType: " + product.getProductType());
        }
    }

    public static void marshaling(Products products, String path){
        try {
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(readSchemaFromJar(SCHEMA_PATH));

            JAXBContext context = JAXBContext.newInstance(Products.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setSchema(schema);

            File file = new File(path);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            marshaller.marshal(products, file);
        } catch (IOException | JAXBException | SAXException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method use for read file out of jar, because just get file is impossible.
     * @return File from jar
     * @since 1.8
     */
    private static File readSchemaFromJar(String targetFile) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream initialStream = classLoader.getResourceAsStream(targetFile);
            File tempFile = new File("tempProducts.xsd");

            Files.copy(initialStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Products unmarshalling(String path){
        try {
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(readSchemaFromJar(SCHEMA_PATH));

            JAXBContext jaxbContext=JAXBContext.newInstance(Products.class);
            Unmarshaller unmarshaller=jaxbContext.createUnmarshaller();
            unmarshaller.setSchema(schema);
            return (Products) unmarshaller.unmarshal(new File(path));
        } catch (SAXException | JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

}
