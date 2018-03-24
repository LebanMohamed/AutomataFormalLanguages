import java.io.*;

class cfgpda
{
    // A context-free grammar
    static class CFG
    {
        int alphabet_size;
        String vars[];
        String R[][];
        String start_var;
    }

    // A push or pop transition
    static class stackTrans
    {
        int source;
        String label;
        int target;
    }

    // A pushdown automaton 
    static class PDA
    {
        int alphabet_size;
        String stack_alphabet[];
        int n_states;
        int delta[][];
        stackTrans delta_pop[];
        stackTrans delta_push[];
        int initial_state;
        int final_states[];
    }

    public static void main(String[] args) 
    {

        print_CFG(gen_cfg0(), "G0");
        print_CFG(gen_cfg1(), "G1");

        print_PDA(gen_pda0(), "A0");
        print_PDA(gen_pda1(), "A1");
        print_PDA(gen_pda2(), "A2");

        print_resulting_PDAs();

    }

    static CFG gen_cfg0()
    { 
        
        CFG G = new CFG();

        G.alphabet_size=3;
        G.vars = new String[]{ "S", "Y" };
        G.R = new String[][]{
            { "S", "0 S 0" },
            { "S", "1 S 1" },
            { "S", "2 S 2" },
            { "S", "Y" },
            { "Y", "0 1" }
        };
        G.start_var = "S";

        return G;
        
    }

    static CFG gen_cfg1()
    { 
        CFG G = new CFG();

        // TODO construct CFG of Question 1(b)
    
      G.alphabet_size=3;
      G.vars = new String[]{ "S", "X", "Y" }; G.R = new String[][]
      {
          {"S","Y"}, 
          {"S","1 X"}, 
          {"X","1 X"}, 
          {"X","0"}, 
          {"Y","Y 0"}, 
          {"Y","1 X 1"}, 
          {"Y","2 X 2"}
          

      };
       G.start_var = "S";
        return G;
        
    }


    static stackTrans gen_trans(int src, String lbl, int trg){
        stackTrans res = new stackTrans();
        res.source = src;
        res.target = trg;
        res.label = lbl;
        return res;
    }

    static PDA gen_pda0()
    {
        PDA A=new PDA();

        A.alphabet_size=2;
        A.stack_alphabet=new String[]{ "$", "0" };
        A.n_states=6;
        A.delta = new int[][]{
            {1, 0, 2},
            {1,-1, 3},
            {4, 1, 3} };
        A.delta_pop = new stackTrans[2];
        A.delta_pop[1] = gen_trans(3, "0", 4);
        A.delta_pop[0] = gen_trans(3, "$", 5);
        A.delta_push = new stackTrans[2];
        A.delta_push[0] = gen_trans(0, "$", 1);
        A.delta_push[1] = gen_trans(2, "0", 1);
        A.initial_state=0;
        A.final_states = new int[]{ 5 };

        return A;
    }


    static PDA gen_pda1()
    {
        PDA A=new PDA();

        // TODO construct PDA of Exercise 3, from Week 8 Execrises
        
        A.alphabet_size=2;
        A.stack_alphabet=new String[]{ "$", "0" };
        A.n_states=7;
        A.delta = new int[][]{
            {1, -1, 3},
            {1, 0, 2},
            {4, 1, 6},
            {6, 1, 3} };
        A.delta_pop = new stackTrans[2];
        A.delta_pop[0] = gen_trans(3, "0", 4);
        A.delta_pop[1] = gen_trans(3, "$", 5);
        A.delta_push = new stackTrans[2];
        A.delta_push[0] = gen_trans(0, "$", 1);
        A.delta_push[1] = gen_trans(2, "0", 1);
        A.initial_state=0;
        A.final_states = new int[]{ 5 };
        return A;
    }

    static PDA gen_pda2()
    {
        PDA A=new PDA();

        // TODO construct PDA of Question 1, from Week 8 Lab sheet
       
         A.alphabet_size=2;
        A.stack_alphabet=new String[]{ "$", "0","1" };
        A.n_states=8;
        A.delta = new int[][]{
            {1, 1, 6},
            {1, 0, 2},
            {1, -1, 3},
            {7, 1, 3}, 
            {4, 0, 3} };
        A.delta_pop = new stackTrans[3];
        A.delta_pop[0] = gen_trans(3, "1", 7);
        A.delta_pop[1] = gen_trans(3, "0", 4);
        A.delta_pop[2] = gen_trans(3, "$", 5);
        A.delta_push = new stackTrans[3];
        A.delta_push[0] = gen_trans(0, "$", 1);
        A.delta_push[1] = gen_trans(6, "1", 1);
        A.delta_push[2] = gen_trans(2, "0", 1);
        A.initial_state=0;
        A.final_states = new int[]{ 5 };
        return A;
    }

    static PDA build_PDA(CFG G)
    {
        PDA A=new PDA();

        // the alphabets are the same
        int n = G.alphabet_size;
        A.alphabet_size=n;

        // the stack alphabet is "$", along with all variables and all letters
        // of the original alphabet
        A.stack_alphabet=new String[n+G.vars.length+1];
        A.stack_alphabet[0]="$";
        for(int i=0; i<G.vars.length; i++) A.stack_alphabet[1+i]=G.vars[i];
        for(int i=0; i<n; i++) A.stack_alphabet[G.vars.length+1+i]=int2string(i);

        // the states of the PDA are:
        // - 3 core states: 0, 1, 2
        // - one state for each letter of the alphabet: letter i -> state 3+i
        // - one state for each variable: variable i -> state 3+n+i
        // where n is the size of the alphabet
        A.n_states = 3+n+G.vars.length;

        // the transitions of the PDA are:
        // - one input transition for each alphabet letter
        // - one push transition from 0 to 1
        // - one push transition per grammar rule
        // - one pop transition from 1 to 2
        // - one pop transition per alphabet symbol and variable
        A.delta = new int[n][];
        A.delta_push = new stackTrans[1+G.R.length];
        A.delta_pop = new stackTrans[1+n+G.vars.length];

        A.initial_state=0;
        A.final_states = new int[]{ 2 };

        // push(S$)
        A.delta_push[0] = gen_trans(0, G.start_var+" $", 1);
        // pop($)
        A.delta_pop[0] = gen_trans(1, "$", 2);

        // Counters for transitions of each kind
        int inp_cnt = 0;
        int push_cnt = 1;
        int pop_cnt = 1;

        // add transitions for each alphabet letter
        for(int i=0; i<n; i++)
        {
          // For each alphabet letter i, two transitions should be added:
          // - a pop(i) transition from state 1 to state 3+i
          // - a transition from state 3+i to 1 that inputs i
          int letter_state = 3+i;


          // TODO insert implementation
            A.delta[inp_cnt] = new int[] {letter_state, i, 1};// -a transition from state 3+1 to 1 that inputs 1
            A.delta_pop[pop_cnt] = gen_trans(i, int2string(i), letter_state);//- a pop(i) transition from state 1 to state 3+1
          pop_cnt++; inp_cnt++;
        }

        // Add transitions for each variable
        for(int i=0; i<G.vars.length; i++)
        {
          // For each variable i, we need to add:
          // - one transition from state 1 to state 3+n+i
          // - for each grammar rule of i, one transition from state 3+n+i to state 1
          // where n is the size of the alphabet
          int var_state = 3+n+i;

          // Here is the pop transition from 1 to 3+n+i
          A.delta_pop[pop_cnt] = gen_trans(1, G.vars[i], var_state);
          pop_cnt++;

          // Now loop through all grammar rules and, for each rule with variable i
          // on the LHS, add a push/epsilon transition from state 3+n+i to state 1
          for(int j=0; j<G.R.length; j++)
            {
              String[] rule=G.R[j];
              
              if(rule[0].equals(G.vars[i]))
              {
                // TODO insert implementation
                    A.delta_push[push_cnt] = gen_trans(var_state, rule[1], i);
                    push_cnt++;//if LHS is 1 then add an epsilon/push transition from state 3+n+1 to state 2
              }
            }
        }

        return A;
    }


    static void print_resulting_PDAs()
    {
        //TODO insert implementaion
        
        print_PDA(build_PDA(gen_cfg0()), " cfg0");
        print_PDA(build_PDA(gen_cfg1()), " cfg0");
            

 
    }


    // Convert an integer to String
    static String int2string(int i)
    {
        return Integer.toString(i);
    }

    // Print a CFG G, if G is a valid CFG. If not, print error message.
    static void print_CFG(CFG G, String name)
    {
        if (!is_valid_CFG(G)){ 
            System.out.println(name+" is not a valid CFG!");
            return; }
        System.out.print("\n"+name+" = (");

        // print the alphabet ...
        System.out.print("{");
        for(int i=0; i<G.alphabet_size; i++)
        {
            if(i!=0) System.out.print(", ");
            System.out.print(i);
        }
        System.out.print("}, ");

        // ... and the set of variables ...
        System.out.print("{");
        for(int i=0; i<G.vars.length; i++)
        {
            if(i!=0) System.out.print(", ");
            System.out.print(G.vars[i]);
        }
        System.out.print("}, R, ");

        // ... and the start variable ...
        System.out.println(G.start_var+")");

        // ... and the set of rules ...
        System.out.println("  where R is the set of rules:");
        for(int i=0; i<G.R.length; i++)
        {
            System.out.print("  "+G.R[i][0]+" -> ");
            if(G.R[i].length==2 && G.R[i][1].equals("")) System.out.print("??");
            for(int j=1; j<G.R[i].length; j++) System.out.print(G.R[i][j]+" ");
            System.out.println();
        }

    }

    // Print the A as a six tuple, if A is a valid PDA. If not, print error message.
    static void print_PDA(PDA A, String name)
    {

        if (!is_valid_PDA(A)){ 
            System.out.println(name+" is not a valid PDA!");
            return; }
        System.out.print("\n"+name+" = (");

        // print the alphabet ...
        System.out.print("{");
        for(int i=0; i<A.alphabet_size; i++)
        {
            if(i!=0) System.out.print(", ");
            System.out.print(i);
        }
        System.out.print("}, ");

        // print the stack alphabet ...
        System.out.print("{");
        for(int i=0; i<A.stack_alphabet.length; i++)
        {
            if(i!=0) System.out.print(", ");
            System.out.print(A.stack_alphabet[i]);
        }
        System.out.print("}, ");

        // ... and the set of states ...
        System.out.print("{");
        for(int i=0; i<A.n_states; i++)
        {
            if(i!=0) System.out.print(", ");
            System.out.print("q"+i);
        }
        System.out.print("}, delta, ");

        // ... and the initial state ...
        System.out.print("q"+A.initial_state);
        System.out.print(", ");
        
        // ... and the set of final states
        System.out.print("{");
        for(int i=0; i<A.final_states.length; i++)
        {
            if(i!=0) System.out.print(", ");
            System.out.print("q"+A.final_states[i]);
        }
        System.out.print("}");

        System.out.println(")");
        
        // ... and the transition relation ...
        System.out.println("  where delta is the transition relation:");
        System.out.print("  {");
        int cnt = 0;
        for(int i=0; i<A.delta.length; i++)
        {
            if(cnt!=0) System.out.print(",\n    "); else System.out.print(" "); cnt++;
            if (A.delta[i][1] != -1) 
                System.out.print("(q"+A.delta[i][0]+", "+A.delta[i][1]+", q"+A.delta[i][2]+")");
            else            
                System.out.print("(q"+A.delta[i][0]+", eps, q"+A.delta[i][2]+")");
        }
        for(int i=0; i<A.delta_push.length; i++)
        {
            if(cnt!=0) System.out.print(",\n    "); else System.out.print(" "); cnt++;
            System.out.print("(q"+A.delta_push[i].source+", push("+A.delta_push[i].label+"), q"+A.delta_push[i].target+")");
        }
        for(int i=0; i<A.delta_pop.length; i++)
        {
            if(cnt!=0) System.out.print(",\n    "); else System.out.print(" "); cnt++;
            System.out.print("(q"+A.delta_pop[i].source+", pop("+A.delta_pop[i].label+"), q"+A.delta_pop[i].target+")");
        }
        System.out.println(" }");

    }

    
    // Checks if G is a valid CFG. If not, it also prints an error message
    static boolean is_valid_CFG(CFG G)
    {
        String check=check_CFG(G);
        // Note that, when we want to compare strings s1 and s2, 
        // we use s1.equals(s2) instead of s1==s2. This is because 
        // s1==s2 can return false even if s1 and s2 are the same 
        if(!check.equals("OK")){
            System.out.println("\nGrammar is not OK -- "+check);
            return false;
        }
        return true;
    }

    // Perform all kinds of checks on a CFG
    // return "OK" if the CFG is OK, otherwise an error message
    static String check_CFG(CFG G)
    {
        // Check the set of variables is non empty, or null, and contains no duplicates
        if(G.vars==null) return ("Bad set of variables (null)");
        if(G.vars.length<=0) return ("Bad number of variables: "+G.vars.length);
        for(int i=0; i<G.vars.length; i++)
            for(int j=i+1; j<G.vars.length; j++)
        if(G.vars[i]==G.vars[j]) 
            return ("This variable appears more than once: "+G.vars[i]);

        // Check that the production rules are all valid
        if(G.R==null) return ("Bad set of rules (null)");
        for(int i=0; i<G.R.length; i++){
            if(G.R[i]==null) return ("Bad rule "+i+" (null)");
            if(G.R[i].length!=2) 
                return ("Bad rule length ("+G.R[i].length+") in rule "+i);
            if(!is_valid_var(G,G.R[i][0]))
                return ("Bad LHS variable ("+G.R[i][0]+") in rule "+i);
            if(!is_valid_rule_RHS(G,G.R[i][1]))
                return ("Bad RHS ("+G.R[i][1]+") in rule "+i);
        };

        // Check that start variable is valid
        if(!is_valid_var(G,G.start_var))
            return ("Bad start variable: "+G.start_var);
 
        return "OK";
    }

    static boolean is_valid_var(CFG G, String v)
    {
        for(int i=0; i<G.vars.length; i++){
            if(v.equals(G.vars[i])) return true;
        }
        return false;
    }

    static boolean is_valid_symbol(CFG G, String s)
    {
        // all alphabet symbols are valid symbols
        for(int i=0; i<G.alphabet_size; i++){
            if(s.equals(int2string(i))) return true;
        }
        return false;
    }

    static boolean is_valid_rule_RHS(CFG G, String s)
    {
        if(s.length()==0) return true;
        int i = splitter(s);
        if(i==-1) return (is_valid_var(G,s)||is_valid_symbol(G,s));
        if(i==0||i==(s.length()-1)) return false;
        String x = s.substring(0,i);
        if(!is_valid_var(G,x) && !is_valid_symbol(G,x)) return false;
        return is_valid_rule_RHS(G,s.substring(i+1,s.length()));
    }

    // Returns the first occurence of ' ' in s (-1 if none)
    static int splitter(String s)
    {
        for(int i=0; i<s.length(); i++) 
            if(s.substring(i,i+i).equals(" ")) return i;
        return -1;
    }
    
    // Checks if A is a valid PDA. If not, it also prints an error message
    static boolean is_valid_PDA(PDA A)
    {
        String check=check_PDA(A);
        if(!check.equals("OK")){
            System.out.println("\nPDA is not OK -- "+check);
            return false;
        }
        return true;
    }

    // Perform all kinds of checks on a PDA
    // return "OK" if the PDA is OK, otherwise an error message
    static String check_PDA(PDA A)
    {

        // Check the alphabet size is valid
        if(A.alphabet_size<0) return ("Bad alphabet size: "+A.alphabet_size);

        // Check the stack alphabet size is valid
        if(A.stack_alphabet==null) return ("Bad stack alphabet (null)");

        // Check the number of states is valid
        if(A.n_states<=0) return ("Bad number of states: "+A.n_states);

        // Check the initial state is a valid state
        if(!is_valid_state(A,A.initial_state))
            return ("Bad inital state: "+A.initial_state);

	// Check that the input transition relation is valid 
        if(A.delta==null) return ("Bad input transition relation (null)");
	for(int i=0; i<A.delta.length; i++){
            if(A.delta[i]==null) return ("Bad input transition "+i+" (null)");
	    if(A.delta[i].length!=3)
		return ("Bad transition length ("+A.delta[i].length+") in input transition "+i);
	    if(!is_valid_state(A,A.delta[i][0]))
		return ("Bad state ("+A.delta[i][0]+") in input transition "+i);
	    if(!is_valid_state(A,A.delta[i][2]))
		return ("Bad state ("+A.delta[i][2]+") in input transition "+i);
	    if(!is_valid_symbol(A,A.delta[i][1]) && !(A.delta[i][1]==-1))
		return ("Bad symbol ("+A.delta[i][1]+") in input transition "+i);
	}

	// Check that the push transition relation is valid 
        if(A.delta_push==null) return ("Bad push transition relation (null)");
	for(int i=0; i<A.delta_push.length; i++){
	    if(!is_valid_state(A,A.delta_push[i].source))
		return ("Bad state ("+A.delta_push[i].source+") in push transition "+i);
	    if(!is_valid_state(A,A.delta_push[i].target))
		return ("Bad state ("+A.delta_push[i].target+") in push transition "+i);
	    if(!is_valid_push_string(A,A.delta_push[i].label))
		return ("Bad string label ("+A.delta_push[i].label+") in push transition "+i);		
	}

	// Check that the pop transition relation is valid
        if(A.delta_pop==null) return ("Bad pop transition relation (null)");
	for(int i=0; i<A.delta_pop.length; i++){
	    if(!is_valid_state(A,A.delta_pop[i].source))
		return ("Bad state ("+A.delta_pop[i].source+") in pop transition "+i);
	    if(!is_valid_state(A,A.delta_pop[i].target))
		return ("Bad state ("+A.delta_pop[i].target+") in pop transition "+i);
	    if(!is_valid_pop_string(A,A.delta_pop[i].label))
		return ("Bad string label ("+A.delta_pop[i].label+") in pop transition "+i);
	}
	
        // Check that final states are valid
        if(A.final_states==null) return ("Bad final states (null)");
        for(int i=0; i<A.final_states.length; i++){
            if(!is_valid_state(A,A.final_states[i]))
                return ("Bad final state: "+A.final_states[i]);
        }

        return "OK";
    }

    static boolean is_valid_state(PDA A,int i)
    {
        return (i>=0 && i<A.n_states);
    }
    
    static boolean is_valid_symbol(PDA A,int a)
    {
        return (a>=0 && a<A.alphabet_size);
    }
    
    static boolean is_valid_stack_symbol(PDA A, String s)
    {
        for(int i=0; i<A.stack_alphabet.length; i++)
            if(s.equals(A.stack_alphabet[i]))
                return true;

        return false;
    }

    static boolean is_valid_pop_string(PDA A, String s)
    {
        return is_valid_stack_symbol(A, s);
    }

    static boolean is_valid_push_string(PDA A, String s)
    {
        if(s.length()==0) return true;
        int i = splitter(s);
        if(i==-1) return is_valid_stack_symbol(A,s);
        if(i==0||i==(s.length()-1)) return false;
        String x = s.substring(0,i);
        if(!is_valid_stack_symbol(A,x)) return false;
        return is_valid_push_string(A,s.substring(i+1,s.length()));
    }

}

