import time
from spade import agent, quit_spade

class DummyAgent(agent.Agent):
    async def setup(self):
        print("Hello World! I'm agent {}".format(str(self.jid)))

dummy = DummyAgent("test_agent@jabbim.pl", "123")

future = dummy.start()
future.result()

dummy.stop()
quit_spade()