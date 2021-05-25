import java.util.Iterator;

import kr.ac.konkuk.ccslab.cm.entity.CMSessionInfo;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMInterestEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

public class TPClientEventHandler implements CMAppEventHandler {
	
	private CMClientStub m_clientStub;
	private TPClient m_client;
	
	public TPClientEventHandler(CMClientStub stub, TPClient client) {
		m_clientStub = stub;
		m_client = client;
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
	//	case CMInfo.CM_INTEREST_EVENT:
	//		processInterestEvent(cme);
	//		break;
	//	case CMInfo.CM_DATA_EVENT:
	//		processDataEvent(cme);
	//		break;
	//	case CMInfo.CM_DUMMY_EVENT:
	//		processDummyEvent(cme);
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
	//	case CMInfo.CM_MQTT_EVENT:
	//		processMqttEvent(cme);
	//		break;
		default:
			return;
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
