package lab1;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;

public class HelloWorldAgent extends Agent
{
//    @Override
//    protected void setup()
//    {
//        System.out.println("Hello, World" + " my name is " + getAID().getName());
//    }

    @Override
    protected void setup()
    {
//        Behaviour b = new OneShotBehaviour(this) {
        Behaviour b = new CyclicBehaviour(this) {
            @Override
            public void action() {
                System.out.println("Hello, world!" + " My name is " + getAID().getName());
                System.out.println("Life is good!");
                doWait(1000);
            }
        };
        addBehaviour(b);
    }

    @Override
    protected void takeDown()
    {
        System.out.println("Goodbye, world!" + " I was " + getAID().getName());
        super.takeDown();
    }
}
