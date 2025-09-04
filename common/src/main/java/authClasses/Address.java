package authClasses;

import java.io.Serializable;

public class Address implements Serializable{
	private static final long serialVersionUID = -4735306842122995280L;
	private String username;
	private String streetName;
	private int streetNumber;
	private String postalCode;
	private String city;
	private String country;
	
	public Address(String username, String streetName, int streetNumber, String postalCode, String city,
			String country) {
		super();
		this.username = username;
		this.streetName = streetName;
		this.streetNumber = streetNumber;
		this.postalCode = postalCode;
		this.city = city;
		this.country = country;
	}
	
	public String getStreetName() {
		return streetName;
	}
	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public int getStreetNumber() {
		return streetNumber;
	}
	public void setStreetNumber(int streetNumber) {
		this.streetNumber = streetNumber;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getUsername() {
		return username;
	}

	@Override
	public String toString() {
		return "Address [username=" + username + ", streetName=" + streetName + ", streetNumber=" + streetNumber
				+ ", postalCode=" + postalCode + ", city=" + city + ", country=" + country + "]";
	}
}
