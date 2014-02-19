package gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import utils.Constants;
import controller.Controller;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Main class to the GUI. Represents the first screen showed to JMLOK user.
 * @author Alysson Milanez and Dennis Souza
 * @version 1.0
 *
 */
public class MainScreenFrame extends JFrame {

	private JPanel contentPane;
	private JTextField textFieldSrcFolder;
	private JTextField textFieldExtLibFolder;
	private JButton btnBrowseExtLibFolder;
	private JLabel lblTime;
	private JTextField textFieldTime;
	private JButton btnRun;
	private JFileChooser dirSources;
	private JFileChooser dirLibs;
	private JLabel lblSeconds;
	private JLabel lblRunningApp;
	private Timer timer;
	private boolean buttonPressed = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainScreenFrame frame = new MainScreenFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainScreenFrame() {
		setTitle("JMLOK");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 483, 149);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		dirLibs = new JFileChooser();
		dirSources = new JFileChooser();
		
		JLabel lblSrcFolder = new JLabel("src Folder");
		lblSrcFolder.setBounds(55, 6, 90, 36);
		contentPane.add(lblSrcFolder);
		
		JLabel lblExternalLibFolder = new JLabel("external lib Folder");
		lblExternalLibFolder.setBounds(27, 38, 144, 46);
		contentPane.add(lblExternalLibFolder);
		
		textFieldSrcFolder = new JTextField();
		textFieldSrcFolder.setBounds(276, 15, 164, 19);
		contentPane.add(textFieldSrcFolder);
		textFieldSrcFolder.setColumns(10);
		
		textFieldExtLibFolder = new JTextField("");
		textFieldExtLibFolder.setBounds(276, 52, 164, 19);
		contentPane.add(textFieldExtLibFolder);
		textFieldExtLibFolder.setColumns(10);
		
		JButton btnBrowseSrcFolder = new JButton("Browse");
		btnBrowseSrcFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				browseSrcFolder();
			}
		});
		btnBrowseSrcFolder.setBounds(177, 15, 87, 19);
		contentPane.add(btnBrowseSrcFolder);
		
		btnBrowseExtLibFolder = new JButton("Browse");
		btnBrowseExtLibFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				browseExtLibFolder();
			}
		});
		btnBrowseExtLibFolder.setBounds(177, 52, 87, 19);
		contentPane.add(btnBrowseExtLibFolder);
		
		lblTime = new JLabel("Time");
		lblTime.setBounds(37, 85, 70, 15);
		contentPane.add(lblTime);
		
		textFieldTime = new JTextField("");
		textFieldTime.setBounds(95, 83, 102, 19);
		contentPane.add(textFieldTime);
		textFieldTime.setColumns(10);
		
		btnRun = new JButton("Run");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				run();
				buttonPressed = true;
			}
		});
		btnRun.setBounds(300, 80, 61, 25);
		contentPane.add(btnRun);
		
		lblSeconds = new JLabel("seconds");
		lblSeconds.setBounds(215, 83, 70, 15);
		contentPane.add(lblSeconds);
		
		lblRunningApp = new JLabel("");
		lblRunningApp.setIcon(new ImageIcon("pictures/Loading.gif"));
		lblRunningApp.setBounds(379, 85, 77, 15);
		contentPane.add(lblRunningApp);
		lblRunningApp.setVisible(false);
		
		timer = new Timer(0, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(lblRunningApp.isVisible() && buttonPressed ){
					executeProgram();
				}
			}
		});
		timer.setInitialDelay(500);
		
	}

	protected void run() {
		lblRunningApp.setVisible(true);
		timer.start();
	}

	private void executeProgram() {
		String extLibFolder = textFieldExtLibFolder.getText();
		String time = textFieldTime.getText();
		if(extLibFolder.equals(""))
			extLibFolder = Constants.JMLC_LIB;
		if(time.equals(""))
			time = "10";
		if(textFieldSrcFolder.getText().equals(""))
			JOptionPane.showMessageDialog(this, "Choose the source folder before running.");
		else{
			Controller.prepareToDetectPhase(Constants.JMLC_COMPILER, textFieldSrcFolder.getText(), extLibFolder, time);
			textFieldSrcFolder.setText("");
			textFieldExtLibFolder.setText("");
			textFieldTime.setText("");
		}
		buttonPressed = false;
		lblRunningApp.setVisible(false);
	}

	protected void browseExtLibFolder() {
		dirLibs.setApproveButtonText("Select");
		dirLibs.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (dirLibs.showOpenDialog(MainScreenFrame.this) == JFileChooser.APPROVE_OPTION) {
			textFieldExtLibFolder.setText(dirLibs.getSelectedFile().getAbsolutePath());
		}
	}

	protected void browseSrcFolder() {
		dirSources.setApproveButtonText("Select");
		dirSources.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (dirSources.showOpenDialog(MainScreenFrame.this) == JFileChooser.APPROVE_OPTION) {
			textFieldSrcFolder.setText(dirSources.getSelectedFile().getAbsolutePath());
		}
	}
}
