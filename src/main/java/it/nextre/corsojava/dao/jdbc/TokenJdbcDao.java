package it.nextre.corsojava.dao.jdbc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.nextre.corsojava.entity.Token;
import it.nextre.corsojava.entity.User;
import it.nextre.corsojava.exception.JdbcDaoException;

public class TokenJdbcDao extends JdbcDao<Token> {
	private static TokenJdbcDao instance = new TokenJdbcDao();

	public static TokenJdbcDao getInstance() {
		return instance;
	}

	private TokenJdbcDao() {
		super(Token.class, "token");
	}

	public Token getTokenByValue(String value) {
		String[] columns = { "value"};
		String[] values = { value };
		return findBy_(columns, values);
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
		} catch (FileNotFoundException e) {

			throw new JdbcDaoException("File not found "+e.getMessage(), e);
		} catch (SQLException e) {

			throw new JdbcDaoException("Sql Exception "+e.getMessage(), e);
		} catch (IOException e) {

			throw new JdbcDaoException("Io Exceptionn "+e.getMessage(), e);
		} 
		
	}

}
