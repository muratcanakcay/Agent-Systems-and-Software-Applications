from time import sleep
from osbrain import run_agent
from osbrain import Agent
from initiatorAgent import InitiatorAgent
from responderAgent import ResponderAgent

class OrganizerAgent(Agent):        

    def on_init(self):
        self.numberOfCouples = 0
        self.numberOfInterests = 0
        self.currentTurn = 1    
        self.responderAgents = []
        self.initiatorAgents = []
        self.matched = False

    def initializeAgents(self):

        for i in range(self.numberOfCouples):
            initiatorAgent = run_agent('InitiatorAgent' + str(i), base=InitiatorAgent)
            responderAgent = run_agent('ResponderAgent' + str(i), base=ResponderAgent)

            initiatorAgent.numberOfInterests = self.numberOfInterests
            initiatorAgent.chooseInterests()
            responderAgent.numberOfInterests = self.numberOfInterests
            responderAgent.chooseInterests()

            matchNotifyAddress = initiatorAgent.bind('PUSH', alias="matchNotifyChannel")
            self.connect(matchNotifyAddress, handler='matchConfirmed')

            self.responderAgents.append(responderAgent)
            self.initiatorAgents.append(initiatorAgent)
        
    def startDating(self):
        if (self.matched == True):
            self.stop()
            return
        
        if(self.currentTurn > self.numberOfCouples):
            self.log_info(f'Sadly, none of the couples matched!')            
            self.stop()
            return
            
        self.log_info(f'Turn# {self.currentTurn}')

        for i in range(self.numberOfCouples):
            initiatorAgent = self.initiatorAgents[(i + self.currentTurn - 1) % self.numberOfCouples]
            initiatorAgentAddress = initiatorAgent.bind('PUSH', alias='ask')
            
            responderAgent = self.responderAgents[i]
            responderAgentAddress = responderAgent.bind('PUSH', alias='reply')

            responderAgent.connect(initiatorAgentAddress, handler='replyToQuestion')
            initiatorAgent.connect(responderAgentAddress, handler='receiveReply')
            
            initiatorAgent.askIfTheyLike()
            sleep(1)
            
            responderAgent.close('reply')
            initiatorAgent.close('ask')

        self.currentTurn += 1
    
    def matchConfirmed(self, message):
        if(self.matched == False):
            self.matched = True
            self.log_info(f"{message.split(' ')[0]} and {message.split(' ')[1]} have told me that they matched first!!!")