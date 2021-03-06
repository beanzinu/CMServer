import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMInterestEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.event.mqttevent.CMMqttEvent;
import kr.ac.konkuk.ccslab.cm.event.mqttevent.CMMqttEventPUBLISH;
import kr.ac.konkuk.ccslab.cm.event.mqttevent.CMMqttEventPUBREC;
import kr.ac.konkuk.ccslab.cm.info.CMConfigurationInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMDBManager;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

public class ServerDemoEventHandler implements CMAppEventHandler {
	private static String userInfo = "Online Users: \n";
	private ServerDemo m_server;
	private CMServerStub m_serverStub;
	private CMInfo m_cmInfo;
	private CMDBManager m_cmdb ;
	public ServerDemoEventHandler(CMServerStub serverStub,ServerDemo server) {
		m_serverStub = serverStub ;
		m_server = server ;
	

	}
	//check event type
	@Override
	public void processEvent(CMEvent cme) {
		// TODO Auto-generated method stub
		switch(cme.getType()) 
		{
		case CMInfo.CM_SESSION_EVENT :
			LOGIN(cme);
			break;
		case CMInfo.CM_INTEREST_EVENT :
			processInterestEvent(cme);
			break;
		case CMInfo.CM_MQTT_EVENT:
			processMqttEvent(cme);
			break;
		case CMInfo.CM_DUMMY_EVENT: {
			CMDummyEvent de = (CMDummyEvent) cme ;
			String dummyMsg = de.getDummyInfo();
			StringTokenizer token = new StringTokenizer(dummyMsg,"##");
			String requestMsg = token.nextToken();
			String user = de.getSender();
			if (dummyMsg.equals("REQ_GROUP"))
			{
				SendGroupInfo(user);
			}
			// create Group store DB
			else if (requestMsg.equals("REQ_STORE"))
			{
				SendStoreInfo(user,token.nextToken());
			}
			// create Group Menu DB
			else if(requestMsg.equals("REQ_MENU"))
			{
				SendMenuInfo(user,token.nextToken());
			}
			// join Group Menu DB
			else if (requestMsg.equals("REQ_MENU2"))
			{
				SendMenuInfo2(user,token.nextToken());
			}
			else
			{
				m_server.MakePublish(cme);
				// send GROUP INFO [ test ]
//				CMDummyEvent group_msg2 = new CMDummyEvent();
//				String str2 = "GROUP##";
//				group_msg2.setDummyInfo(str2+"1");
//				m_serverStub.send(group_msg2,"k");
			}
			break;
		}
		default :
			return ;
		}
	}
	private void processInterestEvent(CMEvent cme)
	{
		CMInterestEvent ie = (CMInterestEvent) cme;
		switch(ie.getID())
		{
		case CMInterestEvent.USER_ENTER : 
		case CMInterestEvent.USER_LEAVE:
			String session = cme.getHandlerSession();
			String group = ie.getCurrentGroup();
			if ( !group.equals("g1")&& !group.equals("")) {
				m_server.MakePublish(session,"USER##"+group) ;
				m_server.printMessage("User info [group]:"+group+" [session]:"+session+" request from client");	
			}
			break;
		}
		
	}
	
	
	// manage login session
	private void LOGIN(CMEvent cme)
	{
		CMConfigurationInfo confInfo = m_serverStub.getCMInfo().getConfigurationInfo();
		CMSessionEvent se = (CMSessionEvent) cme;
		String ID = se.getUserName();
		String pwd = se.getPassword();
		String user = se.getSender();
		
		switch(se.getID())
		{
		case CMSessionEvent.LOGIN:
			printMessage("["+ID+"] requests login.");
			
			if(confInfo.isLoginScheme())
			{
				boolean ret = CMDBManager.authenticateUser(ID,pwd, 
						m_serverStub.getCMInfo());
				if(!ret)
				{
					printMessage("["+ID+"] authentication fails!");
					m_serverStub.replyEvent(cme, 0);
				}
				else
				{
					printMessage("["+ID+"] authentication succeeded.");
					m_serverStub.replyEvent(cme, 1);
					userInfo=userInfo+ID+"\n";
					SendUserInfo(user);
				}
			}
			break;

		case CMSessionEvent.LOGOUT:
			// publish to everyone in the session
			m_server.MakePublish("Hwa-yang","USER##all") ;
			printMessage("["+ID+"] logs out.");
			userInfo=userInfo.replace("\n"+ID+"\n","\n");
			SendUserInfo(user);
			break;
		case CMSessionEvent.REQUEST_SESSION_INFO:
			printMessage("["+ID+"] requests session information.");
			break;
		case CMSessionEvent.CHANGE_SESSION:
			printMessage("["+ID+"] changes to session("+se.getSessionName()+").");
			break;
		case CMSessionEvent.JOIN_SESSION: // 
			printMessage("["+ID+"] requests to join session("+se.getSessionName()+").");
			// client group_table select 
			break;
		case CMSessionEvent.LEAVE_SESSION:
			printMessage("["+ID+"] leaves a session("+se.getSessionName()+").");
			break;
//		case CMSessionEvent.ADD_NONBLOCK_SOCKET_CHANNEL:
//			printMessage("["+se.getChannelName()+"] request to add a nonblocking SocketChannel with key("
//			+se.getChannelNum()+").");
//			break;
		case CMSessionEvent.REGISTER_USER:
			int a = m_server.InsertDeposit(ID);
			printMessage("User registration requested by user["+ID+"].");
			break;
		case CMSessionEvent.DEREGISTER_USER:
			printMessage("User deregistration requested by user["+ID+"].");
			break;
		case CMSessionEvent.FIND_REGISTERED_USER:
			printMessage("User profile requested for user["+ID+"].\n");
			break;
		case CMSessionEvent.UNEXPECTED_SERVER_DISCONNECTION:
			m_server.printStyledMessage("Unexpected disconnection from ["
					+se.getChannelName()+"] with key["+se.getChannelNum()+"]!\n", "bold");
			break;
		case CMSessionEvent.INTENTIONALLY_DISCONNECT:
			m_server.printStyledMessage("Intentionally disconnected all channels from ["
					+se.getChannelName()+"]!\n", "bold");
			break;
		default:
			return;
		}
	}
	
	private void processMqttEvent(CMEvent cme) {
		switch(cme.getID()) {
		case CMMqttEvent.PUBREC:
			CMMqttEventPUBREC pubrecEvent = (CMMqttEventPUBREC)cme;
			printMessage(pubrecEvent.toString());
			break;
		case CMMqttEvent.PUBLISH :
			CMMqttEventPUBLISH string = (CMMqttEventPUBLISH) cme ;
			printMessage(string.toString());
			printMessage(string.getSender() +":"+ string.getAppMessage());
		}
		
	}
	
	private void SendUserInfo(String user) {
		String header="USER##";
		CMDummyEvent e = new CMDummyEvent();
		e.setDummyInfo(header+userInfo);
		if (m_serverStub.broadcast(e)) 
			printMessage("Broadcast login user catalog");
	}
	
	private void SendGroupInfo(String user) {
		// serverDemo cmdb
		m_cmdb = m_server.m_cmdb ;
		String strQuery = "select * from group_table"; 
		// get num of groups
		ResultSet num_group = m_cmdb.sendSelectQuery("select count('group_id') from group_table",m_serverStub.getCMInfo());
		int numGroup = 0 ;
		try {
			num_group.next();
			numGroup = num_group.getInt(1);
		}
		catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		ResultSet result = m_cmdb.sendSelectQuery(strQuery,m_serverStub.getCMInfo());
		String sendMessage = "REQ##" ;
		try {
			while( result.next() == true) 
			{
				int group_id = result.getInt("group_id");
				if(group_id==0) continue;
				String group_host = result.getString("group_host");
				String store_name = result.getString("store_name");
				int collected_amount = result.getInt("collected_amount");
				int least_price = result.getInt("least_price");
				sendMessage= sendMessage + "-----------------------------------------" + '\n'
						+ "[GROUP ID]  : " + Integer.toString(group_id) + '\n' +
						"-----------------------------------------" + '\n'
						+ "GROUP_HOST : " + group_host + '\n'
						+ "STORE_NAME : " + store_name + '\n'
						+ "COLLECTED_AMOUNT : " + Integer.toString(collected_amount) + '\n' 
						+ "LEAST PRICE : " + Integer.toString(least_price) + '\n'
						+ "-----------------------------------------" + '\n';
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(numGroup==1) return;
		else numGroup = numGroup -1 ;
		sendMessage = sendMessage + "##" + Integer.toString(numGroup);
		
		
		
		CMDummyEvent e = new CMDummyEvent();
		e.setDummyInfo(sendMessage);
		if (m_serverStub.send(e,user)) 
			printMessage("send [REQ] MSG : "+sendMessage);
	}
	private void SendStoreInfo(String user,String category) {
		// serverDemo cmdb
		m_cmdb = m_server.m_cmdb ;
		String strQuery = "select * from store_table where store_category='"+category+"'" ;
		
		ResultSet result = m_cmdb.sendSelectQuery(strQuery,m_serverStub.getCMInfo());
		String sendMessage = "REQ##store##";
		try {
			while( result.next() == true) 
			{
				String store_name = result.getString("store_name");
				int least_price = result.getInt("least_price");
				sendMessage = sendMessage + "---------------------" + '\n'
				+ "[STORE NAME] : " + store_name + '\n' 
				+ "   least price : " + Integer.toString(least_price) + '\n';
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		CMDummyEvent e = new CMDummyEvent();
		e.setDummyInfo(sendMessage);
		if (m_serverStub.send(e,user)) 
			printMessage("send [REQ##STORE] MSG : "+sendMessage);
	}
	
	private void SendMenuInfo(String user,String StoreName) {
		// serverDemo cmdb
		m_cmdb = m_server.m_cmdb ;
		String strQuery = "select * from store_menu_table where store_name ='"+StoreName+"'" ;
		
		ResultSet result = m_cmdb.sendSelectQuery(strQuery,m_serverStub.getCMInfo());
		String sendMessage = "REQ##menu##";
		sendMessage = sendMessage + "["+StoreName+" MENU LIST ]" + '\n' ;
		try {
			while( result.next() == true) 
			{
				String menu_name = result.getString("menu");
				int price = result.getInt("price");
				sendMessage = sendMessage +		
				"---------------------" + '\n'
				+ "[MENU NAME] : " + menu_name + '\n' 
				+ "[price] : " + Integer.toString(price) + '\n';
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		CMDummyEvent e = new CMDummyEvent();
		e.setDummyInfo(sendMessage);
		if (m_serverStub.send(e,user)) 
			printMessage("send [REQ##MENU] MSG : "+sendMessage);
	}
	private void SendMenuInfo2(String user,String group_id) {
		// serverDemo cmdb
				m_cmdb = m_server.m_cmdb ;
				String PrestrQuery = "select store_name from group_table where group_id='"+group_id+"'" ;
				ResultSet Preresult = m_cmdb.sendSelectQuery(PrestrQuery, m_serverStub.getCMInfo());
				String StoreName = null ;
				try {
					while( Preresult.next() == true) 
					{
						StoreName = Preresult.getString("store_name");
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
				
				String strQuery = "select * from store_menu_table where store_name='"+StoreName+"'";
				ResultSet result = m_cmdb.sendSelectQuery(strQuery,m_serverStub.getCMInfo());
				String sendMessage = "REQ##menu2##";
				sendMessage = sendMessage + "["+StoreName+" MENU LIST ]" + '\n' ;
				try {
					while( result.next() == true) 
					{
						String menu_name = result.getString("menu");
						int price = result.getInt("price");
						sendMessage = sendMessage +		
						"---------------------" + '\n'
						+ "[MENU NAME] : " + menu_name + '\n' 
						+ "[price] : " + Integer.toString(price) + '\n';
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				CMDummyEvent e = new CMDummyEvent();
				e.setDummyInfo(sendMessage);
				if (m_serverStub.send(e,user)) 
					printMessage("send [REQ##MENU2] MSG : "+sendMessage);
	}
	
	private void printMessage(String strText) {
		m_server.printMessage(strText);
	}
	
	
}
