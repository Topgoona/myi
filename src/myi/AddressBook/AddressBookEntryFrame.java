package myi.AddressBook;

//Java core packages
import java.util.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;

//Java extension packages
import javax.swing.*;


import myi.Connection.Client;
import myi.Main.CreateGui;
import myi.Stream.EncodeVideo;
import net.miginfocom.swing.MigLayout;

public class AddressBookEntryFrame extends JInternalFrame {

	/**
	 * 
	 */

	// HashMap to store JTextField references for quick access
	static HashMap<String, JTextField> fields; 

	//reference to database access object
	private AddressBookDataAccess database;

	// current AddressBookEntry set by AddressBook application
	private AddressBookEntry person;

	// panels to organize GUI
	private JPanel panel;
	JFileChooser fileChooser;
	JLabel picLabel;

	private JButton exit;
	private JButton addPic;
	private JButton makeCall;

	private String ext;

	private FileInputStream fis;
	public static File file;
	public static String str;
	public static String imageString;

	// static integers used to determine new window positions  
	// for cascading windows
	private static int xOffset = 0, yOffset = 0;

	// static Strings that represent name of each text field.
	// These are placed on JLabels and used as keys in 
	// HashMap fields.
	static final String FIRST_NAME = " First Name";

	static final String LAST_NAME = " Last Name";

	static final String HOUSENAME = " House Name/Number";

	public static final String STREET = " Street";

	static final String CITY = " City";

	static final String COUNTY = " County";

	static final String PHONE = " Phone";

	static final String EMAIL = " Email";

	static final String IP = " IP Address";

	private static final String IMAGE = "Image";

	private static final String BUTTONS = "Buttons";

	// construct GUI
	public AddressBookEntryFrame(){
		super( "", true, true );
		
		JLabel picLabel = new JLabel(new ImageIcon(getClass().getResource( "/images/logo89.png" )));
		picLabel.setSize(50, 30);
 
		//toolBar.add(picLabel);
		fields = new HashMap();
		
		file = null;
		str = "";
 
		exit = new JButton(" Save ");
		exit.setBackground(java.awt.Color.black);
		exit.setForeground(java.awt.Color.red);
		exit.setFont(new Font("Tahoma", Font.BOLD, 26));
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				//AddressBook.saveAction.;
				//clearFields();
				//AddressBookEntry entry = clearFields();// = AddressBookEntryFrame.createAddressBookEntryFrame();
				//AddressBookEntryFrame frame = new AddressBookEntryFrame();
				//frame.setAddressBookEntry(clearFields());
				
				// set new AddressBookEntry in window
				//entryFrame.setAddressBookEntry(person.get );
	  
				// display window
				//try {
				//	database.savePerson( clearFields() );
				//} catch (DataAccessException e) {
					// TODO Auto-generated catch block
				//	e.printStackTrace();
				//}
			}
		});
 
		addPic = new JButton(" SnapShot ");
		addPic.setBackground(java.awt.Color.black);
		addPic.setForeground(java.awt.Color.yellow);
		addPic.setFont(new Font("Tahoma", Font.BOLD, 18));
		addPic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				fileChooser = new JFileChooser();
				int returnValue = fileChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.SAVE_DIALOG) {
					BufferedImage pic = EncodeVideo.bgrImg;
					//picLabel.add(pic);
					// TODO Auto-generated method stub
				
				}
			}
		});
		
		makeCall = new JButton(" Select ");
		makeCall.setBackground(java.awt.Color.black);
		makeCall.setForeground(java.awt.Color.green);
		makeCall.setFont(new Font("Tahoma", Font.BOLD, 26));
		makeCall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				Client.host = person.getIpAddress();
				System.out.println(person.getIpAddress());
				CreateGui.newCallAction.setEnabled( true );
				CreateGui.msgText.setForeground(java.awt.Color.red);
				CreateGui.msgText.setBackground(java.awt.Color.white);
				CreateGui.msgText.setText("Call " + person.getFirstName() + " " + person.getLastName());
			}
		});

		panel = new JPanel();
		panel.setLayout( new MigLayout());
	
		createRow( IMAGE );
		createRow( FIRST_NAME );
		createRow( LAST_NAME );
		createRow( STREET );
		createRow( CITY );
		createRow( COUNTY );
		createRow( PHONE );
		createRow( EMAIL );
		createRow( IP );
		createRow( BUTTONS );
		

		Container container = getContentPane();
		container.add( panel, BorderLayout.WEST );
		setBounds( 0, 2, 360, 500 );
	}
	
	public static String getImageFile() {
		if(file != null){
			imageString = file.toString();
		}
		return imageString;
		
		// TODO Auto-generated method stub
		
	}

	// set AddressBookEntry then use its properties to 
	// place data in each JTextField
	public void setAddressBookEntry( AddressBookEntry entry ){
		person = entry;
 
		//setImage( person.getImage());
		setField( FIRST_NAME, person.getFirstName() );
		setField( LAST_NAME, person.getLastName() );
		setField( STREET, person.getStreet() );
		setField( CITY, person.getCity() );
		setField( COUNTY, person.getCounty() );
		setField( PHONE, person.getPhoneNumber() );
		setField( EMAIL, person.getEmailAddress() );
		setField( IP, person.getIpAddress() );
	}

	// store AddressBookEntry data from GUI and return 
	// AddressBookEntry
	public AddressBookEntry getAddressBookEntry(){
 
		person.setFirstName( getField( FIRST_NAME ) );
		person.setLastName( getField( LAST_NAME ) );
		person.setStreet( getField( STREET ) );
		person.setCity( getField( CITY ) );
		person.setCounty( getField( COUNTY ) );
		person.setPhoneNumber( getField( PHONE ) );
		person.setEmailAddress( getField( EMAIL ) );
		person.setIpAddress( getField( IP ) );
 
		return person;
	}
	
	public AddressBookEntry clearFields(){
		AddressBookEntry currentPerson = null;
		currentPerson.setPersonID(person.getPersonID());
		
		JTextField field1 = ( JTextField ) fields.get( FIRST_NAME );
		person.setFirstName(field1.getText());
		System.out.println(person.getFirstName());
		field1.setText("");
		
		JTextField field2 = ( JTextField ) fields.get( LAST_NAME );
		person.setFirstName(field2.getText());
		System.out.println(person.getLastName());
		field2.setText("");
		
		JTextField field3 = ( JTextField ) fields.get( STREET );
		person.setStreet( field3.getText() );
		System.out.println(person.getStreet());
		field3.setText("");
		
		JTextField field4 = ( JTextField ) fields.get( CITY );
		person.setCity( field4.getText() );
		System.out.println(person.getCity());
		field4.setText("");
		
		
		JTextField field5 = ( JTextField ) fields.get( COUNTY );
		person.setCity( field5.getText() );
		System.out.println(person.getCity());
		field5.setText("");
		
		
		JTextField field6 = ( JTextField ) fields.get( PHONE );
		person.setPhoneNumber( field6.getText());
		System.out.println(person.getPhoneNumber());
		field6.setText("");
		
		JTextField field7 = ( JTextField ) fields.get( EMAIL );
		person.setEmailAddress( field7.getText());
		System.out.println(person.getEmailAddress());
		field7.setText("");
		
		JTextField field8 = ( JTextField ) fields.get( IP );
		person.setIpAddress(field8.getText());
		System.out.println(person.getIpAddress());
		field8.setText("");
		
		return currentPerson;
	}

	// set text in JTextField by specifying field's
	// name and value
	private void setField( String fieldName, String value ){
		JTextField field = ( JTextField ) fields.get( fieldName );
		field.setFont(new Font("Tahoma", Font.BOLD, 14));
		field.setForeground(java.awt.Color.white);
		field.setText( value );
	}
	
	
	private void setImage(){
		ImageIcon imageIcon = AddressBookEntry.getImage();
		if(imageIcon == null){
			picLabel = new JLabel(new ImageIcon(getClass().getResource( "/images/image1.png" ) ));
		}
		else{
			picLabel = new JLabel(imageIcon);
		}
		picLabel.setSize(100,100);
		panel.add(addPic, "gapx 15, gapy 20");
		panel.add(picLabel, "wrap, gapx 25, gapy 10");
		//picLabel = null;
	}

	// get text in JTextField by specifying field's name
	static String getField( String fieldName ){
		JTextField field = ( JTextField ) fields.get( fieldName );      
		return field.getText();  
	}

	// utility method used by constructor to create one row in
	// GUI containing JLabel and JTextField
	private void createRow( String name ){            
		JLabel label = new JLabel( name );
		label.setSize(10, 10);
		JTextField field = new JTextField( 15 );
		field.setBackground(java.awt.Color.black);
		label.setForeground(java.awt.Color.white);
		label.setFont(new Font("Tahoma", Font.BOLD, 14));
		//panel.add( addPic);
		if(name == IMAGE){

			setImage();
			//panel.add(addPic, "wrap, gapy 10");
		}
		else if(name == FIRST_NAME){
			panel.add( label ); 
			panel.add( field, "wrap, ,gapx 0, gapy 20");
	   }
	   else if(name == LAST_NAME){
		   panel.add( label ); 
		   panel.add( field, "wrap");
	   }

	   else if(name == STREET){
		   panel.add( label ); 
		   panel.add( field, "wrap");
	   }
	   else if(name == CITY){
		   panel.add( label ); 
		   panel.add( field, "wrap");
	   }
	   else if(name == COUNTY){
		   panel.add( label ); 
		   panel.add( field, "wrap");
	   }
	   else if(name == PHONE){
		   panel.add( label ); 
		   panel.add( field, "wrap, gapx -20, gapy 30");
	   }
	   else if(name == EMAIL){
	   panel.add( label ); 
	   panel.add( field, "wrap");
	   }
	   else if(name == IP){
		   panel.add( label ); 
		   panel.add( field, "wrap");
		   }
	   else if(name == BUTTONS){
		   //panel.add( exit, "gapx 35, gapy 20" );
		   panel.add (makeCall, "gapx 10, gapy 20");
	   }
	   
	   else{
		   panel.add( label ); 
		   panel.add( field);
	   }
 
		fields.put( name, field );
	}

}  // end class AddressBookEntryFrame

