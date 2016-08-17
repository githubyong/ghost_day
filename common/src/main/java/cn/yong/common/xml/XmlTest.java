package cn.yong.common.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;

/**
 *  用jaxb 自动序列化和反序列化 xml
 */
public class XmlTest {
    public static void main(String[] args) throws Exception {
        marshall(new ModelUser("001", "aa", "123"), new File("usr.xml"));
        ModelUser modelUser = unMarshall(new File("usr.xml"));
        System.out.println(modelUser.getName());
//        System.out.println(formatXML(new ModelUser("001", "aa", "123")));
    }


    public static void marshall(ModelUser user, File file) throws Exception {
        JAXBContext context = JAXBContext.newInstance(user.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(user, file);
    }

    public static ModelUser unMarshall(File file) throws Exception {
        JAXBContext context = JAXBContext.newInstance(ModelUser.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (ModelUser) unmarshaller.unmarshal(file);
    }

    public static <T> String formatXML(T entity) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(out));
        encoder.writeObject(entity);
        encoder.close();
        return out.toString();
    }

    public static <T> T parserXML(String xml) {
        ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes());
        XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(in));
        decoder.close();
        return (T) decoder.readObject();
    }
}
