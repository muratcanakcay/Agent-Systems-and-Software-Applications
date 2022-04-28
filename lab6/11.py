from osbrain import NSProxy
from osbrain import run_agent
import osbrain
import pickle
import time

if __name__ == '__main__':
    ns_sock = '127.0.0.1:1129'
    osbrain.config['TRANSPORT'] = 'tcp'
    osbrain.config['SERIALIZER'] = 'json'

    print('Registering Agent with server...')
    alice = run_agent('Alice', ns_sock)
    nameserver = NSProxy(ns_sock)
    alice_address_to_connect = nameserver.proxy('Bob').addr('main')
    print("Alice is connecting to address: " + str(alice_address_to_connect))
    alice.connect(alice_address_to_connect, alias='main')
    print('I have joined the nameserver!')

    for i in range(10):
        print("I try to say HEY!")
        alice.send('main', 'Hey')
        print("I tried")
        time.sleep(2)

    print("Done")
    exit(0)
