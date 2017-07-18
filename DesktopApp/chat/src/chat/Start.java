package chatOther;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Start extends JFrame {
	/**
	 * @author joshuacm18
	 */
	private static final long serialVersionUID = 1L;
	
	
	
	private JButton serverB, clientB;
	
	public Start() {
		super("Chat Program");
		setLayout(new FlowLayout());
		setEnabled(true);
		setSize(220,65);
		setLocationRelativeTo(null);
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		serverB = new JButton("Start Server");
		clientB = new JButton("Start Client");
		
		add(serverB);
		add(clientB);
		
		serverB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unused")
				ServerGUI serverGUI = new ServerGUI(1500);
			}
		});
		
		clientB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unused")
				ClientGUI clientGUI = new ClientGUI("localhost", 1500);
			}
		});
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Start().setVisible(true);
			}
		});
	}
	
}
