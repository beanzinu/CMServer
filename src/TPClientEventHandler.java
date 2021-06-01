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
	
	// 임시로 만들어 놓음
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
			// REQ -> DB 요청결과 
			String Req_msg1 = token.nextToken();
			// store DB 결과
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
		
		
		
		
	}
	
	private void processMqttEvent(CMEvent cme) {
		
		switch(cme.getID()) {
		case CMMqttEvent.PUBLISH :
			
			CMInteractionInfo interInfo = m_clientStub.getCMInfo().getInteractionInfo();
			CMUser myself = interInfo.getMyself();
			String Firstgroup =myself.getCurrentGroup();
			
			CMMqttEventPUBLISH string = (CMMqttEventPUBLISH) cme ;
			String msg = string.getAppMessage() ;
			
			// User entered existing group
			// update GROUP INFO UI if that is my group
			// (ex) USER##1 <- group member changed in group "1"
			StringTokenizer tok = new StringTokenizer(msg,"##");
			String CheckEnter = tok.nextToken();
			if(CheckEnter.equals("USER"))
			{
				// Get  Update Group Info
				String Group = tok.nextToken();
				// Get My Group Info
				String MyGroup = m_clientStub.getMyself().getCurrentGroup();

				// someone Joined my GROUP
				if (Group.equals(MyGroup))
					chatWindow.append("NEW USER\n");
				// someone LEFT ( User might be not MY GROUP )
				else if(Group.equals("all")&&!MyGroup.equals("g1"))
					chatWindow.append("Somebody Logged out\n");
				return;
			}
			
			
			String topic , group_id, UserName ;
			StringTokenizer token = new StringTokenizer(msg,"##");
			topic = token.nextToken();
			group_id = token.nextToken();
			UserName = token.nextToken();
			if(Firstgroup.equals("g1"))
			{
				System.out.println(group_id + "바뀔그룹");
				m_clientStub.changeGroup(group_id);
				String Mygroup =myself.getCurrentGroup();

				chatWindow.append("현재 나의 그룹 :" + Mygroup +" 성공적으로 참여하였습니다.\n");
				
				if(topic.equals("S2")) {
					chatWindow.append(group_id + "번 그룹 주문 완료");

					JOptionPane aa=new JOptionPane();
					aa.showMessageDialog(null,"주문이 완료되었습니다.");
				}
			}
			else {
				
				String Mygroup =myself.getCurrentGroup();
				String MyName = myself.getName();

				//chatWindow.append("현재 나의 그룹" + Mygroup);
				
				if(group_id.equals(Mygroup)) {
					if(topic.equals("S1")) {
						chatWindow.append(UserName + "님이 "+group_id + "번째 그룹에 참여하였습니다.\n");

					}
					else if(topic.equals("S2")) {
						chatWindow.append(group_id + "번그룹 주문완료");
						JOptionPane aa=new JOptionPane();
						aa.showMessageDialog(null,"주문이 완료되었습니다.");
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
			// 내가 기존 그룹에 들어갔을 때
//			m_client.joinWindow.chattingWindow.chatWindow.append("<"+ie.getUserName()+">: "+ie.getTalk()+"\n");
			// 새로운 그룹을 만들었을 때 ( 내가 방장) 
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
