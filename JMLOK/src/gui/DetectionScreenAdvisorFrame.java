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
		setBounds(100, 100, 640, 480);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblDetectionPhaseIs = new JLabel("Detection Phase finished.");
		lblDetectionPhaseIs.setBounds(30, 17, 219, 15);
		contentPane.add(lblDetectionPhaseIs);
		
		JButton btnNext = new JButton("Nonconformances");
		btnNext.setBounds(426, 12, 185, 25);
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				callsCategorization();
			}
		});
		contentPane.add(btnNext);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 51, 614, 388);
		contentPane.add(scrollPane);
		
		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		textArea.setText(baos.toString());
		
	}

	protected void callsCategorization() {
		Controller.showCategorizationScreen();
		setVisible(false);
	}
}
