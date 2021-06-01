import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
import kr.ac.konkuk.ccslab.cm.entity.CMMqttSession;
import kr.ac.konkuk.ccslab.cm.entity.CMSession;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.info.CMConfigurationInfo;
import kr.ac.konkuk.ccslab.cm.info.CMDBInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.info.CMMqttInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMCommManager;
import kr.ac.konkuk.ccslab.cm.manager.CMDBManager;
import kr.ac.konkuk.ccslab.cm.manager.CMMqttManager;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

public class ServerDemo extends JFrame {

	private CMServerStub m_serverStub ;
	private ServerDemoEventHandler m_eventHandler ;
	private CMInfo m_cmInfo;
	public CMDBManager m_cmdb ;
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
				
				else if(InputString.equals("makeGroup") || InputString.equals("9")) {
					int a=makeGroup("Hwa-yang", "heo", "jeayook", "korean", "6000", "12000");//targetString = request client session name
					if(a != -1) printMessage("success makeGroup\n");
					else printMessage("makeGroup failed\n");
				}
				
				else if(InputString.equals("removeGroup") || InputString.equals("10")) {
					int a=removeGroup("Hwa-yang", 1);
					if(a != -1) printMessage("success removeGroup\n");
					else printMessage("removeGroup failed\n");
				}
				// test
				else if(InputString.equals("11")) {
					// send GROUP INFO 
					CMDummyEvent group_msg = new CMDummyEvent();
					String str = "GROUP##";
					group_msg.setDummyInfo(str+"1");
					m_serverStub.send(group_msg,"k");
				}
				else if(InputString.equals("12")) {
					// send GROUP INFO 
					CMDummyEvent group_msg = new CMDummyEvent();
					String str = "REJOIN";
					group_msg.setDummyInfo(str);
					m_serverStub.send(group_msg,"k");
				}
				
				else printMessage("---------WRONG COMMAND -------------");
					
				
				// Input 비우기
				ServerInput.setText("");
			}
		});
		
		
		// 서버에서 제공하는 서비스에 대해서 설명
		serviceList(); 
		// ---------------------TEST AREA ----------------------------
		
		m_cmdb.init(m_cmInfo);
		m_cmdb.connectDB(m_cmInfo);
		
//		m_cmdb.init(m_cmInfo);
//		m_cmdb.connectDB(m_cmInfo) ;
//		m_cmdb.queryInsertUser("j","1234",m_cmInfo) ;
		
		
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
		
		// 서버에서 제공하는 서비스에 대해서 설명
				printMessage("------------------------------");
				printMessage("1. exit : Terminate CM");
				printMessage("2. DBconf : check my DB conf");
				printMessage("3. clear : clear log area");
				printMessage("4. /p [sesssion/groupname] [msg] : publish");
				printMessage("5. ShowSession : show session information");
				printMessage("6. ShowGroup : show group information");
				printMessage("7. getUser : select user DB");
				printMessage("8. getGroup : select group DB");
				printMessage("9. makeGroup : make group in session & DB");
				printMessage("10. removeGroup : remove group in session & DB");
				
				printMessage("------------------------------");
			
		
	}
	
	public boolean MakePublish(CMEvent cme) {
		printMessage("recv success") ;
		CMDummyEvent e = (CMDummyEvent) cme ;
	
		String UserName = cme.getSender();
	
		String string = e.getDummyInfo();
		printMessage(string);
		StringTokenizer token = new StringTokenizer(string,"##");
		switch(token.nextToken()) {
			case "C1" :
			{
				String strTopic ,storeName, menu;
				String storeCat = null;
				int least_price =0;
				int	collected_amount = 0 ;
				
				//client information
				strTopic = token.nextToken();
				storeCat = token.nextToken();
				storeName = token.nextToken();
				menu = token.nextToken();
				// DB
				ResultSet Nresult = m_cmdb.sendSelectQuery("select * from store_table where store_name =  '"+storeName+"';", m_cmInfo) ;
				try {
					Nresult.next() ;
					least_price = Nresult.getInt(2);
//					storeCat = Nresult.getString(3);
					
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				ResultSet Mresult = m_cmdb.sendSelectQuery("select * from store_menu_table where store_name =  '"+storeName+"' and menu = '"+menu+"';", m_cmInfo) ;
				try {
					Mresult.next() ;
					collected_amount = Mresult.getInt(3);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				// Publish 
				// S1 ## group_id ## group_host ## store_name ## store_category ## collected amount ## least price
				
				// update DB				
				int group_id = makeGroup(strTopic,UserName,storeName,storeCat,Integer.toString(collected_amount),Integer.toString(least_price));
				if (group_id != -1) 
				{
					printMessage ( "DB UPDATE SUCCESS");
					String strQuery = "insert into group_menu_table (group_id,menu,member,price) values ('"+Integer.toString(group_id)+"'" +
						",'"+menu+"','"+UserName+"','"+collected_amount+"');";				
					int Result1 = m_cmdb.sendUpdateQuery(strQuery, m_cmInfo);
					if (Result1 > 0 ) printMessage("menuDB UPDATE SUCCESS");
		
//					// send event  [client rejoin] 
//					CMDummyEvent group_msg1 = new CMDummyEvent();
//					String str1 = "REJOIN##";
//					group_msg1.setDummyInfo(str1);
//					m_serverStub.send(group_msg1,UserName);
				
//					// send GROUP INFO
//					CMDummyEvent group_msg2 = new CMDummyEvent();
//					String str2 = "GROUP##";
//					group_msg2.setDummyInfo(str2+group_id);
//					m_serverStub.send(group_msg2,UserName);
					

					String pubMsg = "S1"+"##"+Integer.toString(group_id)+"##"+UserName + "##" + storeName + "##" + storeCat + "##" + Integer.toString(collected_amount) + "##" + Integer.toString(least_price) ;
					printMessage("pub Msg : "+pubMsg);
					boolean Result = MakePublish(strTopic,pubMsg);
					if (Result) 
						printMessage("PUB SUCCESS");
					
					
				}
				else 
				{
					printMessage("MakeGroup failed");
					
				}
				
				break;
			}
			case "C2" :
			{
				// C2 ## group_id ## 메뉴
				String sessionName = e.getHandlerSession();
				printMessage(sessionName);
				String group_id = token.nextToken();
				String menu = token.nextToken();
				int least_price = 0 ;
				String storeCat = null ;
				int amount = 0 ;
				int collected_amount = 0 ;
				String storeName = null ;
				String group_host = null ;
				
				// 가게 이름 구하기
				ResultSet result1 = m_cmdb.sendSelectQuery("select store_name from group_table where group_id =  '"+group_id+"';" ,m_cmInfo) ;
				try {
					result1.next() ;
					storeName = result1.getString(1);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				// 메뉴의 금액 구하기
				ResultSet result2 = m_cmdb.sendSelectQuery("select * from store_menu_table where store_name =  '"+storeName+"' and menu = '"+menu+"';", m_cmInfo) ;
				try {
					result2.next() ;
					amount = result2.getInt(3);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				// store_menu_table update
				String strQuery = "insert into group_menu_table (group_id,member,menu,price) values ('"+group_id+"'," +
						"'"+UserName+"','"+menu+"','"+amount+"');";		
				printMessage(" QUERY : "+strQuery);
				int Result1 = m_cmdb.sendUpdateQuery(strQuery, m_cmInfo);
				if (Result1 > 0 ) printMessage("C2 : store_menu_table update success");
				
				
				// store_table collected_amount update
				String strQuery2 = "update group_table set collected_amount = collected_amount+"+Integer.toString(amount)+" where group_id = '"+group_id+"';";
				int Result2 = m_cmdb.sendUpdateQuery(strQuery2, m_cmInfo);
				if (Result2 > 0 ) printMessage("C2 : group_table update success");
				
				// store_table get information
				ResultSet Nresult = m_cmdb.sendSelectQuery("select * from group_table where store_name =  '"+storeName+"';", m_cmInfo) ;
				try {
					Nresult.next() ;
					least_price = Nresult.getInt("least_price");
					storeCat = Nresult.getString("store_category");
					collected_amount =  Nresult.getInt("collected_amount");
					group_host = Nresult.getString("group_host");
					
					if ( collected_amount >= least_price ) 
					{
						
						MakeOrder(group_id) ;
						MakePublish("Hwa-yang","S2##"+group_id+"##"+UserName) ; // session
						//MakePublish(group_id,"S2##"+group_id) ; // group 
						
						int a =removeGroup("Hwa-yang",Integer.parseInt(group_id));
						if(a == 0) {
							printMessage("remove group is success" );
						}
						break;
					}
					
					
					
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			
				
				
				String pubMsg = "S1"+"##"+group_id+"##"+UserName + "##" + storeName + "##" + storeCat + "##" + Integer.toString(collected_amount) + "##" + Integer.toString(least_price) ;
				printMessage("pubMsg :"+pubMsg);
				// publish to session
				boolean bRequest = MakePublish("Hwa-yang",pubMsg);
				if (bRequest) 
					printMessage("C2(session) : PUB SUCCESS");
				boolean bRequest1 = MakePublish(group_id,pubMsg);
				if (bRequest1)
					printMessage("C2(group) : PUB SUCCESS");
				break;
			}
			
		}
		
	
		return true;
	}

	public boolean MakeOrder(String group_id) {
		
		int amount = 0;
		String member = "a";
		int i = 0;
		int j = 1;
		ResultSet result2 = m_cmdb.sendSelectQuery("select count(*) from group_menu_table where group_id =  '"+group_id+"';", m_cmInfo) ;
		try {
			
			result2.next();
			i = result2.getInt(1);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		while(i+1 != j) {
			String user = member;
			
			ResultSet result1 = m_cmdb.sendSelectQuery("select * from group_menu_table where group_id =  '"+group_id+"';", m_cmInfo) ;
			try {
				for(int k =0; k< j; k++)
					result1.next();
				member = result1.getString("member");
				amount = result1.getInt("price");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			printMessage(member + " "+amount);
			String strQuery1 = "update deposit_db set deposit = deposit-"+Integer.toString(amount)+" where userName = '"+member+"';";
			int Result1 = m_cmdb.sendUpdateQuery(strQuery1, m_cmInfo);
			if (Result1 > 0 ) printMessage("C2 : deposit_table update success");
		
			j++;
		}
		//MakePublish("Hwa-yang","S2##주문완료");
		
		
		
		
		
		
		return true;
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
	
	 public int makeGroup(String s_name, String group_host, String restaurant, String res_category, String collected_amount, String least_price) { // make group 
	   
	      CMInfo cmInfo=m_serverStub.getCMInfo();
	      CMInteractionInfo interInfo = m_serverStub.getCMInfo().getInteractionInfo();
	      Iterator<CMSession> iter = interInfo.getSessionList().iterator();
	      
	      CMSession session=iter.next(); int s_num;
	      if(s_name.equals(new String("Hwa-yang"))) s_num=0;
	      else if(s_name.equals(new String("Ja-yang"))) s_num=1;
	      else if(s_name.equals(new String("Un-yang"))) s_num=2;
	      else {
	         printMessage("session name wrong\n");
	         return -1;
	      }
	      
	      for(int i=0;i<s_num;i++) session=iter.next();
	      
	      //새로운 group의 이름
	      String strQuery = "select MAX(group_id) as max from group_table;"; //모든 test는 일단 Hwa-yang에서 하므로 hwa-yang 만 생각
	      CMDBManager.init(cmInfo);
	      CMDBManager.connectDB(cmInfo);
	      ResultSet rs = CMDBManager.sendSelectQuery(strQuery, m_cmInfo);
	      int groupNum=0;
	      try {
	        rs.next();
	         groupNum = rs.getInt(1)+1;
	         printMessage(Integer.toString(groupNum));
	      } catch (SQLException e) {
	         // TODO Auto-generated catch block
	         e.printStackTrace();
	      }
	      printMessage(Integer.toString(groupNum));
	      if(groupNum>9) return -1;
	      
	      //새로운 group이름
	      String gname = new String(Integer.toString(groupNum));
	      //새로운 group의 주소
	      StringBuffer gaddrformat= new StringBuffer("224.1.1."+Integer.toString(s_num+2));
	      gaddrformat.replace(6, 7, Integer.toString(groupNum+1));
	      String gaddr= new String(gaddrformat.toString());
	      System.out.println(gaddr);
	      
	      //새로운 group의 port
	      StringBuffer gportformat= new StringBuffer("700"+Integer.toString(s_num+1));
	      gportformat.replace(2, 3, Integer.toString(groupNum));
	      String s_gport= new String(gportformat.toString());
	      int gport= Integer.parseInt(s_gport);
	      System.out.println(gport);
	      
//	      //group 생성
//	      if(session.createGroup(gname, gaddr, gport) == null){
//	         return -1;
//	      }
//	      
//	      //conf 수정
//	      insertConf(groupNum+1,gname,gaddr,gport);
	      
	      //group DB에도 추가
	      int insert_check=InsertGroup(groupNum, group_host , restaurant, res_category, collected_amount, least_price);
	      if(insert_check == -1) {
	         printMessage("send insert query fail\n");
	         return -1;
	      }
	      else return groupNum;
	   }
	
	public void insertConf(int groupnum, String gname, String gaddr, int gport) {
		File fd;
		String gnum = Integer.toString(groupnum);
		String sgport= Integer.toString(gport);
		
		
		String strText="";
		int nBuffer;
		try {
			// 파일 읽기
			BufferedReader buffRead = new BufferedReader(new FileReader("./cm-session1.conf"));  
	        while ((nBuffer = buffRead.read()) != -1)  
	        {  
	            strText += (char)nBuffer;  
	        }  
	        printMessage(strText);
	        buffRead.close();  
			
	        
	        BufferedWriter buffWrite = new BufferedWriter(new FileWriter("./cm-session1.conf"));  
	        String Text = strText.replaceAll("GROUP_NUM 1","GROUP_NUM 2");  
	        // 파일 쓰기  
	        buffWrite.write(Text, 0, Text.length());  
	        // 파일 닫기  
	        buffWrite.flush();  
	        buffWrite.close();  

	        
	        
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		try {
			fd = new File("./cm-session1.conf");
			
			FileWriter fw = new FileWriter(fd, true);
			fw.write("\n");
			fw.write("GROUP_NAME"+gnum+"			"+gname+"\n");
			fw.write("GROUP_ADDR"+gnum+"			"+gaddr+"\n");
			fw.write("GROUP_PORT"+gnum+"			"+sgport+"\n");
			fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
	}
	
	//if client made group, we can insert a row in group_table(DB)
	public int InsertGroup(int group_id, String group_host, String restaurant, String res_category, String collected_amount, String least_price) {
		CMInfo cmInfo=m_serverStub.getCMInfo();

		String strQuery = "insert into group_table (group_id, group_host, store_name, store_category, collected_amount, least_price) values ('" 
				+Integer.toString(group_id)+"','"+group_host+"','"+restaurant+"','"+res_category+"','"+collected_amount+"','"+least_price+"');";
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
	
	public int removeGroup(String s_name, int group_id) {
		CMInteractionInfo interInfo = m_serverStub.getCMInfo().getInteractionInfo();
		Iterator<CMSession> iter = interInfo.getSessionList().iterator();
		
		CMSession session=iter.next(); int s_num;
		if(s_name.equals(new String("Hwa-yang"))) s_num=0;
		else if(s_name.equals(new String("Ja-yang"))) s_num=1;
		else if(s_name.equals(new String("Un-yang"))) s_num=2;
		else {
			printMessage("session name wrong\n");
			return -1;
		}
		
		for(int i=0;i<s_num;i++) session=iter.next();
		String gname = Integer.toString(group_id);
		//session
		session.removeGroup(gname);
		
		//conf
		removeConf(group_id);
		
		//DB
		int delete_check = deleteGroup(gname);
		if(delete_check == -1) {
			printMessage("send insert query fail\n");
			return -1;
		}
		
		return 0;
	}
	
	public void removeConf(int group_id) {
		File fd;
		String findgname="GROUP_NAME"+Integer.toString(group_id+1)+"			"+Integer.toString(group_id);
		printMessage(findgname);
		String findgaddr; 
		String findgport;
		try {
			fd = new File("./cm-session1.conf");
			FileWriter fw = new FileWriter(fd, true);//이어쓰기
			BufferedWriter bw = new BufferedWriter(fw);
			FileReader fr = new FileReader(fd);
			BufferedReader br = new BufferedReader(fr);
			String line="";
			while((line = br.readLine()) != null) {
	            String trimmedLine = line.trim();
	            if(trimmedLine.equals(findgname)) {
	            	line.replaceAll(line, "");
	                findgaddr = br.readLine().trim();
	                printMessage(findgaddr);
	                findgaddr.replaceAll(findgaddr, "");	                
	                findgport = br.readLine().trim();
	                printMessage(findgport);
	                findgport.replaceAll(findgport, "");	               
	                break;
	            	}
				}
			br.close();
			fr.close();
			bw.close();
			fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
	}
	
	public int deleteGroup(String gname) {
		CMInfo cmInfo=m_serverStub.getCMInfo();
		CMDBManager.init(cmInfo);
		boolean a= CMDBManager.connectDB(cmInfo);
		System.out.println("connect sucess: "+a);
		String QueryTogroup = "delete from group_table where group_id="+gname+";";
		String QueryTomenu = "delete from group_menu_table where group_id="+gname+";";
		
		int ret1 = CMDBManager.sendUpdateQuery(QueryTogroup, cmInfo);
		int ret2 = CMDBManager.sendUpdateQuery(QueryTomenu, cmInfo);
		
		
		
		if(ret1 == -1 || ret2 == -1)
		{
			System.out.println("CMDBManager.queryDeleteUser(), delete error!");
			return -1;
		}

		if(CMInfo._CM_DEBUG) {
			System.out.println("CMDBManager.queryDeleteUser(), return value("+ret1+").");
			System.out.println("CMDBManager.queryDeleteUser(), return value("+ret2+").");
		}
		return 0;
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
				printMessage(rs.getInt(1) + "\t" + rs.getString(2)+ "\t" + rs.getString(3) + "\t" + rs.getString(4) + "\t" + rs.getString(5)+ "\t" + rs.getString(6));
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
