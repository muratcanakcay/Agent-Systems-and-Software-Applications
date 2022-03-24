import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import static java.lang.System.in;

public class RefereeAgent extends Agent
{
    public int gameCount = 0;
    public int readyPlayers = 0;
    Hashtable<String, ArrayList<String>> players = new Hashtable<String, ArrayList<String>>();
    //public List<String> players = new ArrayList<>();

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
                            players.put(rcv.getSender().getLocalName(), new ArrayList<String>());
                            readyPlayers += 1;
                        }
                        break;
                }
            }

            if (readyPlayers == 2) // could also use a timer to wait when there's unknown number of players
            {
                System.out.println("Both players are ready!");
                removeBehaviour(this);
                System.out.println(getAID().getName() + " Removed InitializeGame behaviour!");
                addBehaviour(PlayGame);
                System.out.println(getAID().getName() + " Added PlayGame behaviour!");

                System.out.println(getAID().getName() + " Let's start the game!");
            }

            block(1000);
        }
    };

    Behaviour PlayGame = new CyclicBehaviour(this) {
        @Override
        public void action() {
            for (String player : players.keySet()) {
                requestResult(player);
            }

            int resultsCount = 0;

            while (resultsCount < players.size()) // don't use while???
            {
                ACLMessage rcv = receive();

                if (rcv != null)
                {
                    switch(rcv.getPerformative())
                    {
                        case ACLMessage.INFORM:
                            if(rcv.getOntology().equals("Result-Ontology"))
                            {
                                System.out.println(rcv.getSender().getLocalName() + " sent " + rcv.getContent());
                                players.get(rcv.getSender().getLocalName()).add(rcv.getContent());
                                resultsCount++;
                            }
                            break;
                    }
                }
            }

            for (String key : players.keySet()) {
                var results = players.get(key);
                System.out.println(key + " results: " + results);
            }

            gameCount++;

            if(gameCount==5)
            {
                removeBehaviour(this);
            }

            block(1000);
        }
    };

    void requestResult(String player)
    {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(new AID(player, AID.ISLOCALNAME));
        msg.setLanguage("English");
        msg.setOntology("Play-Ontology");
        msg.setContent("Play now!");
        System.out.println(getAID().getName() + " is sending 'Play now!' message to " + player);

        send(msg);
    }

    @Override
    protected void takeDown()
    {
        System.out.println("Goodbye, world!" + " I was " + getAID().getName());
        super.takeDown();
    }
}
