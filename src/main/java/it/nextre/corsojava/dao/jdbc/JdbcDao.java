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
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.jboss.logging.Logger;

import it.nextre.aut.pagination.PagedResult;
import it.nextre.corsojava.dao.memory.DaoInterface;
import it.nextre.corsojava.entity.Entity;
import it.nextre.corsojava.entity.annotation.Attribute;
import it.nextre.corsojava.entity.annotation.ManyToMany;
import it.nextre.corsojava.entity.annotation.OneToMeny;
import it.nextre.corsojava.entity.annotation.OneToOne;
import it.nextre.corsojava.exception.JdbcDaoException;

public abstract class JdbcDao<T extends Entity> implements DaoInterface<T> {
    protected static final Logger LOGGER = Logger.getLogger(JdbcDao.class);
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
        try {

            properties.load(JdbcDao.class.getResourceAsStream("/jdbc.properties"));
            user = properties.getProperty("user");
            pass = properties.getProperty("password");
            url = properties.getProperty("url");
        } catch (FileNotFoundException e) {
            LOGGER.error("Properties file not found" + e.getMessage(), e);
            throw e;
        } catch (IOException e) {
            LOGGER.error("Error reading properties file" + e.getMessage(), e);
            throw e;
        }
        LOGGER.tracef("Connecting to database with URL: %d", url);
        LOGGER.debugf("Using username: %d", user);
        Connection conn = DriverManager.getConnection(url, user, pass);
        LOGGER.tracef("Connection %d", conn);
        LOGGER.debug("Connection established successfully.");
        return conn;
    }
    
    
    protected T findBy_(String[] cols,String[] val) {
    	StringBuilder condition=new StringBuilder();
    	for(int i=0;i<cols.length;i++) {
    		condition.append(cols[i]).append(" = '").append(val[i]).append("' ").append("and ");
    	}
    	condition.delete(condition.length()-5,condition.length());
        List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));

        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        StringBuilder sb = new StringBuilder();
        
        for (Field field : fields) {
            Attribute annotation = field.getAnnotation(Attribute.class);
            if (annotation != null) {
                sb.append(annotation.colName()).append(",");
            }
        }
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT " + sb.deleteCharAt(sb.length() - 1) + " FROM " + tableName + " WHERE "+condition.toString();
        System.out.println(query);
        try (Connection connection = getConnection()) {
            ps = connection.prepareStatement(query);
            rs = null;
            rs = ps.executeQuery();
            if (rs.next()) {
            	return(T) createObject(fields, connection, rs,clazz);
            }
        } catch (FileNotFoundException e) {
            throw new JdbcDaoException("File not found" + e.getMessage(), e);
        } catch (SQLException e) {
            throw new JdbcDaoException("SQL error" + e.getMessage(), e);
        } catch (IOException e) {
            throw new JdbcDaoException("IO error" + e.getMessage(), e);
        } catch (InstantiationException e) {
            throw new JdbcDaoException("Instantiation error" + e.getMessage(), e);
        } catch (IllegalAccessException e) {

            throw new JdbcDaoException("Illegal access error" + e.getMessage(), e);
        } catch (InvocationTargetException e) {
            throw new JdbcDaoException("Invocation target error" + e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            throw new JdbcDaoException("No such method error " + e.getMessage(), e);
        } catch (SecurityException e) {
            throw new JdbcDaoException("Security error" + e.getMessage(), e);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    LOGGER.debug("errore in chiusura ps");
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    LOGGER.debug("errore in chiusura rs");
                }
            }
        }

        return null;

    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM " + tableName + " WHERE id=?";
        PreparedStatement ps = null;
        try (Connection connection = getConnection()) {
            ps = connection.prepareStatement(query);
            int i = 1;
            ps.setLong(i++, id);
            ps.executeUpdate();
            List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));

            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            
            for (Field field : fields) {
                ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
                if (manyToMany == null) continue;
                String delete = "DELETE FROM " + manyToMany.supportTable() +
                		" WHERE " + manyToMany.supportJoinColumn() + "=" + id;
				ps = connection.prepareStatement(delete);
				ps.executeUpdate();
               
            }
            
        } catch (FileNotFoundException e) {
            throw new JdbcDaoException("File not found" + e.getMessage(), e);
        } catch (SQLException e) {
            throw new JdbcDaoException("SQL error" + e.getMessage(), e);
        } catch (IOException e) {
            throw new JdbcDaoException("IO error" + e.getMessage(), e);
        }
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                LOGGER.debug("errore in chiusura ps");
            }
        }

    }

    //TODO gestire active di user
    @Override
    public T getById(Long id) {
        List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));

        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        StringBuilder sb = new StringBuilder();
        
        for (Field field : fields) {
            Attribute annotation = field.getAnnotation(Attribute.class);
            if (annotation != null) {
                sb.append(annotation.colName()).append(",");
            }
        }
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT " + sb.deleteCharAt(sb.length() - 1) + " FROM " + tableName + " WHERE id=?";
        try (Connection connection = getConnection()) {
            ps = connection.prepareStatement(query);
            rs = null;
            int i = 1;
            ps.setLong(i++, id);
            rs = ps.executeQuery();
            if (rs.next()) {
            	return(T) createObject(fields, connection, rs,clazz);
            }
        } catch (FileNotFoundException e) {
            throw new JdbcDaoException("File not found" + e.getMessage(), e);
        } catch (SQLException e) {
            throw new JdbcDaoException("SQL error" + e.getMessage(), e);
        } catch (IOException e) {
            throw new JdbcDaoException("IO error" + e.getMessage(), e);
        } catch (InstantiationException e) {
            throw new JdbcDaoException("Instantiation error" + e.getMessage(), e);
        } catch (IllegalAccessException e) {

            throw new JdbcDaoException("Illegal access error" + e.getMessage(), e);
        } catch (InvocationTargetException e) {
            throw new JdbcDaoException("Invocation target error" + e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            throw new JdbcDaoException("No such method error " + e.getMessage(), e);
        } catch (SecurityException e) {
            throw new JdbcDaoException("Security error" + e.getMessage(), e);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    LOGGER.debug("errore in chiusura ps");
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    LOGGER.debug("errore in chiusura rs");
                }
            }
        }

        return null;

    }


    
    public PagedResult<T> getAllPag(int pag,int pagSize) {
    	  List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));

          fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
          StringBuilder sb = new StringBuilder();
          
          for (Field field : fields) {
              Attribute annotation = field.getAnnotation(Attribute.class);
              if (annotation != null) {
                  sb.append(annotation.colName()).append(",");
              }
          }
        String query = "SELECT(select count(*)  from "+tableName+"  ) as totale , " + sb.deleteCharAt(sb.length() - 1) + " FROM " + tableName+
        		" order by id limit "+pagSize+" offset "+pag*pagSize;

        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Connection connection = getConnection()) {
            ps = connection.prepareStatement(query);
            rs = ps.executeQuery();
            Integer totalElement=null;
            ArrayList<T> list = new ArrayList<>();
            while (rs.next()) {
            	
            	if(totalElement==null) totalElement=rs.getInt("totale");
            	list.add((T) createObject(fields, connection, rs, clazz));
            }
            PagedResult<T> pagedResult=new PagedResult<T>(list,  totalElement,pagSize);
            return pagedResult;
        } catch (FileNotFoundException e) {
            throw new JdbcDaoException("File not found" + e.getMessage(), e);
        } catch (SQLException e) {
            throw new JdbcDaoException("SQL error" + e.getMessage(), e);
        } catch (IOException e) {
            throw new JdbcDaoException("IO error" + e.getMessage(), e);
        } catch (InstantiationException e) {
            throw new JdbcDaoException("Instantiation error" + e.getMessage(), e);
        } catch (IllegalAccessException e) {

            throw new JdbcDaoException("Illegal access error" + e.getMessage(), e);
        } catch (InvocationTargetException e) {
            throw new JdbcDaoException("Invocation target error" + e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            throw new JdbcDaoException("No such method error" + e.getMessage(), e);
        } catch (SecurityException e) {
            throw new JdbcDaoException("Security error" + e.getMessage(), e);
        } finally {

            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    LOGGER.debug("errore in chiusura ps");
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    LOGGER.debug("errore in chiusura rs");
                }
            }
        }


    }
    
    
    @Override
    public List<T> getAll() {
  	  List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));

      fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
      StringBuilder sb = new StringBuilder();
      
      for (Field field : fields) {
          Attribute annotation = field.getAnnotation(Attribute.class);
          if (annotation != null) {
              sb.append(annotation.colName()).append(",");
          }
      }
        String query = "SELECT " + sb.deleteCharAt(sb.length() - 1) + " FROM " + tableName;

        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Connection connection = getConnection()) {
            ps = connection.prepareStatement(query);
            rs = ps.executeQuery();
            ArrayList<T> list = new ArrayList<>();
            while (rs.next()) {
            	list.add((T)createObject(fields, connection, rs, clazz));
               
            }
            return list;
        } catch (FileNotFoundException e) {
            throw new JdbcDaoException("File not found" + e.getMessage(), e);
        } catch (SQLException e) {
            throw new JdbcDaoException("SQL error" + e.getMessage(), e);
        } catch (IOException e) {
            throw new JdbcDaoException("IO error" + e.getMessage(), e);
        } catch (InstantiationException e) {
            throw new JdbcDaoException("Instantiation error" + e.getMessage(), e);
        } catch (IllegalAccessException e) {

            throw new JdbcDaoException("Illegal access error" + e.getMessage(), e);
        } catch (InvocationTargetException e) {
            throw new JdbcDaoException("Invocation target error" + e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            throw new JdbcDaoException("No such method error" + e.getMessage(), e);
        } catch (SecurityException e) {
            throw new JdbcDaoException("Security error" + e.getMessage(), e);
        } finally {

            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    LOGGER.debug("errore in chiusura ps");
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    LOGGER.debug("errore in chiusura rs");
                }
            }
        }


    }
    
    
    

    @Override
    public void update(Long id, T item) {
    	List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));

        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        
        StringBuilder sb = new StringBuilder(" SET ");
        StringBuilder struc = new StringBuilder();
        ArrayList<Attribute> value = new ArrayList<>();
        for (Field field : fields) {
            ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
            Attribute annotations = field.getAnnotation(Attribute.class);
            if (annotations != null && !annotations.auto()) {
                sb.append(annotations.colName() + "=").append("?").append(",");
                value.add(annotations);
                struc.append(annotations.colName()).append(",");
            }else if(manyToMany != null) {
            	String delete = "DELETE FROM " + manyToMany.supportTable() + " WHERE " + manyToMany.supportJoinColumn() + "=" + ((Entity) item).getId();
            	PreparedStatement ps = null;
            	try (Connection connection = getConnection()) {
					ps = connection.prepareStatement(delete);
					ps.executeUpdate();
					Object invoke = null;
					try {
						invoke = clazz.getMethod("get" + field.getName().substring(0, 1).toUpperCase() +field.getName().substring(1)).invoke(item);
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if(invoke instanceof Collection<?> c) {
						if(c.isEmpty()) continue;
						for(Object o:c) {
							if(o instanceof Entity e) {
								if(e.getId()==null) continue;
								String checkQuery = "SELECT id FROM " + manyToMany.joinTable() + " WHERE id = " + e.getId();
								ps = connection.prepareStatement(checkQuery);
								ResultSet rs = ps.executeQuery();
								if(rs.next()) {
									String add=	"insert into " + manyToMany.supportTable() + " (" + manyToMany.joinColumn() 
									+ "," + manyToMany.supportJoinColumn() + ") VALUES ("+e.getId()+","+((Entity) item).getId()+")";
						
										ps = connection.prepareStatement(add);
										ps.executeUpdate();
								
										
									
								}
								
								
							
						}
					}
					}
				} catch (SQLException e) {
					throw new JdbcDaoException("SQL error " + e.getMessage(), e);
				} catch (IOException e) {
					throw new JdbcDaoException("IO error " + e.getMessage(), e);
				}
            	
			}
        }
        sb.deleteCharAt(sb.length() - 1);
        String query = "UPDATE " + tableName + sb.toString() + " where id=" + id;
        PreparedStatement ps = null;
        try (Connection connection = getConnection()) {
            ps = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            int i = 1;
            Object value1 = null;
            for (Attribute val : value) {
                if (val.auto()) continue;
                value1 = clazz
                        .getMethod("get" + val.fieldName().substring(0, 1).toUpperCase() + val.fieldName().substring(1))
                        .invoke(item);
                Method method = null;
                if (value1 instanceof Entity e) {
                    value1 = e.getId();

                }
                if (value1 instanceof Instant in) {
                    value1 = Timestamp.valueOf(LocalDateTime.ofInstant(in, ZoneId.systemDefault()));

                }
                method = PreparedStatement.class.getMethod(
                        "set" + val.type().substring(0, 1).toUpperCase() + val.type().substring(1), int.class,
                        val.colClass());

                method.invoke(ps, i++, value1);

            }

            ps.executeUpdate();


        } catch (FileNotFoundException e) {
            throw new JdbcDaoException("File not found " + e.getMessage(), e);
        } catch (SQLException e) {
            throw new JdbcDaoException("SQL error " + e.getMessage(), e);
        } catch (IOException e) {
            throw new JdbcDaoException("IO error " + e.getMessage(), e);
        } catch (IllegalAccessException e1) {
            throw new JdbcDaoException("Illegal access error " + e1.getMessage(), e1);

        } catch (InvocationTargetException e1) {
            throw new JdbcDaoException("Invocation target error " + e1.getMessage(), e1);

        } catch (IllegalArgumentException e) {
            throw new JdbcDaoException("Illegal argument error " + e.getMessage(), e);
        } catch (SecurityException e) {
            throw new JdbcDaoException("Security error " + e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            throw new JdbcDaoException("No such method error " + e.getMessage(), e);
        } catch (NullPointerException e) {
            throw new JdbcDaoException("Null pointer exception: " + e.getMessage(), e);
        } finally {

            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    LOGGER.debug("errore in chiusura ps");
                }
            }
        }


    }

    @Override
    public Long add(T item) {
    	 List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));

         fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
         
        
        StringBuilder sb = new StringBuilder(" VALUES (");
        StringBuilder struc = new StringBuilder(" (");
        ArrayList<Attribute> value = new ArrayList<>();
        List<Field> manyToManyFields = new ArrayList<>();
        for (Field field : fields) {
            Attribute annotations = field.getAnnotation(Attribute.class);
           if(field.getAnnotation(ManyToMany.class) != null) manyToManyFields.add(field);
            try {
            	Object invoke = clazz.getMethod("get" + field.getName().substring(0, 1).toUpperCase() +field.getName().substring(1)).invoke(item);
				if (annotations != null && !annotations.auto() &&  invoke != null ) {
					if(invoke instanceof Entity e) {
						if(e.getId()==null) {continue;}
					}
				    sb.append("?").append(",");
				    value.add(annotations);
				    struc.append(annotations.colName()).append(",");
				}
				
            } catch (IllegalAccessException e) {
            	// TODO Auto-generated catch block
            	e.printStackTrace();
            } catch (IllegalArgumentException e) {
            	// TODO Auto-generated catch block
            	e.printStackTrace();
            } catch (InvocationTargetException e) {
            	// TODO Auto-generated catch block
            	e.printStackTrace();
            } catch (NoSuchMethodException e) {
            	// TODO Auto-generated catch block
            	e.printStackTrace();
            } catch (SecurityException e) {
            	// TODO Auto-generated catch block
            	e.printStackTrace();
            }
        }
        struc.deleteCharAt(struc.length() - 1).append(")");
        String query = "INSERT INTO " + tableName + struc.toString() + sb.toString().substring(0, sb.length() - 1)
                + ")";

        ResultSet rs = null;
        PreparedStatement ps = null;
        try (Connection connection = getConnection()) {
            ps = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            //return addObject(fields, connection, ps, item, clazz);
            int i = 1;
            Object value1 = null;
            for (Attribute val : value) {
                if (val.auto()) continue;
                value1 = clazz
                        .getMethod("get" + val.fieldName().substring(0, 1).toUpperCase() + val.fieldName().substring(1))
                        .invoke(item);
                Method method = null;
                if (value1 instanceof Entity e) {
                    value1 = e.getId();

                }
                if (value1 instanceof Instant in) {
                    value1 = Timestamp.valueOf(LocalDateTime.ofInstant(in, ZoneId.systemDefault()));

                }
                method = PreparedStatement.class.getMethod(
                        "set" + val.type().substring(0, 1).toUpperCase() + val.type().substring(1), int.class,
                        val.colClass());

                method.invoke(ps, i++, value1);

            }

            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                Long id = rs.getLong(1);
                item.setId(id);
                for(Field field:manyToManyFields) {
                    ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
                    Object invoke = clazz.getMethod("get" + field.getName().substring(0, 1).toUpperCase() +field.getName().substring(1)).invoke(item);
    				
					if(invoke instanceof Collection<?> c) {
						if(c.isEmpty()) continue;
						for(Object o:c) {
							if(o instanceof Entity e) {
								if(e.getId()==null) continue;
								String select= "SELECT id FROM " + manyToMany.joinTable() + " WHERE id = " + e.getId();
								
									ps = connection.prepareStatement(select);
									rs = ps.executeQuery();
									if(rs.next()) {
										String queryString="INSERT INTO " + manyToMany.supportTable() + " (" + manyToMany.joinColumn() + "," + manyToMany.supportJoinColumn() + ") VALUES ("+e.getId()+","+((Entity) item).getId()+")";
										
											ps = connection.prepareStatement(queryString);
											ps.executeUpdate();
											
										
									}
								
							
						}
					}
					}
				
                }
                return id;
            }
        } catch (IOException e) {
            throw new JdbcDaoException("IO error " + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new JdbcDaoException("Illegal access error " + e.getMessage(), e);

        } catch (InvocationTargetException e) {
            throw new JdbcDaoException("Invocation target error " + e.getMessage(), e);

        } catch (IllegalArgumentException e) {
            throw new JdbcDaoException("Illegal argument error " + e.getMessage(), e);
        } catch (SecurityException e) {
            throw new JdbcDaoException("Security error " + e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            throw new JdbcDaoException("No such method error " + e.getMessage(), e);
        } catch (NullPointerException e) {
            throw new JdbcDaoException("Null pointer exception " + e.getMessage(), e);
        } catch (SQLException e) {
            throw new JdbcDaoException("sql Exception : " + e.getMessage(), e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                LOGGER.error("Error closing ResultSet: " + e.getMessage(), e);
            }
        }
        return null;

    }
    
    
    private Object createObject(List<Field> fields,Connection connection,ResultSet rs,Class<?> objectClass) throws SQLException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        Object item = objectClass.getDeclaredConstructor().newInstance();
        for (Field field : fields) {
            Attribute annotations = field.getAnnotation(Attribute.class);
            OneToMeny oneToMeny=field.getAnnotation(OneToMeny.class);
            OneToOne oneToOne=field.getAnnotation(OneToOne.class);
            ManyToMany manyToMany=field.getAnnotation(ManyToMany.class);
            if(oneToMeny!=null) {
                List<Field> field2 = new ArrayList<>(Arrays.asList(oneToMeny.mapObject().getSuperclass().getDeclaredFields()));

                field2.addAll(Arrays.asList(oneToMeny.mapObject().getDeclaredFields()));
            	StringBuilder sb2=new StringBuilder();
            	for(Field f:field2) {
            		 Attribute annotation2 = f.getAnnotation(Attribute.class);
                     if (annotation2 != null) {
                         sb2.append(annotation2.colName()).append(",");
                     }
            	}
				String query2="select "+sb2.deleteCharAt(sb2.length()-1)+" from "+oneToMeny.joinTable()+" where "+oneToMeny.joinColumn()+" = "+((Entity) item).getId();
				PreparedStatement statement = connection.prepareStatement(query2);
				ResultSet rs2=statement.executeQuery();
				Set<Object> hashSet = new HashSet<Object>();
				while(rs2.next()) {
	               
	                hashSet.add(createObject(field2, connection, rs2,oneToMeny.mapObject()));
	            }
				String fieldName =field.getName();
                String methodName = "set" + fieldName.substring(0, 1).toUpperCase()
                        + fieldName.substring(1);

                var setter = objectClass.getMethod(methodName,Set.class);
                setter.invoke(item, hashSet);
				
            }else if(oneToOne!=null) {
                List<Field> field2 = new ArrayList<>(Arrays.asList(oneToOne.mapObject().getSuperclass().getDeclaredFields()));

                field2.addAll(Arrays.asList(oneToOne.mapObject().getDeclaredFields()));
            	StringBuilder sb2=new StringBuilder();
            	for(Field f:field2) {
            		 Attribute annotation2 = f.getAnnotation(Attribute.class);
                     if (annotation2 != null) {
                         sb2.append(annotation2.colName()).append(",");
                     }
            	}
				String query2="select "+sb2.deleteCharAt(sb2.length()-1)+" from "+oneToOne.joinTable()+" where id = "+rs.getLong(oneToOne.joinColumn());
				PreparedStatement statement = connection.prepareStatement(query2);
				ResultSet rs2=statement.executeQuery();
				if(rs2.next()) {
				String fieldName =field.getName();
                String methodName = "set" + fieldName.substring(0, 1).toUpperCase()
                        + fieldName.substring(1);

                var setter = objectClass.getMethod(methodName,oneToOne.mapObject());
                setter.invoke(item, createObject(field2, connection, rs2,oneToOne.mapObject()));
				}
				
            }else if(manyToMany!=null) {
                List<Field> field2 = new ArrayList<>(Arrays.asList(manyToMany.mapObject().getSuperclass().getDeclaredFields()));

                field2.addAll(Arrays.asList(manyToMany.mapObject().getDeclaredFields()));
            	StringBuilder sb2=new StringBuilder();
            	for(Field f:field2) {
            		 Attribute annotation2 = f.getAnnotation(Attribute.class);
                     if (annotation2 != null) {
                         sb2.append(annotation2.colName()).append(",");
                     }
            	}
            					sb2.deleteCharAt(sb2.length()-1);
				String query2="select "+sb2+" from " + manyToMany.joinTable() +" a join "+
						manyToMany.supportTable()+" b on a.id=b."+manyToMany.supportJoinColumn()+" where "+manyToMany.supportJoinColumn()+" = " + ((Entity) item).getId();
				PreparedStatement statement = connection.prepareStatement(query2);
				ResultSet rs2=statement.executeQuery();
				Set<Object> hashSet = new HashSet<Object>();
				while(rs2.next()) {
	               
	                hashSet.add(createObject(field2, connection, rs2,manyToMany.mapObject()));
				}
				String fieldName =field.getName();
                String methodName = "set" + fieldName.substring(0, 1).toUpperCase()
                        + fieldName.substring(1);
                var setter = objectClass.getMethod(methodName,Set.class);
				setter.invoke(item,hashSet);
				
            }  else if (annotations != null ) {
                String fieldName = annotations.fieldName();
                String methodName = "set" + fieldName.substring(0, 1).toUpperCase()
                        + fieldName.substring(1);

                var setter = objectClass.getMethod(methodName, annotations.className());
                String typeCol = annotations.type();
                Method method = ResultSet.class.getMethod("get"
                                + typeCol.substring(0, 1).toUpperCase() + typeCol.substring(1),
                        String.class);

                Object me = method.invoke(rs, annotations.colName());
                if (annotations.className().getSuperclass() == Entity.class) {
                    var entity = annotations.className().getDeclaredConstructor().newInstance();
                    Method setId = annotations.className().getMethod("setId", Long.class);
                    setId.invoke(entity, me);
                    setter.invoke(item, entity);
                } else {
                    if (annotations.className() == Instant.class) {
                        LocalDateTime localDateTime = ((Timestamp) me).toLocalDateTime();
                        me = localDateTime.toInstant(ZoneOffset.UTC);
                    }
                    setter.invoke(item, me);
                }

            }
        }
        return item;
    
    }

}
