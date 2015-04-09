package airdel.a3;

import airdel.a3.util.Timer;

/**
 * Main class for Assignment 3 - to run learn, predict and check tasks
 * @author Ryan Millay, Nikit Waghela, Pramod Khare
 * CS6240
 * Assignment 3
 */
public class A3_Driver {
    public static void main(String[] args) throws Exception {
        // check the input
        verifyArgs(args);
        Timer t = new Timer().start();
        // learn via some input file
        if(args[0].equals("-learn")){
            // build a new hadoop job
            new DelayLearner().executeLearnerJob(args);
            System.out.println("Learn features completed\n" + t.stop());
        }
        // try to predict based on some input
        else if (args[0].equals("-predict")) {
            // call prediction function
            new DelayPredicter(args[1], args[2]).predictArrivalDelay();
            System.out.println("Prediction completed\n" + t.stop());
        }
        // verify your past predictions
        else {
            // call prediction checker function
            new DelayChecker(args[1], args[2]).check().printResults();
            System.out.println(t.stop());
        }
    }
    
    public static void verifyArgs(String[] args) {
        // let's verify the number of args
        if(args.length == 0 || (!args[0].equals("-learn") && !args[0].equals("-predict") && !args[0].equals("-check"))) {
            System.err.println("Usage: < -learn | -predict | -check >");
            System.exit(-1);
        } else if(args[0].equals("-learn") && args.length != 3) {
            System.out.println("Usage: -learn <input data file> <output directory>");
            System.exit(-1);
        } else if(args[0].equals("-predict") && args.length != 3) {
            System.out.println("Usage: -predict <model file> <airline file>");
            System.exit(-1);
        } else if(args[0].equals("-check") && args.length != 3){
            System.out.println("Usage: -check <prediction file> <verification file>");
            System.exit(-1);
        }
    }
}