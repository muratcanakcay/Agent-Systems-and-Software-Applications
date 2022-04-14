from spade.agent import Agent
from spade.behaviour import FSMBehaviour, State
from playerAction import playerAction

COUNTING = "COUNTING"
DISTRACTED = "DISTRACTED"

class PlayerBehaviour(FSMBehaviour):
    async def on_end(self):
        print(f"PlayerBehaviour finished with exit code {self.exit_code}.")
        await self.agent.stop()

class Counting(State):
    async def run(self):
        isDistracted = False        
        while not isDistracted:
            isDistracted = await playerAction(self, False)
        
        self.set_next_state(DISTRACTED)

class Distracted(State):
    async def run(self):
        print(f"Player{self.agent.name}'s STATE CHANGED TO DISTRACTED")
        while True:
            isDistracted = await playerAction(self, False)
            if isDistracted:
                print(f"Player{self.agent.name} is ALREADY in DISTRACTED STATE")

class Player(Agent):
    name = None
    otherPlayerName = None
    distractionChance : int = 50  # % chance of the player getting distracting

    async def setup(self):
        self.name = self.get('name')
        self.otherPlayerName = self.get('otherPlayerName')
        self.message_wait_timeout = 60
        self.count = 0
        
        print(f"Player{self.name} started.")

        fsm = PlayerBehaviour()
        fsm.add_state(name=COUNTING, state=Counting(), initial=True)
        fsm.add_state(name=DISTRACTED, state=Distracted())
        fsm.add_transition(source=COUNTING, dest=DISTRACTED)

        self.add_behaviour(fsm)