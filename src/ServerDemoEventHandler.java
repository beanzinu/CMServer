import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
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
	private ServerDemo m_server;
	private CMServerStub m_serverStub;
	private CMInfo m_cmInfo;
	private CMDBManager m_cmdb ;
	public ServerDemoEventHandler(CMServerStub serverStub,ServerDemo server) {
		m_serverStub = serverStub ;
		m_server = server ;

	}
	// Event 수신 시 어떤 타입인지 확인
	@Override
	public void processEvent(CMEvent cme) {
		// TODO Auto-generated method stub
		switch(cme.getType()) 
		{
		case CMInfo.CM_SESSION_EVENT :
			LOGIN(cme);
			break;
		case CMInfo.CM_MQTT_EVENT:
			processMqttEvent(cme);
			break;
		case CMInfo.CM_DUMMY_EVENT: 
			m_server.MakePublish(cme);
		default :
			return ;
		}
	}
	// 로그인 및 세션 관리
	private void LOGIN(CMEvent cme)
	{
		CMConfigurationInfo confInfo = m_serverStub.getCMInfo().getConfigurationInfo();
		CMSessionEvent se = (CMSessionEvent) cme;
		String ID = se.getUserName();
		String pwd = se.getPassword();
		
		switch(se.getID())
		{
		// 로그인 관련
		case CMSessionEvent.LOGIN:
			printMessage("["+ID+"] requests login.");
			// UserInfo DB에 있을 시 ( 유저가 회원가입을 한 경우)
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
				}
			}
			break;
		case CMSessionEvent.LOGOUT:
			printMessage("["+ID+"] logs out.");
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
	

	
	private void printMessage(String strText) {
		m_server.printMessage(strText);
	}
	
	
}
