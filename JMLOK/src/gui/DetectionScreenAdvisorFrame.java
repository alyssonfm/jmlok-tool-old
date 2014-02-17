package gui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.apache.commons.io.output.ByteArrayOutputStream;

import controller.Controller;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class DetectionScreenAdvisorFrame extends JFrame {

	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public DetectionScreenAdvisorFrame(ByteArrayOutputStream baos, int numNonConformities) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 620, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblDetectionPhaseIs = new JLabel("Detection Phase finished.");
		lblDetectionPhaseIs.setBounds(12, 12, 219, 15);
		contentPane.add(lblDetectionPhaseIs);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 51, 557, 162);
		contentPane.add(scrollPane);
		
		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		textArea.setText(baos.toString());
		
		JButton btnNext = new JButton("Next >>");
		btnNext.setBounds(452, 7, 117, 25);
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				callsCategorization();
			}
		});
		contentPane.add(btnNext);
		
		JLabel lblNonconformities = new JLabel("Non-Conformances:");
		lblNonconformities.setBounds(12, 24, 163, 32);
		contentPane.add(lblNonconformities);
		
		JLabel lblQuantitync = new JLabel(numNonConformities + "");
		lblQuantitync.setBounds(179, 33, 130, 15);
		contentPane.add(lblQuantitync);
		
	}

	protected void callsCategorization() {
		Controller.showCategorizationScreen();
		setVisible(false);
	}
}
