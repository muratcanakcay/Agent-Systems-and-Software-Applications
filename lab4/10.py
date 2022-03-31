import time
from spade.agent import Agent
from spade.behaviour import OneShotBehaviour
from spade.behaviour import CyclicBehaviour
from aioxmpp import PresenceShow
import asyncio


class Agent1(Agent):
    async def setup(self):
        print("Agent {} running".format(self.jid))
        self.add_behaviour(self.Behav1())

    class Behav1(OneShotBehaviour):
        def on_available(self, jid, stanza):
            print("[{}] Agent {} is available.".format(self.agent.jid, jid))
            # print(stanza)

        def on_subscribed(self, jid):
            print("[{}] Agent {} has accepted the subscription.".format(
                self.agent.jid, jid))
            print("[{}] Contacts List: {}".format(
                self.agent.jid, self.agent.presence.get_contacts()))

        def on_unsubscribe(self, jid):
            print("[{}] Agent {} asked for unsubscription. I approve it.".format(
                self.agent.jid, jid))

        def on_subscribe(self, jid):
            print("[{}] Agent {} asked for subscription. I approve it.".format(
                self.agent.jid, jid))
            self.presence.approve(jid)

        async def run(self):
            self.presence.on_subscribe = self.on_subscribe
            self.presence.on_unsubscribe = self.on_unsubscribe
            self.presence.on_subscribed = self.on_subscribed
            self.presence.on_available = self.on_available

            self.presence.set_available(show=PresenceShow.CHAT)


class Agent2(Agent):
    async def setup(self):
        print("Agent {} running".format(self.jid))

        self.add_behaviour(self.Behav2())

    class Behav2(OneShotBehaviour):
        def on_available(self, jid, stanza):
            print("[{}] Agent {} is available.".format(self.agent.jid, jid))
            # print(stanza)

        def on_subscribed(self, jid):
            print("[{}] Agent {} has accepted the subscription.".format(
                self.agent.jid, jid))
            print("[{}] Contacts List: {}".format(
                self.agent.jid, self.agent.presence.get_contacts()))

        def on_subscribe(self, jid):
            print("[{}] Agent {} asked for subscription. I do NOT approve it.".format(
                self.agent.jid, jid))

        def on_unsubscribe(self, jid):
            print("[{}] Agent {} asked for unsubscription. I approve it.".format(
                self.agent.jid, jid))

        def on_unsubscribed(self, jid):
            print("[{}] I got unsubscribed from agent {}!".format(
                self.agent.jid, jid))

            print("[{}] I'm trying to subscribe to {}".format(
                self.agent.jid, self.agent.jid1))
            self.presence.subscribe(self.agent.jid1)

        async def run(self):
            self.presence.on_subscribe = self.on_subscribe
            self.presence.on_unsubscribe = self.on_unsubscribe
            self.presence.on_subscribed = self.on_subscribed
            self.presence.on_available = self.on_available

            self.presence.set_available()

            if self.agent.subscribe:
                print("[{}] Subscribing to {}.".format(self.agent.jid, self.agent.jid1))
                self.presence.subscribe(self.agent.jid1)
            else:
                print("[{}] Unsubscribing from {}.".format(self.agent.jid, self.agent.jid1))
                self.presence.unsubscribe(self.agent.jid1)


if __name__ == "__main__":
    jid1 = "test_agent@jabbim.pl"
    passwd1 = "123"

    jid2 = "test_agent@chatserver.space"
    passwd2 = "123"

    agent1 = Agent1(jid1, passwd1)
    agent2 = Agent2(jid2, passwd2)

    agent1.jid2 = jid2
    agent2.jid1 = jid1

    # True - subscribe to Agent1
    # False - unsubscribe from Agent1
    agent2.subscribe = False

    future = agent1.start()
    future.result()

    agent2.start()

    agent1.web.start(port=10000)
    agent2.web.start(port=10001)

    while True:
        try:
            time.sleep(100)
        except KeyboardInterrupt:
            break
    agent1.stop()
    agent2.stop()
