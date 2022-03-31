import time
import asyncio
from spade.agent import Agent
from spade.behaviour import CyclicBehaviour

class CounterAgent(Agent):
    class MyBehaviour(CyclicBehaviour):
        async def on_start(self):
            print("Starting behaviour . . .")
            self.counter = 0

        async def run(self):
            print("Counter: {}".format(self.counter))
            self.counter += 1
            await asyncio.sleep(1)

    async def setup(self):
        print("Agent starting . . .")
        b = self.MyBehaviour()
        self.add_behaviour(b)


async def my_behaviours_controller(request):
    behaviours_list = []
    for b in a.behaviours:
        behaviours_list.append(str(b))

    return {
            "behaviours": behaviours_list,
            }

if __name__ == "__main__":
    a = CounterAgent("test_agent@jabbim.pl", "123")

    a.web.add_get("/hello", my_behaviours_controller, "./hello.html")

    a.start(auto_register=True)
    port = 10000
    a.web.start(port=port)
    print("Go to localhost:{}/spade".format(str(port)))

    print("Wait until user interrupts with ctrl+C")
    while True:
        try:
            time.sleep(1)
        except KeyboardInterrupt:
            break
    a.stop()