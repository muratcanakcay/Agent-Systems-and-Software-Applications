import time
import datetime
from spade.agent import Agent
from spade.behaviour import CyclicBehaviour, TimeoutBehaviour
from spade.message import Message

class TimeoutAgent(Agent):
    class InformBehav(TimeoutBehaviour):
        async def run(self):
            print(f"I am ready (at {datetime.datetime.now().time()})")

        async def on_end(self):
            await self.agent.stop()

    async def setup(self):
        print(f"TimeoutAgent started at {datetime.datetime.now().time()}")
        start_at = datetime.datetime.now() + datetime.timedelta(seconds=5)
        b = self.InformBehav(start_at=start_at)
        self.add_behaviour(b)


if __name__ == "__main__":
    agent = TimeoutAgent("test_agent@jabbim.pl", "123")
    future = agent.start()
    future.result()

    while agent.is_alive():
        try:
            time.sleep(1)
        except KeyboardInterrupt:
            senderagent.stop()
            break
    print("Agents finished")