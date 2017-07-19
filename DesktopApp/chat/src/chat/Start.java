package chat;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Start extends JFrame {
	private static final long serialVersionUID = 1L;
	
	public Start() {
		super("Chat Program");
		setLayout(new FlowLayout());
		setEnabled(true);
		setSize(220,65);
		setLocationRelativeTo(null);
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton serverB = new JButton("Start Server");
		JButton clientB = new JButton("Start Client");
		
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
		if(args.length == 0) {
			try{UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}catch(Exception ex){ex.printStackTrace();}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					new Start().setVisible(true);
				}
			});
		}
		else {
			System.out.println("Starting in Headless mode");
			determineArgs(args);
		}
	}
			/** Parameter format:
			 *  java -jar chatva-d_v_x.x.x.jar <options>
			 * 	
			 * 	Options:
			 * 	-s			Server mode (open Server.java with other params)
			 * 	-c			Client mode (open Client.java with other params)
			 * 	-u=<usrnm>	Username to use (default is CLI-user-x)
			 * 	-a=<adrs>	Use specific IP address
			 * 	-p=<port>	Use specific port
			 */
	public static void determineArgs(String[] args) throws ArrayIndexOutOfBoundsException {
		int tempport = 1500;
		String tempip = "127.0.0.1";
		String tempuser = ("CLI-user-" + Integer.toString((int)(Math.random() * 100)));
		for(int i = 0; i < args.length; i++) {
			if(args[i].contains("a=")) {
				String[] temps = args[i].split("=");
				tempip = temps[1];
			}
			else if(args[i].contains("p=")) {
				String[] temps = args[i].split("=");
				tempport = Integer.parseInt(temps[1]);
			}
			else if(args[i].contains("u=")) {
				String[] temps = args[i].split("=");
				tempuser = temps[1];
			}
			if(args[i].contains("help")) {
				System.out.println("Usage: java -jar chatva-d_v_x.x.x.jar <-s/-c> [-a=address] [-p=port] [-u=username]");
				System.out.println("");
				System.out.println("-s		Start in Server mode");
				System.out.println("-c		Start in Client mode");
				System.out.println("-a=address	Specify address to use for Client mode (default=127.0.0.1)");
				System.out.println("-p=port		Specify port to use for either Server or Client mode (default=1500)");
				System.out.println("-u=username	Specify username to use in Client mode (default=CLI-user-xx)");
				
				System.exit(0);
			}
		}
		for(int i = 0; i < args.length; i++) {
			if(args[i].equals("-s")) {
				Server server = new Server(tempport);
				server.start();
			}
			else if(args[i].equals("-c")) {
				Client client = new Client(tempip, tempport, tempuser);
				client.start();
			}
		}
	}
}