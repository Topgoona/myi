package myi.AddressBook;

import java.sql.Connection;
import java.sql.DriverManager;

public class sqlConnection {
	
	// reference to database connection
	public static Connection connection;
	
	public static Connection connect() throws Exception{
	      
		// Java database controller class name
		String driver = "com.mysql.jdbc.Driver";
     
		// URL to connect to address book database
		String url = "jdbc:mysql://localhost:3306/myi_addressbook";
      
		// load database driver class
		Class.forName( driver );

		// connect to database
		connection = DriverManager.getConnection(url, "root", "laura"); 

		// Require manual commit for transactions. This enables 
		// the program to rollback transactions that do not 
		// complete and commit transactions that complete properly.
		connection.setAutoCommit( false );
		return connection;      
	}

}
