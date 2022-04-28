from osbrain import run_agent
from osbrain import run_nameserver


def reply(agent, message):
    #message= message+1
    field = "mylist"
    agent.log_info(f"I received {message} field {field} is equal to {message[field]}")
    #return 'Received ' + str(message)


if __name__ == '__main__':
    ns = run_nameserver()
    alice = run_agent('Alice')
    bob = run_agent('Bob')

    addr = alice.bind('REP', alias='main', handler=reply)
    bob.connect(addr, alias='main')
    # What about Rob?
    
    for i in range(10):
        bob.send('main', 
        {
            "n" : i,
            "mylist" : [1,23,4]
        })
        reply = bob.recv('main')
        print(reply)

    ns.shutdown()
