import time

import osbrain
from osbrain import run_agent
from osbrain import run_nameserver


def log_message(agent, message):
    agent.log_info('Received:')
    agent.log_info(message)


if __name__ == '__main__':
    # Not secure
    osbrain.config['SERIALIZER'] = 'pickle'

    ns = run_nameserver()
    alice = run_agent('Alice')
    bob = run_agent('Bob')

    addr = alice.bind('PUSH', alias='main')
    print(addr)
    bob.connect(addr, handler=log_message)

    for _ in range(3):
        time.sleep(1)
        alice.send('main', {"message": 'Hello, Bob!'})

    ns.shutdown()
