import time
import asyncio
from spade.agent import Agent
from spade.behaviour import CyclicBehaviour

# https://spade-mas.readthedocs.io/en/latest/usage.html#creating-your-first-dummy-agent
class DummyAgent(Agent):
    class MyBehaviour(CyclicBehaviour):
        async def on_start(self):
            print("Starting behaviour . . .")
            self.counter = 0

        async def run(self):
            print("Counter: {}".format(self.counter))
            self.counter += 1
            await asyncio.sleep(1)

    async def setup(self):
        print("Agent starting . . .")
        b = self.MyBehaviour()
        self.add_behaviour(b)

if __name__ == "__main__":
    dummy = DummyAgent("test_agent@jabbim.pl", "123")
    dummy.start()

    print("Wait until user interrupts with ctrl+C")
    while True:
        try:
            time.sleep(1)
        except KeyboardInterrupt:
            break
    dummy.stop()