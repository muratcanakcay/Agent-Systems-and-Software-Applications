import asyncio
import json
import datetime
import random
from spade.agent import Agent
from spade import behaviour
from spade.message import Message
from spade.template import Template

class Player(Agent):
    name = None
    otherPlayerName = None
    distractionChance : int = 100

    class CountingBehaviour(behaviour.PeriodicBehaviour):
        async def on_start(self):
            print("KickOff...")
            self.count = 0
            self.message_wait_timeout = 60
            self.expected = 0

        async def run(self):
            waitForMessage = False
            if (self.agent.name == "1"):
                waitForMessage = True;

            # If agent is Player1 it starts in waiting mode and then enters the waiting-reply cycle
            # If agent is Player2 it starts by sending the first message and then enters the waiting-reply cycle
            while True:
                if (waitForMessage):
                    print(f"Player{self.agent.name} is waiting for message from Player{self.agent.otherPlayerName}...")
                    

                    while True:
                        msg = await self.receive(timeout=self.message_wait_timeout)
                        if msg:
                            print(f"[{self.count}] Player{self.agent.name} received message: {msg.body}")
                            body = json.loads(msg.body)
                            receivedCount = int(body["count"])
                            print(msg.sender)
                            
                            if str(msg.sender) == "mca@shad0w.io/3": # msg received from spyAgent
                                print(f"{self.agent.name} received {receivedCount} from SPY!")
                                if random.randrange(1, 101) < self.agent.distractionChance:
                                    print(f"Player{self.agent.name} got DISTRACTED!")
                                    self.expected = self.count + 2
                                    self.count = receivedCount + 1
                                    msg = await self.receive(timeout=self.message_wait_timeout) # wait for actual message from other player and discard it
                                    if msg:
                                        print(f"[{self.count}] Player{self.agent.name} received message: {msg.body} and discarding it!")
                                        break
                                else:
                                     continue # wait for actual message from other player and use it
                            else: # message received from other player
                                 self.count = receivedCount + 1
                                 break                            
                        else:
                            print(f"Player{self.agent.name} Did not receive any message after: {self.message_wait_timeout} seconds")
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
                    msg.body = json.dumps({"count" : self.count})
                    await self.send(msg)

                    print(f"Player{self.agent.name} sent message 'Count={self.count}' to {recipient}")


                    
                    
                    if (self.agent.name == "2"):
                        waitForMessage = True
                    else:
                        break
            
            # if self.count >= 15:
            #     self.kill(exit_code=0)            

        async def on_end(self):
            print(f"CountingBehaviour finished with exit code {self.exit_code}.")
            await self.agent.stop()

    async def setup(self):
        self.name = self.get('name')
        self.otherPlayerName = self.get('otherPlayerName')
        print(f"Player{self.name} started. Other player is Player{self.otherPlayerName}!")
        start_at = datetime.datetime.now() #+ datetime.timedelta(seconds=5)
        b = self.CountingBehaviour(period = 1, start_at=start_at)
        self.add_behaviour(b)