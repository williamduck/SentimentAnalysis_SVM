package MySVM;

public class Item {
	private String word = null;
	private String type = null;
	private String orientation = null;
	
	public Item() {

	}

	/**
	 * Item(String word, String type)
	 * @param word
	 * @param type
	 */
	public Item(String word, String type) {
		this.word = word;
		this.type = type;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOrientation() {
		return orientation;
	}

	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}


}
