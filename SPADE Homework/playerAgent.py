import asyncio
import json
import random
from spade.agent import Agent
from spade import behaviour
from spade.message import Message

DEBUG = False  # used for turning on/off informative print statements

class Player(Agent):
    name = None
    otherPlayerName = None
    distractionChance : int = 50

    class CountingBehaviour(behaviour.PeriodicBehaviour):
        async def run(self):
            waitForMessage = False
            if (self.agent.name == "1"):
                waitForMessage = True;

            # If agent is Player1 it starts in waiting mode and then enters the receive-reply cycle
            # If agent is Player2 it starts by sending the first message and then enters the receive-reply cycle
            while True:
                if (waitForMessage):
                    if DEBUG : print(f"Player{self.agent.name} is waiting for message from Player{self.agent.otherPlayerName}...")

                    while True:
                        msg = await self.receive(timeout=self.agent.message_wait_timeout)
                        if msg:
                            if DEBUG : print(f"[{self.agent.count}] Player{self.agent.name} received message: {msg.body}")
                            body = json.loads(msg.body)
                            receivedCount = int(body["count"])
                            
                            if str(msg.sender) == "mca@shad0w.io/3": # msg received from spyAgent                                
                                if random.randrange(1, 101) < self.agent.distractionChance:
                                    print(f"Player{self.agent.name} got DISTRACTED!")
                                    self.agent.count = receivedCount + 1
                                    
                                    # wait for actual message from other player and discard it
                                    msg = await self.receive(timeout=self.agent.message_wait_timeout)
                                    if msg:
                                        if DEBUG : print(f"[{self.agent.count}] Player{self.agent.name} received message: {msg.body} and discarding it!")
                                        break
                                else:
                                    print(f"Player{self.agent.name} WAS NOT DISTRACTED!")
                                    
                                    # restart waiting for actual message from other player
                                    continue
                            else: # message received from other player (not from the spy)
                                 self.agent.count = receivedCount + 1
                                 break
                        else:
                            print(f"Player{self.agent.name} Did not receive any message after: {self.agent.message_wait_timeout} seconds")
                            self.kill(exit_code=1)
                    
                    if (self.agent.name == "1"):
                        waitForMessage = False
                    else:
                        break
                else:                
                    await asyncio.sleep(1) # so the agent waits a little before replying
                    recipient = "mca@shad0w.io/" + self.agent.otherPlayerName
                    msg = Message(to=recipient)
                    msg.set_metadata("performative", "inform")
                    msg.body = json.dumps({"count" : self.agent.count})
                    await self.send(msg)

                    print(f"Player{self.agent.name} sent 'Count={self.agent.count}'")
                    
                    if (self.agent.name == "2"):
                        waitForMessage = True
                    else:
                        break

        async def on_end(self):
            print(f"CountingBehaviour finished with exit code {self.exit_code}.")
            await self.agent.stop()

    async def setup(self):
        self.name = self.get('name')
        self.otherPlayerName = self.get('otherPlayerName')
        self.message_wait_timeout = 60
        self.count = 0
        
        print(f"Player{self.name} started.")
        self.add_behaviour(self.CountingBehaviour(period = 1))