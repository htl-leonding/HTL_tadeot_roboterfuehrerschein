/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package roboscheinbuilder;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Mat
 */
/*
public class GUI extends JFrame implements ActionListener {
    

    JButton btnDrucken;
    JButton btnFoto;
    JTextField txtVorname;
    JTextField txtNachname;
    JTextField txtGeburtsdatum;
    JFileChooser fcFoto;
    JLabel statusMeldung;

    public GUI(){
        this.setTitle("RoboFührerscheinBuilder");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(new Dimension(300, 300));
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        JPanel pnlMain = new JPanel(new GridLayout(6, 2));
        //pnlMain.setSize(new Dimension(250, 250));

        txtVorname = new JTextField(" ", 12);
        txtNachname = new JTextField(" ", 12);
        txtGeburtsdatum = new JTextField(" ", 12);
        fcFoto = new JFileChooser("\\\\duke\\tdot09");
        btnDrucken = new JButton("Erstellen");
        btnDrucken.addActionListener(this);
        btnFoto = new JButton("Foto auswählen");
        btnFoto.addActionListener(this);
        statusMeldung = new JLabel(" ");

        pnlMain.add(new JLabel("Vorname: "));
        pnlMain.add(txtVorname);
        pnlMain.add(new JLabel("Nachname: "));
        pnlMain.add(txtNachname);
        pnlMain.add(new JLabel("Geburtsdatum: "));
        pnlMain.add(txtGeburtsdatum);
        pnlMain.add(btnFoto);
        pnlMain.add(btnDrucken);
        pnlMain.add(statusMeldung);

        this.add(pnlMain);

        this.setVisible(true);
    }
    public static void main(String[] args) {
        System.out.println("start");
        new GUI();
        System.out.println("finished");
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btnDrucken) {
            RoboScheinBuilder robo = new RoboScheinBuilder();
            String vorname = txtVorname.getText();
            String nachname = txtNachname.getText();
            String geburtsdatum = txtGeburtsdatum.getText();
            String ausstellungsdatum;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            ausstellungsdatum = dateFormat.format(new Date());
            String path = "";
            if (fcFoto.getSelectedFile() != null)
                path = fcFoto.getSelectedFile().getPath();
            statusMeldung.setText(robo.generatePDF(vorname, nachname, geburtsdatum, ausstellungsdatum, path));
            //print;
        }
        else if(e.getSource() == btnFoto) {
            fcFoto.showOpenDialog(this);
            String name = fcFoto.getSelectedFile().getName();
            String vorname1 = name.split(" ")[1];
            vorname1 = vorname1.substring(0, vorname1.length() - 4);
            String nachname1 = name.split(" ")[0];
            txtVorname.setText(vorname1);
            txtNachname.setText(nachname1);
        }
    }

}*/
