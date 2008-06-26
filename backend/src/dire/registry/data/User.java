package dire.registry.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Query;

@Entity
@NamedQuery(name=User.AUTHENTICATE, query="FROM User WHERE username = :username AND password = :password")
public class User {
	public static final String AUTHENTICATE = "USER_AUTHENTICATE"; 
	
	@Id
	@GeneratedValue
	private int id;
	
	/** authentication parameters: username */
	@Column(unique=true)
	private String username;

	/** authentication parameters: password */
	private String password;
	
	/** public name of the user */
	private String nickName;
	
	public User() { }

	public User(String username, String password, String nickName) {
		super();
		this.username = username;
		this.password = password;
		this.nickName = nickName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public int getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final User other = (User) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	public static User authenticate(EntityManager em, String username, String password) {
		try {
			Query q = em.createNamedQuery(User.AUTHENTICATE);
			q.setParameter("username", username);
			q.setParameter("password", password);
			return (User) q.getSingleResult();
		} catch (Throwable t) {
			return null;
		}
	}
}
