package at.htl.roboführerscheinbuilder.business;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.MimeConstants;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

/**
 *
 * @author Mat
 */
public class Builder {

    public Builder() {

    }

    private Document setValueInPdf(String name, String value, Document doc) {
        System.out.println("CertificateGenerator.setCandidateNameInPDF()");
        System.out.println(name + "\t" + value);
        try {
            List<?> nameList = XPath.selectNodes( doc, "//fo:block[@id='" + name + "']" );
            System.out.println("length: " + nameList.size());
            for ( Object object : nameList ) {
                Element element = (Element) object;
                System.out.println(element.toString());
                element.setText(value);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("end setCandidateNameInPDF()");
        return doc;
    }

    private Document setPhotoPathInPdf(String name, String path, Document doc) {
        System.out.println(name + "\t" + path);
        try {
            List<?> nameList = XPath.selectNodes( doc, "//fo:external-graphic[@id='" + name + "']" );
            System.out.println("length: " + nameList.size());
            for ( Object object : nameList ) {
                Element element = (Element) object;
                System.out.println(element.toString());
                element.setAttribute("src", path);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return doc;
    }

    public String generatePDF(String pfad, String vorname, String nachname, String geburtsdatum, String ausstellungsdatum, String path) {

        FopFactory fopFactory = FopFactory.newInstance();
        OutputStream out = null;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd HH_mm_ss_SSS", Locale.ENGLISH);
        String xmlPath = pfad+nachname+"_"+vorname+"_" + sdf.format(date) + "_roboschein.xfd";
        String pdfPath = pfad+nachname+"_"+vorname+"_" + sdf.format(date) + "_roboschein.pdf";
        try {
            Document doc = new SAXBuilder().build(xmlPath);
            XMLOutputter xmlOutputter = new XMLOutputter();

            doc = this.setValueInPdf("vorname", vorname.toUpperCase(), doc);
            doc = this.setValueInPdf("nachname", nachname.toUpperCase(), doc);
            doc = this.setValueInPdf("geburtsdatum", geburtsdatum, doc);
            doc = this.setValueInPdf("ausstellungsdatum", ausstellungsdatum, doc);
            //doc = this.setValueInPdf("foto", "url(C:/Users/Mat/Desktop/School/5AHDVK/TdoT/Da Challo.png)", doc);
            if(!path.equals(""))
                doc = this.setPhotoPathInPdf("foto", path, doc);

            out = new BufferedOutputStream(new FileOutputStream(new File(pdfPath)));
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();

            ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
            xmlOutputter.output(doc, byteArrayOut);
            Source src = new StreamSource(new ByteArrayInputStream(byteArrayOut.toByteArray()));
            Result res = new SAXResult(fop.getDefaultHandler());
            transformer.transform(src, res);
        }
        catch (Exception ex) {
            return "Fehler beim schreiben der Datei!";
        }
        finally {
            try {
                out.close();
                return "Erfolgreich!";
            }
            catch (IOException ex) {
                return "Fehler beim schreiben der Datei!";
            }
            catch (Exception ex) {
                return "Fehler beim schreiben in die Datei!";
            }
        }
    }
}