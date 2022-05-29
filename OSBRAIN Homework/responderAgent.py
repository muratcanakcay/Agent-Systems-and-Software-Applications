from osbrain import Agent
from random import randint
from listOfInterests import listOfInterests

class ResponderAgent(Agent):
    numberOfInterests = 0
    interests = []

    def chooseInterests(self):
        while (len(self.interests) < self.numberOfInterests):
            interestIndex = randint(0,len(listOfInterests) - 1)
            if (listOfInterests[interestIndex] not in self.interests):
                self.interests.append(listOfInterests[interestIndex])
        
        self.log_info(self.interests)
    
    def replyToQuestion(self, message):
        askedInterest = message.split(' ')[-2]
        self.log_info(askedInterest)
        
        if(askedInterest in self.interests):
            self.log_info('YES')
            self.send('reply', f'{self.name} YES')
        else:
            self.log_info('NO')
            self.send('reply',f'{self.name} NO')
