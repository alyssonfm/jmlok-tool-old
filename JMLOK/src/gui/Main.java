package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import controller.Controller;
import utils.Constants;
import detect.Detect;

@SuppressWarnings("serial")
public class Main extends JFrame{
	JButton chooseSource;
	JButton chooseLibs;
	JButton run;
	JFileChooser dirSources;
	JFileChooser dirLibs;
	JLabel sourceDir;
	JLabel libsDir;
	JLabel timeTests;
	JTextField time;
	JPanel panel1;
	JPanel panel2;
	JPanel panel3;
	JPanel panel4;
	JPanel panelMain;
	//JTextArea logExec;
	
	private void createInterface(){
		chooseSource = new JButton("Choose source folder");
		chooseLibs = new JButton("Choose lib folder");
		run = new JButton("OK");
		dirSources = new JFileChooser();
		dirLibs = new JFileChooser();
		sourceDir = new JLabel();
		libsDir = new JLabel();
		timeTests = new JLabel("Time to tests generation: ");
		time = new JTextField();
		panel1 = new JPanel();
		panel2 = new JPanel();
		panel3 = new JPanel();
		panel4 = new JPanel();
		panelMain = new JPanel();
		panel3.setLayout(new BoxLayout(panel3, BoxLayout.Y_AXIS));
		panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.Y_AXIS));
		//logExec = new JTextArea("");
		
		chooseLibs.setEnabled(false);
		run.setEnabled(false);
		time.setEnabled(false);

		chooseSource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dirSources.setApproveButtonText("Select");
				dirSources.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (dirSources.showOpenDialog(panel1) == JFileChooser.APPROVE_OPTION) {
					sourceDir.setText(dirSources.getSelectedFile()
							.getAbsolutePath());
					chooseLibs.setEnabled(true);
					time.setEnabled(true);
					run.setEnabled(true);
				}

			}
		});

		chooseLibs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dirLibs.setApproveButtonText("Select");
				dirLibs.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if (dirLibs.showOpenDialog(panel2) == JFileChooser.APPROVE_OPTION) {
					libsDir.setText(dirLibs.getSelectedFile()
							.getAbsolutePath());
				}
			}
		});

		panel1.add(chooseSource);
		panel1.add(sourceDir);
		panel2.add(chooseLibs);
		panel2.add(libsDir);
		panel3.add(timeTests);
		panel3.add(time);
		panel4.add(run);
		//panel3.add(logExec);
		
		run.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Controller.prepareToDetectPhase(Constants.JMLC_COMPILER, sourceDir.getText(), libsDir.getText(), time.getText());
				JOptionPane.showMessageDialog(null, "Finished");
			}
		});
		
		panelMain.add(panel1);
		panelMain.add(panel2);
		panelMain.add(panel3);
		panelMain.add(panel4);

		getContentPane().add(panelMain);
	}

	public Main() {
		super("JMLOK");
		this.createInterface();
	}

	public static void main(String args[]) {
		Main m = new Main();
		m.setLocation(150, 150);
		m.setSize(800, 200);
		m.setVisible(true);
		m.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
