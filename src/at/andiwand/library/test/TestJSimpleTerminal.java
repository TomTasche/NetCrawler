package at.andiwand.library.test;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import at.andiwand.library.graphics.JSimpleTerminal;
import at.andiwand.library.network.ssh.SSH2Client;


public class TestJSimpleTerminal {
	
	public static String requestLogin(String message) {
		return JOptionPane.showInputDialog(message, System.getProperty("user.name") + "@localhost");
	}
	
	public static String requestPassword(String message) {
		JPasswordField passwordField = new JPasswordField();
		int result = JOptionPane.showConfirmDialog(null, new Object[] {passwordField}, message, JOptionPane.OK_CANCEL_OPTION);
		
		if (result == JOptionPane.CANCEL_OPTION) return null;
		
		return new String(passwordField.getPassword());
	}
	
	
	public static void main(String[] args) throws Throwable {
		String login = requestLogin("Enter username@hostname");
		if (login == null) System.exit(0);
		String password = requestPassword("Your password");
		if (password == null) System.exit(0);
		
		SSH2Client sshClient = new SSH2Client(login, password);
		
		
		JSimpleTerminal terminal = new JSimpleTerminal(sshClient);
		terminal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		terminal.setVisible(true);
	}
	
}