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
            msg.setOntology("Ready-to_Play--Ontology");
            msg.setContent(getAID().getName()+ " is ready to play!");
            System.out.println(getAID().getName() + " is sending 'I'm ready to play' message!");

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
                            System.out.println(getAID().getName() + " received message: " + rcv.getContent());
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

    String play()
    {
        return "paper";
    }

    void sendResult(String result)
    {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID("Referee", AID.ISLOCALNAME));
        msg.setLanguage("English");
        msg.setOntology("Result-Ontology");
        msg.setContent(result);
        System.out.println(getAID().getLocalName() + " is sending result: " + result);

        send(msg);
    }

    @Override
    protected void takeDown()
    {
        System.out.println("Goodbye, world!" + " I was " + getAID().getName());
        super.takeDown();
    }
}
