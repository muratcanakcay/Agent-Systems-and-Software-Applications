import time

from osbrain import run_agent
from osbrain import run_nameserver

import random


def log_a(agent, message):
    agent.log_info('Log a: %s' % message)


def log_b(agent, message):
    agent.log_info('Log b: %s' % message)


def send_messages(agent):
    agent.send('main', 'Apple {}'.format(random.randint(0, 10)), topic='a')
    agent.send('main', 'Banana {}'.format(random.randint(0, 10)), topic='b')


if __name__ == '__main__':
    ns = run_nameserver()
    alice = run_agent('Alice')
    bob = run_agent('Bob')
    rob = run_agent('Rob')

    addr = alice.bind('PUB', alias='main')
    alice.each(0.5, send_messages)
    bob.connect(addr, alias='listener', handler={'a': log_a})
    rob.connect(addr, alias='listener', handler={'a': log_a, 'b': log_b})

    time.sleep(2)
    bob.unsubscribe('listener', 'a')
    print("Bob unsubscribed")
    time.sleep(2)
    print("Bob subscribed again!")
    bob.subscribe('listener', handler={'b': log_b})
    time.sleep(2)

    ns.shutdown()
