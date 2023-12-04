import java.io.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;

// runs program
public class Main {
    public static void main(String[] args) {
        try {
            // open the file and prepare to parse it
            Object obj = new JSONParser().parse(new FileReader("DFA1.json"));
            JSONObject json_obj = (JSONObject)obj;

            // read the input
            JSONArray states = (JSONArray)json_obj.get("States");
            JSONArray alphabet = (JSONArray)json_obj.get("Alphabet");
            JSONArray transitions = (JSONArray)json_obj.get("Transitions");
            JSONArray startState = (JSONArray)json_obj.get("Start State");
            JSONArray acceptStates = (JSONArray)json_obj.get("Accept States");

            // if we get here, the input file format is good
            // now need to check that the input itself is valid
            if(checkValidNFA(states, alphabet, transitions, startState, acceptStates)) {
                // valid, make NFA*
                printData(states, alphabet, transitions, startState, acceptStates);
            } else {
                // not valid
                throw new Exception("Invalid input");
            }


            // these exceptions deal with input file format, not the validity of the input itself
        } catch(FileNotFoundException FNF) {
            System.out.println("File name not \"input.json\"");
            FNF.printStackTrace();
        } catch(NullPointerException NP) {
            System.out.println("Tags in input file either missing or incorrectly named");
            NP.printStackTrace();
        } catch(ParseException PE) {
            System.out.println("Error parsing input");
            PE.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    // checks if the input is valid
    // returns false if the input is invalid
    // else returns true
    public static boolean checkValidNFA(JSONArray states, JSONArray alphabet, JSONArray transitions,
                                        JSONArray startState, JSONArray acceptStates) {
        // make sure there's only 1 start state
        if(startState.size() != 1) {
            System.out.println("Incorrect number of start states");
            return false;
        }

        // make sure the start state is in the set of states
        if(!states.contains(startState.get(0))) {
            System.out.println("Start state not in set of states");
            return false;
        }

        // make sure the accept states are a subset of the set of states
        for(Object state : acceptStates) {
            if(!states.contains(state)) {
                System.out.println(state + " in Accept State not in set of states");
                return false;
            }
        }

        // check for duplicates on the input
        if(containsDuplicates(states)) {
            System.out.println("Input contains repeated state names");
            return false;
        } else if(containsDuplicates(alphabet)) {
            System.out.println("Input contains repeated alphabet characters");
            return false;
        } else if(containsDuplicates(transitions)) {
            System.out.println("Input contains repeated transitions");
            return false;
        } else if(containsDuplicates(acceptStates)) {
            System.out.println("Input contains repeated accept state names");
            return false;
        }

        // make sure the transition table is in good format
        // if the transitions are good, the function returns true because
        // everything else was checked previously
        return checkTransitions(states, alphabet, transitions);
    }


    // checks for duplicates within the input
    // return true if a duplicate is found
    // else return false
    public static boolean containsDuplicates(JSONArray arr) {
        HashSet<Object> set = new HashSet<>();
        for(Object value : arr) {
            if(set.contains(value)) {
                System.out.println(value + " is a duplicate");
                return true;
            } else {
                set.add(value);
            }
        }

        // no duplicates
        return false;
    }


    // makes sure the transition table is properly formatted
    // returns false if the transitions aren't good
    // else returns true
    public static boolean checkTransitions(JSONArray states, JSONArray alphabet, JSONArray transitions) {
        // 3 things to check
        // Is the initial state in the set of states
        // Is the symbol in the alphabet
        // Are the final states in the set of states

        // initial state check
        for(Object transition : transitions) {
            JSONArray obj = (JSONArray)transition;
            if(!states.contains(obj.get(0))) {
                System.out.println(obj + ", " + obj.get(0) + " not in set of states");
                return false;
            }
        }

        // alphabet check
        for(Object transition : transitions) {
            JSONArray obj = (JSONArray)transition;
            if(!alphabet.contains(obj.get(1))) {
                System.out.println(obj + ", " + obj.get(1) + " not in alphabet");
                return false;
            }
        }

        // final state check
        for(Object transition : transitions) {
            JSONArray obj = (JSONArray)transition;
            JSONArray finalStates = (JSONArray)obj.get(2);
            for(Object state : finalStates) {
                if(!states.contains(state)) {
                    System.out.println(obj + ", " + finalStates + ", " + state + " not in set of states");
                    return false;
                }
            }
        }

        // return true because the transitions are good
        return true;
    }


    // prints all the data in a user-readable form to a txt file
    public static void printData(JSONArray states, JSONArray alphabet, JSONArray transitions,
                                 JSONArray startState, JSONArray acceptStates) {
        FileWriter fw;
        try {
            fw = new FileWriter("output.txt");

            // get a name for the new start state
            String newStart;
            if(!states.contains("Q_new")) {
                newStart = "Q_new";
            } else {
                newStart = "Q_newStart";
            }

            // print the states
            fw.write("States: ");
            for(Object state : states) {
                fw.write("\"" + state + "\", ");
            }

            // print the new state
            fw.write("\"" + newStart + "\"\n");

            // print the alphabet
            fw.write("Alphabet: ");
            for(Object symbol : alphabet) {
                fw.write("'" + symbol + "', ");
            }

            // if epsilon isn't in the alphabet, add it
            if(!alphabet.contains("e")) {
                fw.write("'e'");
            }

            // print the transitions
            fw.write("\n");
            fw.write("Transitions: ");
            fw.write("\n");
            for(Object transition : transitions) {
                fw.write("    " + transition);
                fw.write("\n");
            }

            // print the new transitions
            for(Object state : acceptStates) {
                fw.write("    [\"" + state + "\",\"e\",[\"" + startState.get(0) +"\"]]\n");
            }

            // print the transition from the new start state
            fw.write("    [\"" + newStart + "\",\"e\",[\"" + startState.get(0) +"\"]]\n");

            // print the new start state
            fw.write("Start State: \"" + newStart + "\"\n");

            // print the accept states
            fw.write("Accept States: ");
            for(Object state : acceptStates) {
                fw.write("\"" + state + "\", ");
            }

            // print the new accept state
            fw.write("\"" + newStart + "\"");

            // close the writer
            fw.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
