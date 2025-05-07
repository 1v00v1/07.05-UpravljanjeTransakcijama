package predavanje;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;


import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Main {

    public static void main(String[] args) {

        DataSource ds = createDataSource();

        try (Connection conn = ds.getConnection()) {
            System.out.println("Spojeni ste na bazu!");
            try (Statement stm1 = conn.createStatement();
                 Statement stm2 = conn.createStatement()) {
                conn.setAutoCommit(false);
                stm1.executeUpdate("INSERT INTO Drzava (Naziv) VALUES('Finska')");
                stm2.executeUpdate("INSERT INTO Drzava (Naziv) VALUES('Grčka')");
                //stm1.executeUpdate("DELETE FROM Drzava WHERE IDDrzava >9");

                conn.commit();
                System.out.println("Uspješno komitano");

                ResultSet rs = stm1.executeQuery("SELECT IDDrzava,Naziv FROM Drzava");
                while (rs.next()){
                    System.out.printf("%d %s\n",rs.getInt("IDDrzava"), rs.getString("Naziv"));
                }
            } catch (SQLException e) {
                System.err.println("Transakcija poništena!");
                e.printStackTrace();
                conn.rollback();
            }

        } catch (SQLException e) {
            System.err.println("Neuspješno spajanje na bazu!");
            e.printStackTrace();

        }
    }

    public static javax.sql.DataSource createDataSource() {

        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setServerName("localhost");
        ds.setPortNumber(1433);
        ds.setDatabaseName("AdventureWorksOBP");
        ds.setUser("sa");
        ds.setPassword("SQL");
        ds.setEncrypt(false);

        return ds;

    }
}
