import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMMqttManager;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;
import kr.ac.konkuk.ccslab.cm.entity.CMSessionInfo;


public class TPClient extends JFrame {
	CMClientStub m_clientStub;
	TPClientEventHandler m_eventHandler;
	CMMqttManager mqttManager ;
	CMDBManager m_cmdb ;
	
	// about swing
	private static final long serialVersionUID = 1L;
	
	
	// login panel
	private JPanel panelLogin;
	private JPanel panel_0;
	private JPanel panel_1;
	private JPanel panel_1_1;
	private JPanel panel_1_2;
	private JPanel panel_2;
	private JPanel panel_2_1;
	private JPanel panel_2_2;
	
	
	private JLabel loginTitle;
	private JLabel idLabel;
	private JLabel pwLabel;
	private JLabel loginResult;
	
	private JTextField id;
	private JPasswordField pw;
	
	private JButton loginButton;
	private JButton registerButton;
	//
	
	//logout Panel
	private JPanel panelLogout;
	
	private JLabel userIdLabel;
	
	private JButton logoutButton;
	//
	
	// joinSessnion panel
	private JPanel panelJoin;
	private JButton location1;
	private JButton location2;
	private JButton location3;
	
	private String UserSessionInfo;
	//
	
	// joinGroup panel
	private JPanel panelGroup;
	private JPanel panelInfo;
	private JPanel panelNow;
	private JPanel panelNowGroups;	
	private JPanel panelGroupButton;
	
	private JLabel sessionInfo;
	private JLabel groupNumInfo;
	private JTextArea groupInfo;
	
	private JButton backBtn;
	private JButton joinGroupBtn;
	private JButton createGroupBtn;
	
	//
	
	
	private String strUserName = null;
	private String strPassword = null;
	
	joinGroupWindow joinWindow;
	CreateGroupWindow createWindow;
	
	//++++++++++++++++++++++++++++++++++++++++++ constructor +++++++++++++++++++++++++++++++++++++++++//
		
	
	public TPClient()
	{
		m_clientStub = new CMClientStub();
		m_eventHandler = new TPClientEventHandler(m_clientStub, this);
		MyActionListener cmActionListener = new MyActionListener();
		
		// // about swing
		setTitle("Share delivery service");
		setSize(500,700);
		setResizable(false); // frame size fixed
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Container c = getContentPane();
		setLayout(null);
		
		//------------------------------------ login panel ----------------------------------//
		int panelX = 300;
		int panelY = 300;
		
		panelLogin = new JPanel();
		//panelLogin.setBackground(Color.gray);
		panelLogin.setLayout(new GridLayout(2,0));
		panelLogin.setBounds(250-(panelX/2), 270-(panelY/2), panelX, panelY);
		
		panel_0 = new JPanel();
		panel_0.setLayout(new BorderLayout());
		
		panel_1 = new JPanel();
		//panel_1.setBackground(Color.blue);
		panel_1.setLayout(new GridLayout(3,0));
		
		panel_1_1 = new JPanel();
		//panel_1_1.setBackground(Color.LIGHT_GRAY);
		panel_1_2 = new JPanel();
		//panel_1_2.setBackground(Color.cyan);
		
		panel_2 = new JPanel();
		//panel_2.setBackground(Color.red);
		panel_1.setLayout(new GridLayout(2,0));
		
		panel_2_1 = new JPanel();
		panel_2_2 = new JPanel();
		 
		idLabel = new JLabel("ID : ");
		pwLabel = new JLabel("PW : ");
		loginResult = new JLabel("");
		loginTitle = new JLabel("Share Delivery Service");
		loginTitle.setHorizontalAlignment(JLabel.CENTER);
		loginTitle.setFont(loginTitle.getFont().deriveFont(22.0f));
	
		id = new JTextField();
		id.setPreferredSize(new Dimension(150,30));
		pw = new JPasswordField();
		pw.setPreferredSize(new Dimension(150,30));
		
		loginButton = new JButton("login");
		registerButton = new JButton("register");
		
		loginButton.addActionListener(cmActionListener);
		registerButton.addActionListener(cmActionListener);
	
		
		// adding 
		//label add
		panel_1_1.add(idLabel);
		panel_1_2.add(pwLabel);
		
		//text-box add
		panel_1_1.add(id);
		panel_1_2.add(pw);
		
		// button add + login result label(in panel_2)
		panel_2_1.add(loginButton);
		panel_2_1.add(registerButton);
		panel_2_2.add(loginResult);
		
		// panel add
		panel_1.add(panel_1_1);
		panel_1.add(panel_1_2);
		
		panel_2.add(panel_2_1);
		panel_2.add(panel_2_2);
		
		panel_0.add(panel_1,BorderLayout.NORTH);
		panel_0.add(panel_2,BorderLayout.CENTER);
		
		panelLogin.add(loginTitle);
		panelLogin.add(panel_0);
		
		c.add(panelLogin);
		
		panelLogin.setVisible(true);
		//
		
		
		//------------------------------------ logout panel ----------------------------------//
		panelLogout = new JPanel();
		panelLogout.setLayout(new GridLayout(2,0));
		
		userIdLabel = new JLabel();
		userIdLabel.setText("");
		
		logoutButton = new JButton("Logout");
		logoutButton.addActionListener(cmActionListener);
		
		panelLogout.setLayout(new FlowLayout());
		panelLogout.setBounds(300, 5, 200, 30);
		
		panelLogout.add(userIdLabel);
		panelLogout.add(logoutButton);
		c.add(panelLogout);
		
		panelLogout.setVisible(false);
		// //
		
		
		//------------------------------------ joinSession panel ----------------------------------//
		int panelJoinX = 400;
		int panelJoinY = 400;
		
		panelJoin = new JPanel();
		//panelJoin.setBackground(Color.gray);
		panelJoin.setLayout(new GridLayout(1,3));
		panelJoin.setBounds(250-(panelJoinX/2), 350-(panelJoinY/2), panelJoinX, panelJoinY);
		
		location1 = new JButton("location1");
		location2 = new JButton("location2");
		location3 = new JButton("location3");
		
		location1.addActionListener(cmActionListener);
		location2.addActionListener(cmActionListener);
		location3.addActionListener(cmActionListener);
	
		// add panel	
		panelJoin.add(location1);
		panelJoin.add(location2);
		panelJoin.add(location3);
		
		c.add(panelJoin);
		
		panelJoin.setVisible(false);
		//
		
		//------------------------------------ joinGroup panel ----------------------------------//
		
		int panelGroupX = 500;
		int panelGroupY = 665;
		
		int panelNowGroupX = 400;
		int panelNowGroupY = 400;
		
		panelGroup = new JPanel();
		//panelJoin.setBackground(Color.gray);
		panelGroup.setLayout(new BorderLayout());
		panelGroup.setBounds(0, 50, panelGroupX, panelGroupY);
		
		panelInfo = new JPanel(); 
		panelInfo.setBackground(Color.gray);
		
		panelNow = new JPanel();
		panelNow.setLayout(null);
		panelNow.setBackground(Color.darkGray);
		
		panelNowGroups = new JPanel(new BorderLayout());
		panelNowGroups.setBounds((panelGroupX-panelNowGroupX)/2,((panelGroupY-panelNowGroupY)/2 - 100) , panelNowGroupX, panelNowGroupY);
		panelNowGroups.setBackground(Color.LIGHT_GRAY);
		
		panelGroupButton = new JPanel(new GridLayout(0,2));
		panelGroupButton.setBounds((panelGroupX-panelNowGroupX)/2, ((panelGroupY-panelNowGroupY)/2 - 100) + panelNowGroupY + 40, panelNowGroupX, 40);
		panelGroupButton.setBackground(Color.darkGray);
		
		sessionInfo = new JLabel("");
		sessionInfo.setFont(sessionInfo.getFont().deriveFont(16.0f));
		groupNumInfo = new JLabel("Store Number : 0");
		groupNumInfo.setFont(groupNumInfo.getFont().deriveFont(14.0f));
		groupNumInfo.setHorizontalAlignment(JLabel.CENTER);
		groupNumInfo.setOpaque(true);
		groupNumInfo.setBackground(Color.GRAY);
		
//		groupInfo = new JTextArea(" [ Store 1 ]  store :  BBQ  || now join user : 2\n [ Store 2 ]  store :  hongkong noodles  || now join user : 1"); // save group info & print to user 
		groupInfo = new JTextArea();
		groupInfo.setEditable(false); 
		groupInfo.setBackground(Color.gray);
		
		backBtn = new JButton("Back");
		backBtn.setBounds(10, 10, 70, 30);
		backBtn.addActionListener(cmActionListener);
		
		joinGroupBtn = new JButton("Join Group!");
		joinGroupBtn.addActionListener(cmActionListener);
		//joinGroupBtn.setBackground(Color.gray);
		createGroupBtn = new JButton("Create new Group!");
		createGroupBtn.addActionListener(cmActionListener);
		//createGroupBtn.setBackground(Color.gray);
		
		panelNowGroups.add(groupNumInfo,BorderLayout.NORTH);
		panelNowGroups.add(new JScrollPane(groupInfo),BorderLayout.CENTER);
		
		panelGroupButton.add(joinGroupBtn);
		panelGroupButton.add(createGroupBtn);
		
		panelInfo.add(sessionInfo);		
		panelNow.add(panelNowGroups);
		panelNow.add(panelGroupButton);
		
		panelGroup.add(panelInfo,BorderLayout.NORTH);
		panelGroup.add(panelNow,BorderLayout.CENTER);
		
		//c.add(panelScroll);
		c.add(backBtn);
		c.add(panelGroup);
		
		//panelScroll.setVisible(false);
		panelGroup.setVisible(false);
		backBtn.setVisible(false);
		//
		
		
		setVisible(true);
	}
	
	
	//++++++++++++++++++++++++++++++++++++++++++ functions ++++++++++++++++++++++++++++++++++++++++++//
	
	public void LoginUser() { // LoginUser
		boolean bRequestResult = false;
		
		System.out.println("====== login to default server");
		
		//
		//Object[] message = { "User Name:", id, "Password:", pw };
		
		strUserName = id.getText();
		strPassword = new String(pw.getPassword()); // security problem?
		pw.setText("");
		
		System.out.println(strUserName);
		
		
		if(strUserName.equals("")) {
			loginResult.setText("Please enter your ID");
		}else if(strPassword.equals("")){
			loginResult.setText("Please enter your PassWord ");
		}
		else 
		{
			m_clientStub.loginCM(strUserName, strPassword);
		}
	}
	
	public void loginpage(CMSessionEvent loginAckEvent) {
		if(loginAckEvent != null)
		{
			// print login result
			if(loginAckEvent.isValidUser() == 0)
			{
				loginResult.setText("Fail Login ");
			}
			else if(loginAckEvent.isValidUser() == -1)
			{
				loginResult.setText("Already Login User ");
			}
			else // success login
			{
				SessionInfo();
				panelJoin.setVisible(true);
				panelLogout.setVisible(true);
				panelLogin.setVisible(false);
				
				
				userIdLabel.setText("|| "+ strUserName +" ||");
			}			
		}
	}

	
	public void LogoutUser() // User Logout
	{
		boolean bRequestResult = false;
		bRequestResult = m_clientStub.logoutCM();
		if(bRequestResult) {
			System.out.println("successfully sent the logout request.");
			
			// after logout return to login panel 
			panelLogin.setVisible(true);
			panelJoin.setVisible(false);
			panelLogout.setVisible(false);
			panelGroup.setVisible(false);
			backBtn.setVisible(false);
			loginResult.setText("Success Logout ");
		}
		else
			System.err.println("failed the logout request!");
		
		strUserName.substring(0, strUserName.length()-1);		
	}
	
	
	public void RegisterUser() { // user register
		new SigninWindow(m_clientStub);
	}
	
	public void JoinSession1(String session_name) // join session1
	{
		boolean bRequestResult = false;
		
		UserSessionInfo = session_name;
		
		bRequestResult = m_clientStub.joinSession(UserSessionInfo);
		mqttManager = (CMMqttManager) m_clientStub.findServiceManager(CMInfo.CM_MQTT_MANAGER);
		mqttManager.connect();
		mqttManager.subscribe(UserSessionInfo,(byte) 0) ;
		
		// [test] 
		CheckGroupDB();
		
		
		if(bRequestResult) {
			System.out.println("successfully sent the session-join request.");
			sessionInfo.setText("Session : "+session_name);
			panelGroup.setVisible(true);
			backBtn.setVisible(true);
			panelJoin.setVisible(false);
			
		}
		else
			System.err.println("failed the session-join request!");
		System.out.println("======");
	}
	public void JoinSession2(String session_name) // join session2
	{			
		boolean bRequestResult = false;
		
		UserSessionInfo = session_name;
	
		bRequestResult = m_clientStub.joinSession(UserSessionInfo);
		mqttManager = (CMMqttManager) m_clientStub.findServiceManager(CMInfo.CM_MQTT_MANAGER);
		mqttManager.connect();
		mqttManager.subscribe(UserSessionInfo,(byte) 0) ;
		
		if(bRequestResult){
			System.out.println("successfully sent the session-join request.");
			sessionInfo.setText("Session : "+session_name);
			panelGroup.setVisible(true);
			backBtn.setVisible(true);
			panelJoin.setVisible(false);
		}
		else
			System.err.println("failed the session-join request!");
		System.out.println("======");
	}
	public void JoinSession3(String session_name) // join session3
	{
		boolean bRequestResult = false;
		
		UserSessionInfo = session_name;
	
		bRequestResult = m_clientStub.joinSession(UserSessionInfo);
		mqttManager = (CMMqttManager) m_clientStub.findServiceManager(CMInfo.CM_MQTT_MANAGER);
		mqttManager.connect();
		mqttManager.subscribe(UserSessionInfo,(byte) 0) ;
		
		if(bRequestResult){
			System.out.println("successfully sent the session-join request.");
			sessionInfo.setText("Session : "+session_name);
			panelGroup.setVisible(true);
			backBtn.setVisible(true);
			panelJoin.setVisible(false);
		}
		else
			System.err.println("failed the session-join request!");
		System.out.println("======");
	}
	
	public void SessionInfo() // check available session from server
	{
		boolean bRequestResult = false;
		System.out.println("====== request session info from default server");
		bRequestResult = m_clientStub.requestSessionInfo();
		if(bRequestResult)
			System.out.println("successfully sent the session-info request.");
		else
			System.err.println("failed the session-info request!");
		System.out.println("======");
	}
	
	public void changeSessionB(String[] session_name) {
		for(int j=0;j<3;j++) System.out.println(session_name[j]);
		location1.setText(session_name[0]);
		location2.setText(session_name[1]);
		location3.setText(session_name[2]);
	}
	
	public void CheckGroupDB() 
	{		 // check current group DB 
				// server do 1
			
		//send "REQ_GROUP" to SERVER
		CMDummyEvent e = new CMDummyEvent();
		e.setDummyInfo("REQ_GROUP");
		
		CMInteractionInfo interInfo = m_clientStub.getCMInfo().getInteractionInfo();
		String strDefServer = interInfo.getDefaultServerInfo().getServerName();
		m_clientStub.send(e,strDefServer);
		
		
	}
	public void CheckGroupDB(String msg) {
		StringTokenizer token = new StringTokenizer(msg,"##");
		String GroupInfo = token.nextToken();
		String Groupnum = token.nextToken();
		
		groupNumInfo.setText("Current Group Num : "+ Groupnum ) ;
		
		groupInfo.setText("");
		groupInfo.append(GroupInfo);
		
	}
	
	
	
	public void GotoBack() {
		System.out.println("Go to Back!!");
		
		panelGroup.setVisible(false);
		backBtn.setVisible(false);
		panelJoin.setVisible(true);
		
		m_clientStub.leaveSession();
		
	}
	

	
	
	
	// *** ActionListener class***
	public class MyActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e)
		{
			JButton button = (JButton) e.getSource();
			if(button.equals(loginButton)) {
				LoginUser();
			}else if(button.equals(registerButton)){
				RegisterUser();
			}else if(button.equals(logoutButton)){
				LogoutUser();
			}else if(button.equals(location1)) {
				JoinSession1(location1.getText());
			}else if(button.equals(location2)) {
				JoinSession2(location2.getText());
			}else if(button.equals(location3)) {
				JoinSession3(location3.getText());
			}else if(button.equals(backBtn)){
				GotoBack();
			}else if(button.equals(joinGroupBtn)){
				joinWindow = new joinGroupWindow(m_clientStub,m_eventHandler);
			}else if(button.equals(createGroupBtn)) {
				createWindow = new CreateGroupWindow(m_clientStub,UserSessionInfo,m_eventHandler);
			}
		}
	}
	
	// *** End ActionListener ***
	
	public static void main(String[] args) 
	{
		TPClient client = new TPClient();
		client.m_clientStub.setAppEventHandler(client.m_eventHandler);
		client.m_clientStub.startCM();
		
	}
}


//////

class SigninWindow extends JFrame{

	private static final long serialVersionUID = 1L;
	
	CMClientStub m_clientStub;
	TPClientEventHandler m_eventHandler;
	
	private JTextField signinID;
	private JPasswordField signinPWD;
	private JPasswordField PWDcheck;
	private JButton signinBtn;
	private JButton cancelBtn;
	
	private String id;
	
	SigninWindow(CMClientStub m_windowstub){
		
		m_clientStub = m_windowstub;
		m_eventHandler = new TPClientEventHandler(m_clientStub);
		MyActionListener cmActionListener = new MyActionListener();
		
		setTitle("Signin");
		setSize(400,300);
		setVisible(true);
		setResizable(false);

		Container NewWindowContainer = getContentPane();
		setLayout(null);

		JLabel IDlabel = new JLabel("ID : ");
		signinID = new JTextField();
		JLabel PWDlabel = new JLabel("Password : ");
		signinPWD = new JPasswordField();
		JLabel PWDchecklabel = new JLabel("Password check");
		PWDcheck = new JPasswordField();
		
		signinBtn = new JButton("sign in");
		signinBtn .addActionListener(cmActionListener);
		cancelBtn = new JButton("Cancel");
		cancelBtn .addActionListener(cmActionListener);
		
		JPanel panel = new JPanel();
		
		JPanel panelButton = new JPanel();
		JPanel panelText = new JPanel();
		
		int panelX = 300;
		int panelY = 500;
		
		
		panel.setLayout(new BorderLayout());
		panel.setBounds(200-(panelX/2), 300-(panelY/2), panelX, panelY);

		panelText.setLayout(new GridLayout(3,2));
		//panelText.setBackground(Color.gray);
		//panelButton.setBackground(Color.yellow);
		
		panelText.add(IDlabel);
		panelText.add(signinID);
		panelText.add(PWDlabel);
		panelText.add(signinPWD);
		panelText.add(PWDchecklabel);
		panelText.add(PWDcheck);
		
		panelButton.add(signinBtn);
		panelButton.add(cancelBtn);
		
		panel.add(panelText,BorderLayout.NORTH);
		panel.add(panelButton,BorderLayout.CENTER);
		
		NewWindowContainer.add(panel);
		
	}
	
	public void Signin() {
		
		id = signinID.getText();
		
		String pwd = "";
		char[] secretpwd = signinPWD.getPassword();			
		for(char cha : secretpwd){         
	         Character.toString(cha);
	         pwd += (pwd.equals("")) ? ""+cha+"" : ""+cha+"";   
	     }

		String checkpwd = "";
		char[] secretcheckpwd = PWDcheck.getPassword();

		for(char cha : secretcheckpwd){         
	         Character.toString(cha);       
	         checkpwd += (checkpwd.equals("")) ? ""+cha+"" : ""+cha+"";   
	     }


		if(pwd.equals(checkpwd)) {
			JOptionPane.showMessageDialog(null,"success");
			m_clientStub.startCM();
			m_clientStub.registerUser(id,pwd); // client stub send register info to server
			dispose();
		}
		else
			JOptionPane.showMessageDialog(null,"check your password");
		
		}
	
	
	
	// *** ActionListener class***
		 class MyActionListener implements ActionListener {
			public void actionPerformed(ActionEvent e)
			{
				JButton button = (JButton) e.getSource();
				if(button.equals(signinBtn)) {
					Signin();
				}else if(button.equals(cancelBtn)){
					dispose();
				}
			}
		}
	
}


class CreateGroupWindow extends JFrame{

	private static final long serialVersionUID = 1L;
	
	ChattingWindow chattingwindow ;
	CMClientStub m_clientStub;
	TPClientEventHandler m_eventHandler;
	
	private JPanel panelType;
	private JPanel panelStore;
	private JPanel panelMenu;
	
	private JButton backBtn;
	private int panelCount = 0;
	
	private StringBuilder creategroup = new StringBuilder();
	
	// panelType
	private JPanel panelTypeButton;
	private JPanel panelTypeVia;
	
	private JLabel typeLabel;
	
	private JButton[] typeButton = new JButton[4];
	
	private String storeTypeNameServer = "";	// store name send to server
	
	//panelStore
	private JPanel panelStoreList;
	private JPanel panelStoreVia;
	private JPanel panelSelectStore;
	
	private JLabel storeLabel;
	private JLabel selectStoreLabel;
	public JTextArea StoreList;
	
	private JTextField selectStore;
	
	private JButton selectStoreBtn;
	
	private String storeNameServer = "";	// store name send to server
	
	//panelMenu
	private JPanel panelMenuList;
	private JPanel panelMenuVia;
	private JPanel panelSelectMenu1;
	private JPanel panelSelectMenu2;
	private JPanel panelInputMenu;
	
	private JLabel menuLabel;
	private JLabel selectMenuLabel;
	private JTextArea MenuList;
	private JLabel menuChooseLabel;
	
	private JTextField selectMenu;
	
	private JButton addMenuBtn;
	private JButton clearMenuBtn;
	private JButton selectMenuBtn;
	
	private String menuName =""; 
	private String menuNameServer = "";	// menu name send to server
	private JTextArea menuChoose;	 	// menu user choose
	
	private int menuNum = 0;
	
	private String category ;
	
	CreateGroupWindow(CMClientStub m_windowstub, String sessionInfo, TPClientEventHandler m_eventhandler){
		
		String UserSessionInfo = sessionInfo;
		
		creategroup.append("C1##");
		creategroup.append(UserSessionInfo+"##");
		
		m_clientStub = m_windowstub;
		m_eventHandler = m_eventhandler;
		MyActionListener cmActionListener = new MyActionListener();
		
		setTitle("Create Group");
		setSize(400,600);
		setVisible(true);
		setResizable(false);

		Container NewGroupWindowContainer = getContentPane();
		setLayout(null);
		
		backBtn = new JButton("Back");
		backBtn.setBounds(10, 10, 70, 30);
		backBtn.addActionListener(cmActionListener);
		backBtn.setVisible(false);
		
		
		
		//--------------------------------- paenlType ---------------------------------//
		
		panelType = new JPanel(new BorderLayout());
		panelType.setBounds(0, 25, 400, 575);
		//panelType.setBackground(Color.yellow);
		
		panelTypeVia = new JPanel();
		panelTypeVia.setLayout(null);
		
		panelTypeButton = new JPanel(new GridLayout(2,2));
		panelTypeButton.setBounds(75, 50, 250, 400);
		//panelTypeButton.setBackground(Color.gray);
		
		typeLabel = new JLabel("Choose Type of Food");
		typeLabel.setFont(typeLabel.getFont().deriveFont(18.0f));
		typeLabel.setHorizontalAlignment(JLabel.CENTER);
		
		typeButton[0] = new JButton("Korean");
		typeButton[1] = new JButton("Japanese");
		typeButton[2] = new JButton("Chinese");
		typeButton[3] = new JButton("Western");
		

		for(int i =0 ; i < 4; i++) {
			panelTypeButton.add(typeButton[i]);
			typeButton[i].addActionListener(cmActionListener);
		}
		
		panelTypeVia.add(panelTypeButton);
		
		panelType.add(typeLabel,BorderLayout.NORTH);
		panelType.add(panelTypeVia,BorderLayout.CENTER);		
		
		panelType.setVisible(true);
		
		//--------------------------------- paenlStore ---------------------------------//
		
		panelStore = new JPanel(new BorderLayout());
		panelStore.setBounds(0, 20, 400, 575);
		
		storeLabel = new JLabel("Choose Store");
		storeLabel.setFont(typeLabel.getFont().deriveFont(18.0f));
		storeLabel.setHorizontalAlignment(JLabel.CENTER);
		
		panelStoreVia = new JPanel();
		panelStoreVia.setLayout(null);
		
		panelStoreList = new JPanel(new GridLayout(1,0)); 
		panelStoreList.setBounds(75, 20, 250, 420);
		panelStoreList.setBackground(Color.gray);
		
		panelSelectStore = new JPanel(new FlowLayout());
		panelSelectStore.setBounds(0, 450, 400, 40);
		//panelSelectStore.setBackground(Color.gray);
		
		StoreList = new JTextArea();
		StoreList.setBackground(Color.gray);
		StoreList.setEditable(false); 
		
		selectStoreLabel = new JLabel("Store : ");
		
		selectStore = new JTextField();
		selectStore.setPreferredSize(new Dimension(150,30));
		selectStore.setFont(typeLabel.getFont().deriveFont(15.0f));
		
		selectStoreBtn = new JButton("Choose");
		selectStoreBtn.addActionListener(cmActionListener);
		
		panelStoreList.add(new JScrollPane(StoreList));
		
		panelSelectStore.add(selectStoreLabel);
		panelSelectStore.add(selectStore);
		panelSelectStore.add(selectStoreBtn);
		
		panelStoreVia.add(panelStoreList);
		panelStoreVia.add(panelSelectStore);
		
		panelStore.add(storeLabel,BorderLayout.NORTH);
		panelStore.add(panelStoreVia,BorderLayout.CENTER);
		
		panelStore.setVisible(false);
		
		//--------------------------------- paenlMenu---------------------------------//
		
		panelMenu = new JPanel(new BorderLayout());
		panelMenu.setBounds(0, 20, 400, 575);
		
		menuLabel = new JLabel("Choose Menu");
		menuLabel.setFont(typeLabel.getFont().deriveFont(18.0f));
		menuLabel.setHorizontalAlignment(JLabel.CENTER);
		
		panelMenuVia = new JPanel();
		panelMenuVia.setLayout(null);
		
		panelMenuList = new JPanel(new GridLayout(1,0)); 
		panelMenuList.setBounds(75, 20, 250, 320);
		panelMenuList.setBackground(Color.gray);
		
		panelInputMenu = new JPanel(new BorderLayout());
		panelInputMenu.setBounds(75,340,250,100);
		panelInputMenu.setBackground(Color.LIGHT_GRAY);
		
		panelSelectMenu1 = new JPanel(new FlowLayout());
		panelSelectMenu1.setBounds(0, 450, 400, 35);
		//panelSelectMenu.setBackground(Color.gray);
		
		panelSelectMenu2 = new JPanel(new GridLayout(0,2));
		panelSelectMenu2.setBounds(50, 487, 300, 30);
		
		MenuList = new JTextArea();
		MenuList.setBackground(Color.gray);
		MenuList.setEditable(false); 
		menuChoose = new JTextArea("");
		menuChoose.setBackground(Color.LIGHT_GRAY);
		menuChoose.setEditable(false); 
		
		menuChooseLabel = new JLabel("====== CHOOSEN MENU ======");
		menuChooseLabel.setHorizontalAlignment(JLabel.CENTER);
		selectMenuLabel = new JLabel(" Menu : ");
		
		selectMenu = new JTextField();
		selectMenu.setPreferredSize(new Dimension(150,30));
		selectMenu.setFont(typeLabel.getFont().deriveFont(15.0f));
		
		addMenuBtn = new JButton("add");
		addMenuBtn.addActionListener(cmActionListener);
		
		clearMenuBtn = new JButton("clear");
		clearMenuBtn.addActionListener(cmActionListener);
		
		selectMenuBtn = new JButton("select");
		selectMenuBtn.addActionListener(cmActionListener);
		
		panelMenuList.add(new JScrollPane(MenuList));
		
		panelInputMenu.add(menuChooseLabel,BorderLayout.NORTH);
		panelInputMenu.add(new JScrollPane(menuChoose),BorderLayout.CENTER);
		
		panelSelectMenu1.add(selectMenuLabel);
		panelSelectMenu1.add(selectMenu);
		panelSelectMenu1.add(addMenuBtn);
		
		panelSelectMenu2.add(clearMenuBtn);
		panelSelectMenu2.add(selectMenuBtn);
		
		panelMenuVia.add(panelMenuList);
		panelMenuVia.add(panelInputMenu);
		panelMenuVia.add(panelSelectMenu1);
		panelMenuVia.add(panelSelectMenu2);
		
		panelMenu.add(menuLabel,BorderLayout.NORTH);
		panelMenu.add(panelMenuVia,BorderLayout.CENTER);
		
		panelMenu.setVisible(false);
	
				
		NewGroupWindowContainer.add(backBtn);
		NewGroupWindowContainer.add(panelMenu);
		NewGroupWindowContainer.add(panelStore);
		NewGroupWindowContainer.add(panelType);
		
		
	}
	public void CheckStoreDB() { // check store DB // server do 2
		
			//send "REQ_STORE" to SERVER
			CMDummyEvent e = new CMDummyEvent();
			e.setDummyInfo("REQ_STORE##"+category);
				
			CMInteractionInfo interInfo = m_clientStub.getCMInfo().getInteractionInfo();
			String strDefServer = interInfo.getDefaultServerInfo().getServerName();
			m_clientStub.send(e,strDefServer);
				
		
		
	}
	
	public void CheckStoreDB(String msg) { // check store DB // server do 2
			
			StoreList.setText("");
			StoreList.append(msg);

	}
	
	
	public void CheckMenuDB() { // check menu DB // server do 3
		//send "REQ_STORE" to SERVER
		CMDummyEvent e = new CMDummyEvent();
		e.setDummyInfo("REQ_MENU##"+storeNameServer);
			
		CMInteractionInfo interInfo = m_clientStub.getCMInfo().getInteractionInfo();
		String strDefServer = interInfo.getDefaultServerInfo().getServerName();
		m_clientStub.send(e,strDefServer);
	}
	
	public void CheckMenuDB(String msg)
	{
		MenuList.setText("");
		MenuList.append(msg);
	}
	
	public void ChangePanelToStore() {
		CheckStoreDB();
		panelStore.setVisible(true);
		backBtn.setVisible(true);
		panelType.setVisible(false);
		panelCount++;
	}
	
	public void ChangePanelToMenu() {
		storeNameServer = "";
		storeNameServer = selectStore.getText();
		selectStore.setText("");
		CheckMenuDB();
		
		panelMenu.setVisible(true);
		panelStore.setVisible(false);
		panelCount++;
	}
	
	public void addMenu() { // think point : make sure it's a real menu && scroll
		String temp =null;
		
		if( (temp = selectMenu.getText()) != null) {
			selectMenu.setText("");
			
			menuNum++;
			menuName = menuName.concat("[ menu "+menuNum+" ] :"+temp+"\n");
			menuChoose.setText(menuName);
			
			menuNameServer = menuNameServer.concat(temp+"##");
			System.out.println("menuNameServer == "+menuNameServer);
		}
	}
	
	public void clearMenu() {
		selectMenu.setText("");
		
		menuNum = 0;
		
		menuName = "";
		menuChoose.setText(menuName);
		
		menuNameServer = "";
		System.out.println("menuNameServer clear");
	}
	
	public void CreateGroup() { // make dummy string server wants
		
		creategroup.append(storeTypeNameServer+"##");
		creategroup.append(storeNameServer+"##");
		creategroup.append(menuNameServer);
		
		CMInteractionInfo interInfo = m_clientStub.getCMInfo().getInteractionInfo();
		String strDefServer = interInfo.getDefaultServerInfo().getServerName();
		
		CMDummyEvent due = new CMDummyEvent();
		String temp = creategroup.toString();
				
		due.setDummyInfo(temp);
//		m_clientStub.broadcast(due); ?? 
		m_clientStub.send(due, strDefServer);
		due = null;
		// clear menu info
		selectMenu.setText("");
		menuNum = 0;
		menuName = "";
		menuChoose.setText(menuName);
		menuNameServer = "";
				
		chattingwindow = new ChattingWindow(m_clientStub,m_eventHandler);
	
	}
	
	public void ChatOutUser() {
		
		
		
	}
	
	public void BackPanel() {
		
		if(panelCount == 1) { // back to typePanel
			
			storeTypeNameServer = "";
			storeNameServer = "";
			
			panelType.setVisible(true);
			panelStore.setVisible(false);
			backBtn.setVisible(false);
			
			panelCount--;	
		}else if(panelCount == 2) { //back to storePanel
			// first clear menuName and menuNameServer
			menuName = "";
			menuChoose.setText(menuName);
			
			menuNameServer = "";
			System.out.println("menuNameServer clear");
			
			panelStore.setVisible(true);
			panelMenu.setVisible(false);
			
			panelCount--;
		}
	}
	
	public class MyActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e)
		{
			JButton button = (JButton) e.getSource();
			if(button.equals(typeButton[0])) {
				category = "Korean";
			
				
				ChangePanelToStore();
				storeTypeNameServer = storeTypeNameServer.concat("Korean");
				System.out.println("storeTypeNameServer : "+storeTypeNameServer);
			}else if(button.equals(typeButton[1])){
				category = "Japanese";
				
				
				ChangePanelToStore();
				storeTypeNameServer = storeTypeNameServer.concat("Japanese");
				System.out.println("storeTypeNameServer : "+storeTypeNameServer);
			}else if(button.equals(typeButton[2])){
				category = "Chinese";
				
				ChangePanelToStore();
				storeTypeNameServer = storeTypeNameServer.concat("Chinese");
			}else if(button.equals(typeButton[3])){
				category = "Western";
			
				ChangePanelToStore();
				storeTypeNameServer = storeTypeNameServer.concat("Western");
			}else if(button.equals(backBtn)){
				BackPanel();
			}else if(button.equals(selectStoreBtn)){
				ChangePanelToMenu();
			}else if(button.equals(selectMenuBtn)) {
				CreateGroup();
			}else if(button.equals(addMenuBtn)) {
				addMenu();
			}else if(button.equals(clearMenuBtn)) {
				clearMenu();
			}
			
			
		}
	}
	
}

class joinGroupWindow extends JFrame{
	private static final long serialVersionUID = 1L;
	
	CMClientStub m_clientStub;
	TPClientEventHandler m_eventHandler;
	
	private JPanel groupselectPanel;
	private JPanel groupPanel;
	
	private int panelCount = 0;
	private JButton backBtn;
	
	private StringBuilder joingroup = new StringBuilder();
		
	private JLabel groupNolabel;
	private JTextField groupNo;
	private JButton OKBtn;	
	
	private String No;
	
	private JPanel panelMenu;
	
	private JPanel panelMenuList;
	private JPanel panelMenuVia;
	private JPanel panelSelectMenu1;
	private JPanel panelSelectMenu2;
	private JPanel panelInputMenu;
	
	private JLabel menuLabel;
	private JLabel selectMenuLabel;
	private JLabel MenuList;
	private JLabel menuChooseLabel;
	
	private JTextField selectMenu;
	
	private JButton addMenuBtn;
	private JButton clearMenuBtn;
	private JButton selectMenuBtn;
	
	private String menuName =""; 
	private String menuNameServer = "";	// menu name send to server
	private JTextArea menuChoose;	 	// menu user choose
	
	private int menuNum = 0;
	
	ChattingWindow chattingWindow;

	
	
	joinGroupWindow(CMClientStub m_windowstub,TPClientEventHandler m_eventhandler){
		
		joingroup.append("C2##");
		
		m_clientStub = m_windowstub;
		m_eventHandler = m_eventhandler;
		MyActionListener cmActionListener = new MyActionListener();
				
		setSize(400,600);
		setTitle("group");
		setResizable(false);
		Container c = getContentPane();
		setLayout(null);
		
		backBtn = new JButton("Back");
		backBtn.setBounds(10, 10, 70, 30);
		backBtn.addActionListener(cmActionListener);
		backBtn.setVisible(false);
		
			
		//--------------------------------groupselectPanel----------------------------//
				
		groupNolabel = new JLabel("Group No : ");
		groupNo = new JTextField();
		OKBtn = new JButton("OK");
		
		groupNo.setPreferredSize(new Dimension(150,30));
		
		OKBtn.addActionListener(cmActionListener);
		
		groupselectPanel = new JPanel();
		
		groupselectPanel.setLayout(new FlowLayout());
		groupselectPanel.setBounds(0, 50, 400, 600);
		
		groupselectPanel.add(groupNolabel);
		groupselectPanel.add(groupNo);
		groupselectPanel.add(OKBtn);
				
		
		c.add(groupselectPanel);
		
		groupselectPanel.setVisible(true);
		

		//---------------panelmenu---------------------//
		
		panelMenu = new JPanel(new BorderLayout());
		panelMenu.setBounds(0, 20, 400, 575);
		
		menuLabel = new JLabel("Choose Menu");
		menuLabel.setFont(menuLabel.getFont().deriveFont(18.0f));
		menuLabel.setHorizontalAlignment(JLabel.CENTER);
		
		panelMenuVia = new JPanel();
		panelMenuVia.setLayout(null);
		
		panelMenuList = new JPanel(new FlowLayout()); 
		panelMenuList.setBounds(75, 20, 250, 320);
		panelMenuList.setBackground(Color.gray);
		
		panelInputMenu = new JPanel(new BorderLayout());
		panelInputMenu.setBounds(75,340,250,100);
		panelInputMenu.setBackground(Color.DARK_GRAY);
		
		panelSelectMenu1 = new JPanel(new FlowLayout());
		panelSelectMenu1.setBounds(0, 450, 400, 35);
		//panelSelectMenu.setBackground(Color.gray);
		
		panelSelectMenu2 = new JPanel(new GridLayout(0,2));
		panelSelectMenu2.setBounds(50, 487, 300, 30);
		
		MenuList = new JLabel("<HTML> [ Menu 1 ] : jajang noodle<br> [ Menu 2 ] : jjamppong<br> [ Menu 3 ] : sweet and sour pork</HTML>");
		menuChoose = new JTextArea("");
		
		menuChooseLabel = new JLabel("Choosen menu");
		menuChooseLabel.setHorizontalAlignment(JLabel.CENTER);
		menuChooseLabel.setBackground(Color.gray);
		selectMenuLabel = new JLabel(" Menu : ");
		
		selectMenu = new JTextField();
		selectMenu.setPreferredSize(new Dimension(150,30));
		selectMenu.setFont(menuLabel.getFont().deriveFont(15.0f));
		
		addMenuBtn = new JButton("add");
		addMenuBtn.addActionListener(cmActionListener);
		
		clearMenuBtn = new JButton("clear");
		clearMenuBtn.addActionListener(cmActionListener);
		
		selectMenuBtn = new JButton("select");
		selectMenuBtn.addActionListener(cmActionListener);
		
		panelMenuList.add(MenuList);
		
		panelInputMenu.add(menuChooseLabel,BorderLayout.NORTH);
		panelInputMenu.add(new JScrollPane(menuChoose),BorderLayout.CENTER);
		
		panelSelectMenu1.add(selectMenuLabel);
		panelSelectMenu1.add(selectMenu);
		panelSelectMenu1.add(addMenuBtn);
		
		panelSelectMenu2.add(clearMenuBtn);
		panelSelectMenu2.add(selectMenuBtn);
		
		panelMenuVia.add(panelMenuList);
		panelMenuVia.add(panelInputMenu);
		panelMenuVia.add(panelSelectMenu1);
		panelMenuVia.add(panelSelectMenu2);
		
		panelMenu.add(menuLabel,BorderLayout.NORTH);
		panelMenu.add(panelMenuVia,BorderLayout.CENTER);
		
		panelMenu.setVisible(false);
		
		c.add(panelMenu);
		c.add(backBtn);
		
		backBtn.setVisible(true);
		
		
		setVisible(true);
	}
	public void select2menu() {
		No = groupNo.getText();
		joingroup.append(No+"##");
		
		panelCount++;
		
		groupselectPanel.setVisible(false);
		panelMenu.setVisible(true);
		groupPanel.setVisible(false);
	}
	public void addMenu() { // think point : make sure it's a real menu && scroll
		String temp =null;
		
		if( (temp = selectMenu.getText()) != null) {
			selectMenu.setText("");
			
			menuNum++;
			menuName = menuName.concat(" [ menu "+menuNum+" ] :"+temp+"\n");
			menuChoose.setText(menuName);
			
			menuNameServer = menuNameServer.concat(temp+"##");
			System.out.println("menuNameServer == "+menuNameServer);
		}
	}
	
	public void clearMenu() {
		selectMenu.setText("");
		
		menuNum = 0;
		
		menuName = "";
		menuChoose.setText(menuName);
		
		menuNameServer = "";
		System.out.println("menuNameServer clear");
	}
	public void joinGroup() {
		
		
		String temp;
		////////////////
		
		
		
		////////////////
		joingroup.append(menuNameServer);
		System.out.println("joingroup::"+joingroup);
		
		CMInteractionInfo interInfo = m_clientStub.getCMInfo().getInteractionInfo();
		String strDefServer = interInfo.getDefaultServerInfo().getServerName();
		
		CMDummyEvent due = new CMDummyEvent();
		temp = joingroup.toString();
				
		due.setDummyInfo(temp);
		m_clientStub.broadcast(due);
		m_clientStub.send(due, strDefServer);
		due = null;
		
		
		
		
	}
	public void back() {
		if(panelCount==1)
		{
			panelCount--;
			joingroup.delete(5,joingroup.length()+1);
			
			groupselectPanel.setVisible(true);
			panelMenu.setVisible(false);
			groupPanel.setVisible(false);			
			
		}
		else if(panelCount==0)
			dispose();
	}

	
	
	class MyActionListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e)
		{
			JButton button = (JButton) e.getSource();
			if(button.equals(OKBtn)) {
				select2menu();
			}else if(button.equals(backBtn)) {
				back();
			}else if(button.equals(addMenuBtn)) {
				addMenu();
			}else if(button.equals(clearMenuBtn)) {
				clearMenu();
			}else if(button.equals(selectMenuBtn)) {
				joinGroup();
				chattingWindow = new ChattingWindow(m_clientStub,m_eventHandler);
			}
		
		}
	}
}


class ChattingWindow extends JFrame{
	
	private static final long serialVersionUID = 1L;
	CMClientStub m_clientStub;
	TPClientEventHandler m_eventHandler;
	
	private JPanel panelChat;
	private JPanel panelChatRoom;
	private JPanel panelChatInput;
	
	private JLabel chatLabel;
	
	public JTextArea chatWindow;
	private JTextArea chatInput;
	
	private JButton chatSendBtn;
	
	private JButton backBtn;
	
	
	
	ChattingWindow(CMClientStub m_windowstub, TPClientEventHandler m_eventhandler)
	{
		m_clientStub = m_windowstub;
		m_eventHandler = m_eventhandler;
		MyActionListener cmActionListener = new MyActionListener();
		
		
		
		
		setTitle("Create Group");
		setSize(400,600);
		setVisible(true);
		setResizable(false);

		Container ChatWindowContainer = getContentPane();
		setLayout(null);
		
		backBtn = new JButton("Back");
		backBtn.setBounds(10, 10, 70, 30);
		backBtn.addActionListener(cmActionListener);
		backBtn.setVisible(false);
		
		panelChat = new JPanel(new BorderLayout());
		panelChat.setBounds(0, 45, 400, 520);
		
		chatLabel = new JLabel("Chat");
		chatLabel.setFont(chatLabel.getFont().deriveFont(18.0f));
		chatLabel.setHorizontalAlignment(JLabel.CENTER);
		
		panelChatRoom = new JPanel(new GridLayout(1,0));
		panelChatInput = new JPanel(new FlowLayout());
		
		chatWindow = new JTextArea("");
		chatWindow.setEditable(false); 
		chatInput = new JTextArea("",4,25);
		
		chatInput.setEditable(true); 
		
		chatSendBtn = new JButton("enter");
		chatSendBtn.addActionListener(cmActionListener);
		
		
		panelChatRoom.add(new JScrollPane(chatWindow));
		panelChatInput.add(new JScrollPane(chatInput));
		panelChatInput.add(chatSendBtn);
		
		panelChat.add(chatLabel,BorderLayout.NORTH);
		panelChat.add(panelChatRoom,BorderLayout.CENTER);
		panelChat.add(panelChatInput,BorderLayout.SOUTH);
		
		panelChat.setVisible(true);
		
		ChatWindowContainer.add(panelChat);
		
		// send handler chatWindow
			m_eventHandler.setWindow(chatWindow);
		
	}
	
	public void ChatInUser() {
		String chatMsg = chatInput.getText();
		
		m_clientStub.chat("/g", chatMsg);
	}
	
class MyActionListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e)
		{
//			ChatInUser();
			JButton button = (JButton) e.getSource();
			if(button.equals(chatSendBtn)) {
			ChatInUser();
		}
			
		
		}
	}


	
}
