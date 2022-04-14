import asyncio
import json
import datetime
import random
import time
from spade.agent import Agent
from spade import behaviour
from spade.message import Message
from spade.template import Template

class Spy(Agent):
    class SpyBehaviour(behaviour.PeriodicBehaviour):
        async def on_start(self):
            print("Spy behaviour starting...")
            time.sleep(3)

        async def run(self):
            
            recipient = "mca@shad0w.io/1"
            msg = Message(to=recipient)
            msg.set_metadata("performative", "inform")
            msg.body = json.dumps({"count" : 100})
            await self.send(msg)

            print(f"Spy sent message 'Count=100' to {recipient}")
            

        async def on_end(self):
            print(f"SpyBehaviour finished with exit code {self.exit_code}.")
            await self.agent.stop()

    async def setup(self):
        print(f"Spy started!")
        start_at = datetime.datetime.now() + datetime.timedelta(seconds=15)
        self.add_behaviour(self.SpyBehaviour(period = 10, start_at=start_at))