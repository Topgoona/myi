package myi.AddressBook;

//Java core packages
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

//Java extension packages
import javax.swing.*;
import javax.swing.event.*;

public class AddressBook extends JFrame {

	//reference for manipulating multiple document interface
	private JDesktopPane desktop;

	//reference to database access object
	private AddressBookDataAccess database;

	//references to Actions
	Action newAction, saveAction, deleteAction, searchAction, exitAction;


	//set up database connection and GUI
	public AddressBook(){
		super( "My I                         CONTACTS" );
		setIconImage(new ImageIcon(getClass().getResource("/images/logo89.png")).getImage());
 
		// create database connection
		try {
			database = new DataAccess();
		}
 
		// detect problems with database connection
		catch ( Exception exception ) {
			exception.printStackTrace();
			System.exit( 1 );
		}
 
		// database connection successful, create GUI
		JToolBar toolBar = new JToolBar();
 
		// Set up actions for common operations. Private inner
		// classes encapsulate the processing of each action.
		newAction = new NewAction();
		saveAction = new SaveAction();
		saveAction.setEnabled( false );    // disabled by default
		deleteAction = new DeleteAction();
		deleteAction.setEnabled( false );  // disabled by default
		searchAction = new SearchAction();
		exitAction = new ExitAction();
 
		// add actions to tool bar
		toolBar.add( new JToolBar.Separator() );
		toolBar.add( newAction );
		toolBar.add( new JToolBar.Separator() );
		toolBar.add( saveAction );
		toolBar.add( new JToolBar.Separator() );
		toolBar.add( deleteAction );
		toolBar.add( new JToolBar.Separator() );
		toolBar.add( searchAction );
		toolBar.add( new JToolBar.Separator() );
		toolBar.add( new JToolBar.Separator() );
		toolBar.add( new JToolBar.Separator() );
		toolBar.add( new JToolBar.Separator() );
		toolBar.add( new JToolBar.Separator() );
		toolBar.add( new JToolBar.Separator() );
		toolBar.add( new JToolBar.Separator() );

	
		toolBar.add( exitAction );
 
		// set up desktop
		desktop = new JDesktopPane();
 
		// get the content pane to set up GUI
		Container c = getContentPane();
		c.add( toolBar, BorderLayout.NORTH );
		c.add( desktop, BorderLayout.CENTER );
		c.setFont(new Font("Tahoma", Font.BOLD, 200));
		c.setForeground(java.awt.Color.red);
 
		// register for windowClosing event in case user 
		// does not select Exit from File menu to terminate
		// application
		addWindowListener( 
				new WindowAdapter() {
					public void windowClosing( WindowEvent event ){
						shutDown();
					}
				}
		);
 
		// set window size and display window
		Toolkit toolkit = getToolkit();
		Dimension dimension = toolkit.getScreenSize();
 
		// position window on screen
		setBounds(655, 10, dimension.width - 1000, dimension.height - 190 );
 
		setVisible( true );
	}  // end AddressBook constructor

	//close database connection and terminate program
	public void shutDown(){
		database.close();   // close database connection
		setVisible( false );
		//System.exit( 0 );   // terminate program
	}

	//create a new AddressBookEntryFrame and register listener
	private AddressBookEntryFrame createAddressBookEntryFrame(){
 
		AddressBookEntryFrame frame = new AddressBookEntryFrame();
		setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		frame.addInternalFrameListener(new InternalFrameAdapter() {

			// internal frame becomes active frame on desktop
			public void internalFrameActivated( InternalFrameEvent event ){
				saveAction.setEnabled( true );  
				deleteAction.setEnabled( true );  
			}

			// internal frame becomes inactive frame on desktop
			public void internalFrameDeactivated( InternalFrameEvent event ){
				saveAction.setEnabled( false );  
				deleteAction.setEnabled( false );  
			}
		}); // end call to addInternalFrameListener
 
		return frame;  
	}  // end method createAddressBookEntryFrame

	//method to launch program execution
	public static void main( String args[] ){
		try {
			UIManager.setLookAndFeel("com.jtattoo.plaf.noire.NoireLookAndFeel");
		}
		catch(Exception e) { }
		new AddressBook();
	}

	//Private inner class defines action that enables 
	//user to input new entry. User must "Save" entry
	//after inputting data.
	private class NewAction extends AbstractAction {
 
		// set up action's name, icon, descriptions and mnemonic
		public NewAction(){
			putValue( NAME, "New" );
			putValue( SMALL_ICON, new ImageIcon(getClass().getResource( "/images/new.png" ) ) );
			putValue( SHORT_DESCRIPTION, "New" );
			putValue( LONG_DESCRIPTION, "Add a new address book entry" );
			putValue( MNEMONIC_KEY, new Integer( 'N' ) );
		}
 
		// display window in which user can input entry
		public void actionPerformed( ActionEvent e ){
			// create new internal window
			AddressBookEntryFrame entryFrame = createAddressBookEntryFrame();
    
			// set new AddressBookEntry in window
			entryFrame.setAddressBookEntry( new AddressBookEntry() );
    
			// display window
			desktop.add( entryFrame );
			entryFrame.setVisible( true );
		}
 
	}  // end inner class NewAction

	//inner class defines an action that can save new or 
	//updated entry
	public class SaveAction extends AbstractAction {
 
		// set up action's name, icon, descriptions and mnemonic
		public SaveAction(){
			putValue( NAME, "Save" );
			putValue( SMALL_ICON, new ImageIcon(getClass().getResource( "/images/save.png" ) ) );
			putValue( SHORT_DESCRIPTION, "Save" );
			putValue( LONG_DESCRIPTION, "Save an address book entry" );
			putValue( MNEMONIC_KEY, new Integer( 'S' ) );
		}
 
		// save new entry or update existing entry
		public void actionPerformed( ActionEvent e ){
			// get currently active window
			AddressBookEntryFrame currentFrame = ( AddressBookEntryFrame ) desktop.getSelectedFrame();

			// obtain AddressBookEntry from window
			AddressBookEntry person = currentFrame.getAddressBookEntry();

			// insert person in address book
			try {
       
				// Get personID. If 0, this is a new entry; 
				// otherwise an update must be performed.
				int personID = person.getPersonID();
       
				// determine string for message dialogs
				String operation = ( personID == 0 ) ? "Insertion" : "Update";
          
				// insert or update entry
				if ( personID == 0 )         
					database.newPerson( person );
				else
					database.savePerson( person );
          
				// display success message
				JOptionPane.showMessageDialog( desktop, operation + " successful" );
			}  // end try
    
			// detect database errors
			catch ( DataAccessException exception ) {
				JOptionPane.showMessageDialog( desktop, exception, "DataAccessException", JOptionPane.ERROR_MESSAGE );
				exception.printStackTrace();  
			}
    
			// close current window and dispose of resources
			currentFrame.dispose();
    
		}  // end method actionPerformed
 
	}  // end inner class SaveAction

	//inner class defines action that deletes entry
	private class DeleteAction extends AbstractAction {
 
		// set up action's name, icon, descriptions and mnemonic
		public DeleteAction(){
			putValue( NAME, "Delete" );
			putValue( SMALL_ICON, new ImageIcon(getClass().getResource( "/images/delete.png" ) ) );
			putValue( SHORT_DESCRIPTION, "Delete" );
			putValue( LONG_DESCRIPTION, "Delete an address book entry" );
			putValue( MNEMONIC_KEY, new Integer( 'D' ) );
		}
 
		// delete entry
		public void actionPerformed( ActionEvent e ){
			// get currently active window
			AddressBookEntryFrame currentFrame = ( AddressBookEntryFrame ) desktop.getSelectedFrame();
    
			// get AddressBookEntry from window
			AddressBookEntry person = currentFrame.getAddressBookEntry();
    
			// If personID is 0, this is new entry that has not
			// been inserted. Therefore, delete is not necessary.
			// Display message and return.
			if ( person.getPersonID() == 0 ) {
				JOptionPane.showMessageDialog( desktop, "New entries must be saved before they can be " +
						"deleted. \nTo cancel a new entry, simply close the window containing the entry" );
				return;            
			}
    
			// delete person
			try {
				database.deletePerson( person );
       
				// display message indicating success
				JOptionPane.showMessageDialog( desktop, "Deletion successful" );         
			}
    
			// detect problems deleting person
			catch ( DataAccessException exception ) {
				JOptionPane.showMessageDialog( desktop, exception, "Deletion failed", JOptionPane.ERROR_MESSAGE );   
				exception.printStackTrace();  
			}
    
			// close current window and dispose of resources
			currentFrame.dispose();
		}  // end method actionPerformed 
 
	}  // end inner class DeleteAction

	//inner class defines action that locates entry
	private class SearchAction extends AbstractAction {
 
		// set up action's name, icon, descriptions and mnemonic
		public SearchAction(){
			putValue( NAME, "Search" );
			putValue( SMALL_ICON, new ImageIcon(getClass().getResource( "/images/search.png" ) ) );
			putValue( SHORT_DESCRIPTION, "Search" );
			putValue( LONG_DESCRIPTION, "Search for an address book entry" );
			putValue( MNEMONIC_KEY, new Integer( 'r' ) );
		}
 
		// locate existing entry
		public void actionPerformed( ActionEvent e ){
			String lastName = JOptionPane.showInputDialog( desktop, "Enter last name" );
    
			int maxindex = DataAccess.getMaxPersonID();
				
			for(int i = 1; i <= maxindex; i++){
				
				if ( lastName != null ) {
					
					AddressBookEntry person = database.findPerson( lastName , i);
					
					
					if ( person != null ) {
						// create window to display AddressBookEntry
						AddressBookEntryFrame entryFrame = createAddressBookEntryFrame();
		            
						// set AddressBookEntry to display
						entryFrame.setAddressBookEntry( person );
		             
						// display window
						desktop.add( entryFrame );
						entryFrame.setVisible( true );

					}
					
				}else
						JOptionPane.showMessageDialog( desktop, "Entry with last name \"" + lastName + "\" not found in address book" );
			}
		}  // end method actionPerformed
	}  // end inner class SearchAction

	//inner class defines action that closes connection to 
	//database and terminates program
	private class ExitAction extends AbstractAction {
 
		// set up action's name, descriptions and mnemonic
		public ExitAction(){
			putValue( NAME, "Exit" );
			putValue( SMALL_ICON, new ImageIcon(getClass().getResource( "/images/exit.png" ) ) );
			putValue( SHORT_DESCRIPTION, "Exit" );
			putValue( LONG_DESCRIPTION, "Terminate the program" );
			putValue( MNEMONIC_KEY, new Integer( 'x' ) );
		}
 
		// terminate program
		public void actionPerformed( ActionEvent e ){
			shutDown();  // close database connection and terminate
		}     
 
	}  // end inner class ExitAction 
}