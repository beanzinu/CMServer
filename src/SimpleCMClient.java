import java.awt.Container;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import kr.ac.konkuk.ccslab.cm.entity.CMSessionInfo;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMMqttManager;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

public class SimpleCMClient extends JFrame {
	// Stub 객체 생성
	
	CMClientStub m_clientStub;
	// 원격지로부터 수신한 Event 전달 받기 위해서 
	//  ( current : NULL )
	CMClientEventHandler m_eventHandler;
	
	// MAIN MENU
//	private JPanel loginPanel = new JPanel(new GridLayout(3,2));
	private JLabel idLabel = new JLabel("아이디");
	private JLabel pwLabel = new JLabel("비밀번호");
	private JTextField idText = new JTextField();
	private TextField pwText = new TextField();
	
	private JButton loginBtn = new JButton("로그인");
	
//	private JPanel MainPanel = new JPanel(new GridLayout(3,2));
	public JTextArea SessionPanel = new JTextArea();
	private JScrollPane scrollPane = new JScrollPane(SessionPanel);
	
	
	
	// 생성자
	public SimpleCMClient(){
		setLocationRelativeTo(null); // 창 화면 중앙에
	
		Container contentPane = getContentPane();
		
		contentPane.setLayout(new GridLayout(3,2));
		contentPane.add(idLabel);
		contentPane.add(pwLabel);
		contentPane.add(idText);
		contentPane.add(pwText); 
		pwText.setEchoChar('*'); // * 로 표기
		contentPane.add(loginBtn);
		setSize(300,100);
		setVisible(true);
		setTitle("로그인 창");
		
		m_clientStub = new CMClientStub();
		m_eventHandler = new CMClientEventHandler(m_clientStub);
	}
	
	public static void main(String[] args) {
		SimpleCMClient client = new SimpleCMClient();
		client.m_clientStub.setAppEventHandler(client.m_eventHandler);
		// start CM '
		client.login(client.m_clientStub);
		client.m_clientStub.startCM();	
		
	}
	
	
	// login ( By Button )
	public void login(CMClientStub m_clientStub) {
		// loginBtn 눌렀을 때
		loginBtn.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		boolean RequestResult = false ; // 성공여부
		String id = idText.getText().trim();
		String pwd = pwText.getText().trim();
		RequestResult = m_clientStub.loginCM(id,pwd);
		
		// login success
		if(RequestResult) {
			JOptionPane.showMessageDialog(null,"로그인 성공!") ;
			setVisible(false);
			new MainMenu(); // 메인메뉴 화면으로 전환
			// session info 가져오기
			CMSessionEvent se = null; 
			se = m_clientStub.syncRequestSessionInfo();
			if(se==null) {
				SessionPanel.append("error,try again \n");
			}
			Iterator<CMSessionInfo> iter = se.getSessionInfoList().iterator();
			while(iter.hasNext()) {
				CMSessionInfo tInfo = iter.next();
				SessionPanel.append(tInfo.getSessionName()+'\n');
			}
			///////////////////////////////
			CMMqttManager mqttManager = 
					(CMMqttManager) m_clientStub.findServiceManager(CMInfo.CM_MQTT_MANAGER);
			mqttManager.connect();
			mqttManager.subscribe("g1", (byte)0);
			mqttManager.publish("g1","hi");
			///////////////////////////////////////////
		}
		else
			JOptionPane.showMessageDialog(null,"로그인 실패!") ;	
		}
		});
		
	}
	// MainMenu
	class MainMenu extends JFrame{
	
		JLabel Session = new JLabel("Session");
		MainMenu() {
			setLocationRelativeTo(null); 
			setTitle("배달공유시스템");
			setSize(300,300);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			JTextField join_session = new JTextField() ;
			JButton join_button = new JButton("join");
			JLabel label1 = new JLabel("Session List");
			JLabel label2 = new JLabel("INPUT");
			Container c = getContentPane();
			c.setLayout(new GridLayout(3,2));
			c.add(label1);
			c.add(label2);
			c.add(SessionPanel);
			c.add(join_session);
			c.add(join_button);
			join_button.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					boolean bRequestResult = false;
					String session = join_session.getText();
					bRequestResult = m_clientStub.joinSession(session);
					if(bRequestResult)
						JOptionPane.showMessageDialog(null,"Successfully joined "+session);
					else
						JOptionPane.showMessageDialog(null,"Session join failed");
				}
			});
			
			SessionPanel.append("Session Names : \n");
			c.add(scrollPane);
			setVisible(true);
		}
		
		
	}

//	// login ( USER GUIDE Version )
//	private void login_UG() {
//		String UserName = null ;
//		String Password = null ;
//		boolean bRequestResult = false ; // request 성공 여부
//		Console console = System.console();
//		
//		System.out.print("user name : ");
//		BufferedReader br =new BufferedReader(new InputStreamReader(System.in));
//			try {
//				UserName = br.readLine() ;
//				if(console == null) {
//					System.out.print("password: ");
//					Password = br.readLine();
//				}
//				else
//					Password = new String(console.readPassword("Password : ")) ;
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		bRequestResult = m_clientStub.loginCM(UserName, Password) ;
//		if(bRequestResult) 
//			System.out.println("login success");
//		else
//			System.err.println("failed");
//	}
//	
	
	// Requesting session login ( USER GUIDE Version )
//	private void Session_Request(CMClientStub stub) {
//		// assume that session info is given to user
//		String SessionName = null;
//		boolean bRequestResult = false;
//		System.out.println("session name: ");
//		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//		try {
//			SessionName = br.readLine();
//		} catch(IOException e) {
//			e.printStackTrace();
//		}
//		bRequestResult = stub.joinSession(SessionName);
//		if(bRequestResult) 
//			System.out.format("successfully joined %s",SessionName);
//		else
//			System.out.println("failed the session-join request!");
//	}
//	
//	
//	
}
