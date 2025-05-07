package zadaci;


import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

//TODO /*
// Napiši Java konzolnu aplikaciju koja rješava sljedeći problem.
// * Kod unosa stavki (tablica Stavka) zabunom je unesena cijena po komadu za stavke sa id-evima 8 i 9.
// Cijenu po komadu za stavku sa id-em 8 treba povećati za 10, a cijenu po komadu stavke sa id-em 9 treba smanjiti za 10
// Osigurati da se obje izmjene dogode u transakciji, tj ili se izvrše obje izmjene ili niti jedna
// */

/*
SQL


BEGIN TRAN
	BEGIN TRY
	DECLARE @cijena1 money
	DECLARE @cijena2 money

	SET @cijena1 = ((SELECT CijenaPoKomadu FROM Stavka WHERE IDStavka =8)+10)
	SET @cijena2 = ((SELECT CijenaPoKomadu FROM Stavka WHERE IDStavka =9)-10)

		 UPDATE  Stavka SET CijenaPoKomadu = @cijena1 WHERE IDStavka =8
		 UPDATE  Stavka SET  CijenaPoKomadu = @cijena2  WHERE IDStavka =9
		COMMIT
	END TRY

	BEGIN CATCH
		ROLLBACK
	END CATCH


* */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DataSource ds = createDataSource();
        try (Connection con = ds.getConnection();
             Statement st1 = con.createStatement()) {
            System.out.print("Unesite ID za povečanje: ");
            int idP = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Unesite ID za smanjivanje: ");
            int idS = Integer.parseInt(scanner.nextLine().trim());
            double cijenaPoKom = 0.0;

            try {

                con.setAutoCommit(false);
                st1.executeUpdate(uvečajCijenuPoId(idP, st1));
                st1.executeUpdate(smanjiCijenuPoId(idS, st1));
                con.commit();

            } catch (SQLException e) {
                System.err.println("Transakcija poništena!");
                e.printStackTrace();
                con.rollback();
            }

        } catch (SQLException e) {
            System.err.println("Neuspješno spajanje na bazu");
        }


    }

    private static String uvečajCijenuPoId(int id, Statement st) {
        String sql = "SELECT CijenaPoKomadu FROM Stavka WHERE IDStavka =" + id;
        double cijena = 0.0;
        try {

            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                cijena = rs.getDouble("CijenaPoKomadu");
            }

            cijena += 10;

            sql = "UPDATE  Stavka SET CijenaPoKomadu = " + cijena + " WHERE IDStavka =" + id;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sql;
    }

    private static String smanjiCijenuPoId(int id, Statement st) {
        String sql = "SELECT CijenaPoKomadu FROM Stavka WHERE IDStavka =" + id;
        double cijena = 0.0;
        try {

            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                cijena = rs.getDouble("CijenaPoKomadu");
            }

            cijena -= 10;

            sql = "UPDATE  Stavka SET CijenaPoKomadu = " + cijena + " WHERE IDStavka =" + id;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return sql;
    }

    private static DataSource createDataSource() {

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
