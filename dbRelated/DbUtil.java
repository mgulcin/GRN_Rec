package dbRelated;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DbUtil {

	

	public static Connection getConnection() throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
		//String dbConn = "jdbc:mysql://localhost:3306/checkin2011db_2";lenova comp
		String dbConn = "jdbc:mysql://localhost:8888/bioInformatics";// mac @t2 & calgary
		String userName = "mg";
		//String password = "";// lenova & mac@t2
		String password = "1234";

		//Connecting to MYSQL Database
		//SQL Database name is java
		//SQL server is localhost, username:root, password:nopassword 
		//Connection con = DriverManager.getConnection("dbConn, userName,password);
		Connection con = DriverManager.getConnection(dbConn,
				userName,
				password);

		return con;
	}

}
