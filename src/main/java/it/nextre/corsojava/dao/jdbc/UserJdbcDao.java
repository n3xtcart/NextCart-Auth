package it.nextre.corsojava.dao.jdbc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import it.nextre.corsojava.entity.Group;
import it.nextre.corsojava.entity.User;
import it.nextre.corsojava.exception.JdbcDaoException;

public class UserJdbcDao extends JdbcDao<User> {
	private static UserJdbcDao instance = new UserJdbcDao();

	public static UserJdbcDao getInstance() {
		return instance;
	}

	private UserJdbcDao() {
		super(User.class,"user");
	}

	public Optional<User> findByEmailPassword(String email, String password) {
		String query="SELECT id,nome,cognome,email,password,ultimaModifica,groupId,active"
				+ " FROM user where email=? and password=? ";
		PreparedStatement ps=null;
		ResultSet rs=null;
		try (Connection connection=getConnection()){
			ps=connection.prepareStatement(query);
			int i=1;
			ps.setString(i++, email);
			ps.setString(i++, password);
			rs=ps.executeQuery();
			if(rs.next()) {
				User user=new User();
				user.setCognome(rs.getString("cognome"));
				user.setEmail(rs.getString("email"));
				user.setId(rs.getLong("id"));
				user.setPassword(rs.getString("password"));
				user.setNome(rs.getString("nome"));
				user.setUltimaModifica(rs.getTimestamp("ultimaModifica").toInstant());
				user.setActive(rs.getBoolean("active"));
				Group  g=new Group();
				g.setId(rs.getLong("groupId"));
				user.setGroup(g);
				return Optional.of(user);
			}
			
			
	
		} catch (FileNotFoundException e) {
			throw new JdbcDaoException(e.getMessage(),e);
		} catch (SQLException e) {
			throw new JdbcDaoException(e.getMessage(),e);
		} catch (IOException e) {
			throw new JdbcDaoException(e.getMessage(),e);
		} finally {
			if(ps!=null)
				try {
					ps.close();
				} catch (SQLException e) {
					LOGGER.debug(e.getMessage(),e);
				}
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					LOGGER.debug(e.getMessage(),e);
				}
		} 
		
		return Optional.empty();
		
	}

	public User getByEmail(String email) {
		String query="SELECT id,nome,cognome,email,password,ultimaModifica,groupId,active"
				+ " FROM user where email=?  ";
		PreparedStatement ps=null;
		ResultSet rs=null;
		try (Connection connection=getConnection()){
			ps=connection.prepareStatement(query);
			int i=1;
			ps.setString(i, email);
			rs=ps.executeQuery();
			if(rs.next()) {
				User user=new User();
				user.setCognome(rs.getString("cognome"));
				user.setEmail(rs.getString("email"));
				user.setId(rs.getLong("id"));
				user.setPassword(rs.getString("password"));
				user.setNome(rs.getString("nome"));
				user.setUltimaModifica(rs.getTimestamp("ultimaModifica").toInstant());
				user.setActive(rs.getBoolean("active"));
				Group  g=new Group();
				g.setId(rs.getLong("groupId"));
				user.setGroup(g);
				return user;
			}
			
			
		} catch (FileNotFoundException e) {
			throw new JdbcDaoException(e.getMessage(),e);
		} catch (SQLException e) {
			throw new JdbcDaoException(e.getMessage(),e);
		} catch (IOException e) {
			throw new JdbcDaoException(e.getMessage(),e);
		} finally {
			if(ps!=null)
				try {
					ps.close();
				} catch (SQLException e) {
					LOGGER.debug(e.getMessage(),e);
				}
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					LOGGER.debug(e.getMessage(),e);
				}
		}
		return null;
		
	}

	

}
