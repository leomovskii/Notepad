package ua.leomovskii.notepad;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.undo.UndoManager;

@SuppressWarnings("serial")
public class Application extends JFrame {

	enum CaseType {
		Upper, Lower, Inverted, Random
	}

	JMenuBar menuBar;
	JTextArea textArea;
	JLabel statusBar;

	SimpleDateFormat formatter = new SimpleDateFormat("hh:mm DD.MM.YYYY");

	UndoManager undoManager;

	JFileChooser fileDialog;
	File file;

	String fileName;
	boolean isSaved = true, isNewFile = true;

	Application() {
		setSize(600, 400);
		setMinimumSize(new Dimension(200, 200));
		setLocationRelativeTo(null);
		setVisible(true);
		setLayout(new BorderLayout());

		fileName = "Безымянный";
		file = new File(fileName);
		setTitle(fileName + " - Блокнот");

		fileDialog = new JFileChooser();
		fileDialog.setCurrentDirectory(fileDialog.getFileSystemView().getDefaultDirectory());

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});

		undoManager = new UndoManager();

		menuBar = new JMenuBar();
		add(menuBar, BorderLayout.NORTH);

		addFileMenu();
		addEditMenu();
		addOperationsMenu();
		addFormatMenu();

		addTextArea();
		addStatusBar();
	}

	private void addFileMenu() {
		JMenu menuFile = new JMenu("Файл");
		menuBar.add(menuFile);

		JMenuItem itemCreate = new JMenuItem("Создать");
		itemCreate.addActionListener(e -> onCreate());
		itemCreate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
		menuFile.add(itemCreate);

		JMenuItem itemOpen = new JMenuItem("Открыть");
		itemOpen.addActionListener(e -> onOpen());
		itemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		menuFile.add(itemOpen);

		JMenuItem itemSave = new JMenuItem("Сохранить");
		itemSave.addActionListener(e -> onSave());
		itemSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		menuFile.add(itemSave);

		JMenuItem itemSaveAs = new JMenuItem("Сохранить как...");
		itemSaveAs.addActionListener(e -> onSaveAs());
		menuFile.add(itemSaveAs);

		menuFile.addSeparator();

		JMenuItem itemExit = new JMenuItem("Выход");
		itemExit.addActionListener(e -> exit());
		menuFile.add(itemExit);
	}

	private void addEditMenu() {
		JMenu menuEdit = new JMenu("Правка");
		menuBar.add(menuEdit);

		JMenuItem itemUndo = new JMenuItem("Отменить");
		itemUndo.addActionListener(e -> {
			if (undoManager.canUndo())
				undoManager.undo();
		});
		itemUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
		menuEdit.add(itemUndo);

		JMenuItem itemRedo = new JMenuItem("Вернуть");
		itemRedo.addActionListener(e -> {
			if (undoManager.canRedo())
				undoManager.redo();
		});
		itemRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
		menuEdit.add(itemRedo);

		menuEdit.addSeparator();

		JMenuItem itemCut = new JMenuItem("Вырезать");
		itemCut.addActionListener(e -> textArea.cut());
		itemCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
		menuEdit.add(itemCut);

		JMenuItem itemCopy = new JMenuItem("Копировать");
		itemCopy.addActionListener(e -> textArea.copy());
		itemCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
		menuEdit.add(itemCopy);

		JMenuItem itemPaste = new JMenuItem("Вставить");
		itemPaste.addActionListener(e -> textArea.paste());
		itemPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
		menuEdit.add(itemPaste);

		JMenuItem itemDelete = new JMenuItem("Удалить");
		itemDelete.addActionListener(e -> textArea.replaceSelection(""));
		itemDelete.setAccelerator(KeyStroke.getKeyStroke("DELETE"));
		menuEdit.add(itemDelete);

		menuEdit.addSeparator();

		JMenuItem itemGoTo = new JMenuItem("Перейти...");
		itemGoTo.addActionListener(e -> new GoToDialog(this, textArea));
		itemGoTo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));
		menuEdit.add(itemGoTo);

		menuEdit.addSeparator();

		JMenuItem itemSelectAll = new JMenuItem("Выделить всё");
		itemSelectAll.addActionListener(e -> textArea.selectAll());
		itemSelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
		menuEdit.add(itemSelectAll);

		JMenuItem itemTimeStamp = new JMenuItem("Время и дата");
		itemTimeStamp.addActionListener(
				e -> textArea.insert(formatter.format(new java.util.Date()), textArea.getSelectionStart()));
		menuEdit.add(itemTimeStamp);
	}

	private void addFormatMenu() {
		JMenu menuFormat = new JMenu("Формат");
		menuBar.add(menuFormat);

		JCheckBoxMenuItem itemWordWrap = new JCheckBoxMenuItem("Перенос по словам");
		itemWordWrap.addActionListener(e -> textArea.setLineWrap(itemWordWrap.isSelected()));
		menuFormat.add(itemWordWrap);

		JMenuItem itemFont = new JMenuItem("Шрифт...");
		itemFont.addActionListener(e -> new FontDialog(this, textArea));
		menuFormat.add(itemFont);
	}

	private void addOperationsMenu() {
		JMenu menuOperations = new JMenu("Операции");
		menuBar.add(menuOperations);

		JMenuItem itemToLowerCase = new JMenuItem("Регистр: прописные");
		itemToLowerCase.addActionListener(e -> setCase(CaseType.Lower));
		menuOperations.add(itemToLowerCase);

		JMenuItem itemToUpperCase = new JMenuItem("Регистр: СТРОЧНЫЕ");
		itemToUpperCase.addActionListener(e -> setCase(CaseType.Upper));
		menuOperations.add(itemToUpperCase);

		JMenuItem itemReverseCase = new JMenuItem("Регистр: иНВЕРТИРОВАТЬ");
		itemReverseCase.addActionListener(e -> setCase(CaseType.Inverted));
		menuOperations.add(itemReverseCase);

		JMenuItem itemRandomCase = new JMenuItem("Регистр: сЛуЧАйнЫй");
		itemRandomCase.addActionListener(e -> setCase(CaseType.Random));
		menuOperations.add(itemRandomCase);

		menuOperations.addSeparator();

		JMenuItem itemSortAscending = new JMenuItem("Сортировка строк: по возрастанию");
		itemSortAscending.addActionListener(e -> sortLines(true));
		menuOperations.add(itemSortAscending);

		JMenuItem itemSortDescending = new JMenuItem("Сортировка строк: по убыванию");
		itemSortDescending.addActionListener(e -> sortLines(false));
		menuOperations.add(itemSortDescending);

		menuOperations.addSeparator();

		JMenuItem itemRemoveEmptyLines = new JMenuItem("Удалить пустые строки");
		itemRemoveEmptyLines
				.addActionListener(e -> textArea.setText(textArea.getText().replaceAll("(?m)^[ \t]*\r?\n", "")));
		menuOperations.add(itemRemoveEmptyLines);

	}

	private void addTextArea() {
		textArea = new JTextArea();
		textArea.getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));
		textArea.setFont(new Font("Serif", Font.PLAIN, 12));
		textArea.setTabSize(1);
		textArea.setMargin(new Insets(3, 3, 3, 3));
		add(new JScrollPane(textArea), BorderLayout.CENTER);

		textArea.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				isSaved = false;
			}

			public void removeUpdate(DocumentEvent e) {
				isSaved = false;
			}

			public void insertUpdate(DocumentEvent e) {
				isSaved = false;
			}
		});

		textArea.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				if (textArea.getText().length() == 0)
					UpdateStatusBar(0, 0);
				else
					try {
						int pos = textArea.getCaretPosition();
						int lin = textArea.getLineOfOffset(pos);
						int col = pos - textArea.getLineStartOffset(lin);
						UpdateStatusBar(lin, col);
					} catch (Exception ex) {
					}
			}
		});

		textArea.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_D && e.isControlDown()) {
					String text = textArea.getSelectedText();
					if (text.equals(null)) {
// textArea.insert(formatter.format(new java.util.Date()), textArea.getSelectionStart())

						// копировать строку где курсор на следующую
					} else {
						textArea.insert(formatter.format(new java.util.Date()), textArea.getSelectionStart());
					}
					System.out.println();
				}
			}
		});
	}

	private void addStatusBar() {
		statusBar = new JLabel();
		statusBar.setHorizontalAlignment(SwingConstants.RIGHT);
		add(statusBar, BorderLayout.SOUTH);
		UpdateStatusBar(0, 0);
	}

	private void UpdateStatusBar(int row, int col) {
		statusBar.setText(String.format("||     Стр %d, стлб %d  ", row + 1, col + 1));
	}

	private void onCreate() {
		if (!confirmSave())
			return;
		textArea.setText("");
		fileName = new String("Безымянный");
		file = new File(fileName);
		isSaved = true;
		isNewFile = true;
		setTitle(fileName + " - Блокнот");
	}

	private boolean confirmSave() {
		if (!isSaved) {
			int x = JOptionPane.showConfirmDialog(this, "Сохранить изменения в файле \"" + fileName + "\"?", "Блокнот",
					JOptionPane.YES_NO_CANCEL_OPTION);

			if (x == JOptionPane.YES_OPTION && !onSaveAs())
				return false;

			if (x == JOptionPane.CANCEL_OPTION || x == JOptionPane.CLOSED_OPTION)
				return false;
		}
		return true;
	}

	private boolean saveFile(File f) {
		try (FileWriter writer = new FileWriter(f)) {
			writer.write(textArea.getText());

			isSaved = true;
			fileName = new String(f.getName());
			if (!f.canWrite()) {
				fileName += " (только чтение)";
				isNewFile = true;
			}
			file = f;
			setTitle(fileName + " - Блокнот");
			isNewFile = false;

			return true;
		} catch (IOException e) {
			return false;
		}
	}

	private boolean onSave() {
		return isNewFile ? onSaveAs() : saveFile(file);
	}

	private boolean onSaveAs() {
		File f = null;
		fileDialog.setDialogTitle("Сохранить как...");
		fileDialog.setApproveButtonText("Сохранить");
		fileDialog.setApproveButtonMnemonic(KeyEvent.VK_S);

		do {
			if (fileDialog.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
				return false;
			f = fileDialog.getSelectedFile();
			if (!f.exists())
				break;
			if (JOptionPane.showConfirmDialog(this, "Файл \"" + f.getPath() + "\" существует. Заменить?",
					"Сохранить как", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				break;
		} while (true);

		return saveFile(f);
	}

	private boolean openFile(File f) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)))) {
			String line;

			while ((line = reader.readLine()) != null)
				textArea.append(line);

			isSaved = true;
			fileName = new String(f.getName());
			if (!f.canWrite()) {
				fileName += " (только чтение)";
				isNewFile = true;
			}
			file = f;
			setTitle(fileName + " - Блокнот");
			isNewFile = false;

			textArea.setCaretPosition(0);

			return true;
		} catch (IOException e) {
			return false;
		}
	}

	private void onOpen() {
		if (!confirmSave())
			return;

		fileDialog.setDialogTitle("Открыть");
		fileDialog.setApproveButtonText("Открыть");
		fileDialog.setApproveButtonMnemonic(KeyEvent.VK_O);

		File f = null;
		do {
			if (fileDialog.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
				return;
			f = fileDialog.getSelectedFile();
			if (f.exists())
				break;
			JOptionPane.showMessageDialog(this,
					"Файл \"" + f.getName() + "\" не найден. Проверьте правильность имени файла.", "Открыть",
					JOptionPane.INFORMATION_MESSAGE);

		} while (true);

		textArea.setText("");

		if (!openFile(f)) {
			fileName = "Безымянный";
			isSaved = true;
			setTitle(fileName + " - Блокнот");
		}
		if (!f.canWrite())
			isNewFile = true;
	}

	private void exit() {
		if (confirmSave())
			System.exit(0);
	}

	private void setCase(CaseType caseType) {
		String selectedString = textArea.getSelectedText();

		if (selectedString == null)
			return;

		if (caseType == CaseType.Upper) {
			textArea.replaceSelection(selectedString.toUpperCase());

		} else if (caseType == CaseType.Lower) {
			textArea.replaceSelection(selectedString.toLowerCase());

		} else {
			StringBuilder sb = new StringBuilder();

			for (String s : selectedString.split(""))
				if (caseType == CaseType.Inverted)
					sb.append(s.equals(s.toUpperCase()) ? s.toLowerCase() : s.toUpperCase());
				else
					sb.append(Math.random() * 99 < 50 ? s.toLowerCase() : s.toUpperCase());

			textArea.replaceSelection(sb.toString());
		}
	}

	private void sortLines(boolean isAlphabetically) {
		ArrayList<String> list = new ArrayList<>();
		list.addAll(Arrays.asList(textArea.getText().split("\n")));

		Collections.sort(list);
		if (!isAlphabetically)
			Collections.reverse(list);

		textArea.setText("");
		for (int i = 0; i < list.size(); i++) {
			textArea.append(list.get(i));
			if (i < list.size() - 1)
				textArea.append("\n");
		}
	}
}
