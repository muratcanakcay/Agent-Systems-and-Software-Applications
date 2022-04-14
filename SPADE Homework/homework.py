import time
from spade.agent import Agent
from spade import behaviour
from spade.message import Message
from spade.template import Template

class Player(Agent):
    name = None
    otherPlayerName = None

    class KickOffBehaviour(behaviour.OneShotBehaviour):
        async def on_start(self):
            print("KickOff...")
            self.name = self.get('name')
            self.otherPlayerName = self.get('otherPlayerName')
        
        async def run(self):
            waitForMessage = False
            if (self.name == "1"):
                waitForMessage = True;

            while True:
                if (waitForMessage):
                    print(f"Player{self.name} is waiting for message from Player{self.otherPlayerName}...")
                    message_wait_timeout = 60

                    msg = await self.receive(timeout=60)
                    if msg:
                        print(f"Player{self.name} received message: {msg.body}")
                    else:
                        print(f"Player{self.name} Did not received any message after: {message_wait_timeout} seconds")
                    
                    if (self.name == "1"):
                        waitForMessage = False
                    if (self.name == "2"):
                        break
                else:                
                    recepient = "mca@shad0w.io/" + self.otherPlayerName
                    msg = Message(to=recepient)

                    msg.set_metadata("performative", "inform")
                    msg.body = f"Hello World from Agent {self.name} to {self.otherPlayerName}"

                    await self.send(msg)
                    print(f"Player{self.name} sent message to {recepient}")

                    if (self.name == "1"):
                        break
                    if (self.name == "2"):
                        waitForMessage = True
            
            
            self.kill(exit_code=0) #kills behaviour
            await self.agent.stop()

        async def on_end(self):
            print("KickOffBehaviour finished with exit code {self.exit_code}.")

    async def setup(self):
        self.name = self.get('name')
        self.otherPlayerName = self.get('otherPlayerName')
        print(f"PlayerAgent{self.name} started. Other player is {self.otherPlayerName}!")
        b = self.KickOffBehaviour()
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
            time.sleep(100)
        except KeyboardInterrupt:
            player1.stop()
            player2.stop()
            break    
    print("Agents finished")