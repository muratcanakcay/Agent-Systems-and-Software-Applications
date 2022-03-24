import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;

public class RefereeAgent extends Agent
{
    public int readyPlayers = 0;
    public List<String> players = new ArrayList<>();

    @Override
    protected void setup()
    {
        addBehaviour(InitializeGame);
    }

    Behaviour InitializeGame = new CyclicBehaviour(this) {
        @Override
        public void action() {

            ACLMessage rcv = receive();

            if (rcv != null)
            {
                switch(rcv.getPerformative())
                {
                    case ACLMessage.INFORM:
                        System.out.println(getAID().getName() + " received message: " + rcv.getContent());
                        if (rcv.getContent().contains("is ready"))
                        {
                            System.out.println(rcv.getSender().getName() + " is ready! - " + (readyPlayers+1) + " player(s) ready!\"");
                            players.add(rcv.getSender().getName());
                            readyPlayers += 1;
                        }
                        break;
                }
            }

            if (readyPlayers == 2)
            {
                System.out.println("Both players are ready!");
                removeBehaviour(this);
                System.out.println(getAID().getName() + " Removed InitializeGame behaviour!");
                addBehaviour(PlayGame);
                System.out.println(getAID().getName() + " Added PlayGame behaviour!");

            }

            block(1000);
        }
    };

    Behaviour PlayGame = new CyclicBehaviour(this) {
        @Override
        public void action() {

            /*System.out.println(getAID().getName() + " PlayGame!");

            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(new AID("Referee", AID.ISLOCALNAME));
            msg.setLanguage("English");
            msg.setOntology("Ready-to_Play--Ontology");
            msg.setContent(getAID().getName()+ " is ready to play!");
            System.out.println(getAID().getName() + " is sending 'I'm ready to play' message!");

            send(msg);*/

            block(1000);
        }
    };

    @Override
    protected void takeDown()
    {
        System.out.println("Goodbye, world!" + " I was " + getAID().getName());
        super.takeDown();
    }
}
