package drinkwater.datasource;

import org.dbunit.dataset.datatype.AbstractDataType;
import org.dbunit.dataset.datatype.TypeCastException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by A406775 on 11/01/2017.
 */


public class JsonbDataType extends AbstractDataType {
    private static final Logger logger = LoggerFactory.getLogger(JsonbDataType.class);

    public JsonbDataType() {
        super("jsonb", 1111, String.class, false);
    }

    public Object getSqlValue(int column, ResultSet resultSet) throws SQLException, TypeCastException {
        return resultSet.getString(column);
    }

    public void setSqlValue(Object jsonb, int column, PreparedStatement statement) throws SQLException, TypeCastException {
        statement.setObject(column, this.getJsonB(jsonb, statement.getConnection()));
    }

    @Override
    public Object typeCast(Object arg0) throws TypeCastException {
        return arg0.toString();
    }

    private Object getJsonB(Object value, Connection connection) throws TypeCastException {
        logger.debug("getJsonB(value={}, connection={}) - start", value, connection);
        Object tempUUID = null;

        try {
            Class e = super.loadClass("org.postgresql.util.PGobject", connection);
            Constructor ct = e.getConstructor((Class[])null);
            tempUUID = ct.newInstance((Object[])null);
            Method setTypeMethod = e.getMethod("setType", String.class);
            setTypeMethod.invoke(tempUUID, "jsonb");
            Method setValueMethod = e.getMethod("setValue", String.class);
            setValueMethod.invoke(tempUUID, value.toString());
            return tempUUID;
        } catch (ClassNotFoundException var8) {
            throw new TypeCastException(value, this, var8);
        } catch (InvocationTargetException var9) {
            throw new TypeCastException(value, this, var9);
        } catch (NoSuchMethodException var10) {
            throw new TypeCastException(value, this, var10);
        } catch (IllegalAccessException var11) {
            throw new TypeCastException(value, this, var11);
        } catch (InstantiationException var12) {
            throw new TypeCastException(value, this, var12);
        }
    }
}
