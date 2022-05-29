from osbrain import Agent
from random import randint
from listOfInterests import listOfInterests

class InitiatorAgent(Agent):
    numberOfInterests = 0
    interests = []

    def chooseInterests(self):
        while (len(self.interests) < self.numberOfInterests):
            interestIndex = randint(0,len(listOfInterests) - 1)
            if (listOfInterests[interestIndex] not in self.interests):
                self.interests.append(listOfInterests[interestIndex])
                
        self.log_info(self.interests)
    
    def askIfTheyLike(self):
        interest = self.interests[randint(0, len(self.interests) - 1)]
        self.log_info(f'Hey! Do you like {interest} ?')
        self.send('ask', f'Hey! Do you like {interest} ?')

    def receiveReply(self, message):
        sender = message.split(' ')[0]
        reply = message.split(' ')[1]
        self.log_info(f'{sender} replied {reply}')
        
        if (reply == 'YES'):
            self.send('matchNotifyChannel', f'{self.name} {sender}')