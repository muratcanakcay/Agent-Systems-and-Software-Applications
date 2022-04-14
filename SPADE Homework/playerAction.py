import asyncio
import json
import random

from spade.behaviour import State
from spade.message import Message

DEBUG = False  # used for turning on/off informative print statements

async def playerAction(self : State, isDistracted: bool):
    waitForMessage = True
    if (self.agent.name == "2"):
        waitForMessage = False;

    # Player1 starts waiting and then sends reply
    # Player2 sends message and then starts waiting
    while True:
        if (waitForMessage):
            if DEBUG : print(f"Player{self.agent.name} is waiting for message from Player{self.agent.otherPlayerName}...")

            while True:
                msg = await self.receive(timeout=self.agent.message_wait_timeout)
                if msg:
                    if DEBUG : print(f"Player{self.agent.name} received message: {msg.body}")
                    body = json.loads(msg.body)
                    receivedCount = int(body["count"])
                    
                    if str(msg.sender) == "mca@shad0w.io/3":
                        # msg received from spyAgent, check if player got distracted
                        if random.randrange(1, 101) < self.agent.distractionChance:
                            
                            # got distracted
                            print(f"Player{self.agent.name} got distracted!")
                            self.agent.count = receivedCount + 1
                            isDistracted = True
                            
                            # wait for actual message from other player and discard it
                            msg = await self.receive(timeout=self.agent.message_wait_timeout)
                            if msg:
                                if DEBUG : print(f"[{self.agent.count}] Player{self.agent.name} received message: {msg.body} and discarding it!")
                                break 
                            else:
                                print(f"Player{self.agent.name} Did not receive any message after: {self.agent.message_wait_timeout} seconds")
                                self.kill(exit_code=1)
                        
                        else: # did not get distracted
                            if DEBUG : print(f"Player{self.agent.name} was not distracted!")
                            
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
            else: #player2
                return isDistracted

        else: #sendReply
            await asyncio.sleep(1) # so the agent waits a little before replying
            msg = Message(to="mca@shad0w.io/" + self.agent.otherPlayerName)
            msg.set_metadata("performative", "inform")
            msg.body = json.dumps({"count" : self.agent.count})
            await self.send(msg)

            print(f"Player{self.agent.name} Count={self.agent.count}")
            
            if (self.agent.name == "2"):
                waitForMessage = True
            else: #player1
                return isDistracted