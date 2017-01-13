package drinkwater.datasource;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;

/**
 * Created by A406775 on 11/01/2017.
 */
public class DWPostgresqlDataTypeFactory extends PostgresqlDataTypeFactory {

    @Override
    public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException {
        if (sqlType == 1111) {
            if ("jsonb".equals(sqlTypeName)) {
                return new JsonbDataType();
            }
        }

        return super.createDataType(sqlType, sqlTypeName);
    }
}
