import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import kr.ac.konkuk.ccslab.cm.entity.CMSessionInfo;
import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMMqttManager;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

public class SimpleCMClient extends JFrame {
	// Stub ��ü ����
	
	CMClientStub m_clientStub;
	// �������κ��� ������ Event ���� �ޱ� ���ؼ� 
	//  ( current : NULL )
	CMClientEventHandler m_eventHandler;
	CMMqttManager mqttManager ;


	
	
	// MAIN MENU
//	private JPanel loginPanel = new JPanel(new GridLayout(3,2));
	private JLabel idLabel = new JLabel("���̵�");
	private JLabel pwLabel = new JLabel("��й�ȣ");
	private JTextField idText = new JTextField();
	private TextField pwText = new TextField();
	
	private JButton loginBtn = new JButton("�α���");
	
//	private JPanel MainPanel = new JPanel(new GridLayout(3,2));
	public JTextArea SessionPanel = new JTextArea();
	private JScrollPane scrollPane = new JScrollPane(SessionPanel);
	
	
	
	// ������
	public SimpleCMClient(){
		setLocationRelativeTo(null); // â ȭ�� �߾ӿ�
	
		Container contentPane = getContentPane();
		
		contentPane.setLayout(new GridLayout(3,2));
		contentPane.add(idLabel);
		contentPane.add(pwLabel);
		contentPane.add(idText);
		contentPane.add(pwText); 
		pwText.setEchoChar('*'); // * �� ǥ��
		contentPane.add(loginBtn);
		setSize(300,100);
		setVisible(true);
		setTitle("�α��� â");
		
	
		m_clientStub = new CMClientStub();
		m_eventHandler = new CMClientEventHandler(m_clientStub,this);
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
		// loginBtn ������ ��
		loginBtn.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		boolean RequestResult = false ; // ��������
		String id = idText.getText().trim();
		String pwd = pwText.getText().trim();
		RequestResult = m_clientStub.loginCM(id,pwd);
		
		// login success
		if(RequestResult) {
			JOptionPane.showMessageDialog(null,"�α��� ����!") ;
			setVisible(false);
			
			//connect to CM_MQTT_SERVER
			mqttManager = (CMMqttManager) m_clientStub.findServiceManager(CMInfo.CM_MQTT_MANAGER);
			mqttManager.connect();
			new MainMenu(); // ���θ޴� ȭ������ ��ȯ
		

		}
		else
			JOptionPane.showMessageDialog(null,"�α��� ����!") ;	
		}
		});
		
	}
	
	JLabel label = new JLabel("Ŭ���̾�Ʈ �׽�Ʈ");
	JTextPane LogArea = new JTextPane();
	JTextField TextField = new JTextField(20);
	JButton button = new JButton("Ȯ��");
	JPanel P1 = new JPanel();
	// MainMenu
	class MainMenu extends JFrame{
		
		MainMenu() {
			setLocationRelativeTo(null); 
			setTitle("��ް����ý���");
			setSize(500,400);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setVisible(true);
			Container c = getContentPane();
			c.setLayout(new BorderLayout());
			c.add(label,BorderLayout.NORTH);
			LogArea.setEditable(false);
			c.add(LogArea,BorderLayout.CENTER);
			P1.add(TextField);
			P1.add(button);
			c.add(P1,BorderLayout.SOUTH);
		// --------------------------- TEST AREA ---------------------------------
			// (����) 
			// ��ɾ� 
			// printMessage(Ȯ��) ;
		// -----------------------------------------------------
			printMessage("1. Print SessionNames : printSessions");
			printMessage("2. JoinSession : /s �����̸�");
			printMessage("3. Get myInfo(group,session) : MyInfo");
			
			
			
			
			// --------------------------------------------------------------------------
			
			
			// Button Listener
			button.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					String Menunum = null ;
					String MenuString = null ;
					
					boolean bRequestResult = false;
					String StringInput = TextField.getText();
					StringTokenizer Strings = new StringTokenizer(StringInput);
					// ex) /s session1
					if(Strings.countTokens()==2) {
						Menunum =  Strings.nextToken();
						MenuString = Strings.nextToken();
					}
					// ex) exit
					else MenuString = StringInput ;	
					
					if(Menunum!= null && Menunum.equals("/s")) 
					{
						bRequestResult = m_clientStub.joinSession(MenuString);
						if(bRequestResult)
						{
							printMessage("Successfully joined "+MenuString);
							mqttManager.subscribe(MenuString,(byte)0);
						}
						else
							printMessage("Session join failed");
					}
					else if (MenuString.equals("printSessions")||MenuString.equals("1"))
						PrintSessionNames();
					else if (MenuString.equals("MyInfo")|| MenuString.equals("3"))
					{
						CMUser user = m_clientStub.getMyself();
						printMessage("My Session : " + user.getCurrentSession());
						printMessage("My Group : " + user.getCurrentGroup()) ;
					}	
					else if (MenuString.equals("4")) {
						String msg = "C2##g1##juice"; 
						mqttManager.subscribe("g1",(byte)0) ;
						CMDummyEvent Nmsg = new CMDummyEvent(); 
						Nmsg.setDummyInfo(msg);
						m_clientStub.send(Nmsg,"SERVER");
						
						
					}
					else 
						printMessage("--------WRONG COMMAND-----------");
					
					//flush
					TextField.setText("");
				}
			});
			
		}
		
		
	}
	
	public void PrintSessionNames() {
		// session info ��������
			CMSessionEvent se = null; 
			se = m_clientStub.syncRequestSessionInfo();
			if(se==null) {
				printMessage("error,try again \n");
			}
			else {
				printMessage("Session Names:");
				Iterator<CMSessionInfo> iter = se.getSessionInfoList().iterator();
				while(iter.hasNext()) {
					CMSessionInfo tInfo = iter.next();
					printMessage(tInfo.getSessionName());
				}		
			}
	}
	
	public void printMessage(String strText)
	{
		StyledDocument doc = LogArea.getStyledDocument();
		try {
			doc.insertString(doc.getLength(), strText+'\n', null);
			LogArea.setCaretPosition(LogArea.getDocument().getLength());

		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return;
	}

}
