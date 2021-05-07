import java.util.Iterator;

import kr.ac.konkuk.ccslab.cm.entity.CMSessionInfo;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.event.mqttevent.CMMqttEvent;
import kr.ac.konkuk.ccslab.cm.event.mqttevent.CMMqttEventCONNACK;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

public class CMClientEventHandler implements CMAppEventHandler {

	private CMClientStub m_clientStub;
	String SessionResult = null ;
	public CMClientEventHandler(CMClientStub stub) {
		m_clientStub = stub;
	}
	
	// CM -> app Event 전달할 때 
	@Override
	public void processEvent(CMEvent cme) {
		// TODO Auto-generated method stub
		switch(cme.getType()) {
		case CMInfo.CM_SESSION_EVENT: // Event의 종류가 Session_event 인 경우
			processSessionEvent(cme);
			break;
		case CMInfo.CM_MQTT_EVENT:
			processMqttEvent(cme);
			break;
		default:
		}	
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
				processRESPONSE_SESSION_INFO(se);
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
	default : 
		break; 
	}
	return;
	}
	
	
	private void processRESPONSE_SESSION_INFO(CMSessionEvent se) {
		Iterator<CMSessionInfo> iter = se.getSessionInfoList().iterator();
		System.out.format("%-60s%n", "------------------------------------------------------------");
		System.out.format("%-20s%-20s%-10s%-10s%n", "name", "address", "port","user num");
		System.out.format("%-60s%n", "------------------------------------------------------------");
		while(iter.hasNext())
		{
		CMSessionInfo tInfo = iter.next();
		SessionResult = SessionResult+tInfo.getSessionName()+'\n';
		System.out.format("%-20s%-20s%-10d%-10d%n",tInfo.getSessionName(), tInfo.getAddress(), tInfo.getPort(), 
		tInfo.getUserNum());
		}

	}

}
