from osbrain import run_agent
from osbrain import run_nameserver

if __name__ == '__main__':
    ns = run_nameserver()
    agent = run_agent('Example')

    agent.log_info('Hello world!')

    ns.shutdown()