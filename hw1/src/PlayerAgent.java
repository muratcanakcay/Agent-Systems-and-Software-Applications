import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class PlayerAgent extends Agent
{
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
                System.out.println(getAID().getName() + " waiting for referee message.");

                ACLMessage rcv = receive();

                if (rcv != null)
                {
                    switch(rcv.getPerformative())
                    {
                        case ACLMessage.REQUEST:
                            System.out.println(getAID().getLocalName() + " received message: " + rcv.getContent());
                            if(rcv.getContent().equals("Play now!"))
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

    String play() //TODO: implement answer logic
    {
        return "paper";
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
