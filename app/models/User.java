package models;

import play.libs.OAuth.TokenPair;
import siena.Generator;
import siena.Id;
import siena.Model;
import siena.Query;
import siena.Table;
import siena.Index;

@Table("users")
public class User extends Model {
	@Id(Generator.AUTO_INCREMENT)
	public Long id;
    @Index("username_index")
	public String username;
	public String provider;    
	public String token;
	public String secret;


	public User(String username) {
		this.username = username;
	}     
	
	
    public TokenPair getTokenPair() {
        return new TokenPair(token, secret);
    }         

 	public void setTokenPair(TokenPair tokens) {
        this.token = tokens.token;
        this.secret = tokens.secret;
        this.update();
    }

	public static Query<User> all() {
		return Model.all(User.class);
	}

 	public static User findOrCreate(String username) {
        User user = User.all().filter("username", username).get();
        if (user == null) {
            user = new User(username);
			user.insert();
        }
        return user;
    }  
	
	public static User getGuess() {
		return User.findOrCreate("guess");
	}
}
