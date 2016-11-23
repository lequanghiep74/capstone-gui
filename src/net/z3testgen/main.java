package net.z3testgen;

import net.z3testgen.create_test.CreateTestcaseBdd;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by lequanghiep on 11/20/2016.
 */
public class main {
    private JTabbedPane tabbedPane;
    private JPanel mainPanel;
    private JPanel dslPanel;
    private JLabel dslLabel;
    private JTextField dslTextview;
    private JButton dslSelect;
    private JTextField dataTextview;
    private JButton dataSelect;
    private JPanel dataPanel;
    private JPanel buttonPanel;
    private JButton btnGen;
    private JPanel genDslPanel;
    private JPanel genBddPanel;
    private JPanel bddPanel;
    private JPanel dataBddPanel;
    private JPanel ButtonPanel;
    private JTextField bddTextField;
    private JButton dataBddSelect;
    private JButton bddSelect;
    private JTextField dataBddTextview;
    private JButton genBddBtn;
    private String fileName;
    private GenTest genTest = new GenTest();

    public main() {
        tabbedPane = new JTabbedPane();
        genDslPanel.setLayout(new GridLayout(0, 1));
        genBddPanel.setLayout(new GridLayout(0, 1));
        tabbedPane.addTab("Gen test", null, genDslPanel, "Does nothing");
        tabbedPane.addTab("Gen bdd", null, genBddPanel, "Does nothing");
        GridBagConstraints myConstraints = new GridBagConstraints();
        myConstraints.ipadx = 80;//streches the component being added along x axis - 200 px on both sides
        myConstraints.ipady = 20;//streches the component being added along y axis - 200 px on both sides
        mainPanel.add(tabbedPane, myConstraints);
        dslTextview.setColumns(10);
        dataTextview.setColumns(10);
        bddTextField.setColumns(10);
        dataBddTextview.setColumns(10);
        dslSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectFile(dslTextview);
            }
        });
        dataSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = fileChooser.showOpenDialog(mainPanel);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        dataTextview.setText(fileChooser.getSelectedFile().getPath());
                    } catch (Exception ex) {
                        System.out.println("problem accessing file");
                    }
                } else {
                    System.out.println("File access cancelled by user.");
                }
            }
        });
        btnGen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    fileName = getBaseName(dslTextview.getText());
                    genTest.genTest(dslTextview.getText(), dataTextview.getText() + "/" + fileName + ".csv");
                    aleart("Success", "Gen bdd success");
                } catch (Exception e1) {
                    aleart("Error", e1.getMessage());
                }
            }
        });
        genBddBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CreateTestcaseBdd.genTestCaseBdd(dataBddTextview.getText(), bddTextField.getText()
                        , getBaseName(bddTextField.getText()) + "_cucumber.feature");
                aleart("Success", "Gen bdd success");
            }
        });
        dataBddSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectFile(dataBddTextview);
            }
        });
        bddSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectFile(bddTextField);
            }
        });
    }

    public static void main(String[] args) {
        JFrame jFrame = new JFrame("T-GATUA");
        jFrame.setLocationRelativeTo(null);
        jFrame.setResizable(false);
        jFrame.setContentPane(new main().mainPanel);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.pack();
        jFrame.setVisible(true);
    }

    public static String getBaseName(String fileDir) {
        int indexSlash = fileDir.lastIndexOf('\\');
        fileDir = fileDir.substring(indexSlash + 1);
        int indexDot = fileDir.lastIndexOf('.');
        if (indexDot == -1) {
            return fileDir;
        } else {
            return fileDir.substring(0, indexDot);
        }
    }

    public void selectFile(JTextField txtField) {
        final JFileChooser fileChooser = new JFileChooser();
        int returnVal = fileChooser.showOpenDialog(mainPanel);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                txtField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            } catch (Exception ex) {
                System.out.println("problem accessing file");
            }
        } else {
            System.out.println("File access cancelled by user.");
        }
    }

    public void aleart(String head, String content) {
        JOptionPane optionPane = new JOptionPane(content, JOptionPane.WARNING_MESSAGE);
        JDialog dialog = optionPane.createDialog(head);
        dialog.setAlwaysOnTop(true); // to show top of all other application
        dialog.setVisible(true); // to visible the dialog
    }
}
