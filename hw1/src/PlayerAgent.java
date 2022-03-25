import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Random;

public class PlayerAgent extends Agent
{
    Random rng = new Random();

    @Override
    protected void setup()
    {
        addBehaviour(sendReady);
        addBehaviour(waitForReferee);
    }

    Behaviour sendReady = new OneShotBehaviour(this) {
        @Override
        public void action() {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(new AID("Referee", AID.ISLOCALNAME));
            msg.setLanguage("English");
            msg.setOntology("Ready-to-Play-Ontology");
            msg.setContent(getAID().getLocalName()+ " is ready to play!");
            System.out.println(getAID().getLocalName() + " is sending 'I'm ready to play' message!");

            send(msg);
        }
    };

    Behaviour waitForReferee = new CyclicBehaviour(this) {
            @Override
            public void action() {
                System.out.println(getAID().getLocalName() + " waiting for referee message.");

                ACLMessage rcv = receive();

                if (rcv != null)
                { // TODO: kill agent when GAME OVER notification is received from referee
                    switch(rcv.getPerformative())
                    {
                        case ACLMessage.REQUEST:
                            System.out.println(getAID().getLocalName() + " received message: " + rcv.getContent());
                            if(rcv.getOntology().equals("Play-Ontology"))
                            {
                                String result = play();
                                sendResult(result);
                            }

                            break;
                    }
                }

                block(1000);

            }
        };

    String play()
    {
        int choice = rng.nextInt(3);

        switch(choice)
        {
            case 0:
                return "paper";
            case 1:
                return "rock";
            default:
                return "scissors";
        }
    }

    void sendResult(String answer)
    {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID("Referee", AID.ISLOCALNAME));
        msg.setLanguage("English");
        msg.setOntology("Answer-Ontology");
        msg.setContent(answer);
        System.out.println(getAID().getLocalName() + " is sending answer: " + answer);

        send(msg);
    }

    @Override
    protected void takeDown()
    {
        System.out.println("Goodbye, world!" + " I was " + getAID().getLocalName());
        super.takeDown();
    }
}
