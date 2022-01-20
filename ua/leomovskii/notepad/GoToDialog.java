package ua.leomovskii.notepad;

import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;

public class GoToDialog {

	private JDialog gotoDialog;
	private JTextArea textArea;

	public GoToDialog(JFrame main, JTextArea textArea) {
		gotoDialog = new JDialog(main, "Переход на строку", true);
		gotoDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		gotoDialog.setSize(300, 110);
		gotoDialog.setResizable(false);
		gotoDialog.setLocationRelativeTo(null);
		gotoDialog.setLayout(null);

		gotoDialog.getRootPane().registerKeyboardAction(e -> gotoDialog.dispose(),
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

		this.textArea = textArea;

		initUI();

		gotoDialog.setVisible(true);
	}

	private void initUI() {
		JLabel lineNumber = new JLabel("Номер строки:");
		lineNumber.setBounds(10, 10, 100, 20);
		gotoDialog.add(lineNumber);

		JTextField inputField = new JTextField();
		try {
			int n = textArea.getLineOfOffset(textArea.getCaretPosition()) + 1;
			inputField.setText(String.valueOf(n));
			inputField.selectAll();
		} catch (BadLocationException e) {
			inputField.setText("");
		}
		inputField.setBounds(102, 10, 174, 20);
		gotoDialog.add(inputField);

		int lines = textArea.getLineCount();
		if (lines < 100000) {
			JLabel maxNumber = new JLabel("Макс: " + String.valueOf(lines));
			maxNumber.setBounds(10, 44, 80, 20);
			gotoDialog.add(maxNumber);
		}

		JButton buttonOK = new JButton("ОК");
		buttonOK.setBounds(94, 44, 80, 20);
		buttonOK.addActionListener(e -> {
			try {
				int n = Integer.parseInt(inputField.getText());
				textArea.setCaretPosition(textArea.getLineStartOffset(n - 1));
			} catch (Exception ex) {
			}
			gotoDialog.dispose();
		});
		gotoDialog.add(buttonOK);

		JButton buttonCancel = new JButton("Отмена");
		buttonCancel.setBounds(196, 44, 80, 20);
		buttonCancel.addActionListener(e -> gotoDialog.dispose());
		gotoDialog.add(buttonCancel);
	}
}
