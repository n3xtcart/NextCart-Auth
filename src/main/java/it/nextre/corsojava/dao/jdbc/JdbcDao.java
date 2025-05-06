package it.nextre.corsojava.dao.jdbc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.nextre.corsojava.dao.DaoInterface;
import it.nextre.corsojava.entity.Entity;
import it.nextre.corsojava.entity.annotation.Attribute;
import it.nextre.corsojava.exception.JdbcDaoException;

public abstract class JdbcDao<T extends Entity> implements DaoInterface<T> {
	protected static final Logger LOGGER = LogManager.getLogger(JdbcDao.class);
	protected String tableName;
	protected Class<?> clazz;
	
	
	protected JdbcDao(Class<?> clazz, String tableName) {
		this.clazz = clazz;
		this.tableName = tableName;
	}

	public Connection getConnection() throws SQLException, FileNotFoundException, IOException {
		Properties properties = new Properties();
		String user;
		String url;
		String pass;
		try  {

			properties.load(JdbcDao.class.getResourceAsStream("/jdbc.properties"));
			user = properties.getProperty("user");
			pass = properties.getProperty("password");
			url = properties.getProperty("url");
		} catch (FileNotFoundException e) {
			LOGGER.error("Properties file not found", e);
			throw e;
		} catch (IOException e) {
			LOGGER.error("Error reading properties file", e);
			throw e;
		}
		LOGGER.trace("Connecting to database with URL: {}", url);
		LOGGER.debug("Using username: {}", user);
		Connection conn = DriverManager.getConnection(url, user, pass);
		LOGGER.trace("Connection [{}]", conn);
		LOGGER.debug("Connection established successfully.");
		return conn;
	}

	@Override
	public void delete(Long id) {
		String query = "DELETE FROM "+tableName+" WHERE id=?";
		try (Connection connection = getConnection()) {
			PreparedStatement ps = connection.prepareStatement(query);
			int i = 1;
			ps.setLong(i++, id);
			ps.executeUpdate();
		} catch (FileNotFoundException e) {
			throw new JdbcDaoException("File not found", e);
		} catch (SQLException e) {
			throw new JdbcDaoException("SQL error", e);
		} catch (IOException e) {
			throw new JdbcDaoException("IO error", e);
		}

	}

	@Override
	public T getById(Long id) {
		Field[] fields = clazz.getDeclaredFields();
		StringBuilder sb = new StringBuilder();
		for (Field field : fields) {
			Attribute annotations = field.getAnnotation(Attribute.class);
			if (annotations != null) {
				sb.append(annotations.name().equals("group")?"groupId":annotations.name()).append(",");
			}
		}
		String query = "SELECT "+sb.deleteCharAt(sb.length()-1)+" FROM "+tableName+" WHERE id=?";
		System.out.println(query);
		try (Connection connection = getConnection()) {
			PreparedStatement ps = connection.prepareStatement(query);
			ResultSet rs = null;
			int i = 1;
			ps.setLong(i++, id);
			rs=ps.executeQuery();
			if (rs.next()) {
				T item = (T) clazz.getDeclaredConstructor().newInstance();
				for (Field field : fields) {
					Attribute annotations = field.getAnnotation(Attribute.class);
					if (annotations != null) {

						String methodName="set"+field.getName().substring(0, 1).toUpperCase()+field.getName().substring(1);
						
		                var setter = clazz.getMethod(methodName, annotations.className());
		                
		                
							Method method=ResultSet.class.getMethod("get"+annotations.type().substring(0,1).toUpperCase()+annotations.type().substring(1),String.class);
							
							Object me=method.invoke(rs, annotations.name().equals("group")?"groupId":annotations.name());
							if(annotations.className()!=String.class) {
								Entity entity=new Entity();
								entity.setId( (long) Integer.parseInt((String) me));
								setter.invoke(item, entity);
							}else	setter.invoke(item, me);
						

		               

}
				}
				return item;
			}
		} catch (FileNotFoundException e) {
			throw new JdbcDaoException("File not found", e);
		} catch (SQLException e) {
			throw new JdbcDaoException("SQL error", e);
		} catch (IOException e) {
			throw new JdbcDaoException("IO error", e);
		} catch (InstantiationException e) {
			throw new JdbcDaoException("Instantiation error", e);
		} catch (IllegalAccessException e) {
            
			throw new JdbcDaoException("Illegal access error", e);
		}catch (InvocationTargetException e) {
			throw new JdbcDaoException("Invocation target error", e);
		} catch (NoSuchMethodException e) {
			throw new JdbcDaoException("No such method error", e);
		} catch (SecurityException e) {
			throw new JdbcDaoException("Security error", e);
		}
		return null;

	}

	@Override
	public List<T> getAll() {
		Field[] fields = clazz.getDeclaredFields();
		StringBuilder sb = new StringBuilder();
		for (Field field : fields) {
			Attribute annotations = field.getAnnotation(Attribute.class);
			if (annotations != null) {
				sb.append(annotations.name().equals("group")?"groupId":annotations.name()).append(",");
			}
		}
		String query = "SELECT "+sb.deleteCharAt(sb.length()-1)+" FROM "+tableName;
		System.out.println(query);
		try (Connection connection = getConnection()) {
			PreparedStatement ps = connection.prepareStatement(query);
			ResultSet rs = null;
			int i = 1;
			rs=ps.executeQuery();
			ArrayList<T> list=new ArrayList<>();
			while (rs.next()) {
				T item = (T) clazz.getDeclaredConstructor().newInstance();
				for (Field field : fields) {
					Attribute annotations = field.getAnnotation(Attribute.class);
					if (annotations != null) {

						String methodName="set"+field.getName().substring(0, 1).toUpperCase()+field.getName().substring(1);
						
		                var setter = clazz.getMethod(methodName, annotations.className());
		                
		                
							Method method=ResultSet.class.getMethod("get"+annotations.type().substring(0,1).toUpperCase()+annotations.type().substring(1),String.class);
							
							Object me=method.invoke(rs, annotations.name().equals("group")?"groupId":annotations.name());
							if(annotations.className()!=String.class) {
								Entity entity=new Entity();
								entity.setId( (long) Integer.parseInt((String) me));
								setter.invoke(item, entity);
							}else	setter.invoke(item, me);
						

		               

}
				}
				list.add(item);
			}
			return list;
		} catch (FileNotFoundException e) {
			throw new JdbcDaoException("File not found", e);
		} catch (SQLException e) {
			throw new JdbcDaoException("SQL error", e);
		} catch (IOException e) {
			throw new JdbcDaoException("IO error", e);
		} catch (InstantiationException e) {
			throw new JdbcDaoException("Instantiation error", e);
		} catch (IllegalAccessException e) {
            
			throw new JdbcDaoException("Illegal access error", e);
		}catch (InvocationTargetException e) {
			throw new JdbcDaoException("Invocation target error", e);
		} catch (NoSuchMethodException e) {
			throw new JdbcDaoException("No such method error", e);
		} catch (SecurityException e) {
			throw new JdbcDaoException("Security error", e);
		}

	}

	@Override
	public abstract void update(Long id, T item);

	@Override
	public void add(T item) {
		Field[] fields = clazz.getDeclaredFields();
		StringBuilder sb = new StringBuilder(" VALUES (id,");
		StringBuilder struc = new StringBuilder(" (id,");
		ArrayList<Attribute> value=new ArrayList<>();
		for (Field field : fields) {
			System.out.println(field.getName());
			Attribute annotations = field.getAnnotation(Attribute.class);
			if (annotations != null) {
				sb.append("?").append(",");
                value.add(annotations);
                struc.append(annotations.name().equals("group")?"groupId":annotations.name()).append(",");
			}
		}
		struc.deleteCharAt(struc.length()-1).append(")");
		System.out.println(sb.toString());
		String query = "INSERT INTO "+tableName+struc.toString()+sb.toString().substring(0, sb.length()-1)+")";
		System.out.println(query);
		try (Connection connection = getConnection()) {
			PreparedStatement ps = connection.prepareStatement(query);
			int i = 1;
			Object value1 = null;
			for(Attribute val:value) {
				
					value1 = clazz.getMethod("get"+val.name().substring(0, 1).toUpperCase()+val.name().substring(1)).invoke(item);
					Method method = null;
					try {
						method = PreparedStatement.class.getMethod("set"+val.type().substring(0,1)+val.type().substring(1),int.class,val.className());
						if(value1 instanceof Entity e) {
							value1=e.getId();
						}
						method.invoke(ps, i++, value1);
					 
					} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException
							| SecurityException e) {
	                throw new JdbcDaoException("Error invoking method", e);
				
					}
				
				
			}
		
			ps.executeUpdate();
		} catch (FileNotFoundException e) {
			throw new JdbcDaoException("File not found", e);
		} catch (SQLException e) {
			throw new JdbcDaoException("SQL error", e);
		} catch (IOException e) {
			throw new JdbcDaoException("IO error", e);
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
		
	

	

}
