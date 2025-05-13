package it.nextre.corsojava.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import it.nextre.corsojava.entity.Token;
import it.nextre.corsojava.entity.User;

public class TokenJdbcDao extends JdbcDao<Token> {
	private static TokenJdbcDao instance = new TokenJdbcDao();

	public static TokenJdbcDao getInstance() {
		return instance;
	}

	private TokenJdbcDao() {
		super(Token.class, "token");
	}

	public Token getTokenByValue(String value) {
		String query="SELECT value,id,ultimaModifica,userId,scadenza"
				+ " FROM token where value=? order by scadenza desc limit 1";
		PreparedStatement ps=null;
		ResultSet rs=null;
		try (Connection connection=getConnection()){
			ps=connection.prepareStatement(query);
			int i=1;
			ps.setString(i, value);
			rs=ps.executeQuery();
			if(rs.next()) {
				Token token=new Token();
				token.setValue(rs.getString("value"));
				token.setId(rs.getLong("id"));
				token.setUltimaModifica(rs.getTimestamp("ultimaModifica").toInstant());
				token.setDataScadenza(rs.getTimestamp("scadenza").toInstant());
				User user=new User();
				user.setId(rs.getLong("userId"));
				token.setUser(user);
				return token;
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}return null;
		
	}

	public List<Token> getTokenByIdUser(Long id) {
		String query="SELECT value,id,ultimaModifica,userId"
				+ " FROM token where userId=? ";
		PreparedStatement ps=null;
		ResultSet rs=null;
		try (Connection connection=getConnection()){
			ps=connection.prepareStatement(query);
			int i=1;
			ps.setLong(i, id);
			rs=ps.executeQuery();
			ArrayList<Token> tokens=new ArrayList<Token>();
			while(rs.next()) {
				Token token=new Token();
				token.setValue(rs.getString("value"));
				token.setId(rs.getLong("id"));
				token.setUltimaModifica(rs.getTimestamp("ultimaModifica").toInstant());
				User user=new User();
				user.setId(rs.getLong("userId"));
				token.setUser(user);
				tokens.add(token);
			}
			
			return tokens;
		} catch (Exception e) {
			// TODO: handle exception
		}return null;
		
	}

}
