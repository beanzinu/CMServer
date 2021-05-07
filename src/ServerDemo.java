import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMCommManager;
import kr.ac.konkuk.ccslab.cm.manager.CMMqttManager;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

public class ServerDemo extends JFrame {

	private CMServerStub m_serverStub ;
	private ServerDemoEventHandler m_eventHandler ;
	boolean m_run = true ;
	
	
	//----------------------- UI --------------------------------------
	JLabel label1 = new JLabel("Server Logs",JLabel.CENTER);
	JTextPane LogArea = new JTextPane();
	JPanel P1 = new JPanel(new FlowLayout());
	JTextField ServerInput = new JTextField(20);
	JButton ServerInputButton = new JButton("확인");
	//------------------------------------------------------------
	
	
	// 생성자
	public ServerDemo()
	{
		m_serverStub = new CMServerStub();
		m_eventHandler = new ServerDemoEventHandler(m_serverStub,this);
	}
	private ServerDemoEventHandler getServerEventHandler() {
		return m_eventHandler ;
	}
	public void startCM()
	{
		// get current server info from the server configuration file
		String strSavedServerAddress = null;
		String strCurServerAddress = null;
		int nSavedServerPort = -1;
		String strNewServerAddress = null;
		String strNewServerPort = null;
		int nNewServerPort = -1;
		
		strSavedServerAddress = m_serverStub.getServerAddress();
		strCurServerAddress = CMCommManager.getLocalIP();
		nSavedServerPort = m_serverStub.getServerPort();
		
//		// ask the user if he/she would like to change the server info
//		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//		System.out.println("========== start CM");
//		System.out.println("detected server address: "+strCurServerAddress);
//		System.out.println("saved server port: "+nSavedServerPort);
//		
//		try {
//			System.out.print("new server address (enter for detected value): ");
//			strNewServerAddress = br.readLine().trim();
//			if(strNewServerAddress.isEmpty()) strNewServerAddress = strCurServerAddress;
//
//			System.out.print("new server port (enter for saved value): ");
//			strNewServerPort = br.readLine().trim();
//			try {
//				if(strNewServerPort.isEmpty()) 
//					nNewServerPort = nSavedServerPort;
//				else
//					nNewServerPort = Integer.parseInt(strNewServerPort);				
//			} catch (NumberFormatException e) {
//				e.printStackTrace();
//				return;
//			}
//			
//			// update the server info if the user would like to do
//			if(!strNewServerAddress.equals(strSavedServerAddress))
//				m_serverStub.setServerAddress(strNewServerAddress);
//			if(nNewServerPort != nSavedServerPort)
//				m_serverStub.setServerPort(Integer.parseInt(strNewServerPort));
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		boolean bRet = m_serverStub.startCM();
		if(!bRet)
		{
			System.err.println("CM initialization error!");
			return;
		}
		else StartService() ; //  서버 오픈 성공 후 서비스 시작
	}
	public void StartService()  {
		// CM Start
		printMessage("Server Start!");
		
		LogArea.setEditable(false);  // false -> cannot fix logs
		Container contentPane = getContentPane() ;
		contentPane.setLayout(new BorderLayout());
		contentPane.add(label1,BorderLayout.NORTH);
		contentPane.add(LogArea,BorderLayout.CENTER);
		P1.add(ServerInput);
		P1.add(ServerInputButton);
		contentPane.add(P1,BorderLayout.SOUTH);	
		
		setSize(400,300);
		setVisible(true);
		setTitle("Server");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		
		ServerInputButton.addActionListener(new ActionListener() {
			// Button Clicked
			public void actionPerformed(ActionEvent e) {
				String InputString = ServerInput.getText(); 
				// Terminate CM 
				if (InputString.equals("exit")) {
					m_run = false ;
					m_serverStub.terminateCM();
					printMessage("Server Stopped!");
				}
				
				// Input 비우기
				ServerInput.setText("");
			}
		});
		
		// waiting for input
		// 서버에서 제공하는 서비스에 대해서 설명
		printMessage("------------------------------");
		printMessage("1. exit : Terminate CM");
		printMessage("------------------------------");
		CMMqttManager mqttManager = 
                (CMMqttManager) m_serverStub.findServiceManager(CMInfo.CM_MQTT_MANAGER);
		 mqttManager.connect();
         mqttManager.subscribe("g1", (byte)0);
         mqttManager.publish("g1","hi");
		while(m_run) {

			
			
		}
		
		
	}
	
	
	public void printMessage(String strText)
	{
		StyledDocument doc = LogArea.getStyledDocument();
		try {
			doc.insertString(doc.getLength(), strText+'\n', null);
			LogArea.setCaretPosition(LogArea.getDocument().getLength());

		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return;
	}
	public void printStyledMessage(String strText, String strStyleName)
	{
		StyledDocument doc = LogArea.getStyledDocument();
		try {
			doc.insertString(doc.getLength(), strText, doc.getStyle(strStyleName));
			LogArea.setCaretPosition(LogArea.getDocument().getLength());

		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ServerDemo server = new ServerDemo();
		CMServerStub cmStub = server.m_serverStub ;
		cmStub.setAppEventHandler(server.getServerEventHandler());
		server.startCM();
		
		System.out.println("Server application is terminated.");
	}

}
