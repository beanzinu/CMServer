import java.util.Iterator;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import kr.ac.konkuk.ccslab.cm.entity.CMSessionInfo;
import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMInterestEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.event.mqttevent.CMMqttEvent;
import kr.ac.konkuk.ccslab.cm.event.mqttevent.CMMqttEventPUBLISH;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMMqttManager;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

public class TPClientEventHandler implements CMAppEventHandler {
	
	private CMClientStub m_clientStub;
	private TPClient m_client;
	private CMMqttManager m_mqttManager ;
	private CMInfo m_cmInfo ;
	
	private JTextArea chatWindow;
	
	public TPClientEventHandler(CMClientStub stub, TPClient client) {
		m_clientStub = stub;
		m_client = client;
		m_cmInfo = m_clientStub.getCMInfo();
	
	}
	
	public TPClientEventHandler(CMClientStub stub) {
		m_clientStub = stub;
	}
	

	@Override
	public void processEvent(CMEvent cme) {
		// TODO Auto-generated method stub
		switch(cme.getType())
		{
		case CMInfo.CM_SESSION_EVENT:
			processSessionEvent(cme);
			break;
		case CMInfo.CM_INTEREST_EVENT:
			processInterestEvent(cme);
			break;
	//	case CMInfo.CM_DATA_EVENT:
	//		processDataEvent(cme);
	//		break;
		case CMInfo.CM_DUMMY_EVENT:
			processDummyEvent(cme);
			break;
	//		break;
	//	case CMInfo.CM_USER_EVENT:
	//		processUserEvent(cme);
	//		break;
	//	case CMInfo.CM_FILE_EVENT:
	//		processFileEvent(cme);
	//		break;
	//	case CMInfo.CM_SNS_EVENT:
	//		processSNSEvent(cme);
	//		break;
	//	case CMInfo.CM_MULTI_SERVER_EVENT:
	//		processMultiServerEvent(cme);
	//		break;
		case CMInfo.CM_MQTT_EVENT:
			processMqttEvent(cme);
			break;
		default:
			return;
		}
	}
	
	// �엫�떆濡� 留뚮뱾�뼱 �넃�쓬
	private void processDummyEvent(CMEvent cme) {
		
		CMDummyEvent due = (CMDummyEvent) cme;
		String msg = due.getDummyInfo();
		StringTokenizer token = new StringTokenizer(msg,"##");
		String topic , group_id ;
		topic = token.nextToken();
		if(topic.equals("REJOIN")) {
			String session = m_clientStub.getMyself().getCurrentSession();
			m_clientStub.leaveSession();
			m_clientStub.joinSession(session);
		}
		else if(topic.equals("GROUP")) {
			group_id = token.nextToken();
			m_clientStub.changeGroup(group_id);
		}	
		else if(topic.equals("REQ")) {
			// REQ -> DB �슂泥�寃곌낵 
			String Req_msg1 = token.nextToken();
			// store DB 寃곌낵
			if (Req_msg1.equals("store")) 
			{
				if(token.hasMoreTokens()==false)
					m_client.createWindow.StoreList.setText("");
				else 
				{
				String store_msg = token.nextToken();
				m_client.createWindow.CheckStoreDB(store_msg);
				}
			}
			// create group db result
			else if (Req_msg1.equals("menu"))
			{
				String menu_msg = token.nextToken();
				m_client.createWindow.CheckMenuDB(menu_msg);
			}
			else if (Req_msg1.equals("menu2"))
			{
				String menu_msg = token.nextToken();
				System.out.println("received");
				m_client.joinWindow.checkDB(menu_msg);
			}
			// main group db result
			else 
			{
			String Req_msg2 = token.nextToken();
			String Req_msg = Req_msg1 + "##" + Req_msg2;
			m_client.CheckGroupDB(Req_msg);
			}
		}
		else if(topic.equals("USER"))
		{
			String userInfo=token.nextToken();
			m_client.userInfo(userInfo);
		}
		
	}
	
	private void processMqttEvent(CMEvent cme) {
		
		switch(cme.getID()) {
		case CMMqttEvent.PUBLISH :
			
			CMInteractionInfo interInfo = m_clientStub.getCMInfo().getInteractionInfo();
			CMUser myself = interInfo.getMyself();
			String Firstgroup =myself.getCurrentGroup();
			
			CMMqttEventPUBLISH string = (CMMqttEventPUBLISH) cme ;
			String msg = string.getAppMessage() ;
			
			
			
			String topic , group_id, UserName ;
			StringTokenizer token = new StringTokenizer(msg,"##");
			topic = token.nextToken();
			group_id = token.nextToken();
			UserName = token.nextToken();
			if(Firstgroup.equals("g1"))
			{
				System.out.println(group_id + "諛붾�붽렇猷�");
				m_clientStub.changeGroup(group_id);
				String Mygroup =myself.getCurrentGroup();

				chatWindow.append("�쁽�옱 �굹�쓽 洹몃９ :" + Mygroup +" �꽦怨듭쟻�쑝濡� 李몄뿬�븯���뒿�땲�떎.\n");
				
				if(topic.equals("S2")) {
					chatWindow.append(group_id + "二쇰Ц �셿猷�");

					JOptionPane aa=new JOptionPane();
					aa.showMessageDialog(null,"二쇰Ц�씠 �셿猷뚮릺�뿀�뒿�땲�떎.");
				}
			}
			else {
				
				String Mygroup =myself.getCurrentGroup();
				String MyName = myself.getName();

				//chatWindow.append("�쁽�옱 �굹�쓽 洹몃９" + Mygroup);
				
				if(group_id.equals(Mygroup)) {
					if(topic.equals("S1")) {
						chatWindow.append(UserName + "�떂�씠 "+group_id + "踰덉㎏ 洹몃９�뿉 李몄뿬�븯���뒿�땲�떎.\n");

					}
					else if(topic.equals("S2")) {
						chatWindow.append(group_id + "踰덇렇猷� 二쇰Ц�셿猷�");
						JOptionPane aa=new JOptionPane();
						aa.showMessageDialog(null,"二쇰Ц�씠 �셿猷뚮릺�뿀�뒿�땲�떎.");
					}
				//chatWindow.append(string.getAppMessage()+"\n");
				}
			
			
			
//			m_mqttManager.subscribe(group_id,(byte) 0);
			break;
			
			}
		}
		
		
	}
	
	
	private void processSessionEvent(CMEvent cme)
	{
		CMSessionEvent se = (CMSessionEvent)cme;
		
		switch(se.getID())
		{
		case CMSessionEvent.RESPONSE_SESSION_INFO:
			String[] session_name = new String[3];
			 session_name=processRESPONSE_SESSION_INFO(se);
			m_client.changeSessionB(session_name);
			break;
		case CMSessionEvent.LOGIN_ACK:
			if(se.isValidUser() == 0 )
			{
				System.err.println("failed login");
				m_client.loginpage(se);
			}
			else if(se.isValidUser() == -1 )
			{
				System.err.println("alreay login");
				m_client.loginpage(se);
			}
			else
			{
				System.out.println("success");
				m_client.loginpage(se);
			}
		default:
			return;
		}	
	}
	
	private void processInterestEvent(CMEvent cme)
	{
	
		switch(cme.getID())
		{
		case CMInterestEvent.USER_TALK:
			CMInterestEvent ie = (CMInterestEvent) cme;
			//System.out.println("("+ie.getHandlerSession()+", "+ie.getHandlerGroup()+")");
			//printMessage("("+ie.getHandlerSession()+", "+ie.getHandlerGroup()+")\n");
			//System.out.println("<"+ie.getUserName()+">: "+ie.getTalk());
			//printMessage("<"+ie.getUserName()+">: "+ie.getTalk()+"\n");
			//m_client.CreateWindow.chatWindow.setText("");
			// �궡媛� 湲곗〈 洹몃９�뿉 �뱾�뼱媛붿쓣 �븣
//			m_client.joinWindow.chattingWindow.chatWindow.append("<"+ie.getUserName()+">: "+ie.getTalk()+"\n");
			// �깉濡쒖슫 洹몃９�쓣 留뚮뱾�뿀�쓣 �븣 ( �궡媛� 諛⑹옣) 
			chatWindow.append("<"+ie.getUserName()+">: "+ie.getTalk()+"\n");
			break;
		default:
			return;
		}
	}
	
		
		private String[] processRESPONSE_SESSION_INFO(CMSessionEvent se)
		{
			String[] session_name=new String[3];
			int i=0;
			Iterator<CMSessionInfo> iter = se.getSessionInfoList().iterator();
			
			System.out.format("%-60s%n", "------------------------------------------------------------");
			System.out.format("%-20s%-20s%-10s%-10s%n", "name", "address", "port", "user num");
			System.out.format("%-60s%n", "------------------------------------------------------------");

			while(iter.hasNext())
			{
				CMSessionInfo tInfo = iter.next();
				System.out.format("%-20s%-20s%-10d%-10d%n", tInfo.getSessionName(), tInfo.getAddress(), 
						tInfo.getPort(), tInfo.getUserNum());
				session_name[i]=tInfo.getSessionName();
				i++;
			}
			return session_name;
		}
		
		public void setWindow(JTextArea area) {
			chatWindow = area;
		}
		
		
}
