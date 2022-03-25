import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class RefereeAgent extends Agent
{
    public int gameCount = 0;
    public int readyPlayers = 0;
    Hashtable<String, ArrayList<String>> players = new Hashtable<>();
    Hashtable<String, Integer> scores = new Hashtable<>();

    @Override
    protected void setup()
    {
        addBehaviour(InitializeGame);
    }

    Behaviour InitializeGame = new CyclicBehaviour(this) {
        @Override
        public void action()
        {
            waitForReadyPlayer();

            if (readyPlayers == 2) // TODO: implement a timer to wait when there's unknown number of players
            {
                allPlayersReady(this);
            }

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
                        players.put(rcv.getSender().getLocalName(), new ArrayList<>());
                        scores.put(rcv.getSender().getLocalName(), 0);

                        readyPlayers += 1;
                    }

                    break;
            }
        }
    }

    void allPlayersReady(Behaviour b)
    {
        System.out.println(getAID().getLocalName() + " Both players are ready!");

        removeBehaviour(b);
        System.out.println(getAID().getLocalName() + " Removed InitializeGame behaviour!");

        addBehaviour(PlayGame);
        System.out.println(getAID().getLocalName() + " Added PlayGame behaviour!");

        System.out.println(getAID().getLocalName() + " Let's start the game!");
    }

    Behaviour PlayGame = new TickerBehaviour(this, 3000) {
        @Override
        public void onTick() {

            requestAnswers();
            waitForAnswers();
            displayAnswers();
            calculateScores();
            displayScores();

            gameCount++;
            if(gameCount==5) // TODO: INFORM players that game is over
            {
                System.out.println(getAID().getLocalName() + " GAME OVER!");
                // TODO: display which player won the game
                removeBehaviour(this);
                // TODO: kill refereeAgent when game is over
            }
        }
    };

    void requestAnswers()
    {
        for (String player : players.keySet())
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

    private void calculateScores()
    {
        List<String> checkedPlayers = new ArrayList<>();

        for (String player : players.keySet())
        {
            checkedPlayers.add(player);

            for (String otherPlayer : players.keySet())
            {
                if (checkedPlayers.contains(otherPlayer)) { continue; }

                var firstAnswer= players.get(player).get(gameCount);
                var secondAnswer= players.get(otherPlayer).get(gameCount);

                if (firstAnswer.equals(secondAnswer)) { continue; }

                if (firstAnswer.equals("rock") && secondAnswer.equals("scissors")
                        || firstAnswer.equals("scissors") && secondAnswer.equals("paper")
                        || firstAnswer.equals("paper") && secondAnswer.equals("rock"))
                {
                    scores.put(player, scores.get(player)+1);
                    continue;
                }

                scores.put(otherPlayer, scores.get(otherPlayer)+1);
            }
        }
    }

    private void displayScores()
    {
        for (String player : scores.keySet()) {
            System.out.println(player + " score: " + scores.get(player));
        }
    }

    @Override
    protected void takeDown()
    {
        System.out.println("Goodbye, world!" + " I was " + getAID().getLocalName());
        super.takeDown();
    }
}
