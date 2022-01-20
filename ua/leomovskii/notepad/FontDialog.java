package ua.leomovskii.notepad;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

public class FontDialog {

	private String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

	private String[] styleNames = { "Обычный", "Жирный", "Курсив", "Жирный курсив" };
	private int[] styles = { Font.PLAIN, Font.BOLD, Font.ITALIC, Font.BOLD | Font.ITALIC };

	private String[] fontSizeStrings = { "8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "26", "28",
			"36", "48", "72" };
	private int[] fontSizes = { 8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72 };

	private JDialog fontDialog;
	private JTextArea textArea, exampleText;
	private JComboBox<String> boxFontFamily, boxFontStyle, boxFontSize;

	public FontDialog(JFrame mainFrame, JTextArea textArea) {
		fontDialog = new JDialog(mainFrame, "Шрифт", true);
		fontDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		fontDialog.setSize(300, 300);
		fontDialog.setResizable(false);
		fontDialog.setLocationRelativeTo(null);
		fontDialog.setLayout(null);

		fontDialog.getRootPane().registerKeyboardAction(e -> fontDialog.dispose(),
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

		this.textArea = textArea;

		initUI();

		fontDialog.setVisible(true);
	}

	private void initUI() {
		exampleText = new JTextArea("Пример текста\n\nText example\n\n文本示例\n\n0 1 2 3 4 5 6 7 8 9");
		exampleText.setFont(textArea.getFont());
		exampleText.setTabSize(1);
		exampleText.setLineWrap(true);
		exampleText.setCaretPosition(exampleText.getText().length());
		exampleText.setMargin(new Insets(3, 3, 3, 3));

		JScrollPane scrollPane = new JScrollPane(exampleText);
		scrollPane.setBounds(5, 5, 276, 145);
		fontDialog.add(scrollPane);

		JLabel fontFamily = new JLabel("Шрифт:");
		fontFamily.setBounds(10, 155, 80, 20);
		fontDialog.add(fontFamily);

		boxFontFamily = new JComboBox<String>(fonts);
		boxFontFamily.setSelectedItem(textArea.getFont().getFamily());
		boxFontFamily.setBounds(80, 155, 200, 20);
		boxFontFamily.addActionListener(e -> updateFont());
		fontDialog.add(boxFontFamily);

		JLabel fontStyle = new JLabel("Стиль:");
		fontStyle.setBounds(10, 180, 80, 20);
		fontDialog.add(fontStyle);

		boxFontStyle = new JComboBox<String>(styleNames);
		boxFontStyle.setSelectedItem(styleNames[textArea.getFont().getStyle()]);
		boxFontStyle.setBounds(80, 180, 200, 20);
		boxFontStyle.addActionListener(e -> updateFont());
		fontDialog.add(boxFontStyle);

		JLabel fontSize = new JLabel("Размер:");
		fontSize.setBounds(10, 205, 80, 20);
		fontDialog.add(fontSize);

		boxFontSize = new JComboBox<String>(fontSizeStrings);
		boxFontSize.setSelectedItem(getFontSizeString());
		boxFontSize.setBounds(80, 205, 200, 20);
		boxFontSize.addActionListener(e -> updateFont());

		fontDialog.add(boxFontSize);

		JButton buttonOK = new JButton("ОК");
		buttonOK.setBounds(94, 235, 80, 20);
		buttonOK.addActionListener(e -> {
			textArea.setFont(exampleText.getFont());
			fontDialog.dispose();
		});
		fontDialog.add(buttonOK);

		JButton buttonCancel = new JButton("Отмена");
		buttonCancel.setBounds(196, 235, 80, 20);
		buttonCancel.addActionListener(e -> fontDialog.dispose());
		fontDialog.add(buttonCancel);
	}

	private void updateFont() {
		String fontFamily = (String) boxFontFamily.getSelectedItem();
		int fontStyle = styles[boxFontStyle.getSelectedIndex()];
		int fontSize = fontSizes[boxFontSize.getSelectedIndex()];

		exampleText.setFont(new Font(fontFamily, fontStyle, fontSize));
	}

	private String getFontSizeString() {
		for (int i = 0; i < fontSizes.length; i++)
			if (fontSizes[i] == textArea.getFont().getSize())
				return fontSizeStrings[i];
		return fontSizeStrings[0];
	}
}
