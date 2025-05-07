package predavanje;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;


import javax.sql.DataSource;

import java.sql.*;


public class Main {

    public static void main(String[] args) {

        DataSource ds = createDataSource();
        Savepoint savepoint = null;

        try (Connection conn = ds.getConnection()) {
            System.out.println("Spojeni ste na bazu!");
            try (Statement stm1 = conn.createStatement();
                 Statement stm2 = conn.createStatement()) {
                conn.setAutoCommit(false);
                stm1.executeUpdate("INSERT INTO Drzava (Naziv) VALUES('Finska')");
                savepoint = conn.setSavepoint("kontrolnaTocka1");

                stm2.executeUpdate("INSERT INTO Drzava (Naziv) VALUES('Grčka')");
                //stm1.executeUpdate("DELETE FROM Drzava WHERE IDDrzava >9");

                conn.commit();
                System.out.println("Uspješno komitano");

                ResultSet rs = stm1.executeQuery("SELECT IDDrzava,Naziv FROM Drzava");
                while (rs.next()) {
                    System.out.printf("%d %s\n", rs.getInt("IDDrzava"), rs.getString("Naziv"));
                }
            } catch (SQLException e) {
                System.err.println("Transakcija dijelom poništena!");

                try {
                    conn.rollback(savepoint);
                    conn.commit();
                    System.out.println("Transakcija vračena na kontrolnu točku 1");

                } catch (SQLException ee) {
                    ee.printStackTrace();
                }
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
