import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.swing.*;

public class Operator {
    static String filePath;
    static JTextArea jta;

    public static void main(String[] args) {
        operate();
    }

    static void operate() {
        JFrame f = new JFrame("final_project");
        final JTextField tf = new JTextField();
        tf.setBounds(10, 50, 500, 30);
        jta = new JTextArea("(latitude, longitude)\n", 1566, 1);
        JScrollPane jsp = new JScrollPane(jta);
        jsp.setBounds(200, 100, 200, 300);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        JButton b = new JButton("Choose a .txt file");
        JButton d = new JButton("GGA");
        JButton e = new JButton("GGL");
        JButton g = new JButton("RMC");
        JButton h = new JButton("Copy and open Google map");

        b.setBounds(10, 20, 150, 20);
        d.setBounds(10, 100, 150, 20);
        e.setBounds(10, 130, 150, 20);
        g.setBounds(10, 160, 150, 20);
        h.setBounds(150, 450, 230, 40);

        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    filePath = selectedFile.getAbsolutePath();
                    tf.setText("File selected: " + filePath);
                }
            }
        });

        d.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jta.append("GGA:\n");
                processData("$GPGGA");
            }
        });

        e.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jta.append("GGL:\n");
                processData("$GPGLL");
            }
        });

        g.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jta.append("RMC\n");
                processData("$GPRMC");
            }
        });

        h.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedText = jta.getSelectedText();
                if (selectedText != null && !selectedText.isEmpty()) {
                    try {
                        String url = "https://www.google.com/maps/search/?api=1&query=" + selectedText;
                        java.net.URI uri = java.net.URI.create(url);
                        java.awt.Desktop dp = java.awt.Desktop.getDesktop();
                        if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {
                            dp.browse(uri);
                        }
                    } catch (java.lang.NullPointerException | java.io.IOException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select text to copy and open map.");
                }
            }
        });

        f.add(b);
        f.add(tf);
        f.add(d);
        f.add(e);
        f.add(g);
        f.add(h);

        f.add(jsp);
        f.setSize(550, 550);
        f.setLayout(null);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    static void processData(String dataType) {
        if (filePath == null) {
            JOptionPane.showMessageDialog(null, "Please select a file first.");
            return;
        }

        try {
            Scanner inFile1 = new Scanner(new File(filePath));
            while (inFile1.hasNextLine()) {
                String line = inFile1.nextLine();
                if (line.startsWith(dataType)) {
                    // extract latitude and longitude
                    String[] parts = line.split(",");
                    if (dataType.equals("$GPGGA") && parts.length >= 10) {
                        displayCoordinates(parts[2], parts[4]);
                    } else if (dataType.equals("$GPGLL") && parts.length >= 7) {
                        displayCoordinates(parts[1], parts[3]);
                    } else if (dataType.equals("$GPRMC") && parts.length >= 7) {
                        displayCoordinates(parts[3], parts[5]);
                    }
                }
            }
            inFile1.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    static void displayCoordinates(String latitude, String longitude) {
        // convert to latitude and longitude
        try {
            float lat = Float.parseFloat(latitude);
            float lat2 = lat - 2500;
            float lat3 = 25 + lat2 / 60;
            float lon = Float.parseFloat(longitude);
            float lon2 = lon - 12100;
            float lon3 = 121 + lon2 / 60;

            jta.append("(" + lat3 + "," + lon3 + ")\n");
            jta.setEditable(false);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}
