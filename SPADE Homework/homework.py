import time
import datetime
import random
from spade.agent import Agent
from spade import behaviour
from spade.message import Message
from spade.template import Template

class Player(Agent):
    name = None
    otherPlayerName = None

    class CountingBehaviour(behaviour.PeriodicBehaviour):
        async def on_start(self):
            print("KickOff...")
            self.counter = 0

        async def run(self):
            waitForMessage = False
            if (self.agent.name == "1"):
                waitForMessage = True;

            while True:
                if (waitForMessage):
                    print(f"Player{self.agent.name} is waiting for message from Player{self.agent.otherPlayerName}...")
                    message_wait_timeout = 60

                    msg = await self.receive(timeout=60)
                    if msg:
                        print(f"[{self.counter}] Player{self.agent.name} received message: {msg.body}")
                    else:
                        print(f"Player{self.agent.name} Did not received any message after: {message_wait_timeout} seconds")

                                        
                    
                    if (self.agent.name == "1"):
                        waitForMessage = False
                    else:
                        break
                else:                
                    recipient = "mca@shad0w.io/" + self.agent.otherPlayerName
                    msg = Message(to=recipient)

                    msg.set_metadata("performative", "inform")
                    msg.body = f"Hello World from Agent {self.agent.name} to {self.agent.otherPlayerName}"

                    await self.send(msg)
                    print(f"Player{self.agent.name} sent message to {recipient}")
                    
                    if (self.agent.name == "2"):
                        waitForMessage = True
                    else:
                        break
            
            if self.counter == 5:
                self.kill(exit_code=0) #kills behaviour
            self.counter += 1            

        async def on_end(self):
            print(f"KickOffBehaviour finished with exit code {self.exit_code}.")
            await self.agent.stop()

    async def setup(self):
        self.name = self.get('name')
        self.otherPlayerName = self.get('otherPlayerName')
        print(f"Player{self.name} started. Other player is Player{self.otherPlayerName}!")
        start_at = datetime.datetime.now() + datetime.timedelta(seconds=5)
        b = self.CountingBehaviour(period = 3, start_at=start_at)
        self.add_behaviour(b)

if __name__ == "__main__":
    player1 = Player("mca@shad0w.io/1", "12345678")
    player1.set("name", "1")
    player1.set("otherPlayerName", "2")

    future = player1.start()
    future.result()

    player2 = Player("mca@shad0w.io/2", "12345678")
    player2.set("name", "2")
    player2.set("otherPlayerName", "1")
    player2.start()

    while player1.is_alive() or player2.is_alive():
        try:
            time.sleep(1)
        except KeyboardInterrupt:
            player1.stop()
            player2.stop()
            break    
    print("Agents finished")