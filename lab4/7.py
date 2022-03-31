import asyncio
from spade.agent import Agent
from spade.behaviour import OneShotBehaviour
from spade import quit_spade
import time


class DummyAgent(Agent):
    class ThinkingBehaviour(OneShotBehaviour):
        async def run(self):
            print("I am thinking...")
            #time.sleep(5) blocks the entire agent
            await asyncio.sleep(5) #blocks only this behaviour
            print("Thinking has finished")

    class WaitingBehav(OneShotBehaviour):
        async def run(self):
            print("I am waiting...")
            await self.agent.thinking_behaviour.join() # this join must be awaited
            print("I am done waiting!")

    async def setup(self):
        print("Agent starting . . .")
        self.thinking_behaviour = self.ThinkingBehaviour()
        self.add_behaviour(self.thinking_behaviour)
        self.waiting_behaviour = self.WaitingBehav()
        self.add_behaviour(self.waiting_behaviour)


if __name__ == "__main__":
    dummy = DummyAgent("test_agent@jabbim.pl/1", "123")
    future = dummy.start()
    future.result()

    dummy.waiting_behaviour.join()

    print("Stopping agent.")
    dummy.stop()
