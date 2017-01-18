/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htl.roboführerscheinbuilder.gui;


import at.htl.roboführerscheinbuilder.business.Builder;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


/**
 *
 * @author Jochen Bamminger
 */
public class Gui extends JFrame implements ActionListener {

    String pfad = "C:\\Users\\living\\Desktop\\Robofuehrerschein\\RoboScheinBuilder";              // Pfad des Ordners wo das Projekt, die Datein robo-schein.jpg, roboschein.pdf und robschein.xfd sind
    String searchPath = "C:\\Users\\living\\Desktop\\";        // Basispfad, wenn das Fenster für die Bildersuche sich öffnet

    /*
     * WICHTIG
     *
     * In der Datei 'roboschein.xfd' muss der Pfad auch aktualisiert werden!
     *
     */
    JFrame mainFrame;
    JLabel vorName;
    JLabel nachName;
    JLabel gebDat;
    JLabel meldung;
    JPanel name;
    JPanel vorNamePanel;
    JPanel nachNamePanel;
    JPanel gebDatPanel;
    JPanel buttonPanel;
    JTextField vorNameTxt;
    JTextField nachNameTxt;
    JTextField gebDatTxt;
    JFileChooser file;
    JButton picture;
    JButton printPDF;

    public Gui() {
        try {
            read();
        } catch (FileNotFoundException f) {
            JOptionPane.showMessageDialog(this, f.getMessage(), "Datei - Fehler", JOptionPane.OK_OPTION);
            System.exit(1);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Fehler", JOptionPane.OK_OPTION);
            System.exit(1);
        }

        mainFrame = new JFrame("RoboFührerscheinBuilder");
        mainFrame.setBounds(300, 200, 400, 235);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        vorName = new JLabel("    Vorname:     ");
        vorNameTxt = new JTextField(20);
        nachName = new JLabel("   Nachname:   ");
        nachNameTxt = new JTextField(20);
        gebDat = new JLabel("Geburtsdatum:");
        gebDatTxt = new JTextField(20);
        gebDatTxt.setToolTipText("Datum im Format dd-mm-yyyy");
        meldung = new JLabel();

        vorNamePanel = new JPanel();
        vorNamePanel.add(vorName);
        vorNamePanel.add(vorNameTxt);

        nachNamePanel = new JPanel();
        nachNamePanel.add(nachName);
        nachNamePanel.add(nachNameTxt);

        gebDatPanel = new JPanel();
        gebDatPanel.add(gebDat);
        gebDatPanel.add(gebDatTxt);

        file = new JFileChooser(searchPath);

        picture = new JButton("Bild auswählen");
        picture.addActionListener(this);
        printPDF = new JButton("PDF erstellen");
        printPDF.addActionListener(this);

        buttonPanel = new JPanel();
        buttonPanel.add(picture);
        buttonPanel.add(printPDF);

        name = new JPanel();
        name.add(vorNamePanel);
        name.add(nachNamePanel);
        name.add(gebDatPanel);
        name.add(buttonPanel);

        mainFrame.add(new JLabel("\r\n"), BorderLayout.NORTH);
        mainFrame.add(name, BorderLayout.CENTER);
        mainFrame.add(meldung, BorderLayout.SOUTH);

        mainFrame.setVisible(true);
    }

    public void read() throws Exception {
        File file = new File("ini.properties");
        if (!file.exists()) {
            throw new FileNotFoundException("ini Datei nicht vorhanden!");
        }

        Properties prop = new Properties();
        try {

            prop.load(new FileInputStream("ini.properties"));
            pfad = prop.getProperty("path");
            searchPath = prop.getProperty("searchPath");

        } catch (Exception e) {
            System.out.println("FileNotFoundException");
        }
    }

    public static void main(String[] args) {
        System.out.println("RoboFührerscheinBuilder - start");
        new Gui();
        System.out.println("RoboFührerscheinBuilder - finished");
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == printPDF) {
            Builder robo = new Builder();
            String vorname = vorNameTxt.getText();
            String nachname = nachNameTxt.getText();
            String gesname="";
            String geburtsdatum = gebDatTxt.getText();
            String ausstellungsdatum;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            ausstellungsdatum = dateFormat.format(new Date());
            String path = "";

            if (file.getSelectedFile() != null) {
                path = file.getSelectedFile().getPath();
            }
            else
            {
                gesname=nachname+" "+vorname;
                path=searchPath+gesname+".png";
            }
            meldung.setText(" " + robo.generatePDF(pfad, vorname, nachname, geburtsdatum, ausstellungsdatum, path));
            path = null;
            file.setSelectedFile(null);
            Process p;
            try {
                p = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler "+pfad+"roboschein.pdf");
                p.waitFor();
            } catch (Exception ex) {
            }
        } else if (e.getSource() == picture) {
            try {
                file.showOpenDialog(this);
                String datei = file.getSelectedFile().getName();
                meldung.setText(datei);
                String vorname1 = datei.split(" ")[1];
                vorname1 = vorname1.substring(0, vorname1.length() - 4);
                String nachname1 = datei.split(" ")[0];
                vorNameTxt.setText(vorname1);
                nachNameTxt.setText(nachname1);
                meldung.setText(" " + datei + " ausgewählt");
            } catch (Exception ex) {
                meldung.setText(" Fehler bei der Dateiauswahl!");
            }
        }
    }
}
