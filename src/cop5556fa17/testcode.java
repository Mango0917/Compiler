package cop5556fa17;

public class testcode {
	static int x;
	   
    static int inc(int dx)
    { 	
    	String str=new String();
    	int y = dx*2;
      return x+y;
    }
    public static void main(String[] args)
    {  x = 5;
       x = inc(3);
       System.out.println(x);
    }
}
