package dbRelated;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


public class DbCreater {
	/* Note: Refer to http://www.ntu.edu.sg/home/ehchua/programming/java/JDBC_Basic.html
	 *  for basic SQL + Java
	 * 
	 */
	public static void main(String[] args) {
		// get connection
		Connection con = null;
		try {
			con = DbUtil.getConnection();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// create spellman data table
		String spellmanPath = "./data/Spellman_subset.csv";
		String tableName = "spellmanData";
		createSpellmanDataTable(con, spellmanPath, tableName);

		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void createSpellmanDataTable(Connection con, String spellmanPath, String tableName) {
		
		try {
			createTable(con, tableName);
			
			Statement stmt = con.createStatement();
			// INSERT a partial record
			String sqlInsert = "insert into "
					+ tableName;

			System.out.println("The SQL query is: " + sqlInsert);  // Echo for debugging
			int countInserted = stmt.executeUpdate(sqlInsert);
			System.out.println(countInserted + " records inserted to "+tableName);

			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void createTable(Connection con, String tableName) {
		try {

			//Using the Connection Object now Create a Statement
			Statement stmnt = con.createStatement();

			switch(tableName){
			case "spellmanData":
			{
				// create table
				String sqlCreate = "create table " + tableName 
						+ " (stdName varchar(50), sysName varchar(50), definition varchar(200))";
				System.out.println("The SQL query is: " + sqlCreate);  // Echo for debugging
				stmnt.execute(sqlCreate);
			}
				break;
			default: System.out.println("Wrong table name!!"); break;
			}
			

			//Close the Statement & connection
			stmnt.close();

		}  catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
