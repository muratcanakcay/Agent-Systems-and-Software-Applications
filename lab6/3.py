import time

from osbrain import run_agent
from osbrain import run_nameserver

import random


def log_message(agent, message):
    agent.log_info('Received: %s' % message)


if __name__ == '__main__':
    ns = run_nameserver()
    alice = run_agent('Alice')
    bob = run_agent('Bob')
    # rob = run_agent('Rob')

    addr = alice.bind('PUSH', alias='main')
    bob.connect(addr, handler=log_message)
    # rob.connect(addr, handler=log_message)

    for _ in range(3):
        time.sleep(1)
        alice.send('main', 'Hello, Bob! Here is your random number {}'.format(
            random.randint(0, 10)))

    ns.shutdown()
