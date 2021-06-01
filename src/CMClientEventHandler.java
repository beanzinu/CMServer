import java.util.StringTokenizer;

import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMInterestEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.event.mqttevent.CMMqttEvent;
import kr.ac.konkuk.ccslab.cm.event.mqttevent.CMMqttEventCONNACK;
import kr.ac.konkuk.ccslab.cm.event.mqttevent.CMMqttEventPUBLISH;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMGroupManager;
import kr.ac.konkuk.ccslab.cm.manager.CMMqttManager;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

public class CMClientEventHandler implements CMAppEventHandler {

	private CMClientStub m_clientStub;
	private SimpleCMClient m_client ;
	private CMMqttManager m_mqttManager ;
	private CMInfo m_cmInfo ;
	private CMGroupManager m_groupManager;
	
	String SessionResult = null ;
	public CMClientEventHandler(CMClientStub stub,SimpleCMClient CM) {
		m_clientStub = stub;
		m_client = CM ;
		m_cmInfo = m_clientStub.getCMInfo();
		m_mqttManager = new CMMqttManager(m_cmInfo);
		m_groupManager.init(m_cmInfo);
	}
	
	// CM -> app Event 전달할 때 
	@Override
	public void processEvent(CMEvent cme) {
		// TODO Auto-generated method stub
		switch(cme.getType()) {
		case CMInfo.CM_SESSION_EVENT: // Event의 종류가 Session_event 인 경우
			processSessionEvent(cme);
			break;
		case CMInfo.CM_INTEREST_EVENT :
			CMInterestEvent ie = (CMInterestEvent) cme;
			m_client.printMessage(ie.getTalk());
		case CMInfo.CM_MQTT_EVENT:
			processMqttEvent(cme);
			break;
//		case CMInfo.CM_DUMMY_EVENT:
//			processDummyEvent(cme);
		default:
		}	
	}
	
	private void processDummyEvent(CMEvent cme) {
		CMDummyEvent due = (CMDummyEvent) cme;
		String msg = due.getDummyInfo();
		StringTokenizer token = new StringTokenizer(msg,"##");
		String topic , group_id ;
		topic = token.nextToken();
		group_id = token.nextToken();
		m_mqttManager.subscribe(group_id,(byte) 0);
		m_clientStub.changeGroup(group_id);
		
		
		System.out.println("----------------------------------------\n");
		System.out.println(due.getDummyInfo()+'\n');
		System.out.println("----------------------------------------\n");
		
	}
	
	
	private void processMemeberEvent(CMEvent cme) {
		
		
	}
	
	
	private void processSessionEvent(CMEvent cme)
	{
		CMSessionEvent se = (CMSessionEvent)cme;
		switch(se.getID())
			{
			case CMSessionEvent.LOGIN_ACK:
				if(se.isValidUser() == 0)
				{
				System.err.println("This client failsauthentication by the default server!");
				}
				else if(se.isValidUser() == -1)
				{
				System.err.println("This client is already in the login-user list!");
				}
				else
				{
				System.out.println("This client successfully logs in to the default server.");
				}
				break;
			case CMSessionEvent.RESPONSE_SESSION_INFO:
//				processRESPONSE_SESSION_INFO(se);
				break;
			default:
			    return;
			}
	}
	
	
	private void processMqttEvent(CMEvent cme)
	{
	switch(cme.getID())
	{
	case CMMqttEvent.CONNACK:
		CMMqttEventCONNACK conackEvent = (CMMqttEventCONNACK)cme;
		System.out.println("received "+conackEvent.toString());
		break;
	// SERVER - > CLIENT PUBLISH INFO
	case CMMqttEvent.PUBLISH:
		{
			CMMqttEventPUBLISH string = (CMMqttEventPUBLISH)cme;

			m_client.printMessage(m_groupManager.toString());
			m_clientStub.changeGroup("1");
			
//			m_mqttManager = (CMMqttManager) m_clientStub.findServiceManager(CMInfo.CM_MQTT_MANAGER);
//			m_mqttManager.connect();
//			m_clientStub.changeGroup("1") ;
			
		    m_client.printMessage(string.getAppMessage());
		
		    break;
		}
		
	default : 
		break; 
	}
	return;
	}
	
	


}
