package gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import utils.Constants;
import controller.Controller;

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
		
		textFieldExtLibFolder = new JTextField();
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
		lblTime.setBounds(75, 85, 70, 15);
		contentPane.add(lblTime);
		
		textFieldTime = new JTextField();
		textFieldTime.setBounds(133, 83, 164, 19);
		contentPane.add(textFieldTime);
		textFieldTime.setColumns(10);
		
		btnRun = new JButton("Run");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				run();
			}
		});
		btnRun.setBounds(338, 83, 61, 25);
		contentPane.add(btnRun);
	}

	protected void run() {
		Controller.prepareToDetectPhase(Constants.JMLC_COMPILER, textFieldSrcFolder.getText(), textFieldExtLibFolder.getText(), textFieldTime.getText());
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
