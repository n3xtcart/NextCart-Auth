package it.nextre.corsojava.dao.jdbc;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import java.util.Set;

import org.jboss.logging.Logger;

import io.agroal.api.AgroalDataSource;
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
	protected AgroalDataSource dataSource;
	private static final String FROM = " FROM ";
	private static final String WHERE = " WHERE ";
	private static final String DELETEFROM = " DELETE FROM ";
	private static final String SELECT = " SELECT ";

	protected JdbcDao(Class<?> clazz, String tableName, AgroalDataSource dataSource,String createTable) {
		this.clazz = clazz;
		this.tableName = tableName;
		this.dataSource = dataSource;
		PreparedStatement statement = null;
		ResultSet rSet = null;
		try (Connection connection = dataSource.getConnection()) {
			
				LOGGER.info("controllo e creazione tabella "+tableName);
				statement=connection.prepareStatement(createTable);
				statement.execute();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
				try {
					if(statement!=null)statement.close();
					if(rSet!=null)rSet.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	protected JdbcDao(Class<?> clazz, String tableName, AgroalDataSource dataSource) {
		this.clazz = clazz;
		this.tableName = tableName;
		this.dataSource = dataSource;
	}

	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	protected T findBy_(String[] cols, String[] val) {
		LOGGER.debug("findBy_ called with columns: " + Arrays.toString(cols) + " and values: " + Arrays.toString(val));
		StringBuilder condition = new StringBuilder();
		for (int i = 0; i < cols.length; i++) {
			condition.append(cols[i]).append(" = '").append(val[i]).append("' ").append("and ");
		}
		condition.delete(condition.length() - 5, condition.length());
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
		String query = SELECT + sb.deleteCharAt(sb.length() - 1) + FROM + tableName + WHERE + condition.toString();
		LOGGER.info("Executing query: " + query);
		try (Connection connection = getConnection()) {
			ps = connection.prepareStatement(query);
			rs = null;
			rs = ps.executeQuery();
			if (rs.next()) {
				return (T) createObject(fields, connection, rs, clazz);
			}
		} catch (SQLException e) {
			throw new JdbcDaoException("SQL error" + e.getMessage(), e);
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
		LOGGER.debug("delete called with id: " + id);
		String query = DELETEFROM + tableName + WHERE + "id=?";
		LOGGER.info("Executing query: " + query);
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
				if (manyToMany == null)
					continue;
				String delete = DELETEFROM + manyToMany.supportTable() + WHERE + manyToMany.supportJoinColumn() + "="
						+ id;
				ps = connection.prepareStatement(delete);
				ps.executeUpdate();

			}

		} catch (SQLException e) {
			throw new JdbcDaoException("SQL error" + e.getMessage(), e);
		}finally {
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
	public T getById(Long id) {
		LOGGER.debug("getById called with id: " + id);
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
		String query = SELECT + sb.deleteCharAt(sb.length() - 1) + FROM + tableName + WHERE + "id=?";
		LOGGER.info("Executing query: " + query);
		try (Connection connection = getConnection()) {
			ps = connection.prepareStatement(query);
			rs = null;
			int i = 1;
			ps.setLong(i++, id);
			rs = ps.executeQuery();
			if (rs.next()) {
				return (T) createObject(fields, connection, rs, clazz);
			}
		} catch (SQLException e) {
			throw new JdbcDaoException("SQL error" + e.getMessage(), e);
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

	public PagedResult<T> getAllPag(int pag, int pagSize) {
		LOGGER.debug("getAllPag called with page: " + pag + " and page size: " + pagSize);
		List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));

		fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
		StringBuilder sb = new StringBuilder();

		for (Field field : fields) {
			Attribute annotation = field.getAnnotation(Attribute.class);
			if (annotation != null) {
				sb.append(annotation.colName()).append(",");
			}
		}
		String query = SELECT + "(select count(*) " + FROM + tableName + "  ) as totale , "
				+ sb.deleteCharAt(sb.length() - 1) + FROM + tableName + " order by id limit " + pagSize + " offset "
				+ pag * pagSize;
		LOGGER.info("Executing query: " + query);

		PreparedStatement ps = null;
		ResultSet rs = null;
		try (Connection connection = getConnection()) {
			ps = connection.prepareStatement(query);
			rs = ps.executeQuery();
			Integer totalElement = null;
			ArrayList<T> list = new ArrayList<>();
			while (rs.next()) {

				if (totalElement == null)
					totalElement = rs.getInt("totale");
				list.add((T) createObject(fields, connection, rs, clazz));
			}if (totalElement!=null)return new PagedResult<>(list, totalElement, pagSize);
			else return new PagedResult<>(list, 0, pagSize);
		} catch (SQLException e) {
			throw new JdbcDaoException("SQL error" + e.getMessage(), e);
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
		LOGGER.debug("getAll called");
		List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));

		fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
		StringBuilder sb = new StringBuilder();

		for (Field field : fields) {
			Attribute annotation = field.getAnnotation(Attribute.class);
			if (annotation != null) {
				sb.append(annotation.colName()).append(",");
			}
		}
		String query = SELECT + sb.deleteCharAt(sb.length() - 1) + FROM + tableName;

		LOGGER.info("Executing query: " + query);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try (Connection connection = getConnection()) {
			ps = connection.prepareStatement(query);
			rs = ps.executeQuery();
			ArrayList<T> list = new ArrayList<>();
			while (rs.next()) {
				list.add((T) createObject(fields, connection, rs, clazz));

			}
			return list;
		} catch (SQLException e) {
			throw new JdbcDaoException("SQL error" + e.getMessage(), e);
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

	private void updateManyToMany(ManyToMany manyToMany, Object item, Connection connection, Field field)
			throws SQLException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		LOGGER.debug("ManyToMany field found: " + field.getName());
		String delete = DELETEFROM + manyToMany.supportTable() + WHERE + manyToMany.supportJoinColumn() + "="
				+ ((Entity) item).getId();
		LOGGER.info("Executing delete old field query : " + delete);
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(delete);
			ps.executeUpdate();
			Object invoke = null;
			invoke = clazz
					.getMethod("get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1))
					.invoke(item);

			if (invoke instanceof Collection<?> c) {
				for (Object o : c) {
					if (o instanceof Entity e && e.getId() != null) {

						LOGGER.debug("Processing ManyToMany entity with id: " + e.getId());
						String checkQuery = SELECT + " id FROM " + manyToMany.joinTable() + WHERE + "  id = "
								+ e.getId();
						LOGGER.info("Executing check query: " + checkQuery);
						ps = connection.prepareStatement(checkQuery);
						ResultSet rs = ps.executeQuery();
						if (rs.next()) {
							LOGGER.debug("Entity with id " + e.getId() + " exists in join table.");
							String add = "insert into " + manyToMany.supportTable() + " (" + manyToMany.joinColumn()
									+ "," + manyToMany.supportJoinColumn() + ") VALUES (" + e.getId() + ","
									+ ((Entity) item).getId() + ")";

							LOGGER.info("Executing add query: " + add);
							ps = connection.prepareStatement(add);
							ps.executeUpdate();

						}

					}
				}
			}
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
	public void update(Long id, T item) {
		LOGGER.debug("update called with id: " + id + " and item: " + item);
		List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getSuperclass().getDeclaredFields()));

		fields.addAll(Arrays.asList(clazz.getDeclaredFields()));

		StringBuilder sb = new StringBuilder(" SET ");
		StringBuilder struc = new StringBuilder();
		ArrayList<Attribute> value = new ArrayList<>();
		PreparedStatement ps = null;
		try {
			for (Field field : fields) {
				ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
				Attribute annotations = field.getAnnotation(Attribute.class);
				Object value1;
				value1 = clazz
						.getMethod("get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1))
						.invoke(item);
				if (value1 == null) {
					LOGGER.debug("Value for field " + field.getName() + " is null, skipping.");
					continue;
				}

				if (annotations != null && !annotations.auto() && annotations.update()) {

					sb.append(annotations.colName() + "=").append("?").append(",");
					value.add(annotations);
					struc.append(annotations.colName()).append(",");
				} else if (manyToMany != null) {
					updateManyToMany(manyToMany, item, getConnection(), field);
				}
			}
			sb.deleteCharAt(sb.length() - 1);
			String query = "UPDATE " + tableName + sb.toString() + " where id=" + id;
			try (Connection connection = getConnection()) {
				if (sb.toString().equals(" SET")) {
					LOGGER.debug("No fields to update, skipping update.");
					return;
				}
				ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				int i = 1;
				Object value1 = null;
				for (Attribute val : value) {
					value1 = clazz.getMethod(
							"get" + val.fieldName().substring(0, 1).toUpperCase() + val.fieldName().substring(1))
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

			} finally {

				if (ps != null) {
					ps.close();
				}

			}
		} catch (SQLException e) {
			throw new JdbcDaoException("SQL error " + e.getMessage(), e);
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
		}

	}

	private void addSupport(Object item, Connection connection, Field field)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, SQLException {

		ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
		Object invoke = clazz
				.getMethod("get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1))
				.invoke(item);

		if (invoke instanceof Collection<?> c) {

			for (Object o : c) {
				if (o instanceof Entity e && e.getId() != null) {

					String selectQuery = "SELECT id FROM " + manyToMany.joinTable() + " WHERE id = " + e.getId();

					try (PreparedStatement ps = connection.prepareStatement(selectQuery)) {
						ResultSet rs = ps.executeQuery();
						if (rs.next()) {
							String queryString = "INSERT INTO " + manyToMany.supportTable() + " ("
									+ manyToMany.joinColumn() + "," + manyToMany.supportJoinColumn() + ") VALUES ("
									+ e.getId() + "," + ((Entity) item).getId() + ")";

							try (PreparedStatement ps2 = connection.prepareStatement(queryString)) {
								ps2.executeUpdate();
							}

						}
					}

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

		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			for (Field field : fields) {
				Attribute annotations = field.getAnnotation(Attribute.class);
				if (field.getAnnotation(ManyToMany.class) != null)
					manyToManyFields.add(field);
				Object invoke = clazz
						.getMethod("get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1))
						.invoke(item);
				if (invoke==null ||( invoke instanceof Entity e && e.getId() == null)) {
					continue;
				}
				if (annotations != null && !annotations.auto()) {
					

					sb.append("?").append(",");
					value.add(annotations);
					struc.append(annotations.colName()).append(",");
				}
			}

			struc.deleteCharAt(struc.length() - 1).append(")");
			String query = "INSERT INTO " + tableName + struc.toString() + sb.toString().substring(0, sb.length() - 1)
					+ ")";
			try (Connection connection = getConnection()) {
				ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				int i = 1;
				Object value1 = null;
				for (Attribute val : value) {
					value1 = clazz.getMethod(
							"get" + val.fieldName().substring(0, 1).toUpperCase() + val.fieldName().substring(1))
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
						for (Field field : manyToManyFields) {

							addSupport(item, connection, field);
						}
					
					return id;
				}
			}
		} catch (IllegalAccessException e) {
			throw new JdbcDaoException("IllegalAccessException " + e.getMessage(), e);

		} catch (IllegalArgumentException e) {
			throw new JdbcDaoException("IllegalArgumentException " + e.getMessage(), e);
		} catch (InvocationTargetException e) {

			throw new JdbcDaoException("InvocationTargetException " + e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			throw new JdbcDaoException("NoSuchMethodException " + e.getMessage(), e);
		} catch (SecurityException e) {
			throw new JdbcDaoException("SecurityException" + e.getMessage(), e);

		} catch (SQLException e) {
			throw new JdbcDaoException("sql Exception : " + e.getMessage(), e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
				LOGGER.error("Error closing ResultSet: " + e.getMessage(), e);
			}
		}
		return null;

	}

	private void createOneToMany(OneToMeny oneToMeny, Object item, Connection connection, Field field,
			Class<?> objectClass) throws SQLException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		List<Field> field2 = new ArrayList<>(Arrays.asList(oneToMeny.mapObject().getSuperclass().getDeclaredFields()));

		field2.addAll(Arrays.asList(oneToMeny.mapObject().getDeclaredFields()));
		StringBuilder sb2 = new StringBuilder();
		for (Field f : field2) {
			Attribute annotation2 = f.getAnnotation(Attribute.class);
			if (annotation2 != null) {
				sb2.append(annotation2.colName()).append(",");
			}
		}
		String query2 = SELECT + sb2.deleteCharAt(sb2.length() - 1) + FROM + oneToMeny.joinTable() + WHERE
				+ oneToMeny.joinColumn() + " = " + ((Entity) item).getId();
		try (PreparedStatement statement = connection.prepareStatement(query2)) {
			ResultSet rs2 = statement.executeQuery();
			Set<Object> hashSet = new HashSet<>();
			while (rs2.next()) {

				hashSet.add(createObject(field2, connection, rs2, oneToMeny.mapObject()));
			}

			String fieldName = field.getName();
			String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

			var setter = objectClass.getMethod(methodName, Set.class);
			setter.invoke(item, hashSet);
		}

	}

	private void createOneToOne(OneToOne oneToOne, Object item, Connection connection, Field field,
			Class<?> objectClass, ResultSet rs) throws SQLException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {

		List<Field> field2 = new ArrayList<>(Arrays.asList(oneToOne.mapObject().getSuperclass().getDeclaredFields()));

		field2.addAll(Arrays.asList(oneToOne.mapObject().getDeclaredFields()));
		StringBuilder sb2 = new StringBuilder();
		for (Field f : field2) {
			Attribute annotation2 = f.getAnnotation(Attribute.class);
			if (annotation2 != null) {
				sb2.append(annotation2.colName()).append(",");
			}
		}
		String query2 = SELECT + sb2.deleteCharAt(sb2.length() - 1) + FROM + oneToOne.joinTable() + WHERE + " id = "
				+ rs.getLong(oneToOne.joinColumn());
		try (PreparedStatement statement = connection.prepareStatement(query2)) {
			ResultSet rs2 = statement.executeQuery();
			if (rs2.next()) {
				String fieldName = field.getName();
				String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

				var setter = objectClass.getMethod(methodName, oneToOne.mapObject());
				setter.invoke(item, createObject(field2, connection, rs2, oneToOne.mapObject()));
			}
		}

	}

	private void createManyToMany(ManyToMany manyToMany, Object item, Connection connection, Field field,
			Class<?> objectClass) throws SQLException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		List<Field> field2 = new ArrayList<>(Arrays.asList(manyToMany.mapObject().getSuperclass().getDeclaredFields()));

		field2.addAll(Arrays.asList(manyToMany.mapObject().getDeclaredFields()));
		StringBuilder sb2 = new StringBuilder();
		for (Field f : field2) {
			Attribute annotation2 = f.getAnnotation(Attribute.class);
			if (annotation2 != null) {
				sb2.append(annotation2.colName()).append(",");
			}
		}
		sb2.deleteCharAt(sb2.length() - 1);
		String query2 = SELECT + sb2 + FROM + manyToMany.joinTable() + " a join " + manyToMany.supportTable()
				+ " b on a.id=b." + manyToMany.joinColumn() + " where " + manyToMany.supportJoinColumn() + " = "
				+ ((Entity) item).getId();
		try (PreparedStatement statement = connection.prepareStatement(query2)) {
			ResultSet rs2 = statement.executeQuery();
			Set<Object> hashSet = new HashSet<>();
			while (rs2.next()) {

				hashSet.add(createObject(field2, connection, rs2, manyToMany.mapObject()));
			}
			String fieldName = field.getName();
			String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
			var setter = objectClass.getMethod(methodName, Set.class);
			setter.invoke(item, hashSet);
		}

	}

	private Object createObject(List<Field> fields, Connection connection, ResultSet rs, Class<?> objectClass) {
		Object item = null;
		try {
			item = objectClass.getDeclaredConstructor().newInstance();
			for (Field field : fields) {
				Attribute annotations = field.getAnnotation(Attribute.class);
				OneToMeny oneToMeny = field.getAnnotation(OneToMeny.class);
				OneToOne oneToOne = field.getAnnotation(OneToOne.class);
				ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
				if (oneToMeny != null) {
					createOneToMany(oneToMeny, item, connection, field, objectClass);
				} else if (oneToOne != null) {
					createOneToOne(oneToOne, item, connection, field, objectClass, rs);
				} else if (manyToMany != null) {
					createManyToMany(manyToMany, item, connection, field, objectClass);
				} else if (annotations != null) {
					String fieldName = annotations.fieldName();
					String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

					var setter = objectClass.getMethod(methodName, annotations.className());
					String typeCol = annotations.type();
					Method method = ResultSet.class.getMethod(
							"get" + typeCol.substring(0, 1).toUpperCase() + typeCol.substring(1), String.class);

					Object me = method.invoke(rs, annotations.colName());
					if (me == null) {
						continue;
					}

					if (annotations.className() == Instant.class) {
						LocalDateTime localDateTime = ((Timestamp) me).toLocalDateTime();
						me = localDateTime.toInstant(ZoneOffset.UTC);
					}
					setter.invoke(item, me);

				}
			}
		} catch (InstantiationException e) {
			throw new JdbcDaoException("InstantiationException" + e.getMessage(), e);

		} catch (IllegalAccessException e) {
			throw new JdbcDaoException("IllegalAccessException " + e.getMessage(), e);

		} catch (IllegalArgumentException e) {
			throw new JdbcDaoException("IllegalArgumentException " + e.getMessage(), e);

		} catch (InvocationTargetException e) {
			throw new JdbcDaoException("InvocationTargetException " + e.getMessage(), e);

		} catch (NoSuchMethodException e) {
			throw new JdbcDaoException("NoSuchMethodException " + e.getMessage(), e);

		} catch (SecurityException e) {
			throw new JdbcDaoException("SecurityException " + e.getMessage(), e);

		} catch (SQLException e) {
			throw new JdbcDaoException("SQLException" + e.getMessage(), e);

		}
		return item;

	}

}
