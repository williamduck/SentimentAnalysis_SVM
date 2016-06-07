package MySVM;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import com.mysql.jdbc.PreparedStatement;

import service.svm_predict;
import service.svm_train;

/**
 * 
 * @author Solomon
 *
 */
public class Comment_SVM {
	private String encoding = "GBK";
	private String comment = null;
	private double ratio = 0.01;
	// The top ratio% most frequent adj_words from samples to form the Feature vector.
	private Vector<String> Feature = new Vector<String>();
	// HashMap<String, Integer> feat is HashMap<adj_words, frequency> which is formed according to the word frequency.
	private HashMap<String, Integer> feat = new HashMap<String, Integer>();
	String filepath1 = "E:\\";

	private connect_db connector;
	private int NumOfTrain = 2000;
	private int NumOfTest = 2000;
	private ArrayList<Integer> Comment_id = new ArrayList<Integer>();
	HashMap<Integer, Vector<String>> train_pos = new HashMap<Integer, Vector<String>>();
	HashMap<Integer, Vector<String>> train_neg = new HashMap<Integer, Vector<String>>();
	HashMap<Integer, Vector<String>> test_pos = new HashMap<Integer, Vector<String>>();
	HashMap<Integer, Vector<String>> test_neg = new HashMap<Integer, Vector<String>>();

	public Comment_SVM() {
		this.connector = new connect_db();
	}

	private final static String[] neg = {"不","没","无","非","莫","弗","毋","勿","未","否","别","無","休","怎么"};

	/**
	 * makeTrainData:
	 * Load some sample positive/negative comments from the DataBase,
	 * Tokenize each comments and,
	 * classify positive comments as 1 while negative as -1,
	 * select the most frequent adj words as Featured adjs,
	 * write the training data as:
	 * each of the train_pos sentence as "1 adj_Feature_index1 appear-times1 adj_Feature_index2 appear-times2 ..."
	 * each of the train_neg sentence as "-1 adj_Feature_index1 appear-times1 adj_Feature_index2 appear-times2 ..."
	 * 
	 * @param product_type
	 * @throws SQLException
	 */
	public void makeTrainData(String product_type) throws SQLException {
		String s = "train.txt";
		try {
			String filepath = filepath1 + s;
			File f = new File(filepath);
			if(f.exists()) {
				System.out.println("File is existing!");
				f.delete();
				f.createNewFile();
			} else {
				System.out.println("File isn't existing!");
				f.createNewFile();
			}
			BufferedWriter output = new BufferedWriter(new FileWriter(f));

			/**
			 *  Positive training samples.
			 */
			PreparedStatement pjs = null;
			ResultSet rs = null;

			// Pull out such NumOfSample products as positive train samples : 
			// product type is product_type, rate is 5-star
			String QryMacString = "SELECT a.`product_name`, b.`comment`, b.`customer_satisfy` FROM product a, pro_comment b WHERE a.class_id LIKE'" + 
					product_type + "'AND a.`product_no` = b.`product_no` AND b.`customer_satisfy` LIKE '%5%' LIMIT " + 0 + "," + NumOfTrain;
			try {
				pjs = (PreparedStatement) this.connector.getConnections().prepareStatement(QryMacString);
				rs = pjs.executeQuery();
				int i = 0;
				while(rs.next()) {
					comment = rs.getString("comment");
					// Extract all the adjs with their corresponding positive words in a comment sentence.
					Vector<String> adj = extractAdj_train();
					// Store all the positive comments into the train_neg<Sentence_No., Vector<corresponding adj words>> 
					// HashMap<Integer, Vector<String>>()
					train_pos.put(i, adj);
					i++;
				}
			} finally {
				this.connector.conn.close();
			}

			/**
			 * Negative training samples.
			 */
			// Pull out such NumOfSample products as negative train samples : 
			// product type is product_type, rate is 1-star
			QryMacString = "SELECT a.`product_name`, b.`comment`, b.`customer_satisfy` FROM product a, pro_comment b WHERE a.class_id LIKE '" +
					product_type + "' AND a.`product_no` = b.`product_no` AND b.`customer_satisfy` LIKE '%1%' LIMIT " + 0 + "," + NumOfTrain;
			try {
				pjs = (PreparedStatement) this.connector.getConnections().prepareStatement(QryMacString);
				rs = pjs.executeQuery();
				int i = 0;
				while(rs.next()) {
					comment = rs.getString("comment");
					// Extract all the adjs with their corresponding negative words in a comment sentence.
					Vector<String> adj = extractAdj_train();
					// Store all the negative comments into the train_neg<Sentence_No., Vector<corresponding adj words>> 
					// HashMap<Integer, Vector<String>>()
					train_neg.put(i, adj);
					i++;
				}
			} finally {
				this.connector.conn.close();
			}

			// Construct the featured adj HashMap.
			// HashMap<String, Integer> feat is HashMap<adj_words, frequency> which is formed according to the word frequency 
			// statistic data from the positive/negative extractAdj_train().
			Iterator iter = feat.entrySet().iterator();
			int num = feat.size();
			while(iter.hasNext()) {
				Map.Entry entry = (Entry) iter.next();
				// Get the top ratio% most frequent adj_words to form the Feature vector.
				if((Integer) entry.getValue() > num * ratio) {
					// The top ratio% most frequent adj_words to form the Feature vector.
					Feature.add((String) entry.getKey());
				}
			}

			iter = train_pos.entrySet().iterator();
			// write each of the train_pos sentence as "1 adj_Feature_index1 appear-times1 adj_Feature_index2 appear-times2 ..."
			while(iter.hasNext()) {
				Map.Entry entry = (Entry) iter.next();
				writetext((Vector<String>) entry.getValue(), "1 ", output);
			}
			iter = train_neg.entrySet().iterator();
			// write each of the train_neg sentence as "-1 adj_Feature_index1 appear-times1 adj_Feature_index2 appear-times2 ..."
			while(iter.hasNext()) {
				Map.Entry entry = (Entry) iter.next();
				writetext((Vector<String>) entry.getValue(), "-1 ", output);
			}
			output.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * makeTrainData:
	 * 
	 * Pull out some specific comments and test the prediction accuracy.
	 * 
	 * Because the format of the test-for-prediction file is the same with the training file,
	 * the pre-classification of "1" or "-1" were nonsense but still required.
	 * The same function with makeTrainData,
	 * without form the Feature Vector.
	 * 
	 * @param product_type
	 * @throws SQLException
	 */
	public void makeTestData(String product_type) throws SQLException {
		String s = "test.txt";
		try {
			String filepath = filepath1 + s;
			File f = new File(filepath);
			if(f.exists()) {
				System.out.println("File is existing!");
				f.delete();
				f.createNewFile();
			} else {
				System.out.println("File isn't existing!");
				f.createNewFile();
			}
			BufferedWriter output = new BufferedWriter(new FileWriter(f));

			/**
			 * Positive training samples.
			 */
			PreparedStatement pjs = null;
			ResultSet rs = null;
			// Pull out such NumOfSample products as positive train samples : 
			// product type is product_type, rate is 5-star
			String QryMacString = "SELECT a.`product_name`, b.`comment`, b.`customer_satisfy` FROM product a, pro_comment b WHERE a.class_id LIKE '" +
					product_type + "'AND a.`product_no` = b.`product_no` AND b.`customer_satisfy` LIKE '%5%' LIMIT " + NumOfTrain + "," + NumOfTest;
			try {
				pjs = (PreparedStatement) this.connector.getConnections().prepareStatement(QryMacString);
				rs = pjs.executeQuery();
				while(rs.next()) {
					comment = rs.getString("comment");
					Vector<String> adj = extractAdj();
					// "1" indicate the positive classification.
					writetext(adj, "1 ", output);
				}
			} finally {
				this.connector.conn.close();
			}

			/**
			 * Negative training samples.
			 */
			// Pull out such NumOfSample products as negative train samples : 
			// product type is product_type, rate is 1-star
			QryMacString = "SELECT a.`product_name`, b.`comment`, b.`customer_satisfy` FROM product a, pro_comment b WHERE a.class_id LIKE '" +
					product_type + "'AND a.`product_no` = b.`product_no` AND b.`customer_satisfy` LIKE '%1%' LIMIT " + NumOfTrain + "," + NumOfTest;
			try {
				pjs = (PreparedStatement) this.connector.getConnections().prepareStatement(QryMacString);
				rs = pjs.executeQuery();
				while(rs.next()) {
					comment = rs.getString("comment");
					Vector<String> adj = extractAdj();
					// "-1" indicate negative classification.
					writetext(adj, "-1 ", output);
				}
			} finally {
				this.connector.conn.close();
			}
			output.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * makeAllComment:
	 * Pull out all the comments from DataBase to predict all their Sentiments.
	 * Because the format of the test-for-prediction file is the same with the training file,
	 * the pre-classification of "1" or "-1" were nonsense but still required.
	 * 
	 * @param product_type
	 * @throws SQLException
	 */
	public void makeAllComment(String product_type) throws SQLException {
		String s = "allcomment.txt";
		String com = "comments.txt";
		try {
			String filepath = filepath1 + s;
			File f = new File(filepath);
			if(f.exists()) {
				System.out.println("File is existing!");
				f.delete();
				f.createNewFile();
			} else {
				System.out.println("File isn't existing!");
				f.createNewFile();
			}

			String filepath2 = filepath1 + com;
			File f2 = new File(filepath2);
			if(f2.exists()) {
				System.out.println("File is existing!");
				f2.delete();
				f2.createNewFile();
			} else {
				System.out.println("File isn't existing!");
				f2.createNewFile();
			}
			BufferedWriter output = new BufferedWriter(new FileWriter(f));
			BufferedWriter output2 = new BufferedWriter(new FileWriter(f2));

			PreparedStatement pjs = null;
			ResultSet rs = null;
			// Get all the comments of "product_type" type.
			String QryMacString = "SELECT b.`comment_id`, b.`comment` FROM product a, pro_comment b WHERE a.`product_no` AND b.`product_no` AND a.`class_id` = '" + 
					product_type + "'";
			try {
				pjs = (PreparedStatement) this.connector.getConnections().prepareStatement(QryMacString);
				rs = pjs.executeQuery();
				int id = 0;
				Comment_id.clear();
				while(rs.next()) {
					Comment_id.add(rs.getInt("comment_id"));
					comment = rs.getString("comment");
					Vector<String> adj = extractAdj();
					// classification "1" is nonsense but required as format.
					Boolean flag = writetext(adj, "1 ", output);
				}
			} finally {
				this.connector.conn.close();
			}
			output.close();
			output2.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Train SVM Model.
	 * Training arg {"input training data path", "output model path"}
	 * 
	 * @throws IOException
	 */
	public void trainSVMModel() throws IOException {
		// Training arg {"input training data path", "output model path"}
		String [] arg = {filepath1 + "train.txt", filepath1 + "model.txt"};
		System.out.println("-------------train svm start-----------------");
		svm_train t = new svm_train();
		t.main(arg);
	}

	/**
	 * Test SVM Predication.
	 * 
	 * Testing parg {"input Testing data path", "input model path (output model path of trainSVMModel)",
	 * "output result path"}
	 * 
	 * @throws IOException
	 */
	public void testSVMModel() throws IOException {
		// Testing parg {"input Testing data path", "input model path (output model path of trainSVMModel)",
		// "output result path"}
		String [] parg = {filepath1 + "test.txt", filepath1 + "model.txt", filepath1 + "out_test.txt"};
		svm_predict p = new svm_predict();
		p.main(parg);
	}

	/**
	 * Test SVM Predication.
	 * 
	 * Testing parg {"input Testing data path", "input model path (output model path of trainSVMModel)",
	 * "output result path"}
	 * 
	 * @throws IOException
	 */
	public void judge() throws IOException, SQLException {
		// Testing parg {"input Testing data path", "input model path (output model path of trainSVMModel)",
		// "output result path"}
		String [] parg = {filepath1 + "test.txt", filepath1 + "model.txt", filepath1 + "out_test.txt"};
		svm_predict p = new svm_predict();
		// svm_predict.main_my is updated from svm_predict.main,
		// added the function to insert into DataBase.
		p.main_my(parg, Comment_id);
	}

	/**
	 * writetext:
	 * count the appear times of adjs of adjset which in Feature vector,
	 * *******NOTE************
	 * Only adj which is in the Feature vector shall be counted.
	 * *******NOTE************
	 * makeup the form of the adj - times number pair.
	 * as: 
	 * class adj1:times-no. adj2:times-no
	 * 1 14:1 16:2 33:1
	 * 
	 * @param adjset   tokenized comment sentence adj output
	 * @param s        class token
	 * @param output   BufferedWriter
	 * @return         boolean - true: there are some to write; false: nothing to write.
	 * @throws IOException
	 */
	public Boolean writetext(Vector<String> adjset, String s, BufferedWriter output) throws IOException {
		StringBuffer S = new StringBuffer();
		S.append(s);
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		// static each of the adj appear times.
		for(int i = 0; i < adjset.size(); i++) {
			// The top ratio% most frequent adj_words from samples to form the Feature vector.
			// Only write the adjset contained in the Feature Vector.
			int index = Feature.indexOf(adjset.get(i));
			if(index >= 0 && map.containsKey(index)) {
				map.put(index, map.get(index) + 1);
			} else if(index >= 0 && !map.containsKey(index)) {
				map.put(index, 1);
			}
		}
		// HashMap<Integer, Integer> map is now HashMap<adj_words index, appear-times in the sentence>

		Object[] key = map.keySet().toArray();
		Arrays.sort(key);

		// makeup the form of the adj - times number pair.
		// as: 
		// class adj1:times-no. adj2:times-no
		// 1 14:1 16:2 33:1
		for(int i = 0; i < key.length; i++) {
			S.append(key[i] + ":");
			S.append(map.get(key[i]) + " ");
		}
		if(map.size() > 0) {
			output.write(S.toString());
			output.write("\n");
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Extract all the adjs with their corresponding negative words in a comment sentence.
	 * @return   the adj vector from a comment sentence.
	 */
	public Vector<String> extractAdj_train() {
		Tokenizer token = new Tokenizer();
		Vector<String> adj = new Vector<String>();
		Vector<Item> Comment = token.tokenizer(comment);

		int index = 0;
		for(int i = 0; i < Comment.size(); i++) {
			if((Comment.get(i).getType().subSequence(0, 1)).equals("a")) {
				Vector<Item> C = new Vector<Item>();
				for(int j = index; j <= i; j++) {
					C.add(Comment.get(j));
				}
				String negString = neg(C);
				String f = Comment.get(i).getWord();
				if(negString != null) {
					f = negString + Comment.get(i).getWord();
				}
				// store the "adj - times-no" pair to the feat.
				if(feat.containsKey(f)) {
					feat.put(f, feat.get(f) + 1);
				} else {
					feat.put(f, 1);
				}
				adj.add(f);
				index = i + 1;
			}
			if((Comment.get(i).getType().subSequence(0, 1)).equals("w") || Comment.get(i).getType().equals("null")) {
				index = i + 1;
			}
		}
		return adj;
	}

	/**
	 * Extract all the adjs with their corresponding negative words in a comment sentence.
	 * Same with extractAdj_train, but not count the adj frequency which is to form the Feature Vector.
	 * @return   the adj vector from a comment sentence.
	 */
	public Vector<String> extractAdj() {
		Tokenizer token = new Tokenizer();
		Vector<String> adj = new Vector<String>();
		Vector<Item> Comment = token.tokenizer(comment);

		// index indicate a new static start.
		int index = 0;
		for(int i = 0; i < Comment.size(); i++) {
			// Extract adj
			if((Comment.get(i).getType().subSequence(0, 1)).equals("a")) {
				// Static the adj corresponding negative words.
				Vector<Item> C = new Vector<Item>();
				for(int j = index; j <= i; j++) {
					C.add(Comment.get(j));
				}
				String negString = neg(C);

				// "negString(before adj) + adj" forms the complete adj
				String f = Comment.get(i).getWord();
				if(negString != null) {
					f = negString + Comment.get(i).getWord();
				}
				// append the complete adj to the vector adj.
				adj.add(f);
				index = i + 1;
			}
			if((Comment.get(i).getType().subSequence(0, 1)).equals("w") || Comment.get(i).getType().equals("null")) {
				index = i + 1;
			}
		}
		return adj;
	}

	/**
	 *  Deal with negative words.
	 *  Cut the single negative word or a string between the negative words.
	 * @param comment
	 * @return
	 */
	public String neg(Vector<Item> comment) {
		int count = 0;
		int ind_start = -1;
		int ind_end = -1;
		for(int i = 0; i < comment.size(); i++) {
			for(int j = 0; j < neg.length; j++) {
				if(comment.get(i).getWord().equals(neg[j]) && !comment.get(i).getType().equals("a")) {
					if(ind_start == -1) {
						ind_start = i;
						ind_end = i;
					} else {
						ind_end = i;
					}
					count++;
				}
				break;
			}
		}

		if(ind_start > -1) {
			StringBuffer S = new StringBuffer();
			for(int j = ind_start; j <= ind_end; j++) {
				S.append(comment.get(j).getWord());
			}
			return S.toString();
		} else {
			return null;
		}
	}

	public void Insert_Data() {

	}

	public static void main(String argv[]) throws SQLException {
		Comment_SVM cf = new Comment_SVM();
		PreparedStatement pjs1 = null;
		ResultSet rs1 = null;
		String QryMacString1 = "SELECT class_id FROM product";
		try {
			pjs1 = (PreparedStatement) cf.connector.getConnections().prepareStatement(QryMacString1);
			rs1 = pjs1.executeQuery();
			while(rs1.next()) {
				String product_type = rs1.getString("class_id");
				System.out.println(product_type);

				cf.makeTrainData(product_type);
				cf.trainSVMModel();

				// make test data
				cf.makeTestData(product_type);
				cf.testSVMModel();

				// Use DataBase
//				cf.makeAllComment(product_type);
//				cf.judge();
			} 
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			cf.connector.conn.close();
		}
	}
}
