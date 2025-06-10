package it.nextre.corsojava.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import io.agroal.api.AgroalDataSource;
import it.nextre.corsojava.entity.Token;
import it.nextre.corsojava.entity.User;
import it.nextre.corsojava.exception.JdbcDaoException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TokenJdbcDao extends JdbcDao<Token> {

	@Inject
	public  TokenJdbcDao(AgroalDataSource dataSource) {
		super(Token.class, "token", dataSource);
	}
	
	public TokenJdbcDao() {
		super(Token.class, "token", null);
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
		} catch (SQLException e) {

			throw new JdbcDaoException("Sql Exception "+e.getMessage(), e);
		} 
		
	}

}
