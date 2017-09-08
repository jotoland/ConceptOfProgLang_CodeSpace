public class Lex
{
	final int STRINGLING = 100;
	String star[];
	int numTokens = 0;
	int index = 0;
	int i, j = 0;
  
	public Lex(String s) 
	{
		star = new String[STRINGLING];
		String coin;
		String cha = "null";
		String chaAhead = cha;
		int end = 0;
		int start = 0;
			 
		for(int i =0; i<s.length()-1; i++){
			start = i;
			end = i+1;
			chaAhead = s.substring(start+1, end+1);
			cha = s.substring(start, end);
			if(!cha.equals(" ")){
				coin = cha;
				if(coin.equals("-") && chaAhead.equals(" ")){
					Interpreter.trueArrow = false;
				}else{
					star[numTokens++] = coin;
				}
			}
		}
		if(end<s.length()){
			cha = s.substring(start+1);
			if(s.substring(start, start+1).equals("-"))Interpreter.trueArrow = false;
			if(!cha.equals(" ")){
				coin = cha;
				star[numTokens++] = coin;
			}
		}
	}
	
	public String nextToken()
	{ 
	  return(star[index++]);
	}
  	

}
