import time

from osbrain import run_agent
from osbrain import run_nameserver


def log_message(agent, message):
    agent.log_info('Received: %s' % message)


def annoy(agent, say, more=None):
    message = say if not more else say + ' ' + more + '!'
    agent.send('annoy', message)


def delayed(agent, message):
    agent.log_info(message)


if __name__ == '__main__':
    ns = run_nameserver()

    # Repeated actions
    orange = run_agent('Orange')
    apple = run_agent('Apple')
    addr = orange.bind('PUSH', alias='annoy')
    apple.connect(addr, handler=log_message)

    orange.each(1.0, annoy, 'Hey')
    orange.each(1.4142, annoy, 'Apple')
    orange.each(3.1415, annoy, 'Hey', more='Apple')

    # Delayed actions
    delayed_agent = run_agent('delayed_agent')

    delayed_agent.after(2, delayed, "Delayed action")
    delayed_agent.log_info('Logged now')

    # Stopping actions
    stopped_agent = run_agent('stopped_agent')

    stopped_agent.after(1, delayed, 'Hello!')
    timer0 = stopped_agent.after(1, delayed, 'Never logged')
    stopped_agent.after(1, delayed, 'Never logged either', alias='timer_alias')

    stopped_agent.stop_timer(timer0)
    stopped_agent.stop_timer('timer_alias')

    time.sleep(10)

    ns.shutdown()
