package com.company;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.simmetrics.StringMetric;
import org.simmetrics.metrics.CosineSimilarity;
import org.simmetrics.simplifiers.Simplifiers;
import org.simmetrics.tokenizers.Tokenizers;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.font.LineMetrics;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import static org.simmetrics.builders.StringMetricBuilder.with;


class WordStart {
	public WordStart(int i, int j, boolean is_across, int length) {
	this.i = i;
	this.j = j;
	this.is_across = is_across;
	this.length = length;
	}
	int i;
	int j;
	boolean is_across; // if false, this is a down start
	int length;
}

public class PuzzleGUI extends JPanel{
	//these variable are for making random puzzles
	static int xCurrSquare=0;
	static int yCurrSquare=0;
	
	static int xss=500; //xss = x screen size
	static int yss=500; //yss = y screen size
	
	static int word_starts[][];
	static final int WALL = -1;
	static final int BLANK = 0;
	static final int ACROSS = 1;
	static final int DOWN = 2;
	static final int BOTH = 3;

	static final String[] myCluelist = new String[10];
	static final String[] myLabelist = new String[10];

	static ArrayList<String> DATACLUE = new ArrayList<>();
	static ArrayList<String> DATAANSWER = new ArrayList<>();


	static ArrayList<String> wordsUsed = new ArrayList<String>();
	
	static ArrayList<String> words = new ArrayList<String>();
	
	static ArrayList<Integer> counts = new ArrayList<Integer>();
	

	public void paintComponent(Graphics oldg){
	Graphics2D g = (Graphics2D)oldg;
	g.setRenderingHint(
	        RenderingHints.KEY_ANTIALIASING,
	        RenderingHints.VALUE_ANTIALIAS_ON);
	g.setColor(Color.white);
	g.fillRect(0, 0, 500, 500);
	
	if (word_starts == null) {
	return;
	}g.setColor(Color.black);
        System.out.println("The puzzle is drawed");
	graphics(g);
	drawOutline(g);
	}

	
	private void drawOutline(Graphics2D g) {
	// System.out.println("drawOutline");
	int xStart =xss*2/11;
	int yStart =yss*2/11;
	//g.drawRect(xStart, yStart, xss*7/11, yss*7/11);
	//g.drawRect(100, 100, 1*7/11, 100);
	for(int i=0;i<=word_starts[0].length;i++){
	int x = (int)(xStart+7.0/11*xss*i/word_starts[0].length);
	g.drawLine(x, yStart, x , (int)(yStart+yss*7.0/11));
	}
	for(int j=0;j<=word_starts[1].length;j++){
	int y=(int) (yStart+7.0/11*yss*j/word_starts[1].length);
	g.drawLine(xStart, y, (int)(xStart+xss*7.0/11), y);
	}
	drawBox(xCurrSquare, yCurrSquare, g, Color.black);
	
	}
	
	public static void fillBox(int i, int j, Graphics2D g, Color color){
	int xStart =xss*2/11;
	int yStart =yss*2/11;
	g.setColor(color);
	g.fillRect((int) (xStart + j * xss * 7.0 / 11 / word_starts[0].length), yStart + (int) (i * yss * 7.0 / 11 / word_starts[0].length), (int) (7.0 / 11 * xss / word_starts[0].length), (int) (7.0 / 11 * yss / word_starts[0].length));
	}
	public static void drawBox(int i, int j, Graphics2D g, Color color){
	int xStart =xss*2/11;
	int yStart =yss*2/11;
	g.setColor(color);
	g.drawRect((int) (xStart + j * xss * 7.0 / 11 / word_starts[0].length), yStart + (int) (i * yss * 7.0 / 11 / word_starts[0].length), (int) (7.0 / 11 * xss / word_starts[0].length), (int) (7.0 / 11 * yss / word_starts[0].length));
	}
	private void graphics(Graphics2D g) {
	Font f = new Font("Arial", Font.PLAIN, 12);
	g.setFont(f);
	int count=0;
	int xStart =xss*2/11;
	int yStart =yss*2/11;
	g.setColor(Color.black);
	for(int i=0;i<word_starts.length;i++){
	for(int j=0;j<word_starts[0].length;j++){
	if(word_starts[i][j]==WALL){
	fillBox(i,j,g,Color.black);
	} else if(word_starts[i][j]==BLANK) {
	}else{
	count++;
	LineMetrics lm = f.getLineMetrics(".", g.getFontRenderContext());
	g.drawString(count+".", (int)(2+xStart+j*xss*7.0/11/word_starts[0].length), (int)(2+lm.getAscent() + yStart+i*yss*7.0/11/word_starts[0].length));
	}

	}
	}
	
	}
	public static void main(String[] args) throws IOException {
		data n=new data();
		n.printClues();
		DATACLUE=n.getclues();
		DATAANSWER=n.getAnswers();
	boolean[] myBoxlist = new boolean[25];

        /*String doc = Jsoup.connect("https://www.nytimes.com/crosswords/game/mini").get().outerHtml().toString();
        //String text = doc.body().text();
        System.out.println(doc);

        Elements hTags = doc.select("h2");*/
    	int counter = 0;
    	int counter2 = 0;
    	int counter3 = 0;
    	try {
    	  System.out.println("-GOING TO https://www.nytimes.com/crosswords/game/mini-");
    	  System.out.println();
    	  // fetch the document over HTTP
    	      Document doc = Jsoup.connect("https://www.nytimes.com/crosswords/game/mini").get();
    	      
    	  System.out.println("-GET THE SOURCE CODE-");
    	      // get the page title
    	      String title = doc.title();
    	      System.out.println("title: " + title);
    	  System.out.println();

    	      // get all links in page
    	      Elements links = doc.select("a[href]");
    	      Elements hlist = doc.select("h2");
    	      Elements clabels = doc.select("li[value]");
    	      Elements clues = doc.select("li[value]");
    	      Elements boxes = doc.select("div[class]");
    	      for (Element clabel : clabels) {
      	        
      	        myLabelist[counter3] = clabel.attr("value");
      	        counter3++;
      	        
      	      }
    	  
    	      System.out.println("-TAKING THE CLUES FROM SOURCE CODE-");
    	      for (Element clue : clues) {
        	        System.out.print("clue " + myLabelist[counter] + ". ");
        	        System.out.println(clue.text());
        	        myCluelist[counter] = clue.text();
        	        counter++;
        	      }
	    	  System.out.println();
    	      System.out.println("-DEFINING THE PLACE OF THE BLACK CELLS FROM SOURCE CODE-");
    	      for (Element box : boxes) {
        	      if((box.attr("class")).equals("flex-cell ")){
          	          myBoxlist[counter2] = true;
          	          counter2++;
        	    	  System.out.println(counter2 + ". cell: " + "white");
        	      }
        	      if((box.attr("class")).equals("flex-cell black")){
          	          myBoxlist[counter2] = false;
          	          counter2++;
        	    	  System.out.println(counter2 + ". cell: " + "black");
        	      }
        	      //System.out.println(counter2);
        	      }
    	      
    	}catch (IOException e) {
    	    e.printStackTrace();
    	    }
    	SwingUtilities.invokeLater(new Runnable() {
	public void run() {
	JFrame frame = new JFrame("NY Times Crossword Puzzle");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setVisible(true);
	frame.setSize(1230, 500);
	
	
                JPanel gui = new JPanel(new BorderLayout(1,0));
                gui.setBackground(Color.BLACK);
                gui.setBorder(new EmptyBorder(2,2,2,2));

        	System.out.println("In here, the clues of ACROSS are written to the panel");
                JPanel panel1 = new JPanel();
                panel1.setBorder(new EmptyBorder(15,20,20,20));
                panel1.setBackground(Color.WHITE);
	JLabel label1=new JLabel("<html>ACROSS" + "<br>" + "<br>" + myLabelist[0]+ ". " + myCluelist[0]  
	+ "<br>" + myLabelist[1] + ". " + myCluelist[1]
	+ "<br>" + myLabelist[2] + ". " + myCluelist[2]
	+ "<br>" + myLabelist[3] + ". " + myCluelist[3]
	+ "<br>" + myLabelist[4] + ". " + myCluelist[4]
	+	"</html>", SwingConstants.CENTER);
	
	Font font = new Font("Courier", Font.BOLD,16);
	label1.setFont(font);
	panel1.add(label1);
                gui.add(panel1, BorderLayout.WEST);
                
                
        	System.out.println("In here, the clues of DOWN are written to the panel");
        	System.out.println();
                JPanel panel2 = new JPanel();
                panel2.setBorder(new EmptyBorder(15,20,20,20));
                panel2.setBackground(Color.WHITE);
	JLabel label2=new JLabel("<html>DOWN" + "<br>" + "<br>" + myLabelist[5]+ ". " + myCluelist[5]  
	+ "<br>" + myLabelist[6] + ". " + myCluelist[6]
	+ "<br>" + myLabelist[7] + ". " + myCluelist[7]
	+ "<br>" + myLabelist[8] + ". " + myCluelist[8]
	+ "<br>" + myLabelist[9] + ". " + myCluelist[9]
	+	"</html>", SwingConstants.CENTER);
	panel2.add(label2);
	gui.add(panel2, BorderLayout.EAST);
	Font font2 = new Font("Courier", Font.BOLD,16);
	label2.setFont(font2);
                frame.add(gui, BorderLayout.EAST);
	
	PuzzleGUI game = new PuzzleGUI();
	frame.add(game, BorderLayout.CENTER);
	
	frame.validate();
        
            }
        });
	System.out.println("Now placing black cells to the puzzle");
	boolean hasLetter[][] = {
	{myBoxlist[0], myBoxlist[1], myBoxlist[2], myBoxlist[3], myBoxlist[4]},
	{myBoxlist[5], myBoxlist[6], myBoxlist[7], myBoxlist[8], myBoxlist[9]},
	{myBoxlist[10], myBoxlist[11], myBoxlist[12], myBoxlist[13], myBoxlist[14]},
	{myBoxlist[15], myBoxlist[16], myBoxlist[17], myBoxlist[18], myBoxlist[19]},
	{myBoxlist[20], myBoxlist[21], myBoxlist[22], myBoxlist[23], myBoxlist[24]},
	};

	
	word_starts = new int[hasLetter.length][hasLetter.length];
	char board[][] = new char[hasLetter.length][hasLetter.length];
	
	for (int i = 0; i < word_starts.length; i++) {
	for (int j = 0; j < word_starts[0].length; j++) {
	if (hasLetter[i][j] == false) {
	word_starts[i][j] = WALL;
	} else {
	word_starts[i][j] = BLANK;
	}
	}
	}
	for (int i = 0; i < word_starts.length; i++) {
	for (int j = 0; j < word_starts[0].length; j++) {
	int thing=0;
	if(hasLetter[i][j]==false){
	continue;
	}
	if((i==0 || hasLetter[i-1][j]==false) && // there is a wall above us AND
	(i < 4 && hasLetter[i+1][j]==true)) { // no wall below us
	word_starts[i][j]=DOWN;
	thing++;
	}
	if((j==0 || hasLetter[i][j-1]==false) &&
	(j < 4 && hasLetter[i][j+1]==true)){
	if(thing==1){
	word_starts[i][j]=BOTH;
	}
	else{
	word_starts[i][j]=ACROSS;
	}
	}
	}
	}
	
	//System.out.println("Word starts:");
	//printStarts(word_starts);
	System.out.println();
	
	ArrayList<WordStart> word_start_list = new ArrayList<WordStart>();
	for (int i = 0; i < word_starts.length; i++) {
	for (int j = 0; j < word_starts[0].length; j++) {
	if (word_starts[i][j] == ACROSS || word_starts[i][j] == BOTH) {
	word_start_list.add(new WordStart(i, j, true, findWordLength(word_starts, i, j, true)));
	}
	if (word_starts[i][j] == DOWN || word_starts[i][j] == BOTH) {
	word_start_list.add(new WordStart(i, j, false, findWordLength(word_starts, i, j, false)));
	}
	}
	}
	
	counts = new ArrayList<Integer>();
	for (int i = 0; i < word_start_list.size(); i++) {
	counts.add(0);
	}

		StringMetric metric =
				with(new CosineSimilarity<String>())
						.simplify(Simplifiers.toLowerCase(Locale.ENGLISH))
						.simplify(Simplifiers.removeDiacritics())
						.simplify(Simplifiers.replaceNonWord())
						.tokenize(Tokenizers.whitespace())
						.tokenize(Tokenizers.qGram(3))
						.build();

		//StringMetric metric = StringMetrics.cosineSimilarity();
		ArrayList<String> matched=new ArrayList<>();
		ArrayList<String> answr=new ArrayList<>();
		for (int i=0;i<myCluelist.length;i++)
		{
			for(int g=0;g<DATACLUE.size();g++)
			{
				double sim=similarity(DATACLUE.get(g),myCluelist[i]);
				float result = metric.compare(DATACLUE.get(g),myCluelist[i]);
				if(result>=0.6)
				{matched.add(myCluelist[i]);
				answr.add(DATAANSWER.get(g));}
			}

		}

		for(int x=0;x<matched.size();x++)
		{System.out.println("MATCHED CLUE: "+matched.get(x)+"ANSWER: "+answr.get(x));

		}
	
	}

	static int findWordLength(int[][] word_starts, int i, int j,
	boolean is_across) {
	int len;
	for (len = 1; len < word_starts.length; len++) {
	if (is_across) {
	j++;
	} else {
	i++;
	}
	if (i >= word_starts.length || j >= word_starts.length || word_starts[i][j] == WALL) {
	break;
	}
	}
	return len;
	}

	private static boolean tryToSolve(ArrayList<WordStart> word_starts, char[][] board, int cur_word_start) {
	if (cur_word_start >= word_starts.size()) {
	return true;
	}
	
	counts.set(cur_word_start, counts.get(cur_word_start) + 1);
	WordStart ws = word_starts.get(cur_word_start);
	//System.out.println("Finding a word for " + ws.i + ", " + ws.j + ", with length " + ws.length + " -- across? " + ws.is_across);
	
	// Remember which characters in this word were already set
	boolean had_letter[] = new boolean[ws.length];
	for (int i = 0; i < ws.length; i++) {
	had_letter[i] = true;
	}
	int r=ws.i;
	int c=ws.j;
	for (int i = 0; i < ws.length; i++) {
	if (board[r][c] == 0) {
	had_letter[i] = false;
	}
	if (ws.is_across)
	c++;
	else
	r++;
	}
	
	// Find a word that fits here, given the letters already on the board
	for (int i = 0; i < words.size(); i++) {
	String word = words.get(i);
	
	if (!wordsUsed.contains(word) && word.length() == ws.length) {
	boolean word_fits = true;
	r=ws.i;
	c=ws.j;
	for (int j = 0; j < ws.length; j++) {
	if (board[r][c] != 0 && board[r][c] != word.charAt(j)) {
	word_fits = false;
	break;
	}
	if (ws.is_across)
	c++;
	else
	r++;
	}
	
	if (word_fits) {
	// Place this word on the board
	wordsUsed.add(word);
	r=ws.i;
	c=ws.j;
	for (int j = 0; j < ws.length; j++) {
	board[r][c] = word.charAt(j);
	if (ws.is_across)
	c++;
	else
	r++;
	}
	
	// If puzzle can be solved this way, we're done
	if (tryToSolve(word_starts, board, cur_word_start + 1)) {
	return true;
	}
	
	// If not, take up letters that we placed and try a different word
	r=ws.i;
	c=ws.j;
	for (int j = 0; j < ws.length; j++) {
	if (!had_letter[j])
	board[r][c] = 0;
	if (ws.is_across)
	c++;
	else
	r++;
	}
	
	wordsUsed.remove(word);
	}
	}
	}
	
	// If no word can work, return false.
	return false;
	
	}

	public static ArrayList<String> matchedClues()
	{
		ArrayList<String> matched=new ArrayList<>();
		for (int i=0;i<myCluelist.length;i++)
		{
			for(int g=0;g<DATACLUE.size();g++)
			{
				if(DATACLUE.get(g).contains(myCluelist[i]))
					matched.add(myCluelist[i]);
			}

		}

		for(int x=0;x<matched.size();x++)
		{System.out.println("MATCHED CLUE:");

		System.out.println(matched.get(x));
		}

		return matched;
	}

	private static void printSolution(int[][] word_starts, char[][] board) {
	for (int i = 0; i < word_starts.length; i++) {
	for (int j = 0; j < word_starts[0].length; j++) {
	int ws=word_starts[i][j];
	if(ws==WALL){
	System.out.print("_ ");
	} else {
	System.out.print(board[i][j] + " ");
	}
	}
	System.out.println();
	}
	}

	/**
	 * Calculates the similarity (a number within 0 and 1) between two strings.
	 */
	public static double similarity(String s1, String s2) {
		String longer = s1, shorter = s2;
		if (s1.length() < s2.length()) { // longer should always have greater length
			longer = s2; shorter = s1;
		}
		int longerLength = longer.length();
		if (longerLength == 0) { return 1.0; /* both strings are zero length */ }
    /* // If you have StringUtils, you can use it to calculate the edit distance:
    return (longerLength - StringUtils.getLevenshteinDistance(longer, shorter)) /
                               (double) longerLength; */
		return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

	}

	// Example implementation of the Levenshtein Edit Distance
	// See http://rosettacode.org/wiki/Levenshtein_distance#Java
	public static int editDistance(String s1, String s2) {
		s1 = s1.toLowerCase();
		s2 = s2.toLowerCase();

		int[] costs = new int[s2.length() + 1];
		for (int i = 0; i <= s1.length(); i++) {
			int lastValue = i;
			for (int j = 0; j <= s2.length(); j++) {
				if (i == 0)
					costs[j] = j;
				else {
					if (j > 0) {
						int newValue = costs[j - 1];
						if (s1.charAt(i - 1) != s2.charAt(j - 1))
							newValue = Math.min(Math.min(newValue, lastValue),
									costs[j]) + 1;
						costs[j - 1] = lastValue;
						lastValue = newValue;
					}
				}
			}
			if (i > 0)
				costs[s2.length()] = lastValue;
		}
		return costs[s2.length()];
	}

	public static void printSimilarity(String s, String t) {
		System.out.println(String.format(
				"%.3f is the similarity between \"%s\" and \"%s\"", similarity(s, t), s, t));
	}


}