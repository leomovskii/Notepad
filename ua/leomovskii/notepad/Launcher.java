package ua.leomovskii.notepad;

import javax.swing.SwingUtilities;

public class Launcher {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(Application::new);
	}
}
