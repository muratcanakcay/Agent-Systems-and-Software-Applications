from osbrain import run_nameserver
from osbrain.proxy import locate_ns
from osbrain import run_agent
import osbrain
import time
import pickle


def log_message(agent, message):
    agent.log_info('Received: %s' % message)


if __name__ == '__main__':
    ns_sock = '127.0.0.1:1129' # 0.0.0.0 on Linux (?)

    osbrain.config['TRANSPORT'] = 'tcp'
    osbrain.config['SERIALIZER'] = 'json'

    nameserver = run_nameserver(ns_sock)
    ns_addr = locate_ns(ns_sock)

    bob = run_agent('Bob', ns_sock)
    bob_bind_address = bob.bind('PULL', alias='main', handler=log_message, serializer='json')

    print("Bob is bound to address: " + str(bob_bind_address))

    print("Setups done, waiting for message.")

    time.sleep(10)
    print("Proxy works both ways. Here is Alice address", nameserver.proxy('Alice').addr('main'))

