import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Hashtable;

public class RefereeAgent extends Agent
{
    public int gameCount = 0;
    public int readyPlayers = 0;
    Hashtable<String, ArrayList<String>> players = new Hashtable<String, ArrayList<String>>();

    @Override
    protected void setup()
    {
        addBehaviour(InitializeGame);
    }

    Behaviour InitializeGame = new CyclicBehaviour(this) {
        @Override
        public void action() {

            waitForReadyPlayer();
            checkAllPlayersReady(this);

            block(1000);
        }
    };

    void waitForReadyPlayer()
    {
        ACLMessage rcv = receive();

        if (rcv != null)
        {
            switch(rcv.getPerformative())
            {
                case ACLMessage.INFORM:
                    System.out.println(getAID().getLocalName() + " received message: " + rcv.getContent());
                    if (rcv.getOntology().equals("Ready-to-Play-Ontology"))
                    {
                        System.out.println(rcv.getSender().getLocalName() + " is ready! - " + (readyPlayers+1) + " player(s) ready!\"");
                        players.put(rcv.getSender().getLocalName(), new ArrayList<String>());
                        readyPlayers += 1;
                    }
                    break;
            }
        }
    }

    void checkAllPlayersReady(Behaviour b)
    {
        if (readyPlayers == 2) // could also use a timer to wait when there's unknown number of players
        {
            System.out.println("Both players are ready!");
            removeBehaviour(b);
            System.out.println(getAID().getLocalName() + " Removed InitializeGame behaviour!");
            addBehaviour(PlayGame);
            System.out.println(getAID().getLocalName() + " Added PlayGame behaviour!");

            System.out.println(getAID().getLocalName() + " Let's start the game!");
        }
    }

    Behaviour PlayGame = new TickerBehaviour(this, 3000) {
        @Override
        public void onTick() {
            for (String player : players.keySet()) {
                requestAnswer(player);
            }

            waitForAnswers();
            displayAnswers();

            gameCount++;
            if(gameCount==5)
            {
                removeBehaviour(this);
            }

            //block(50000);  // why is it not blocking???
        }
    };

    void requestAnswer(String player)
    {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(new AID(player, AID.ISLOCALNAME));
        msg.setLanguage("English");
        msg.setOntology("Play-Ontology");
        msg.setContent("Play now!");

        System.out.println();
        System.out.println(getAID().getLocalName() + " is sending 'Play now!' message to " + player);
        System.out.println();

        send(msg);
    }

    void waitForAnswers()
    {
        int answersCount = 0;

        while (answersCount < players.size())
        {
            ACLMessage rcv = receive();

            if (rcv != null)
            {
                switch(rcv.getPerformative())
                {
                    case ACLMessage.INFORM:
                        if(rcv.getOntology().equals("Answer-Ontology"))
                        {
                            System.out.println(getAID().getLocalName() + " received answer: \"" + rcv.getContent() + "\" from " + rcv.getSender().getLocalName() );
                            players.get(rcv.getSender().getLocalName()).add(rcv.getContent());
                            answersCount++;
                        }
                        break;
                }
            }
        }
    }

    void displayAnswers()
    {
        for (String key : players.keySet()) {
            var answers = players.get(key);
            System.out.println(key + " answers: " + answers);
        }
    }

    @Override
    protected void takeDown()
    {
        System.out.println("Goodbye, world!" + " I was " + getAID().getLocalName());
        super.takeDown();
    }
}
