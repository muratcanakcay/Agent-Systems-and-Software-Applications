import time

from osbrain import Agent
from osbrain import run_agent
from osbrain import run_nameserver


class Greeter(Agent):
    def on_init(self):
        self.bind('PUSH', alias='main')

    def hello(self, name):
        self.send('main', 'Hello, %s!' % name)


class Bob(Agent):
    # https://osbrain.readthedocs.io/en/stable/considerations.html#setting-initial-attributes
    age = 20

    def custom_log(self, message):
        self.age += 1
        self.log_info("Received: %s (I'm %d years old.)" % (message, self.age))


if __name__ == '__main__':
    ns = run_nameserver()
    alice = run_agent('Alice', base=Greeter)
    bob = run_agent('Bob', base=Bob)

    bob.connect(alice.addr('main'), handler='custom_log')

    for _ in range(3):
        alice.hello('Bob')
        time.sleep(1)

    ns.shutdown()
