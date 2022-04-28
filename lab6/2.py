from osbrain import run_agent
from osbrain import run_nameserver
import time


if __name__ == '__main__':
    address = "127.0.0.2:9000"
    ns = run_nameserver(address)
    run_agent('Agent0')
    run_agent('Agent1')
    run_agent('Agent2')

    for alias in ns.agents():
        print(alias)

    # https://osbrain.readthedocs.io/en/stable/api/nameserver.html
    # print(ns.ping())
    # ns.async_kill_agents(address)

    ns.shutdown()

    time.sleep(2)

    # print(ns.ping())

# https://osbrain.readthedocs.io/en/stable/considerations.html#oop