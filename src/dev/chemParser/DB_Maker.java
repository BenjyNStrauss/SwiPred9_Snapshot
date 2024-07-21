package dev.chemParser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import assist.util.LabeledList;
import biology.molecule.types.AminoType;
import utilities.LocalToolBase;

/**
 * Designed to check:
 * (1) if there are any duplicate codes for enums representing moleculeTypes
 * @author Benjamin Strauss
 *
 */

public class DB_Maker extends LocalToolBase {
	public static final String H2_DRIVER = "org.h2.Driver";
	public static final String H2_DB_URL = "jdbc:h2:./files/compounds";
	
	public static void main(String[] args) {
		showTables();
		insertAminoTypes();
	}
	
	private static void insertAminoTypes() {
		StringBuilder sb = new StringBuilder();
		for(AminoType at: AminoType.values()) {
			sb.setLength(0);
			sb.append("INSERT INTO COMPOUNDS VALUES(");
			sb.append(to_sql(at.name()));
			sb.append(to_sql(at.code));
			
			if(at.pubChem_id > 0) {
				sb.append(at.pubChem_id+",null,");
			} else if(at.pubChem_id < -5){
				sb.append(",null"+at.pubChem_id+",");
			} else {
				sb.append("null,null,");
			}
			
			sb.append(to_sql(""+at.letter));
			sb.append(to_sql(""+at.utf16_letter));
			sb.append(to_sql(at.clazz.toString()));
			sb.append(to_sql(at.baseForm));
			
			if(at.chemCode != null) {
				sb.append(to_sql(at.chemCode));
			} else {
				sb.append("null,");
			}
			
			sb.append("true,false,false,false");
			sb.append(");");
			insertIntoDB(sb.toString());
		}
	}
	
	private static String to_sql(String str) {
		return "\""+str+"\",";
	}
	
	private static void insertIntoDB(String query) {
		Connection conn = null;
		Statement st = null;
		
		try {
			Class.forName(H2_DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			conn = DriverManager.getConnection(H2_DB_URL);
			st = conn.createStatement();
			st.executeUpdate(query);
		} catch (SQLException e) {
			qerr("SQL Exception for query =\""+query+"\"");
			e.printStackTrace();
			System.exit(0);
		}
	}

	private static void showTables() {
		Connection conn = null;
		Statement st = null;
		
		try {
			Class.forName(H2_DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			conn = DriverManager.getConnection(H2_DB_URL);
			st = conn.createStatement();
			ResultSet rs = st.executeQuery("SHOW TABLES;");
			
			qp(rs.getString(1));
			
		} catch (SQLException e) {
			e.printStackTrace();
			
			 System.out.println("SQLException: " + e.getMessage());
		     System.out.println("SQLState: " + e.getSQLState());
		     System.out.println("VendorError: " + e.getErrorCode());
			System.exit(0);
		}
	}
}
