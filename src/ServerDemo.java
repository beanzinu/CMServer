import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import kr.ac.konkuk.ccslab.cm.entity.CMMqttSession;
import kr.ac.konkuk.ccslab.cm.info.CMConfigurationInfo;
import kr.ac.konkuk.ccslab.cm.info.CMDBInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.info.CMMqttInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMCommManager;
import kr.ac.konkuk.ccslab.cm.manager.CMDBManager;
import kr.ac.konkuk.ccslab.cm.manager.CMMqttManager;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

public class ServerDemo extends JFrame {

	private CMServerStub m_serverStub ;
	private ServerDemoEventHandler m_eventHandler ;
	private CMInfo m_cmInfo;
	private CMDBManager m_cmdb ;
	boolean m_run = true ;
	
	
	//----------------------- UI --------------------------------------
	JLabel label1 = new JLabel("Server Logs",JLabel.CENTER);
	JTextPane LogArea = new JTextPane();
	JScrollPane scrollPane = new JScrollPane(LogArea);
	JPanel P1 = new JPanel(new FlowLayout());
	JTextField ServerInput = new JTextField(20);
	JButton ServerInputButton = new JButton("확인");
	//------------------------------------------------------------
	
	
	// 생성자
	public ServerDemo()
	{
		m_serverStub = new CMServerStub();
		m_eventHandler = new ServerDemoEventHandler(m_serverStub,this);
		m_cmInfo = m_serverStub.getCMInfo();
	}
	// return myEventHandler
	private ServerDemoEventHandler getServerEventHandler() {
		return m_eventHandler ;
	}
	
	public void startCM()
	{
		// get current server info from the server configuration file
		String strSavedServerAddress = null;
		String strCurServerAddress = null;
		int nSavedServerPort = -1;
		
		strSavedServerAddress = m_serverStub.getServerAddress();
		strCurServerAddress = CMCommManager.getLocalIP();
		nSavedServerPort = m_serverStub.getServerPort();
		
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
		
		setLocationRelativeTo(null);
		LogArea.setEditable(false);  // false -> cannot fix logs
		Container contentPane = getContentPane() ;
		contentPane.setLayout(new BorderLayout());
		contentPane.add(label1,BorderLayout.NORTH);
//		contentPane.add(LogArea,BorderLayout.CENTER);
		contentPane.add(scrollPane,BorderLayout.CENTER);
		P1.add(ServerInput);
		P1.add(ServerInputButton);
		contentPane.add(P1,BorderLayout.SOUTH);	
		
		setSize(700,500);
		setVisible(true);
		setTitle("Server");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		
		ServerInputButton.addActionListener(new ActionListener() {
			// Button Clicked
			public void actionPerformed(ActionEvent e) {
				String MenuString = null ;
				String targetString = null ;
				String InputString = ServerInput.getText(); 
				StringTokenizer Strings = new StringTokenizer(InputString);
				if(Strings.countTokens()==3) 
				{
					MenuString = Strings.nextToken();
					targetString = Strings.nextToken();
					InputString = Strings.nextToken();
				}
				
				// Terminate CM 
				if (InputString.equals("exit") || InputString.equals("1")) {
					m_run = false ;
					m_serverStub.terminateCM();
					printMessage("Server Stopped!");
				}
				else if(InputString.equals("DBconf") || InputString.equals("2"))
					DBinformation();
				else if(InputString.equals("clear") || InputString.equals("3")) {
					LogArea.setText(null);
					serviceList();
				}
				else if(MenuString != null && MenuString.equals("/p"))
				{
					if(MakePublish(targetString,InputString))
						printMessage("successfully published msg to "+targetString + " " + InputString);
					else
						printMessage("Publish failed");
				}
				
				else printMessage("---------WRONG COMMAND -------------");
					
				
				// Input 비우기
				ServerInput.setText("");
			}
		});
		
		
		// 서버에서 제공하는 서비스에 대해서 설명
		serviceList(); 
		// ---------------------TEST AREA ----------------------------
		
		
		
		
		// waiting for input
		while(m_run) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
//			// cast test
//			CMDummyEvent msg = new CMDummyEvent();
//			msg.setDummyInfo("hi");
//			m_serverStub.cast(msg,"Hwa-yang",null);
//			printMessage("cast fin");
			
		}
		
		
	}
	
	public boolean MakePublish(String strTopic,String strMsg) {
		// 서버 -> 클라이언트 메시지 전달( Session or Group ) 
		byte qos = (byte) 0 ;
		boolean bDupFlag = false ;
		boolean bRetainFlag = false ;
		
		CMMqttInfo mqttInfo = m_cmInfo.getMqttInfo();
		Hashtable<String,CMMqttSession> sessionHashtable = mqttInfo.getMqttSessionHashtable();
		Set<String> keys = sessionHashtable.keySet();
		for(String key : keys) {
			CMMqttSession session = sessionHashtable.get(key);
			if(session==null) {
				printMessage("ERROR :session of client is null!");
				continue;
			}
			// PUB  - SUB Test 
			CMMqttManager mqttManager = 
	                (CMMqttManager) m_serverStub.findServiceManager(CMInfo.CM_MQTT_MANAGER);
			mqttManager.publishFromServerToOneClient(strTopic, strMsg, qos, bDupFlag, bRetainFlag, key, session);
		}
		
		return true ;
	}
	
	public void DBinformation() {
	      CMInfo cmInfo=m_serverStub.getCMInfo();
	      CMDBInfo dbInfo = cmInfo.getDBInfo();
	      CMConfigurationInfo confInfo = cmInfo.getConfigurationInfo();
	      String url = dbInfo.getDBURL();
	      String user = confInfo.getDBUser();
	      String pass = confInfo.getDBPass();
	      printMessage( "url :"+ url );
	      printMessage( "user :"+ user );
	      printMessage( "pass :"+ pass );
	   }
	public void serviceList() {
		
		// 서버에서 제공하는 서비스에 대해서 설명
				printMessage("------------------------------");
				printMessage("1. exit : Terminate CM");
				printMessage("2. DBconf : check my DB conf");
				printMessage("3. clear : clear log area");
				printMessage("4. /p [sesssion/groupname] [msg] : publish");
				printMessage("------------------------------");
			
		
	}
	
	//---------------------------------------서버 로그에 프린트 ----------------------------------
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
	//-------------------------------------------------------------------------------------
	

	
	
	// main 
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ServerDemo server = new ServerDemo();
		CMServerStub cmStub = server.m_serverStub ;
		cmStub.setAppEventHandler(server.getServerEventHandler());
		server.startCM();
		
		System.out.println("Server application is terminated.");
	}

}
