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
import java.util.Arrays;
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
			LOGGER.error("Properties file not found"+e.getMessage(), e);
			throw e;
		} catch (IOException e) {
			LOGGER.error("Error reading properties file"+e.getMessage(), e);
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
			throw new JdbcDaoException("File not found"+e.getMessage(), e);
		} catch (SQLException e) {
			throw new JdbcDaoException("SQL error"+e.getMessage(), e);
		} catch (IOException e) {
			throw new JdbcDaoException("IO error"+e.getMessage(), e);
		}

	}

	@Override
	public T getById(Long id) {       
		List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));

		fields.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));
		StringBuilder sb = new StringBuilder();
		for (Field field : fields) {
			Attribute annotations = field.getAnnotation(Attribute.class);
			if (annotations != null) {
				sb.append(annotations.colName()).append(",");
			}
		}
		String query = "SELECT "+sb.deleteCharAt(sb.length()-1)+" FROM "+tableName+" WHERE id=?";
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
							
							Object me=method.invoke(rs, annotations.colName());
							if(annotations.className().getSuperclass()==Entity.class) {
								var entity=annotations.className().getDeclaredConstructor().newInstance();
								Method setId=annotations.className().getMethod("setId",Long.class);
								setId.invoke(entity,  me);
								setter.invoke(item, entity);
							}else	setter.invoke(item, me);
						

		               

}
				}
				return item;
			}
		} catch (FileNotFoundException e) {
			throw new JdbcDaoException("File not found"+e.getMessage(), e);
		} catch (SQLException e) {
			throw new JdbcDaoException("SQL error" + e.getMessage(), e);
		} catch (IOException e) {
			throw new JdbcDaoException("IO error"+e.getMessage(), e);
		} catch (InstantiationException e) {
			throw new JdbcDaoException("Instantiation error"+e.getMessage(), e);
		} catch (IllegalAccessException e) {
            
			throw new JdbcDaoException("Illegal access error"+e.getMessage(), e);
		}catch (InvocationTargetException e) {
			throw new JdbcDaoException("Invocation target error"+e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			throw new JdbcDaoException("No such method error " + e.getMessage(), e);
		} catch (SecurityException e) {
			throw new JdbcDaoException("Security error"+e.getMessage(), e);
		}
		return null;

	}

	@Override
	public List<T> getAll() {
		List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
		fields.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));
		StringBuilder sb = new StringBuilder();
		for (Field field : fields) {
			Attribute annotations = field.getAnnotation(Attribute.class);
			if (annotations != null) {
				sb.append(annotations.colName()).append(",");
			}
		}
		String query = "SELECT "+sb.deleteCharAt(sb.length()-1)+" FROM "+tableName;
		
		try (Connection connection = getConnection()) {
			PreparedStatement ps = connection.prepareStatement(query);
			ResultSet rs = null;
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
							
							Object me=method.invoke(rs,annotations.colName());
							if(annotations.className().getSuperclass()==Entity.class) {
								var entity=annotations.className().getDeclaredConstructor().newInstance();
								Method setId=annotations.className().getMethod("setId",Long.class);
								setId.invoke(entity,  me);
								setter.invoke(item, entity);
							}else	setter.invoke(item, me);
						

		               

}
				}
				list.add(item);
			}
			return list;
		} catch (FileNotFoundException e) {
			throw new JdbcDaoException("File not found"+e.getMessage(), e);
		} catch (SQLException e) {
			throw new JdbcDaoException("SQL error"+e.getMessage(), e);
		} catch (IOException e) {
			throw new JdbcDaoException("IO error"+e.getMessage(), e);
		} catch (InstantiationException e) {
			throw new JdbcDaoException("Instantiation error"+e.getMessage(), e);
		} catch (IllegalAccessException e) {
            
			throw new JdbcDaoException("Illegal access error"+e.getMessage(), e);
		}catch (InvocationTargetException e) {
			throw new JdbcDaoException("Invocation target error"+e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			throw new JdbcDaoException("No such method error"+e.getMessage(), e);
		} catch (SecurityException e) {
			throw new JdbcDaoException("Security error"+e.getMessage(), e);
		}

	}

	@Override
	public  void update(Long id, T item) {

		List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
		StringBuilder sb = new StringBuilder(" SET ");
		StringBuilder struc = new StringBuilder();
		ArrayList<Attribute> value=new ArrayList<>();
		for (Field field : fields) {
			Attribute annotations = field.getAnnotation(Attribute.class);
			if (annotations != null) {
				sb.append(annotations.colName()+"=").append("?").append(",");
                value.add(annotations);
                struc.append(annotations.colName()).append(",");
			}
		}
		sb.deleteCharAt(sb.length()-1);
		String query = "UPDATE "+tableName+sb.toString();
		try (Connection connection = getConnection()) {
			PreparedStatement ps = connection.prepareStatement(query);
			int i = 1;
			Object value1 = null;
			for(Attribute val:value) {
				
					value1 = clazz.getMethod("get"+val.fieldName().substring(0, 1).toUpperCase()+val.fieldName().substring(1)).invoke(item);
					if(value1 instanceof Entity e ) {
						value1=e.getId();
					}
					Method method = null;
						method = PreparedStatement.class.getMethod("set"+val.type().toUpperCase().substring(0,1)+val.type().substring(1),int.class,val.colClass());
						method.invoke(ps, i++, value1);
					 
					
				
				
			}
		
			ps.executeUpdate();
		} catch (FileNotFoundException e) {
			throw new JdbcDaoException("File not found "+e.getMessage(), e);
		} catch (SQLException e) {
			throw new JdbcDaoException("SQL error "+e.getMessage(), e);
		} catch (IOException e) {
			throw new JdbcDaoException("IO error "+e.getMessage(), e);
		} catch (IllegalAccessException e1) {
			throw new JdbcDaoException("Illegal access error "+e1.getMessage(), e1);
			
		} catch (InvocationTargetException e1) {
			throw new JdbcDaoException("Invocation target error "+e1.getMessage(), e1);
			
		} catch (IllegalArgumentException e) {
			throw new JdbcDaoException("Illegal argument error "+e.getMessage(), e);
		} catch (SecurityException e) {
			throw new JdbcDaoException("Security error "+e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			throw new JdbcDaoException("No such method error "+e.getMessage(), e);
		} catch (NullPointerException e) {
			LOGGER.error("Null pointer exception: "+e.getMessage(), e);
		}

	
	}

	@Override
	public Long add(T item) {
		Field[] fields = clazz.getDeclaredFields();
		StringBuilder sb = new StringBuilder(" VALUES (");
		StringBuilder struc = new StringBuilder(" (");
		ArrayList<Attribute> value=new ArrayList<>();
		for (Field field : fields) {
			Attribute annotations = field.getAnnotation(Attribute.class);
			if (annotations != null) {
				sb.append("?").append(",");
                value.add(annotations);
                struc.append(annotations.colName()).append(",");
			}
		}
		struc.deleteCharAt(struc.length()-1).append(")");
		String query = "INSERT INTO "+tableName+struc.toString()+sb.toString().substring(0, sb.length()-1)+")";
		
		ResultSet rs = null;
		PreparedStatement ps = null;
		try (Connection connection = getConnection()) {
			ps = connection.prepareStatement(query,PreparedStatement.RETURN_GENERATED_KEYS);
			int i = 1;
			Object value1 = null;
			for(Attribute val:value) {
				
					value1 = clazz.getMethod("get"+val.fieldName().substring(0, 1).toUpperCase()+val.fieldName().substring(1)).invoke(item);
					Method method = null;
						if(value1 instanceof Entity e) {
							value1=e.getId();
						
						}
						method = PreparedStatement.class.getMethod("set"+val.type().substring(0,1).toUpperCase()+val.type().substring(1),int.class,val.colClass());
						
						method.invoke(ps, i++, value1);
					 
					
				
				
			}
		
			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			if (rs.next()) {
				Long id = rs.getLong(1);
				item.setId(id);
				return id;
			}
		} catch (IOException e) {
			throw new JdbcDaoException("IO error "+e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new JdbcDaoException("Illegal access error "+e.getMessage(), e);
			
		} catch (InvocationTargetException e) {
			throw new JdbcDaoException("Invocation target error "+e.getMessage(), e);
			
		} catch (IllegalArgumentException e) {
			throw new JdbcDaoException("Illegal argument error "+e.getMessage(), e);
		} catch (SecurityException e) {
			throw new JdbcDaoException("Security error "+e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			throw new JdbcDaoException("No such method error "+e.getMessage(), e);
		} catch (NullPointerException e) {
throw new JdbcDaoException("Null pointer exception "+e.getMessage(), e);
		} catch (Exception e) {
			LOGGER.error("Error: "+e.getMessage(), e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				LOGGER.error("Error closing ResultSet: "+e.getMessage(), e);
			}
		} 
		return null;

	}
		
	

	

}
