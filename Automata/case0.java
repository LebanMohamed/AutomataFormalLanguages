/*
 
 LEBAN MOHAMED
 Student ID: 140397653
 
 */
import java.io.*;

class case0
{
    // Finite-state automata with epsilon-transitions
    static class FSAe
    {
        int alphabet_size;
        int n_states;
        int delta[][];
        int initial_state;
        int final_states[];
    }

    public static void main(String[] args) 
    {
        FSAe A=new FSAe();

        // TODO create the automaton by defining the fields of A appropriately
        // for example, create an automaton with:
        // ... an alphabet {0,1}
       
        A.alphabet_size = 3;
        A.n_states = 3;
        A.delta = new int[][] {{0, 0, 0}, {0, -1, 1}, {1, 1, 1}, {1, -1, 2}, {2, 2, 2}};
        A.initial_state = 0;
        A.final_states = new int[] {2};

      
        
        // Print the automaton A, after checking it is correctly formatted
        String check=check_FSAe(A);
        if(!check.equals("OK")){
           System.out.println("A is not OK -- "+check);
           return;
        }
        print_FSAe(A,"A");

        // Now compute the epsilon-closure of A
        boolean[][] eClos = e_closure(A);
        print_eclosure(eClos);

        // Compute the equivalent FSA without epsilon-transitions and print it 
        //
        // NOTE: you need to fill in the code of e_remove in order for this 
        // step to produce a meaningful automaton!
        FSAe A_noepsilon = e_remove(A);  
        print_FSAe(A_noepsilon,"A'");

    }

    // Given an FSAe A, compute its epsilon-closure relation
    static boolean[][] e_closure(FSAe A)
    {
        boolean[][] eClos = new boolean[A.n_states][A.n_states];
        // initialise the matrix with diagonal elements to true
        for (int i=0; i<A.n_states; i++) { eClos[i][i] = true; }
        boolean finish=true;
        do {
          finish=true;
          for (int i=0; i<A.delta.length; i++) 
             if (A.delta[i][1]==-1){
                 int src = A.delta[i][0];
                 int trg = A.delta[i][2];
                 for (int j=0; j<A.n_states; j++) {
                   if (eClos[trg][j] && !eClos[src][j]) {
                     eClos[src][j] = true;
                     finish = false;
                   } 
                 } 
             }
        }
        while (!finish);
        return eClos;
    } 

    // Given an FSAe A, return an equivalent one without epsilon-transitions
    static FSAe e_remove(FSAe A)
    {
        // Step 1: compute the epsilon-closure
        boolean[][] eClos = e_closure(A);

        // Step 2: compute the new transition relation
        int trRel[][] = new int[0][0];
        for (int i=0; i<A.n_states; i++) {
            for (int j=0; j<A.n_states; j++) {
                if (eClos[i][j]) {
                    for(int k=0; k<A.delta.length; k++){
                        if(A.delta[k][0] == j && A.delta[k][1] !=-1){
                            trRel = add_transition(trRel, i, A.delta [k][1], A.delta[k][2]);
                        }
                    }
                }
            }
        }
        // TODO (make sure the new transition relation is named trRel)
        // for now, we just create an empty relation so that we compile
        

        // Step 3: compute the new set of final states
        int finals[] = new int[0];
        for (int i=0; i<A.n_states; i++) {
            for (int j=0; j<A.n_states; j++) { 
                if (eClos[i][j] && is_final(A,j)) 
                    finals = add_final(finals,i);            
            }
        }

        // Step 4: return the new automaton
        FSAe A_noeps = new FSAe();
        A_noeps.alphabet_size=A.alphabet_size;
        A_noeps.n_states=A.n_states;
        A_noeps.delta=trRel;
        A_noeps.initial_state=A.initial_state;
        A_noeps.final_states=finals;
        return A_noeps;
    }

    static int[][] add_transition(int[][] trRel, int src, int let, int trg)
    {
        boolean exists=false;
        int newRel[][] = new int[trRel.length+1][3];
        for (int i=0; i<trRel.length; i++) {
            newRel[i] = trRel[i];
            if (trRel[i][0]==src && trRel[i][1]==let && trRel[i][2]==trg) exists=true;
        }
        if (exists) { return trRel; }
        else {
            newRel[trRel.length][0] = src;
            newRel[trRel.length][1] = let;
            newRel[trRel.length][2] = trg;
            return newRel;
        }
    } 

    static int[] add_final(int[] finals, int st)
    {
        boolean exists=false;
        int newFinals[] = new int[finals.length+1];
        for (int i=0; i<finals.length; i++) {
            newFinals[i] = finals[i];
            if (finals[i]==st) exists=true;
        }
        if (exists) { return finals; }
        else {
            newFinals[finals.length] = st;
            return newFinals;
        }
    }

    static boolean is_final(FSAe A, int st)
    {
        boolean it_is = false;
        for (int i=0; i<A.final_states.length; i++) {
            if (A.final_states[i]==st) it_is = true;
        }
        return it_is;
    }

    // Perform all kinds of checks on a FSA
    // return "OK" if the FSA is OK, otherwise an error message
    static String check_FSAe(FSAe A)
    {
        // Check the alphabet size is valid
        if(A.alphabet_size<0) return ("Bad alphabet size: "+A.alphabet_size);
        // Check the number of states is valid
        if(A.n_states<=0) return ("Bad number of states: "+A.n_states);
        // Check the initial state is a valid state
        if(!is_valid_state(A,A.initial_state))
            return ("Bad inital state: "+A.initial_state);
        // Check that the transition relation is not empty
        if (A.delta==null) return ("Null transition relation");
        // Check that the transition relation has valid transitions
        for(int i=0; i<A.delta.length; i++){
            if (A.delta[i]==null) return ("Null transition "+i);
            if(A.delta[i].length!=3) 
                return ("Bad transition length ("+A.delta[i].length+") in transition "+i);
            if(!is_valid_state(A,A.delta[i][0]))
                return ("Bad state ("+A.delta[i][0]+") in transition "+i);
            if(!is_valid_state(A,A.delta[i][2]))
                return ("Bad state ("+A.delta[i][2]+") in transition "+i);
            if(!is_valid_symbol(A,A.delta[i][1]))
                return ("Bad symbol ("+A.delta[i][1]+") in transition "+i);
        }
        // Check that final states are valid
        if (A.final_states==null) return ("Null final states");
        for(int i=0; i<A.final_states.length; i++){
            if(!is_valid_state(A,A.final_states[i]))
                return ("Bad final state: "+A.final_states[i]);
        }
        return "OK";
    }      

    static boolean is_valid_state(FSAe A,int i)
    {
        return (i>=0 && i<A.n_states);
    }

    static boolean is_valid_symbol(FSAe A,int a)
    {
        return (a>=-1 && a<A.alphabet_size);
    }

    // Print the given epsilon-closure relation
    static void print_eclosure(boolean[][] eClos)
    {
        System.out.print("\n\u03B5-closure:\n    |");
        for(int i=0; i<eClos.length; i++)
            System.out.print(" q"+i+" |");
        System.out.print("\n-----");
        for(int i=0; i<eClos.length; i++)
            System.out.print("-----");
        for(int i=0; i<eClos.length; i++)
            for(int j=-1; j<eClos.length; j++) {
              if(j==-1) System.out.print("\n q"+i+" |");
              else {
                  if(eClos[i][j]) System.out.print("yes |");
                  else System.out.print("no  |");
              }
            } 
        System.out.print("\n");
    }

    // Print the automaton as a five tuple
    static void print_FSAe(FSAe A, String name)
    {
        System.out.print("\n"+name+" = (");
        // print the alphabet ...
        System.out.print("{");
        for(int i=0; i<A.alphabet_size; i++)
        {
            if(i!=0) System.out.print(", ");
            System.out.print(i);
        }
        System.out.print("}, ");
        // ... and the set of states ...
        System.out.print("{");
        for(int i=0; i<A.n_states; i++)
        {
            if(i!=0) System.out.print(", ");
            System.out.print("q"+i);
        }
        System.out.print("}, ");
        // ... and the transition relation ...
        System.out.print("{");
        for(int i=0; i<A.delta.length; i++)
        {
            if(i!=0) System.out.print(", ");
            if (A.delta[i][1] != -1) 
                System.out.print("(q"+A.delta[i][0]+", "+A.delta[i][1]+", q"+A.delta[i][2]+")");
            else            
                System.out.print("(q"+A.delta[i][0]+", \u03B5, q"+A.delta[i][2]+")");
        }
        System.out.print("}, ");
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
    }

    static FSAe get_example(int i)
    {
	FSAe a = new FSAe();
	
	switch (i) {
	    case 0:
		a.alphabet_size = 3;
		a.n_states = 3;
		a.delta = new int[][] {{0, 0, 0}, {0, -1, 1}, {1, 1, 1}, {1, -1, 2}, {2, 2, 2}};
		a.initial_state = 0;
		a.final_states = new int[] {2};
		return a;
	    case 1:
		a.alphabet_size = 1;
		a.n_states = 3;
		a.delta = new int[][] {{0, -1, 1}, {1, -1, 2}};
		a.initial_state = 0;
		a.final_states = new int[] {2};
		return a;
	    case 2:
		a.alphabet_size = 2;
		a.n_states = 3;
		a.delta = new int[][] {{0, -1, 0}, {0, 0, 1}, {1, -1, 1}, {1, 1, 2}, {2, -1, 2}};
		a.initial_state = 0;
		a.final_states = new int[] {2};
		return a;
	    case 3:
		a.alphabet_size = 1;
		a.n_states = 2;
		a.delta = new int[][] {{0, 0, 1}, {1, -1, 0}};
		a.initial_state = 0;
		a.final_states = new int[] {1};
		return a;
	    case 4:
		a.alphabet_size = 2;
		a.n_states = 3;
		a.delta = new int [][] {{0, -1, 1}, {1, -1, 0}, {1, 0, 2}, {2, 1, 1}};
		a.initial_state = 0;
		a.final_states = new int[] {1};
		return a;
	    default: 
		return a;
	    }
    }
}
