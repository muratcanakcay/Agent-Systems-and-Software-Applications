import time

import osbrain
from osbrain import run_agent
from osbrain import run_nameserver


def log_message(agent, message):
    agent.log_info('Received: %s' % message)


if __name__ == '__main__':
    osbrain.config['TRANSPORT'] = 'inproc'

    ns = run_nameserver()
    alice = run_agent('Alice', transport='tcp')
    bob = run_agent('Bob')

    addr = alice.bind('PUSH', alias='main')
    bob.connect(addr, handler=log_message)

    # What about performance?
    for _ in range(3):
        time.sleep(1)
        alice.send('main', 'Hello, Bob!')

    ns.shutdown()