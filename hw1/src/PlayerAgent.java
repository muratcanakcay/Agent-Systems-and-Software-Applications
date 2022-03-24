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



        /*Behaviour b = new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
                msg.addReceiver(new AID("Muratcan", AID.ISLOCALNAME));
                msg.setLanguage("English");
                msg.setOntology("Weather-Forecast-Ontology");
                msg.setContent("How's the weather today?");
                System.out.println(getAID().getName() + " is sending query message!");
                send(msg);

                ACLMessage rcv = receive();

                if (rcv != null)
                {
                    switch(rcv.getPerformative())
                    {
                        case ACLMessage.INFORM:
                            System.out.println(getAID().getName() + " received message: " + rcv.getContent());
                            break;
                    }
                }

                block(15000);



            }
        };
        addBehaviour(b);*/
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

    @Override
    protected void takeDown()
    {
        System.out.println("Goodbye, world!" + " I was " + getAID().getName());
        super.takeDown();
    }
}
