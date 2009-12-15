package perfEval;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import eu.secse.deliveryManager.interest.Interest;
import eu.secse.deliveryManager.model.Deliverable;

public class DeleteTablesInterest implements Interest {

	private static final long serialVersionUID = -9126162998289262092L;

	static {
		try {
			System.out.println("Loading jdbc driver");
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			System.out.println("Jdbc driver loaded");
			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static final String[] TABLES = 
	{ 
		"operationsetsim",
		"operationsim",
		"unknowwords",
		"wordsim" 
	};
	
	public String getName() {
		return "DeleteTablesInterest";
	}

	public float getSimilarity(Deliverable msg) {
		return 0;
	}

	public boolean isCoveredBy(Interest other) {
		return false;
	}

	public boolean matches(Deliverable msg) {
		if(msg instanceof DeleteTables) {
			System.err.println("Deleting tables");
			
			try {
				Statement stmt = connect().createStatement();
				
				for (String table : TABLES) {
					int n = stmt.executeUpdate("DELETE FROM " + table + ";");
					System.err.println("Removed " + n + " entries from " + table);
				}
				stmt.close();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}

			return true;
		}
		
		return false;
	}

	private static Connection conn;
	private Connection connect() throws SQLException {
		if (conn == null)
			conn = DriverManager.getConnection("jdbc:mysql://localhost/optidb?user=root");

		return conn;
	}


	@Override
	public int hashCode() {
		return 31;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}
}