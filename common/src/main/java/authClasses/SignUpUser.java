package authClasses;

import java.io.Serializable;

public class SignUpUser implements Serializable{
	private static final long serialVersionUID = 201961341905987671L;
	private String username;
	private String nameFirst;
	private String nameLast;
	public SignUpUser(String username, String nameFirst, String nameLast) {
		this.username = username;
		this.nameFirst = nameFirst;
		this.nameLast = nameLast;
	}
	public String getNameFirst() {
		return nameFirst;
	}

	public String getNameLast() {
		return nameLast;
	}

	public String getUsername() {
		return username;
	}
}