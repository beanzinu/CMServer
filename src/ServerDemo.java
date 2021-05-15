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
/*
import kr.ac.konkuk.ccslab.cm.entity.CMMqttSession;
import kr.ac.konkuk.ccslab.cm.info.CMConfigurationInfo;
import kr.ac.konkuk.ccslab.cm.info.CMDBInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.info.CMMqttInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMCommManager;
import kr.ac.konkuk.ccslab.cm.manager.CMDBManager;
import kr.ac.konkuk.ccslab.cm.manager.CMMqttManager;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;
*/
import kr.ac.konkuk.ccslab.cm.entity.CMGroup;
import kr.ac.konkuk.ccslab.cm.entity.CMList;
import kr.ac.konkuk.ccslab.cm.entity.CMMember;
import kr.ac.konkuk.ccslab.cm.entity.CMMessage;
import kr.ac.konkuk.ccslab.cm.entity.CMMqttSession;
import kr.ac.konkuk.ccslab.cm.entity.CMRecvFileInfo;
import kr.ac.konkuk.ccslab.cm.entity.CMSendFileInfo;
import kr.ac.konkuk.ccslab.cm.entity.CMServer;
import kr.ac.konkuk.ccslab.cm.entity.CMSession;
import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMBlockingEventQueue;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.info.CMCommInfo;
import kr.ac.konkuk.ccslab.cm.info.CMConfigurationInfo;
import kr.ac.konkuk.ccslab.cm.info.CMDBInfo;
import kr.ac.konkuk.ccslab.cm.info.CMFileTransferInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.info.CMMqttInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMCommManager;
import kr.ac.konkuk.ccslab.cm.manager.CMConfigurator;
import kr.ac.konkuk.ccslab.cm.manager.CMDBManager;
import kr.ac.konkuk.ccslab.cm.manager.CMMqttManager;
import kr.ac.konkuk.ccslab.cm.sns.CMSNSUserAccessSimulator;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import javax.swing.JOptionPane;

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
	JButton ServerInputButton = new JButton("Ȯ��");
	//------------------------------------------------------------
	
	
	// ������
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
		else StartService() ; //  ���� ���� ���� �� ���� ����
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
				
				else if(InputString.equals("DBconf") || InputString.equals("2")) {
					DBinformation();
				}
				
				else if(InputString.equals("clear") || InputString.equals("3")) {
					LogArea.setText(null);
					serviceList();
				}
				
				else if(MenuString != null && MenuString.equals("/p")) {
					if(MakePublish(targetString,InputString))
						printMessage("successfully published msg to "+targetString + " " + InputString);
					else
						printMessage("Publish failed");
				}
				
				else if(InputString.equals("ShowSession") || InputString.equals("5")) {
					printSessionInfo();
				}
				
				else if(InputString.equals("ShowGroup") || InputString.equals("6")) {
					printGroupInfo();
				}
				
				else if(InputString.equals("getUser") || InputString.equals("7")) {
					printUserDB(queryGetUsers(0, -1, m_cmInfo));
				}
				
				else if(InputString.equals("getGroup") || InputString.equals("8")) {
					printGroupDB(queryGetGroup(0, -1, m_cmInfo));
				}
				
				else if(InputString.equals("insertPublish") || InputString.equals("9")) {
					makeGroup();
				}
				
				else printMessage("---------WRONG COMMAND -------------");
					
				
				// Input ����
				ServerInput.setText("");
			}
		});
		
		
		// �������� �����ϴ� ���񽺿� ���ؼ� ����
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
public void serviceList() {
		
		// �������� �����ϴ� ���񽺿� ���ؼ� ����
				printMessage("------------------------------");
				printMessage("1. exit : Terminate CM");
				printMessage("2. DBconf : check my DB conf");
				printMessage("3. clear : clear log area");
				printMessage("4. /p [sesssion/groupname] [msg] : publish");
				printMessage("5. ShowSession : show session information");
				printMessage("6. ShowGroup : show group information");
				printMessage("7. getUser : select user DB");
				printMessage("8. getGroup : select group DB");
				printMessage("9. insertPublish : insert publish DB");
				printMessage("------------------------------");
			
		
	}
	
	public boolean MakePublish(String strTopic,String strMsg) {
		// ���� -> Ŭ���̾�Ʈ �޽��� ����( Session or Group ) 
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
	
	public void DBinformation() { // DB access information
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
	
	public void printSessionInfo()
	{
		printMessage("----------------------------------------------------------------\n");
		printMessage(String.format("%-20s%-20s%-10s%-10s%n", "session name", "session addr", "port", "#users"));
		printMessage("----------------------------------------------------------------\n");
		
		CMInteractionInfo interInfo = m_serverStub.getCMInfo().getInteractionInfo();
		Iterator<CMSession> iter = interInfo.getSessionList().iterator();
		while(iter.hasNext())
		{
			CMSession session = iter.next();
			printMessage(String.format("%-20s%-20s%-10d%-10d%n", session.getSessionName(), session.getAddress()
					, session.getPort(), session.getSessionUsers().getMemberNum()));
		}
		return;
	}
	
	public void printGroupInfo()
	{
		String strSessionName = null;
		
		printMessage("====== print group information\n");
		strSessionName = JOptionPane.showInputDialog("Session Name");
		if(strSessionName == null)
		{
			return;
		}
		
		CMInteractionInfo interInfo = m_serverStub.getCMInfo().getInteractionInfo();
		CMSession session = interInfo.findSession(strSessionName);
		if(session == null)
		{
			printMessage("Session("+strSessionName+") not found.\n");
			return;
		}
		
		printMessage("------------------------------------------------------------------\n");
		printMessage(String.format("%-20s%-20s%-10s%-10s%n", "group name", "multicast addr", "port", "#users"));
		printMessage("------------------------------------------------------------------\n");

		Iterator<CMGroup> iter = session.getGroupList().iterator();
		while(iter.hasNext())
		{
			CMGroup gInfo = iter.next();
			printMessage(String.format("%-20s%-20s%-10d%-10d%n", gInfo.getGroupName(), gInfo.getGroupAddress()
					, gInfo.getGroupPort(), gInfo.getGroupUsers().getMemberNum()));
		}

		printMessage("======\n");
		return;
	}
	
	public void makeGroup() { // make group 
		//String sname= new String("session1"); //client msg �� �޾ƾ���(��û�� client�� ���� ���� session)
		//String saddr= new String("192.168.189.1");//client msg
		//int sport= 7777;//client msg
		//CMSession session =new CMSession("session1", "192.168.189.1", 7770);//client�� ���� session ��ü
		CMInteractionInfo interInfo = m_serverStub.getCMInfo().getInteractionInfo();
		Iterator<CMSession> iter = interInfo.getSessionList().iterator();
		CMSession session = iter.next();
		
		
		int groupNum=session.getGroupList().size();//���� group��
	
		
		//���ο� group�� �̸�, �ּ�, port ��ȣ  ���� (���� group���� ����)
		StringBuffer gnameformat= new StringBuffer("g1");
		gnameformat.replace(1, 2, Integer.toString(groupNum+1));
		String gname= new String(gnameformat.toString());
		System.out.println(gname);
		
		StringBuffer gaddrformat= new StringBuffer("224.1.1.2");
		gaddrformat.replace(6, 7, Integer.toString(groupNum+1));
		String gaddr= new String(gaddrformat.toString());
		System.out.println(gaddr);
		
		StringBuffer gportformat= new StringBuffer("7001");
		gportformat.replace(2, 3, Integer.toString(groupNum));
		String s_gport= new String(gportformat.toString());
		int gport= Integer.parseInt(s_gport);
		System.out.println(gport);
		
		//group ����
		session.createGroup(gname, gaddr, gport);
		InsertGroup(gname, "heo", "noodle", "korean", "5000", "10000");
		
	}
	
	//if client made group, we can insert a row in group_table(DB)
	public int InsertGroup(String group_id, String group_host, String restaurant, String res_category, String collected_amount, String least_price) {
		CMInfo cmInfo=m_serverStub.getCMInfo();
		String strQuery = "insert into group_table (group_id, group_host, restaurant, res_category, collected_amount, least_price) values ('" 
				+group_id+"','"+group_host+"','"+restaurant+"','"+res_category+"','"+collected_amount+"','"+least_price+"');";
		CMDBManager.init(cmInfo);
		boolean a= CMDBManager.connectDB(cmInfo);
		System.out.println("connect sucess: "+a);
		int ret = CMDBManager.sendUpdateQuery(strQuery, cmInfo);

		if(ret == -1)
		{
			System.out.println("makeOrderGroup(), error!");
			return ret;
		}

		if(CMInfo._CM_DEBUG)
			System.out.println("makeOrderGroup(), return value("+ret+").");

		return ret;
		
	}
	public static ResultSet queryGetUsers(int index, int num, CMInfo cmInfo)
	{
		String strQuery = null;
		ResultSet rs = null;
		if(index == 0 && num == -1)
			strQuery = "select * from user_table;";
		else
			strQuery = "select * from user_table limit "+index+", "+num+";";

		rs = CMDBManager.sendSelectQuery(strQuery, cmInfo);
		
		if(CMInfo._CM_DEBUG)
			System.out.println("CMDBManager.queryGetUsers(), end.");

		return rs;
	}
	public void printUserDB(ResultSet rs) {
		try {
			while(rs.next()) {
				printMessage(rs.getInt(1) + "\t" + rs.getString(2)+ "\t" + rs.getString(3));
			}
			printMessage("-----------------------------------------------------\n");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static ResultSet queryGetGroup(int index, int num, CMInfo cmInfo) //select group_table (jihun makes group_table in mysql)
	{
		String strQuery = null;
		ResultSet rs = null;
		if(index == 0 && num == -1)
			strQuery = "select * from group_table;";
		else
			strQuery = "select * from group_table limit "+index+", "+num+";";

		rs = CMDBManager.sendSelectQuery(strQuery, cmInfo);
		
		if(CMInfo._CM_DEBUG)
			System.out.println("CMDBManager.queryGetUsers(), end.");

		return rs;
	}
	public void printGroupDB(ResultSet rs) { //print about group_table in mysql
		try {
			while(rs.next()) {
				printMessage(rs.getString(1) + "\t" + rs.getString(2)+ "\t" + rs.getString(3) + "\t" + rs.getString(4) + "\t" + rs.getString(5)+ "\t" + rs.getString(6));
			}
			printMessage("-----------------------------------------------------\n");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	//---------------------------------------���� �α׿� ����Ʈ ----------------------------------
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
	
	///////////////not finish!!!!!!!!!!!!!////////////////////////
	public void removeGroup(String targetGroup) {
		CMInteractionInfo interInfo = m_serverStub.getCMInfo().getInteractionInfo();
		Iterator<CMSession> iter = interInfo.getSessionList().iterator();
		CMSession session = iter.next();
		
		
		
		session.removeGroup(targetGroup);
		
		
	}
	///////////////////////////////////////////////
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
