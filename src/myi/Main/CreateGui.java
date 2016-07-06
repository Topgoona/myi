package myi.Main;

import java.awt.*;
import java.awt.event.*;



import javax.swing.*;

import myi.AddressBook.AddressBook;
import myi.Connection.Client;
import myi.Connection.Server;

@SuppressWarnings("serial")
public class CreateGui extends JFrame implements KeyListener{
	
	public static JTextField msgText;
	public static String msg = "";
	private boolean addressBookOpen = false;

	public static String getMsg() {
		return msg;
	}

	public static void setMsg(String msg) {
		CreateGui.msg = msg;
	}

	public static Action newCallAction, endCallAction, addressAction, textAction, acceptAction, exitAction;
	
	public static Server server = new Server();
	Client client = new Client();
	AddressBook ab1;
	JLabel picLabel = null;
	
	public CreateGui(){
		
		super();
		
		setTitle("MyI");
		
		JToolBar toolBar = new JToolBar();
		toolBar.setBackground(java.awt.Color.red);
		
		msgText = new JTextField();
		msgText.addKeyListener(this);
		picLabel = new JLabel(new ImageIcon(getClass().getResource( "/images/logo89.png" )));
		//picLabel.setSize(80, 60);
 
		newCallAction = new NewCallAction();
		newCallAction.setEnabled(true);
		endCallAction = new EndCallAction();
		endCallAction.setEnabled( false );    
		addressAction = new AddressAction();
		addressAction.setEnabled( true );  
		acceptAction = new AcceptAction();
		acceptAction.setEnabled(false);
		//textAction = new AddTextAction();
		exitAction = new ExitAction();
		
		msgText.setFont(new Font("Tahoma", Font.BOLD, 14));
		toolBar.add( new JToolBar.Separator() );
		toolBar.add(picLabel);
		
		toolBar.add( new JToolBar.Separator() );
		toolBar.add(msgText);
		
		toolBar.add( new JToolBar.Separator() );
		toolBar.add( addressAction );
		toolBar.add( new JToolBar.Separator() );
		toolBar.add( newCallAction );
		toolBar.add( new JToolBar.Separator() );
		toolBar.add( endCallAction );
		toolBar.add( new JToolBar.Separator() );
		toolBar.add( acceptAction );
		
		toolBar.add( new JToolBar.Separator() );
		toolBar.add( new JToolBar.Separator() );
		toolBar.add( exitAction );
 
		Container c = getContentPane();
		c.add( toolBar, BorderLayout.NORTH );

		addWindowListener( 
				new WindowAdapter() {
					public void windowClosing( WindowEvent event ){
						shutDown();
					}
				}
		);
 
		Toolkit toolkit = getToolkit();
		Dimension dimension = toolkit.getScreenSize();
 
		setBounds( 10, 10, dimension.width - 719, dimension.height - 690 );
 
		setVisible( true );
		client.run();
	}  

	private void shutDown(){
		System.exit( 0 ); 
	}
	

	public static void main( String args[] ){
		try {
			UIManager.setLookAndFeel("com.jtattoo.plaf.noire.NoireLookAndFeel");
		}
		catch(Exception e) {
			
		}
		
		new CreateGui();
	}


	private class NewCallAction extends AbstractAction {
		
		public NewCallAction(){
			putValue( NAME, "New" );
			putValue( SMALL_ICON, new ImageIcon(getClass().getResource( "/images/call.png" ) ) );
			putValue( SHORT_DESCRIPTION, "Make a Call" );
		}
		public void actionPerformed( ActionEvent e ){
			server.run();
			newCallAction.setEnabled( false );
			endCallAction.setEnabled( true );
			addressAction.setEnabled( true );
			msgText.setForeground(java.awt.Color.white);
			msgText.setText("");
		}
	}  


	public class EndCallAction extends AbstractAction {
 
		public EndCallAction(){
			putValue( NAME, "Save" );
			putValue( SMALL_ICON, new ImageIcon(getClass().getResource( "/images/endCall.png" ) ) );
			putValue( SHORT_DESCRIPTION, "End this Call" );
		}

		public void actionPerformed( ActionEvent e ){
			Server.closeServer();
			Client.closeClient();
			newCallAction.setEnabled( true );
			endCallAction.setEnabled( false );
			addressAction.setEnabled( false );
		}  
	} 

	private class AddressAction extends AbstractAction {
 
		public AddressAction(){
			putValue( NAME, "Address Book" );
			putValue( SMALL_ICON, new ImageIcon(getClass().getResource( "/images/msg.png" ) ) );
			putValue( SHORT_DESCRIPTION, "Address book" );
		}
 
		public void actionPerformed( ActionEvent e ){
			if(addressBookOpen == false){
				ab1 = new AddressBook();
				addressBookOpen = true;
			}
			else{
				ab1.shutDown();
				addressBookOpen = false;
			}
		} 
	} 
	
	 

	private class AcceptAction extends AbstractAction {
 
		public AcceptAction(){
			putValue( NAME, "Search" );
			putValue( SMALL_ICON, new ImageIcon(getClass().getResource( "/images/accept.png" ) ) );
			putValue( SHORT_DESCRIPTION, "Accept Incoming Call" );
		}
 
		public void actionPerformed( ActionEvent e ){
			server.run();
			acceptAction.setEnabled(false);
			newCallAction.setEnabled( false );
			endCallAction.setEnabled( true );
			addressAction.setEnabled( true );
		} 
	}  


	private class ExitAction extends AbstractAction {
 
		public ExitAction(){
			putValue( NAME, "Exit" );
			putValue( SMALL_ICON, new ImageIcon(getClass().getResource( "/images/exit.png" ) ) );
			putValue( SHORT_DESCRIPTION, "Exit MyI" );
		}
 
		public void actionPerformed( ActionEvent e ){
			shutDown();  
		}     
	}


	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getSource() == msgText) {
			if ((e.getKeyCode() == KeyEvent.VK_ENTER)) {
				msg = msgText.getText();
				msgText.setText("");
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	} 

}
