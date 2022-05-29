import sys
from osbrain import run_agent
from osbrain import run_nameserver
from organizerAgent import OrganizerAgent 


if __name__ == '__main__':
    if (len(sys.argv) != 3):
        print("You must enter only the number of couples and number of interests! (e.g.: python speedDating.py 4 4")
        exit()

    ns_sock = run_nameserver()
     
    organizerAgent = run_agent('OrganizerAgent', base=OrganizerAgent)
    organizerAgent.numberOfCouples = int(sys.argv[1])
    organizerAgent.numberOfInterests = int(sys.argv[2])
    organizerAgent.initializeAgents()
    
    while organizerAgent.is_running():
        try: 
            organizerAgent.startDating()
        except KeyboardInterrupt:
            ns_sock.shutdown_agents()
            ns_sock.shutdown()
        
    ns_sock.shutdown_agents()
    ns_sock.shutdown()
