import time
from spade.agent import Agent
from spade.behaviour import OneShotBehaviour
from spade.message import Message
from spade.template import Template


class SenderAgent(Agent):
    class InformBehav(OneShotBehaviour):
        async def run(self):
            print("InformBehav running")
            msg = Message(to="test_agent@jabbim.pl/1")

            msg.set_metadata("performative", "inform")
            msg.body = "Hello World"

            await self.send(msg)
            print("Message sent!")

            self.kill(exit_code=0)
            # await self.agent.stop()

        async def on_end(self):
            print("InformBehav finished with exit code {}.".format(self.exit_code))

    async def setup(self):
        print("SenderAgent started")
        b = self.InformBehav()
        self.add_behaviour(b)


class ReceiverAgent(Agent):
    class RecvBehav(OneShotBehaviour):
        async def run(self):
            print("RecvBehav running")
            message_wait_timeout = 10

            msg = await self.receive(timeout=10)
            if msg:
                print("Message received with content: {}".format(msg.body))
            else:
                print("Did not received any message after: {} seconds".format(message_wait_timeout))

            self.kill(exit_code=0)
            # await self.agent.stop()
        async def on_end(self):
            print("RecvBehav finished with exit code {}.".format(self.exit_code))

    async def setup(self):
        print("ReceiverAgent started")
        b = self.RecvBehav()
        template = Template()
        template.set_metadata("performative", "inform")
        self.add_behaviour(b, template)


if __name__ == "__main__":
    receiveragent = ReceiverAgent("test_agent@jabbim.pl/1", "123")
    future = receiveragent.start()
    future.result()

    senderagent = SenderAgent("test_agent@jabbim.pl/2", "123")
    senderagent.start()

    while receiveragent.is_alive():
        try:
            time.sleep(100)
        except KeyboardInterrupt:
            senderagent.stop()
            receiveragent.stop()
            break
    print("Agents finished")
