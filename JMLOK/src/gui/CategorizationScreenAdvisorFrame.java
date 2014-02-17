package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.AbstractListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import categorize.Nonconformance;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JInternalFrame;
import javax.swing.JTextArea;

import utils.Constants;
import utils.FileUtil;

public class CategorizationScreenAdvisorFrame extends JFrame {
	
	private List<Nonconformance> nc;
	private String[] namesNC;
	private JPanel contentPane;
	private JList listNonconformances;
	private JLabel lblMethodNameSetter;
	private JLabel lblClassNameSetter;
	private JLabel lblLikelyCauseSetter;
	private JLabel lblLikelyCauseExplanationSetter;
	private JLabel lblPackageNameSetter;
	private JLabel lblPackageName;
	private JTextArea textAreaTestCases;

	/**
	 * Create the frame.
	 */
	 public CategorizationScreenAdvisorFrame(List<Nonconformance> nonconformance) {
		nc = nonconformance;
		namesNC = new String[nc.size()];
		for (int i = 0; i < nc.size(); i++) {
			namesNC[i] = nc.get(i).getType() + " " + nc.get(i).getTest(); 
		}
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 639, 464);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNonconf = new JLabel("Nonconformances");
		lblNonconf.setBounds(45, 27, 214, 15);
		contentPane.add(lblNonconf);
		
		JLabel lblNewLabel = new JLabel("Location");
		lblNewLabel.setBounds(283, 27, 70, 15);
		contentPane.add(lblNewLabel);
		
		JLabel lblLikelyCause = new JLabel("Likely Cause");
		lblLikelyCause.setBounds(456, 27, 106, 15);
		contentPane.add(lblLikelyCause);
		
		JLabel lblTestCases = new JLabel("Test Cases");
		lblTestCases.setBounds(34, 222, 134, 15);
		contentPane.add(lblTestCases);
		
		JButton btnSaveResults = new JButton("Save Results");
		btnSaveResults.setBounds(446, 263, 143, 25);
		contentPane.add(btnSaveResults);
		
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeWindow();
			}
		});
		btnExit.setBounds(446, 317, 143, 25);
		contentPane.add(btnExit);
		
		textAreaTestCases = new JTextArea();
		textAreaTestCases.setBounds(24, 242, 401, 100);
		
		JLabel lblClassName = new JLabel("Class Name");
		lblClassName.setBounds(220, 54, 106, 15);
		contentPane.add(lblClassName);
		
		JLabel lblMethodName = new JLabel("Method Name");
		lblMethodName.setBounds(220, 110, 106, 15);
		contentPane.add(lblMethodName);
		
		lblClassNameSetter = new JLabel("");
		lblClassNameSetter.setBounds(269, 81, 134, 15);
		contentPane.add(lblClassNameSetter);
		
		lblMethodNameSetter = new JLabel("");
		lblMethodNameSetter.setBounds(269, 137, 134, 15);
		contentPane.add(lblMethodNameSetter);
		
		JLabel lblCause = new JLabel("Cause");
		lblCause.setBounds(426, 55, 70, 15);
		contentPane.add(lblCause);
		
		lblLikelyCauseSetter = new JLabel("");
		lblLikelyCauseSetter.setBounds(446, 66, 143, 25);
		contentPane.add(lblLikelyCauseSetter);
		
		lblLikelyCauseExplanationSetter = new JLabel("Explanation");
		lblLikelyCauseExplanationSetter.setBounds(426, 103, 153, 15);
		contentPane.add(lblLikelyCauseExplanationSetter);
		
		lblPackageName = new JLabel("");
		lblPackageName.setBounds(233, 164, 133, 15);
		contentPane.add(lblPackageName);
		
		lblPackageNameSetter = new JLabel("");
		lblPackageNameSetter.setBounds(269, 191, 134, 15);
		contentPane.add(lblPackageNameSetter);
		
		listNonconformances = new JList(namesNC);
		listNonconformances.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listNonconformances.setBounds(24, 54, 178, 156);
		contentPane.add(listNonconformances);
		
		listNonconformances.addListSelectionListener(
			new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					lblClassNameSetter.setText(nc.get(listNonconformances.getSelectedIndex()).getClassName());
					lblMethodNameSetter.setText(nc.get(listNonconformances.getSelectedIndex()).getMethodName());
					lblLikelyCauseSetter.setText(nc.get(listNonconformances.getSelectedIndex()).getCause());
					if(nc.get(listNonconformances.getSelectedIndex()).getPackageName() == ""){
						lblPackageName.setText("");
						lblPackageNameSetter.setText(nc.get(listNonconformances.getSelectedIndex()).getPackageName());						
					}else{
						lblPackageName.setText("Package Name");
						lblPackageNameSetter.setText(nc.get(listNonconformances.getSelectedIndex()).getPackageName());						
					}
					textAreaTestCases.setText(testCaseContent(nc.get(listNonconformances.getSelectedIndex()).getTestFile(), nc.get(listNonconformances.getSelectedIndex()).getLineNumber()));
				}
			}
		);
		
		JScrollPane scrollPane = new JScrollPane(listNonconformances);
		scrollPane.setBounds(24, 54, 178, 150);
		contentPane.add(scrollPane);
		
		
		contentPane.add(textAreaTestCases);
		
		JScrollPane scrollPane_2 = new JScrollPane(textAreaTestCases);
		scrollPane_2.setBounds(27, 250, 398, 92);
		contentPane.add(scrollPane_2);
	}

	protected String testCaseContent(String testFile, int wishedLine) {
		BufferedReader f;
		String toReturn = "";
		try {
			f = new BufferedReader(new FileReader(Constants.TEST_DIR + Constants.FILE_SEPARATOR + testFile));
			String line;
			int counterLines = 0;
			try {
				while ((line = f.readLine()) != null) {
					counterLines++;
					toReturn += line+"\n";
					if(counterLines == wishedLine)
						break;
				}
				f.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return toReturn;
	}

	protected void closeWindow() {
		this.setVisible(false);
	}
}
