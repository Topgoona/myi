package myi.AddressBook;

import java.sql.Blob;
import java.sql.SQLException;

import javax.swing.ImageIcon;

public class AddressBookEntry {
	private int personID;
	private int addressID;
	private int phoneID;
	private int emailID;
	private int ipID;
	private int imageID;

	private String firstName = "";
	private String lastName = "";
	private String houseNameNum = "";
	private String street = "";
	private String city = "";
	private String county = "";
	private String phoneNumber = "";
	private String emailAddress = "";
	private String ipAddress = "";
	private String imageName;
	private static ImageIcon image;
	
	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	
	public AddressBookEntry(){
		
	}

	public AddressBookEntry( int id ){
	   personID = id;
	}
	
	
	public static ImageIcon getImage() {
		return image;
	}

	public ImageIcon setImage(Blob blob) {
		//this.image = blob;
		try {
			//ImageIcon imageIcon = new ImageIcon( blob.getBytes( 1L, (int) blob.length() ));
			ImageIcon imageIcon = new ImageIcon(blob.getBytes(1, (int)blob.length()));
			image = imageIcon;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return image;
	}


	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getHouseNameNum() {
		return houseNameNum;
	}

	public void setHouseNameNum(String houseNameNum) {
		this.houseNameNum = houseNameNum;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	public int getPersonID() {
		return personID;
	}

	public void setPersonID(int personID) {
		this.personID = personID;
	}

	public int getAddressID() {
		return addressID;
	}

	public void setAddressID(int addressID) {
		this.addressID = addressID;
	}

	public int getPhoneID() {
		return phoneID;
	}

	public void setPhoneID(int phoneID) {
		this.phoneID = phoneID;
	}

	public int getEmailID() {
		return emailID;
	}

	public void setEmailID(int emailID) {
		this.emailID = emailID;
	}
	
	public int getIpID() {
		return ipID;
	}

	public void setIpID(int ipID) {
		this.ipID = ipID;
	}

	public int getImageID() {
		return imageID;
	}

	public void setImageID(int imageID) {
		this.imageID = imageID;
	}
	
}
