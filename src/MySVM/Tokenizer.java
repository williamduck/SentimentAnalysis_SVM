package MySVM;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

public class Tokenizer {
	private Vector<Item> Comment;
	
	public Tokenizer() {
		
	}

	/**
	 * Analyze the comment and return a tokenized item vector.
	 * item : "word, type, orientation"
	 * 
	 * @param comment
	 * @return
	 */
	public Vector<Item> tokenizer(String comment) {
		// Tokenizing the comment.
		List<Term> parse = ToAnalysis.parse(comment);

		Comment = new Vector<Item>();
		// Store each of the result list to the Vector<Item> vector.
		for(int i = 0; i < parse.size(); i++) {
			Item item = new Item(parse.get(i).getName(), parse.get(i).getNatrue().natureStr);
			Comment.add(item);
		}
		return Comment;
	}

	public static void main(String[] args) throws SQLException{
		
		connect_db db = new connect_db();
		Connection conn = (Connection) db.getConnections();
		
		Tokenizer token = new Tokenizer();
		String QryMacString = "SELECT commend_id, comment FROM pro_comment";
		PreparedStatement pjs = null;
		ResultSet rs = null;

		// Pull out the comments from the database and tokenize them one by one, 
		// then print each of the "word - type" pairs out.
		try {
			pjs = (PreparedStatement) conn.prepareStatement(QryMacString);
			rs = pjs.executeQuery();
			while(rs.next()) {
				String comment = rs.getString("comment");
				Vector<Item> Comment = token.tokenizer(comment);
				for(int i = 0; i < Comment.size(); i++) {
					System.out.println(Comment.get(i).getWord() + "/" + Comment.get(i).getType() + "; ");
				}
			}
		} finally {
			conn.close();
		}
	}
}
