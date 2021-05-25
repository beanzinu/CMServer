import java.util.Iterator;
import java.util.StringTokenizer;

import kr.ac.konkuk.ccslab.cm.entity.CMSessionInfo;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMInterestEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.event.mqttevent.CMMqttEvent;
import kr.ac.konkuk.ccslab.cm.event.mqttevent.CMMqttEventPUBLISH;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMMqttManager;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

public class TPClientEventHandler implements CMAppEventHandler {
	
	private CMClientStub m_clientStub;
	private TPClient m_client;
	private CMMqttManager m_mqttManager ;
	private CMInfo m_cmInfo ;
	
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
	
	
	private void processDummyEvent(CMEvent cme) {
		
		CMDummyEvent due = (CMDummyEvent) cme;
		String msg = due.getDummyInfo();
		StringTokenizer token = new StringTokenizer(msg,"##");
		String topic , group_id ;
		topic = token.nextToken();
		if(topic.equals("GROUP")) {
			group_id = token.nextToken();
			m_mqttManager = (CMMqttManager) m_clientStub.findServiceManager(CMInfo.CM_MQTT_MANAGER);
			m_mqttManager.connect();
			
			m_clientStub.changeGroup("1");
			
			
			System.out.println("----------------------------------------\n");
			System.out.println(due.getDummyInfo()+'\n');
			System.out.println("----------------------------------------\n");
		}	
	}
	
	private void processMqttEvent(CMEvent cme) {
		
		switch(cme.getID()) {
		case CMMqttEvent.PUBLISH :
			CMMqttEventPUBLISH string = (CMMqttEventPUBLISH) cme ;
			String msg = string.getAppMessage() ;
			String topic , group_id ;
			StringTokenizer token = new StringTokenizer(msg,"##");
			topic = token.nextToken();
			group_id = token.nextToken();
			
			
			m_mqttManager.subscribe(group_id,(byte) 0);
			System.out.println(string.getAppMessage());
			break;
			
			
		}
		
		
	}
	
	
	private void processSessionEvent(CMEvent cme)
	{
		CMSessionEvent se = (CMSessionEvent)cme;
		switch(se.getID())
		{
		case CMSessionEvent.RESPONSE_SESSION_INFO:
			processRESPONSE_SESSION_INFO(se);
			break;
		default:
			return;
		}	
	}
	
	private void processInterestEvent(CMEvent cme)
	{
		CMInterestEvent ie = (CMInterestEvent) cme;
		switch(ie.getID())
		{
		case CMInterestEvent.USER_TALK:
			//System.out.println("("+ie.getHandlerSession()+", "+ie.getHandlerGroup()+")");
			//printMessage("("+ie.getHandlerSession()+", "+ie.getHandlerGroup()+")\n");
			//System.out.println("<"+ie.getUserName()+">: "+ie.getTalk());
			//printMessage("<"+ie.getUserName()+">: "+ie.getTalk()+"\n");
			//m_client.CreateWindow.chatWindow.setText("");
			m_client.joinWindow.chattingWindow.chatWindow.setText("<"+ie.getUserName()+">: "+ie.getTalk()+"\n");
			break;
		default:
			return;
		}
	}
	
		
		private void processRESPONSE_SESSION_INFO(CMSessionEvent se)
		{
			Iterator<CMSessionInfo> iter = se.getSessionInfoList().iterator();

			System.out.format("%-60s%n", "------------------------------------------------------------");
			System.out.format("%-20s%-20s%-10s%-10s%n", "name", "address", "port", "user num");
			System.out.format("%-60s%n", "------------------------------------------------------------");

			while(iter.hasNext())
			{
				CMSessionInfo tInfo = iter.next();
				System.out.format("%-20s%-20s%-10d%-10d%n", tInfo.getSessionName(), tInfo.getAddress(), 
						tInfo.getPort(), tInfo.getUserNum());
			}
		}
		
	
}
