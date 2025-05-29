package it.nextre.corsojava.dao.jdbc;

import it.nextre.corsojava.dao.DaoInterface;
import it.nextre.corsojava.entity.Entity;
import it.nextre.corsojava.entity.annotation.Attribute;
import it.nextre.corsojava.exception.JdbcDaoException;
import org.jboss.logging.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

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

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM " + tableName + " WHERE id=?";
        PreparedStatement ps = null;
        try (Connection connection = getConnection()) {
            ps = connection.prepareStatement(query);
            int i = 1;
            ps.setLong(i++, id);
            ps.executeUpdate();
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
        List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));

        fields.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));
        StringBuilder sb = new StringBuilder();
        for (Field field : fields) {
            Attribute annotations = field.getAnnotation(Attribute.class);
            if (annotations != null) {
                sb.append(annotations.colName()).append(",");
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
                T item = (T) clazz.getDeclaredConstructor().newInstance();
                for (Field field : fields) {
                    Attribute annotations = field.getAnnotation(Attribute.class);
                    if (annotations != null) {
                        String fieldName = annotations.fieldName();
                        String methodName = "set" + fieldName.substring(0, 1).toUpperCase()
                                + fieldName.substring(1);

                        var setter = clazz.getMethod(methodName, annotations.className());
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
        List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        fields.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));
        StringBuilder sb = new StringBuilder();
        for (Field field : fields) {
            Attribute annotations = field.getAnnotation(Attribute.class);
            if (annotations != null) {
                sb.append(annotations.colName()).append(",");
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
                T item = (T) clazz.getDeclaredConstructor().newInstance();
                for (Field field : fields) {
                    Attribute annotations = field.getAnnotation(Attribute.class);
                    if (annotations != null) {
                        String fieldName = annotations.fieldName();
                        String methodName = "set" + fieldName.substring(0, 1).toUpperCase()
                                + fieldName.substring(1);

                        var setter = clazz.getMethod(methodName, annotations.className());
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
                list.add(item);
            }
            PagedResult<T> pagedResult=new PagedResult<T>(pagSize, list, totalElement);
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
        List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        fields.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));
        StringBuilder sb = new StringBuilder();
        for (Field field : fields) {
            Attribute annotations = field.getAnnotation(Attribute.class);
            if (annotations != null) {
                sb.append(annotations.colName()).append(",");
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
                T item = (T) clazz.getDeclaredConstructor().newInstance();
                for (Field field : fields) {
                    Attribute annotations = field.getAnnotation(Attribute.class);
                    if (annotations != null) {
                        String fieldName = annotations.fieldName();
                        String methodName = "set" + fieldName.substring(0, 1).toUpperCase()
                                + fieldName.substring(1);

                        var setter = clazz.getMethod(methodName, annotations.className());
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
                list.add(item);
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

        List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        fields.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));
        StringBuilder sb = new StringBuilder(" SET ");
        StringBuilder struc = new StringBuilder();
        ArrayList<Attribute> value = new ArrayList<>();
        for (Field field : fields) {
            Attribute annotations = field.getAnnotation(Attribute.class);
            if (annotations != null && !annotations.auto()) {
                sb.append(annotations.colName() + "=").append("?").append(",");
                value.add(annotations);
                struc.append(annotations.colName()).append(",");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        String query = "UPDATE " + tableName + sb.toString() + "where id=" + id;
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
        List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
        fields.addAll(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));
        StringBuilder sb = new StringBuilder(" VALUES (");
        StringBuilder struc = new StringBuilder(" (");
        ArrayList<Attribute> value = new ArrayList<>();
        for (Field field : fields) {
            Attribute annotations = field.getAnnotation(Attribute.class);
            if (annotations != null && !annotations.auto()) {
                sb.append("?").append(",");
                value.add(annotations);
                struc.append(annotations.colName()).append(",");
            }
        }
        struc.deleteCharAt(struc.length() - 1).append(")");
        String query = "INSERT INTO " + tableName + struc.toString() + sb.toString().substring(0, sb.length() - 1)
                + ")";

        ResultSet rs = null;
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
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                Long id = rs.getLong(1);
                item.setId(id);
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

}
