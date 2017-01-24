package drinkwater.test.db;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;

public class DatabaseInject {
    public static void cleanAndInject(DataSource datasource, String resourceFilePath) throws Exception {
        IDataSet ds = readDataSet(resourceFilePath);
        cleanlyInsert(datasource, ds);
    }

    private static void cleanlyInsert(DataSource datasource, IDataSet dataSet) throws Exception {
        //FIXME this assumes we always use postgres
        PostgresDataSourceTester databaseTester = new PostgresDataSourceTester(datasource);
        databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
        databaseTester.setDataSet(dataSet);
        databaseTester.onSetup();
    }

    private static IDataSet readDataSet(String fileName) throws Exception {
        return new FlatXmlDataSetBuilder().build(getFullFilepath(fileName));
    }

    private static File getFullFilepath(String resourceFilePath) throws IOException {

        ClassLoader classLoader = DatabaseInject.class.getClassLoader();

        File file = new File(classLoader.getResource(resourceFilePath).getFile());

        return file;
    }
}
