package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
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
import java.util.List;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JInternalFrame;

public class CategorizationScreenAdvisorFrame extends JFrame {
	
	private List<Nonconformance> nc;
	private JPanel contentPane;
	private JList<String> listNonconformances;
	private JLabel lblMethodNameSetter;
	private JLabel lblClassNameSetter;
	private JLabel lblLikelyCauseSetter;
	private JLabel lblLikelyCauseExplanationSetter;

	/**
	 * Create the frame.
	 */
	public CategorizationScreenAdvisorFrame(List<Nonconformance> nonconformance) {
		nc = nonconformance;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 559, 383);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNonconf = new JLabel("Nonconformances");
		lblNonconf.setBounds(24, 27, 214, 15);
		contentPane.add(lblNonconf);
		
		listNonconformances = new JList<String>();
		listNonconformances.setModel(new AbstractListModel<String>() {
			public int getSize() { return nc.size(); }
			public String getElementAt(int index) { return nc.get(index).getType() + " " + nc.get(index).getTest(); }
		});
		listNonconformances.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listNonconformances.setBounds(24, 54, 134, 156);
		contentPane.add(listNonconformances);
		
		listNonconformances.addListSelectionListener(
			new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					lblClassNameSetter.setText(nc.get(listNonconformances.getSelectedIndex()).getClassName());
					lblMethodNameSetter.setText(nc.get(listNonconformances.getSelectedIndex()).getMethodName());
					lblLikelyCauseSetter.setText(nc.get(listNonconformances.getSelectedIndex()).getCause());
				}
			}
		);
		
		JLabel lblNewLabel = new JLabel("Location");
		lblNewLabel.setBounds(239, 27, 70, 15);
		contentPane.add(lblNewLabel);
		
		JLabel lblLikelyCause = new JLabel("Likely Cause");
		lblLikelyCause.setBounds(402, 27, 106, 15);
		contentPane.add(lblLikelyCause);
		
		JLabel lblTestCases = new JLabel("Test Cases");
		lblTestCases.setBounds(34, 222, 134, 15);
		contentPane.add(lblTestCases);
		
		JButton btnSaveResults = new JButton("Save Results");
		btnSaveResults.setBounds(386, 245, 143, 25);
		contentPane.add(btnSaveResults);
		
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeWindow();
			}
		});
		btnExit.setBounds(386, 282, 143, 25);
		contentPane.add(btnExit);
		
		JLabel lblClassName = new JLabel("Class Name");
		lblClassName.setBounds(176, 66, 106, 15);
		contentPane.add(lblClassName);
		
		JLabel lblMethodName = new JLabel("Method Name");
		lblMethodName.setBounds(176, 127, 106, 15);
		contentPane.add(lblMethodName);
		
		lblClassNameSetter = new JLabel("");
		lblClassNameSetter.setBounds(219, 93, 134, 15);
		contentPane.add(lblClassNameSetter);
		
		lblMethodNameSetter = new JLabel("");
		lblMethodNameSetter.setBounds(219, 162, 134, 15);
		contentPane.add(lblMethodNameSetter);
		
		JLabel lblCause = new JLabel("Cause");
		lblCause.setBounds(368, 54, 70, 15);
		contentPane.add(lblCause);
		
		lblLikelyCauseSetter = new JLabel("");
		lblLikelyCauseSetter.setBounds(386, 66, 143, 25);
		contentPane.add(lblLikelyCauseSetter);
		
		lblLikelyCauseExplanationSetter = new JLabel("Explanation");
		lblLikelyCauseExplanationSetter.setBounds(376, 127, 153, 93);
		contentPane.add(lblLikelyCauseExplanationSetter);
	}

	protected void closeWindow() {
		this.setVisible(false);
	}
}
