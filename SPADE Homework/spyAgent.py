import json
import datetime
import random
from spade.agent import Agent
from spade import behaviour
from spade.message import Message

class Spy(Agent):
    class SpyBehaviour(behaviour.PeriodicBehaviour):
        async def run(self):
            target = random.randrange(1, 3)
            randomCount = random.randrange(1, 1001)
            
            recipient = f"mca@shad0w.io/{target}"
            msg = Message(to=recipient)
            msg.set_metadata("performative", "inform")

            msg.body = json.dumps({"count" : randomCount})
            await self.send(msg)

            print(f"Spy sent message 'Count={randomCount}' to Player{target}")

        async def on_end(self):
            print(f"SpyBehaviour ended with exit code {self.exit_code}.")
            await self.agent.stop()

    async def setup(self):
        print(f"Spy started!")
        
        start_at = datetime.datetime.now() + datetime.timedelta(seconds=20)
        self.add_behaviour(self.SpyBehaviour(period = 10, start_at=start_at))