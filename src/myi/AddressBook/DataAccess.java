package myi.AddressBook;

// Java core packages

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;



public class DataAccess implements AddressBookDataAccess {
      
	// reference to database connection
	private static Connection connection;
      
	// reference to prepared statement for locating entry
	private PreparedStatement sqlFind;
	private PreparedStatement sqlFind2;

	// reference to prepared statement for determining personID
	private PreparedStatement sqlPersonID;

	// references to prepared statements for inserting entry
		private PreparedStatement sqlInsertName;
		private PreparedStatement sqlInsertAddress;;
		private PreparedStatement sqlInsertPhone;
		private PreparedStatement sqlInsertEmail;
		private PreparedStatement sqlInsertIp;
		private PreparedStatement sqlInsertImage;

			// references to prepared statements for updating entry
		private static PreparedStatement sqlUpdateName;
		private static PreparedStatement sqlUpdateAddress;
		private static PreparedStatement sqlUpdatePhone;
		private static PreparedStatement sqlUpdateEmail;
		private static PreparedStatement sqlUpdateIp;
		private static PreparedStatement sqlUpdateImage;

		// references to prepared statements for updating entry
		private PreparedStatement sqlDeleteName;
		private PreparedStatement sqlDeleteAddress;
		private PreparedStatement sqlDeletePhone;
		private PreparedStatement sqlDeleteEmail;
		private PreparedStatement sqlDeleteIp;
		private PreparedStatement sqlDeleteImage;

	// set up PreparedStatements to access database
	public DataAccess() throws Exception{
		// connect to addressbook database
		connection = sqlConnection.connect();

		// locate person
		sqlFind = connection.prepareStatement("SELECT names.personID, firstName, lastName, addressID, houseNameNum, street, city, county, " +
				"phoneID, phoneNumber, emailID, emailAddress, ipID, ipAddress, imageID, imageName, image " +
				"FROM names, address, phonenumbers, emailaddresses, ipaddresses, images " +
				"WHERE lastName = ? AND names.personID = ? AND " +
				"names.personID = address.personID AND names.personID = phonenumbers.personID AND " +
            	"names.personID = emailaddresses.personID AND names.personID = ipaddresses.personID AND names.personID = images.personID");
      
		// Obtain personID for last person inserted in database.
		// [This is a Cloudscape-specific database operation.]
		sqlPersonID = connection.prepareStatement("SELECT MAX(personID) FROM names" );

		// Insert first and last names in table names. 
		// For referential integrity, this must be performed 
		// before sqlInsertAddress, sqlInsertPhone and
		// sqlInsertEmail.
      
		sqlInsertName = connection.prepareStatement("INSERT INTO names (firstName, lastName) VALUES ( ? , ? )" );

		// insert address in table addresses
		sqlInsertAddress = connection.prepareStatement("INSERT INTO address (personID, houseNameNum, street, city, county) VALUES ( ? , ? , ? , ? , ? )" );

		// insert phone number in table phoneNumbers
		sqlInsertPhone = connection.prepareStatement("INSERT INTO phonenumbers (personID, phoneNumber) VALUES ( ? , ? )" );

		// insert email in table emailAddresses
		sqlInsertEmail = connection.prepareStatement("INSERT INTO emailaddresses (personID, emailAddress) VALUES ( ? , ? )" );
		
		// insert ipAddress in table ipAddresses
		sqlInsertIp = connection.prepareStatement("INSERT INTO ipaddresses (personID, ipAddress) VALUES ( ? , ? )" );
      
		// insert image in table emailAddresses
		sqlInsertImage = connection.prepareStatement("INSERT INTO images (personID, imageName, image) VALUES ( ? , ? , ? )" );

		// update first and last names in table names
		sqlUpdateName = connection.prepareStatement("UPDATE names SET firstName = ?, lastName = ? WHERE personID = ?" );

		// update address in table addresses
		sqlUpdateAddress = connection.prepareStatement("UPDATE address SET houseNameNum = ?, street = ?, city = ?, county = ? WHERE addressID = ?" );

		// update phone number in table phoneNumbers
		sqlUpdatePhone = connection.prepareStatement("UPDATE phonenumbers SET phoneNumber = ? WHERE phoneID = ?" );

		// update email in table emailAddresses
		sqlUpdateEmail = connection.prepareStatement("UPDATE emailaddresses SET emailAddress = ? WHERE emailID = ?" );
		
		// update ip in table ip Addresses
		sqlUpdateIp = connection.prepareStatement("UPDATE ipaddresses SET ipAddress = ? WHERE personID = ?" );
      
		// update image in table emailAddresses
		sqlUpdateImage = connection.prepareStatement("UPDATE images SET imageName = ?, image = ? WHERE imageID = ?" );

		// Delete row from table names. This must be executed 
		// after sqlDeleteAddress, sqlDeletePhone and
		// sqlDeleteEmail, because of referential integrity.
		sqlDeleteName = connection.prepareStatement("DELETE FROM names WHERE personID = ?" );

		// delete address from table addresses
		sqlDeleteAddress = connection.prepareStatement("DELETE FROM address WHERE personID = ?" );

		// delete phone number from table phoneNumbers
		sqlDeletePhone = connection.prepareStatement("DELETE FROM phonenumbers WHERE personID = ?" );

		// delete email address from table emailAddresses
		sqlDeleteEmail = connection.prepareStatement("DELETE FROM emailaddresses WHERE personID = ?" );
		
		// delete ip address from table ipAddresses
		sqlDeleteIp = connection.prepareStatement("DELETE FROM ipaddresses WHERE personID = ?" );
      
		// delete image address from table emailAddresses
		sqlDeleteImage = connection.prepareStatement("DELETE FROM images WHERE personID = ?" );
		
	}  // end CloudscapeDataAccess constructor
   
	
	// Locate specified person. Method returns AddressBookEntry
	// containing information.
	public AddressBookEntry findPerson( String lastName, int personID ){
      
		try {
			// set query parameter and execute query
			sqlFind.setString( 1, lastName );
			sqlFind.setInt( 2, personID );
			ResultSet resultSet = sqlFind.executeQuery();
         
			// if no records found, return immediately
			if ( !resultSet.next() ) 
				return null; 
       
			// create new AddressBookEntry
			AddressBookEntry person = new AddressBookEntry( resultSet.getInt( 1 ) );
         
			// set AddressBookEntry properties
			person.setFirstName( resultSet.getString( 2 ) );
			person.setLastName( resultSet.getString( 3 ) );
         
			person.setAddressID( resultSet.getInt( 4 ) );
			person.setHouseNameNum( resultSet.getString( 5 ) );
			person.setStreet( resultSet.getString( 6 ) );
			person.setCity( resultSet.getString( 7 ) );
			person.setCounty( resultSet.getString( 8 ) );

			person.setPhoneID( resultSet.getInt( 9 ) );
			person.setPhoneNumber( resultSet.getString( 10 ) );

			person.setEmailID( resultSet.getInt( 11 ) );
			person.setEmailAddress( resultSet.getString( 12 ) );
         
			person.setIpID( resultSet.getInt( 13 ) );
			person.setIpAddress(resultSet.getString(14));
         
			person.setImageID(resultSet.getInt( 15 ));
			person.setImageName( resultSet.getString( 16 ) );
			person.setImage(resultSet.getBlob(17));
			
			return person;
         
		}
         
		// catch SQLException
		catch ( SQLException sqlException ) {
			return null;
		}
	}  // end method findPerson
   
	// Update an entry. Method returns boolean indicating 
	// success or failure.
	public boolean savePerson( AddressBookEntry person )throws DataAccessException{
		
		// update person in database
		try {
			int result;
         
			// update names table
			sqlUpdateName.setString( 1, person.getFirstName() );
			sqlUpdateName.setString( 2, person.getLastName() );
			sqlUpdateName.setInt( 3, person.getPersonID() );
			result = sqlUpdateName.executeUpdate();

			// if update fails, rollback and discontinue 
			if ( result == 0 ) {
				connection.rollback(); // rollback update
				return false;          // update unsuccessful
			}      
         
			// update addresses table
			sqlUpdateAddress.setString( 1, person.getHouseNameNum());
			sqlUpdateAddress.setString( 2, person.getStreet() );
			sqlUpdateAddress.setString( 3, person.getCity() );
			sqlUpdateAddress.setString( 4, person.getCounty() );

			sqlUpdateAddress.setInt( 5, person.getAddressID() );
			result = sqlUpdateAddress.executeUpdate();
         
			// if update fails, rollback and discontinue 
			if ( result == 0 ) {
				connection.rollback(); // rollback update
				return false;          // update unsuccessful
			}      
         
			// update phoneNumbers table
			sqlUpdatePhone.setString( 1, person.getPhoneNumber() );
			sqlUpdatePhone.setInt( 2, person.getPhoneID() );
			result = sqlUpdatePhone.executeUpdate();
         
			// if update fails, rollback and discontinue 
			if ( result == 0 ) {
				connection.rollback(); // rollback update
				return false;          // update unsuccessful
			}      
         
			// update emailAddresses table
			sqlUpdateEmail.setString( 1, person.getEmailAddress() );
			sqlUpdateEmail.setInt( 2, person.getEmailID() );
			result = sqlUpdateEmail.executeUpdate();
			
			// insert IP  in ipAddresses table
			sqlUpdateIp.setString( 2, person.getIpAddress() );
			sqlUpdateIp.setInt( 1, person.getPersonID() );
			result = sqlUpdateIp.executeUpdate();
						            
			// if insert fails, rollback and discontinue 
			if ( result == 0 ) {
				connection.rollback(); // rollback insert
				result = 0;
				return false;          // insert unsuccessful
			} 
			
			// update image table
			String imgstr = AddressBookEntryFrame.getImageFile();
			if(imgstr != null){
				File imgfile = new File(imgstr);
				try {
					FileInputStream fis = new FileInputStream(imgfile);
					sqlUpdateImage.setString(1, imgfile.getName());
					sqlUpdateImage.setBinaryStream(2, fis, imgfile.length());
					sqlUpdateImage.setInt( 3, person.getImageID() );
				} catch (FileNotFoundException e) {
					System.out.println("couldn't send image");
					e.printStackTrace();
				}
			}
			result = sqlUpdateImage.executeUpdate();

			// if update fails, rollback and discontinue 
			if ( result == 0 ) {
				connection.rollback(); // rollback update
				return false;          // update unsuccessful
			}      
         
			connection.commit();   // commit update
			return true;           // update successful
		}  // end try
      
		// detect problems updating database
		catch ( SQLException sqlException ) {
      
			// rollback transaction
			try {
				connection.rollback(); // rollback update
				return false;          // update unsuccessful
			}
         
			// handle exception rolling back transaction
			catch ( SQLException exception ) {
				throw new DataAccessException( exception );
			}
		}
	}  // end method savePerson

	// Insert new entry. Method returns boolean indicating 
	// success or failure.
	public boolean newPerson( AddressBookEntry person )throws DataAccessException{
      
		// insert person in database
		try {
    	  
			int result;
         
			// insert first and last name in names table
			sqlInsertName.setString( 1, person.getFirstName() );
			sqlInsertName.setString( 2, person.getLastName() );
			result = sqlInsertName.executeUpdate();
          
			// if insert fails, rollback and discontinue 
			if ( result == 0 ) {
				connection.rollback(); // rollback insert
				return false;          // insert unsuccessful
			}      
			String query;
			// determine new personID
			ResultSet resultPersonID = sqlPersonID.executeQuery();
        
			if( resultPersonID.next()){ 
				int personID = resultPersonID.getInt( 1 );
				// insert address in addresses table
				sqlInsertAddress.setInt( 1, personID );
				sqlInsertAddress.setString( 2, person.getHouseNameNum());
				sqlInsertAddress.setString( 3, person.getStreet() );
				sqlInsertAddress.setString( 4, person.getCity() );
				sqlInsertAddress.setString( 5, person.getCounty() );
            

				result = sqlInsertAddress.executeUpdate();
				// if insert fails, rollback and discontinue 
				if ( result == 0 ) {
					connection.rollback(); // rollback insert
					return false;          // insert unsuccessful
				}      

				// insert phone number in phoneNumbers table
				sqlInsertPhone.setInt( 1, personID );
				sqlInsertPhone.setString( 2, person.getPhoneNumber() );
				result = sqlInsertPhone.executeUpdate();
         
				// if insert fails, rollback and discontinue 
				if ( result == 0 ) {
					connection.rollback(); // rollback insert
					return false;          // insert unsuccessful
				}      

				// insert email address in emailAddresses table
				sqlInsertEmail.setInt( 1, personID );
				sqlInsertEmail.setString( 2, person.getEmailAddress() );
				result = sqlInsertEmail.executeUpdate();
            
				// insert IP  in ipAddresses table
				sqlInsertIp.setInt( 1, personID );
				sqlInsertIp.setString( 2, person.getIpAddress() );
				result = sqlInsertIp.executeUpdate();
            
				// if insert fails, rollback and discontinue 
				if ( result == 0 ) {
					connection.rollback(); // rollback insert
					return false;          // insert unsuccessful
				} 
				
				String imgstr = AddressBookEntryFrame.getImageFile();
				if(imgstr == null){
					imgstr = "C:\\Users\\Owner\\Desktop\\Database\\AddressBookWithImages\\src\\images/image1.png"; //CHANGE THIS IF FILE IS MOVED
				}
				File imgfile = new File(imgstr);
				try {
					FileInputStream fis = new FileInputStream(imgfile);
					sqlInsertImage.setInt( 1, personID );
					sqlInsertImage.setString(2, imgfile.getName());
					sqlInsertImage.setBinaryStream(3, fis, imgfile.length());
					result = sqlInsertImage.executeUpdate();
					//sqlInsertImage.setBlob(3, fis);
				} catch (FileNotFoundException e) {
					System.out.println("couldn't send image");
					e.printStackTrace();
				}

				// if insert fails, rollback and discontinue 
				if ( result == 0 ) {
					connection.rollback(); // rollback insert
					return false;          // insert unsuccessful
				}      
       
				connection.commit();   // commit insert
				return true;           // insert successful
			}
         
			else{ 
				return false;
			}
				
		}  // end try
      
		// detect problems updating database
		catch ( SQLException sqlException ) {
			// rollback transaction
			try {
				// connection.commit();
				connection.rollback(); // rollback update
				return false;          // update unsuccessful
			}
         
			// handle exception rolling back transaction
			catch ( SQLException exception ) {
				throw new DataAccessException( exception );
			}
		}
   }  // end method newPerson
      
	// Delete an entry. Method returns boolean indicating 
	// success or failure.
	public boolean deletePerson( AddressBookEntry person )throws DataAccessException{
		// delete a person from database
		try {
			int result;
			
			PreparedStatement sqlPersonID2 = connection.prepareStatement("SELECT MAX(personID) FROM names" );
                  
			// delete address from addresses table
			sqlDeleteAddress.setInt( 1, person.getPersonID() );
			result = sqlDeleteAddress.executeUpdate();

			// if delete fails, rollback and discontinue 
			if ( result == 0 ) {
				connection.rollback(); // rollback delete
				return false;          // delete unsuccessful
			}      

			// delete phone number from phoneNumbers table
			sqlDeletePhone.setInt( 1, person.getPersonID() );
			result = sqlDeletePhone.executeUpdate();
         
			// if delete fails, rollback and discontinue 
			if ( result == 0 ) {
				connection.rollback(); // rollback delete
				return false;          // delete unsuccessful
			}      

			// delete email address from emailAddresses table
			sqlDeleteEmail.setInt( 1, person.getPersonID() );
			result = sqlDeleteEmail.executeUpdate();

			// if delete fails, rollback and discontinue 
			if ( result == 0 ) {
				connection.rollback(); // rollback delete
				return false;          // delete unsuccessful
			}   
			
			// delete ipAddress from ip addresses
			sqlDeleteIp.setInt(1, person.getIpID());
			result = sqlDeleteIp.executeUpdate();
				         
			// if delete fails, rollback and discontinue 
			if ( result == 0 ) {
				connection.rollback(); // rollback delete
				return false;          // delete unsuccessful
			}

			// delete name from names table
			sqlDeleteName.setInt( 1, person.getPersonID() );
			result = sqlDeleteName.executeUpdate();

			// if delete fails, rollback and discontinue 
			if ( result == 0 ) {
				connection.rollback(); // rollback delete
				return false;          // delete unsuccessful
			}      

			connection.commit();   // commit delete
			return true;           // delete successful
		}  // end try
      
		// detect problems updating database
		catch ( SQLException sqlException ) {
			// rollback transaction
			try {
				connection.rollback(); // rollback update
				return false;          // update unsuccessful
			}
         
			// handle exception rolling back transaction
			catch ( SQLException exception ) {
				throw new DataAccessException( exception );
			}
      	}
	}  // end method deletePerson
	
	public static int getMaxPersonID(){
		int retval = 0;
		try {
			PreparedStatement sqlPersonID2 = connection.prepareStatement("SELECT MAX(personID) FROM names" );
			ResultSet rs = sqlPersonID2.executeQuery();
			if( rs.next()){ 
				retval = rs.getInt( 1 );
			}
			
			return retval;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retval;
	}

	// method to close statements and database connection
	public void close(){
		// close database connection
		try {
    	  
			sqlFind.close();
			sqlPersonID.close();
			sqlInsertName.close();
			sqlInsertAddress.close();
			sqlInsertPhone.close();
			sqlInsertEmail.close();
			sqlUpdateName.close();
			sqlUpdateAddress.close();
			sqlUpdatePhone.close();
			sqlUpdateEmail.close();
			sqlDeleteName.close();
			sqlDeleteAddress.close();
			sqlDeletePhone.close();
			sqlDeleteEmail.close();         
			connection.close();
		}  // end try
      
		// detect problems closing statements and connection
		catch ( SQLException sqlException ) {
			sqlException.printStackTrace();  
		}   
	}  // end method close

	// Method to clean up database connection. Provided in case
	// CloudscapeDataAccess object is garbage collected.
	protected void finalize(){
		close(); 
	}
}  // end class CloudscapeDataAccess
